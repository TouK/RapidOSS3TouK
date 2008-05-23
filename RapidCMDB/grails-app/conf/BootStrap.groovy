import datasource.RCMDBDatasource
import com.ifountain.rcmdb.util.RapidCMDBConstants
import com.ifountain.rcmdb.scripting.ScriptManager
import org.jsecurity.crypto.hash.Sha1Hash
import auth.Role
import auth.RsUser
import auth.UserRoleRel
import org.apache.commons.beanutils.ConvertUtils
import com.ifountain.rcmdb.domain.converter.DateConverter
import com.ifountain.rcmdb.domain.converter.LongConverter
import datasource.SnmpDatasource
import com.ifountain.rcmdb.domain.converter.RapidConvertUtils

class BootStrap {

    def init = {servletContext ->
        RapidConvertUtils.getInstance().register (new DateConverter("yyyy-dd-MM HH:mm:ss"), Date.class)
        RapidConvertUtils.getInstance().register (new LongConverter(), Long.class)
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
	    def adminUser = RsUser.findByUsername("rsadmin");
	    if(!adminUser){
			adminUser = new RsUser(username: "rsadmin", passwordHash: new Sha1Hash("changeme").toHex());
			adminUser.save();    
		}
        new UserRoleRel(rsUser: adminUser, role: adminRole).save()
        def rcmdbDatasource = RCMDBDatasource.findByName(RapidCMDBConstants.RCMDB);
        if (rcmdbDatasource == null) {
            new RCMDBDatasource(name: RapidCMDBConstants.RCMDB).save();
        }
        ScriptManager.getInstance().initialize();
    }
    def destroy = {
        SnmpDatasource.list().each{
            it.close();
        }
        ScriptManager.getInstance().destroy();
    }
} 