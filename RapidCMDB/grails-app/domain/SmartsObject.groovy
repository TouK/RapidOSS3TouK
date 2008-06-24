
import com.ifountain.core.domain.annotations.*;

class SmartsObject 
{
    
    //AUTO_GENERATED_CODE

    static searchable = {
        except = ["displayName"];
    };
    static datasources = ["westRegionDs":["master":false, "keys":["creationClassName":["nameInDs":"CreationClassName"], "name":["nameInDs":"Name"]]], "eastRegionDs":["master":false, "keys":["name":["nameInDs":"Name"], "creationClassName":["nameInDs":"CreationClassName"]]], "RCMDB":["master":true, "keys":["name":["nameInDs":"name"], "creationClassName":["nameInDs":"creationClassName"]]]]

    
    String displayName ="";
    
    String name ="";
    
    String smartDs ="";
    
    String creationClassName ="";
    

    static hasMany = [:]
    
        static mapping = {
            tablePerHierarchy false
        }
    
    static constraints={
    displayName(blank:true,nullable:true)
        
     name(blank:false,nullable:false)
        
     smartDs(blank:true,nullable:true)
        
     creationClassName(blank:false,nullable:false,key:["name"])
        
     
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