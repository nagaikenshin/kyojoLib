package org.kyojo.core.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.Channels;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.appengine.tools.cloudstorage.GcsFileOptions;
import com.google.appengine.tools.cloudstorage.GcsFilename;
import com.google.appengine.tools.cloudstorage.GcsInputChannel;
import com.google.appengine.tools.cloudstorage.GcsOutputChannel;
import com.google.appengine.tools.cloudstorage.GcsService;

public class DefaultGAEFileIO implements FileIO {

	private static final Log logger = LogFactory.getLog(DefaultGAEFileIO.class);

	private String key;
	private GcsService gcsService;
	private String bucketName;
	private InputStream is = null;
	private OutputStream os = null;

	private static final int BUFFER_SIZE = 2 * 1024 * 1024;

	public DefaultGAEFileIO(String key, GcsService gcsService, String bucketName, DefaultIOLayer ioLayer) {
		this.key = key;
		this.gcsService = gcsService;
		this.bucketName = bucketName;
	}

	public InputStream getInputStream() {
		try {
			if(is == null) {
				GcsFilename gcsFilename = new GcsFilename(bucketName, key);
				GcsInputChannel readChannel = gcsService.openPrefetchingReadChannel(gcsFilename, 0, BUFFER_SIZE);
				is = Channels.newInputStream(readChannel);
			}
			return is;
		} catch(Exception ex) {
			logger.warn(ex.getMessage(), ex);
			return null;
		}
	}

	public OutputStream getOutputStream() {
		try {
			if(os == null) {
				GcsFileOptions instance = GcsFileOptions.getDefaultInstance();
				GcsFilename gcsFilename = new GcsFilename(bucketName, key);
				GcsOutputChannel outputChannel = gcsService.createOrReplace(gcsFilename, instance);
				os = Channels.newOutputStream(outputChannel);
			}
			return os;
		} catch(IOException ioe) {
			return null;
		}
	}

	public void release() throws IOException {
		if(is != null) {
			try {
				is.close();
			} catch(Exception ex) {}
			is = null;
		}
		if(os != null) {
			try {
				os.close();
			} catch(Exception ex) {}
			os = null;
		}
	}

}
