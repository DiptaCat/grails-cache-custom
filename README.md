
Grails Cache Custom Plugin
====================

Change the cache source implementation for the [Grails Cache Plugin](https://github.com/grails-plugins/grails-cache)


This is (at this moment) a private plugin

Installation
------------

To install this plugin into a grails3 application just add this dependency to build.gradle file:

`compile "cat.dipta.plugins:cache-custom:0.2.3"`


You can safely remove the grails-cache dependency as this plugin already depends on it

Database Mode
-------------

By default, the plugin uses a database to store cache items. However, you also need to configure a datasource named **cache**. The name is not configurable, so the name **MUST** be "cache"

If you need to pre-generate db tables, you can use the SQL code in `ddl-oracle.sql` file

Infinispan Wildfly Mode
-----------------------

In addition, you have to configure the plugin to work with the Infinispan subsystem on a Wildfly server.

1) Considering the following cache configuration : 

``` xml
<cache-container name="cache" default-cache="default" module="org.wildfly.clustering.server" jndi-name="infinispan/cache">
   <transport lock-timeout="60000"/>
   <replicated-cache name="default" jndi-name="infinispan/cache/default" mode="SYNC"/>
</cache-container>
```

2) The settings (ex. `application.yml`) needs a valid JDNI pointing to an Infinispan container.

``` yml
grails:
    cache:
        custom:
            impl: 'wildfly'
            wildfly:
                jdni: "java:jboss/infinispan/cache"
                default: "java:jboss/infinispan/cache/default"
```
3) Explicitly declare the default-cache within the web.xml that will use the plugin

```xml
<web-app ... version="3.0">
    <resource-ref>
        <res-ref-name>infinispan/cache/default</res-ref-name>
        <lookup-name>java:jboss/infinispan/cache/default</lookup-name>
    </resource-ref>

    <distributable/>
</web-app>
```
