package message

import auth.Role
import auth.RsUser
import com.ifountain.annotations.HideProperty

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
        return [
                [name:"email",channelType:"email"]
               ]
    }

    public static def getDestination(String destinationType)
    {
        return getDestinations().find{it.name==destinationType};
    }
    public static def getDestinationChannelType(String destinationType)
    {
        return getDestination(destinationType)?.channelType;
    }

    @HideProperty public static List getDestinationNames() {
        def destinationConfig = getDestinations();
        return destinationConfig.name;
    }

    @HideProperty public static List getDesnitationGroups()
    {
        return  [
                 [name:"Channel",destinationNames:getChannelDestinationNames()],
                 [name:"Non-Channel",destinationNames:getNonChannelDestinationNames()]
                ]
    }

    public static List getDesnitationGroupsForUser(String username)
    {
        def groups=getDesnitationGroups();

        def user = auth.RsUser.get(username: username)
        if(user == null)
            throw new Exception("No user defined with username '${username}'");

        def isAdmin = user.hasRole(Role.ADMINISTRATOR);
        if(!isAdmin)
        {
           groups.remove(1);
        }
        return groups;
    }

    @HideProperty public static List getChannelDestinationNames(){
       return RsMessageRuleOperations.getDestinations().findAll{isChannelType(it.channelType)}.name;
    }
    @HideProperty public static List getNonChannelDestinationNames()
    {
       return RsMessageRuleOperations.getDestinations().findAll{!isChannelType(it.channelType)}.name;
    }

    public static boolean isChannelType(String channelType)
    {
        if(channelType != null && channelType != "")
        {
            return true;
        }
        return false;
    }

    public static def getUserDestinationForChannel(RsUser user,String channelType)
    {
        return user.retrieveChannelInformation(channelType)?.destination;
    }

    public static RsMessageRule addMessageRuleForUser(params,String username)
    {
        def createParams=prepareAndValidateParamsForUser(params,username);

        def messageRule=RsMessageRule.add(createParams);
        return messageRule;
    }


    public static RsMessageRule updateMessageRuleForUser(RsMessageRule messageRule,params,String username)
    {
        def updateParams=prepareAndValidateParamsForUser(params,username);

        messageRule.update(updateParams);
        return messageRule;
    }

    public static void validateUserDestinationForChannel(RsUser user,String destination,String channelType)
    {
        def isAdmin = user.hasRole(Role.ADMINISTRATOR);

        if(isChannelType(channelType))
        {
            if(destination == null ||destination == "")
            {
                throw new Exception("${user.username}'s destination for ${channelType} is not defined")
            }
        }
        else
        {
            if(!isAdmin)
            {
                throw new Exception("${user.username} does not have permission to create rule with Non-Channel destination");
            }
        }

    }
    private static def prepareAndValidateParamsForUser(ruleParams,String username)
    {
        def params=[:];
        params.putAll(ruleParams);
        
        def user = auth.RsUser.get(username: username)
        if(user == null)
            throw new Exception("No user defined with username '${username}'");

        params.userId = user.id

        def channelType=getDestinationChannelType(params.destinationType)
        def destination = getUserDestinationForChannel(user, channelType);
        validateUserDestinationForChannel(user,destination,channelType);

        return params;
    }

}