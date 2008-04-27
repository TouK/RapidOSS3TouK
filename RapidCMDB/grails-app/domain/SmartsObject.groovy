import com.ifountain.core.domain.annotations.*;


class SmartsObject
{

    //AUTO_GENERATED_CODE


    static datasources = ["westRegionDs":["master":false, "keys":["creationClassName":["nameInDs":"CreationClassName"], "name":["nameInDs":"Name"]]], "eastRegionDs":["master":false, "keys":["name":["nameInDs":"Name"], "creationClassName":["nameInDs":"CreationClassName"]]], "RCMDB":["master":true, "keys":["name":["nameInDs":"name"]]]]

    
    String creationClassName ;
    
    String name ;
    
    String smartDs ;
    
    String displayName ;
    

    static hasMany = [:]

    static constraints={
    name(unique:true)

     creationClassName(blank:false,nullable:false)

     displayName(blank:true,nullable:true)

     smartDs(blank:true,nullable:true)

     
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
