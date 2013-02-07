package models;

import static org.fest.assertions.Assertions.assertThat;

import org.bson.types.ObjectId;
import org.junit.Test;

public class BookTest {

	@Test
	public void createABook() {
		ObjectId id = ObjectId.get();
		String title = "This is a book";
		String author = "an author";
		String isbn = "1234";
		String language = "English";
		String originaltitle = "Tämä on kirja";
		String pages = "5092";
		String publisheddate = "2012-01-01";
		String publishedplace = "Espoo";
		String publisher = "Otava";
		String series = "A series";
		String source = "Manual";
		String translator = "a translator";
		int version = 1;

		Book book = new Book(id, title);
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
	}
}
