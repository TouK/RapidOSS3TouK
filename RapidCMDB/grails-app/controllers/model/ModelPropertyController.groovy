package model

import datasource.BaseDatasource;

class ModelPropertyController {

    def scaffold = ModelProperty;

    def show = {
        def modelProperty = ModelProperty.get(params.id)
        if (!modelProperty) {
            flash.message = "ModelProperty not found with id ${params.id}"
            redirect(action: list)
        }
        else {return [modelProperty: modelProperty]}
    }
     def save = {
        
        if(params.name)
        {
            if(params.name.length() > 1)
            {
                def firstChar = params.name.substring (0,1)
                def remaining = params.name.substring (1);
                params.name = firstChar.toLowerCase()+remaining;
            }
            else
            {
                params.name = params.name.toLowerCase();                
            }
        }
        if(params["datasource.id"] != null && params["datasource.id"] != "null"){
            def baseDatasource = BaseDatasource.get(params["datasource.id"]);
            def modelNameList = [];
            def currentModel = Model.get(params["model.id"]);
            modelNameList.add(currentModel.name);
            def tempModel = currentModel.parentModel;
            while(tempModel != null){
                modelNameList.add(tempModel.name);
                tempModel = tempModel.parentModel;
            }
            def c = ModelDatasource.createCriteria();
            def modelDatasource = c.get{
                model{
                    'in'('name', modelNameList)
                }
                datasource{
                    eq('name', baseDatasource.name)
                }
            }
//            def modelDatasource = ModelDatasource.findByModelAndDatasource(model, baseDatasource);
            if(modelDatasource == null){
                println "couldnt find datasource";
                modelDatasource = new ModelDatasource(model:currentModel, datasource:baseDatasource, master:false).save();
            }
            params.remove("datasource.id");
            params["propertyDatasource.id"] = modelDatasource.id;
        }
        def modelProperty = new ModelProperty(params)
        if(!modelProperty.hasErrors() && modelProperty.save()) {
            flash.message = "ModelProperty ${modelProperty} created"
            redirect(action:show,controller:'model', id:modelProperty.model?.id)
        }
        else {
            render(view:'create',model:[modelProperty:modelProperty])
        }
    }

    def delete = {
        def modelProperty = ModelProperty.get( params.id )
        if(modelProperty) {
            def modelId = modelProperty.model?.id;
            def modelPropertyName = modelProperty.toString();
            modelProperty.delete()
            flash.message = "Property ${modelPropertyName} deleted"
            redirect(action:show, controller:'model', id:modelId)
        }
        else {
            flash.message = "Property not found"
            redirect(action:list)
        }
    }

    def update = {
        if(params.name)
        {
            if(params.name.length() > 1)
            {
                def firstChar = params.name.substring (0,1)
                def remaining = params.name.substring (1);
                params.name = firstChar.toLowerCase()+remaining;
            }
            else
            {
                params.name = params.name.toLowerCase();
            }
        }
        def modelProperty = ModelProperty.get( params.id )
        if(modelProperty) {
            if(params["datasource.id"] != null && params["datasource.id"] != "null"){
                def baseDatasource = BaseDatasource.get(params["datasource.id"]);
               def modelNameList = [];
                def currentModel = Model.get(params["model.id"]);
                modelNameList.add(currentModel.name);
                def tempModel = currentModel.parentModel;
                while(tempModel != null){
                    modelNameList.add(tempModel.name);
                    tempModel = tempModel.parentModel;
                }
                def c = ModelDatasource.createCriteria();
                def modelDatasource = c.get{
                    model{
                        'in'('name', modelNameList)
                    }
                    datasource{
                        eq('name', baseDatasource.name)
                    }
                }
                if(modelDatasource == null){
                    modelDatasource = new ModelDatasource(model:currentModel, datasource:baseDatasource, master:false).save();
                }
                params.remove("datasource.id");
                params["propertyDatasource.id"] = modelDatasource.id;
            }
            modelProperty.properties = params
            if(!modelProperty.hasErrors() && modelProperty.save()) {
                flash.message = "ModelProperty ${modelProperty} updated"
                redirect(action:"show",controller:'model', id:_getModelId(modelProperty))
            }
            else {
                render(view:'edit',model:[modelProperty:modelProperty])
            }
        }
        else {
            flash.message = "ModelProperty not found with id ${params.id}"
            redirect(action:edit,id:params.id)
        }
    }

     def _getModelId = {modelProperty ->
        return modelProperty.model?.id;
    }
}
