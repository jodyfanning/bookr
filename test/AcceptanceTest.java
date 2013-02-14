import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.fail;
import static play.test.Helpers.HTMLUNIT;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.running;
import static play.test.Helpers.testServer;

import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.Book;
import models.BookDAO;
import models.BookDAOImpl;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.junit.Before;
import org.junit.Test;

import play.libs.F.Callback;
import play.libs.Json;
import play.test.TestBrowser;

import com.google.code.morphia.Morphia;
import com.mongodb.Mongo;
import com.mongodb.MongoException;

/**
 * Acceptance tests running against real MongoDB environment
 */
public class AcceptanceTest {
	protected BookDAO dao = null;
	protected Map<String, Book> books = new HashMap<String, Book>();
	protected static String defaultEncoding = "UTF-8";

	@Before
	public void setupTestEnvironment() throws UnknownHostException, MongoException {
		// Same Mongo as in TestGlobal
		dao = new BookDAOImpl(new Mongo("127.0.0.1", 27017), new Morphia(), "testbookdatabase");
		dao.getCollection().drop();
		books.clear();

		// Put a book
		Book book = new Book("A new book");
		book.setAuthor("Bob, Wibble");
		dao.save(book);
		books.put("A", book);

		book = new Book("Z is the name");
		book.setAuthor("Kate, Wibble");
		dao.save(book);
		books.put("B", book);

		book = new Book("H is the name");
		book.setAuthor("Orphius, Wibble");
		dao.save(book);
		books.put("C", book);
	}

	protected JsonNode getAsJson(TestBrowser browser) {
		return Json.parse(browser.pageSource());
	}

	protected List<Book> getAsBookList(JsonNode json) {
		List<Book> bookList = new ArrayList<Book>();
		try {
			ObjectMapper mapper = new ObjectMapper();
			bookList = mapper.readValue(json, new TypeReference<List<Book>>() {
			});
		} catch (JsonParseException e) {
			fail("Parsing exception");
		} catch (JsonMappingException e) {
			fail("Mapping exception");
		} catch (IOException e) {
			fail("IO exception");
		}
		return bookList;
	}

	protected HttpResponse putBook(String url, Book book) {
		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpPut put = new HttpPut(url);
		put.setHeader("Content-Type", "application/json");
		HttpResponse httpResponse = null;
		try {
			put.setEntity(new StringEntity(Json.stringify(Json.toJson(book))));
			httpResponse = httpClient.execute(put);
		} catch (UnsupportedEncodingException e) {
			fail();
		} catch (ClientProtocolException e) {
			fail();
		} catch (IOException e) {
			fail();
		}
		return httpResponse;
	}

	@Test
	public void getBooks() {
		running(testServer(3333, fakeApplication(new TestGlobal())), HTMLUNIT, new Callback<TestBrowser>() {
			public void invoke(TestBrowser browser) {
				browser.goTo("http://localhost:3333/books");
				List<Book> fetchedBooks = getAsBookList(getAsJson(browser));
				assertThat(fetchedBooks).hasSize(3);
				assertThat(fetchedBooks.get(0).getTitle()).isEqualTo(books.get("A").getTitle());
			}
		});
	}

	@Test
	public void getBooksInOrder() {
		running(testServer(3333, fakeApplication(new TestGlobal())), HTMLUNIT, new Callback<TestBrowser>() {
			public void invoke(TestBrowser browser) {
				browser.goTo("http://localhost:3333/books?sort=author:desc&title:asc");
				List<Book> fetchedBooks = getAsBookList(getAsJson(browser));
				assertThat(fetchedBooks).hasSize(3);
				assertThat(fetchedBooks.get(0).getTitle()).isEqualTo(books.get("C").getTitle());
			}
		});
	}

	@Test
	public void addsANewBook() {
		running(testServer(3333, fakeApplication(new TestGlobal())), HTMLUNIT, new Callback<TestBrowser>() {
			public void invoke(TestBrowser browser) {
				final String newBookTitle = "A first book";
				Book newBook = new Book(newBookTitle);

				DefaultHttpClient httpClient = new DefaultHttpClient();
				HttpPost post = new HttpPost("http://localhost:3333/books");
				post.setHeader("Content-Type", "application/json");
				String response = null;
				try {
					post.setEntity(new StringEntity(Json.stringify(Json.toJson(newBook))));
					ResponseHandler<String> responseHandler = new BasicResponseHandler();
					response = httpClient.execute(post, responseHandler);
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}

				Book inserted = Json.fromJson(Json.parse(response), Book.class);
				assertThat(inserted).isEqualTo(newBook);

				browser.goTo("http://localhost:3333/books?sort=title:asc");
				List<Book> books = getAsBookList(getAsJson(browser));
				assertThat(books).hasSize(4);
				String title = books.get(0).getTitle();
				assertThat(title).isEqualTo(newBookTitle);
			}
		});
	}

