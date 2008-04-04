package model

import datasource.BaseDatasource;
class ModelDatasource {
    BaseDatasource datasource;
    boolean master = false;
    Model model;
    static belongsTo = Model;

    static hasMany = [keyMappings: ModelDatasourceKeyMapping];

    static constraints = {
        datasource(unique:'model');
        master(validator: {val, obj ->
            if (val){
                def isValid = true;
                ModelDatasource.findAllByModelAndMaster(obj.model, true).each
                {
                    if(it.datasource.name != obj.datasource.name)
                    {
                        isValid = false;
                        return;
                    }
                }
                if(!isValid){
                    return ['model.invalid.master'];
                }
            }
        })

        model(validator: {val, obj ->
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
