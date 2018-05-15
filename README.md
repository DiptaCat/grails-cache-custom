
Grails Cache Custom Plugin
====================

Change the cache source implementation for the [Grails Cache Plugin](https://github.com/grails-plugins/grails-cache)


This is (at this moment) a private plugin

Installation
------------

To install this plugin into a grails3 application just add this dependency to build.gradle file:

`compile "cat.dipta.plugins:cache-custom:0.1"`


You can safely remove the grails-cache dependency as this plugin already depends on it

Database Mode
-------------

As the plugin uses a database to store cache items, you also need to configure a datasource named **cache**. The name is not configurable, so the name **MUST** be "cache"

If you need to pre-generate db tables, you can use the SQL code in `ddl-oracle.sql` file

Infinispan Wildfly Mode
-----------------------

For the cache to work it must be enabled in the application.yml and it must have a valid JDNI pointing to the Infinispan subsystem.

grails:
    cache:
        custom:
            impl: 'wildfly'
            wildfly:
	       jdni: "java:jboss/infinispan/container/test"
