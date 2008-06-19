
class Employee extends Person
{
    
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
    dept(blank:false,nullable:true)
        
     salary(blank:false,nullable:true)
        
     prevEmp(nullable:true)
        
     nextEmp(nullable:true)
        
     
    }

    static mappedBy=["prevEmp":"nextEmp", "manages":"managedBy", "nextEmp":"prevEmp"]
    static belongsTo = []
    static propertyConfiguration= [:]
    static transients = [];
    
    //AUTO_GENERATED_CODE
}