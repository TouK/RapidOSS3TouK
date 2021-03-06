

class RrdArchive
{

    //AUTO_GENERATED_CODE

    static searchable = {
        except = ["errors", "__operation_class__", "__dynamic_property_storage__", "variables"];


    };
     static datasources = ["RCMDB":["mappedName":"RCMDB", "keys":["id":["nameInDs":"id"]]]]

    String function ="AVERAGE";

    Double xff =0.5;

    Long step =1;

    Long numberOfDatapoints =100;

    org.springframework.validation.Errors errors ;

    Long id ;

    Long version ;

    Long rsInsertedAt =0;

    Long rsUpdatedAt =0;

    Object __operation_class__ ;

    Object __dynamic_property_storage__ ;

    List variables =[];


    static relations = [

        variables:[type:RrdVariable, reverseName:"archives", isMany:true]

    ]

    static constraints={

     function(blank:true,nullable:true)

     xff(nullable:true)

     step(nullable:true)

     numberOfDatapoints(nullable:true)

     errors(nullable:true)

     __operation_class__(nullable:true)

     __dynamic_property_storage__(nullable:true)


    }

    static propertyConfiguration= [:]
    static transients = ["errors", "__operation_class__", "__dynamic_property_storage__", "variables"];

    public String toString()
    {
    	return "${getClass().getName()}[id:${getProperty("id")}]";
    }

    public boolean equals(Object obj) {
        return obj.id == this.id;
    }
    //AUTO_GENERATED_CODE
}