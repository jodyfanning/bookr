package controllers;

import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.Book;
import models.InternalServerErrorException;
import models.InvalidContentException;

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
	public static Result books(List<String> sort) {
		try {
			@SuppressWarnings("unchecked")
			List<Book> books = Collections.EMPTY_LIST;
			if (null == sort || sort.size() == 0) {
				books = MorphiaObject.dao.findAll();
			} else {
				Map<String, String> queries = new HashMap<String, String>();
				for (String field : sort) {
					String[] split = field.split(":");
					if (split.length == 2) {
						queries.put(split[0], split[1]);
					} else {
						queries.put(split[0], "asc");
					}
				}
				books = MorphiaObject.dao.findByQuery(queries);
			}
			JsonNode result = Json.toJson(books);
			return ok(result);
		} catch (InternalServerErrorException e) {
			return internalServerError(e.getMessage());
		}
	}

	@With(JsonAction.class)
	@BodyParser.Of(BodyParser.Json.class)
	public static Result newBook() {
		try {
			JsonNode json = request().body().asJson();
			if (null == json) {
				return badRequest();
			}

			Book newBook = Json.fromJson(json, Book.class);
			MorphiaObject.dao.saveNew(newBook, new WriteConcern(true));
			JsonNode result = Json.toJson(newBook);
			return created(result);
		} catch (InternalServerErrorException e) {
			return internalServerError(e.getMessage());
		} catch (InvalidContentException e) {
			return badRequest(e.getMessage());
		}
	}

	@With(JsonAction.class)
	public static Result getBook(String id) {
		try {
			Book book;
			book = MorphiaObject.dao.getSingle(new ObjectId(id));
			if (null != book) {
				JsonNode result = Json.toJson(book);
				return ok(result);
			}
			return notFound();
		} catch (InternalServerErrorException e) {
			return internalServerError(e.getMessage());
		}
	}

	@With(JsonAction.class)
	@BodyParser.Of(BodyParser.Json.class)
	public static Result updateBook(String id) {
		try {
			JsonNode json = request().body().asJson();
			if (null == json) {
				throw new InvalidContentException("Missing JSON content");
			}

			Book updatedBook = Json.fromJson(json, Book.class);
			if (id.equals(updatedBook.getId().toString())) {
				Book saved = MorphiaObject.dao.safeUpdate(updatedBook);
				JsonNode result = Json.toJson(saved);
				return ok(result);
			}

			return notFound();
		} catch (ConcurrentModificationException e) {
			return status(CONFLICT);
		} catch (InvalidContentException e) {
			return badRequest(e.getMessage());
		} catch (InternalServerErrorException e) {
			return internalServerError(e.getMessage());
		}
	}

	@With(JsonAction.class)
	public static Result deleteBook(String id) {
		try {
			ObjectId oid = new ObjectId(id);
			Book book = MorphiaObject.dao.get(oid);
			if (null != book) {
				WriteResult wr = MorphiaObject.dao.deleteSingle(oid);
				if (wr.getLastError().ok()) {
					JsonNode result = Json.toJson(book);
					response().setHeader(CACHE_CONTROL, "no-cache, no-store");
					return ok(result);
				} else {
					return internalServerError();
				}
			}
			return notFound();
		} catch (InternalServerErrorException e) {
			return internalServerError(e.getMessage());
		}
	}
}