	@Test
	public void addsAnInvalidBook() {
		running(testServer(3333, fakeApplication(new TestGlobal())), HTMLUNIT, new Callback<TestBrowser>() {
			public void invoke(TestBrowser browser) {
				Book newBook = new Book();

				DefaultHttpClient httpClient = new DefaultHttpClient();
				HttpPost post = new HttpPost("http://localhost:3333/books");
				post.setHeader("Content-Type", "application/json");
				int responseCode = play.mvc.Http.Status.OK;
				try {
					post.setEntity(new StringEntity(Json.stringify(Json.toJson(newBook))));
					HttpResponse postResponse = httpClient.execute(post);
					responseCode = postResponse.getStatusLine().getStatusCode();
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}

				assertThat(responseCode).isEqualTo(play.mvc.Http.Status.BAD_REQUEST);
			}
		});
	}

	@Test
	public void getASpecificBook() {
		running(testServer(3333, fakeApplication(new TestGlobal())), HTMLUNIT, new Callback<TestBrowser>() {
			public void invoke(TestBrowser browser) {
				String bookURL = "http://localhost:3333/books/" + books.get("C").getId().toString();
				browser.goTo(bookURL);

				Book book = Json.fromJson(Json.parse(browser.pageSource()), Book.class);
				assertThat(book).isEqualTo(books.get("C"));
			}
		});
	}

	@Test
	public void deletesABook() {
		running(testServer(3333, fakeApplication(new TestGlobal())), HTMLUNIT, new Callback<TestBrowser>() {
			public void invoke(TestBrowser browser) {
				String bookURL = "http://localhost:3333/books/" + books.get("C").getId().toString();
				DefaultHttpClient httpClient = new DefaultHttpClient();
				HttpDelete delete = new HttpDelete(bookURL);
				String response = null;
				int responseCode = play.mvc.Http.Status.SERVICE_UNAVAILABLE;
				try {
					HttpResponse postResponse = httpClient.execute(delete);
					responseCode = postResponse.getStatusLine().getStatusCode();
					StringWriter writer = new StringWriter();
					IOUtils.copy(postResponse.getEntity().getContent(), writer, defaultEncoding);
					response = writer.toString();
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}

				Book deleted = Json.fromJson(Json.parse(response), Book.class);
				assertThat(deleted).isEqualTo(books.get("C"));
				assertThat(responseCode).isEqualTo(play.mvc.Http.Status.OK);

				browser.goTo("http://localhost:3333/books");
				List<Book> fetchedBooks = getAsBookList(getAsJson(browser));
				assertThat(fetchedBooks).hasSize(2);
			}
		});
	}

	@Test
	public void updatesAnExistingBook() {
		running(testServer(3333, fakeApplication(new TestGlobal())), HTMLUNIT, new Callback<TestBrowser>() {
			public void invoke(TestBrowser browser) {
				Book updatedBook = books.get("B");
				updatedBook.setLanguage("English");
				String bookURL = "http://localhost:3333/books/" + books.get("B").getId().toString();

				HttpResponse httpResponse = putBook(bookURL, updatedBook);

				int responseCode = httpResponse.getStatusLine().getStatusCode();
				StringWriter writer = new StringWriter();
				try {
					IOUtils.copy(httpResponse.getEntity().getContent(), writer, defaultEncoding);
				} catch (IOException e) {
					e.printStackTrace();
				}
				String response = writer.toString();

				assertThat(responseCode).isEqualTo(play.mvc.Http.Status.OK);
				Book updated = Json.fromJson(Json.parse(response), Book.class);
				assertThat(updated).isEqualTo(updatedBook);

				browser.goTo("http://localhost:3333/books");
				List<Book> fetchedBooks = getAsBookList(getAsJson(browser));
				assertThat(fetchedBooks).hasSize(3);

				browser.goTo(bookURL);
				Book book = Json.fromJson(Json.parse(browser.pageSource()), Book.class);
				assertThat(book).isEqualTo(updatedBook);
			}
		});
	}

	@Test
	public void updatesFailsForModifiedBook() {
		running(testServer(3333, fakeApplication(new TestGlobal())), HTMLUNIT, new Callback<TestBrowser>() {
			public void invoke(TestBrowser browser) {

				// Update version 1
				Book updatedBook = books.get("B");
				updatedBook.setLanguage("English");
				String bookURL = "http://localhost:3333/books/" + updatedBook.getId().toString();

				HttpResponse httpResponse = putBook(bookURL, updatedBook);
				int responseCode = httpResponse.getStatusLine().getStatusCode();
				assertThat(responseCode).isEqualTo(play.mvc.Http.Status.OK);
				// Version 1 is now 2 in Mongo

				// Reset to version 1 and do again
				updatedBook.setVersion(1);

				httpResponse = putBook(bookURL, updatedBook);
				responseCode = httpResponse.getStatusLine().getStatusCode();
				assertThat(responseCode).isEqualTo(play.mvc.Http.Status.CONFLICT);
			}
		});
	}

}
