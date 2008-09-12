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

CmdbScript.addScript(name: "modelCreator");
CmdbScript.addScript(name: "removeAll");
CmdbScript.addScript(name: "acknowledge");
CmdbScript.addScript(name: "setOwnership");
CmdbScript.addScript(name: "queryList");
CmdbScript.addScript(name: "createQuery");
CmdbScript.addScript(name: "editQuery");
CmdbScript.addScript(name: "reloadOperations");

// topology scripts
CmdbScript.addScript(name: "createMap");
CmdbScript.addScript(name: "editMap");
CmdbScript.addScript(name: "expandMap");
CmdbScript.addScript(name: "getMap");
CmdbScript.addScript(name: "mapList");
CmdbScript.addScript(name: "saveMap");
CmdbScript.addScript(name: "getMapData");

def adminUser = RsUser.RSADMIN;
def bySevertyGroup = SearchQueryGroup.add(name: "By Severity", username:adminUser, isPublic:true, type:"notification");

SearchQuery.add(group: bySevertyGroup, name: "Critical Events", query: "severity:1", sortProperty: "lastChangedAt", sortOrder: "desc", username:adminUser, isPublic:true, type:"notification");
SearchQuery.add(group: bySevertyGroup, name: "Major Events", query: "severity:2", sortProperty: "lastChangedAt", sortOrder: "desc", username:adminUser, isPublic:true, type:"notification");
SearchQuery.add(group: bySevertyGroup, name: "Minor Events", query: "severity:3", sortProperty: "lastChangedAt", sortOrder: "desc", username:adminUser, isPublic:true, type:"notification");
SearchQuery.add(group: bySevertyGroup, name: "Unknown Events", query: "severity:4", sortProperty: "lastChangedAt", sortOrder: "desc", username:adminUser, isPublic:true, type:"notification");
SearchQuery.add(group: bySevertyGroup, name: "Normal Events", query: "severity:5", sortProperty: "lastChangedAt", sortOrder: "desc", username:adminUser, isPublic:true, type:"notification");

def byClassGroup = SearchQueryGroup.add(name: "By Class", username:adminUser, isPublic:true, type:"topology");

SearchQuery.add(group: byClassGroup, name: "Router", query: "creationClassName:\"Router\"", sortProperty: "name", sortOrder: "asc", username:adminUser, isPublic:true, type:"topology");
SearchQuery.add(group: byClassGroup, name: "Switch", query: "creationClassName:\"Switch\"", sortProperty: "name", sortOrder: "asc", username:adminUser, isPublic:true, type:"topology");
SearchQuery.add(group: byClassGroup, name: "Interface", query: "creationClassName:\"Interface\"", sortProperty: "name", sortOrder: "asc", username:adminUser, isPublic:true, type:"topology");
SearchQuery.add(group: byClassGroup, name: "Port", query: "creationClassName:\"Port\"", sortProperty: "name", sortOrder: "asc", username:adminUser, isPublic:true, type:"topology");
