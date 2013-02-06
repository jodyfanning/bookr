package controllers;

import play.mvc.Action;
import play.mvc.Http.Context;
import play.mvc.Result;

public class JsonAction extends Action.Simple {

	@Override
	public Result call(Context ctx) throws Throwable {
		if (play.mvc.Controller.request().accepts("application/json")) {
			return delegate.call(ctx);
		}
		return status(406, "<h1>Only 'application/json' response type is supported</h1>").as("text/html");
	}

}
