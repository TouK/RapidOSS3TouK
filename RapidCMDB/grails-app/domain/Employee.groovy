
import com.ifountain.core.domain.annotations.*;

class Employee  extends Person {  

    //AUTO_GENERATED_CODE

    static searchable = {
        except = ["manages", "prevEmp", "nextEmp"];
    };
    static datasources = [:]

    
    String dept ="";
    
    Long salary =1000;
    
    Employee prevEmp ;
    
    Employee nextEmp ;
    List manages = [];

    static relations = [manages:[isMany:true, type:Team, reverseName:"managedBy"],
            prevEmp:[isMany:false, type:Employee, reverseName:"nextEmp"],
            nextEmp:[isMany:false, type:Employee, reverseName:"prevEmp"],

    ]
    static constraints={
    dept(blank:true,nullable:true)
        
     salary(blank:true,nullable:true)
        
     prevEmp(nullable:true)
        
     nextEmp(nullable:true)
        
     
    }

    static propertyConfiguration= [:]
    static transients = [];
    
    //AUTO_GENERATED_CODE
    
}
