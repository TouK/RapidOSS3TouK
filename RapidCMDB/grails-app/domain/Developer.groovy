
import com.ifountain.core.domain.annotations.*;

class Developer extends Employee  
{
    
    //AUTO_GENERATED_CODE

    static searchable = {
        except = [];
    };
    static datasources = [:]

    
    String language ="";
    

    static hasMany = [worksOn:Task]
    
    static constraints={
    language(blank:true,nullable:true)
        
     
    }

    static mappedBy=["worksOn":"workedOnBy"]
    static belongsTo = []
    static propertyConfiguration= [:]
    static transients = [];
    
    //AUTO_GENERATED_CODE
}