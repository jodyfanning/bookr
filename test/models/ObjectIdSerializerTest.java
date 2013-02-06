package models;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.io.IOException;

import org.bson.types.ObjectId;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.SerializerProvider;
import org.junit.Test;

public class ObjectIdSerializerTest {
	ObjectId id = ObjectId.get();
	JsonGenerator jgen = mock(JsonGenerator.class);
	SerializerProvider provider = mock(SerializerProvider.class);
	ObjectIdSerializer serializer = new ObjectIdSerializer();

	@Test
	public void stringFromObjectId() throws JsonGenerationException, IOException {
		serializer.serialize(id, jgen, provider);
		verify(jgen).writeString(id.toString());
	}
	
	@Test
	public void nullfromNull() throws JsonGenerationException, IOException {
		serializer.serialize(null, jgen, provider);
		verify(jgen).writeNull();
	}
}
