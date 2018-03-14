package grails.plugin.cache.custom

import custom.cache.CacheItemORM
import custom.cache.CacheORM
import org.springframework.cache.Cache
import org.springframework.cache.support.SimpleValueWrapper
import grails.plugin.cache.GrailsCache

import java.util.concurrent.Callable

class GrailsCustomCache<K, V> implements GrailsCache {

    protected String name
    protected GrailsCache cache

    GrailsCustomCache(String name, GrailsCache cache) {
        this.cache = cache
        this.name = name
    }

    @Override
    String getName() {
        name
    }

    @Override
    Collection<Object> getAllKeys() {
        return getNativeCache().getAllKeys()
    }


    @Override
    GrailsCache getNativeCache() {
        cache
    }

    @Override
    Cache.ValueWrapper get(Object key) {
        return getNativeCache().get(key)
    }

    @Override
    def <T> T get(Object key, Class<T> type) {
        return getNativeCache().get(key, type)
    }

    @Override
    def <T> T get(Object key, Callable<T> valueLoader) {
        return getNativeCache().get(key, valueLoader)
    }

    @Override
    void put(Object key, Object value) {
        getNativeCache().put(key, value)
    }

    @Override
    Cache.ValueWrapper putIfAbsent(Object key, Object value) {
        return getNativeCache().putIfAbsent(key, value)
    }

    @Override
    void evict(Object key) {
        getNativeCache().evict(key)
    }

    @Override
    void clear() {
        getNativeCache().clear()
    }
}