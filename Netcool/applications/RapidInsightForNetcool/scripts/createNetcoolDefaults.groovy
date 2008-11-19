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
CmdbScript.addScript(name: "importUsers");
CmdbScript.addScript(name: "severity");
CmdbScript.addScript(name: "takeOwnership");
CmdbScript.addScript(name: "taskList");
CmdbScript.addScript(name: "suppress");

def adminGroup = Group.get(name: RsUser.RSADMIN);
def rootUser = RsUser.add(username: "root", passwordHash: new Sha1Hash("changeme").toHex())
rootUser.addRelation(groups:adminGroup);

