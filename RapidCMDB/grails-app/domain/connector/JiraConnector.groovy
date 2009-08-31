package connector

import datasource.JiraDatasource

class JiraConnector {
    static searchable = {
        except = ["errors", "__operation_class__", "__is_federated_properties_loaded__", "ds"];
    };
    static datasources = ["RCMDB":["keys":["name":["nameInDs":"name"]]]]
    Long id;
    Long version=0;
    Date rsInsertedAt = new Date(0);
    Date rsUpdatedAt  = new Date(0);
    String name ="";
    String rsOwner = "p"
    JiraDatasource ds;
    Long reconnectInterval = 0;
    org.springframework.validation.Errors errors ;

    Object __operation_class__ ;

    Object __is_federated_properties_loaded__ ;
    static relations  =[
            ds:[type:JiraDatasource, isMany:false]
    ]
    static constraints={
      name(blank:false,nullable:false,key:[])
      ds(nullable:true)
      __operation_class__(nullable:true)

     __is_federated_properties_loaded__(nullable:true)

     errors(nullable:true)
    }
    static propertyConfiguration= [:]
    static transients = ["errors", "__operation_class__", "__is_federated_properties_loaded__", "ds"];

    public String toString()
    {
    	return name;
    }
}