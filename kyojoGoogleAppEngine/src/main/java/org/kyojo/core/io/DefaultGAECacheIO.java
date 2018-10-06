package org.kyojo.core.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.cache.Cache;

public class DefaultGAECacheIO implements CacheIO {

	private String key;
	private byte[] value;
	private Cache cache;
	private InputStream is = null;
	private OutputStream os = null;
	private ByteArrayOutputStream baos = null;

	public DefaultGAECacheIO(String key, byte[] value, Cache cache, DefaultGAEIOLayer ioLayer) {
		this.key = key;
		this.value = value;
		this.cache = cache;
	}

	public InputStream getInputStream() {
		if(value == null) {
			return null;
		}

		if(is == null) {
			is = new ByteArrayInputStream(value);
		}
		return is;
	}

	public OutputStream getOutputStream() {
		if(os == null) {
			os = baos = new ByteArrayOutputStream();
		}
		return os;
	}

	@SuppressWarnings("unchecked")
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

			value = baos.toByteArray();
			if(value.length <= 1000000) {
				cache.put(key, value);
			}
		}
	}

}
