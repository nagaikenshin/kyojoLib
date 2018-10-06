package org.kyojo.plugin.html5;

import java.lang.reflect.Type;

import org.kyojo.gson.JsonDeserializationContext;
import org.kyojo.gson.JsonDeserializer;
import org.kyojo.gson.JsonElement;
import org.kyojo.gson.JsonObject;
import org.kyojo.gson.JsonParseException;

public class HtmlNodeDeserializer implements JsonDeserializer<HtmlNode> {

	@Override
	public HtmlNode deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context)
			throws JsonParseException {
		if(jsonElement.isJsonPrimitive()) {
			HtmlNode htmlNode = new HtmlNodeImpl();
			htmlNode.setText(jsonElement.getAsString());
			return htmlNode;
		}

		JsonObject jsonObject = jsonElement.getAsJsonObject();
		if(jsonObject.has("name")) {
			return context.deserialize(jsonElement, HtmlElementImpl.class);
		} else {
			return context.deserialize(jsonElement, HtmlNodeImpl.class);
		}
	}

}
