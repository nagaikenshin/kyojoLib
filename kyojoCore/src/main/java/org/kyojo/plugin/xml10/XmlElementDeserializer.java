package org.kyojo.plugin.xml10;

import java.lang.reflect.Type;

import org.kyojo.gson.JsonDeserializationContext;
import org.kyojo.gson.JsonDeserializer;
import org.kyojo.gson.JsonElement;
import org.kyojo.gson.JsonParseException;

public class XmlElementDeserializer implements JsonDeserializer<XmlElement> {

	@Override
	public XmlElement deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context)
			throws JsonParseException {
		if(jsonElement.isJsonPrimitive()) {
			XmlElement xmlElement = new XmlElementImpl();
			xmlElement.setText(jsonElement.getAsString());
			return xmlElement;
		}

		return context.deserialize(jsonElement, XmlElementImpl.class);
	}

}
