import datasource.RCMDBDatasource
import com.ifountain.rcmdb.util.RapidCMDBConstants
class BootStrap {

     def init = { servletContext ->
        def rcmdbDatasource = RCMDBDatasource.findByName(RapidCMDBConstants.RCMDB);
        if(rcmdbDatasource == null){
            new RCMDBDatasource(name:RapidCMDBConstants.RCMDB).save();
        }
     }
     def destroy = {
     }
} 