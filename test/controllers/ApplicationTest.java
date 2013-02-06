package controllers;

import static org.fest.assertions.Assertions.assertThat;
import static play.test.Helpers.contentAsString;
import static play.test.Helpers.contentType;

import java.util.ArrayList;
import java.util.List;

import models.Book;

import org.bson.types.ObjectId;
import org.junit.Test;

import play.data.Form;
import play.mvc.Content;

/**
 * 
 * Simple (JUnit) tests that can call all parts of a play app. If you are
 * interested in mocking a whole application, see the wiki for more details.
 * 
 */
public class ApplicationTest {

	@Test
	public void renderTemplate() {
		Content html = views.html.index.render(new ArrayList<Book>(), Form.form(Book.class));
		assertThat(contentType(html)).isEqualTo("text/html");
		assertThat(contentAsString(html)).contains("0 books");
	}

	@Test
	public void renderCreateForm() {
		Content html = views.html.index.render(new ArrayList<Book>(), Form.form(Book.class));
		assertThat(contentType(html)).isEqualTo("text/html");
		assertThat(contentAsString(html)).contains("Title:");
		assertThat(contentAsString(html)).contains("Author:");
		assertThat(contentAsString(html)).contains("ISBN:");
		assertThat(contentAsString(html)).contains("Publisher:");
		assertThat(contentAsString(html)).contains("Published date:");
		assertThat(contentAsString(html)).contains("Published place:");
		assertThat(contentAsString(html)).contains("Series:");
		assertThat(contentAsString(html)).contains("Original title:");
		assertThat(contentAsString(html)).contains("Translator:");
		assertThat(contentAsString(html)).contains("Source:");
	}

	@Test
	public void renderABook() {
		@SuppressWarnings("serial")
		List<Book> bookList = new ArrayList<Book>() {
			{
				add(new Book(ObjectId.get(), "A test book"));
			}
		};
		Content html = views.html.index.render(bookList, Form.form(Book.class));
		assertThat(contentAsString(html)).contains("1 book");
	}

	@Test
	public void renderMultipleBooks() {
		@SuppressWarnings("serial")
		List<Book> bookList = new ArrayList<Book>() {
			{
				add(new Book(ObjectId.get(), "A test book"));
				add(new Book(ObjectId.get(), "A second test book"));
			}
		};
		Content html = views.html.index.render(bookList, Form.form(Book.class));
		assertThat(contentAsString(html)).contains("2 books");
	}

}
