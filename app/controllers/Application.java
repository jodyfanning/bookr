package controllers;

import models.Book;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;

public class Application extends Controller {

	static Form<Book> bookForm = Form.form(Book.class);

	public static Result index() {
		return ok(views.html.index.render(MorphiaObject.dao.find().asList(), bookForm));
	}
}
