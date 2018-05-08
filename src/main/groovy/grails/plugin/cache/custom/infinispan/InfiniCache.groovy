package grails.plugin.cache.custom.infinispan

import grails.plugin.cache.GrailsCache
import groovy.util.logging.Slf4j
import org.springframework.cache.Cache
import org.springframework.cache.support.SimpleValueWrapper

import java.util.concurrent.Callable

@Slf4j
class InfiniCache<K,V> implements GrailsCache
{
    protected org.infinispan.Cache<K,V> infinispanCache
    protected String name

    InfiniCache(String name, org.infinispan.Cache<K,V> infinispanCache)
    {
        this.name = name
        this.infinispanCache = infinispanCache
    }

    @Override
    Collection<Object> getAllKeys()
    {
        log.debug("Get all keys")
        infinispanCache.keySet()
    }

    @Override
    String getName()
    {
        log.debug("Get cache name")
        name
    }

    @Override
    org.infinispan.Cache<K,V> getNativeCache()
    {
        log.debug("Get native cache")
        infinispanCache
    }

    @Override
    Cache.ValueWrapper get(Object key)
    {
        log.debug("Get($key) from cache")
        V value = getNativeCache().get(key)
        value == null ? null : new SimpleValueWrapper(value)
    }

    @Override
    <T> T get(Object key, Class<T> type)
    {
        log.debug("Get($key, $type) from cache")
        V value = getNativeCache().get(key)
        if (value != null && type != null && !type.isInstance(value)) {
            throw new IllegalStateException("Cached value is not of required type [" + type.getName() + "]: " + value)
        }
        return value as T
    }

    @Override
    <T> T get(Object key, Callable<T> valueLoader)
    {
        log.debug("Get($key, $valueLoader) from cache")
        def value = getNativeCache().get(key)
        if (value == null)
        {
            value = valueLoader.call()
            put(key, value)
        }
        return value
    }

    @Override
    void put(Object key, Object value)
    {
        log.debug("Put($key, $value) in cache")
        getNativeCache().put(key as K,value as V)
    }

    @Override
    Cache.ValueWrapper putIfAbsent(Object key, Object value)
    {
        log.debug("putIfAbsent($key, $value) in cache")
        new SimpleValueWrapper(getNativeCache().putIfAbsent((K)key,(V)value))
    }

    @Override
    void evict(Object key)
    {
        log.debug("Evict($key) from cache")
        getNativeCache().evict(key as K)
    }

    @Override
    void clear()
    {
        log.debug("Clear cache")
        getNativeCache().clear()
    }
}
