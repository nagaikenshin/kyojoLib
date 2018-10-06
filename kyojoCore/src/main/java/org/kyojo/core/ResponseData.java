package org.kyojo.core;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

public class ResponseData implements Map<String, ParamSidePack> {

	private HttpServletResponse response;

	private Map<String, ParamSidePack> map = new HashMap<>();

	public ResponseData(HttpServletResponse response) {
		this.response = response;
	}

	public HttpServletResponse getResponse() {
		return response;
	}

	@Override
	public void clear() {
		map.clear();
	}

	@Override
	public boolean containsKey(Object key) {
		return map.containsKey(key);
	}

	@Override
	public boolean containsValue(Object val) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Set<Entry<String, ParamSidePack>> entrySet() {
		return map.entrySet();
	}

	@Override
	public ParamSidePack get(Object key) {
		return map.get(key);
	}

	@Override
	public boolean isEmpty() {
		return map.isEmpty();
	}

	@Override
	public Set<String> keySet() {
		return map.keySet();
	}

	@Override
	public ParamSidePack put(String key, ParamSidePack val) {
		return map.put(key, val);
	}

	@Override
	public void putAll(Map<? extends String, ? extends ParamSidePack> map) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ParamSidePack remove(Object key) {
		return map.remove(key);
	}

	@Override
	public int size() {
		return map.size();
	}

	@Override
	public Collection<ParamSidePack> values() {
		return map.values();
	}

	public boolean hasValidationMessage() {
		for(ParamSidePack psp : map.values()) {
			if(ParamSidePack.hasValidationMessage(psp)) {
				return true;
			}
		}

		return false;
	}

}
