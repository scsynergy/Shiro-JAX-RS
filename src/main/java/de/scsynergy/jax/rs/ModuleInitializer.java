package de.scsynergy.jax.rs;

import java.util.EnumSet;
import javax.servlet.DispatcherType;
import static javax.servlet.DispatcherType.ASYNC;
import static javax.servlet.DispatcherType.ERROR;
import static javax.servlet.DispatcherType.FORWARD;
import static javax.servlet.DispatcherType.INCLUDE;
import static javax.servlet.DispatcherType.REQUEST;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.annotation.WebListener;
import org.apache.shiro.config.Ini;
import org.apache.shiro.util.ClassUtils;
import org.apache.shiro.web.env.EnvironmentLoaderListener;
import org.apache.shiro.web.env.WebEnvironment;

/**
 * Every module should provide its own language pack; including a means of
 * initially storing the language pack's key value pairs to the database. In
 * addition every module needs to add to the basic system's language pack the
 * labels and URLs which are to be presented in the navigation bar which is
 * rendered by the basic system.
 *
 * Classes extending this class need to apply the @WebListener annotation to the
 * class.
 *
 * @author rf
 */
@WebListener
public class ModuleInitializer extends EnvironmentLoaderListener {

    public ModuleInitializer() {
    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        super.contextInitialized(sce);
        initializeShiro(sce);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        super.contextDestroyed(sce);
    }

    @Override
    protected void customizeEnvironment(WebEnvironment environment) {
        String iniFile = """
                                 [main]
                                 sessionManager = org.apache.shiro.web.session.mgt.DefaultWebSessionManager
                                 securityManager.sessionManager = $sessionManager
                                 sessionDAO = org.apache.shiro.session.mgt.eis.EnterpriseCacheSessionDAO
                                 securityManager.sessionManager.sessionDAO = $sessionDAO
                                 securityManager.cacheManager = $cacheManagerInfinispan
                                 
                                 cookie = org.apache.shiro.web.servlet.SimpleCookie
                                 cookie.name = SsoCookie
                                 cookie.path = /
                                 cookie.secure = true
                                 cookie.sameSite = NONE
                                 cookie.httpOnly = true
                                 securityManager.sessionManager.sessionIdCookie = $cookie
                                 
                                 #credentialsMatcher = org.apache.shiro.authc.credential.Sha512CredentialsMatcher
                                 #credentialsMatcher.storedCredentialsHexEncoded = false
                                 #credentialsMatcher.hashIterations = 500000
                                 #mongoRealm.credentialsMatcher = $credentialsMatcher
                                                                  
                                 firstStrategy = org.apache.shiro.authc.pam.FirstSuccessfulStrategy
                                 securityManager.authenticator.authenticationStrategy = $firstStrategy
                                 securityManager.realms = $mongoRealm
                                 
                                 authc = org.apache.shiro.web.filter.authc.PassThruAuthenticationFilter
                                 authc.loginUrl = /login.xhtml
                                 authc.successUrl = /welcome.xhtml
                                 
                                 #invalidRequest = org.apache.shiro.web.filter.InvalidRequestFilter
                                 #invalidRequest.enabled = true
                                 
                                 logout = org.apache.shiro.web.filter.authc.LogoutFilter
                                 logout.redirectUrl = /login.xhtml
                                 
                                 [urls]
                                 /login.xhtml = ssl[443], authc
                                 /logout = logout
                                 ### the next line is needed to retrieve jsf resources from jar library
                                 /javax.faces.resource/** = ssl[443], anon
                                 /rest/wopi/files/cors = noSessionCreation, ssl[443], anon
                                 /rest/gitVersion = noSessionCreation, ssl[443], anon
                                 /rest/version = noSessionCreation, ssl[443], anon
                                 /rest/** = noSessionCreation, ssl[443], authcBasic
                                 /** = ssl[443], authc                                 
                                 """;
        Ini ini = new Ini();
        ini.load(iniFile);
        ((CdiIniWebEnvironment) environment).setIni(ini);
    }

    @Override
    protected WebEnvironment determineWebEnvironment(ServletContext servletContext) {
        return (WebEnvironment) ClassUtils.newInstance(CdiIniWebEnvironment.class);
    }

    private void initializeShiro(ServletContextEvent sce) {
        FilterRegistration.Dynamic dynamic = sce.getServletContext().addFilter("ShiroFilter", "org.apache.shiro.web.servlet.ShiroFilter");
        dynamic.setAsyncSupported(true);
        EnumSet<DispatcherType> enumSet = EnumSet.of(REQUEST, FORWARD, INCLUDE, ERROR, ASYNC);
        dynamic.addMappingForUrlPatterns(enumSet, true, "/*");
    }
}
