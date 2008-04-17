import com.ifountain.core.domain.annotations.*;


class SmartsObject  implements com.ifountain.domain.GeneratedModel
{

    //AUTO_GENERATED_CODE


    static datasources = ["RCMDB":["master":true, "keys":["name":["nameInDs":"name"]]], "eastRegionDs":["master":false, "keys":["creationClassName":["nameInDs":"CreationClassName"], "name":["nameInDs":"Name"]]], "westRegionDs":["master":false, "keys":["name":["nameInDs":"Name"], "creationClassName":["nameInDs":"CreationClassName"]]]]

    
    String displayName ;
    
    String name ;
    
    String smartDs ;
    
    String creationClassName ;
    

    static hasMany = [:]

    static constraints={
    displayName(blank:true,nullable:true)
        
     name(unique:true)
        
     smartDs(blank:true,nullable:true)
        
     creationClassName(blank:false,nullable:false)
        
     
    }

    static mappedBy=[:]
    static belongsTo = []
    static propertyConfiguration= ["displayName":["nameInDs":"DisplayName", "datasourceProperty":"smartDs", "lazy":true]]
    static transients = ["displayName"];
    
    public String toString()
    {
    	return "${getClass().getName()}[name:$name]";
    }
    
    //AUTO_GENERATED_CODE    
}
