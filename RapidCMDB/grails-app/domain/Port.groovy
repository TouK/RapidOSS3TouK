
import com.ifountain.core.domain.annotations.*;

class Port extends DeviceAdapter
{
    
    //AUTO_GENERATED_CODE


    static datasources = [:]

    
    String portType ;
    
    String portNumber ;
    
    String portKey ;
    

    static hasMany = [:]

    static constraints={
    portKey(blank:true,nullable:true)

     portNumber(blank:true,nullable:true)

     portType(blank:true,nullable:true)

     
    }

    static mappedBy=[:]
    static belongsTo = []
    static propertyConfiguration= ["portNumber":["nameInDs":"PortNumber", "datasourceProperty":"smartDs", "lazy":false], "portKey":["nameInDs":"PortKey", "datasourceProperty":"smartDs", "lazy":true]]
    static transients = ["portNumber", "portKey"];
    
    //AUTO_GENERATED_CODE
}