package grails.plugin.cache.custom.infinispan

import grails.config.Config
import groovy.util.logging.Slf4j
import org.infinispan.manager.EmbeddedCacheManager

import javax.naming.Context
import javax.naming.InitialContext

@Slf4j
class WildflyCacheConfig implements CacheConfig
{
    protected EmbeddedCacheManager manager

    WildflyCacheConfig(Config config)
    {
        def jdniURI = config.getProperty('grails.cache.custom.wildfly.jdni', String)
        log.info("JDNI: $jdniURI")

        if (jdniURI == null)
            throw new RuntimeException("Please provide a JDNI URI for the Wildfly Infinispan cache container")

        try {
            Context context = new InitialContext()
            manager = (EmbeddedCacheManager) context.lookup(jdniURI)
            log.info("Cache manager acquired")
        } catch( Exception ex)
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
