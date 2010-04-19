import auth.RsUser
import search.SearchQuery
import search.SearchQueryGroup

/**
* Created by IntelliJ IDEA.
* User: Sezgin Kucukkaraaslan
* Date: Apr 19, 2010
* Time: 11:00:52 AM
*/

def queryType = params.type;
def queryGroupType = queryType;
def queryName = params.name;
def userName = web.session.username;
if (queryName == SearchQueryGroup.MY_QUERIES) {
    queryGroupType = SearchQueryGroup.DEFAULT_TYPE;
}
def queryGroup = SearchQueryGroup.get(name: queryName, type: queryGroupType, username: userName);
if (!queryGroup) {
    queryGroup = SearchQueryGroup.get(name: queryName, type: queryGroupType, username: RsUser.RSADMIN);
    if (!queryGroup) {
        throw new Exception("Query group with name '${queryName}' and type '${queryType}' does not exist.");
    }
}

def parentQueryCandidates = SearchQuery.getEditableQueries(queryGroup, userName, queryType);
def sw = new StringWriter();
def builder = new groovy.xml.MarkupBuilder(sw)
builder.Queries {
    parentQueryCandidates.each {query ->
        builder.Query(id: query.id, name: query.name)
    }
}
return sw.toString();
