package org.kyojo.plugin.html5;

import java.util.Map;

import org.kyojo.plugin.markup.BracketsElement;

public interface HtmlElement extends HtmlNode, BracketsElement<HtmlNode> {

	public String getSpec();

	public void setSpec(String spec);

	public String getName();

	public void setName(String name);

	public Map<String, String> getAttrs();

	public void setAttrs(Map<String, String> attrs);

	public String getType();

	public void setType(String type);

	public String getOpen();

	public void setOpen(String open);

	public String getDefaultClass();

	public void setDefaultClass(String defaultClass);

	public String getAddClass();

	public void setAddClass(String addClass);

	public void composeClass();

}
