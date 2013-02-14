import java.net.UnknownHostException;

import models.BookDAOFactory;
import play.GlobalSettings;

import com.google.code.morphia.Morphia;
import com.mongodb.Mongo;

import controllers.MorphiaObject;

public class Global extends GlobalSettings {

	@Override
	public void onStart(play.Application application) {
		super.beforeStart(application);
		try {
			MorphiaObject.dao = BookDAOFactory.getBookDAO(new Mongo("127.0.0.1", 27017), new Morphia(), "bookdatabase");
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}
}