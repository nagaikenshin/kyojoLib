package org.kyojo.plugin.html5

import java.util.List

import org.kyojo.core.annotation.OutOfRequestData
import org.kyojo.core.annotation.OutOfResponseData
import org.kyojo.plugin.html5.TableElement.Builder

class TableOpenElement extends TableElement {

	@OutOfRequestData
	@OutOfResponseData
	public String getOpen() {
		getDefaultOpen()
	}

	@OutOfRequestData
	@OutOfResponseData
	public void setOpen(String open) {
	}

	@Override
	public String getDefaultOpen() {
		"open"
	}

	public static class Builder extends org.kyojo.plugin.html5.HtmlElementImpl.Builder<TableOpenElement> {

		private CaptionElement caption
		private List<ColGroupElement> colGroups
		private THeadElement tHead
		private TBodyElement tBody
		private TFootElement tFoot
		private List<TrElement> trs

		Builder setCaption(CaptionElement caption) {
			this.caption = caption
			return this
		}

		Builder setColGroups(List<ColGroupElement> colGroups) {
			this.colGroups = colGroups
			return this
		}

		Builder setTHead(THeadElement tHead) {
			this.tHead = tHead
			return this
		}

		Builder setTBody(TBodyElement tBody) {
			this.tBody = tBody
			return this
		}

		Builder setTFoot(TFootElement tFoot) {
			this.tFoot = tFoot
			return this
		}

		Builder setTrs(List<TrElement> trs) {
			this.trs = trs
			return this
		}

		@Override
		TableOpenElement build() {
			TableOpenElement obj = super.build()
			obj.caption = caption
			obj.colGroups = colGroups
			obj.tHead = tHead
			obj.tBody = tBody
			obj.tFoot = tFoot
			obj.trs = trs
			return obj
		}

	}

}
