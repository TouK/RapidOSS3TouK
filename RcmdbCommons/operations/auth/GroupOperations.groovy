package auth

import com.ifountain.rcmdb.auth.SegmentQueryHelper
import com.ifountain.rcmdb.exception.MessageSourceException
import com.ifountain.rcmdb.auth.UserConfigurationSpace

/**
* Created by IntelliJ IDEA.
* User: mustafa sener
* Date: Dec 17, 2008
* Time: 5:16:08 PM
* To change this template use File | Settings | File Templates.
*/
class GroupOperations extends com.ifountain.rcmdb.domain.operation.AbstractDomainOperation
{
    def beforeDelete()
    {
        if (name.equalsIgnoreCase(RsUser.RSADMIN))
        {
            throw new Exception("Can not delete group ${RsUser.RSADMIN}");
        }
    }


    def afterInsert() {
        SegmentQueryHelper.getInstance().calculateGroupFilters(name);
        UserConfigurationSpace.getInstance().groupAdded(this.domainObject);
    }

    def afterUpdate(params) {
        if (params.updatedProps.containsKey("segmentFilter") || params.updatedProps.containsKey("segmentFilterType")) {
            SegmentQueryHelper.getInstance().calculateGroupFilters(name);
        }

        if (params.updatedProps.containsKey("name")) {
            UserConfigurationSpace.getInstance().groupRemoved(params.updatedProps.name);
        }
        UserConfigurationSpace.getInstance().groupAdded(this.domainObject);
    }
    def afterDelete() {
        SegmentQueryHelper.getInstance().removeGroupFilters(name);
        UserConfigurationSpace.getInstance().groupRemoved(name)
    }


    public static Group addGroup(params)
    {
        return _addGroup(params, false);
    }
    public static Group addUniqueGroup(params)
    {
        return _addGroup(params, true);
    }
    private static Group _addGroup(params, boolean addUnique)
    {
        def group = null;

        if (params.role == null)
        {
            throw new MessageSourceException("no.role.specified", [] as Object[]);
        }

        params.users = getUsersFromRepository(params.users)

        if (addUnique)
        {
            group = Group.addUnique(params);
        }
        else
        {
            group = Group.add(params);
        }
        return group;
    }

    public static Group updateGroup(group, params)
    {
        if (params.users != null)
        {
            params.users = getUsersFromRepository(params.users)
        }
        if (params.containsKey("role"))
        {
            if (params.role == null)
            {
                throw new MessageSourceException("no.role.specified", [] as Object[]);
            }
        }

        group.update(params);
        return group;
    }

    public static void removeGroup(Group group) {
        group.remove();
    }
    private static List getUsersFromRepository(List users)
    {
        def usersToBeAdded = [];
        users.each {userObject ->
            def username = userObject;
            if (userObject instanceof RsUser)
            {
                username = userObject.username;
            }
            RsUser rsUser = RsUser.get(username: username);
            if (rsUser == null)
            {
                throw new Exception("Could not created group since user ${username} does not exist.");
            }
            usersToBeAdded.add(rsUser);
        }
        return usersToBeAdded;
    }

    public void addUsers(List userList)
    {
        List usersToBeAdded = getUsersFromRepository(userList);
        addRelation(users: usersToBeAdded);
        UserConfigurationSpace.getInstance().groupAdded(domainObject);
    }

    public void removeUsers(List userList)
    {
        List usersToBeRemoved = getUsersFromRepository(userList);
        removeRelation(users: usersToBeRemoved);
        UserConfigurationSpace.getInstance().groupAdded(domainObject);
    }

    public void assignRole(role)
    {
        addRelation(role: getRole(role));
        UserConfigurationSpace.getInstance().groupAdded(domainObject);
    }

    public void removeRole(role)
    {
        removeRelation(role: getRole(role));
        UserConfigurationSpace.getInstance().groupAdded(domainObject);
    }

    private static getRole(role)
    {
        String roleName = role;
        if (role instanceof Role)
        {
            roleName = role.name;
        }
        def roleFromRi = Role.get(name: roleName);
        if (roleFromRi == null)
        {
            throw new Exception("Role ${roleName} does not exist");
        }
        return roleFromRi;
    }
}