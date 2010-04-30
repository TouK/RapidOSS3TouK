package search

import auth.RsUser
import auth.Role

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Feb 24, 2009
* Time: 2:52:22 PM
* To change this template use File | Settings | File Templates.
*/
class SearchQueryGroupOperations extends com.ifountain.rcmdb.domain.operation.AbstractDomainOperation {
    def beforeDelete() {
        def tempQueries = getProperty("queries");
        if (tempQueries != null)
        {
            if (tempQueries.size() > 0)
            {
                throw new Exception("Can not delete Query Group ${name}. Group contains queries. Please first move or remove sub queries");
            }
        }
    }

    public static String getVisibleGroupsQuery(String username, String type) {
        return "( type:${type.exactQuery()} OR type:${SearchQueryGroup.DEFAULT_TYPE.exactQuery()} ) AND  ( ( username:${RsUser.RSADMIN.exactQuery()} AND isPublic:true) OR (username:${username.exactQuery()}) )"
    }

    public static String getEditableGroupsQuery(String username, String type) {
        if (!RsUser.hasRole(username, Role.ADMINISTRATOR)) {
            return "username:${username.exactQuery()} AND (type:${type.exactQuery()} OR type:${SearchQueryGroup.DEFAULT_TYPE.exactQuery()})"
        }
        else {
            return getVisibleGroupsQuery(username, type)
        }
    }

    public static List getVisibleGroups(String username, String type) {
        def query = getVisibleGroupsQuery(username, type);
        return SearchQueryGroup.searchEvery(query, [sort:'name']);
    }
    public static List getEditableGroups(String username, String type) {
        def query = getEditableGroupsQuery(username, type);
        return SearchQueryGroup.searchEvery(query, [sort:'name']);
    }
}