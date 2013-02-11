package models;

import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;

import com.google.code.morphia.dao.DAO;

/**
 * Custom DAO for handling book operations
 */
public interface BookDAO extends DAO<Book, ObjectId> {

	/**
	 * Get all items in the database
	 * @return A {@link List} containing objects type {@link Book}
	 */
	List<Book> findAll();

	/**
	 * Updates an existing item. If the item has been modified between the 
	 * read and write this should throw a {@link ConcurrentModificationException}
	 * @param item A existing {@link Book} to update
	 * @return Returns the updated {@link Book}
	 */
	Book safeUpdate(Book item) throws ConcurrentModificationException;

	/**
	 * Given map of queries return all matching items. The query is a {@link Map}
	 * of field names and order, for example "title" and "desc". Valid orderings
	 * are "asc" and "desc".
	 * @param A {@link Map} of field, order values
	 * @return a {@link List} of all matching {@link Book}.
	 */
	List<Book> findByQuery(Map<String, String> queries);
}
