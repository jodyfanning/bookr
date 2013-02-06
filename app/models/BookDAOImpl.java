package models;

import java.util.List;

import org.bson.types.ObjectId;

import com.google.code.morphia.Morphia;
import com.google.code.morphia.dao.BasicDAO;
import com.mongodb.Mongo;

public class BookDAOImpl extends BasicDAO<Book, ObjectId> implements BookDAO<Book, ObjectId> {

	public BookDAOImpl(Mongo mongo, Morphia morphia, String dbName) {
		super(mongo, morphia, dbName);
	}

	@Override
	public List<Book> findAll() {
		return ds.find(Book.class).order("author,title").asList();
	}
	
	
}
