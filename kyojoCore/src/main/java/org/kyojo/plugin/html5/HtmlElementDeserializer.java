package org.kyojo.plugin.html5;

import java.lang.reflect.Type;

import org.kyojo.gson.JsonDeserializationContext;
import org.kyojo.gson.JsonDeserializer;
import org.kyojo.gson.JsonElement;
import org.kyojo.gson.JsonParseException;

public class HtmlElementDeserializer implements JsonDeserializer<HtmlElement> {

	@Override
	public HtmlElement deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context)
			throws JsonParseException {
		if(jsonElement.isJsonPrimitive()) {
			HtmlElement htmlElement = new HtmlElementImpl();
			htmlElement.setText(jsonElement.getAsString());
			return htmlElement;
		}

		return context.deserialize(jsonElement, HtmlElementImpl.class);
	}

}
