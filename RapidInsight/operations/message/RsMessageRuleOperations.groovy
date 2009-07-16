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
    public static Map getDestinationConfig() {
        ////////////////////////// Match destination type with user channel information type where user destination is stored ////////////
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //return ["email": [channelInformationType:"email"]]
        return ["email": "email"]
    }

    public static List getDestinationNames() {
        def destinationConfig = getDestinationConfig();
        return new ArrayList(destinationConfig.keySet());
    }

    public static List getChannelDestinationNames(){
        return new ArrayList(RsMessageRuleOperations.getDestinationConfig().findAll{it.value != null && it.value != ""}.keySet());
       //return new ArrayList(RsMessageRuleOperations.getDestinationConfig().findAll{it.value.channelInformationType != null}.keySet());
    }
    public static List getNonChannelDestinationNames()
    {
        return new ArrayList(RsMessageRuleOperations.getDestinationConfig().findAll{it.value == null || it.value == ""}.keySet());
       //return new ArrayList(RsMessageRuleOperations.getDestinationConfig().findAll{it.value.channelInformationType == null}.keySet());
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

        def destination = getUserDestination(user, params.destinationType);
        def isAdmin = user.hasRole(Role.ADMINISTRATOR);

        if(isChannelDestination(params.destinationType))
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

    private static def getUserDestination(user,destinationType)
    {
        //def userChannelInfoType = RsMessageRule.getDestinationConfig()[destinationType]?.channelInformationType;
        def userChannelInfoType = RsMessageRule.getDestinationConfig()[destinationType];
        if (userChannelInfoType) {
            return ChannelUserInformation.get(userId: user.id, type: userChannelInfoType)?.destination
        }
        return null;
    }

    private static boolean isChannelDestination(destinationType)
    {
        //def channelInformationType=getDestinationConfig()[destinationType]?.channelInformationType;
        def channelInformationType=getDestinationConfig()[destinationType];
        if(channelInformationType != null && channelInformationType != "")
        {
            return true;
        }
        return false;
    }
}