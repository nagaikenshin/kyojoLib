package org.kyojo.plugin.xml10.xhtml11;

import org.kyojo.plugin.xml10.XmlElement;

public interface XhtmlElement extends XmlElement {

	public String getType();

	public void setType(String type);

	public String getDefaultClass();

	public void setDefaultClass(String defaultClass);

	public String getAddClass();

	public void setAddClass(String addClass);

	public void composeClass();

}
