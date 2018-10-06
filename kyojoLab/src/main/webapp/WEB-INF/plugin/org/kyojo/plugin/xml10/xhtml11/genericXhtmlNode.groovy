package org.kyojo.plugin.xml10.xhtml11;

import java.util.regex.Matcher
import java.util.regex.Pattern
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils
import org.kyojo.plugin.markup.AbstractBracketsNode
import org.kyojo.plugin.markup.BracketsNode

class GenericXhtmlNode extends AbstractBracketsNode<XhtmlNode>
		implements XhtmlNode, BracketsNode<XhtmlNode> {

	private List<XhtmlNode> nodes;

	@Override
	public List<XhtmlNode> getNodes() {
		return nodes;
	}

	@Override
	public void setNodes(List<XhtmlNode> nodes) {
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
	public String escape(String text) {
		return StringEscapeUtils.escapeXml10(text);
	}

	public static class Builder<T extends XhtmlNode>
			extends AbstractBracketsNode.Builder<T, XhtmlNode> {
	}

	private static final String nameStartPtStr = ":A-Z_a-z\\xC0-\\xD6\\xD8-\\xF6\\xF8-\\x2FF\\x370-\\x37D\\x37F-\\x1FFF\\x200C-\\x200D\\x2070-\\x218F\\x2C00-\\x2FEF\\x3001-\\xD7FF\\xF900-\\xFDCF][:A-Z_a-z\\xC0-\\xD6\\xD8-\\xF6\\xF8-\\x2FF\\x370-\\x37D\\x37F-\\x1FFF\\x200C-\\x200D\\x2070-\\x218F\\x2C00-\\x2FEF\\x3001-\\xD7FF\\xF900-\\xFDCF";

	private static Pattern tagNamePt = Pattern.compile("[" + nameStartPtStr + "][" + nameStartPtStr + "\\-\\.0-9\\xB7\\x0300-\\x036F\\x203F-\\x2040]*");

	private static Pattern tagAttrPt = tagNamePt;

	private static Pattern styleUrlPt = Pattern.compile("url\\s*\\(", Pattern.CASE_INSENSITIVE);

	public static void sanitize(XhtmlNode node) {
		if(node == null) {
			return;
		}

		if(node instanceof XhtmlElement) {
			XhtmlElement element = (XhtmlElement)node;

			if(element.getName() != null) {
				// 文字種チェック
				Matcher tagNameMt = tagNamePt.matcher(element.getName());
				if(!tagNameMt.matches()) {
					element.setName("div");
				}

				String name = element.getName().toLowerCase().trim();
				if(name.equals("base") || name.equals("head") || name.equals("link") || name.equals("meta") ||
						name.equals("style") || name.equals("applet") || name.equals("audio") || name.equals("body") ||
						name.equals("embed") || name.equals("frame") || name.equals("frameset") || name.equals("html") ||
						name.equals("iframe") || name.equals("img") || name.equals("object") || name.equals("picture") ||
						name.equals("script") || name.equals("source") || name.equals("video")) {
					element.setName("div");
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

					String key = ent.getKey().toLowerCase().trim();

					// onで始まる属性は禁止
					if(key.startsWith("on")) {
						rmvs.add(ent.getKey());
						continue;
					}

					// 属性は100文字まで
					if(ent.getValue() != null && ent.getValue().length() > 100) {
						rmvs.add(ent.getKey());
						continue;
					}

					if(key.equals("style")) {
						// styleはurlを含んではいけない
						if(ent.getValue() != null) {
							Matcher styleUrlMt = styleUrlPt.matcher(ent.getValue());
							if(styleUrlMt.find()) {
								rmvs.add(ent.getKey());
								continue;
							}
						}
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
			for(XhtmlNode node2: node.getNodes()) {
				sanitize(node2);
			}
		}
	}

}
