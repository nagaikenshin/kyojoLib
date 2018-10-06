package org.kyojo.plugin.html5;

import java.util.regex.Matcher
import java.util.regex.Pattern
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils
import org.kyojo.plugin.html5.HtmlNodeImpl.Builder
import org.kyojo.plugin.markup.AbstractBracketsNode
import org.kyojo.plugin.markup.BracketsNode

class GenericHtmlNode extends AbstractBracketsNode<HtmlNode>
		implements HtmlNode, BracketsNode<HtmlNode> {

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
	public String escape(String text) {
		return StringEscapeUtils.escapeHtml4(text);
	}

	public static class Builder<T extends HtmlNode>
			extends AbstractBracketsNode.Builder<T, HtmlNode> {
	}

	private static Pattern tagNamePt = Pattern.compile("[A-Za-z0-9]+");

	private static Pattern tagAttrPt = Pattern.compile("[\\x21\\x23-\\x26\\x28-\\x2E\\x30-\\x3B\\x3F-\\x7E]+");

	private static Pattern styleUrlPt = Pattern.compile("url\\s*\\(", Pattern.CASE_INSENSITIVE);

	public static void sanitize(HtmlNode node) {
		if(node == null) {
			return;
		}

		if(node instanceof HtmlElement) {
			HtmlElement element = (HtmlElement)node;

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
			for(HtmlNode node2: node.getNodes()) {
				sanitize(node2);
			}
		}
	}

}
