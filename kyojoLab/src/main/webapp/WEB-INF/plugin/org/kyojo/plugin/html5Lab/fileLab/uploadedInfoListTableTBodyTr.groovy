package org.kyojo.plugin.html5Lab.fileLab

import java.time.OffsetDateTime
import org.kyojo.core.annotation.ArgsListNo

class UploadedInfoListTableTBodyTr {

	@ArgsListNo
	int listNo
	String fileName
	Integer fileSize
	OffsetDateTime uploadedDateTime
	String storedKey

}
