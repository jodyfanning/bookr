package controllers;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static play.test.Helpers.callAction;
import static play.test.Helpers.contentAsString;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.fakeRequest;
import static play.test.Helpers.running;
import static play.test.Helpers.status;

import java.io.IOException;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.Book;
import models.BookDAO;
import models.InternalServerErrorException;
import models.InvalidContentException;

import org.bson.types.ObjectId;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import play.libs.Json;
import play.mvc.Result;

import com.mongodb.CommandResult;
import com.mongodb.WriteConcern;
import com.mongodb.WriteResult;

public class RestTest {

	protected final BookDAO dao = mock(BookDAO.class);

	@Test
	public void wrapperPassesThroughAndReturns200() {
		running(fakeApplication(), new Runnable() {
			public void run() {
				Result result = callAction(controllers.routes.ref.Rest.books(null),
						fakeRequest().withHeader(play.mvc.Http.HeaderNames.ACCEPT, "application/json"));
				assertThat(status(result)).isEqualTo(play.mvc.Http.Status.OK);
			}
		});
	}

	@Test
	public void wrapperCatchesAndReturns406() {
		running(fakeApplication(), new Runnable() {
			public void run() {
				Result result = callAction(controllers.routes.ref.Rest.books(null),
						fakeRequest().withHeader(play.mvc.Http.HeaderNames.ACCEPT, "text/html,application/xhtml+xml"));
				assertThat(status(result)).isEqualTo(play.mvc.Http.Status.NOT_ACCEPTABLE);
			}
		});
	}

	@Test
	public void booksReturnsSomeBooks() throws InternalServerErrorException {
		@SuppressWarnings("serial")
		List<Book> books = new ArrayList<Book>() {
			{
				add(new Book("A test book"));
			}
		};
		when(dao.findAll()).thenReturn(books);

		running(fakeApplication(), new Runnable() {
			public void run() {
				MorphiaObject.dao = dao;
				Result result = callAction(controllers.routes.ref.Rest.books(null),
						fakeRequest().withHeader(play.mvc.Http.HeaderNames.ACCEPT, "application/json"));
				String jsonResult = contentAsString(result);

				assertThat(status(result)).isEqualTo(play.mvc.Http.Status.OK);
				assertThat(jsonResult.equals("[]")).isFalse();
				assertThat(jsonResult.contains("\"title\":\"A test book\"")).isTrue();
			}
		});
	}

	@Test
	public void getsASingleBookById() throws InternalServerErrorException {
		final ObjectId id = ObjectId.get();
		final String bookId = id.toString();
		final Book book = new Book("A single book");
		when(dao.getSingle(id)).thenReturn(book);

		running(fakeApplication(), new Runnable() {
			public void run() {
				MorphiaObject.dao = dao;
				Result result = callAction(controllers.routes.ref.Rest.getBook(bookId),
						fakeRequest().withHeader(play.mvc.Http.HeaderNames.ACCEPT, "application/json"));
				String jsonResult = contentAsString(result);

				assertThat(status(result)).isEqualTo(play.mvc.Http.Status.OK);
				assertThat(jsonResult.equals("[]")).isFalse();
				assertThat(jsonResult.contains("\"title\":\"A single book\"")).isTrue();
			}
		});
	}

	@Test
	public void failsAs404SingleBookById() {
		final ObjectId id = ObjectId.get();
		final String bookId = id.toString();
		when(dao.get(id)).thenReturn(null);

		running(fakeApplication(), new Runnable() {
			public void run() {
				MorphiaObject.dao = dao;
				Result result = callAction(controllers.routes.ref.Rest.getBook(bookId),
						fakeRequest().withHeader(play.mvc.Http.HeaderNames.ACCEPT, "application/json"));

				assertThat(status(result)).isEqualTo(play.mvc.Http.Status.NOT_FOUND);
			}
		});
	}

	@Test
	public void deletesASingleBookById() throws InternalServerErrorException {
		final ObjectId id = ObjectId.get();
		final String bookId = id.toString();
		final WriteResult deleteResult = mock(WriteResult.class);
		final CommandResult commandResult = mock(CommandResult.class);
		final Book book = new Book("A deleted book");
		when(dao.get(id)).thenReturn(book);
		when(dao.deleteSingle(id)).thenReturn(deleteResult);
		when(deleteResult.getLastError()).thenReturn(commandResult);
		when(commandResult.ok()).thenReturn(true);

		running(fakeApplication(), new Runnable() {
			public void run() {
				MorphiaObject.dao = dao;
				Result result = callAction(controllers.routes.ref.Rest.deleteBook(bookId),
						fakeRequest().withHeader(play.mvc.Http.HeaderNames.ACCEPT, "application/json"));
				String jsonResult = contentAsString(result);

				assertThat(status(result)).isEqualTo(play.mvc.Http.Status.OK);
				assertThat(jsonResult.equals("[]")).isFalse();
				assertThat(jsonResult.contains("\"title\":\"A deleted book\"")).isTrue();
			}
		});
	}

