package com.mycompany.ehcache;

import org.apache.log4j.Logger;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.Status;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.units.EntryUnit;
import org.ehcache.config.units.MemoryUnit;

import com.mycompany.ehcache.domain.User;

public class MySimpleCacheManager {

	private static final Logger logger = Logger.getLogger(MySimpleCacheManager.class);
	
	private static final String CACHE_PATH = "/tmp/esb-cache/mySimpleCache";
	private static final String NAME_OF_USER_CACHE = "USER_CACHE"; 
	
	private CacheManager cacheManager;
			
	private Cache<String, User> myUserCache;
		
	private static final class InstanceHolder {
		static final MySimpleCacheManager INSTANCE = new MySimpleCacheManager();
	}	
	
	public static MySimpleCacheManager getInstance() {
		return InstanceHolder.INSTANCE;
	}

	private MySimpleCacheManager() {

		logger.info("*** MySimpleCacheManager() ...");
		createCache();
		logger.info("*** MySimpleCacheManager().");
	}

	public void shutdown() {
		logger.info("*** MySimpleCache::shutdown() instance: '" + InstanceHolder.INSTANCE + "' ...");
		
		if ((InstanceHolder.INSTANCE != null) 
				&& (cacheManager != null) && (cacheManager.getStatus() == Status.AVAILABLE) ) {
			cacheManager.close();
			logger.info("***  cacheManager closed.");
		}
		
		logger.info("*** MySimpleCache::shutdown().");
	}
	
	protected int getSize() {
	    int count = 0;
	    for(Cache.Entry<String, User> entry : myUserCache) {
	        count++;
	    }
	    
	    return count;
	}
	
	public void populateCache() {
		
		if (getSize() < 1) {
			logger.info("*** populateCache() ..."); 
			for (int i = 0; i < 42; i++) {
				User u = new User("Mustermann-" + i, "Hans-" + i);
				myUserCache.put("U-" + i, u);
			}
			logger.info("*** populateCache()."); 
		} else {
			logger.info("*** populateCache() - cache already populated. #entries: " + getSize()); 
		}
	}
	
	public void dumpCache() {
		
		logger.info("*** dumpCache() ...");
		
		for (int i = 0; i < getSize(); i++) {
			User u = myUserCache.get("U-" + i);
			logger.info("***  User: " + u);
		}
		
		logger.info("*** dumpCache().");
	}

	private void createCache() {
		
		if (cacheManager == null) {
			cacheManager = CacheManagerBuilder.newCacheManagerBuilder()
								.with(CacheManagerBuilder.persistence(CACHE_PATH))
								.build(true);
		}
		
		if (myUserCache == null) {
			myUserCache = cacheManager.getCache(NAME_OF_USER_CACHE, String.class, User.class);
			logger.info("*** myUserCache: " + myUserCache);
			if (myUserCache == null) {
				// Cache für Branchen anlegen (wird etwas anders behandelt)
				myUserCache = cacheManager.createCache(NAME_OF_USER_CACHE, 
						CacheConfigurationBuilder.newCacheConfigurationBuilder(String.class, User.class, 
								ResourcePoolsBuilder.newResourcePoolsBuilder()
									.heap(25, EntryUnit.ENTRIES)
									.disk(10, MemoryUnit.MB, true)
							).build());	
				logger.info("*** myUserCache: " + myUserCache);
			}
		}
		
		logger.info("cache (USER_CACHE) contains entries: #" + getSize());
	}
	
}
