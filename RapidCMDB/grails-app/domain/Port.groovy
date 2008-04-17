import com.ifountain.core.domain.annotations.*;


class Port extends DeviceAdapter implements com.ifountain.domain.GeneratedModel
{

    //AUTO_GENERATED_CODE


    static datasources = [:]

    
    String portType ;
    
    String portKey ;
    
    String portNumber ;
    

    static hasMany = [:]

    static constraints={
    portType(blank:true,nullable:true)
        
     portKey(blank:true,nullable:true)
        
     portNumber(blank:true,nullable:true)
        
     
    }

    static mappedBy=[:]
    static belongsTo = []
    static propertyConfiguration= ["portKey":["nameInDs":"PortKey", "datasourceProperty":"smartDs", "lazy":true], "portNumber":["nameInDs":"PortNumber", "datasourceProperty":"smartDs", "lazy":false]]
    static transients = ["portKey", "portNumber"];
    
    //AUTO_GENERATED_CODE    
}
