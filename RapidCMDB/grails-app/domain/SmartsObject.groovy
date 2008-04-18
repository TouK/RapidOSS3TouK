import com.ifountain.core.domain.annotations.*;


class SmartsObject  implements com.ifountain.rcmdb.domain.GeneratedModel
{

    //AUTO_GENERATED_CODE


    static datasources = ["westRegionDs":["master":false, "keys":["name":["nameInDs":"Name"], "creationClassName":["nameInDs":"CreationClassName"]]], "eastRegionDs":["master":false, "keys":["creationClassName":["nameInDs":"CreationClassName"], "name":["nameInDs":"Name"]]], "RCMDB":["master":true, "keys":["name":["nameInDs":"name"], "creationClassName":["nameInDs":"creationClassName"]]]]

    
    String displayName ;
    
    String smartDs ;
    
    String name ;
    
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
