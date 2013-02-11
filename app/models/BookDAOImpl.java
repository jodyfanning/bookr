package models;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.bson.types.ObjectId;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.Morphia;
import com.google.code.morphia.dao.BasicDAO;
import com.google.code.morphia.mapping.Mapper;
import com.google.code.morphia.query.Query;
import com.google.code.morphia.query.UpdateOperations;
import com.google.code.morphia.query.UpdateResults;
import com.mongodb.Mongo;

public class BookDAOImpl extends BasicDAO<Book, ObjectId> implements BookDAO {

	public BookDAOImpl(Mongo mongo, Morphia morphia, String dbName) {
		super(mongo, morphia, dbName);
	}

	public BookDAOImpl(Datastore ds) {
		super(ds);
	}

	@Override
	public List<Book> findAll() {
		@SuppressWarnings("serial")
		Map<String, String> query = new HashMap<String, String>() {{
			put("author", "asc");
			put("title", "asc");
		}};
		return findByQuery(query);
	}
	
	@Override
	public List<Book> findByQuery(Map<String, String> queries) {
		List<String> query = new ArrayList<String>();
		for (String field : queries.keySet()) {
			String order = queries.get(field);
			if(order.equalsIgnoreCase("desc")) {
				field = "-" + field;
			}
			query.add(field);
		}
		Query<Book> q = ds.createQuery(Book.class).order(StringUtils.join(query, ","));
		return q.asList();
	}
	
	@Override
	public Book safeUpdate(Book item) throws ConcurrentModificationException {
		Query<Book> query = ds.createQuery(Book.class).field(Mapper.ID_KEY).equal(item.getId()).field("version")
				.equal(item.getVersion());
		UpdateOperations<Book> ops = ds.createUpdateOperations(Book.class).set("author", item.getAuthor())
				.set("isbn", item.getIsbn()).set("publisher", item.getPublisher()).set("language", item.getLanguage())
				.set("pages", item.getPages()).set("publisheddate", item.getPublisheddate())
				.set("publishedplace", item.getPublishedplace()).set("series", item.getSeries())
				.set("originaltitle", item.getOriginaltitle()).set("translator", item.getTranslator())
				.set("source", item.getSource()).inc("version");
		UpdateResults<Book> result = ds.update(query, ops);
		if (!result.getUpdatedExisting()) {
			throw new ConcurrentModificationException("Book has already been modified");
		}
		item.setVersion(item.getVersion() + 1);
		return item;
	}

}
