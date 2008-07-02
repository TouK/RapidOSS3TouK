package model

import com.ifountain.rcmdb.util.RapidCMDBConstants
import com.ifountain.rcmdb.domain.util.ControllerUtils;
class ModelPropertyController {

    def list = {
        if(!params.max) params.max = 10
        [ modelPropertyList: ModelProperty.list( params ) ]
    }

    def edit = {
        def modelProperty = ModelProperty.get( [id:params.id] )

        if(!modelProperty) {
            flash.message = "ModelProperty not found with id \${params.id}"
            redirect(action:list)
        }
        else {
            return [ modelProperty : modelProperty ]
        }
    }

    def create = {
        def modelProperty = new ModelProperty()
        modelProperty.properties = params
        return ['modelProperty':modelProperty]
    }

    def show = {
        def modelProperty = ModelProperty.get(id:params.id)
        if (!modelProperty) {
            flash.message = "ModelProperty not found with id ${params.id}"
            redirect(action: list)
        }
        else {return [modelProperty: modelProperty]}
    }
    def save = {
        if (params["datasource.id"] != null && params["datasource.id"] != "null") {
            def datasourceName = DatasourceName.get(id:params["datasource.id"]);
            def currentModel = Model.get(id:params["model.id"]);
            def tempModel = currentModel;
            def modelDatasource = null;
            while (tempModel != null && !modelDatasource) {
                tempModel.datasources.each
                {
                    if (it.datasource.name == datasourceName.name)
                    {
                        modelDatasource = it;
                        return;
                    }
                }
                tempModel = tempModel.parentModel;
            }


            if (!modelDatasource) {
                def isMaster = false;
                if (datasourceName.name == RapidCMDBConstants.RCMDB ) {
                    isMaster = true;
                }
                modelDatasource = ModelDatasource.add(model: currentModel, datasource: datasourceName);
            }
            params["propertyDatasource"] = ["id":modelDatasource.id];
        }
        else
        {
            params["propertyDatasource"] = ["id":"null"];
        }
        def modelProperty = ModelProperty.add(ControllerUtils.getClassProperties(params, ModelProperty))
        if (!modelProperty.hasErrors()) {
            flash.message = "ModelProperty ${modelProperty} created"
            redirect(action: show, controller: 'model', id: modelProperty.model?.id)
        }
        else {
            render(view: 'create', model: [modelProperty: modelProperty])
        }
    }

    def delete = {
        def modelProperty = ModelProperty.get(id:params.id)
        if (modelProperty) {
            def modelId = modelProperty.model?.id;
            def modelPropertyName = modelProperty.toString();
            try {
                modelProperty.remove()
                flash.message = "Property ${modelPropertyName} deleted"
                redirect(action: show, controller: 'model', id: modelId)
            }
            catch (e) {
                def errors = [message(code: "default.couldnot.delete", args: [ModelProperty.class.getName(), modelProperty])]
                flash.errors = errors;
                redirect(action: show, id: modelProperty.id)
            }
        }
        else {
            flash.message = "Property not found"
            redirect(action: list)
        }
    }

    def update = {
        def modelProperty = ModelProperty.get(id:params.id)
        if (modelProperty) {
            if (params["datasource.id"] != null && params["datasource.id"] != "null") {
                def datasourceName = DatasourceName.get(id:params["datasource.id"]);
                def currentModel = Model.get(id:params["model.id"]);
                def tempModel = currentModel;
                def modelDatasource = null;
                while (tempModel != null && !modelDatasource) {
                    tempModel.datasources.each
                    {
                        if (it.datasource.name == datasourceName.name)
                        {
                            modelDatasource = it;
                            return;
                        }
                    }
                    tempModel = tempModel.parentModel;
                }


                if (!modelDatasource) {
                    def isMaster = false;
                    if (datasourceName.name == RapidCMDBConstants.RCMDB) {
                        isMaster = true;
                    }
                    modelDatasource = ModelDatasource.add(model: currentModel, datasource: datasourceName);
                }
                params["propertyDatasource"] = ["id":modelDatasource.id];
            }
            else
            {
                params["propertyDatasource"] = ["id":"null"];
            }
            modelProperty.update(ControllerUtils.getClassProperties(params, ModelProperty));
            if (!modelProperty.hasErrors()) {
                flash.message = "ModelProperty ${modelProperty} updated"
                redirect(action: "show", controller: 'model', id: _getModelId(modelProperty))
            }
            else {
                render(view: 'edit', model: [modelProperty: modelProperty])
            }
        }
        else {
            flash.message = "ModelProperty not found with id ${params.id}"
            redirect(action: edit, id: params.id)
        }
    }

    def _getModelId = {modelProperty ->
        return modelProperty.model?.id;
    }
}
