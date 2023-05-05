package de.scsynergy.jax.rs;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.inject.Inject;
import org.apache.shiro.cache.AbstractCacheManager;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;

/**
 *
 * @author rf
 */
@ShiroIni
public class CacheManagerInfinispan extends AbstractCacheManager {

    @Inject
    private CacheManagerInfinispanProvider cacheManagerInfinispanProvider;
    private static final Pattern PATTERN = Pattern.compile("([^.]+)\\.authorizationCache\\.?\\d*");

    protected CacheManagerInfinispan() {
    }

    @Override
    protected Cache createCache(String name) throws CacheException {
        Matcher matcher = PATTERN.matcher(name);
        if (matcher.find()) {
            name = matcher.group(1);
        }
        return new CacheInfinispan(cacheManagerInfinispanProvider.httpSessionManager(name));
    }
}
