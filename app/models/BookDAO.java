package models;

import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;

import com.google.code.morphia.Key;
import com.google.code.morphia.dao.DAO;
import com.mongodb.WriteConcern;
import com.mongodb.WriteResult;

/**
 * Custom DAO for handling book operations
 */
public interface BookDAO extends DAO<Book, ObjectId> {

	/**
	 * Get all books in the database
	 * 
	 * @return A {@link List} containing objects type {@link Book}
	 * @throws InternalServerErrorException
	 */
	List<Book> findAll() throws InternalServerErrorException;

	/**
	 * Updates an existing book. If the item has been modified between the read
	 * and write this will throw a {@link ConcurrentModificationException}
	 * 
	 * @param item
	 *            A existing {@link Book} to update
	 * @return Returns the updated {@link Book}
	 * @throws InternalServerErrorException
	 */
	Book safeUpdate(Book item) throws ConcurrentModificationException, InternalServerErrorException;

	/**
	 * Given map of queries return all matching books. The query is a
	 * {@link Map} of field names and order, for example "title" and "desc".
	 * Valid orderings are "asc" and "desc".
	 * 
	 * @param A
	 *            {@link Map} of field, order values
	 * @return a {@link List} of all matching {@link Book}.
	 * @throws InternalServerErrorException
	 */
	List<Book> findByQuery(Map<String, String> queries) throws InternalServerErrorException;

	/**
	 * Saves a new book to the database.
	 * 
	 * @param item
	 *            A new book to save
	 * @param wc
	 *            The requested write concern
	 * @return The {@link Key} for the saved book
	 * @throws InternalServerErrorException
	 */
	Key<Book> saveNew(Book item, WriteConcern wc) throws InternalServerErrorException;

	/**
	 * Get a single book based on the id
	 * 
	 * @param id
	 * @return The book
	 * @throws InternalServerErrorException
	 */
	Book getSingle(ObjectId id) throws InternalServerErrorException;

	/**
	 * Delete a single book based on the id
	 * 
	 * @param id
	 * @return The result of the operation
	 * @throws InternalServerErrorException
	 */
	WriteResult deleteSingle(ObjectId id) throws InternalServerErrorException;
}
