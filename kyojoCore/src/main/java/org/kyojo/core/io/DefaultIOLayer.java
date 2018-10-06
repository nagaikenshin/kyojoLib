package org.kyojo.core.io;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.kyojo.core.GlobalData;
import org.kyojo.core.SessionData;

public class DefaultIOLayer implements IOLayer {

	private GlobalData gbd = null;

	@Override
	public void initialize(GlobalData gbd) throws IOException, Exception {
		this.gbd = gbd;
	}

	protected HashMap<String, ReentrantReadWriteLock> fileSystemCacheLockTable = new HashMap<>();

	protected Lock lockFileSystemCache(String path, boolean isRead) {
		Lock lock;
		synchronized(fileSystemCacheLockTable) {
			ReentrantReadWriteLock readWriteLock;
			if(fileSystemCacheLockTable.containsKey(path)) {
				readWriteLock = fileSystemCacheLockTable.get(path);
			} else {
				readWriteLock = new ReentrantReadWriteLock();
				fileSystemCacheLockTable.put(path, readWriteLock);
			}

			lock = isRead ? readWriteLock.readLock() : readWriteLock.writeLock();
			lock.lock();
		}

		return lock;
	}

	protected void unlockFileSystemCache(String path, Lock lock) {
		lock.unlock();
		synchronized(fileSystemCacheLockTable) {
			ReentrantReadWriteLock readWriteLock = fileSystemCacheLockTable.get(path);
			if(readWriteLock != null && !readWriteLock.hasQueuedThreads()) {
				fileSystemCacheLockTable.remove(path);
			}
		}
	}

	@Override
	public CacheIO acquireCacheIO(String key) {
		String ioFPath = gbd.get("CACHE_DPATH") + key;
		Lock lock = lockFileSystemCache(ioFPath, false);
		return new DefaultCacheIO(ioFPath, lock, this);
	}

	@Override
	public void deleteCache(String key) {
		String ioFPath = gbd.get("CACHE_DPATH") + key;
		Lock lock = lockFileSystemCache(ioFPath, false);
		File f = new File(ioFPath);
		f.delete();
		unlockFileSystemCache(ioFPath, lock);
	}

	@Override
	public boolean canReadOfFileSystem(String path) {
		File f = new File(path);
		return f.canRead();
	}

	@Override
	public long lastModifiedOfFileSystem(String path) {
		File f = new File(path);
		return f.canRead() ? f.lastModified() : 0L;
	}

	@Override
	public String getSkinDPath(SessionData ssd) {
		return gbd.get("SKIN_DPATH").toString();
	}

	@Override
	public String getPluginDPath() {
		return gbd.get("PLUGIN_DPATH").toString();
	}

	@Override
	public String getPluginPkgs(SessionData ssd) {
		return gbd.get("PLUGIN_PKGS").toString();
	}

	@Override
	public FileIO acquireFileIO(String key) {
		String ioFPath = gbd.get("FILE_DPATH") + key;
		Lock lock = lockFileSystemCache(ioFPath, false);
		return new DefaultFileIO(ioFPath, lock, this);
	}

	@Override
	public void deleteFile(String key) {
		String ioFPath = gbd.get("FILE_DPATH") + key;
		Lock lock = lockFileSystemCache(ioFPath, false);
		File f = new File(ioFPath);
		f.delete();
		unlockFileSystemCache(ioFPath, lock);
	}

	@Override
	public HttpSession getSession(boolean create, HttpServletRequest req) {
		return req.getSession(create);
	}

}
