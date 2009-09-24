package ui.map

import auth.RsUser

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Jul 6, 2009
* Time: 11:15:37 AM
* To change this template use File | Settings | File Templates.
*/
class MapGroupOperations extends com.ifountain.rcmdb.domain.operation.AbstractDomainOperation{
    def beforeDelete(){
        def tempQueries=getProperty("maps");
        if(tempQueries!=null)
        {
            if(tempQueries.size()>0)
            {
                throw new Exception("Can not delete Map Group ${groupName}. Group contains maps. Please first move or remove maps of the group");
            }
        }
    }
    static def getVisibleGroupsForUser(username)
    {
        return MapGroup.searchEvery("username:${username.exactQuery()} OR (username:${RsUser.RSADMIN.exactQuery()} AND isPublic:true)");
    }
    static def getSaveGroupsForUser(username)
    {
        return MapGroup.searchEvery("username:${username.exactQuery()} AND isPublic:false");    
    }

    public static String MY_MAPS()
    {
        return "My Maps";
    }
}