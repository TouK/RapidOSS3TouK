
import com.ifountain.core.domain.annotations.*;

class Team
{
    
    //AUTO_GENERATED_CODE


    static datasources = ["RCMDB":["master":true, "keys":["name":["nameInDs":"name"]]]]

    
    String maskot ;
    
    String name ;
    
    Employee managedBy ;
    

    static hasMany = [:]

    static constraints={
    maskot(blank:false,nullable:false)
        
     name(unique:true)
        
     managedBy(nullable:true)
        
     
    }

    static mappedBy=["managedBy":"manages"]
    static belongsTo = []
    static propertyConfiguration= [:]
    static transients = [];
    
    public String toString()
    {
    	return "${getClass().getName()}[name:$name]";
    }
    
    //AUTO_GENERATED_CODE
}