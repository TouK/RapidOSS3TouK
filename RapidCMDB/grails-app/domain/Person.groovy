
import com.ifountain.core.domain.annotations.*;

class Person {

    //AUTO_GENERATED_CODE

    static searchable = {
        except = [];
    };
    static datasources = ["RCMDB":["master":true, "keys":["name":["nameInDs":"name"]]]]

    
    String bday ="";
    
    String name ="";
    
    static relations  =[:]
    static constraints={
    bday(blank:true,nullable:true)
        
     name(blank:false,nullable:false,key:[])
        
     
    }

    static propertyConfiguration= [:]
    static transients = [];
    
    public String toString()
    {
    	return "${getClass().getName()}[name:$name]";
    }

    public boolean equals(Object obj) {
        return obj.getProperty("id").longValue() == this.getProperty("id").longValue();
    }
    
    //AUTO_GENERATED_CODE
    
}
