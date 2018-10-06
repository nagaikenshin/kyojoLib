package org.kyojo.plugin.xml10;

import java.util.regex.Matcher
import java.util.regex.Pattern

import org.apache.commons.text.StringEscapeUtils;
import org.kyojo.plugin.markup.AbstractBracketsNode;
import org.kyojo.plugin.markup.BracketsNode;

class GenericXmlNode extends AbstractBracketsNode<XmlNode>
		implements XmlNode, BracketsNode<XmlNode> {

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

	@Override
	public String escape(String text) {
		return StringEscapeUtils.escapeXml10(text);
	}

	public static class Builder<T extends XmlNode>
			extends AbstractBracketsNode.Builder<T, XmlNode> {
	}

	private static final String nameStartPtStr = ":A-Z_a-z\\xC0-\\xD6\\xD8-\\xF6\\xF8-\\x2FF\\x370-\\x37D\\x37F-\\x1FFF\\x200C-\\x200D\\x2070-\\x218F\\x2C00-\\x2FEF\\x3001-\\xD7FF\\xF900-\\xFDCF][:A-Z_a-z\\xC0-\\xD6\\xD8-\\xF6\\xF8-\\x2FF\\x370-\\x37D\\x37F-\\x1FFF\\x200C-\\x200D\\x2070-\\x218F\\x2C00-\\x2FEF\\x3001-\\xD7FF\\xF900-\\xFDCF";

	private static Pattern tagNamePt = Pattern.compile("[" + nameStartPtStr + "][" + nameStartPtStr + "\\-\\.0-9\\xB7\\x0300-\\x036F\\x203F-\\x2040]*");

	private static Pattern tagAttrPt = tagNamePt;

	public static void sanitize(XmlNode node) {
		if(node == null) {
			return;
		}

		if(node instanceof XmlElement) {
			XmlElement element = (XmlElement)node;

			if(element.getName() != null) {
				// 文字種チェック
				Matcher tagNameMt = tagNamePt.matcher(element.getName());
				if(!tagNameMt.matches()) {
					element.setName("_");
				}
			}

			if(element.getAttrs() != null) {
				Set<String> rmvs = new HashSet<>();
				for(Map.Entry<String, String> ent: element.getAttrs().entrySet()) {
					// 文字種チェック
					Matcher tagAttrMt = tagAttrPt.matcher(ent.getKey());
					if(!tagAttrMt.matches()) {
						rmvs.add(ent.getKey());
						continue;
					}

					// 属性は100文字まで
					if(ent.getValue() != null && ent.getValue().length() > 100) {
						rmvs.add(ent.getKey());
						continue;
					}
				}
				for(String key: rmvs) {
					element.getAttrs().remove(key);
				}
			}

			// rawは禁止
			if(element.getRaw() != null) {
				element.setRaw(null);
			}
		}

		if(node.getNodes() != null) {
			for(XmlNode node2: node.getNodes()) {
				sanitize(node2);
			}
		}
	}

}
