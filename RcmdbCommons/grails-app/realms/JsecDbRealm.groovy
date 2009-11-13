import auth.RsUser
import org.jsecurity.authc.SimpleAccount
import org.jsecurity.authc.UnknownAccountException

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

class JsecDbRealm {
    static authTokenClass = org.jsecurity.authc.UsernamePasswordTokenWithParams

    def credentialMatcher

    def authenticate(authToken) {
        log.info "Attempting to authenticate login:${authToken.username} in DB realm..."

        // Get the user with the given username. If the user is not
        // found, then they don't have an account and we throw an
        // exception.

        def user = RsUser.authenticateUser(authToken.params)
        if (!user) {
            throw new UnknownAccountException("No account found for user [${authToken.username}]")
        }

        def account = new SimpleAccount(user.username, user.passwordHash, "JsecDbRealm");
        return account
    }
    def hasRole(principal, roleName) {
        return RsUser.hasRole(principal, roleName)
    }

    def hasAllRoles(principal, roles) {
        return RsUser.hasAllRoles(principal, roles)
    }

    def isPermitted(principal, requiredPermission) {
        // Does the user have the given permission directly associated
        // with himself?
        //
        // First find all the permissions that the user has that match
        // the required permission's type and project code.
        def user = RsUser.get(username: principal);
        if (!user)
            return false;
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
