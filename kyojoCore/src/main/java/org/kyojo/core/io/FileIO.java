package org.kyojo.core.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface FileIO {

	public InputStream getInputStream();

	public OutputStream getOutputStream();

	public void release() throws IOException;

}
