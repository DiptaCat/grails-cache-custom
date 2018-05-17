package grails.plugin.cache.custom.infinispan

import grails.config.Config
import groovy.util.logging.Slf4j
import org.infinispan.Cache
import org.infinispan.manager.EmbeddedCacheManager

import javax.naming.Context
import javax.naming.InitialContext

@Slf4j
class WildflyCacheConfig implements CacheConfig
{
    protected EmbeddedCacheManager manager

    WildflyCacheConfig(Config config)
    {
        String jdniContainer = config.getProperty('grails.cache.custom.wildfly.jdni', String)

        if (jdniContainer == null)
            throw new RuntimeException("Please provide a JDNI URI for the Wildfly Infinispan cache container")

        log.info("Container JDNI: $jdniContainer")

        String jdniCache = config.get('grails.cache.custom.wildfly.default', String)

        if (jdniCache == null)
            throw new RuntimeException("Please provide a JDNI URI for the Wildfly Infinispan default cache")

        log.info("Default Cache JDNI: $jdniCache")

        Context context = new InitialContext()
        Cache defaultCache

        try {
             defaultCache = (Cache) context.lookup(jdniCache)

        } catch(Exception ex)
        {
            log.error(ex.message)
            throw new RuntimeException("Please provide a valid JDNI for the Wildfly Infinispan Default Cache")
        }

        try {
            manager = (EmbeddedCacheManager) context.lookup(jdniContainer)
            manager.defineConfiguration('default', defaultCache.getCacheConfiguration())
            log.info('Default cache configuration retrieved')
        } catch(Exception ex)
        {
            log.error(ex.message)
            throw new RuntimeException("Please provide a valid JDNI for the Wildfly Infinispan cache container")
        }
    }

    @Override
    EmbeddedCacheManager getManager()
    {
        return manager
    }
}
