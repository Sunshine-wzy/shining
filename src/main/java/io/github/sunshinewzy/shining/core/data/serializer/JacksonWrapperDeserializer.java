package io.github.sunshinewzy.shining.core.data.serializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import io.github.sunshinewzy.shining.core.data.JacksonWrapper;

import java.io.IOException;

public class JacksonWrapperDeserializer extends JsonDeserializer<JacksonWrapper<?>> implements ContextualDeserializer {

	private JavaType type;
	

	@Override
	public JacksonWrapper<?> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
		return new JacksonWrapper<>(ctxt.readValue(p, type));
	}

	@Override
	public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) {
		JacksonWrapperDeserializer deserializer = new JacksonWrapperDeserializer();
		
		if(property == null) {
			deserializer.type = ctxt.getContextualType().containedType(0);
		} else {
			deserializer.type = property.getType().containedType(0);
		}
		
		return deserializer;
	}
	
}
