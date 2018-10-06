package org.kyojo.core.io;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.cache.Cache;
import javax.cache.CacheException;
import javax.cache.CacheFactory;
import javax.cache.CacheManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kyojo.core.GlobalData;

import com.google.appengine.api.memcache.stdimpl.GCacheFactory;
import com.google.appengine.tools.cloudstorage.GcsFilename;
import com.google.appengine.tools.cloudstorage.GcsService;
import com.google.appengine.tools.cloudstorage.GcsServiceFactory;
import com.google.appengine.tools.cloudstorage.RetryParams;

public class DefaultGAEIOLayer extends DefaultIOLayer {

	private static final Log logger = LogFactory.getLog(DefaultGAEIOLayer.class);

	private Cache cache = null;

	private final GcsService gcsService = GcsServiceFactory
			.createGcsService(new RetryParams.Builder()
				.initialRetryDelayMillis(10)
				.retryMaxAttempts(10)
				.totalRetryPeriodMillis(15000)
				.build());

	private String bucketName = null;

	@Override
	public void initialize(GlobalData gbd) throws IOException, Exception {
		super.initialize(gbd);

		try {
			CacheFactory cacheFactory = CacheManager.getInstance().getCacheFactory();
			Map<Object, Object> properties = new HashMap<>();
			properties.put(GCacheFactory.EXPIRATION_DELTA, TimeUnit.DAYS.toSeconds(30));
			cache = cacheFactory.createCache(properties);
		} catch(CacheException ce) {
			logger.error(ce.getMessage(), ce);
		}

		bucketName = gbd.get("GAE_BUCKET_NAME").toString();
	}

	@Override
	public CacheIO acquireCacheIO(String key) {
		byte[] value = (byte[])cache.get(key);
		return new DefaultGAECacheIO(key, value, cache, this);
	}

	@Override
	public void deleteCache(String key) {
		cache.remove(key);
	}

	@Override
	public FileIO acquireFileIO(String key) {
		return new DefaultGAEFileIO(key, gcsService, bucketName, this);
	}

	@Override
	public void deleteFile(String key) {
		GcsFilename gcsFilename = new GcsFilename(bucketName, key);
		try {
			gcsService.delete(gcsFilename);
		} catch(IOException ioe) {
			logger.warn(ioe.getMessage(), ioe);
		}
	}

}
