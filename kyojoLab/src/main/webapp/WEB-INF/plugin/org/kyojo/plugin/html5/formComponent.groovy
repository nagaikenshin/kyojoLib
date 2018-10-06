package org.kyojo.plugin.html5

import org.apache.commons.lang3.StringUtils

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
import org.kyojo.minion.My

abstract class FormComponent {

	String layoutClass
	HtmlElement compo
	HtmlElement label
	Map<String, String> attrs
	String defaultClass
	String addClass
	HtmlElement form
	HtmlElement desc

	abstract protected HtmlElement createFormElement()

	void giveFormDefaults(String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te) throws PluginException {
		form = createFormElement()

		if(form.attrs == null || form.attrs.size() == 0) {
			form.attrs = attrs
		}
		if(StringUtils.isNotBlank(defaultClass)) {
			form.defaultClass = defaultClass
		}
		if(StringUtils.isNotBlank(addClass)) {
			form.addClass = addClass
		}
	}

	void giveLabelDefaults(String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te) throws PluginException {
		if(label != null) {
			if(StringUtils.isBlank(label.name) || label.name == label.rootName) {
				label.name = "label"
			}
		}

		if(attrs != null && attrs.containsKey("id") && StringUtils.isNotBlank(attrs.id)) {
			if(label != null && StringUtils.isNotBlank(label.text)) {
				if(label.attrs == null) {
					label.attrs = [:]
				}
				label.attrs.for = attrs.id
			}
		}
	}

	void giveDescDefaults(String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te) throws PluginException {
		if(form.attrs != null && form.attrs.containsKey("id")) {
			String aid = form.attrs.id + Constants.VLD_MSG_KEY_SUFFIX
			String mwk = My.constantize(aid)
			String mwv = te.convMagicWord(mwk)
			if(StringUtils.isNotBlank(mwv)) {
				desc = new SmallElement()
				desc.defaultClass = "vldMsg"
				desc.text = mwv
			}
		}

		if(desc != null) {
			if(StringUtils.isBlank(desc.name) || desc.name == desc.rootName) {
				desc.name = "small"
			}
		}
	}

	protected HtmlElement createCompoElement() {
		return new DivElement()
	}

	void giveCompoDefaults(String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te) throws PluginException {
		String name
		if(compo == null) {
			compo = createCompoElement()
			name = compo.name
		} else {
			name = createCompoElement().name
		}

		if(StringUtils.isBlank(compo.defaultClass)) {
			compo.defaultClass = StringUtils.uncapitalize(getClass().getSimpleName())
		}
		if(StringUtils.isBlank(compo.addClass) && StringUtils.isNotBlank(layoutClass)) {
			compo.addClass = layoutClass
		}
		compo.composeClass()

		if(attrs != null && attrs.containsKey("id") && StringUtils.isNotBlank(attrs.id)) {
			if(compo.attrs == null) {
				compo.attrs = [:]
			}
			if(!compo.attrs.containsKey("id") || StringUtils.isBlank(compo.attrs.id)) {
				compo.attrs.id = attrs.id + StringUtils.capitalize(name)
			}
		}
	}

	Object buildCache(Cache cache, String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, String indent, boolean isForced)
			throws PluginException, RedirectThrowable, CompleteThrowable {
		giveFormDefaults(args, gbd, ssd, rqd, rpd, te)
		giveLabelDefaults(args, gbd, ssd, rqd, rpd, te)
		giveDescDefaults(args, gbd, ssd, rqd, rpd, te)
		giveCompoDefaults(args, gbd, ssd, rqd, rpd, te)

		return null
	}

}
