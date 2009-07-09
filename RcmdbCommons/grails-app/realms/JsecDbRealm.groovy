/* 
* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be
* noted in a separate copyright notice. All rights reserved.
* This file is part of RapidCMDB.
*
* RapidCMDB is free software; you can redistribute it and/or modify
* it under the terms version 2 of the GNU General Public License as
* published by the Free Software Foundation. This program is distributed
* in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
* even the implied warranty of MERCHANTABILITY or FITNESS FOR A
* PARTICULAR PURPOSE. See the GNU General Public License for more
* details.
*
* You should have received a copy of the GNU General Public License
* along with this program; if not, write to the Free Software
* Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
* USA.
*/

import auth.RsUser
import org.jsecurity.authc.AccountException
import org.jsecurity.authc.IncorrectCredentialsException
import org.jsecurity.authc.SimpleAccount
import org.jsecurity.authc.UnknownAccountException
import auth.Role
import connection.LdapConnection
import auth.LdapUserInformation
import org.codehaus.groovy.grails.commons.ApplicationHolder
import javax.naming.NamingException

class JsecDbRealm {
    static authTokenClass = org.jsecurity.authc.UsernamePasswordToken

    def credentialMatcher

    def authenticate(authToken) {
        log.info "Attempting to authenticate ${authToken.username} in DB realm..."
        def username = authToken.username

        // Null username is invalid
        if (username == null) {
            throw new AccountException('Null usernames are not allowed by this realm.')
        }


        // Get the user with the given username. If the user is not
        // found, then they don't have an account and we throw an
        // exception.

        def user = RsUser.findByUsername(username)
        if (!user) {
            throw new UnknownAccountException("No account found for user [${username}]")
        }
        username = user.username;
        log.info "Found user '${user.username}' in DB"

        def account = new SimpleAccount(username, user.passwordHash, "JsecDbRealm")
        String authenticationType = ApplicationHolder.application.config.toProperties()["rapidCMDB.authentication.type"];


        if (authenticationType == "ldap" && username != "rsadmin")
        {
            def ldapInformation = user.retrieveLdapInformation();
            if (ldapInformation != null)
            {

                def ldapConnection = ldapInformation.ldapConnection

                log.info 'gonna try ldap auth from realm'
                if (!ldapConnection.checkAuthentication(ldapInformation.userdn, new String(authToken.password)))
                {
                    log.info 'Invalid password (DB realm - LDAP)'
                    throw new IncorrectCredentialsException("Invalid password for user '${username}'")
                }
            }
            else
            {
                throw new UnknownAccountException("Ldap Information could not be found for [${username}]")
            }
        }
        else //do local authentication
        {
            // Now check the user's password against the hashed value stored
            // in the database.
            if (!credentialMatcher.doCredentialsMatch(authToken, account)) {
                log.info 'Invalid password (DB realm)'
                throw new IncorrectCredentialsException("Invalid password for user '${username}'")
            }
        }

        return account
    }
    def hasRole(principal, roleName) {
        def user = RsUser.get(username: principal);
        def res = user.groups.findAll {it.role?.name == roleName};
        return res.size() > 0
    }

    def hasAllRoles(principal, roles) {
        def user = RsUser.get(username: principal);
        int numberOfFoundRoles = 0;
        roles.each {Role role ->
            user.groups.each {group ->
                if (role.name == group.role?.name)
                {
                    numberOfFoundRoles++;
                    return;
                }
            }

        }

        return numberOfFoundRoles == roles.size()
    }

    def isPermitted(principal, requiredPermission) {
        // Does the user have the given permission directly associated
        // with himself?
        //
        // First find all the permissions that the user has that match
        // the required permission's type and project code.
        def user = RsUser.get(username: principal);
        def permissions = user.permissionRelations.findAll {it.permission.type == requiredPermission.class.name}

        // Try each of the permissions found and see whether any of
        // them confer the required permission.
        def retval = permissions?.find {rel ->
            // Create a real permission instance from the database
            // permission.
            def perm = null
            def constructor = findConstructor(rel.permission.type)
            if (constructor.parameterTypes.size() == 2) {
                perm = constructor.newInstance(rel.target, rel.actions)
            }
            else if (constructor.parameterTypes.size() == 1) {
                perm = constructor.newInstance(rel.target)
            }
            else {
                log.error "Unusable permission: ${rel.permission.type}"
                return false
            }

            // Now check whether this permission implies the required
            // one.
            if (perm.implies(requiredPermission)) {
                // User has the permission!
                return true
            }
            else {
                return false
            }
        }

        if (retval != null) {
            // Found a matching permission!
            return true
        }

        // If not, does he gain it through a role?
        //
        // First, find the roles that the user has.
        def roles = user.groups;

        // If the user has no roles, then he obviously has no permissions
        // via roles.
        if (groups.isEmpty()) return false

        def results = [];
        groups.each {group ->
            if (group.role != null) {
                group.role.permissionRelations.each {rolePermissionRelation ->
                    if (rolePermissionRelation.permission.type == requiredPermission.class.name)
                    {
                        results.add(rolePermissionRelation);
                    }
                }
            }
        }


        // There may be some duplicate entries in the results, but
        // at this stage it is not worth trying to remove them. Now,
        // create a real permission from each result and check it
        // against the required one.
        retval = results.find {rel ->
            def perm = null
            def constructor = findConstructor(rel.permission.type)
            if (constructor.parameterTypes.size() == 2) {
                perm = constructor.newInstance(rel.target, rel.actions)
            }
            else if (constructor.parameterTypes.size() == 1) {
                perm = constructor.newInstance(rel.target)
            }
            else {
                log.error "Unusable permission: ${rel.permission.type}"
                return false
            }

            // Now check whether this permission implies the required
            // one.
            if (perm.implies(requiredPermission)) {
                // User has the permission!
                return true
            }
            else {
                return false
            }
        }

        if (retval != null) {
            // Found a matching permission!
            return true
        }
        else {
            return false
        }
    }

    def findConstructor(className) {
        // Load the required permission class.
        def clazz = this.class.classLoader.loadClass(className)

        // Check the available constructors. If any take two
        // string parameters, we use that one and pass in the
        // target and actions string. Otherwise we try a single
        // parameter constructor and pass in just the target.
        def preferredConstructor = null
        def fallbackConstructor = null
        clazz.declaredConstructors.each {constructor ->
            def numParams = constructor.parameterTypes.size()
            if (numParams == 2) {
                if (constructor.parameterTypes[0].equals(String) &&
                        constructor.parameterTypes[1].equals(String)) {
                    preferredConstructor = constructor
                }
            }
            else if (numParams == 1) {
                if (constructor.parameterTypes[0].equals(String)) {
                    fallbackConstructor = constructor
                }
            }
        }

        return (preferredConstructor != null ? preferredConstructor : fallbackConstructor)
    }
}
