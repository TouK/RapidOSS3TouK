import org.apache.log4j.Logger
import datasource.RCMDBDatasource

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