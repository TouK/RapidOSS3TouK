import script.CmdbScript
import datasource.SmartsModel
import auth.Role
import auth.RsUser
import org.jsecurity.crypto.hash.Sha1Hash
import auth.UserRoleRel
import search.SearchQueryGroup
import search.SearchQuery

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Jul 7, 2008
 * Time: 5:29:58 PM
 * To change this template use File | Settings | File Templates.
 */

CmdbScript.addScript(name: "modelCreator");
CmdbScript.addScript(name: "getDevices");
CmdbScript.addScript(name: "removeAll");
CmdbScript.addScript(name: "acknowledge");
CmdbScript.addScript(name: "setOwnership");
if(SmartsModel.get(name:"RsNotification") == null)
{
    CmdbScript.runScript("modelCreator", [web:[:]]);
}

/*def adminRole = Role.get(name: Role.ADMINISTRATOR);
def rootUser = RsUser.add(username: "root", passwordHash: new Sha1Hash("changeme").toHex())
UserRoleRel.add(rsUser: rootUser, role: adminRole)*/


def adminUser = RsUser.RSADMIN;
def defaultGroup = SearchQueryGroup.add(name: "By State", username:adminUser, isPublic:true);

SearchQuery.add(group:defaultGroup, name: "All Events", query: "alias:RsEvent", sortProperty:"displayName", sortOrder:"desc", username:adminUser, isPublic:true);


def bySevertyGroup = SearchQueryGroup.add(name: "By Severity", username:adminUser, isPublic:true);


SearchQuery.add(group: bySevertyGroup, name: "Critical Events", query: "severity:1", sortProperty: "lastChangedAt", sortOrder: "desc", username:adminUser, isPublic:true);
SearchQuery.add(group: bySevertyGroup, name: "Major Events", query: "severity:2", sortProperty: "lastChangedAt", sortOrder: "desc", username:adminUser, isPublic:true);
SearchQuery.add(group: bySevertyGroup, name: "Minor Events", query: "severity:3", sortProperty: "lastChangedAt", sortOrder: "desc", username:adminUser, isPublic:true);
SearchQuery.add(group: bySevertyGroup, name: "Unknown Events", query: "severity:4", sortProperty: "lastChangedAt", sortOrder: "desc", username:adminUser, isPublic:true);
SearchQuery.add(group: bySevertyGroup, name: "Normal Events", query: "severity:5", sortProperty: "lastChangedAt", sortOrder: "desc", username:adminUser, isPublic:true);
