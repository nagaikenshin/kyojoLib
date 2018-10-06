package org.kyojo.plugin.html5

import java.lang.reflect.Type

import org.apache.commons.beanutils.BeanUtils
import org.apache.commons.lang3.StringUtils
import org.apache.commons.text.StringEscapeUtils
import org.kyojo.core.Cache
import org.kyojo.core.CompleteThrowable
import org.kyojo.core.Constants
import org.kyojo.core.GlobalData
import org.kyojo.core.PluginException
import org.kyojo.core.RedirectThrowable
import org.kyojo.core.RequestData
import org.kyojo.core.ResponseData
import org.kyojo.core.SessionData
import org.kyojo.core.TemplateEngine
import org.kyojo.core.Time14
import org.kyojo.core.PluginException.Level
import org.kyojo.gson.JsonParseException
import org.kyojo.gson.JsonSyntaxException
import org.kyojo.gson.reflect.TypeToken
import org.kyojo.minion.My
import org.kyojo.schemaorg.SimpleJsonBuilder

class SelectElement extends FormElement {

	List<OptGroupElement> optGroups
	List<OptionElement> options

	SelectElement() {}

	SelectElement(List<OptGroupElement> optGroups, List<OptionElement> options) {
		this.optGroups = optGroups
		this.options = options
	}

	@Override
	protected String getDefaultExpires() {
		return Time14.OLD.toString()
	}

	void extract(String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, Set<String> valueSet) throws PluginException {
		String iKey = extractFormID(args, gbd, ssd, rqd, rpd, te, valueSet)

		if(iKey != null) {
			extractValue(iKey, args, gbd, ssd, rqd, rpd, te, valueSet)
		}

		if(options != null) {
			selectOptions(options, valueSet)
		}
		if(optGroups != null) {
			selectOptGroups(optGroups, valueSet)
		}
		if(nodes != null) {
			selectNodes(nodes, valueSet)
		}

		composeClass()
	}

	@Override
	boolean attach(String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, Set<String> valueSet) throws PluginException {
		attachHtmlCommon(args, gbd, ssd, rqd, rpd, te, valueSet)
		attachOptGroups(args, gbd, ssd, rqd, rpd, te, valueSet)
		attachOptions(args, gbd, ssd, rqd, rpd, te, valueSet)
	}

	static void selectOptions(List<OptionElement> options, Set<String> valueSet) {
		options.each { option ->
			if(option.attrs == null) {
				option.attrs = [:]
			}
			if(valueSet != null
				&& ((option.attrs.containsKey("value") && option.attrs.value != null
						&& valueSet.contains(option.attrs.value))
					|| (!option.attrs.containsKey("value")
						&& option.text != null && valueSet.contains(option.text)))) {
				option.attrs.selected = "selected"
			} else {
				option.attrs.remove("selected")
			}
		}
	}

	boolean attachOptions(String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, Set<String> valueSet) {
		return attachWithNaming(args, gbd, ssd, rqd, rpd, te,
				"Options." + Constants.MINION_SUFFIX, { mnn ->
			Type listType = TypeToken.getParameterized(List.class, OptionElement.class).getType()
			List<OptionElement> options = My.deminion(mnn, listType)
			if(options != null) {
				selectOptions(options, valueSet)
				this.options = options

				return true
			}

			return false
		})
	}

	static void selectOptGroups(List<OptGroupElement> optGroups, Set<String> valueSet) {
		optGroups.each { optGroup ->
			if(optGroup.options != null) {
				optGroup.options.each { option ->
					if(option.attrs == null) {
						option.attrs = [:]
					}
					if(valueSet != null
						&& ((option.attrs.containsKey("value") && option.attrs.value != null
								&& valueSet.contains(option.attrs.value))
							|| (!option.attrs.containsKey("value")
								&& option.text != null && valueSet.contains(option.text)))) {
						option.attrs.selected = "selected"
					} else {
						option.attrs.remove("selected")
					}
				}
			}
		}
	}