	@Test
	public void failsAs404DeleteASingleBookById() {
		final ObjectId id = ObjectId.get();
		final String bookId = id.toString();
		when(dao.get(id)).thenReturn(null);

		running(fakeApplication(), new Runnable() {
			public void run() {
				MorphiaObject.dao = dao;
				Result result = callAction(controllers.routes.ref.Rest.deleteBook(bookId),
						fakeRequest().withHeader(play.mvc.Http.HeaderNames.ACCEPT, "application/json"));

				assertThat(status(result)).isEqualTo(play.mvc.Http.Status.NOT_FOUND);
			}
		});
	}

	@Test
	public void failsAs500DeleteASingleBookById() throws InternalServerErrorException {
		final ObjectId id = ObjectId.get();
		final String bookId = id.toString();
		final WriteResult deleteResult = mock(WriteResult.class);
		final CommandResult commandResult = mock(CommandResult.class);
		final Book book = new Book("A deleted book");
		when(dao.get(id)).thenReturn(book);
		when(dao.deleteSingle(id)).thenReturn(deleteResult);
		when(deleteResult.getLastError()).thenReturn(commandResult);
		when(commandResult.ok()).thenReturn(false);

		running(fakeApplication(), new Runnable() {
			public void run() {
				MorphiaObject.dao = dao;
				Result result = callAction(controllers.routes.ref.Rest.deleteBook(bookId),
						fakeRequest().withHeader(play.mvc.Http.HeaderNames.ACCEPT, "application/json"));

				assertThat(status(result)).isEqualTo(play.mvc.Http.Status.INTERNAL_SERVER_ERROR);
			}
		});
	}

	@Test
	public void createsANewBook() {
		Book book = new Book("A new book");
		book.setAuthor("Any Body");
		final Book fBook = book;
		final JsonNode body = Json.toJson(fBook);

		running(fakeApplication(), new Runnable() {
			public void run() {
				MorphiaObject.dao = dao;
				Result result = callAction(controllers.routes.ref.Rest.newBook(),
						fakeRequest().withJsonBody(body).withHeader(play.mvc.Http.HeaderNames.ACCEPT, "application/json"));
				String jsonResult = contentAsString(result);

				assertThat(status(result)).isEqualTo(play.mvc.Http.Status.CREATED);
				Book insertedBook = Json.fromJson(Json.parse(jsonResult), Book.class);
				assertThat(insertedBook.equals(fBook)).isTrue();
				try {
					verify(dao).saveNew(any(Book.class), any(WriteConcern.class));
				} catch (InternalServerErrorException e) {
					fail("Internal server error");
				} catch (InvalidContentException e) {
					fail("Invalid content");
				}
			}
		});
	}

	@Test
	public void aNewBookRejectsMissingTitle() throws InvalidContentException, InternalServerErrorException {
		Book book = new Book();
		book.setAuthor("Any Body");
		final Book fBook = book;
		final JsonNode body = Json.toJson(fBook);
		doThrow(new InvalidContentException("Arrrgggh!")).when(dao).saveNew(any(Book.class), any(WriteConcern.class));

		running(fakeApplication(), new Runnable() {
			public void run() {
				MorphiaObject.dao = dao;
				Result result = callAction(controllers.routes.ref.Rest.newBook(),
						fakeRequest().withJsonBody(body).withHeader(play.mvc.Http.HeaderNames.ACCEPT, "application/json"));

				assertThat(status(result)).isEqualTo(play.mvc.Http.Status.BAD_REQUEST);
			}
		});
	}

	@Test
	public void aNewBookRejectsMissingBody() {
		running(fakeApplication(), new Runnable() {
			public void run() {
				MorphiaObject.dao = dao;
				Result result = callAction(controllers.routes.ref.Rest.newBook(),
						fakeRequest().withHeader(play.mvc.Http.HeaderNames.ACCEPT, "application/json"));

				assertThat(status(result)).isEqualTo(play.mvc.Http.Status.BAD_REQUEST);
			}
		});
	}

	@Test
	public void aNewBookRejectsBrokenISBNs() throws InvalidContentException, InternalServerErrorException {
		Book book = new Book("A new book");
		book.setAuthor("Any Body");
		book.setIsbn("abc");
		final Book fBook = book;
		final JsonNode body = Json.toJson(fBook);
		doThrow(new InvalidContentException("Arrrgggh!")).when(dao).saveNew(any(Book.class), any(WriteConcern.class));

		running(fakeApplication(), new Runnable() {
			public void run() {
				MorphiaObject.dao = dao;
				Result result = callAction(controllers.routes.ref.Rest.newBook(),
						fakeRequest().withJsonBody(body).withHeader(play.mvc.Http.HeaderNames.ACCEPT, "application/json"));
				assertThat(status(result)).isEqualTo(play.mvc.Http.Status.BAD_REQUEST);
			}
		});
	}

