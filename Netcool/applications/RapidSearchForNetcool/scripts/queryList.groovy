import search.SearchQuery;
import groovy.xml.MarkupBuilder;
import search.SearchQueryGroup;
import auth.*;
import org.apache.commons.collections.map.ReferenceMap;

def user = RsUser.findByUsername(web.session.username);

def writer = new StringWriter();
def queryBuilder = new MarkupBuilder(writer);
//def tenMinBeforeNow = (System.currentTimeMillis()/1000) - (60*10) ;
ReferenceMap map = new ReferenceMap(ReferenceMap.WEAK, ReferenceMap.WEAK);

def defaultGroup = SearchQueryGroup.add(name: "Default", user: user);
def bySevertyGroup = SearchQueryGroup.add(name: "By Severity", user: user);

Map previouslyAddedDefaultQueries = map.get(defaultGroup);
if(previouslyAddedDefaultQueries == null)
{
    previouslyAddedDefaultQueries = [:]
    map.put(defaultGroup, previouslyAddedDefaultQueries);
}
Map previouslyAddedSeverityQueries = map.get(bySevertyGroup);
if(previouslyAddedSeverityQueries == null)
{
    previouslyAddedSeverityQueries = [:]
    map.put(bySevertyGroup, previouslyAddedSeverityQueries);
}

SearchQuery.add(group:bySevertyGroup, name: "Critical Events", query: "severity:Critical", user: user);
SearchQuery.add(group:bySevertyGroup, name: "Major Events", query: "severity:Major", user: user);
SearchQuery.add(group:bySevertyGroup, name: "Minor Events", query: "severity:Minor", user: user);
SearchQuery.add(group:bySevertyGroup, name: "Warning Events", query: "severity:Warning", user: user);
SearchQuery.add(group:bySevertyGroup, name: "Indeterminate Events", query: "severity:Indeterminate", user: user);
SearchQuery.add(group:bySevertyGroup, name: "Clear Events", query: "severity:Clear", user: user);

previouslyAddedSeverityQueries["Critical Events"] = "Critical Events";
previouslyAddedSeverityQueries["Major Events"] = "Major Events";
previouslyAddedSeverityQueries["Minor Events"] = "Minor Events";
previouslyAddedSeverityQueries["Warning Events"] = "Warning Events";
previouslyAddedSeverityQueries["Indeterminate Events"] = "Indeterminate Events";
previouslyAddedSeverityQueries["Clear Events"] = "Clear Events";

SearchQuery.add(group:defaultGroup, name: "All Events", query: "id:*", user: user);
//SearchQuery.add(group:defaultGroup, name: "Last 10 Minutes", query: "statechange: [" +tenMinBeforeNow+ " TO NOW]", user: user);
SearchQuery.add(group:defaultGroup, name: "In Maintenance", query: "manager: * - manager:*Watch suppressescl:Maintenance", user: user);
SearchQuery.add(group:defaultGroup, name: "Escalated", query: "manager: * - manager:*Watch (suppressescl:Normal OR suppressescl:Escalated OR suppressescl:Escalated-Level 2 OR suppressescl:Escalated-Level 3 OR suppressescl:Suppressed) ", user: user);

previouslyAddedDefaultQueries["All Events"] = "All Events";
previouslyAddedDefaultQueries["In Maintenance"] = "In Maintenance";
previouslyAddedDefaultQueries["Escalated"] = "Escalated";


def queryGroups = SearchQueryGroup.list();
queryBuilder.Filters
{
    queryGroups.each {SearchQueryGroup group ->
    	if (group.user.username.equals( user.username)) {
            queryBuilder.Filter(id: group.id, name: group.name, nodeType: "group")
                {
                	if(group.name.equals("Default") || group.name.equals("By Severity"))
                	{
                    	group.queries.each {SearchQuery query ->
                    		 if(previouslyAddedSeverityQueries.containsKey(query.name) || previouslyAddedDefaultQueries.containsKey(query.name))
                              	queryBuilder.Filter(id: query.id, isDefault:true , name: query.name, nodeType: "filter", query: query.query)
                             else
                             	queryBuilder.Filter(id: query.id, name: query.name, nodeType: "filter", query: query.query)
                		}
                	}
                	else
            		{
                        group.queries.each {SearchQuery query ->
                              queryBuilder.Filter(id: query.id, name: query.name, nodeType: "filter", query: query.query)
                		}

            		}
                }
        }
    }
}
return writer.toString();