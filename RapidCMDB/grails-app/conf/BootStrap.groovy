import datasource.RCMDBDatasource
import com.ifountain.rcmdb.util.RapidCMDBConstants
import com.ifountain.rcmdb.scripting.ScriptManager

class BootStrap {

     def init = { servletContext ->
        def rcmdbDatasource = RCMDBDatasource.findByName(RapidCMDBConstants.RCMDB);
        if(rcmdbDatasource == null){
            new RCMDBDatasource(name:RapidCMDBConstants.RCMDB).save();
        }
        ScriptManager.getInstance().initialize();
     }
     def destroy = {
         ScriptManager.getInstance().destroy();
     }
} 