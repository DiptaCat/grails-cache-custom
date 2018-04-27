package grails.plugin.cache.custom.infinispan

import spock.lang.Shared
import spock.lang.Specification

class InfiniCacheManegerSpec extends Specification
{
    @Shared InfiniCacheManager manager

    def setupSpec() {
        manager = new InfiniCacheManager(null)
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
        manager.manager.close()
    }
}
