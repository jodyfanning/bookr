package controllers;

import java.util.ConcurrentModificationException;
import java.util.List;

import models.Book;
import models.BookValidator;

import org.bson.types.ObjectId;
import org.codehaus.jackson.JsonNode;

import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.With;

import com.mongodb.WriteConcern;
import com.mongodb.WriteResult;

public class Rest extends Controller {

	@With(JsonAction.class)
	public static Result books(String filter, String order) {
		List<Book> books = MorphiaObject.dao.findAll();
		JsonNode result = Json.toJson(books);
		return ok(result);
	}

	@With(JsonAction.class)
	@BodyParser.Of(BodyParser.Json.class)
	public static Result newBook() {
		JsonNode json = request().body().asJson();
		Book newBook = Json.fromJson(json, Book.class);
		BookValidator validator = new BookValidator();
		if (validator.validate(newBook)) {
			MorphiaObject.dao.save(newBook, new WriteConcern(true));
			JsonNode result = Json.toJson(newBook);
			return created(result);
		}
		return badRequest();
	}

	@With(JsonAction.class)
	public static Result getBook(String id) {
		Book book = MorphiaObject.dao.get(new ObjectId(id));
		if (null != book) {
			JsonNode result = Json.toJson(book);
			return ok(result);
		}
		return notFound();
	}

	@With(JsonAction.class)
	@BodyParser.Of(BodyParser.Json.class)
	public static Result updateBook(String id) {
		JsonNode json = request().body().asJson();
		Book updatedBook = Json.fromJson(json, Book.class);
		BookValidator validator = new BookValidator();
		if (validator.validate(updatedBook) && id.equals(updatedBook.getId().toString())) {
			try {
				Book saved = MorphiaObject.dao.safeUpdate(updatedBook);
				JsonNode result = Json.toJson(saved);
				return ok(result);
			} catch (ConcurrentModificationException e) {
				return status(CONFLICT);
			}
		}
		return notFound();
	}

	@With(JsonAction.class)
	public static Result deleteBook(String id) {
		ObjectId oid = new ObjectId(id);
		Book book = MorphiaObject.dao.get(oid);
		if (null != book) {
			WriteResult wr = MorphiaObject.dao.deleteById(oid);
			if (wr.getLastError().ok()) {
				JsonNode result = Json.toJson(book);
				response().setHeader(CACHE_CONTROL, "no-cache, no-store");
				return ok(result);
			} else {
				return internalServerError();
			}
		}
		return notFound();
	}
}