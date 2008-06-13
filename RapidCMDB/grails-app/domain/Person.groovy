
import com.ifountain.core.domain.annotations.*;

class Person
{
    
    //AUTO_GENERATED_CODE

    static searchable = {
        except = [];
    };
    static datasources = ["RCMDB":["master":true, "keys":["name":["nameInDs":"name"]]]]

    
    String name ="";
    
    String bday ="";
    

    static hasMany = [:]
    
        static mapping = {
            tablePerHierarchy false
        }
    
    static constraints={
    name(blank:false,nullable:false,key:[])
        
     bday(blank:false,nullable:true)
        
     
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