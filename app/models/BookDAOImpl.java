package models;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.bson.types.ObjectId;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.Key;
import com.google.code.morphia.Morphia;
import com.google.code.morphia.dao.BasicDAO;
import com.google.code.morphia.mapping.Mapper;
import com.google.code.morphia.query.Query;
import com.google.code.morphia.query.UpdateOperations;
import com.google.code.morphia.query.UpdateResults;
import com.mongodb.Mongo;
import com.mongodb.WriteConcern;
import com.mongodb.WriteResult;

public class BookDAOImpl extends BasicDAO<Book, ObjectId> implements BookDAO {

	public BookDAOImpl(Mongo mongo, Morphia morphia, String dbName) {
		super(mongo, morphia, dbName);
	}

	public BookDAOImpl(Datastore ds) {
		super(ds);
	}

	@Override
	public List<Book> findAll() {
		return findByQuery(new HashMap<String, String>());
	}

	@Override
	public List<Book> findByQuery(Map<String, String> queries) {
		List<String> query = new ArrayList<String>();
		if (null == queries || queries.isEmpty()) {
			query.add("author");
			query.add("title");
		} else {
			for (String field : queries.keySet()) {
				String order = queries.get(field);
				if (order.equalsIgnoreCase("desc")) {
					field = "-" + field;
				}
				query.add(field);
			}
		}
		Query<Book> q = ds.createQuery(Book.class).order(StringUtils.join(query, ","));
		return q.asList();
	}

	@Override
	public Book safeUpdate(Book item) throws ConcurrentModificationException, InternalServerErrorException {
		Query<Book> query = ds.createQuery(Book.class).field(Mapper.ID_KEY).equal(item.getId()).field("version")
				.equal(item.getVersion());

		UpdateOperations<Book> ops = ds.createUpdateOperations(Book.class);

		List<String> fields = Book.properties;
		for (String field : fields) {
			try {
				Object value = PropertyUtils.getSimpleProperty(item, field);
				if (value != null) {
					ops.set(field.toLowerCase(Locale.ENGLISH), value.toString());
				} else {
					ops.unset(field.toLowerCase(Locale.ENGLISH));
				}
			} catch (IllegalAccessException e) {
				throw new InternalServerErrorException(e);
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			}
		}

		ops.inc("version");

		UpdateResults<Book> result = ds.update(query, ops);
		if (!result.getUpdatedExisting()) {
			throw new ConcurrentModificationException("Book has already been modified");
		}
		item.setVersion(item.getVersion() + 1);
		return item;
	}

	@Override
	public Key<Book> saveNew(Book item, WriteConcern wc) throws InternalServerErrorException {
		try {
			return save(item, new WriteConcern(true));
		} catch (Exception e) {
			throw new InternalServerErrorException(e);
		}
	}

	@Override
	public Book getSingle(ObjectId id) throws InternalServerErrorException {
		try {
			return get(id);
		} catch (Exception e) {
			throw new InternalServerErrorException(e);
		}
	}

	@Override
	public WriteResult deleteSingle(ObjectId id) throws InternalServerErrorException {
		try {
			return deleteById(id);
		} catch (Exception e) {
			throw new InternalServerErrorException(e);
		}
	}
}
