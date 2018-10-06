package org.kyojo.core.io;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.kyojo.core.GlobalData;
import org.kyojo.core.SessionData;

public interface IOLayer {

	public void initialize(GlobalData gbd) throws IOException, Exception;

	public CacheIO acquireCacheIO(String key);

	public void deleteCache(String key);

	public boolean canReadOfFileSystem(String path);

	public long lastModifiedOfFileSystem(String path);

	public String getSkinDPath(SessionData ssd);

	public String getPluginDPath();

	public String getPluginPkgs(SessionData ssd);

	public FileIO acquireFileIO(String key);

	public void deleteFile(String key);

	public HttpSession getSession(boolean create, HttpServletRequest req);

}
