package auth

import org.jsecurity.crypto.hash.Sha1Hash
import com.ifountain.rcmdb.exception.MessageSourceException
import com.ifountain.rcmdb.execution.ExecutionContextManager
import com.ifountain.rcmdb.execution.ExecutionContext
import com.ifountain.rcmdb.util.RapidCMDBConstants
import com.ifountain.rcmdb.domain.util.ControllerUtils
import org.codehaus.groovy.grails.commons.ApplicationHolder


import org.jsecurity.authc.IncorrectCredentialsException
import org.jsecurity.authc.UnknownAccountException
/**
* Created by IntelliJ IDEA.
* User: mustafa sener
* Date: Dec 17, 2008
* Time: 3:21:02 PM
* To change this template use File | Settings | File Templates.
*/
class RsUserOperations extends com.ifountain.rcmdb.domain.operation.AbstractDomainOperation
{
    public static List getChannelTypes()
    {
        return ["email"];
    }
    public static List getEditableChannelTypes()
    {
        return ["email"];
    }
    
    public static def getAuthenticationType()
    {
        return "local";
    }
    public static RsUser authenticateUser(String username,String password)
    {
        def authLogPrefix="User Authentication : ";
        getLogger().info(authLogPrefix+"Authenticating User '${username}'");

        String authenticationType = RsUser.getAuthenticationType();

        // Get the user with the given username. If the user is not found than exception is thrown
        def user = RsUser.get(username:username)
        if (!user) {
            getLogger().warn(authLogPrefix+"No account found for user '${username}'");
            throw new UnknownAccountException("No account found for user ${username}");
        }

        username = user.username;
        getLogger().info(authLogPrefix+"Found user '${user.username}' in Repository");
        


        //do ldap authentication  
        if (authenticationType == "ldap" && username != RsUser.RSADMIN)
        {
            def ldapInformation = user.retrieveLdapInformation();
            if (ldapInformation == null)
            {
                getLogger().warn(authLogPrefix+"Ldap Information could not be found for '${username}'");
                throw new UnknownAccountException("Ldap Information could not be found for '${username}'");
            }            
            authenticateWithLdap(ldapInformation.ldapConnection,ldapInformation.userdn,password,username);
        }
        else //do local authentication
        {
            // Now check the user's password against the hashed value stored in the database.
            if (!user.isPasswordSame(password)) {
                getLogger().warn(authLogPrefix+"Invalid password for user '${username}'");
                throw new IncorrectCredentialsException("Invalid password for user '${username}'");
            }
        }
        getLogger().info(authLogPrefix+"Authentication successfully done for user '${username}' ");

        return user;
    }
    private static void authenticateWithLdap(ldapConnection,String ldapUserdn,String ldapPassword,String username)
    {
        def authLogPrefix="User Authentication : ";
        if (ldapConnection == null)
        {
            getLogger().warn(authLogPrefix+"LdapInformation is not bound with an LdapConnection for user '${username}'");
            throw new UnknownAccountException("LdapInformation is not bound with an LdapConnection for user '${username}'");
        }

        getLogger().info(authLogPrefix+"Authenticating User '${username}' against Ldap");
        if (!ldapConnection.checkAuthentication(ldapUserdn, ldapPassword))
        {
            getLogger().warn(authLogPrefix+"Ldap Authentication failed for user '${username}'");
            throw new IncorrectCredentialsException("Invalid Ldap password for user '${username}'");
        }
    }
    def beforeDelete()
    {
       if(username.equalsIgnoreCase(getCurrentUserName()))
       {
           throw new Exception("Can not delete your own account");
       }
       if(username.equalsIgnoreCase(RsUser.RSADMIN))
       {
           throw new Exception("Can not delete user ${RsUser.RSADMIN}");
       }
    }
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

            params.groups = getGroupsFromRepository(params.groups);
        }

        user.update(params);
        return user;
    }
    private static void restoreOldData(oldProperties)
    {
        oldProperties.each {object, oldObjectProperties ->
            object.update(oldObjectProperties);
        }
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
                throw new Exception("Could not create user since Group ${groupName} does not exist.");
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


    def addChannelInformation(channelParams) {
        def channelInformation = ChannelUserInformation.add(userId: id, type:channelParams.type, destination: channelParams.destination)
        if(!channelInformation.hasErrors())
        {
            addRelation(userInformations: channelInformation);
        }
        return channelInformation;
    }

    def retrieveChannelInformation(channelType) {
        return ChannelUserInformation.get(userId: id, type: channelType);
    }



    def addChannelInformationsAndRollBackIfErrorOccurs(channelInformationList)
    {
        def oldInformationProperties=[:];
        //save all old destinations
        ChannelUserInformation.searchEvery("userId:${id}").each{ userInfo ->
             oldInformationProperties[userInfo.id]=ControllerUtils.backupOldData(userInfo, ["destination":""])
        }

        def errorOccured=false;

        def addedInformations=addChannelInformations(channelInformationList);

        //add all informations and track if error occured
        addedInformations.each{ addedInfo ->
            if(addedInfo.hasErrors())
            {
                errorOccured=true;
            }
        }

        //roll back the information adds / updates if error occured
        if(errorOccured)
        {
            addedInformations.each{ addedInfo ->
                if(!addedInfo.hasErrors())  //if has error no change is done
                {
                    //if information exists earlier update it else remove it
                    def oldProperties=oldInformationProperties[addedInfo.id];
                    if(oldProperties!=null)
                    {
                        addedInfo.update(oldProperties);
                    }
                    else
                    {
                        addedInfo.remove();
                    }
                }
            }
        }

        return addedInformations;
    }


    def addChannelInformations(channelInformationList)
    {
        def addedInformations=[];
        channelInformationList.each{ channelParams ->
            addedInformations.add(addChannelInformation(channelParams));
        }
        return addedInformations;
    }

    def retrieveLdapInformation() {
        return LdapUserInformation.get(userId: id, type: "ldap");
    }

    def addLdapInformation(params) {
        params.userId = id;
        params.type = "ldap"
        def ldapInformation = LdapUserInformation.add(params)
        if(!ldapInformation.hasErrors())
        {
            addRelation(userInformations: ldapInformation);
        }
        return ldapInformation;
    }

   def hasRole(roleName) {
        def res = groups.findAll {it.role?.name == roleName};
        return res.size() > 0
    }

    def hasAllRoles(roleNames)
    {
        int numberOfFoundRoles = 0;
        def groupList=groups;

        roleNames.each {String role ->
            boolean found=false;
            groupList.each {group ->
                if (role == group.role?.name && !found)
                {
                    numberOfFoundRoles++;
                    found=true;
                }
            }

        }

        return numberOfFoundRoles == roleNames.size()
    }
}