	boolean attachOptGroups(String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, Set<String> valueSet) {
		return attachWithNaming(args, gbd, ssd, rqd, rpd, te,
				"OptGroups." + Constants.MINION_SUFFIX, { mnn ->
			Type listType = TypeToken.getParameterized(List.class, OptGroupElement.class).getType()
			List<OptGroupElement> optGroups = My.deminion(mnn, listType)
			if(optGroups != null) {
				selectOptGroups(optGroups, valueSet)
				this.optGroups = optGroups

				return true
			}

			return false
		})
	}

	static void selectNodes(List<HtmlNode> nodes, Set<String> valueSet) {
		nodes.each { node ->
			if(node instanceof HtmlElement) {
				HtmlElement elem = (HtmlElement)node
				if(elem.name == "option") {
					if(elem.attrs == null) {
						elem.attrs = [:]
					}
					if(valueSet != null
						&& ((elem.attrs.containsKey("value") && elem.attrs.value != null
								&& valueSet.contains(elem.attrs.value))
							|| (!elem.attrs.containsKey("value")
								&& elem.text != null && valueSet.contains(elem.text)))) {
						elem.attrs.selected = "selected"
					} else {
						elem.attrs.remove("selected")
					}
				} else if(elem.name == "optGroup") {
					List<HtmlNode> nodes2 = elem.nodes
					nodes2.each { node2 ->
						if(node2 instanceof HtmlElement) {
							HtmlElement elem2 = (HtmlElement)node2
							if(elem2.name == "option") {
								if(elem2.attrs == null) {
									elem2.attrs = [:]
								}
								if(valueSet != null
									&& ((elem2.attrs.containsKey("value") && elem2.attrs.value != null
											&& valueSet.contains(elem2.attrs.value))
										|| (!elem2.attrs.containsKey("value")
											&& elem2.text != null && valueSet.contains(elem2.text)))) {
									elem2.attrs.selected = "selected"
								} else {
									elem2.attrs.remove("selected")
								}
							}
						}
					}
				}
			}
		}
	}

	@Override
	boolean hasChildren() {
		optGroups != null || options != null || super.hasChildren()
	}

	@Override
	boolean parseChildren(Cache cache, String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, String indent, boolean isForced)
			throws PluginException, RedirectThrowable, CompleteThrowable {
		parseOptGroups(cache, args, gbd, ssd, rqd, rpd, te, indent, isForced) ||
			parseOptions(cache, args, gbd, ssd, rqd, rpd, te, indent, isForced) ||
			super.parseChildren(cache, args, gbd, ssd, rqd, rpd, te, indent, isForced)
	}

	boolean parseOptGroups(Cache cache, String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, String indent, boolean isForced)
			throws PluginException, RedirectThrowable, CompleteThrowable {
		if(optGroups == null) {
			return false
		} else {
			optGroups.each { optGroup ->
				optGroup.parse(cache, null, gbd, ssd, rqd, rpd, te, indent, isForced)
			}
			return true
		}
	}

	boolean parseOptions(Cache cache, String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, String indent, boolean isForced)
			throws PluginException, RedirectThrowable, CompleteThrowable {
		if(options == null) {
			return false
		} else {
			options.each { option ->
				option.parse(cache, null, gbd, ssd, rqd, rpd, te, indent, isForced)
			}
			return true
		}
	}

	public static class Builder extends org.kyojo.plugin.html5.HtmlElementImpl.Builder<SelectElement> {

		private List<OptGroupElement> optGroups
		private List<OptionElement> options

		Builder setOptGroups(List<OptGroupElement> optGroups) {
			this.optGroups = optGroups
			return this
		}

		Builder setOptions(List<OptionElement> options) {
			this.options = options
			return this
		}

		@Override
		SelectElement build() {
			SelectElement obj = super.build()
			obj.optGroups = optGroups
			obj.options = options
			return obj
		}

	}

}
