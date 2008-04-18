
import com.ifountain.core.domain.annotations.*;

class Person  implements com.ifountain.rcmdb.domain.GeneratedModel
{
    
    //AUTO_GENERATED_CODE


    static datasources = ["RCMDB":["master":true, "keys":["name":["nameInDs":"name"]]]]

    
    String bday ;
    
    String name ;
    

    static hasMany = [:]

    static constraints={
    bday(blank:false,nullable:false)
        
     name(unique:true)
        
     
    }

    static mappedBy=[:]
    static belongsTo = []
    static propertyConfiguration= [:]
    static transients = [];
    
    public String toString()
    {
    	return "${getClass().getName()}[name:$name]";
    }
    
    //AUTO_GENERATED_CODE
}