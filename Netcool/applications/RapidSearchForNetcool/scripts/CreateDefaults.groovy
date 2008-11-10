import script.CmdbScript
import auth.Role
import auth.RsUser
import org.jsecurity.crypto.hash.Sha1Hash
import search.SearchQueryGroup
import search.SearchQuery
import auth.Group

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Jul 7, 2008
 * Time: 5:29:58 PM
 * To change this template use File | Settings | File Templates.
 */

CmdbScript.addScript(name: "NetcoolColumnMapping");
CmdbScript.addScript(name: "NetcoolConfigurationLoader");
CmdbScript.addScript(name: "importUsers");
CmdbScript.addScript(name: "queryList");
CmdbScript.addScript(name: "editQuery");
CmdbScript.addScript(name: "createQuery");
CmdbScript.addScript(name: "acknowledge");
CmdbScript.addScript(name: "severity");
CmdbScript.addScript(name: "takeOwnership");
CmdbScript.addScript(name: "taskList");
CmdbScript.addScript(name: "suppress");
CmdbScript.addScript(name: "removeAll");
CmdbScript.addScript(name: "getViewFields");

def adminGroup = Group.get(name: RsUser.RSADMIN);
def rootUser = RsUser.add(username: "root", passwordHash: new Sha1Hash("changeme").toHex())
rootUser.addRelation(groups:adminGroup);

def adminUser = RsUser.RSADMIN;
def defaultGroup = SearchQueryGroup.add(name: "By State", username:adminUser, isPublic:true);
def bySevertyGroup = SearchQueryGroup.add(name: "By Severity", username:adminUser, isPublic:true);


SearchQuery.add(group: bySevertyGroup, name: "Critical Events", query: "severity:5", sortProperty: "statechange", sortOrder: "desc", username:adminUser, isPublic:true);
SearchQuery.add(group: bySevertyGroup, name: "Major Events", query: "severity:4", sortProperty: "statechange", sortOrder: "desc", username:adminUser, isPublic:true);
SearchQuery.add(group: bySevertyGroup, name: "Minor Events", query: "severity:3", sortProperty: "statechange", sortOrder: "desc", username:adminUser, isPublic:true);
SearchQuery.add(group: bySevertyGroup, name: "Warning Events", query: "severity:2", sortProperty: "statechange", sortOrder: "desc", username:adminUser, isPublic:true);
SearchQuery.add(group: bySevertyGroup, name: "Indeterminate Events", query: "severity:1", sortProperty: "statechange", sortOrder: "desc", username:adminUser, isPublic:true);
SearchQuery.add(group: bySevertyGroup, name: "Clear Events", query: "severity:0", sortProperty: "statechange", sortOrder: "desc", username:adminUser, isPublic:true);

SearchQuery.add(group:defaultGroup, name: "All Events", query: "alias:*", sortProperty:"statechange", sortOrder:"desc", username:adminUser, isPublic:true);
SearchQuery.add(group:defaultGroup, name: "In Maintenance", query: "suppressescl:6 NOT manager:*Watch ", sortProperty:"manager", username:adminUser, isPublic:true);
SearchQuery.add(group:defaultGroup, name: "Escalated", query: "suppressescl:{0 TO 4} NOT manager:*Watch", sortProperty:"suppressescl", username:adminUser, isPublic:true);
SearchQuery.add(group:defaultGroup, name: "Active Events", query: "severity:[1 TO 5]", sortProperty:"statechange", sortOrder:"desc", username:adminUser, isPublic:true);

