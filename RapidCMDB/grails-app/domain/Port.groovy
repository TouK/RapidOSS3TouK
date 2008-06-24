
import com.ifountain.core.domain.annotations.*;

class Port extends DeviceAdapter
{
    
    //AUTO_GENERATED_CODE

    static searchable = {
        except = ["portNumber", "portKey"];
    };
    static datasources = [:]

    
    String portNumber ="";
    
    String portKey ="";
    
    String portType ="";
    

    static hasMany = [:]
    
    static constraints={
    portNumber(blank:true,nullable:true)
        
     portKey(blank:true,nullable:true)
        
     portType(blank:true,nullable:true)
        
     
    }

    static mappedBy=[:]
    static belongsTo = []
    static propertyConfiguration= ["portNumber":["nameInDs":"PortNumber", "datasourceProperty":"smartDs", "lazy":false], "portKey":["nameInDs":"PortKey", "datasourceProperty":"smartDs", "lazy":true]]
    static transients = ["portNumber", "portKey"];
    
    //AUTO_GENERATED_CODE
}