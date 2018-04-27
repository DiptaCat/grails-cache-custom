package grails.plugin.cache.custom.infinispan

import grails.plugin.cache.GrailsCache
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Stepwise
import spock.lang.Unroll

import java.util.concurrent.Callable

@Stepwise
class InfiniCacheSpec extends Specification
{
    @Shared InfiniCacheManager manager
    @Shared String cacheName = 'random'

    def setupSpec()
    {
        manager = new InfiniCacheManager(null)
    }

    def "Save and retrieve a value in cache"()
    {
        given:
        def cache = manager.getCache(cacheName)

        when:
        cache.put('some-key', 'some-value')

        then:
        cache.get('some-key').get() == 'some-value'
    }

    @Unroll("Retrieve a value with type: #type")
    def "Retrieve with type"()
    {
        given:
        def cache = manager.getCache(cacheName)

        when:
        cache.put(key,value)

        then:
        cache.get(key,type) == value


        where:
        key         | value     | type
        'a string'  | 'qwerty'  | String
        'a integer' | 1         | Integer
        'a double'  | 2.2d      | Double
        'a list'    | [1,2,3,4] | List
    }

    def "Get with valueLoader"()
    {
        given:
        def cache = manager.getCache(cacheName)
        def key = 'random key'
        def callableValue = new Callable() {

            @Override
            Object call() throws Exception
            {
                'random-value'
            }
        }

        expect:
        cache.get(key) == null

        when:
        def value = cache.get(key, callableValue)

        then:
        value == 'random-value'

        and:
        cache.get(key).get() == 'random-value'
    }

    def "Get keys"()
    {
        given:
        def cache = manager.getCache(cacheName) as GrailsCache

        expect:
        cache.getAllKeys().size() == 6
        cache.getAllKeys().contains(key)

        where:
        key << ['a list', 'a double', 'a string', 'a integer', 'random key', 'some-key']
    }

    def "PutIf absent"()
    {
        given:
        def cache = manager.getCache(cacheName)
        def key = 'another key'

        expect:
        cache.putIfAbsent(key, 'some-value').get() == null      //Previous value is null

        and:
        cache.putIfAbsent(key, 'another-value').get() == 'some-value'
    }

    def cleanupSpec()
    {
        manager.destroyCache(cacheName)
        manager.manager.stop()
    }

}
