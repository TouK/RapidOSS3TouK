import datasource.RCMDBDatasource
import com.ifountain.rcmdb.util.RapidCMDBConstants
import com.ifountain.rcmdb.scripting.ScriptManager
import org.jsecurity.crypto.hash.Sha1Hash
import auth.Role
import auth.User
import auth.UserRoleRel

class BootStrap {

    def init = {servletContext ->
    	def adminRole = Role.findByName("Administrator");
    	if(!adminRole){
	    	adminRole = new Role(name: "Administrator");
	    	adminRole.save();	
	    }
	    def userRole = Role.findByName("User");
    	if(!userRole){
	    	userRole = new Role(name: "User");
	    	userRole.save();	
	    }
	    def adminUser = User.findByUsername("rsadmin");
	    if(!adminUser){
			adminUser = new User(username: "rsadmin", passwordHash: new Sha1Hash("changeme").toHex());
			adminUser.save();    
		}
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