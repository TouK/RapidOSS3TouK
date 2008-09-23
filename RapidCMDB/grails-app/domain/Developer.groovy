
import com.ifountain.core.domain.annotations.*;

class Developer extends Employee  
{
    
    //AUTO_GENERATED_CODE

    static searchable = {
        except = ["worksOn"];
    };
    static datasources = [:]

    
    String language ="";
    List worksOn = [];

    static relations = [worksOn:[isMany:true, type:Task, reverseName:"workedOnBy"]]    
    static constraints={
    language(blank:true,nullable:true)
        
     
    }

    static propertyConfiguration= [:]
    static transients = [];
    
    //AUTO_GENERATED_CODE
}