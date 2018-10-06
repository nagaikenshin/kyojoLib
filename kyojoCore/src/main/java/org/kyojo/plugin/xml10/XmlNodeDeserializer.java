package org.kyojo.plugin.xml10;

import java.lang.reflect.Type;

import org.kyojo.gson.JsonDeserializationContext;
import org.kyojo.gson.JsonDeserializer;
import org.kyojo.gson.JsonElement;
import org.kyojo.gson.JsonObject;
import org.kyojo.gson.JsonParseException;

public class XmlNodeDeserializer implements JsonDeserializer<XmlNode> {

	@Override
	public XmlNode deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context)
			throws JsonParseException {
		if(jsonElement.isJsonPrimitive()) {
			XmlNode xmlNode = new XmlNodeImpl();
			xmlNode.setText(jsonElement.getAsString());
			return xmlNode;
		}

		JsonObject jsonObject = jsonElement.getAsJsonObject();
		if(jsonObject.has("name")) {
			return context.deserialize(jsonElement, XmlElementImpl.class);
		} else {
			return context.deserialize(jsonElement, XmlNodeImpl.class);
		}
	}

}
