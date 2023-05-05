package de.scsynergy.jax.rs;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.apache.shiro.cache.CacheException;
import org.infinispan.Cache;

/**
 *
 * @author rf
 */
public class CacheInfinispan<K, V> implements org.apache.shiro.cache.Cache<K, V> {

    private Cache<K, V> cache;

    protected CacheInfinispan() {
    }

    public CacheInfinispan(Cache cache) {
        this.cache = cache;
    }

    @Override
    public V get(K key) throws CacheException {
        return cache.get(key);
    }

    @Override
    public V put(K key, V value) throws CacheException {
        if (Thread.currentThread().getName().startsWith("Camel ")) {
            return cache.putIfAbsent(key, value);
        } else {
            return cache.put(key, value, 1, TimeUnit.DAYS);
        }
    }

    @Override
    public V remove(K key) throws CacheException {
        return cache.remove(key);
    }

    @Override
    public void clear() throws CacheException {
        cache.clear();
    }

    @Override
    public int size() {
        return cache.size();
    }

    @Override
    public Set<K> keys() {
        return cache.keySet();
    }

    @Override
    public Collection<V> values() {
        return cache.values();
    }
}
