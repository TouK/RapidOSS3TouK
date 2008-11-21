import search.SearchQuery
import auth.RsUser
import search.SearchQueryGroup

def adminUser = RsUser.RSADMIN;
def defaultEventGroup = SearchQueryGroup.add(name: "Default", username:adminUser, isPublic:true, type:"event");

SearchQuery.add(group: defaultEventGroup, name: "All Events", query: "alias:*", sortProperty: "changedAt", sortOrder: "desc", username:adminUser, isPublic:true, type:"event");
SearchQuery.add(group: defaultEventGroup, name: "Critical Events", query: "severity:1", sortProperty: "changedAt", sortOrder: "desc", username:adminUser, isPublic:true, type:"event");
SearchQuery.add(group: defaultEventGroup, name: "Major Events", query: "severity:2", sortProperty: "changedAt", sortOrder: "desc", username:adminUser, isPublic:true, type:"event");
SearchQuery.add(group: defaultEventGroup, name: "Minor Events", query: "severity:3", sortProperty: "changedAt", sortOrder: "desc", username:adminUser, isPublic:true, type:"event");
SearchQuery.add(group: defaultEventGroup, name: "Unknown Events", query: "severity:4", sortProperty: "changedAt", sortOrder: "desc", username:adminUser, isPublic:true, type:"event");
SearchQuery.add(group: defaultEventGroup, name: "Normal Events", query: "severity:5", sortProperty: "changedAt", sortOrder: "desc", username:adminUser, isPublic:true, type:"event");

def defaultTopologyGroup = SearchQueryGroup.add(name: "Default", username:adminUser, isPublic:true, type:"topology");

SearchQuery.add(group: defaultTopologyGroup, name: "Router", query: "className:\"Router\"", sortProperty: "name", sortOrder: "asc", username:adminUser, isPublic:true, type:"topology");
SearchQuery.add(group: defaultTopologyGroup, name: "Switch", query: "className:\"Switch\"", sortProperty: "name", sortOrder: "asc", username:adminUser, isPublic:true, type:"topology");
SearchQuery.add(group: defaultTopologyGroup, name: "Interface", query: "className:\"Interface\"", sortProperty: "name", sortOrder: "asc", username:adminUser, isPublic:true, type:"topology");
SearchQuery.add(group: defaultTopologyGroup, name: "Port", query: "className:\"Port\"", sortProperty: "name", sortOrder: "asc", username:adminUser, isPublic:true, type:"topology");
SearchQuery.add(group: defaultTopologyGroup, name: "Down Interfaces/Ports", query: "alias:SmartsNetworkAdapter AND adminStatus:\"UP\" AND operStatus:\"DOWN\"", sortProperty: "name", sortOrder: "asc", username:adminUser, isPublic:true, type:"topology");
SearchQuery.add(group: defaultTopologyGroup, name: "Disabled Interfaces", query: "className:\"Interface\" AND adminStatus:\"DOWN\"", sortProperty: "name", sortOrder: "asc", username:adminUser, isPublic:true, type:"topology");