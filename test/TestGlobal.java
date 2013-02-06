import java.net.UnknownHostException;

import models.BookDAOImpl;
import play.GlobalSettings;

import com.google.code.morphia.Morphia;
import com.mongodb.Mongo;

import controllers.MorphiaObject;

public class TestGlobal extends GlobalSettings {

	@Override
	public void onStart(play.Application arg0) {
		super.beforeStart(arg0);
		try {
			MorphiaObject.dao = new BookDAOImpl(new Mongo("127.0.0.1", 27017), new Morphia(), "testbookdatabase"); 
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}
}