	@Test
	public void updatesAnExistingBook() throws ConcurrentModificationException, InternalServerErrorException,
			InvalidContentException {
		Book oBook = new Book("A new book");
		oBook.setAuthor("Any Body");
		final Book originalBook = oBook;
		Book eBook = new Book("A new book");
		eBook.setAuthor("Any Body");
		eBook.setVersion(2);
		final Book expectedBook = eBook;
		final JsonNode body = Json.toJson(originalBook);
		when(dao.safeUpdate(originalBook)).thenReturn(expectedBook);

		running(fakeApplication(), new Runnable() {
			public void run() {
				MorphiaObject.dao = dao;
				Result result = callAction(controllers.routes.ref.Rest.updateBook(originalBook.getId().toString()), fakeRequest()
						.withJsonBody(body).withHeader(play.mvc.Http.HeaderNames.ACCEPT, "application/json"));
				String jsonResult = contentAsString(result);

				assertThat(status(result)).isEqualTo(play.mvc.Http.Status.OK);
				Book insertedBook = Json.fromJson(Json.parse(jsonResult), Book.class);
				assertThat(insertedBook.equals(expectedBook)).isTrue();
			}
		});
	}

	@Test
	public void updateRejectsNonExistingBook() {
		Book originalBook = new Book("A new book");
		originalBook.setAuthor("Any Body");
		final JsonNode body = Json.toJson(originalBook);

		running(fakeApplication(), new Runnable() {
			public void run() {
				MorphiaObject.dao = dao;
				Result result = callAction(controllers.routes.ref.Rest.updateBook(ObjectId.get().toString()), fakeRequest()
						.withJsonBody(body).withHeader(play.mvc.Http.HeaderNames.ACCEPT, "application/json"));
				assertThat(status(result)).isEqualTo(play.mvc.Http.Status.NOT_FOUND);
			}
		});
	}

	@Test
	public void updateRejectsMissingBody() {
		running(fakeApplication(), new Runnable() {
			public void run() {
				MorphiaObject.dao = dao;
				Result result = callAction(controllers.routes.ref.Rest.newBook(),
						fakeRequest().withHeader(play.mvc.Http.HeaderNames.ACCEPT, "application/json"));

				assertThat(status(result)).isEqualTo(play.mvc.Http.Status.BAD_REQUEST);
			}
		});
	}

	@Test
	public void updateRejectsModifiedBook() throws ConcurrentModificationException, InternalServerErrorException,
			InvalidContentException {
		Book oBook = new Book("A new book");
		oBook.setAuthor("Any Body");
		final Book originalBook = oBook;
		final JsonNode body = Json.toJson(originalBook);

		when(dao.safeUpdate(originalBook)).thenThrow(new ConcurrentModificationException());

		running(fakeApplication(), new Runnable() {
			public void run() {
				MorphiaObject.dao = dao;
				Result result = callAction(controllers.routes.ref.Rest.updateBook(originalBook.getId().toString()), fakeRequest()
						.withJsonBody(body).withHeader(play.mvc.Http.HeaderNames.ACCEPT, "application/json"));
				assertThat(status(result)).isEqualTo(play.mvc.Http.Status.CONFLICT);
			}
		});
	}

	@Test
	public void sortsByGivenFields() {
		@SuppressWarnings("serial")
		final List<Book> books = new ArrayList<Book>() {
			{
				add(new Book("A test book"));
				add(new Book("Fake book 2"));
			}
		};

		running(fakeApplication(), new Runnable() {
			@SuppressWarnings({ "rawtypes", "serial", "unchecked" })
			public void run() {
				try {
					List<String> sort = new ArrayList<String>() {
						{
							add("title:asc");
						}
					};
					MorphiaObject.dao = dao;
					ArgumentCaptor<Map> argument = ArgumentCaptor.forClass(Map.class);
					when(dao.findByQuery(any(Map.class))).thenReturn(books);

					Result result = callAction(controllers.routes.ref.Rest.books(sort),
							fakeRequest().withHeader(play.mvc.Http.HeaderNames.ACCEPT, "application/json"));
					String jsonResult = contentAsString(result);

					assertThat(status(result)).isEqualTo(play.mvc.Http.Status.OK);

					JsonNode json = Json.parse(jsonResult);

					List<Book> bookList = new ArrayList<Book>();
					ObjectMapper mapper = new ObjectMapper();
					bookList = mapper.readValue(json, new TypeReference<List<Book>>() {
					});

					verify(dao).findByQuery(argument.capture());
					Map sortMap = argument.getValue();
					Map<String, String> expectedSortMap = new HashMap<String, String>() {
						{
							put("title", "asc");
						}
					};

					assertThat(sortMap).hasSize(1).isEqualTo(expectedSortMap);
					assertThat(bookList).hasSize(2);
					assertThat(bookList.get(0)).isEqualTo(books.get(0));
				} catch (JsonParseException e) {
					fail("Parsing exception");
				} catch (JsonMappingException e) {
					fail("Mapping exception");
				} catch (IOException e) {
					fail("IO exception");
				} catch (InternalServerErrorException e) {
					fail("Internal server error");
				}
			}
		});
	}
}
