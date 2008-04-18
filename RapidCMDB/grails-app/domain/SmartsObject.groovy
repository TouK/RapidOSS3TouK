import com.ifountain.core.domain.annotations.*;


class SmartsObject  implements com.ifountain.rcmdb.domain.GeneratedModel
{

    //AUTO_GENERATED_CODE


    static datasources = ["eastRegionDs":["master":false, "keys":["name":["nameInDs":"Name"], "creationClassName":["nameInDs":"CreationClassName"]]], "RCMDB":["master":true, "keys":["name":["nameInDs":"name"], "creationClassName":["nameInDs":"creationClassName"]]], "westRegionDs":["master":false, "keys":["name":["nameInDs":"Name"], "creationClassName":["nameInDs":"CreationClassName"]]]]

    
    String name ;
    
    String displayName ;
    
    String smartDs ;
    
    String creationClassName ;
    

    static hasMany = [:]

    static constraints={
    displayName(blank:true,nullable:true)
        
     smartDs(blank:true,nullable:true)
        
     creationClassName(unique:["name"])
        
     
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
