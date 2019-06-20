package grails.plugin.cache.custom

import custom.cache.CacheItemORM
import custom.cache.CacheORM
import grails.plugin.cache.GrailsCache
import groovy.transform.Synchronized
import groovy.util.logging.Slf4j
import org.springframework.cache.Cache
import org.springframework.cache.support.SimpleValueWrapper
import org.springframework.dao.DuplicateKeyException
import org.xbib.groovy.crypt.CryptUtil

import java.sql.SQLIntegrityConstraintViolationException
import java.util.concurrent.Callable

@Slf4j
class CustomCacheORM implements GrailsCache {

    String name
    CacheORM cacheORM

    CustomCacheORM(String name, CacheORM cacheORM) {
        this.name = name
        this.cacheORM = cacheORM

        log.debug "[m:CustomCacheORM]: name: {}, cacheORM: {}", name, cacheORM
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
        log.debug "[m:get] cache: {}, key: {}, key.dump(): {}", name, key, key.dump()

        /*key.getProperties().each {
            log.debug "[m:get]: key: {}, value: {}", it.key, it.value
        }*/

        /*
        El log de dalt mostra:

        2019-06-14 10:44:35.998 DEBUG [m:get]: key: class, value: class grails.plugin.cache.custom.CustomKeyGenerator$TemporaryGrailsCacheKey
        2019-06-14 10:44:35.998 DEBUG [m:get]: key: simpleKey, value: {}
        2019-06-14 10:44:35.998 DEBUG [m:get]: key: targetMethodName, value: null
        2019-06-14 10:44:35.998 DEBUG [m:get]: key: targetClassName, value: null
         */

        CacheItemORM.withNewSession {
            def value = this.internalGet(digestKey(key))
            value == null ? null : new SimpleValueWrapper(value)
        }
    }

    @Override
    def <T> T get(Object key, Class<T> type) {
        log.debug "[m:get]: cache: {}, key: {}, tye: {}", name, key, type

/*
        key.getProperties().each {
            log.debug "[m:get]: key: {}, value: {}", it.key, it.value
        }
*/

        def value = this.internalGet(digestKey(key))
        if (value != null && type != null && !type.isInstance(value)) {
            throw new IllegalStateException("Cached value is not of required type [" + type.getName() + "]: " + value)
        }
        (T) value
    }

    @Override
    def <T> T get(Object key, Callable<T> valueLoader) {
        log.debug "[m:get]: cache: {}, key: {}", name, key
        throw new UnsupportedOperationException()
    }

    @Override
    void put(Object key, Object value) {
        log.debug "[m:put]: cache: {}, key: {}, value: {}", name, key, value
        CacheItemORM.withNewSession {
            this.internalPut(digestKey(key), value)
        }
    }

    @Override
    @Synchronized
    Cache.ValueWrapper putIfAbsent(Object key, Object value) {
        log.debug "[m:putIfAbsent]: cache: {}, key: {}, value: {}", name, key, value
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
        log.debug "[m:evict]: cache: {}, key: {}", name, key

        CacheItemORM.withNewSession { session ->
            CacheItemORM item = CacheItemORM.findByCacheAndKey(this.cacheORM, digestKey(key))
            if (item) {
                item.delete(flush: true)
            }
        }
    }

    @Override
    void clear() {
        log.debug "[m:clear] cache: {} ..", name

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
        log.debug "[m:getAllKeys] cache: {}", name
        CacheItemORM.findAllByCache(this.cacheORM)*.key
    }

    // -------------------------------------------------
    // END - grails.plugin.cache.GrailsCache
    // -------------------------------------------------

    private def internalGet(key) {
        log.debug "[m:internalGet] cache: {}, this.cacheORM: {}, key: {}", name, this.cacheORM, key
        CacheItemORM item = CacheItemORM.findByCacheAndKey(this.cacheORM, key)
        if (item) {
            return item.value
        }

        null
    }

    private def internalPut(key, value) {
        log.debug "[m:internalPut] this.name: {}, key: {}", this.name, key
        def oldValue = null
        CacheItemORM item = CacheItemORM.findByCacheAndKey(this.cacheORM, key)
        if (item == null) {
            try {
                item = new CacheItemORM([cache: this.cacheORM, key: key, value: value])
            } catch (e) {
                log.error("[m:internalPut] cache: $name, CRITICAL ERROR storing key/value pair ($key/$value) : "+e.message, e)
                return oldValue
            }
        } else {
            oldValue = item.value
            item.value = value
        }
        try {
            item.save(flush: true)
        } catch (DuplicateKeyException dke) {
            log.warn("[m:internalPut] cache: $name, Duplicate key [$key] in cache [${this.name}]: $dke.message", dke)
        } catch (SQLIntegrityConstraintViolationException sqlIntegrityConstraintViolationException) {
            log.warn("[m:internalPut] cache: $name, Duplicate key [$key] in cache [${this.name}]: $sqlIntegrityConstraintViolationException.message", sqlIntegrityConstraintViolationException)
        }

        oldValue
    }


    private static String digestKey(Serializable key) {
        //log.debug "[m:digestKey] key: {}, hashCode: {}, Key resultant: {}, simpleKey: {}", key, key.hashCode(), CryptUtil.sha256(String.valueOf(key.hashCode())), key.getAt('simpleKey')

        CryptUtil.sha256(String.valueOf(key.hashCode()))
    }

}
