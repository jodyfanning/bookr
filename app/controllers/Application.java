package controllers;

import models.Book;

import org.bson.types.ObjectId;

import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;

public class Application extends Controller {

	static Form<Book> bookForm = Form.form(Book.class);

	public static Result index() {
		return redirect(routes.Application.books());
	}

	public static Result books() {
		return ok(views.html.index.render(MorphiaObject.dao.find().asList(), bookForm));
	}

	public static Result newBook() {
		Form<Book> filledForm = bookForm.bindFromRequest();
		if (filledForm.hasErrors()) {
			return badRequest(views.html.index.render(MorphiaObject.dao.find().asList(), bookForm));
		}
		Book newBook = filledForm.get();
		MorphiaObject.dao.save(newBook);
		return redirect(routes.Application.books());
	}

	public static Result deleteBook(String id) {
		MorphiaObject.dao.deleteById(new ObjectId(id));
		return redirect(routes.Application.books());
	}

}
