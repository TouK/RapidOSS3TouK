package auth
/**
 * Created by IntelliJ IDEA.
 * User: mustafa sener
 * Date: Dec 17, 2008
 * Time: 5:16:08 PM
 * To change this template use File | Settings | File Templates.
 */
class GroupOperations extends com.ifountain.rcmdb.domain.operation.AbstractDomainOperation 
{
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
        groupProps.users = usersToBeAdded;
        return Group.add(groupProps);    
    }
}