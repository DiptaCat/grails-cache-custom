package grails.plugin.cache.custom

import grails.config.Config
import grails.plugin.cache.custom.infinispan.InfiniCacheManager
import grails.plugin.cache.custom.infinispan.WildflyCacheConfig
import org.grails.plugin.cache.GrailsCacheManager

class CustomCacheManagerFactory {


    GrailsCacheManager getInstance(def cacheType, Config config) {
        switch (cacheType){
            case 'orm':
                return new CustomORMCacheManager(config)
                break
            case 'wildfly':
                return new InfiniCacheManager(new WildflyCacheConfig(config))
                break
            default:
                throw new IllegalStateException("'$cacheType' is not a valid custom cache implementation type")
        }
    }
}
