package org.kyojo.plugin.mz1Lab.formsLab

import org.kyojo.core.CompleteThrowable
import org.kyojo.core.GlobalData
import org.kyojo.core.PluginException
import org.kyojo.core.RedirectThrowable
import org.kyojo.core.RequestData
import org.kyojo.core.ResponseData
import org.kyojo.core.SessionData
import org.kyojo.core.annotation.OutOfRequestData

class CheckboxesLab {

	@OutOfRequestData
	String title
	String blankCheckbox
	String defaultCheckbox
	String filledInCheckbox
	String indeterminateCheckbox
	String disabledCheckbox
	String disabledBlankCheckbox

	Object initialize(String args, GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd)
			throws PluginException, RedirectThrowable, CompleteThrowable {
		title = "Forms Checkboxes"

		defaultCheckbox = "1"
		filledInCheckbox = "1"
		disabledCheckbox = "1"

		return null
	}

}
