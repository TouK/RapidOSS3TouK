import com.ifountain.core.domain.annotations.*;


class DeviceComponent extends SmartsObject implements com.ifountain.rcmdb.domain.GeneratedModel
{

    //AUTO_GENERATED_CODE


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
