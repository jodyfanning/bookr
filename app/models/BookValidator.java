package models;

class BookValidator {

	private String error = "";

	public BookValidator() {
	}

	public boolean validate(Book book) {
		error = "";
		if (book.getTitle() == null || book.getTitle().trim().isEmpty()) {
			error = "Title is missing";
			return false;
		}

		if (book.getIsbn() != null && !book.getIsbn().trim().isEmpty() && !(isISBNValid(book.getIsbn()))) {
			error = "Invalid ISBN";
			return false;
		}

		return true;
	}

	public String getError() {
		return error;
	}

	protected boolean isISBNValid(String isbn) {
		String cleanIsbn = isbn.replaceAll("\\W", "");
		return (isISBN10Valid(cleanIsbn) || isISBN13Valid(cleanIsbn));
	}

	protected static boolean isISBN13Valid(String isbn) {
		if (isbn.length() != 13) {
			return false;
		}
		int check = 0;
		for (int i = 0; i < 12; i += 2) {
			check += Integer.valueOf(isbn.substring(i, i + 1));
		}
		for (int i = 1; i < 12; i += 2) {
			check += Integer.valueOf(isbn.substring(i, i + 1)) * 3;
		}
		check += Integer.valueOf(isbn.substring(12));
		return (check % 10 == 0);
	}

	protected static boolean isISBN10Valid(String isbn) {
		if (isbn.length() != 10) {
			return false;
		}

		int check = 0;

		for (int i = 0; i < 10; i++) {
			String character = isbn.substring(i, i + 1);
			int value = ("X".equalsIgnoreCase(character)) ? 10 : Integer.valueOf(character);
			check += value * (10 - i);
		}
		return (check % 11 == 0);
	}

}
