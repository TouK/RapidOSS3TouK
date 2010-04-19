package search

import com.ifountain.rcmdb.domain.operation.AbstractDomainOperation
import auth.RsUser
import auth.Role

/**
* Created by IntelliJ IDEA.
* User: Sezgin Kucukkaraaslan
* Date: Apr 16, 2010
* Time: 5:07:32 PM
*/
class SearchQueryOperations extends AbstractDomainOperation {
    def beforeDelete() {
        if (SearchQuery.countHits("parentQueryId:${id}") > 0)
        {
            throw new Exception("Can not delete Query ${name}. Query contains sub queries. Please first move or remove sub queries");
        }
    }

    public static Collection getVisibleQueries(List queries, String username, String type) {
        return queries.findAll {it.type == type && (it.username == username || it.isPublic)};
    }

    public static Collection getVisibleQueries(SearchQueryGroup group, String username, String type) {
        return getVisibleQueries(group.queries, username, type)
    }

    public static Collection getEditableQueries(List queries, String username, String type) {
        if (RsUser.hasRole(username, Role.ADMINISTRATOR)) {
            return getVisibleQueries(queries, username, type)
        }
        else {
            return queries.findAll {it.type == type && it.username == username};
        }
    }

    public static Collection getEditableQueries(SearchQueryGroup group, String username, String type) {
        return getEditableQueries(group.queries, username, type)
    }
}