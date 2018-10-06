package org.kyojo.core;

import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.File;
import java.io.InputStream;
import java.io.FileInputStream;

public class JavaFGets {

	private InputStream is = null;
	private int idx = 0;
	private int len = 0;
	private String str = null;
	private StringBuilder sb = new StringBuilder();
	private byte[] buf = new byte[256];
	private boolean afterCR = false;

	public JavaFGets(File f) throws FileNotFoundException {
		is = new FileInputStream(f);
	}

	public JavaFGets(String fpath) throws FileNotFoundException {
		is = new FileInputStream(fpath);
	}

	public JavaFGets(InputStream is) {
		this.is = is;
	}

	public InputStream getInputStream() {
		return is;
	}

	private String procLine(char ch, boolean afterCR) {
		String line = sb.toString();
		sb = new StringBuilder();
		sb.append(ch);
		this.afterCR = afterCR;
		return line;
	}

	public String readLine() throws IOException {
		if(len < 0) {
			return null;
		}

		char ch;
		while(true) {
			if(idx >= len) {
				len = is.read(buf);
				idx = 0;
				if(len < 0) {
					if(sb.length() > 0) {
						return sb.toString();
					} else {
						return null;
					}
				} else {
					str = new String(buf, 0, len, "ISO-8859-1");
				}
			}

			if(len == 0) {
				return "";
			}

			ch = str.charAt(idx);
			idx++;
			if(ch == '\r') {
				if(afterCR) {
					return procLine('\r', true);
				}
				sb.append('\r');
				afterCR = true;
			} else if(ch == '\n') {
				sb.append('\n');
				String line = sb.toString();
				sb = new StringBuilder();
				afterCR = false;
				return line;
			} else {
				if(afterCR) {
					return procLine(ch, false);
				}
				sb.append(ch);
				afterCR = false;
			}
		}
	}

	public void close() throws IOException {
		len = -1;
		is.close();
	}

}
