package de.scsynergy.jax.rs;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.enterprise.context.ApplicationScoped;
import org.infinispan.Cache;
import org.infinispan.commons.api.CacheContainerAdmin;
import org.infinispan.commons.marshall.JavaSerializationMarshaller;
import org.infinispan.configuration.cache.Configuration;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.transaction.LockingMode;
import org.infinispan.transaction.TransactionMode;
import org.infinispan.util.concurrent.IsolationLevel;

/**
 * @author rf
 */
@ApplicationScoped
public class CacheManagerInfinispanProvider {

    @Resource(lookup = "java:app/AppName")
    private String applicationName;
    private DefaultCacheManager cacheContainer;
    private static final Configuration HTTPSESSIONMANAGER = new ConfigurationBuilder()
            .locking()
            .isolationLevel(IsolationLevel.READ_COMMITTED)
            .transaction()
            .transactionMode(TransactionMode.NON_TRANSACTIONAL)
            .lockingMode(LockingMode.OPTIMISTIC)
            .persistence()
            .passivation(false)
            .build();
    private static final String[] REGEXPS = new String[]{
        "java\\.util\\..+",
        "javax\\.el\\..+",
        "javax\\.faces\\..+",
        "org\\.jboss\\.weld\\..+",
        "com\\.sun\\.el\\..+",
        "com\\.sun\\.faces\\..+",
        "org\\.bson\\.Document",
        "org\\.bson\\.types\\..+",
        "org\\.primefaces\\..+",
        "org\\.apache\\.shiro\\..+",
        "org\\.omnifaces\\.cdi\\.push\\.Socket",
        "de\\.scsynergy\\.elementary\\.qi\\..+",
        "de\\.scsynergy\\.elementary\\.dms\\..+",
        "javax\\.security\\.auth\\.kerberos\\.KerberosPrincipal"
    };

    @PostConstruct
    private void init() {
        GlobalConfigurationBuilder globalConfigurationBuilder = new GlobalConfigurationBuilder();
        globalConfigurationBuilder
                .transport().defaultTransport().clusterName("HONESTY").nodeName(applicationName)
                .serialization()
                .marshaller(new JavaSerializationMarshaller())
                .allowList()
                .addRegexps(REGEXPS);
        cacheContainer = new DefaultCacheManager(globalConfigurationBuilder.build(), true);
    }

    @PreDestroy
    private void preDestroy() {
        for (String name : cacheContainer.getCacheNames()) {
            cacheContainer.getCache(name).stop();
        }
    }

    public <K, V> Cache<K, V> httpSessionManager(String name) {
        return cacheContainer.administration().withFlags(CacheContainerAdmin.AdminFlag.VOLATILE).getOrCreateCache(name, HTTPSESSIONMANAGER);
    }
}
