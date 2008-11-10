package model

class ModelDatasource {
    static searchable = {
        except:["model","keyMappings","datasource", "errors", "__operation_class__", "__is_federated_properties_loaded__"]
    };
    Long id;
    Long version;
    org.springframework.validation.Errors errors;
    Object __operation_class__;
    Object __is_federated_properties_loaded__;
    DatasourceName datasource;
    Model model;
    String rsOwner = "p"
    List keyMappings = [];
    static relations = [
            datasource:[type:DatasourceName, reverseName:"modelDatasources", isMany:false],
            model:[type:Model, reverseName:"datasources", isMany:false],
            keyMappings:[type:ModelDatasourceKeyMapping, reverseName:"datasource", isMany:true],
    ]
    static cascaded = ["keyMappings":true]
    static constraints = {
         __operation_class__(nullable: true)
        __is_federated_properties_loaded__(nullable: true)
        errors(nullable: true)
        model(key:["datasource"], validator: {val, obj ->
            def error = null;
            def tempModel = val.parentModel;
            while(tempModel)
            {
                tempModel.datasources.each
                {
                    if(it.datasource.name == obj.datasource.name)
                    {
                        error = ['model.datasource.override', tempModel, it]
                        return;
                    }
                }
                tempModel = tempModel.parentModel;
            }
            return error;
        })
    }
    String toString(){
        return getProperty("datasource").name;  
    }
}
