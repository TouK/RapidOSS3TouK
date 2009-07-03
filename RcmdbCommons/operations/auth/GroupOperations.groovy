package auth

import com.ifountain.rcmdb.auth.SegmentQueryHelper

/**
* Created by IntelliJ IDEA.
* User: mustafa sener
* Date: Dec 17, 2008
* Time: 5:16:08 PM
* To change this template use File | Settings | File Templates.
*/
class GroupOperations extends com.ifountain.rcmdb.domain.operation.AbstractDomainOperation 
{
    def afterDelete(){
        SegmentQueryHelper.getInstance().removeGroupFilters(name);
    }

     def afterInsert(){
        SegmentQueryHelper.getInstance().calculateGroupFilters(name);
    }

    def afterUpdate(params){
        if(params.updatedProps.containsKey("segmentFilter") || params.updatedProps.containsKey("segmentFilterType")){
            SegmentQueryHelper.getInstance().calculateGroupFilters(name);    
        }
    }

    public static Group createGroup(Map groupProps)
    {
        return createGroup(groupProps, []);
    }
    public static Group createGroup(Map groupProps, List users)
    {
        if(groupProps == null)
        {
            throw new Exception("No group props specified");
        }
        List usersToBeAdded = getUsersFromRepository(users);
        groupProps.users = usersToBeAdded;
        return Group.add(groupProps);    
    }

    private static List getUsersFromRepository(List users)
    {
        def usersToBeAdded = [];
        users.each{userObject->
            def username = userObject;
            if(userObject instanceof RsUser)
            {
                username = userObject.username;
            }
            RsUser rsUser = RsUser.get(username:username);
            if(rsUser == null)
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
        addRelation(users:usersToBeAdded);
    }

    public void removeUsers(List userList)
    {
        List usersToBeRemoved = getUsersFromRepository(userList);
        removeRelation(users:usersToBeRemoved);
    }

    public void assignRole(role)
    {
        addRelation(role:getRole(role));
    }

    public void removeRole(role)
    {
        removeRelation(role:getRole(role));
    }

    private static getRole(role)
    {
        String roleName = role;
        if(role instanceof Role)
        {
            roleName = role.name;
        }
        def roleFromRi = Role.get(name:roleName);
        if(roleFromRi == null)
        {
            throw new Exception("Role ${roleName} does not exist");
        }
        return roleFromRi;
    }
}