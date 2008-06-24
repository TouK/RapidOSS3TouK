
import com.ifountain.core.domain.annotations.*;

class Port extends DeviceAdapter
{
    
    //AUTO_GENERATED_CODE

    static searchable = {
        except = ["portKey", "portNumber"];
    };
    static datasources = [:]

    
    String portKey ="";
    
    String portNumber ="";
    
    String portType ="";
    

    static hasMany = [:]
    
    static constraints={
    portKey(blank:true,nullable:true)
        
     portNumber(blank:true,nullable:true)
        
     portType(blank:true,nullable:true)
        
     
    }

    static mappedBy=[:]
    static belongsTo = []
    static propertyConfiguration= ["portKey":["nameInDs":"PortKey", "datasourceProperty":"smartDs", "lazy":true], "portNumber":["nameInDs":"PortNumber", "datasourceProperty":"smartDs", "lazy":false]]
    static transients = ["portKey", "portNumber"];
    
    //AUTO_GENERATED_CODE
}