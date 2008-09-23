
import com.ifountain.core.domain.annotations.*;

class Task {

    //AUTO_GENERATED_CODE

    static searchable = {
        except = ["workedOnBy"];
    };
    static datasources = ["RCMDB":["master":true, "keys":["name":["nameInDs":"name"]]]]

    
    String name ="";
    List workedOnBy = [];

    static relations = [workedOnBy:[isMany:true, type:Developer, reverseName:"worksOn"]
    ]
    static constraints={
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
