import search.SearchQueryGroup
import auth.RsUser
import search.SearchQuery

/**
* Created by IntelliJ IDEA.
* User: Sezgin Kucukkaraaslan
* Date: Nov 19, 2008
* Time: 5:34:51 PM
*/
def adminUser = RsUser.RSADMIN;
def defaultGroup = SearchQueryGroup.add(name: "By State", username:adminUser, isPublic:true, type:"event");
def bySevertyGroup = SearchQueryGroup.add(name: "By Severity", username:adminUser, isPublic:true, type:"event");


SearchQuery.add(group: bySevertyGroup, name: "Critical Events", query: "severity:5", sortProperty: "statechange", sortOrder: "desc", username:adminUser, isPublic:true, type:"event");
SearchQuery.add(group: bySevertyGroup, name: "Major Events", query: "severity:4", sortProperty: "statechange", sortOrder: "desc", username:adminUser, isPublic:true, type:"event");
SearchQuery.add(group: bySevertyGroup, name: "Minor Events", query: "severity:3", sortProperty: "statechange", sortOrder: "desc", username:adminUser, isPublic:true, type:"event");
SearchQuery.add(group: bySevertyGroup, name: "Warning Events", query: "severity:2", sortProperty: "statechange", sortOrder: "desc", username:adminUser, isPublic:true, type:"event");
SearchQuery.add(group: bySevertyGroup, name: "Indeterminate Events", query: "severity:1", sortProperty: "statechange", sortOrder: "desc", username:adminUser, isPublic:true, type:"event");
SearchQuery.add(group: bySevertyGroup, name: "Clear Events", query: "severity:0", sortProperty: "statechange", sortOrder: "desc", username:adminUser, isPublic:true, type:"event");

SearchQuery.add(group:defaultGroup, name: "All Events", query: "alias:*", sortProperty:"statechange", sortOrder:"desc", username:adminUser, isPublic:true, type:"event");
SearchQuery.add(group:defaultGroup, name: "In Maintenance", query: "suppressescl:6 NOT manager:*Watch ", sortProperty:"manager", username:adminUser, isPublic:true, type:"event");
SearchQuery.add(group:defaultGroup, name: "Escalated", query: "suppressescl:{0 TO 4} NOT manager:*Watch", sortProperty:"suppressescl", username:adminUser, isPublic:true, type:"event");
SearchQuery.add(group:defaultGroup, name: "Active Events", query: "severity:[1 TO 5]", sortProperty:"statechange", sortOrder:"desc", username:adminUser, isPublic:true, type:"event");