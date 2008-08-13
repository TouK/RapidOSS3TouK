import script.CmdbScript
import auth.Role
import auth.RsUser
import org.jsecurity.crypto.hash.Sha1Hash
import auth.UserRoleRel

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Jul 7, 2008
 * Time: 5:29:58 PM
 * To change this template use File | Settings | File Templates.
 */

CmdbScript.addScript(name:"NetcoolColumnMapping");
CmdbScript.addScript(name:"NetcoolConfigurationLoader");
CmdbScript.addScript(name:"queryList");
CmdbScript.addScript(name:"acknowledge");
CmdbScript.addScript(name:"severity");
CmdbScript.addScript(name:"takeOwnership");
CmdbScript.addScript(name:"taskList");
CmdbScript.addScript(name:"suppress");
CmdbScript.addScript(name:"removeAll");

def adminRole = Role.get(name:"Administrator");
def rootUser = RsUser.add(username: "root", passwordHash: new Sha1Hash("changeme").toHex())
UserRoleRel.add(rsUser: rootUser, role: adminRole)

