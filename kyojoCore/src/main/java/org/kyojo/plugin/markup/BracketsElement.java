package org.kyojo.plugin.markup;

import java.util.Map;

public interface BracketsElement<N extends BracketsNode<N>> extends BracketsNode<N> {

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

	public String getDefaultIndent();

}
