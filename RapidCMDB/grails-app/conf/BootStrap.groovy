import datasource.RCMDBDatasource
import com.ifountain.rcmdb.util.RapidCMDBConstants
import com.ifountain.rcmdb.scripting.ScriptManager
import org.jsecurity.crypto.hash.Sha1Hash
import auth.Role
import auth.User
import auth.UserRoleRel

class BootStrap {

    def init = {servletContext ->
        def adminRole = new Role(name: "Administrator").save()
        def userRole = new Role(name: "User").save()
        def adminUser = new User(username: "rsadmin", passwordHash: new Sha1Hash("changeme").toHex()).save()
        new UserRoleRel(user: adminUser, role: adminRole).save()
        
        def rcmdbDatasource = RCMDBDatasource.findByName(RapidCMDBConstants.RCMDB);
        if (rcmdbDatasource == null) {
            new RCMDBDatasource(name: RapidCMDBConstants.RCMDB).save();
        }
        ScriptManager.getInstance().initialize();
    }
    def destroy = {
        ScriptManager.getInstance().destroy();
    }
} 