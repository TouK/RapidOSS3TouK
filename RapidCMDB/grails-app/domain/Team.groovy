
import com.ifountain.core.domain.annotations.*;

class Team implements com.ifountain.rcmdb.domain.generation.IGeneratedModel
{
    
    //AUTO_GENERATED_CODE

    static searchable = {
        except = [];
    };
    static datasources = ["RCMDB":["master":true, "keys":["name":["nameInDs":"name"]]]]

    
    String maskot ="";
    
    String name ="";
    
    Employee managedBy ;
    

    static hasMany = [:]
    
        static mapping = {
            tablePerHierarchy false
        }
    
    static constraints={
    maskot(blank:false,nullable:true)
        
     name(blank:false,nullable:false,key:[])
        
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