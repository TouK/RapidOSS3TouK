
import com.ifountain.core.domain.annotations.*;

class Task {

    //AUTO_GENERATED_CODE

    static searchable = {
        except = ["workedOnBy"];
    };
    static datasources = ["RCMDB":["master":true, "keys":["name":["nameInDs":"name"]]]]

    
    String name ="";
    List workedOnBy = [];

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
