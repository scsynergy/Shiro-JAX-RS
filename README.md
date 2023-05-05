# Shiro-JAX-RS

Needs Wildfly 26.1.3 with this initial setup:

Place the files shiro-core-1.11.0.jar, shiro-web-1.11.0.jar and encoder-1.2.3.jar into the directory [WLDFLY_HOME]/modules/org/apache/shiro/main/

Create a file called module.xml in the same directory with this content
```
<?xml version="1.0" encoding="UTF-8"?>
<module name="org.apache.shiro" xmlns="urn:jboss:module:1.6">
        <resources>
                <resource-root path="shiro-core-1.11.0.jar"/>
                <resource-root path="shiro-web-1.11.0.jar"/>
                <resource-root path="encoder-1.2.3.jar"/>
        </resources>
        <dependencies>
                <module name="javax.api"/>
                <module name="javax.servlet.api" optional="true"/>
                <module name="org.apache.commons.beanutils"/>
                <module name="org.slf4j"/>
        </dependencies>
</module>
```
