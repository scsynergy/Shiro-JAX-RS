package de.scsynergy.jax.rs;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.Permission;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;

/**
 *
 * @author rf
 */
@ShiroIni
public class MongoRealm extends AuthorizingRealm implements Serializable {

    public MongoRealm() {
        super();
    }

    public MongoRealm(CacheManager cacheManager) {
        super(cacheManager);
    }

    public MongoRealm(CredentialsMatcher matcher) {
        super(matcher);
    }

    public MongoRealm(CacheManager cacheManager, CredentialsMatcher matcher) {
        super(cacheManager, matcher);
    }

    @Override
    public boolean supports(AuthenticationToken token) {
        return token != null && token instanceof UsernamePasswordToken && token.getPrincipal() != null;
    }

    /**
     * The method is the same as inside ActiveDirectoryRealm, only there it is
     * nested inside "if (getRolePermissionResolver() == null) { }". Notice that
     * pc.getRealmNames() returns "MongoRealm" but "ActiveDirectoryRealm_6"
     * probably because the default constructor of ActiveDirectoryRealm does not
     * call super() - which might be a bug. Because of this we look up both
     * names as parameter to determine "realms".
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection pc) {
        Set<String> roles = new HashSet<>();
        Set<String> stringPermissions = new HashSet<>();
        Set<Permission> permissions = new HashSet<>();
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        info.setRoles(roles);
        info.setStringPermissions(stringPermissions);
        info.setObjectPermissions(permissions);
        return info;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken at) throws AuthenticationException {
        return new SimpleAuthenticationInfo(at.getPrincipal(), at.getCredentials(), getClass().getName());
    }
}
