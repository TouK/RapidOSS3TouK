package auth

import application.RapidApplication
import com.ifountain.rcmdb.auth.UserConfigurationSpace
import com.ifountain.rcmdb.domain.util.ControllerUtils
import com.ifountain.rcmdb.exception.MessageSourceException
import org.jsecurity.authc.AccountException
import org.jsecurity.crypto.hash.Sha1Hash
import com.ifountain.rcmdb.util.ExecutionContextManagerUtils
import com.ifountain.rcmdb.util.DataStore
import com.ifountain.rcmdb.util.RapidCMDBConstants
import com.ifountain.annotations.HideProperty

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
        def channelTypes=[];
        channelTypes.addAll(getConfiguredDestinationNames());
        //channelTypes.add("email");     //existing destinations from connectors can be added/removed
        return channelTypes;
    }
    public static List getEditableChannelTypes()
    {
        def channelTypes=[];
        channelTypes.addAll(getConfiguredDestinationNames());
        //channelTypes.add("email");     // existing destinations from connectors can be added/removed
        return channelTypes;
    }
    public static def getConfiguredDestinationNames()
    {
        def configuredDestinationNames=DataStore.get(RapidCMDBConstants.CONFIGURED_DESTINATIONS_CACHE_KEY_NAME);
        if(configuredDestinationNames==null)
        {
           configuredDestinationNames=[];               
        }
        return configuredDestinationNames;
    }

    public static def getAuthenticator()
    {
        return "auth.RsUserLocalAuthenticator";
    }
    public static RsUser authenticateUser(params)
    {
        String username = params.login;
        String loginToken = params.loginToken;

        if (username)
        {
            String selectedAuthenticator = RsUser.getAuthenticator();
            if(username == RsUser.RSADMIN)
            {
               selectedAuthenticator="auth.RsUserLocalAuthenticator";
            }                
            return RapidApplication.getUtility(selectedAuthenticator).authenticateUser(params);
        }
        else if (loginToken)
        {
            return RapidApplication.getUtility("auth.RsUserTokenAuthenticator").authenticateUser(params);
        }
        else
        {
            throw new AccountException('Login or LoginToken must be specified for authentication.');
        }
    }

    //Before & After Triggers
    def beforeDelete()
    {
        if (username.equalsIgnoreCase(getCurrentUserName()))
        {
            throw new Exception("Can not delete your own account");
        }
        if (username.equalsIgnoreCase(RsUser.RSADMIN))
        {
            throw new Exception("Can not delete user ${RsUser.RSADMIN}");
        }
    }

	def afterInsert()
    {
        UserConfigurationSpace.getInstance().userAdded(this.domainObject);
    }
	def afterUpdate(params)
    {
        if (params.updatedProps.containsKey("username")) {
            UserConfigurationSpace.getInstance().userRemoved(params.updatedProps.username);
        }
        UserConfigurationSpace.getInstance().userAdded(this.domainObject);
    }
	def afterDelete()
    {
        UserConfigurationSpace.getInstance().userRemoved(username);
    }
    //Before & After Triggers End
    
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
        return _addUser(params, false);
    }
    public static RsUser addUniqueUser(params)
    {
        return _addUser(params, true);
    }
    private static RsUser _addUser(params, boolean addUnique)
    {
        params.passwordHash = hashPassword(params.password);

        def rsUser = null;

        if (params.groups == null || params.groups.isEmpty())
        {
            throw new MessageSourceException("no.group.specified", [] as Object[]);
        }

        params.groups = getGroupsFromRepository(params.groups)

        if (addUnique)
        {
            rsUser = RsUser.addUnique(params);
        }
        else
        {
            rsUser = RsUser.add(params);
        }
        return rsUser;
    }
    public static void removeUser(RsUser user) {        
        user.remove();        
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

    public static String getCurrentUserName()
    {
        def currentuser=ExecutionContextManagerUtils.getUsernameFromCurrentContext();
        if(currentuser ==null)
        {
            currentuser = "system";
        }
        return currentuser;
    }


    def addChannelInformation(channelParams) {
        def isDefault = channelParams.isDefault != null ? channelParams.isDefault : false;
        def channelInformation = ChannelUserInformation.add(userId: id, type: channelParams.type, destination: channelParams.destination, isDefault:isDefault)
        if (!channelInformation.hasErrors())
        {
            addRelation(userInformations: channelInformation);
        }
        return channelInformation;
    }

    def retrieveChannelInformation(channelType) {
        return ChannelUserInformation.get(userId: id, type: channelType);
    }
    def retrieveDefaultChannelInformation() {
        return ChannelUserInformation.searchEvery("userId:${id} AND isDefault:true")[0];
    }


    def addChannelInformationsAndRollBackIfErrorOccurs(channelInformationList)
    {
        def oldInformationProperties = [:];
        //save all old destinations
        ChannelUserInformation.searchEvery("userId:${id}").each {userInfo ->
            oldInformationProperties[userInfo.id] = ControllerUtils.backupOldData(userInfo, ["destination": "", "isDefault":""])
        }

        def errorOccured = false;

        def addedInformations = addChannelInformations(channelInformationList);

        //add all informations and track if error occured
        addedInformations.each {addedInfo ->
            if (addedInfo.hasErrors())
            {
                errorOccured = true;
            }
        }

        //roll back the information adds / updates if error occured
        if (errorOccured)
        {
            addedInformations.each {addedInfo ->
                if (!addedInfo.hasErrors()) //if has error no change is done
                {
                    //if information exists earlier update it else remove it
                    def oldProperties = oldInformationProperties[addedInfo.id];
                    if (oldProperties != null)
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
        def addedInformations = [];
        channelInformationList.each {channelParams ->
            addedInformations.add(addChannelInformation(channelParams));
        }
        return addedInformations;
    }

    def retrieveLdapInformation() {
        return LdapUserInformation.get(userId: id, type: "ldap");
    }
    
    @HideProperty def getChannelInformations() {
        return ChannelUserInformation.searchEvery("userId: ${id}");
    }

    def addLdapInformation(params) {
        params.userId = id;
        params.type = "ldap"
        def ldapInformation = LdapUserInformation.add(params)
        if (!ldapInformation.hasErrors())
        {
            addRelation(userInformations: ldapInformation);
        }
        return ldapInformation;
    }

    def static hasRole(uName, roleName) {
        return UserConfigurationSpace.getInstance().hasRole(uName, roleName)
    }

    def static hasAllRoles(uName, roleNames)
    {
        return UserConfigurationSpace.getInstance().hasAllRoles(uName, roleNames)
    }

    def static hasGroup(uName, groupName) {
        return UserConfigurationSpace.getInstance().hasGroup(uName, groupName)
    }

    def static hasAllGroups(uName, groupNames)
    {
        return UserConfigurationSpace.getInstance().hasAllGroups(uName, groupNames)
    }
}