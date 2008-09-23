
import com.ifountain.core.domain.annotations.*;

class Team {

    //AUTO_GENERATED_CODE

    static searchable = {
        except = ["managedBy"];
    };
    static datasources = ["RCMDB":["master":true, "keys":["name":["nameInDs":"name"]]]]

    
    String maskot ="";
    
    String name ="";
    
    Employee managedBy ;
    
    static relations = [managedBy:[isMany:false, type:Employee, reverseName:"manages"]
    ]
    static constraints={
    maskot(blank:true,nullable:true)
        
     name(blank:false,nullable:false,key:[])
        
     managedBy(nullable:true)
        
     
    }

    static propertyConfiguration= [:]
    static transients = [];
    
    public String toString()
    {
    	return "${getClass().getName()}[name:$name]";
    }

    public boolean equals(Object obj) {
        return obj.id == this.id;
    }
    
    //AUTO_GENERATED_CODE
    
}
