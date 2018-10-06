package org.kyojo.plugin.xml10;

import java.util.Map;

import org.kyojo.plugin.markup.BracketsElement;

public interface XmlElement extends XmlNode, BracketsElement<XmlNode> {

	public String getSpec();

	public void setSpec(String spec);

	public String getName();

	public void setName(String name);

	public Map<String, String> getAttrs();

	public void setAttrs(Map<String, String> attrs);

	public String getOpen();

	public void setOpen(String open);

}
