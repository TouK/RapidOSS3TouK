package model

class ModelDatasource {
    static searchable = true;
    DatasourceName datasource;
    Model model;
    static belongsTo = Model;

    static hasMany = [keyMappings: ModelDatasourceKeyMapping];
    static mappedBy = [model:'datasources',keyMappings:'datasource',datasource:"modelDatasources"]
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
        return datasource.name;  
    }
}
