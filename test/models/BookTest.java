package models;

import static org.junit.Assert.assertEquals;

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
		
		assertEquals(book.getId(), id);
		assertEquals(book.getTitle(), title);
		assertEquals(book.getAuthor(), author);
		assertEquals(book.getIsbn(), isbn);
		assertEquals(book.getLanguage(), language);
		assertEquals(book.getOriginaltitle(), originaltitle);
		assertEquals(book.getPages(), pages);
		assertEquals(book.getPublisheddate(), publisheddate);
		assertEquals(book.getPublishedplace(), publishedplace);
		assertEquals(book.getPublisher(), publisher);
		assertEquals(book.getSeries(), series);
		assertEquals(book.getSource(), source);
		assertEquals(book.getTranslator(), translator);
	}
}
