package org.kyojo.plugin.html5

import org.apache.commons.lang3.StringUtils
import org.kyojo.core.Cache
import org.kyojo.core.GlobalData
import org.kyojo.core.PluginException
import org.kyojo.core.RequestData
import org.kyojo.core.ResponseData
import org.kyojo.core.SessionData
import org.kyojo.core.TemplateEngine
import org.kyojo.core.Time14

abstract class InputGroupElement extends InputElement {

	Boolean checked

	@Override
	protected String getDefaultExpires() {
		return Time14.OLD.toString()
	}

	void extract(String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, Set<String> valueSet) throws PluginException {
		String iKey = null
		String nKey = null
		String vKey = null
		if(attrs.containsKey("value")) {
			String[] elms = attrs.value.split("\\.")
			StringBuilder sb = new StringBuilder(elms[0])
			for(int ei = 1; ei < elms.length - 1; ei++) {
				sb.append(".")
				sb.append(elms[ei])
			}
			vKey = sb.toString()
			attrs.value = elms[elms.length - 1]
		}
		if(attrs.containsKey("id")) {
			iKey = attrs.id
			String[] elms = iKey.split("\\.")
			StringBuilder sb = new StringBuilder(elms[0])
			for(int ei = 1; ei < elms.length - 1; ei++) {
				sb.append(".")
				sb.append(elms[ei])
			}
			nKey = sb.toString()
			if(attrs.containsKey("name")) {
				nKey = attrs.name
			} else {
				attrs.name = nKey
			}
			if(!attrs.containsKey("value")) {
				elms = iKey.split("\\.")
				if(elms.length > 1) {
					sb = new StringBuilder(elms[0])
					for(int ei = 1; ei < elms.length - 1; ei++) {
						sb.append(".")
						sb.append(elms[ei])
					}
					vKey = sb.toString()
					attrs.value = elms[elms.length - 1]
				} else {
					vKey = iKey
					attrs.value = "1"
				}
			}
		}
		if(attrs.containsKey("disabled")) {
			if(StringUtils.isNotBlank(attrs.disabled) && attrs.disabled != "disabled") {
				iKey = attrs.disabled
				String[] elms = attrs.get("disabled").split("\\.")
				StringBuilder sb = new StringBuilder(elms[0])
				for(int ei = 1; ei < elms.length - 1; ei++) {
					sb.append(".")
					sb.append(elms[ei])
				}
				nKey = sb.toString()
				if(attrs.containsKey("id")) {
					iKey = attrs.id
				} else {
					attrs.id = "_" + iKey
				}
				if(attrs.containsKey("name")) {
					nKey = attrs.name
				} else {
					attrs.name = "_" + nKey
				}
				if(!attrs.containsKey("value")) {
					elms = iKey.split("\\.")
					if(elms.length > 1) {
						sb = new StringBuilder(elms[0])
						for(int ei = 1; ei < elms.length - 1; ei++) {
							sb.append(".")
							sb.append(elms[ei])
						}
						vKey = sb.toString()
						attrs.value = elms[elms.length - 1]
					} else {
						vKey = iKey
						attrs.value = "1"
					}
				}
			}
			attrs.disabled = "disabled"
		}
		if(attrs.containsKey("readonly")) {
			if(StringUtils.isNotBlank(attrs.readonly) && attrs.readonly != "readonly") {
				iKey = attrs.readonly
				String[] elms = attrs.get("readonly").split("\\.")
				StringBuilder sb = new StringBuilder(elms[0])
				for(int ei = 1; ei < elms.length - 1; ei++) {
					sb.append(".")
					sb.append(elms[ei])
				}
				nKey = sb.toString()
				if(attrs.containsKey("id")) {
					iKey = attrs.id
				} else {
					attrs.id = "_" + iKey
				}
				if(attrs.containsKey("name")) {
					nKey = attrs.name
				} else {
					attrs.name = "_" + nKey
				}
				if(!attrs.containsKey("value")) {
					elms = iKey.split("\\.")
					if(elms.length > 1) {
						sb = new StringBuilder(elms[0])
						for(int ei = 1; ei < elms.length - 1; ei++) {
							sb.append(".")
							sb.append(elms[ei])
						}
						vKey = sb.toString()
						attrs.value = elms[elms.length - 1]
					} else {
						vKey = iKey
						attrs.value = "1"
					}
				}
			}
			attrs.readonly = "readonly"
		}

		if(!attrs.containsKey("type")) {
			attrs.type = this.type
		}

		if(vKey != null) {
			extractValue(vKey, args, gbd, ssd, rqd, rpd, te, valueSet)
		}

		if(checked == null) {
			if(attrs.containsKey("value") && attrs.value != null
					&& valueSet != null && valueSet.contains(attrs.value)) {
				attrs.checked = "checked"
			} else {
				attrs.remove("checked")
			}
			checked = attrs.containsKey("checked")
		} else {
			if(checked) {
				attrs.checked = "checked"
			} else {
				attrs.remove("checked")
			}
		}

		composeClass()
	}

}
