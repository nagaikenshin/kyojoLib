package org.kyojo.plugin.html5;

import java.util.List;

import org.kyojo.plugin.markup.BracketsNode;

public interface HtmlNode extends BracketsNode<HtmlNode> {

	@Override
	public List<HtmlNode> getNodes();

	@Override
	public void setNodes(List<HtmlNode> nodes);

}
