
import com.ifountain.core.domain.annotations.*;

class Employee  extends Person {  

    //AUTO_GENERATED_CODE

    static searchable = {
        except = [];
    };
    static datasources = [:]

    
    String dept ="";
    
    Long salary =1000;
    
    Employee prevEmp ;
    
    Employee nextEmp ;
    

    static hasMany = [manages:Team]
    
    static constraints={
    dept(blank:true,nullable:true)
        
     salary(blank:true,nullable:true)
        
     prevEmp(nullable:true)
        
     nextEmp(nullable:true)
        
     
    }

    static mappedBy=["manages":"managedBy", "prevEmp":"nextEmp", "nextEmp":"prevEmp"]
    static belongsTo = []
    static propertyConfiguration= [:]
    static transients = [];
    
    //AUTO_GENERATED_CODE
    
}
