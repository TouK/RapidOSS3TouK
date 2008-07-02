package model

import org.codehaus.groovy.grails.commons.ConfigurationHolder

class ModelRelation {
     static searchable = true;
     public static String ONE = "One";
     public static String MANY = "Many";
     Model firstModel;
     Model secondModel;
     String firstName;
     String secondName;
     String firstCardinality;
     String secondCardinality;
     static belongsTo=[firstModel:Model, secondModel:Model];
    static mappedBy = [firstModel:'fromRelations', secondModel:'toRelations']
     static constraints = {
         firstCardinality(inList:[ONE, MANY]);
         secondCardinality(inList:[ONE, MANY]);
         firstName(blank:false, key:['firstModel', 'secondName','secondModel'], validator:{val, obj ->
            if(!val.matches(ConfigurationHolder.config.toProperties()["rapidcmdb.property.validname"])){
                return ['modelrelation.name.not.match'];
            }
            def invalidNames = ConfigurationHolder.config.flatten().get("rapidcmdb.invalid.names")
            if(invalidNames.contains(val.toLowerCase()))
            {
                return ['modelrelation.name.invalid'];
            }
        });
        secondName(blank:false, validator:{val, obj ->
            if(!val.matches(ConfigurationHolder.config.toProperties()["rapidcmdb.property.validname"])){
                return ['modelrelation.name.not.match'];
            }
            def invalidNames = ConfigurationHolder.config.flatten().get("rapidcmdb.invalid.names")
            if(invalidNames.contains(val.toLowerCase()))
            {
                return ['modelrelation.name.invalid'];
            }
        });
     }

     String toString(){
         return "$firstName";
     }
}
