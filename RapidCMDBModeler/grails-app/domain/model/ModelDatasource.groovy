package model

class ModelDatasource {
    static searchable = {
        except:["model","keyMappings","datasource"]
    };
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
