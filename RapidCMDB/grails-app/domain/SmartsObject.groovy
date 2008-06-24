
import com.ifountain.core.domain.annotations.*;

class SmartsObject 
{
    
    //AUTO_GENERATED_CODE

    static searchable = {
        except = ["displayName"];
    };
    static datasources = ["eastRegionDs":["master":false, "keys":["name":["nameInDs":"Name"], "creationClassName":["nameInDs":"CreationClassName"]]], "westRegionDs":["master":false, "keys":["creationClassName":["nameInDs":"CreationClassName"], "name":["nameInDs":"Name"]]], "RCMDB":["master":true, "keys":["name":["nameInDs":"name"], "creationClassName":["nameInDs":"creationClassName"]]]]

    
    String name ="";
    
    String creationClassName ="";
    
    String smartDs ="";
    
    String displayName ="";
    

    static hasMany = [:]
    
        static mapping = {
            tablePerHierarchy false
        }
    
    static constraints={
    name(blank:false,nullable:false)
        
     creationClassName(blank:false,nullable:false,key:["name"])
        
     smartDs(blank:true,nullable:true)
        
     displayName(blank:true,nullable:true)
        
     
    }

    static mappedBy=[:]
    static belongsTo = []
    static propertyConfiguration= ["displayName":["nameInDs":"DisplayName", "datasourceProperty":"smartDs", "lazy":true]]
    static transients = ["displayName"];
    
    public String toString()
    {
    	return "${getClass().getName()}[creationClassName:$creationClassName, name:$name]";
    }
    
    //AUTO_GENERATED_CODE
}