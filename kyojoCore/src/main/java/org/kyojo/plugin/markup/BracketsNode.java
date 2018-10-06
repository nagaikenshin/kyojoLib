package org.kyojo.plugin.markup;

import java.util.List;

public interface BracketsNode<N extends BracketsNode<N>> {

	public String getSpec();

	public void setSpec(String spec);

	public String getKey();

	public void setKey(String key);

	public List<N> getNodes();

	public void setNodes(List<N> nodes);

	public String getText();

	public void setText(String text);

	public String getRaw();

	public void setRaw(String raw);

	public String getRootSpec();

	public String getRootName();

	public String getDefaultSpec();

	public String getExpires();

	public void setExpires(String expires);

	public String escape(String text);

}
