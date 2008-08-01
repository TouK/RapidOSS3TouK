import search.SearchQuery
import groovy.xml.MarkupBuilder
import search.SearchQueryGroup
import auth.*;

def user = RsUser.findByUsername(web.session.username);
def queryGroups = SearchQueryGroup.list();
def writer = new StringWriter();
def queryBuilder = new MarkupBuilder(writer);

def tenMinBeforeNow = (System.currentTimeMillis() / 1000) - (1000*60*10) ;

SearchQueryGroup.add(name: "Default", user: user);
def defaultGroup = SearchQueryGroup.get(name: "Default", user: user);

SearchQuery.add(group:defaultGroup, name: "All Events", query: "id:*", user: user);
SearchQuery.add(group:defaultGroup, name: "Critical Events", query: "severity: Critical", user: user);
SearchQuery.add(group:defaultGroup, name: "Last 10 Minutes", query: "statechange: [" +tenMinBeforeNow+ "TO NOW]", user: user);
SearchQuery.add(group:defaultGroup, name: "In Maintenance", query: "manager: * - manager:*Watch suppressescl:Maintenance", user: user);
SearchQuery.add(group:defaultGroup, name: "Escalated", query: "manager: * - manager:*Watch (suppressescl:Normal OR suppressescl:Escalated OR suppressescl:Escalated-Level 2 OR suppressescl:Escalated-Level 3 OR suppressescl:Suppressed) ", user: user);

queryBuilder.Filters
{
    queryGroups.each {SearchQueryGroup group ->
        if (group.user.username.equals( user.username)) {
            queryBuilder.Filter(id: group.id, name: group.name, nodeType: "group")
                    {
	                        group.queries.each {SearchQuery query ->
	                            queryBuilder.Filter(id: query.id, name: query.name, nodeType: "filter", query: query.query)

                    		}
                    }
        }
    }
}

return writer.toString();