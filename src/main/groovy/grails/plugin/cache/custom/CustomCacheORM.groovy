package grails.plugin.cache.custom

import custom.cache.CacheItemORM
import custom.cache.CacheORM
import grails.plugin.cache.GrailsCache
import groovy.transform.Synchronized
import groovy.util.logging.Slf4j
import org.springframework.cache.Cache
import org.springframework.cache.support.SimpleValueWrapper
import org.xbib.groovy.crypt.CryptUtil

import java.util.concurrent.Callable

@Slf4j
class CustomCacheORM implements GrailsCache {

    String name
    CacheORM cacheORM

    CustomCacheORM(String name, CacheORM cacheORM) {
        this.name = name
        this.cacheORM = cacheORM

        log.debug "CustomCacheORM: $name, $cacheORM"
    }


    // -------------------------------------------------
    // BEGIN - org.springframework.cache.Cache
    // -------------------------------------------------

    @Override
    Object getNativeCache() {
        return cacheORM
    }

    @Override
    Cache.ValueWrapper get(Object key) {
        log.debug "get(1): ${key.dump()}"
        CacheItemORM.withNewSession {
            def value = this.internalGet(digestKey(key))
            value == null ? null : new SimpleValueWrapper(value)
        }
    }

    @Override
    def <T> T get(Object key, Class<T> type) {
        log.debug "get(2): $key, $type"
        def value = this.internalGet(digestKey(key))
        if (value != null && type != null && !type.isInstance(value)) {
            throw new IllegalStateException("Cached value is not of required type [" + type.getName() + "]: " + value)
        }
        (T) value
    }

    @Override
    def <T> T get(Object key, Callable<T> valueLoader) {
        log.debug "get(3): $key"
        throw new UnsupportedOperationException()
    }

    @Override
    void put(Object key, Object value) {
        log.debug "put: $key, $value"
        CacheItemORM.withNewSession {
            this.internalPut(digestKey(key), value)
        }
    }

    @Override
    @Synchronized
    Cache.ValueWrapper putIfAbsent(Object key, Object value) {
        log.debug "putIfAbsent: $key, $value"
        def digestedKey = digestKey(key)
        def existingValue = this.internalGet(digestedKey)
        if (existingValue == null) {
            this.internalPut(digestedKey, value)
            return null
        } else {
            return new SimpleValueWrapper(existingValue)
        }
    }

    @Override
    void evict(Object key) {
        log.debug "evict: $key"
        CacheItemORM item = CacheItemORM.findByCacheAndKey(this.cacheORM, digestKey(key))
        if (item) {
            item.delete(flush: true)
        }
    }

    @Override
    void clear() {
        log.debug 'clear'

        CacheItemORM.withNewSession { session ->
            CacheItemORM.deleteAll(CacheItemORM.findAllByCache(this.cacheORM))
            this.cacheORM.delete()
            session.flush()
        }
    }

    // -------------------------------------------------
    // END - org.springframework.cache.Cache
    // -------------------------------------------------

    // -------------------------------------------------
    // BEGIN - grails.plugin.cache.GrailsCache
    // -------------------------------------------------

    @Override
    Collection<Object> getAllKeys() {
        log.debug 'getAllKeys'
        CacheItemORM.findAllByCache(this.cacheORM)*.key
    }

    // -------------------------------------------------
    // END - grails.plugin.cache.GrailsCache
    // -------------------------------------------------

    private def internalGet(key) {
        log.debug "internalGet(${this.cacheORM}, $key)"
        CacheItemORM item = CacheItemORM.findByCacheAndKey(this.cacheORM, key)
        if (item) {
            return item.value
        }

        null
    }

    private def internalPut(key, value) {
        log.debug "internalPut(${this.name}, $key)"
        def oldValue = null
        CacheItemORM item = CacheItemORM.findByCacheAndKey(this.cacheORM, key)
        if (item == null) {
            item = new CacheItemORM([cache: this.cacheORM, key: key, value: value])
        } else {
            oldValue = item.value
            item.value = value
        }
        item.save(flush:true)

        oldValue
    }


    private static String digestKey(Serializable key) {
        CryptUtil.sha256(String.valueOf(key.hashCode()))
    }


}
