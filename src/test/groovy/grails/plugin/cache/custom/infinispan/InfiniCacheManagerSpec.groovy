package grails.plugin.cache.custom.infinispan

import grails.testing.spring.AutowiredTest
import org.infinispan.manager.DefaultCacheManager
import org.infinispan.manager.EmbeddedCacheManager
import spock.lang.Shared
import spock.lang.Specification

class InfiniCacheManagerSpec extends Specification implements AutowiredTest
{
    @Shared InfiniCacheManager manager

    def setupSpec() {
        manager = new InfiniCacheManager(new CacheConfig() {

            @Override
            EmbeddedCacheManager getManager()
            {
                new DefaultCacheManager()
            }
        })
    }

    def "Create cache"()
    {
        given:
        def cacheName = 'random-cache'

        expect:
        !manager.cacheExists(cacheName)

        when:
        def cache = manager.getCache(cacheName)

        then:
        manager.cacheExists(cacheName)

        and:
        cache.getName() == cacheName
    }

    def "Get cache"()
    {
        given:
        def cacheName = 'random-cache'

        expect:
        manager.cacheExists(cacheName)

        when:
        def cache = manager.getCache(cacheName)

        then:
        cache.getName() == cacheName
    }

    def "Get cache names"()
    {
        given:
        def caches = manager.getCacheNames()

        expect:
        caches.size() == 1
        caches.contains('random-cache')
    }

    def "Destroy cache"()
    {
        given:
        def cacheName = 'random-cache'

        expect:
        manager.cacheExists(cacheName)

        when:
        manager.destroyCache(cacheName)

        then:
        !manager.cacheExists(cacheName)

    }

    def cleanupSpec() {
        manager.manager.stop()
    }
}
