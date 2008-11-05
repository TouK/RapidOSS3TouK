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
    String rsOwner = "p";    
    BaseListeningDatasource ds;
    SmartsConnectionTemplate connectionTemplate;
    
    
    static relations  =[
            connectionTemplate:[type:SmartsConnectionTemplate, isMany:false],
            ds:[type:BaseListeningDatasource, isMany:false]
    ]
    static constraints={
      name(blank:false,nullable:false,key:[])      
      ds(nullable:true)
      
    }
    static propertyConfiguration= [:]
    static transients = [];

    public String toString()
    {
    	return name;
    }
}