package controllers;

import models.Book;
import models.BookDAO;

import org.bson.types.ObjectId;

public class MorphiaObject {
	static public BookDAO<Book, ObjectId> dao;
}