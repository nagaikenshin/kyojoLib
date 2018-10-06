package org.kyojo.plugin.markup;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kyojo.core.Cache;
import org.kyojo.core.CompleteThrowable;
import org.kyojo.core.Constants;
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
import org.kyojo.gson.JsonParseException;
import org.kyojo.gson.JsonSyntaxException;
import org.kyojo.gson.reflect.TypeToken;
import org.kyojo.minion.My;

abstract public class AbstractBracketsElement<N extends BracketsNode<N>, E extends N>
		extends AbstractBracketsNode<N> {

	private static final Log logger = LogFactory.getLog(AbstractBracketsElement.class);

	private String name = getDefaultName();

	@OutOfRequestData
	@OutOfResponseData
	public String getName() {
		return name;
	}

	@OutOfRequestData
	@OutOfResponseData
	public void setName(String name) {
		this.name = name;
	}

	abstract public String getDefaultName();

	protected String getElementName() {
		String className = getClass().getSimpleName();
		int len = className.length();
		if(className.endsWith("ElementImpl")) len -= 11;
		else if(className.endsWith("OpenElement")) len -= 11;
		else if(className.endsWith("Element")) len -= 7;
		return StringUtils.uncapitalize(className.substring(0, len));
	}

	private Map<String, String> attrs;

	public Map<String, String> getAttrs() {
		return attrs;
	}

	public void setAttrs(Map<String, String> attrs) {
		this.attrs = attrs;
	}

	protected Map<String, String> getDefaultAttrs() {
		return null;
	}

	private String type = getDefaultType();

	public String getDefaultType() {
		return null;
	}

	@OutOfRequestData
	@OutOfResponseData
	public String getType() {
		return type;
	}

	@OutOfRequestData
	@OutOfResponseData
	public void setType(String type) {
		this.type = type;
	}

	private String open = getDefaultOpen();

	abstract public String getDefaultOpen();

	@OutOfRequestData
	@OutOfResponseData
	public String getOpen() {
		return open;
	}

	@OutOfRequestData
	@OutOfResponseData
	public void setOpen(String open) {
		this.open = open;
	}

	@OutOfRequestData
	@OutOfResponseData
	public String getDefaultIndent() {
		return " ";
	}

	public Object buildCache(Cache cache, String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, String indent, boolean isForced)
			throws PluginException, RedirectThrowable, CompleteThrowable {
		cache.setExpires(getExpires() == null ? Time14.FUTURE.toString() : getExpires());
		if(attrs == null) attrs = new HashMap<>();
		Set<String> valueSet = new HashSet<>();

		extract(args, gbd, ssd, rqd, rpd, te, valueSet);

		if(attach(args, gbd, ssd, rqd, rpd, te, valueSet)) {
			cache.setExpires(Time14.OLD.toString());
		}

		parse(cache, args, gbd, ssd, rqd, rpd, te, "", isForced);

		return null;
	}

	public void extract(String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, Set<String> valueSet) throws PluginException {
		if(attrs != null && attrs.containsKey("id")) {
			extractValue(attrs.get("id"), args, gbd, ssd, rqd, rpd, te, valueSet);
		}
	}

	protected void extractValue(String iKey, String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, Set<String> valueSet) throws PluginException {
		String aid = "";
		String mwk = "";
		String mwv = "";
		try {
			aid = iKey + "." + Constants.MINION_SUFFIX;
			mwk = My.constantize(aid);
			mwv = te.convMagicWord(mwk);
			if(mwv != null && mwv.startsWith("[")) {
				Type listType = TypeToken.getParameterized(List.class, String.class).getType();
				List<String> tmpList = My.deminion(mwv, listType);
				valueSet.addAll(tmpList);
			} else {
				aid = iKey;
				mwk = My.constantize(aid);
				mwv = te.convMagicWord(mwk);
				if(mwv != null) {
					valueSet.add(mwv);
				}
			}
		} catch(JsonSyntaxException jse) {
			StringBuilder errMsg = new StringBuilder();
			errMsg.append("JsonSyntaxException: " + jse.getMessage() + "\n");
			errMsg.append("param: " + aid + "\n");
			errMsg.append("json: " + StringUtils.abbreviate(mwv, 1024) + "\n");
			throw new PluginException(this, getClass(), args,
					gbd, ssd, rqd, rpd, errMsg.toString(), jse, PluginException.Level.WARN);
		} catch(JsonParseException jpe) {
			StringBuilder errMsg = new StringBuilder();
			errMsg.append("JsonParseException: " + jpe.getMessage() + "\n");
			errMsg.append("param: " + aid + "\n");
			errMsg.append("json: " + StringUtils.abbreviate(mwv, 1024) + "\n");
			throw new PluginException(this, getClass(), args,
					gbd, ssd, rqd, rpd, errMsg.toString(), jpe, PluginException.Level.WARN);
		} catch(Exception ex) {
			StringBuilder errMsg = new StringBuilder();
			errMsg.append("Exception: " + ex.getMessage() + "\n");
			errMsg.append("param: " + aid + "\n");
			errMsg.append("json: " + StringUtils.abbreviate(mwv, 1024) + "\n");
			throw new PluginException(this, getClass(), args,
					gbd, ssd, rqd, rpd, errMsg.toString(), ex, PluginException.Level.WARN);
		}
	}

	public boolean attach(String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, Set<String> valueSet) throws PluginException {
		return attachCommon(args, gbd, ssd, rqd, rpd, te, valueSet);
	}

	public boolean attachWithNaming(String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, String suffix, Predicate<String> predicate)
			throws PluginException {
		boolean found = false;
		if(attrs != null && attrs.containsKey("id")) {
			String aid = "";
			String mwk = "";
			String mwv = "";
			try {
				aid = attrs.get("id") + suffix;
				mwk = My.constantize(aid);
				mwv = te.convMagicWord(mwk);
				if(StringUtils.isNotBlank(mwv)) {
					found = predicate.test(mwv);
				}
			} catch(JsonSyntaxException jse) {
				StringBuilder errMsg = new StringBuilder();
				errMsg.append("JsonSyntaxException: " + jse.getMessage() + "\n");
				errMsg.append("param: " + aid + "\n");
				errMsg.append("json: " + StringUtils.abbreviate(mwv, 1024) + "\n");
				throw new PluginException(this, getClass(), args,
						gbd, ssd, rqd, rpd, errMsg.toString(), jse, PluginException.Level.WARN);
			} catch(JsonParseException jpe) {
				StringBuilder errMsg = new StringBuilder();
				errMsg.append("JsonParseException: " + jpe.getMessage() + "\n");
				errMsg.append("param: " + aid + "\n");
				errMsg.append("json: " + StringUtils.abbreviate(mwv, 1024) + "\n");
				throw new PluginException(this, getClass(), args,
						gbd, ssd, rqd, rpd, errMsg.toString(), jpe, PluginException.Level.WARN);
			} catch(Exception ex) {
				StringBuilder errMsg = new StringBuilder();
				errMsg.append("Exception: " + ex.getMessage() + "\n");
				errMsg.append("param: " + aid + "\n");
				errMsg.append("json: " + StringUtils.abbreviate(mwv, 1024) + "\n");
				throw new PluginException(this, getClass(), args,
						gbd, ssd, rqd, rpd, errMsg.toString(), ex, PluginException.Level.WARN);
			}
		}

		return found;
	}

	public boolean attachCommon(String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, Set<String> valueSet)
			throws PluginException {
		return attachNodes(args, gbd, ssd, rqd, rpd, te, valueSet);
	}

	abstract public N createNode();
	abstract public E createElement();

	public boolean attachNodes(String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, Set<String> valueSet)
			throws PluginException {
		return attachWithNaming(args, gbd, ssd, rqd, rpd, te,
				"Nodes." + Constants.MINION_SUFFIX, (mnn) -> {
			Type mapType = TypeToken.getParameterized(Map.class, String.class, Object.class).getType();
			Type listType = TypeToken.getParameterized(List.class, mapType).getType();
			List<Map<String, Object>> maps = My.deminion(mnn, listType);
			if(maps != null && maps.size() > 0) {
				try {
					List<N> nodes = new ArrayList<>();
					for(Map<String, Object> map : maps) {
						if(map != null) {
							if(map.containsKey("name")) {
								E element = createElement();
								BeanUtils.copyProperties(element, map);
								nodes.add(element);
							} else {
								N node = createNode();
								BeanUtils.copyProperties(node, map);
								nodes.add(node);
							}
						}
					}
					setNodes(nodes);
				} catch(IllegalAccessException iae) {
					logger.warn(iae.getMessage(), iae);
					return false;
				} catch(InvocationTargetException ite) {
					logger.warn(ite.getMessage(), ite);
					return false;
				}

				return true;
			}

			return false;
		});
	}

	public void parse(Cache cache, String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, String indent, boolean isForced)
			throws PluginException, RedirectThrowable, CompleteThrowable {
		if(open == null && (attrs == null || attrs.size() == 0)
				&& getText() == null && getRaw() == null && !hasChildren()) {
			return;
		}

		StringBuilder sb = new StringBuilder();
		String name = this.name.toLowerCase();
		sb.append(indent);
		sb.append("<");
		sb.append(name);
		if(attrs != null && attrs.size() > 0) {
			for(Map.Entry<String, String> attr : attrs.entrySet()) {
				sb.append(" ");
				sb.append(escape(attr.getKey()));
				if(attr.getValue() != null) {
					sb.append("=\"");
					sb.append(escape(attr.getValue()));
					sb.append("\"");
				}
			}
		}
		if(getText() == null && getRaw() == null && !hasChildren()) {
			if(open == null) {
				sb.append(" />\n");
			} else {
				sb.append(">\n");
			}
			cache.addLine(sb.toString());
		} else {
			sb.append(">");

			if(hasChildren()) {
				sb.append("\n");
				cache.addLine(sb.toString());
				parseChildren(cache, args, gbd, ssd, rqd, rpd, te, indent + getDefaultIndent(), isForced);
				sb = new StringBuilder();
				sb.append(indent);
			}
			if(getRaw() != null) {
				sb.append(getRaw());
			}
			if(getText() != null) {
				sb.append(escape(getText()));
			}
			if(open == null) {
				sb.append("</");
				sb.append(name);
				sb.append(">\n");
				cache.addLine(sb.toString());
			} else if(!hasChildren()) {
				sb.append("\n");
				cache.addLine(sb.toString());
			}
		}
	}

	public static class Builder<T extends BracketsElement<N>, N extends BracketsNode<N>>
			extends AbstractBracketsNode.Builder<T, N> {

		private String name;
		private Map<String, String> attrs;
		private String type;
		private String open;

		public Builder<T, N> setName(String name) {
			this.name = name;
			return this;
		}

		public Builder<T, N> setAttrs(Map<String, String> attrs) {
			this.attrs = attrs;
			return this;
		}

		public Builder<T, N> setType(String type) {
			this.type = type;
			return this;
		}

		public Builder<T, N> setOpen(String open) {
			this.open = open;
			return this;
		}

		@Override
		public T build() {
			T obj = super.build();
			obj.setName(name);
			obj.setAttrs(attrs);
			obj.setType(type);
			obj.setOpen(open);
			return obj;
		}

	}

}
