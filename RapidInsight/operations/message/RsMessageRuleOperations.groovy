package message

import org.jsecurity.SecurityUtils
import auth.Role
import auth.ChannelUserInformation

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
        return [[name:"email",channelType:"email"]]
    }

    public static def getDestination(destinationType)
    {
        return getDestinations().find{it.name==destinationType};
    }
    public static def getDestinationChannelType(destinationType)
    {
        return getDestination(destinationType)?.channelType;
    }

    public static List getDestinationNames() {
        def destinationConfig = getDestinations();
        return destinationConfig.name;
    }


    public static List getChannelDestinationNames(){
       return RsMessageRuleOperations.getDestinations().findAll{isChannelType(it.channelType)}.name;
    }
    public static List getNonChannelDestinationNames()
    {
       return RsMessageRuleOperations.getDestinations().findAll{!isChannelType(it.channelType)}.name;
    }

    private static boolean isChannelType(channelType)
    {
        if(channelType != null && channelType != "")
        {
            return true;
        }
        return false;
    }

    public static def getUserDestinationForChannel(user,channelType)
    {
        if (channelType) {
            return ChannelUserInformation.get(userId: user.id, type: channelType)?.destination
        }
        return null;
    }

    public static RsMessageRule addMessageRuleForUser(params,username)
    {
        def createParams=prepareAndValidateParamsForUser(params,username);

        def messageRule=RsMessageRule.add(createParams);
        return messageRule;
    }


    public static RsMessageRule updateMessageRuleForUser(messageRule,params,username)
    {
        def updateParams=prepareAndValidateParamsForUser(params,username);

        messageRule.update(updateParams);
        return messageRule;
    }

    private static def prepareAndValidateParamsForUser(ruleParams,username)
    {
        def params=[:];
        params.putAll(ruleParams);
        
        def user = auth.RsUser.get(username: username)
        if(user == null)
            throw new Exception("No user defined with username '${username}'");

        params.userId = user.id

        def channelType=getDestinationChannelType(params.destinationType)
        def destination = getUserDestinationForChannel(user, channelType);
        def isAdmin = user.hasRole(Role.ADMINISTRATOR);

        if(isChannelType(channelType))
        {
            if(destination == null ||destination == "")
            {
                throw new Exception("${username}'s destination for ${params.destinationType} is not defined")
            }
        }
        else
        {
            if(!isAdmin)
            {
                throw new Exception("${username} does not have permission to create rule with Non-Channel destination ${params.destinationType}");
            }
        }

        return params;
    }

}