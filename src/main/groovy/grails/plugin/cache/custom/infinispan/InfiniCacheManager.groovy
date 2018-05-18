package grails.plugin.cache.custom.infinispan

import groovy.util.logging.Slf4j
import org.grails.plugin.cache.GrailsCacheManager
import org.infinispan.configuration.cache.CacheMode
import org.infinispan.configuration.cache.ConfigurationBuilder
import org.infinispan.manager.EmbeddedCacheManager
import org.springframework.cache.Cache

import java.util.concurrent.ConcurrentHashMap

@Slf4j
class InfiniCacheManager implements GrailsCacheManager
{
    protected EmbeddedCacheManager manager
    protected ConcurrentHashMap<String, InfiniCache> cacheMap = new ConcurrentHashMap<>()

    InfiniCacheManager(CacheConfig config)
    {
        manager = config.manager
        log.debug("Retrieve existing caches")
        manager.getCacheNames().each { name ->
            def cache = new InfiniCache(name, manager.getCache(name))
            cacheMap.put(name, cache)
        }
    }

    @Override
    boolean cacheExists(String s)
    {
        log.debug("Check if cache $s exists")
        manager.cacheExists(s)
    }

    @Override
    boolean destroyCache(String s)
    {
        log.debug("Remove cache $s")
        manager.removeCache(s)
        true
    }

    @Override
    Cache getCache(String name)
    {
        log.debug("Get or create cache $name")
        def cache = cacheMap.get(name)
        if (cache == null)
        {
            cache = createCache(name)
            cacheMap.put(cache.name, cache)
        }
        return cache
    }

    @Override
    Collection<String> getCacheNames()
    {
        log.debug("Get cache names")
        manager.getCacheNames()
    }

    protected InfiniCache createCache(String name)
    {
        log.debug("Creating a new cache: $name")

        //Create a new cache applying the configuration retrieved from the default cache
        new InfiniCache(name, manager.getCache(name,'default'))
    }
}
