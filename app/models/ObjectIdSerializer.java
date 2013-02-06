package models;

import java.io.IOException;

import org.bson.types.ObjectId;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;

public class ObjectIdSerializer extends JsonSerializer<ObjectId> {

	public ObjectIdSerializer() {
		super();
	}

	@Override
	public void serialize(ObjectId id, JsonGenerator jgen,
			SerializerProvider provider) throws IOException,
			JsonGenerationException {
		if(id != null) {
			String sId = id.toString();
			jgen.writeString(sId);
			return;
		}
		jgen.writeNull();
	}

}
