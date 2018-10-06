package org.kyojo.plugin.html5;

import java.lang.reflect.Type;
import java.util.Set

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils
import org.kyojo.core.Constants;
import org.kyojo.core.GlobalData;
import org.kyojo.core.PluginException;
import org.kyojo.core.RequestData;
import org.kyojo.core.ResponseData;
import org.kyojo.core.SessionData;
import org.kyojo.core.TemplateEngine;
import org.kyojo.gson.reflect.TypeToken;
import org.kyojo.minion.My
import org.kyojo.plugin.markup.AbstractBracketsElement;
import org.kyojo.plugin.markup.BracketsElement

class GenericHtmlElement extends AbstractBracketsElement<HtmlNode, HtmlElement>
		implements HtmlElement, BracketsElement<HtmlNode> {

	public GenericHtmlElement() {
	}

	public GenericHtmlElement(String name) {
		setName(name);
	}

	private List<HtmlNode> nodes;

	@Override
	public List<HtmlNode> getNodes() {
		return nodes;
	}

	@Override
	public void setNodes(List<HtmlNode> nodes) {
		this.nodes = nodes;
	}

	@Override
	public String getRootSpec() {
		return "html5";
	}

	@Override
	public String getRootName() {
		return "genericHtml";
	}

	@Override
	public String getDefaultSpec() {
		return getRootSpec();
	}

	@Override
	public String getDefaultName() {
		return getRootName();
	}

	private String defaultClass

	@Override
	String getDefaultClass() {
		return defaultClass
	}

	@Override
	void setDefaultClass(String defaultClass) {
		this.defaultClass = defaultClass
	}

	private String addClass

	@Override
	String getAddClass() {
		return addClass
	}

	@Override
	void setAddClass(String addClass) {
		this.addClass = addClass
	}

	@Override
	void composeClass() {
		// defaultClass
		if(attrs != null && attrs.containsKey("class") && StringUtils.isNotBlank(attrs["class"])) {
			// attrs.classが存在すれば上書きしない
		} else if(StringUtils.isNotBlank(defaultClass)) {
			if(attrs == null) {
				attrs = [:]
			}
			attrs["class"] = defaultClass
		}

		// addClass
		if(StringUtils.isNotBlank(addClass)) {
			if(attrs == null) {
				attrs = [:]
			}
			if(!attrs.containsKey("class") || StringUtils.isBlank(attrs["class"])) {
				attrs["class"] = addClass
			} else {
				Set<String> clss = []
				attrs["class"].split("\\s+").each {
					clss.add(it)
				}
				addClass.split("\\s+").each {
					clss.add(it)
				}
				attrs["class"] = clss.join(" ")
			}
		}
	}

	@Override
	void extract(String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, Set<String> valueSet) throws PluginException {
		super.extract(args, gbd, ssd, rqd, rpd, te, valueSet)

		composeClass()
	}

	@Override
	public String getDefaultType() {
		return null;
	}

	@Override
	public String getDefaultOpen() {
		return null;
	}

	@Override
	public HtmlNode createNode() {
		return new HtmlNodeImpl();
	}

	@Override
	public HtmlElement createElement() {
		return new HtmlElementImpl();
	}

	@Override
	public boolean attach(String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, Set<String> valueSet) throws PluginException {
		attachHtmlCommon(args, gbd, ssd, rqd, rpd, te, valueSet);
	}

	public boolean attachHtmlCommon(String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, Set<String> valueSet) throws PluginException {
		return attachCommon(args, gbd, ssd, rqd, rpd, te, valueSet) ||
			attachAttrs(args, gbd, ssd, rqd, rpd, te, valueSet) ||
			attachClass(args, gbd, ssd, rqd, rpd, te, valueSet) ||
			attachAddClass(args, gbd, ssd, rqd, rpd, te, valueSet) ||
			attachStyle(args, gbd, ssd, rqd, rpd, te, valueSet) ||
			attachDisabled(args, gbd, ssd, rqd, rpd, te, valueSet) ||
			attachReadonly(args, gbd, ssd, rqd, rpd, te, valueSet);
	}

	public boolean attachAttrs(String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, Set<String> valueSet) throws PluginException {
		return attachWithNaming(args, gbd, ssd, rqd, rpd, te,
				"Attrs." + Constants.MINION_SUFFIX, { mnn ->
			Type mapType = TypeToken.getParameterized(Map.class, String.class, String.class).getType();
			Map<String, String> attrs = My.deminion(mnn, mapType);
			if(attrs != null && attrs.size() > 0) {
				getAttrs().putAll(attrs);
				return true;
			}

			return false;
		})
	}

	public boolean attachClass(String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, Set<String> valueSet) throws PluginException {
		return attachWithNaming(args, gbd, ssd, rqd, rpd, te,
				"Class", { str ->
			if(StringUtils.isNotBlank(str)) {
				getAttrs().put("class", str);
				return true;
			}

			return false;
		})
	}

	public boolean attachAddClass(String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, Set<String> valueSet) throws PluginException {
		return attachWithNaming(args, gbd, ssd, rqd, rpd, te,
				"AddClass", { str ->
			if(StringUtils.isNotBlank(str)) {
				if(!getAttrs().containsKey("class") || StringUtils.isBlank(getAttrs().get("class"))) {
					getAttrs().put("class", str);
				} else {
					Set<String> clss = []
					getAttrs().get("class").split("\\s+").each {
						clss.add(it)
					}
					str.split("\\s+").each {
						clss.add(it)
					}
					getAttrs().put("class", clss.join(" "))
				}

				return true;
			}

			return false;
		})
	}

	public boolean attachStyle(String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, Set<String> valueSet) throws PluginException {
		return attachWithNaming(args, gbd, ssd, rqd, rpd, te,
				"Style", { str ->
			if(StringUtils.isNotBlank(str)) {
				getAttrs().put("style", str);
				return true;
			}

			return false;
		})
	}

	public boolean attachDisabled(String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, Set<String> valueSet) throws PluginException {
		return attachWithNaming(args, gbd, ssd, rqd, rpd, te,
				"Disabled", { str ->
			if(StringUtils.isBlank(str) || str == "0" || str == "false" || str == "FALSE") {
				getAttrs().remove("disabled");
			} else {
				getAttrs().put("disabled", "disabled");
			}

			return true;
		})
	}

	public boolean attachReadonly(String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, Set<String> valueSet) throws PluginException {
		return attachWithNaming(args, gbd, ssd, rqd, rpd, te,
				"Readonly", { str ->
			if(StringUtils.isBlank(str) || str == "0" || str == "false" || str == "FALSE") {
				getAttrs().remove("readonly");
			} else {
				getAttrs().put("readonly", "readonly");
			}

			return true;
		})
	}

	@Override
	public String escape(String text) {
		return StringEscapeUtils.escapeHtml4(text);
	}

	public static class Builder<T extends HtmlElement>
			extends AbstractBracketsElement.Builder<T, HtmlNode> {

		private String defaultClass;
		private String addClass;

		public Builder<T> setDefaultClass(String defaultClass) {
			this.defaultClass = defaultClass;
			return this;
		}

		public Builder<T> setAddClass(String addClass) {
			this.addClass = addClass;
			return this;
		}

		@Override
		public T build() {
			T obj = super.build();
			obj.setDefaultClass(defaultClass);
			obj.setAddClass(addClass);
			return obj;
		}

	}

}
