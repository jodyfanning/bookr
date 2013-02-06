package models;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.bson.types.ObjectId;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.DeserializationContext;
import org.junit.Test;

public class ObjectIdDeserializerTest {

	private ObjectIdDeserializer deserializer = new ObjectIdDeserializer();
	String id = ObjectId.get().toString();
	final JsonParser parser = mock(JsonParser.class);
	final DeserializationContext context = mock(DeserializationContext.class);

	@Test
	public void deserializesAValidObjectId() throws JsonProcessingException, IOException {
		when(parser.getText()).thenReturn(id);
		ObjectId newId = deserializer.deserialize(parser, context);
		assertThat(newId.toString()).isEqualTo(id);
	}

	@Test
	public void nullIfNullJSONObjectId() throws JsonParseException, IOException {
		when(parser.getText()).thenReturn("null");
		ObjectId newId = deserializer.deserialize(parser, context);
		assertThat(newId).isNull();
	}

	@Test
	public void nullIfNullObjectId() throws JsonParseException, IOException {
		when(parser.getText()).thenReturn(null);
		ObjectId newId = deserializer.deserialize(parser, context);
		assertThat(newId).isNull();
	}

	@Test
	public void exceptionIfInvalidObjectId() {
		try {
			when(parser.getText()).thenReturn("abc234");
			deserializer.deserialize(parser, context);
		} catch (Exception e) {
			assertThat(e).isInstanceOf(JsonParseException.class);
		}
	}

}
