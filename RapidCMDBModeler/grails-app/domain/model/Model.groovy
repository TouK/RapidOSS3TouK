package model

import org.codehaus.groovy.grails.commons.ConfigurationHolder

class Model {
    static searchable = {
        except = ["fromRelations", "toRelations", "modelProperties", "datasources", "parentModel"];
    };
    String name;
    String rsOwner = "p"
    Boolean resourcesWillBeGenerated = false;
    Model parentModel;
    List modelProperties = [];
    List datasources = [];
    List fromRelations = [];
    List toRelations = [];
    static relations = [
            parentModel:[type:Model, isMany:false],
            modelProperties:[type:ModelProperty, reverseName:"model", isMany:true],
            datasources:[type:ModelDatasource, reverseName:"model", isMany:true],
            fromRelations:[type:ModelRelation, reverseName:"firstModel", isMany:true],
            toRelations:[type:ModelRelation, reverseName:"secondModel", isMany:true],
    ]
    static cascaded = ["datasources":true, "fromRelations":true, "toRelations":true, modelProperties:true]
    static constraints = {
        name(blank:false, key:[], validator:{val, obj ->
            def firstChar = val.charAt(0);
            if(!(firstChar >= 65 && firstChar <= 90)){
                return ['model.name.lowercased'];
            }
            def invalidNames = ConfigurationHolder.config.flatten().get("rapidcmdb.invalid.names")
            if(invalidNames.contains(val.toLowerCase()))
            {
                return ['model.name.invalid'];                
            }
        });
        parentModel(nullable:true);
        resourcesWillBeGenerated(nullable:true);
    }
//
//    def getOperations()
//    {
//        def operations = [];
//        if(name)
//        {
//            Class cls = ApplicationHolder.application.getClassForName(name)
//            if(cls)
//            {
//                def methods = cls.getDeclaredMethods();
//                methods.each{domainMethod->
//                    def annotation = domainMethod.getAnnotation(CmdbOperation.class)
//                    if(annotation)
//                    {
//                        operations += [name:domainMethod.getName(), description:annotation.description()];
//                        return;
//                    }
//                }
//            }
//        }
//        return operations;
//    }
        
    String toString(){
        return "$name";
    }

}
