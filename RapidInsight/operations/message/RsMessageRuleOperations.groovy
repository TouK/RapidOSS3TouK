package message

import auth.Role
import auth.RsUser
import com.ifountain.rcmdb.util.DataStore
import com.ifountain.annotations.HideProperty
import com.ifountain.rcmdb.util.RapidCMDBConstants

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: May 18, 2009
* Time: 3:50:37 PM
* To change this template use File | Settings | File Templates.
*/
class RsMessageRuleOperations extends com.ifountain.rcmdb.domain.operation.AbstractDomainOperation {

    public static List getDestinations() {
        //// Match destination type with user channel information type where user destination is stored ////
        def destinationMapping = [];

        getConfiguredDestinationNames().each {destination ->
            destinationMapping.add([name: destination, channelType: destination]);
        }

        //configuredDestinationNames destinations from connectors can be modified
        //destinationMapping.add([name:"email",channelType:"email"]);
        return destinationMapping;
    }

    public static def getConfiguredDestinationNames()
    {
        def configuredDestinationNames = DataStore.get(RapidCMDBConstants.CONFIGURED_DESTINATIONS_CACHE_KEY_NAME);
        if (configuredDestinationNames == null)
        {
            cacheConnectorDestinationNames();
            configuredDestinationNames = DataStore.get(RapidCMDBConstants.CONFIGURED_DESTINATIONS_CACHE_KEY_NAME);
        }
        return configuredDestinationNames;
    }
    public static void setConfiguredDestinationNames(names)
    {
        DataStore.put(RapidCMDBConstants.CONFIGURED_DESTINATIONS_CACHE_KEY_NAME, names);
    }
    public static void cacheConnectorDestinationNames()
    {
        def cachedDestinationNames = [];

        def destinationConnectors = connector.NotificationConnector.getPropertyValues("showAsDestination:true", ["name"], [sort: "name", order: "asc"]);
        if (destinationConnectors.size() > 0)
        {
            cachedDestinationNames.addAll(destinationConnectors.name);
        }
        setConfiguredDestinationNames(cachedDestinationNames);
    }
    public static def getDestination(String destinationType)
    {
        return getDestinations().find {it.name == destinationType};
    }
    public static def getDestinationChannelType(String destinationType)
    {
        return getDestination(destinationType)?.channelType;
    }


    @HideProperty public static List getDestinationNames() {
        def destinationConfig = getDestinations();
        return destinationConfig.name;
    }

    @HideProperty public static List getDestinationGroups()
    {
        return [
                [name: "Channel", destinationNames: getChannelDestinationNames()],
                [name: "Non-Channel", destinationNames: getNonChannelDestinationNames()]
        ]
    }


    public static List getDestinationGroupsForUser(String username)
    {
        def groups = getDestinationGroups();

        def user = auth.RsUser.get(username: username)
        if (user == null)
            throw new Exception("No user defined with username '${username}'");

        def isAdmin = auth.RsUser.hasRole(username, Role.ADMINISTRATOR);
        if (!isAdmin)
        {
            groups.remove(1);
        }
        return groups;
    }

    @HideProperty public static List getChannelDestinationNames() {
        return RsMessageRuleOperations.getDestinations().findAll {isChannelType(it.channelType)}.name;
    }
    @HideProperty public static List getNonChannelDestinationNames()
    {
        return RsMessageRuleOperations.getDestinations().findAll {!isChannelType(it.channelType)}.name;
    }

    public static boolean isChannelType(String channelType)
    {
        if (channelType != null && channelType != "")
        {
            return true;
        }
        return false;
    }

    public static def getUserDestinationForChannel(RsUser user, String channelType)
    {
        return user.retrieveChannelInformation(channelType)?.destination;
    }

    public static RsMessageRule addMessageRuleForUser(params, String username)
    {
        def createParams = prepareAndValidateParamsForUser(params, username);

        def messageRule = RsMessageRule.add(createParams);
        return messageRule;
    }


    public static RsMessageRule updateMessageRuleForUser(RsMessageRule messageRule, params, String username)
    {
        def updateParams = prepareAndValidateParamsForUser(params, username);

        messageRule.update(updateParams);
        return messageRule;
    }

    public static void validateUserDestinationForChannel(RsUser user, String destination, String channelType)
    {
        def isAdmin = auth.RsUser.hasRole(user.username, Role.ADMINISTRATOR);

        if (isChannelType(channelType))
        {
            if (destination == null || destination == "")
            {
                throw new Exception("${user.username}'s destination for ${channelType} is not defined")
            }
        }
        else
        {
            if (!isAdmin)
            {
                throw new Exception("${user.username} does not have permission to create rule with Non-Channel destination");
            }
        }

    }
    private static def prepareAndValidateParamsForUser(ruleParams, String username)
    {
        def params = [:];
        params.putAll(ruleParams);

        def user = auth.RsUser.get(username: username)
        if (user == null)
            throw new Exception("No user defined with username '${username}'");

        params.userId = user.id

        def channelType = getDestinationChannelType(params.destinationType)
        def destination = getUserDestinationForChannel(user, channelType);
        validateUserDestinationForChannel(user, destination, channelType);

        return params;
    }

    public static List getCalendars(String username) {
        return RsMessageRuleCalendar.searchEvery("username:${username.exactQuery()} OR (username:${RsUser.RSADMIN} AND isPublic:true)")
    }

}