package grails.plugin.cache.custom

import grails.config.Config
import grails.plugin.cache.custom.infinispan.InfiniCacheManager
import org.grails.plugin.cache.GrailsCacheManager

class CustomCacheManagerFactory {


    GrailsCacheManager getInstance(def cacheType, Config config) {
        switch (cacheType){
            case 'orm':
                return new CustomORMCacheManager(config)
                break
            case 'infinispan':
                return new InfiniCacheManager(config)
                break
            default:
                throw new IllegalStateException("'$cacheType' is not a valid custom cache implementation type")
        }
    }
}
