# Shiro-JAX-RS

Needs Adoptium jdk-17.0.7+7 (https://adoptium.net/temurin/archive/) and Wildfly 26.1.3 (https://www.wildfly.org/downloads/) with this initial setup:

Place the files https://repo1.maven.org/maven2/org/apache/shiro/shiro-core/1.11.0/shiro-core-1.11.0.jar, https://repo1.maven.org/maven2/org/apache/shiro/shiro-web/1.11.0/shiro-web-1.11.0.jar and https://search.maven.org/remotecontent?filepath=org/owasp/encoder/encoder/1.2.3/encoder-1.2.3.jar into the directory [WLDFLY_HOME]/modules/org/apache/shiro/main/

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
