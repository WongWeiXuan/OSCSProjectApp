package login;

import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;

public class CacheMan {
	private CacheManager cacheManager;

	public CacheMan() {
		cacheManager = CacheManagerBuilder
				.newCacheManagerBuilder().withCache("preConfirgured", CacheConfigurationBuilder
						.newCacheConfigurationBuilder(String.class, String.class, ResourcePoolsBuilder.heap(5)))
				.build();
		cacheManager.init();
	}

	public CacheManager getCacheManager() {
		return cacheManager;
	}
}
