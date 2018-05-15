package grails.plugin.cache.custom.infinispan

import org.infinispan.manager.EmbeddedCacheManager

interface CacheConfig
{
    EmbeddedCacheManager getManager()
}