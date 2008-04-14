import datasource.RCMDBDatasource
import model.Model
import org.codehaus.groovy.grails.commons.ApplicationHolder

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