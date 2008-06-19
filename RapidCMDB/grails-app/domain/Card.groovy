class Card  extends DeviceComponent {

    //AUTO_GENERATED_CODE

    static searchable = {
        except = ["status"];
    };
    static datasources = [:]

    
    String status ;
    

    static hasMany = [realises:DeviceAdapter]
    
    static constraints={
    status(blank:true,nullable:true)
        
     
    }

    static mappedBy=["realises":"realizedBy"]
    static belongsTo = []
    static propertyConfiguration= ["status":["nameInDs":"Status", "datasourceProperty":"smartDs", "lazy":true]]
    static transients = ["status"];
    
    //AUTO_GENERATED_CODE







}
