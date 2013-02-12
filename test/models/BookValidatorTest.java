package models;

import static org.fest.assertions.Assertions.assertThat;

import org.junit.Test;

public class BookValidatorTest {

	private BookValidator validator = new BookValidator();

	@Test
	public void hasAValidISBN10() {
		assertThat(validator.isISBNValid("052164819X")).isTrue();
	}

	@Test
	public void hasAnInvalidISBN10() {
		assertThat(validator.isISBNValid("1234567890")).isFalse();
	}

	@Test
	public void hasAValidISBN13() {
		assertThat(validator.isISBNValid("978-0-596-51978-0")).isTrue();
	}

	@Test
	public void hasAnInvalidISBN13() {
		assertThat(validator.isISBNValid("1234567890123")).isFalse();
	}

	@Test
	public void validatesABookWithTitleAndNoISBN() {
		Book validBook = new Book("A title");
		assertThat(validator.validate(validBook)).isTrue();
	}

	@Test
	public void validatesABookWithTitleAndEmptyISBN() {
		Book validBook = new Book("A title");
		validBook.setIsbn("");
		assertThat(validator.validate(validBook)).isTrue();
	}

	@Test
	public void validatesABookWithTitleAndISBN() {
		Book validBook = new Book("A title");
		validBook.setIsbn("978-0-596-51978-0");
		assertThat(validator.validate(validBook)).isTrue();
	}

	@Test
	public void invalidatesABookWithNoTitle() {
		Book invalidBook = new Book();
		assertThat(validator.validate(invalidBook)).isFalse();
	}

	@Test
	public void invalidatesABookWithEmptyTitle() {
		Book invalidBook = new Book();
		invalidBook.setTitle("");
		assertThat(validator.validate(invalidBook)).isFalse();
	}

	@Test
	public void invalidatesABookWithTitleAndInvalidISBN() {
		Book invalidBook = new Book("A title");
		invalidBook.setIsbn("abc");
		assertThat(validator.validate(invalidBook)).isFalse();
	}
}