
import com.ifountain.core.domain.annotations.*;

class Employee extends Person implements com.ifountain.rcmdb.domain.GeneratedModel
{
    
    //AUTO_GENERATED_CODE


    static datasources = [:]

    
    Long salary =1000;
    
    String dept ;
    
    Employee prevEmp ;
    
    Employee nextEmp ;
    
    Employee manager ;
    

    static hasMany = [employees:Employee, manages:Team]

    static constraints={
    salary(blank:false,nullable:false)
        
     dept(blank:false,nullable:false)
        
     prevEmp(nullable:true)
        
     nextEmp(nullable:true)
        
     manager(nullable:true)
        
     
    }

    static mappedBy=["prevEmp":"nextEmp", "employees":"manager", "manages":"managedBy", "nextEmp":"prevEmp", "manager":"employees"]
    static belongsTo = []
    static propertyConfiguration= [:]
    static transients = [];
    
    //AUTO_GENERATED_CODE
}