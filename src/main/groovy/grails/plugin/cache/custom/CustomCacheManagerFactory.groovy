package grails.plugin.cache.custom

import grails.config.Config
import org.grails.plugin.cache.GrailsCacheManager

class CustomCacheManagerFactory {


    GrailsCacheManager getInstance(def cacheType, Config config) {
        switch (cacheType){
            case 'orm':
                return new CustomORMCacheManager(config)
                break;
            default:
                throw new IllegalStateException("'$cacheType' is not a valid custom cache implementation type")
        }
    }
}
