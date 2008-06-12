
import com.ifountain.core.domain.annotations.*;

class Task implements com.ifountain.rcmdb.domain.generation.IGeneratedModel
{
    
    //AUTO_GENERATED_CODE

    static searchable = {
        except = [];
    };
    static datasources = ["RCMDB":["master":true, "keys":["name":["nameInDs":"name"]]]]

    
    String name ="";
    

    static hasMany = [workedOnBy:Developer]
    
        static mapping = {
            tablePerHierarchy false
        }
    
    static constraints={
    name(blank:false,nullable:false,key:[])
        
     
    }

    static mappedBy=["workedOnBy":"worksOn"]
    static belongsTo = [Developer]
    static propertyConfiguration= [:]
    static transients = [];
    
    public String toString()
    {
    	return "${getClass().getName()}[name:$name]";
    }
    
    //AUTO_GENERATED_CODE
}