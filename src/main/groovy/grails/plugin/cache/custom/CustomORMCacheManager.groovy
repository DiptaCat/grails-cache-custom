package grails.plugin.cache.custom

import custom.cache.CacheORM
import grails.config.Config
import groovy.util.logging.Slf4j
import org.grails.plugin.cache.GrailsCacheManager
import org.springframework.cache.Cache

@Slf4j
class CustomORMCacheManager implements GrailsCacheManager {

    protected Config config

    CustomORMCacheManager(Config config) {
        this.config = config
    }

    @Override
    boolean cacheExists(String s) {
        boolean exists = false
        CacheORM.withNewSession {
            CacheORM cache = CacheORM.findByName(s)
            exists = (cache != null)
        }
        exists
    }

    @Override
    boolean destroyCache(String s) {
        Cache cache = getCacheInternal(s, false)
        if (cache) {
            cache.clear()
        }
        return false
    }

    @Override
    Cache getCache(String name) {

        log.debug "[m:getCache] name: {}", name

        getCacheInternal(name)
    }

    private Cache getCacheInternal(String name, boolean create = true) {
        def cacheORM = null
        CacheORM.withNewSession {
            cacheORM = CacheORM.findByName(name)
        }
        if (cacheORM) {
            return new CustomCacheORM(name, cacheORM)
        }

        if (create) {
            return getOrCreateCache(name)
        }

        null
    }

    // TODO: Afegir configuracions per cache segons el nom
    protected Cache getOrCreateCache(String s) {
        Cache cache
        CacheORM.withNewSession {
            CacheORM cacheORM = new CacheORM([name: s])
            cacheORM.save(flush: true)

            cache = new CustomCacheORM(s, cacheORM)
        }
        cache
    }



    @Override
    Collection<String> getCacheNames() {
        Collections.unmodifiableList(CacheORM.all*.name)
    }
}
