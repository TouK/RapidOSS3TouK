package auth
/**
 * Created by IntelliJ IDEA.
 * User: mustafa sener
 * Date: Dec 17, 2008
 * Time: 3:21:02 PM
 * To change this template use File | Settings | File Templates.
 */
class RsUserOperations extends com.ifountain.rcmdb.domain.operation.AbstractDomainOperation
{
    public static RsUser createUser(Map params)
    {
        return createUser(params, []);
    }

    public static RsUser createUser(Map params, List groups)
    {
        if(params == null)
        {
            throw new Exception("No user props specified");
        }

        List groupsToBeAssigned = getGroupsFromRepository(groups)
        params["groups"] = groupsToBeAssigned;
        RsUser rsUser = RsUser.add(params);
        return rsUser;
    }

    private static List getGroupsFromRepository(List groups)
    {
        def groupsToBeAssigned = [];
        groups.each{groupObject->
            def groupName = groupObject;
            if(groupObject instanceof Group)
            {
                groupName = groupObject.name;
            }
            Group group = Group.get(name:groupName);
            if(group == null)
            {
                throw new Exception("Could not created user since Group ${groupName} does not exist.");
            }
            groupsToBeAssigned.add(group);
        }
        return groupsToBeAssigned;
    }

    public void addToGroups(List groups)
    {
        List groupsToBeAssigned = getGroupsFromRepository(groups)
        addRelation(groups:groupsToBeAssigned);
    }

    public void removeFromGroups(List groups)
    {
        List groupsToBeAssigned = getGroupsFromRepository(groups)
        removeRelation(groups:groupsToBeAssigned);
    }
}