package org.kyojo.core.io;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.locks.Lock;

public class DefaultFileIO implements FileIO {

	private String ioFPath;
	private Lock lock;
	private DefaultIOLayer ioLayer;
	private InputStream is = null;
	private OutputStream os = null;

	public DefaultFileIO(String ioFPath, Lock lock, DefaultIOLayer ioLayer) {
		this.ioFPath = ioFPath;
		this.lock = lock;
		this.ioLayer = ioLayer;
	}

	public InputStream getInputStream() {
		try {
			if(is == null) {
				is = new BufferedInputStream(new FileInputStream(new File(ioFPath)));
			}
			return is;
		} catch(IOException ioe) {
			return null;
		}
	}

	public OutputStream getOutputStream() {
		try {
			if(os == null) {
				File f = new File(ioFPath);
				File d = f.getParentFile();
				if(!d.exists()) {
					d.mkdirs();
				}
				os = new BufferedOutputStream(new FileOutputStream(new File(ioFPath)));
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
		ioLayer.unlockFileSystemCache(ioFPath, lock);
	}

}
