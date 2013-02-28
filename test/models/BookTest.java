package models;

import static org.fest.assertions.Assertions.assertThat;

import org.bson.types.ObjectId;
import org.junit.Test;

public class BookTest {
	private String title = "This is a book";
	private String author = "an author";
	private String isbn = "1234";
	private String language = "English";
	private String originaltitle = "Tämä on kirja";
	private String pages = "5092";
	private String publisheddate = "2012-01-01";
	private String publishedplace = "Espoo";
	private String publisher = "Otava";
	private String series = "A series";
	private String source = "Manual";
	private String translator = "a translator";
	private int version = 1;

	protected Book configureABook(Book book) {
		book.setTitle(title);
		book.setAuthor(author);
		book.setIsbn(isbn);
		book.setLanguage(language);
		book.setOriginaltitle(originaltitle);
		book.setPages(pages);
		book.setPublisheddate(publisheddate);
		book.setPublishedplace(publishedplace);
		book.setPublisher(publisher);
		book.setSeries(series);
		book.setSource(source);
		book.setTranslator(translator);
		book.setVersion(version);

		return book;
	}

	@Test
	public void createABook() {
		ObjectId id = ObjectId.get();
		Book book = configureABook(new Book(id, ""));

		assertThat(book.getId()).isEqualTo(id);
		assertThat(book.getTitle()).isEqualTo(title);
		assertThat(book.getAuthor()).isEqualTo(author);
		assertThat(book.getIsbn()).isEqualTo(isbn);
		assertThat(book.getLanguage()).isEqualTo(language);
		assertThat(book.getOriginaltitle()).isEqualTo(originaltitle);
		assertThat(book.getPages()).isEqualTo(pages);
		assertThat(book.getPublisheddate()).isEqualTo(publisheddate);
		assertThat(book.getPublishedplace()).isEqualTo(publishedplace);
		assertThat(book.getPublisher()).isEqualTo(publisher);
		assertThat(book.getSeries()).isEqualTo(series);
		assertThat(book.getSource()).isEqualTo(source);
		assertThat(book.getTranslator()).isEqualTo(translator);
		assertThat(book.getVersion()).isEqualTo(version);

		Book book2 = configureABook(new Book(id, ""));

		assertThat(book).isEqualTo(book2);
		assertThat(book.hashCode()).isEqualTo(book2.hashCode());
	}
}
