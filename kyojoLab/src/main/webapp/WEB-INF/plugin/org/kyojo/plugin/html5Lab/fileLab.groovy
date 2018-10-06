package org.kyojo.plugin.html5Lab

import java.time.OffsetDateTime
import java.util.regex.Matcher
import java.util.regex.Pattern
import javax.mail.internet.MimeUtility
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import org.apache.commons.fileupload.FileItem
import org.apache.commons.io.IOUtils
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.kyojo.core.Cache
import org.kyojo.core.CompleteThrowable
import org.kyojo.core.GlobalData
import org.kyojo.core.PluginException
import org.kyojo.core.RedirectThrowable
import org.kyojo.core.RequestData
import org.kyojo.core.ResponseData
import org.kyojo.core.SessionData
import org.kyojo.core.TemplateEngine
import org.kyojo.core.annotation.InheritDefault
import org.kyojo.core.annotation.OutOfRequestData
import org.kyojo.core.io.FileIO
import org.kyojo.core.io.IOLayer
import org.kyojo.plugin.html5Lab.fileLab.UploadedInfo

class FileLab {

	private static final Log logger = LogFactory.getLog(FileLab.class)

	transient FileItem imgFile
	transient List<FileItem> mltFiles
	@OutOfRequestData
	List<UploadedInfo> uploadedInfoList
	@OutOfRequestData
	String uploadMsg
	@InheritDefault
	transient String path

	Object initialize(String args, GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd)
			throws PluginException, RedirectThrowable, CompleteThrowable {
		uploadedInfoList = []
		uploadMsg = "Select files, and press the upload button."
		return null
	}

	Object doUpload(Cache cache, String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, String indent, boolean isForced)
			throws PluginException, RedirectThrowable, CompleteThrowable {
		if(imgFile != null && imgFile.size > 0) {
			addUploadedFileToList(imgFile, gbd)
		}

		if(mltFiles != null && mltFiles.size() > 0) {
			mltFiles.each { mltFile ->
				if(mltFile != null && mltFile.size > 0) {
					addUploadedFileToList(mltFile, gbd)
				}
			}
		}

		return null
	}

	private void addUploadedFileToList(FileItem fileItem, GlobalData gbd) {
		def uploadedInfo = new UploadedInfo()
		uploadedInfo.fileName = fileItem.name
		uploadedInfo.fileSize = fileItem.size
		uploadedInfo.uploadedDateTime = OffsetDateTime.now()
		uploadedInfo.storedKey = "html5Lab/fileLab/" +
			uploadedInfo.uploadedDateTime.toEpochSecond() + uploadedInfo.uploadedDateTime.getNano() +
			String.format("%04d", (int)(Math.random() * 10000))

		IOLayer ioLayer = gbd.get(IOLayer.class)
		FileIO fileIO = null
		try {
			fileIO = ioLayer.acquireFileIO(uploadedInfo.storedKey)
			IOUtils.copy(fileItem.inputStream, fileIO.outputStream)
		} catch(IOException ioe) {
			logger.warn(ioe.getMessage(), ioe)
		} finally {
			if(fileIO != null) {
				try {
					fileIO.release()
				} catch(IOException ioe) {}
			}
		}

		if(uploadedInfoList == null) {
			uploadedInfoList = []
		}
		uploadedInfoList.add(uploadedInfo)
	}

	Object doDownload(Cache cache, String args,
			GlobalData gbd, SessionData ssd, RequestData rqd, ResponseData rpd,
			TemplateEngine te, String indent, boolean isForced)
			throws PluginException, RedirectThrowable, CompleteThrowable {
		int listNo = 0;
		if(path != null) {
			try {
				Pattern pt = Pattern.compile("/(\\d+)");
				Matcher mc = pt.matcher(path);
				if(mc.matches()) {
					listNo = Integer.parseInt(mc.group(1))
				}
			} catch(Exception ex) {}
		}

		if(listNo > 0) {
			int listIdx = listNo - 1
			if(listIdx < uploadedInfoList.size()) {
				UploadedInfo uploadedInfo = uploadedInfoList[listIdx]
				IOLayer ioLayer = gbd.get(IOLayer.class)
				FileIO fileIO = null
				try {
					fileIO = ioLayer.acquireFileIO(uploadedInfo.storedKey)
					downloadFile(fileIO.inputStream, uploadedInfo.fileName,
						"application/octet-stream", rqd.getRequest(), rpd.getResponse())
				} catch(IOException ioe) {
					logger.warn(ioe.getMessage(), ioe)
				} finally {
					if(fileIO != null) {
						try {
							fileIO.release()
						} catch(IOException ioe) {}
					}
				}
			}
		}

		return null
	}

	static String encodeDownloadFileName(String orgFileName, HttpServletRequest request)
			throws UnsupportedEncodingException {
		String encFileName;

		if(request.getHeader("User-Agent").indexOf("Safari") > -1) {
			// Safariの場合
			encFileName = new String(orgFileName.getBytes("UTF-8"), "8859_1");
		} else if(request.getHeader("User-Agent").indexOf("MSIE") > -1
				|| request.getHeader("User-Agent").toLowerCase().indexOf("trident") > -1) {
			// IEの場合
			encFileName = URLEncoder.encode(orgFileName, "UTF-8");
		} else {
			// それ以外の場合（Firefox,Google Chromeで動作確認済み）
			encFileName = MimeUtility.encodeWord(orgFileName, "ISO-2022-JP", "B");
		}

		return encFileName;
	}

	static boolean downloadFile(InputStream is, String fileName, String contentType,
			HttpServletRequest request, HttpServletResponse response) throws IOException {
		boolean ret = true;
		final String encFileName = encodeDownloadFileName(fileName, request);

		OutputStream os = response.getOutputStream();
		try {
			// ファイルのダウンロード処理を行う
			response.setContentType(contentType);
			response.setHeader("Content-Disposition", "attachment;filename=\"" + encFileName + "\"");
			int data;
			byte[] b = new byte[1024];
			while((data = is.read(b, 0, 1024)) != -1) {
				os.write(b, 0, data);
			}
		} catch(final Exception ex) {
			logger.warn(ex.getMessage(), ex);
			ret = false;
		} finally {
			os.close();
		}

		return ret;
	}

}
