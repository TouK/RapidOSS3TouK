package connector

import connection.SmartsConnectionTemplate
import datasource.BaseListeningDatasource
import org.apache.log4j.Level

/**
 * Created by IntelliJ IDEA.
 * User: mustafa sener
 * Date: Aug 27, 2008
 * Time: 3:06:21 PM
 * To change this template use File  | Settings | File Templates.
 */
class SmartsConnector {
    static searchable = {
        except = [];
    };
    static datasources = ["RCMDB":["keys":["name":["nameInDs":"name"]]]]
    String name ="";
    String rsOwner = "p"
    String logLevel = Level.WARN.toString();
    BaseListeningDatasource ds;
    SmartsConnectionTemplate connectionTemplate;
    int reconnectInterval = 0;
    
    static relations  =[
            connectionTemplate:[type:SmartsConnectionTemplate, isMany:false],
            ds:[type:BaseListeningDatasource, isMany:false]
    ]
    static constraints={
      name(blank:false,nullable:false,key:[])
      logLevel(inList:[Level.ALL.toString(),Level.DEBUG.toString(),Level.INFO.toString(),
              Level.WARN.toString(), Level.ERROR.toString(), Level.FATAL.toString(), Level.OFF.toString()])
      ds(nullable:true)
      connectionTemplate(nullable:true)
      
    }
    static propertyConfiguration= [:]
    static transients = [];

    public String toString()
    {
    	return name;
    }
}