package grails.plugin.cache.custom.infinispan

import grails.config.Config
import groovy.util.logging.Slf4j
import org.grails.plugin.cache.GrailsCacheManager
import org.infinispan.configuration.cache.ConfigurationBuilder
import org.infinispan.manager.DefaultCacheManager
import org.infinispan.manager.EmbeddedCacheManager
import org.springframework.cache.Cache

import java.util.concurrent.ConcurrentHashMap

@Slf4j
class InfiniCacheManager implements GrailsCacheManager
{
    protected Config config
    protected EmbeddedCacheManager manager
    protected ConcurrentHashMap<String, InfiniCache> cacheMap = new ConcurrentHashMap<>()

    InfiniCacheManager(Config config)
    {
        this.config = config
        startInfinispanManager()
    }

    private startInfinispanManager()
    {
        def config = new ConfigurationBuilder().build()
        manager = new DefaultCacheManager(config,true)
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
        manager.administration().removeCache(s)
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
        log.debug("Create cache $name")
        new InfiniCache(name, manager.getCache(name))
    }
}
