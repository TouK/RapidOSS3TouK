
import com.ifountain.core.domain.annotations.*;

class Developer extends Employee implements com.ifountain.rcmdb.domain.GeneratedModel
{
    
    //AUTO_GENERATED_CODE


    static datasources = [:]

    
    String language ;
    

    static hasMany = [worksOn:Task]

    static constraints={
    language(blank:false,nullable:false)
        
     
    }

    static mappedBy=["worksOn":"workedOnBy"]
    static belongsTo = []
    static propertyConfiguration= [:]
    static transients = [];
    
    //AUTO_GENERATED_CODE
}