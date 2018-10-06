package org.kyojo.plugin.markup;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.kyojo.core.Cache;
import org.kyojo.core.CompleteThrowable;
import org.kyojo.core.GlobalData;
import org.kyojo.core.PluginException;
import org.kyojo.core.RedirectThrowable;
import org.kyojo.core.RequestData;
import org.kyojo.core.ResponseData;
import org.kyojo.core.SessionData;
import org.kyojo.core.TemplateEngine;
import org.kyojo.core.Time14;
import org.kyojo.core.annotation.OutOfRequestData;
import org.kyojo.core.annotation.OutOfResponseData;
import org.kyojo.schemaorg.SimpleJsonBuilder;

abstract public class AbstractBracketsNode<N extends BracketsNode<N>> {

	private String spec = getDefaultSpec();

	@OutOfRequestData
	@OutOfResponseData
	public String getSpec() {
		return spec;
	}

	@OutOfRequestData
	@OutOfResponseData
	public void setSpec(String spec) {
		this.spec = spec;
	}

	private String key;

	@OutOfRequestData
	@OutOfResponseData
	public String getKey() {
		return key;
	}

	@OutOfRequestData
	@OutOfResponseData
	public void setKey(String key) {
		this.key = key;
	}

	abstract public List<N> getNodes();

	abstract public void setNodes(List<N> nodes);

	private String text;

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	private String raw;

	public String getRaw() {
		return raw;
	}

	public void setRaw(String raw) {
		this.raw = raw;
	}

	abstract protected String getRootSpec();

	abstract protected String getRootName();

	abstract protected String getDefaultSpec();

	private String expires = getDefaultExpires();

	protected String getDefaultExpires() {
		return null;
	}

	@OutOfRequestData
	@OutOfResponseData
	public String getExpires() {
		return expires;
	}

	@OutOfRequestData
	@OutOfResponseData
	public void setExpires(String expires) {
		this.expires = expires;
	}

	abstract public String escape(String text);

	public Object buildCache(Cache cache, String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, String indent, boolean isForced)
			throws PluginException, RedirectThrowable, CompleteThrowable {
		cache.setExpires(getExpires() == null ? Time14.FUTURE.toString() : getExpires());
		parse(cache, args, gbd, ssd, rqd, rpd, te, "", isForced);

		return null;
	}

	public void parse(Cache cache, String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, String indent, boolean isForced)
			throws PluginException, RedirectThrowable, CompleteThrowable {
		if(hasChildren()) {
			parseChildren(cache, args, gbd, ssd, rqd, rpd, te, "", isForced);
		}
		if(getRaw() != null) {
			cache.addLine(getRaw() + "\n");
		}
		if(getText() != null) {
			cache.addLine(escape(getText()) + "\n");
		}
	}

	public boolean hasChildren() {
		return getNodes() != null;
	}

	public boolean parseChildren(Cache cache, String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, String indent, boolean isForced)
			throws PluginException, RedirectThrowable, CompleteThrowable {
		return parseNodes(cache, args, gbd, ssd, rqd, rpd, te, indent, isForced);
	}

	public boolean parseNodes(Cache cache, String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, String indent, boolean isForced)
			throws PluginException, RedirectThrowable, CompleteThrowable {
		if(getNodes() == null) {
			return false;
		} else {
			for(N node : getNodes()) {
				if(node == null) {
					continue;
				}
				if(node instanceof BracketsElement) {
					BracketsElement<?> element = (BracketsElement<?>)node;
					String spec = StringUtils.isBlank(element.getSpec())
							? element.getRootSpec() : element.getSpec();
					String name = StringUtils.isBlank(element.getName())
							? element.getRootName() : element.getName();
					String type = element.getType() == null ? "" : StringUtils.capitalize(element.getType());
					String open = element.getOpen() == null ? "" : "Open";
					te.appendParsedTemplate(cache, spec + "/" + name + type + open + "Element",
						SimpleJsonBuilder.toJson(element), indent, isForced);
				} else {
					String spec = StringUtils.isBlank(node.getSpec())
							? node.getRootSpec() : node.getSpec();
					te.appendParsedTemplate(cache, spec + "/" + node.getRootName() + "Node",
						SimpleJsonBuilder.toJson(node), indent, isForced);
				}
			}
			return true;
		}
	}

	public static class Builder<T extends BracketsNode<N>, N extends BracketsNode<N>> {

		private String spec;
		private String text;
		private String raw;
		private List<N> nodes;

		public Builder<T, N> setSpec(String spec) {
			this.spec = spec;
			return this;
		}

		public Builder<T, N> setText(String text) {
			this.text = text;
			return this;
		}

		public Builder<T, N> setRaw(String raw) {
			this.raw = raw;
			return this;
		}

		public Builder<T, N> setNodes(List<N> nodes) {
			this.nodes = nodes;
			return this;
		}

		@SuppressWarnings("unchecked")
		private T createInstance() {
			try {
				ParameterizedType gType = (ParameterizedType)getClass().getGenericSuperclass();
				Type[] aTypes = gType.getActualTypeArguments();
				Class<?> cls = (Class<?>)aTypes[0];
				return (T)cls.newInstance();
			} catch(ReflectiveOperationException roe) {
				throw new RuntimeException(roe);
			}
		}

		public T build() {
			T obj = createInstance();
			obj.setSpec(spec);
			obj.setText(text);
			obj.setRaw(raw);
			obj.setNodes(nodes);
			return obj;
		}

	}

}
