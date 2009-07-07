package auth

import org.jsecurity.crypto.hash.Sha1Hash
import com.ifountain.rcmdb.exception.MessageSourceException
import com.ifountain.rcmdb.execution.ExecutionContextManager
import com.ifountain.rcmdb.execution.ExecutionContext
import com.ifountain.rcmdb.util.RapidCMDBConstants

/**
* Created by IntelliJ IDEA.
* User: mustafa sener
* Date: Dec 17, 2008
* Time: 3:21:02 PM
* To change this template use File | Settings | File Templates.
*/
class RsUserOperations extends com.ifountain.rcmdb.domain.operation.AbstractDomainOperation
{

    public static String hashPassword(password)
    {
        return new Sha1Hash(password).toHex();
    }
    public boolean isPasswordSame(passwordParam)
    {
        return passwordHash == hashPassword(passwordParam);
    }
    public static RsUser updateUser(user, params)
    {
        if (params.password != null)
        {
            params.passwordHash = hashPassword(params.password);
        }

        if (params.groups != null)
        {
            if (params.groups.isEmpty())
            {
                throw new MessageSourceException("no.group.specified", [] as Object[]);
            }
        }

        user.update(params);
        user.addEmail(params.email);

        return user;
    }
    public static RsUser addUser(params)
    {
        return _addUser(params,false);
    }
    public static RsUser addUniqueUser(params)
    {
        return _addUser(params,true);
    }
    private static RsUser _addUser(params,boolean addUnique)
    {
        params.passwordHash = hashPassword(params.password);

        def rsUser = null;


        if (params.groups == null || params.groups.isEmpty())
        {
            throw new MessageSourceException("no.group.specified", [] as Object[]);
        }

        params.groups = getGroupsFromRepository(params.groups)

        if(addUnique)
        {
            rsUser = RsUser.addUnique(params);
        }
        else
        {
            rsUser = RsUser.add(params);
        }

        if (!rsUser.hasErrors()) {
            rsUser.addEmail(params.email);
        }

        return rsUser;
    }


    private static List getGroupsFromRepository(List groups)
    {
        def groupsToBeAssigned = [];
        groups.each {groupObject ->
            def groupName = groupObject;
            if (groupObject instanceof Group)
            {
                groupName = groupObject.name;
            }
            Group group = Group.get(name: groupName);
            if (group == null)
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
        addRelation(groups: groupsToBeAssigned);
    }

    public void removeFromGroups(List groups)
    {
        List groupsToBeAssigned = getGroupsFromRepository(groups)
        removeRelation(groups: groupsToBeAssigned);
    }


    public static String getCurrentUserName()
    {
        ExecutionContext context = ExecutionContextManager.getInstance().getExecutionContext();
        if (context != null)
        {
            def currentuser = context[RapidCMDBConstants.USERNAME];
            if (currentuser == null)
            {
                currentuser = "system";
            }
            return currentuser;
        }
        else
        {
            return "system";
        }
    }

    def retrieveLdapInformation() {
        return LdapUserInformation.get(userId: id, type: "ldap");
    }
    def addLdapInformation(params) {
        params.userId = id;
        params.type = "ldap"
        def ldapInformation = LdapUserInformation.add(params)
        addRelation(userInformations: ldapInformation);
        return ldapInformation;
    }

    def addEmail(email) {
        def emailInformation = ChannelUserInformation.add(userId: id, type: "email", destination: email)
        addRelation(userInformations: emailInformation);
        return emailInformation;
    }

    def retrieveEmail() {
        def emailInformation = ChannelUserInformation.get(userId: id, type: "email");
        if (emailInformation != null) {
            return emailInformation.destination;
        }
        return "";
    }
}