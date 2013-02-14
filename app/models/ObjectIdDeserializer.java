package models;

import java.io.IOException;

import org.bson.types.ObjectId;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;

class ObjectIdDeserializer extends JsonDeserializer<ObjectId> {

	@Override
	public ObjectId deserialize(JsonParser parser, DeserializationContext context) throws IOException, JsonProcessingException {
		String value = parser.getText();
		if (value != null && !value.equalsIgnoreCase("null")) {
			try {
				ObjectId id = new ObjectId(value);
				return id;
			} catch (Exception e) {
				throw new JsonParseException(e.getMessage(), parser.getCurrentLocation(), e);
			}
		}
		return null;
	}

}
