package model

import org.codehaus.groovy.grails.commons.ConfigurationHolder

class ModelRelation {
     public static String ONE = "One";
     public static String MANY = "Many";
     Model firstModel;
     Model secondModel;
     String firstName;
     String secondName;
     String firstCardinality;
     String secondCardinality;
     static belongsTo=[firstModel:Model, secondModel:Model];
     static constraints = {
         firstCardinality(inList:[ONE, MANY]);
         secondCardinality(inList:[ONE, MANY]);
         firstName(blank:false, unique:'firstModel', validator:{val, obj ->
            if(!val.matches(ConfigurationHolder.config.toProperties()["rapidcmdb.property.validname"])){
                return ['modelrelation.name.not.match'];
            }
            def invalidNames = ConfigurationHolder.config.flatten().get("rapidcmdb.invalid.names")
            if(invalidNames.contains(val.toLowerCase()))
            {
                return ['modelrelation.name.invalid'];
            }
        });
        secondName(blank:false, unique:'secondModel', validator:{val, obj ->
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
     
     def xml(){
		def relation = {
			relation{
				firstModel(firstModel.name)
				secondModel(secondModel.name)
				firstName(firstName)
				secondName(secondName)
				firstCardinality(firstCardinality)
				secondCardinality(secondCardinality)
			}
		}
		
		return relation;
     }     
}
