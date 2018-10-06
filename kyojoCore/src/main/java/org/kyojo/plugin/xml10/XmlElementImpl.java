package org.kyojo.plugin.xml10;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.text.StringEscapeUtils;
import org.kyojo.core.Constants;
import org.kyojo.core.GlobalData;
import org.kyojo.core.PluginException;
import org.kyojo.core.RequestData;
import org.kyojo.core.ResponseData;
import org.kyojo.core.SessionData;
import org.kyojo.core.TemplateEngine;
import org.kyojo.core.annotation.OutOfRequestData;
import org.kyojo.core.annotation.OutOfResponseData;
import org.kyojo.gson.reflect.TypeToken;
import org.kyojo.minion.My;
import org.kyojo.plugin.markup.AbstractBracketsElement;
import org.kyojo.plugin.markup.BracketsElement;

public class XmlElementImpl extends AbstractBracketsElement<XmlNode, XmlElement>
		implements XmlElement, BracketsElement<XmlNode> {

	public XmlElementImpl() {
	}

	public XmlElementImpl(String name) {
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
		return "xml10";
	}

	@Override
	public String getRootName() {
		return "genericXml";
	}

	@Override
	public String getDefaultSpec() {
		return getRootSpec();
	}

	@OutOfRequestData
	@OutOfResponseData
	public String getDefaultIndent() {
		return "\t";
	}

	@Override
	public String getDefaultName() {
		return getRootName();
	}

	@Override
	public String getDefaultOpen() {
		return null;
	}

	@Override
	public XmlNode createNode() {
		return new XmlNodeImpl();
	}

	@Override
	public XmlElement createElement() {
		return new XmlElementImpl();
	}

	@Override
	public boolean attach(String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, Set<String> valueSet) throws PluginException {
		return attachXmlCommon(args, gbd, ssd, rqd, rpd, te, valueSet);
	}

	public boolean attachXmlCommon(String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, Set<String> valueSet) throws PluginException {
		return attachCommon(args, gbd, ssd, rqd, rpd, te, valueSet) ||
			attachAttrs(args, gbd, ssd, rqd, rpd, te, valueSet);
	}

	public boolean attachAttrs(String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, Set<String> valueSet) throws PluginException {
		return attachWithNaming(args, gbd, ssd, rqd, rpd, te,
				"Attrs." + Constants.MINION_SUFFIX, (mnn) -> {
			Type mapType = TypeToken.getParameterized(Map.class, String.class, String.class).getType();
			Map<String, String> attrs = My.deminion(mnn, mapType);
			if(attrs != null && attrs.size() > 0) {
				getAttrs().putAll(attrs);
				return true;
			}

			return false;
		});
	}

	@Override
	public String escape(String text) {
		return StringEscapeUtils.escapeXml10(text);
	}

	public static class Builder<T extends XmlElement>
			extends AbstractBracketsElement.Builder<T, XmlNode> {
	}

}
