class SmartsObject {

    //AUTO_GENERATED_CODE

    static searchable = {
        except = ["displayName"];
    };
    static datasources = ["westRegionDs":["master":false, "keys":["creationClassName":["nameInDs":"CreationClassName"], "name":["nameInDs":"Name"]]], "RCMDB":["master":true, "keys":["name":["nameInDs":"name"], "creationClassName":["nameInDs":"creationClassName"]]], "eastRegionDs":["master":false, "keys":["creationClassName":["nameInDs":"CreationClassName"], "name":["nameInDs":"Name"]]]]

    
    String creationClassName ;
    
    String smartDs ;
    
    String displayName ;
    
    String name ;
    

    static hasMany = [:]
    
        static mapping = {
            tablePerHierarchy false
        }
    
    static constraints={
    creationClassName(blank:false,nullable:false)
        
     smartDs(blank:true,nullable:true)
        
     displayName(blank:true,nullable:true)
        
     name(blank:false,nullable:false,key:["creationClassName"])
        
     
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
