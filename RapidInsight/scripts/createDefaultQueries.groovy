import auth.RsUser
import search.SearchQueryGroup
import search.SearchQuery

/**
* Created by IntelliJ IDEA.
* User: Sezgin Kucukkaraaslan
* Date: Nov 19, 2008
* Time: 5:32:17 PM
*/

def adminUser = RsUser.RSADMIN;
def defaultEventGroup = SearchQueryGroup.add(name: "Default", username:adminUser, isPublic:true, type:"event");

SearchQuery.add(group: defaultEventGroup, name: "All Events", query: "alias:*", sortProperty: "changedAt", sortOrder: "desc", username:adminUser, isPublic:true, type:"event");
SearchQuery.add(group: defaultEventGroup, name: "Critical Events", query: "severity:1", sortProperty: "changedAt", sortOrder: "desc", username:adminUser, isPublic:true, type:"event");
SearchQuery.add(group: defaultEventGroup, name: "Major Events", query: "severity:2", sortProperty: "changedAt", sortOrder: "desc", username:adminUser, isPublic:true, type:"event");
SearchQuery.add(group: defaultEventGroup, name: "Minor Events", query: "severity:3", sortProperty: "changedAt", sortOrder: "desc", username:adminUser, isPublic:true, type:"event");
SearchQuery.add(group: defaultEventGroup, name: "Unknown Events", query: "severity:4", sortProperty: "changedAt", sortOrder: "desc", username:adminUser, isPublic:true, type:"event");
SearchQuery.add(group: defaultEventGroup, name: "Normal Events", query: "severity:5", sortProperty: "changedAt", sortOrder: "desc", username:adminUser, isPublic:true, type:"event");