package grails.plugin.cache.custom

import grails.plugin.cache.CustomCacheKeyGenerator
import grails.plugins.*
import groovy.util.logging.Slf4j
import org.grails.plugin.cache.GrailsCacheManager

@Slf4j
class CustomCacheGrailsPlugin extends Plugin {

    // the version or versions of Grails the plugin is designed for
    def grailsVersion = "3.3.2 > *"
    // resources that are excluded from plugin packaging
    def pluginExcludes = [
        "grails-app/views/error.gsp"
    ]

    // TODO Fill in these fields
    def title = "Custom Cache" // Headline display name of the plugin
    def author = "Juan Fuentes"
    def authorEmail = "jfuentes@dipta.cat"
    def description = '''\
Custom implementation for grails cache plugin
'''

    // URL to the plugin's documentation
//    def documentation = "http://grails.org/plugin/custom-cache"

    // Extra (optional) plugin metadata

    // License: one of 'APACHE', 'GPL2', 'GPL3'
    def license = "APACHE"

    // Details of company behind the plugin (if there is one)
    def organization = [ name: "Diputació de Tarragona", url: "http://www.dipta.cat/" ]

    // Any additional developers beyond the author specified above.
//    def developers = [ [ name: "Joe Bloggs", email: "joe@bloggs.net" ]]

    // Location of the plugin's issue tracker.
//    def issueManagement = [ system: "JIRA", url: "http://jira.grails.org/browse/GPMYPLUGIN" ]

    // Online location of the plugin's browseable source code.
//    def scm = [ url: "http://svn.codehaus.org/grails-plugins/" ]

    List loadAfter = ['cache', 'hibernate3', 'hibernate4', 'hibernate5']

    def dependsOn = [cache: "4.0.0.BUILD-SNAPSHOT > *"]


    Closure doWithSpring() { {->
        log.info 'Configuring custom-cache-plugin'
        String customImpl = config.getProperty('grails.cache.custom.impl', String, 'orm')
        log.debug "Custom cache type = $customImpl"
        if (customImpl != 'default') {
            // Only substitute cache beans if implementation is not 'default'

            customCacheKeyGenerator(CustomKeyGenerator)

            GrailsCacheManager customManagerImpl = new CustomCacheManagerFactory().getInstance(customImpl, config)

            grailsCacheManager(GrailsCustomCacheManager) {
                customCacheManager = customManagerImpl
            }
        }
    }
    }

}
