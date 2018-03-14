package grails.plugin.cache.custom

import groovy.util.logging.Slf4j
import org.grails.plugin.cache.GrailsCacheManager
import org.springframework.beans.factory.DisposableBean
import org.springframework.beans.factory.InitializingBean
import org.springframework.cache.Cache

@Slf4j
class GrailsCustomCacheManager implements GrailsCacheManager, InitializingBean, DisposableBean {

    protected GrailsCacheManager customCacheManager

    GrailsCustomCacheManager() {
    }

    public void setCustomCacheManager(GrailsCacheManager customCacheManager) {
        this.customCacheManager = customCacheManager
    }

    @Override
    boolean cacheExists(String s) {
        return this.customCacheManager.cacheExists(s)
    }

    @Override
    boolean destroyCache(String s) {
        return this.customCacheManager.destroyCache(s)
    }

    @Override
    void destroy() throws Exception {

    }

    @Override
    void afterPropertiesSet() throws Exception {
        log.debug 'Set configuration'
    }

    @Override
    Cache getCache(String name) {
        return this.customCacheManager.getCache(name)
    }

    @Override
    Collection<String> getCacheNames() {
        return this.customCacheManager.getCacheNames()
    }
}
