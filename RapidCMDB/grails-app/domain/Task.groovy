
import com.ifountain.core.domain.annotations.*;

class Task
{
    
    //AUTO_GENERATED_CODE


    static datasources = ["RCMDB":["master":true, "keys":["name":["nameInDs":"name"]]]]

    
    String name ;
    

    static hasMany = [workedOnBy:Developer]

    static constraints={
    name(unique:true)
        
     
    }

    static mappedBy=["workedOnBy":"worksOn"]
    static belongsTo = [Developer]
    static propertyConfiguration= [:]
    static transients = [];
    
    public String toString()
    {
    	return "${getClass().getName()}[name:$name]";
    }
    
    //AUTO_GENERATED_CODE
}