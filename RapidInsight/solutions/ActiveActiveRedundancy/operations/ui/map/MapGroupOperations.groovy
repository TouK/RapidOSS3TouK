package ui.map

import auth.RsUser
import auth.Role

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Jul 6, 2009
* Time: 11:15:37 AM
* To change this template use File | Settings | File Templates.
*/
class MapGroupOperations extends com.ifountain.rcmdb.domain.operation.AbstractDomainOperation {
    //	changed for isLocal property
    def afterInsert()
    {
        application.RapidApplication.getUtility("RedundancyUtility").objectInAfterInsert(this.domainObject);
    }
    def afterUpdate(params)
    {
        application.RapidApplication.getUtility("RedundancyUtility").objectInAfterUpdate(this.domainObject);
    }
    def afterDelete()
    {
        application.RapidApplication.getUtility("RedundancyUtility").objectInAfterDelete(this.domainObject);
    }
    //change ended

    def beforeDelete() {
        def tempQueries = getProperty("maps");
        if (tempQueries != null)
        {
            if (tempQueries.size() > 0)
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
        if (RsUser.hasRole(username, Role.ADMINISTRATOR))
        {
            return MapGroup.searchEvery("username:${username.exactQuery()} OR (username:${RsUser.RSADMIN.exactQuery()} AND isPublic:true)");
        }
        else
        {
            return MapGroup.searchEvery("username:${username.exactQuery()}");
        }
    }

    public static String MY_MAPS()
    {
        return "My Maps";
    }
}