import com.ifountain.core.domain.annotations.*;


class DeviceInterface  extends DeviceAdapter {

    //AUTO_GENERATED_CODE

    static searchable = true;
    static datasources = [:]

    
    String interfaceKey ;
    
    Ip underlying ;
    

    static hasMany = [:]

    

    static constraints={
    interfaceKey(blank:true,nullable:true)
        
     underlying(nullable:true)
        
     
    }

    static mappedBy=["underlying":"layeredOver"]
    static belongsTo = []
    static propertyConfiguration= ["interfaceKey":["nameInDs":"InterfaceKey", "datasourceProperty":"smartDs", "lazy":true]]
    static transients = ["interfaceKey"];
    
    //AUTO_GENERATED_CODE






}
