package grails.plugin.cache.custom.infinispan

import grails.plugin.cache.GrailsCache
import org.springframework.cache.Cache
import org.springframework.cache.support.SimpleValueWrapper

import java.util.concurrent.Callable

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
        infinispanCache.keySet()
    }

    @Override
    String getName()
    {
        name
    }

    @Override
    org.infinispan.Cache<K,V> getNativeCache()
    {
        infinispanCache
    }

    @Override
    Cache.ValueWrapper get(Object key)
    {
        V value = getNativeCache().get(key)
        value == null ? null : new SimpleValueWrapper(value)
    }

    @Override
    <T> T get(Object key, Class<T> type)
    {
        V value = getNativeCache().get(key)
        if (value != null && type != null && !type.isInstance(value)) {
            throw new IllegalStateException("Cached value is not of required type [" + type.getName() + "]: " + value)
        }
        return value as T
    }

    @Override
    <T> T get(Object key, Callable<T> valueLoader)
    {
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
        getNativeCache().put(key as K,value as V)
    }

    @Override
    Cache.ValueWrapper putIfAbsent(Object key, Object value)
    {
        new SimpleValueWrapper(getNativeCache().putIfAbsent((K)key,(V)value))
    }

    @Override
    void evict(Object key)
    {
        getNativeCache().evict(key as K)
    }

    @Override
    void clear()
    {
        getNativeCache().clear()
    }
}
