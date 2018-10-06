package org.kyojo.plugin.xml10.xhtml11;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.kyojo.core.Constants;
import org.kyojo.core.GlobalData;
import org.kyojo.core.PluginException;
import org.kyojo.core.RequestData;
import org.kyojo.core.ResponseData;
import org.kyojo.core.SessionData;
import org.kyojo.core.TemplateEngine;
import org.kyojo.gson.reflect.TypeToken;
import org.kyojo.minion.My;
import org.kyojo.plugin.markup.AbstractBracketsElement;
import org.kyojo.plugin.markup.BracketsElement;
import org.kyojo.plugin.xml10.XmlElement;
import org.kyojo.plugin.xml10.XmlNode;

public class XhtmlElementImpl extends AbstractBracketsElement<XmlNode, XmlElement>
		implements XhtmlElement, BracketsElement<XmlNode> {

	public XhtmlElementImpl() {
	}

	public XhtmlElementImpl(String name) {
		setName(name);
	}

	private List<XmlNode> nodes;

	@Override
	public List<XmlNode> getNodes() {
		return nodes;
	}

	@Override
	public void setNodes(List<XmlNode> nodes) {
		this.nodes = nodes;
	}

	@Override
	public String getRootSpec() {
		return "xml10/xhtml11";
	}

	@Override
	public String getRootName() {
		return "genericXhtml";
	}

	@Override
	public String getDefaultSpec() {
		return getRootSpec();
	}

	@Override
	public String getDefaultName() {
		return getRootName();
	}

	private String defaultClass;

	@Override
	public String getDefaultClass() {
		return defaultClass;
	}

	@Override
	public void setDefaultClass(String defaultClass) {
		this.defaultClass = defaultClass;
	}

	private String addClass;

	@Override
	public String getAddClass() {
		return addClass;
	}

	@Override
	public void setAddClass(String addClass) {
		this.addClass = addClass;
	}

	@Override
	public void composeClass() {
		// defaultClass
		if (getAttrs() != null && getAttrs().containsKey("class") && StringUtils.isNotBlank(getAttrs().get("class"))) {
			// attrs.classが存在すれば上書きしない
		} else if (StringUtils.isNotBlank(defaultClass)) {
			if (getAttrs() == null) {
				setAttrs(new HashMap<>());
			}
			getAttrs().put("class", defaultClass);
		}

		// addClass
		if (StringUtils.isNotBlank(addClass)) {
			if (getAttrs() == null) {
				setAttrs(new HashMap<>());
			}
			if (!getAttrs().containsKey("class") || StringUtils.isBlank(getAttrs().get("class"))) {
				getAttrs().put("class", addClass);
			} else {
				Set<String> clss = new HashSet<>();
				Stream<String> strm = Arrays.stream(getAttrs().get("class").split("\\s+"));
				strm.forEach(cls -> {
					clss.add(cls);
				});
				strm = Arrays.stream(addClass.split("\\s+"));
				strm.forEach(cls -> {
					clss.add(cls);
				});
				getAttrs().put("class", StringUtils.join(clss, " "));
			}
		}
	}

	@Override
	public void extract(String args, GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, Set<String> valueSet) throws PluginException {
		super.extract(args, gbd, ssd, rqd, rpd, te, valueSet);

		composeClass();
	}

	@Override
	public String getDefaultOpen() {
		return null;
	}

	@Override
	public XhtmlNode createNode() {
		return new XhtmlNodeImpl();
	}

	@Override
	public XhtmlElement createElement() {
		return new XhtmlElementImpl();
	}

	@Override
	public boolean attach(String args, GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, Set<String> valueSet) throws PluginException {
		return attachXhtmlCommon(args, gbd, ssd, rqd, rpd, te, valueSet);
	}

	public boolean attachXhtmlCommon(String args, GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, Set<String> valueSet) throws PluginException {
		return attachCommon(args, gbd, ssd, rqd, rpd, te, valueSet)
				|| attachAttrs(args, gbd, ssd, rqd, rpd, te, valueSet)
				|| attachClass(args, gbd, ssd, rqd, rpd, te, valueSet)
				|| attachAddClass(args, gbd, ssd, rqd, rpd, te, valueSet)
				|| attachStyle(args, gbd, ssd, rqd, rpd, te, valueSet)
				|| attachDisabled(args, gbd, ssd, rqd, rpd, te, valueSet)
				|| attachReadonly(args, gbd, ssd, rqd, rpd, te, valueSet);
	}

	public boolean attachAttrs(String args, GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, Set<String> valueSet) throws PluginException {
		return attachWithNaming(args, gbd, ssd, rqd, rpd, te, "Attrs." + Constants.MINION_SUFFIX, (mnn) -> {
			Type mapType = TypeToken.getParameterized(Map.class, String.class, String.class).getType();
			Map<String, String> attrs = My.deminion(mnn, mapType);
			if (attrs != null && attrs.size() > 0) {
				getAttrs().putAll(attrs);
				return true;
			}

			return false;
		});
	}

	public boolean attachClass(String args, GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, Set<String> valueSet) throws PluginException {
		return attachWithNaming(args, gbd, ssd, rqd, rpd, te, "Class", (str) -> {
			if (StringUtils.isNotBlank(str)) {
				getAttrs().put("class", str);
				return true;
			}

			return false;
		});
	}

	public boolean attachAddClass(String args, GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, Set<String> valueSet) throws PluginException {
		return attachWithNaming(args, gbd, ssd, rqd, rpd, te, "AddClass", (str) -> {
			if (StringUtils.isNotBlank(str)) {
				if (!getAttrs().containsKey("class") || StringUtils.isBlank(getAttrs().get("class"))) {
					getAttrs().put("class", str);
				} else {
					Set<String> clss = new HashSet<>();
					Stream<String> strm = Arrays.stream(getAttrs().get("class").split("\\s+"));
					strm.forEach(cls -> {
						clss.add(cls);
					});
					strm = Arrays.stream(str.split("\\s+"));
					strm.forEach(cls -> {
						clss.add(cls);
					});
					getAttrs().put("class", StringUtils.join(clss, " "));
				}

				return true;
			}

			return false;
		});
	}

	public boolean attachStyle(String args, GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, Set<String> valueSet) throws PluginException {
		return attachWithNaming(args, gbd, ssd, rqd, rpd, te, "Style", (str) -> {
			if (StringUtils.isNotBlank(str)) {
				getAttrs().put("style", str);
				return true;
			}

			return false;
		});
	}

	public boolean attachDisabled(String args, GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, Set<String> valueSet) throws PluginException {
		return attachWithNaming(args, gbd, ssd, rqd, rpd, te, "Disabled", (str) -> {
			if (StringUtils.isBlank(str) || str == "0" || str == "false" || str == "FALSE") {
				getAttrs().remove("disabled");
			} else {
				getAttrs().put("disabled", "disabled");
			}

			return true;
		});
	}

	public boolean attachReadonly(String args, GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, Set<String> valueSet) throws PluginException {
		return attachWithNaming(args, gbd, ssd, rqd, rpd, te, "Readonly", (str) -> {
			if (StringUtils.isBlank(str) || str == "0" || str == "false" || str == "FALSE") {
				getAttrs().remove("readonly");
			} else {
				getAttrs().put("readonly", "readonly");
			}

			return true;
		});
	}

	@Override
	public String escape(String text) {
		return StringEscapeUtils.escapeXml10(text);
	}

	public static class Builder<T extends XhtmlElement> extends AbstractBracketsElement.Builder<T, XmlNode> {

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
