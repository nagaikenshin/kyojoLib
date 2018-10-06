package org.kyojo.plugin.xml10;

import java.util.List;

import org.kyojo.plugin.markup.BracketsNode;

public interface XmlNode extends BracketsNode<XmlNode> {

	@Override
	public List<XmlNode> getNodes();

	@Override
	public void setNodes(List<XmlNode> nodes);

}
