package models;

import java.util.List;

import com.google.code.morphia.dao.DAO;

public interface BookDAO<T,K> extends DAO<T, K> {

	List<T> findAll();
}
