import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.fail;
import static play.test.Helpers.HTMLUNIT;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.running;
import static play.test.Helpers.testServer;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import models.Book;
import models.BookDAO;
import models.BookDAOImpl;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
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

public class AcceptanceTest {
	BookDAO dao = null;

	@Before
	public void setupTestMongo() throws UnknownHostException, MongoException {
		dao = new BookDAOImpl(new Mongo("127.0.0.1", 27017), new Morphia(), "testbookdatabase");
		dao.getCollection().drop();
		// Put a book
		Book book = new Book("A new book");
		book.setAuthor("Bob, Wibble");
		dao.save(book);

		book = new Book("Z is the name");
		book.setAuthor("Kate, Wibble");
		dao.save(book);

		book = new Book("H is the name");
		book.setAuthor("Orphius, Wibble");
		dao.save(book);
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

	@Test
	public void getBooks() {
		running(testServer(3333, fakeApplication(new TestGlobal())), HTMLUNIT, new Callback<TestBrowser>() {
			public void invoke(TestBrowser browser) {
				browser.goTo("http://localhost:3333/books");
				List<Book> books = getAsBookList(getAsJson(browser));
				assertThat(books).hasSize(3);
				assertThat(books.get(0).getTitle()).isEqualTo("A new book");
			}
		});
	}

	@Test
	public void getBooksInOrder() {
		running(testServer(3333, fakeApplication(new TestGlobal())), HTMLUNIT, new Callback<TestBrowser>() {
			public void invoke(TestBrowser browser) {
				browser.goTo("http://localhost:3333/books?sort=author:desc&title:asc");
				List<Book> books = getAsBookList(getAsJson(browser));
				assertThat(books).hasSize(3);
				assertThat(books.get(0).getTitle()).isEqualTo("H is the name");
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
}
