package models;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.Morphia;
import com.mongodb.Mongo;

public final class BookDAOFactory {

	public static BookDAO getBookDAO(Datastore ds) {
		return new BookDAOImpl(ds);
	}

	public static BookDAO getBookDAO(Mongo mongo, Morphia morphia, String dbName) {
		return new BookDAOImpl(mongo, morphia, dbName);
	}
}
