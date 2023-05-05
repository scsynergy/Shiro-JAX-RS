/*
 * Copyright 2013 Harald Wellmann
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.scsynergy.jax.rs;

import java.util.Map;

import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.CDI;

import org.apache.shiro.config.Ini;
import org.apache.shiro.config.IniFactorySupport;
import org.apache.shiro.config.IniSecurityManagerFactory;
import org.apache.shiro.util.CollectionUtils;
import org.apache.shiro.util.Factory;
import org.apache.shiro.web.env.IniWebEnvironment;
import org.apache.shiro.web.filter.mgt.FilterChainResolver;
import org.apache.shiro.web.mgt.WebSecurityManager;

/**
 * An extension of {@link IniWebEnvironment} which makes CDI beans qualified
 * with {@code @ShiroIni} available to Shiro, to be referenced in INI files.
 *
 * @author Harald Wellmann
 */
public class CdiIniWebEnvironment extends IniWebEnvironment {

    @Override
    protected WebSecurityManager createWebSecurityManager() {
        Ini ini = getIni();

        if (CollectionUtils.isEmpty(ini)) {
            ini = null;
        }

        BeanManager beanManager = CDI.current().getBeanManager();
        IniSecurityManagerFactory factory = new CdiWebIniSecurityManagerFactory(beanManager);
        factory.setIni(ini);

        Map<String, Object> defaults = getDefaults();
        if (!CollectionUtils.isEmpty(defaults)) {
            factory.setDefaults(defaults);
        }

        WebSecurityManager wsm = (WebSecurityManager) factory.getInstance();
        Map<String, ?> beans = factory.getBeans();
        if (!CollectionUtils.isEmpty(beans)) {
            this.objects.putAll(beans);
        }

        return wsm;
    }

    @Override
    protected FilterChainResolver createFilterChainResolver() {
        FilterChainResolver resolver = null;

        Ini ini = getIni();

        if (!CollectionUtils.isEmpty(ini)) {
            Factory<FilterChainResolver> factory = (Factory<FilterChainResolver>) this.objects.get(FILTER_CHAIN_RESOLVER_NAME);
            if (factory instanceof IniFactorySupport) {
                IniFactorySupport iniFactory = (IniFactorySupport) factory;
                iniFactory.setIni(ini);
                iniFactory.setDefaults(this.objects);
            }
            resolver = factory.getInstance();
        }

        return resolver;
    }
}
