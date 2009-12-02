package connector

import datasource.JiraDatasource

class JiraConnector {
    static searchable = {
        except = ["errors", "__operation_class__", "__dynamic_property_storage__", "ds"];
    };
    static datasources = ["RCMDB":["keys":["name":["nameInDs":"name"]]]]
    Long id;
    Long version=0;
    Long rsInsertedAt =0;
    Long rsUpdatedAt =0;
    String name ="";
    String rsOwner = "p"
    JiraDatasource ds;
    Long reconnectInterval = 0;
    org.springframework.validation.Errors errors ;

    Object __operation_class__ ;

    Object __dynamic_property_storage__ ;
    static relations  =[
            ds:[type:JiraDatasource, isMany:false]
    ]
    static constraints={
      name(blank:false,nullable:false,key:[])
      ds(nullable:true)
      __operation_class__(nullable:true)

     __dynamic_property_storage__(nullable:true)

     errors(nullable:true)
    }
    static propertyConfiguration= [:]
    static transients = ["errors", "__operation_class__", "__dynamic_property_storage__", "ds"];

    public String toString()
    {
    	return name;
    }
}