import com.ifountain.core.domain.annotations.*;


class DeviceComponent  extends SmartsObject {

    //AUTO_GENERATED_CODE

    static searchable = {
        except = [];
    };
    static datasources = [:]

    
    Device partOf ;
    

    static hasMany = [:]
    
    static constraints={
    partOf(nullable:true)
        
     
    }

    static mappedBy=["partOf":"composedOf"]
    static belongsTo = []
    static propertyConfiguration= [:]
    static transients = [];
    
    //AUTO_GENERATED_CODE







}
