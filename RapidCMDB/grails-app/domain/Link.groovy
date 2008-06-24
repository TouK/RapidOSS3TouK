
import com.ifountain.core.domain.annotations.*;

class Link extends SmartsObject
{
    
    //AUTO_GENERATED_CODE

    static searchable = {
        except = [];
    };
    static datasources = [:]

    

    static hasMany = [connectedTo:DeviceAdapter, connectedSystems:Device]
    
    static constraints={
    
    }

    static mappedBy=["connectedTo":"connectedVia", "connectedSystems":"connectedVia"]
    static belongsTo = [Device]
    static propertyConfiguration= [:]
    static transients = [];
    
    //AUTO_GENERATED_CODE
}