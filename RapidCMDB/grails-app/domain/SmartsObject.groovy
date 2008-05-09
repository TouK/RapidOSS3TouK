import com.ifountain.core.domain.annotations.*;


class SmartsObject {

    //AUTO_GENERATED_CODE


    static datasources = ["eastRegionDs":["master":false, "keys":["creationClassName":["nameInDs":"CreationClassName"], "name":["nameInDs":"Name"]]], "westRegionDs":["master":false, "keys":["creationClassName":["nameInDs":"CreationClassName"], "name":["nameInDs":"Name"]]], "RCMDB":["master":true, "keys":["name":["nameInDs":"name"], "creationClassName":["nameInDs":"creationClassName"]]]]

    
    String smartDs ;
    
    String name ;
    
    String creationClassName ;
    
    String displayName ;
    

    static hasMany = [:]

    
        static mapping = {
            tablePerHierarchy false
        }
    

    static constraints={
    smartDs(blank:true,nullable:true)
        
     creationClassName(unique:["name"])
        
     displayName(blank:true,nullable:true)
        
     
    }

    static mappedBy=[:]
    static belongsTo = []
    static propertyConfiguration= ["displayName":["nameInDs":"DisplayName", "datasourceProperty":"smartDs", "lazy":true]]
    static transients = ["displayName"];
    
    public String toString()
    {
    	return "${getClass().getName()}[name:$name, creationClassName:$creationClassName]";
    }
    
    //AUTO_GENERATED_CODE






}
