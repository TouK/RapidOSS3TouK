import auth.RsUser
import script.CmdbScript
import search.SearchQuery
import search.SearchQueryGroup

/**
* Created by IntelliJ IDEA.
* User: Administrator
* Date: Jul 7, 2008
* Time: 5:29:58 PM
* To change this template use File | Settings | File Templates.
*/

CmdbScript.addScript(name: "autocomplete");
CmdbScript.addScript(name: "getHierarchy");
CmdbScript.addScript(name: "getEventHistory");
CmdbScript.addScript(name: "getSummaryData");
CmdbScript.addScript(name: "getGeocodes");
CmdbScript.addScript(name: "getDeviceLocations");

def adminUser = RsUser.RSADMIN;
def defaultEventGroup = SearchQueryGroup.add(name: "Default", username:adminUser, isPublic:true, type:"event");

SearchQuery.add(group: defaultEventGroup, name: "All Events", query: "alias:*", sortProperty: "changedAt", sortOrder: "desc", username:adminUser, isPublic:true, type:"event");
SearchQuery.add(group: defaultEventGroup, name: "Critical Events", query: "severity:1", sortProperty: "changedAt", sortOrder: "desc", username:adminUser, isPublic:true, type:"event");
SearchQuery.add(group: defaultEventGroup, name: "Major Events", query: "severity:2", sortProperty: "changedAt", sortOrder: "desc", username:adminUser, isPublic:true, type:"event");
SearchQuery.add(group: defaultEventGroup, name: "Minor Events", query: "severity:3", sortProperty: "changedAt", sortOrder: "desc", username:adminUser, isPublic:true, type:"event");
SearchQuery.add(group: defaultEventGroup, name: "Unknown Events", query: "severity:4", sortProperty: "changedAt", sortOrder: "desc", username:adminUser, isPublic:true, type:"event");
SearchQuery.add(group: defaultEventGroup, name: "Normal Events", query: "severity:5", sortProperty: "changedAt", sortOrder: "desc", username:adminUser, isPublic:true, type:"event");

def defaultTopologyGroup = SearchQueryGroup.add(name: "Default", username:adminUser, isPublic:true, type:"topology");

SearchQuery.add(group: defaultTopologyGroup, name: "Router", query: "creationClassName:\"Router\"", sortProperty: "name", sortOrder: "asc", username:adminUser, isPublic:true, type:"topology");
SearchQuery.add(group: defaultTopologyGroup, name: "Switch", query: "creationClassName:\"Switch\"", sortProperty: "name", sortOrder: "asc", username:adminUser, isPublic:true, type:"topology");
SearchQuery.add(group: defaultTopologyGroup, name: "Interface", query: "creationClassName:\"Interface\"", sortProperty: "name", sortOrder: "asc", username:adminUser, isPublic:true, type:"topology");
SearchQuery.add(group: defaultTopologyGroup, name: "Port", query: "creationClassName:\"Port\"", sortProperty: "name", sortOrder: "asc", username:adminUser, isPublic:true, type:"topology");
SearchQuery.add(group: defaultTopologyGroup, name: "Down Interfaces/Ports", query: "alias:RsNetworkAdapter AND adminStatus:\"UP\" AND operStatus:\"DOWN\"", sortProperty: "name", sortOrder: "asc", username:adminUser, isPublic:true, type:"topology");
SearchQuery.add(group: defaultTopologyGroup, name: "Disabled Interfaces", query: "creationClassName:\"Interface\" AND adminStatus:\"DOWN\"", sortProperty: "name", sortOrder: "asc", username:adminUser, isPublic:true, type:"topology");
