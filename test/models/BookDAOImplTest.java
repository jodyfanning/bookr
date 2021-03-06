package models;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;
import org.junit.Test;

import com.google.code.morphia.DatastoreImpl;
import com.google.code.morphia.mapping.Mapper;
import com.google.code.morphia.query.FieldEnd;
import com.google.code.morphia.query.Query;
import com.google.code.morphia.query.UpdateOperations;
import com.google.code.morphia.query.UpdateResults;
import com.mongodb.MongoException;

public class BookDAOImplTest {

	private static final String DEFAULT_QUERY = "author,title";

	DatastoreImpl ds = mock(DatastoreImpl.class);

	private void setupMocks() {
		Mapper mapper = mock(Mapper.class);
		when(ds.getMapper()).thenReturn(mapper);
		when(mapper.addMappedClass(any(Class.class))).thenReturn(null);
	}

	@Test
	public void findsAll() {
		@SuppressWarnings("serial")
		List<Book> fakeList = new ArrayList<Book>() {
			{
				add(new Book("A fake book"));
			}
		};

		setupMocks();

		@SuppressWarnings("unchecked")
		Query<Book> query = mock(Query.class);
		when(ds.createQuery(Book.class)).thenReturn(query);
		when(query.order(DEFAULT_QUERY)).thenReturn(query);
		when(query.asList()).thenReturn(fakeList);

		BookDAOImpl bookDAO = new BookDAOImpl(ds);
		List<Book> list = bookDAO.findAll();
		assertThat(list).isEqualTo(fakeList);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void safeUpdate() throws ConcurrentModificationException, InternalServerErrorException, InvalidContentException {
		setupMocks();
		Book originalBook = new Book("Another book");
		originalBook.setPublishedplace("Helsinki");
		int originalVersion = originalBook.getVersion();

		Query<Book> query = mock(Query.class);
		FieldEnd end = mock(FieldEnd.class);
		when(ds.createQuery(Book.class)).thenReturn(query);
		when(query.field(any(String.class))).thenReturn(end);
		when(end.equal(any(ObjectId.class))).thenReturn(query);

		UpdateOperations<Book> ops = mock(UpdateOperations.class);
		when(ds.createUpdateOperations(Book.class)).thenReturn(ops);
		when(ops.set(any(String.class), any(Object.class))).thenReturn(ops);
		when(ops.unset(any(String.class))).thenReturn(ops);
		when(ops.inc(any(String.class))).thenReturn(ops);

		UpdateResults<Book> result = mock(UpdateResults.class);
		when(ds.update(query, ops)).thenReturn(result);
		when(result.getUpdatedExisting()).thenReturn(true);

		BookDAOImpl bookDAO = new BookDAOImpl(ds);
		Book updatedBook = bookDAO.safeUpdate(originalBook);
		assertThat(updatedBook.getVersion()).isEqualTo(originalVersion + 1);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void safeUpdateThrowsConcurrentException() {
		setupMocks();
		Book originalBook = new Book("Another book");

		Query<Book> query = mock(Query.class);
		FieldEnd end = mock(FieldEnd.class);
		when(ds.createQuery(Book.class)).thenReturn(query);
		when(query.field(any(String.class))).thenReturn(end);
		when(end.equal(any(ObjectId.class))).thenReturn(query);

		UpdateOperations<Book> ops = mock(UpdateOperations.class);
		when(ds.createUpdateOperations(Book.class)).thenReturn(ops);
		when(ops.set(any(String.class), any(Object.class))).thenReturn(ops);
		when(ops.inc(any(String.class))).thenReturn(ops);

		UpdateResults<Book> result = mock(UpdateResults.class);
		when(ds.update(query, ops)).thenReturn(result);
		when(result.getUpdatedExisting()).thenReturn(false);

		BookDAOImpl bookDAO = new BookDAOImpl(ds);
		try {
			bookDAO.safeUpdate(originalBook);
		} catch (ConcurrentModificationException e) {
			return;
		} catch (InternalServerErrorException e) {
			fail("Internal server error");
		} catch (InvalidContentException e) {
			fail("Invalid content");
		}
		fail("Didn't get concurrent modification exception");
	}

	@Test
	public void safeUpdateThrowsInvalidException() {
		setupMocks();
		Book originalBook = new Book();

		BookDAOImpl bookDAO = new BookDAOImpl(ds);
		try {
			bookDAO.safeUpdate(originalBook);
		} catch (ConcurrentModificationException e) {
			fail("Concurrent error");
		} catch (InternalServerErrorException e) {
			fail("Internal server error");
		} catch (InvalidContentException e) {
			return;
		}
		fail("Didn't get invalid data exception");
	}

	@Test
	public void safeUpdateThrowsServerException() {
		setupMocks();
		Book originalBook = new Book("bob");
		doThrow(new MongoException("Arrrgggh")).when(ds).createQuery(Book.class);

		BookDAOImpl bookDAO = new BookDAOImpl(ds);
		try {
			bookDAO.safeUpdate(originalBook);
		} catch (ConcurrentModificationException e) {
			fail("Concurrent error");
		} catch (InternalServerErrorException e) {
			return;
		} catch (InvalidContentException e) {
			fail("Invalid content error");
		}
		fail("Didn't get server error exception");
	}

	@Test
	public void queryByFieldAndOrder() {
		@SuppressWarnings("serial")
		List<Book> fakeList = new ArrayList<Book>() {
			{
				add(new Book("A fake book"));
			}
		};

		setupMocks();

		@SuppressWarnings("unchecked")
		Query<Book> query = mock(Query.class);
		when(ds.createQuery(Book.class)).thenReturn(query);
		when(query.order("author,-title,source")).thenReturn(query);
		when(query.asList()).thenReturn(fakeList);

		BookDAOImpl bookDAO = new BookDAOImpl(ds);

		@SuppressWarnings("serial")
		Map<String, String> q = new HashMap<String, String>() {
			{
				put("author", "asc");
				put("title", "desc");
				put("source", "asc");
			}
		};

		List<Book> list = bookDAO.findByQuery(q);
		assertThat(list).isEqualTo(fakeList);

		// What happens when the map is empty?
		q = new HashMap<String, String>();
		when(query.order(DEFAULT_QUERY)).thenReturn(query);
		list = bookDAO.findByQuery(q);
		verify(query).order(DEFAULT_QUERY);
	}
}
