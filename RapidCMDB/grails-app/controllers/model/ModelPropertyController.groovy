package model

import datasource.BaseDatasource
import com.ifountain.rcmdb.util.RapidCMDBConstants;

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
        if (params["datasource.id"] != null && params["datasource.id"] != "null") {
            def baseDatasource = BaseDatasource.get(params["datasource.id"]);
            def currentModel = Model.get(params["model.id"]);
            def tempModel = currentModel;
            def modelDatasource = null;
            while (tempModel != null && !modelDatasource) {
                tempModel.datasources.each
                {
                    if (it.datasource.name == baseDatasource.name)
                    {
                        modelDatasource = it;
                        return;
                    }
                }
                tempModel = tempModel.parentModel;
            }


            if (!modelDatasource) {
                def isMaster = false;
                if (baseDatasource.name == RapidCMDBConstants.RCMDB && ModelDatasource.findByModelAndMaster(currentModel, true) == null) {
                    isMaster = true;
                }
                modelDatasource = new ModelDatasource(model: currentModel, datasource: baseDatasource, master: isMaster).save();
            }
            params.remove("datasource.id");
            params["propertyDatasource.id"] = modelDatasource.id;
        }
        else
        {
            params["propertyDatasource.id"] = "null";
        }
        def modelProperty = new ModelProperty(params)
        if (!modelProperty.hasErrors() && modelProperty.save()) {
            PropertyShouldBeCleared.findAllByModelNameAndPropertyName(modelProperty.model.name, modelProperty.name)*.delete(flush:true);
            PropertyShouldBeCleared prop1 = new PropertyShouldBeCleared(modelName:modelProperty.model.name, propertyName:modelProperty.name, isRelation:false);
            prop1.save(flush:true);
            flash.message = "ModelProperty ${modelProperty} created"
            redirect(action: show, controller: 'model', id: modelProperty.model?.id)
        }
        else {
            render(view: 'create', model: [modelProperty: modelProperty])
        }
    }

    def delete = {
        def modelProperty = ModelProperty.get(params.id)
        if (modelProperty) {
            def modelId = modelProperty.model?.id;
            def modelPropertyName = modelProperty.toString();
            try {
                modelProperty.delete(flush: true)
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
        def modelProperty = ModelProperty.get(params.id)
        if (modelProperty) {
            if (params["datasource.id"] != null && params["datasource.id"] != "null") {
                def baseDatasource = BaseDatasource.get(params["datasource.id"]);
                def currentModel = Model.get(params["model.id"]);
                def tempModel = currentModel;
                def modelDatasource = null;
                while (tempModel != null && !modelDatasource) {
                    tempModel.datasources.each
                    {
                        if (it.datasource.name == baseDatasource.name)
                        {
                            modelDatasource = it;
                            return;
                        }
                    }
                    tempModel = tempModel.parentModel;
                }


                if (!modelDatasource) {
                    def isMaster = false;
                    if (baseDatasource.name == RapidCMDBConstants.RCMDB && ModelDatasource.findByModelAndMaster(currentModel, true) == null) {
                        isMaster = true;
                    }
                    modelDatasource = new ModelDatasource(model: currentModel, datasource: baseDatasource, master: isMaster).save();
                }
                params.remove("datasource.id");
                params["propertyDatasource.id"] = modelDatasource.id;
            }
            else
            {
                params["propertyDatasource.id"] = "null";
            }
            modelProperty.properties = params
            if (!modelProperty.hasErrors() && modelProperty.save()) {
                PropertyShouldBeCleared.findAllByModelNameAndPropertyName(modelProperty.model.name, modelProperty.name)*.delete(flush:true);
                PropertyShouldBeCleared prop1 = new PropertyShouldBeCleared(modelName:modelProperty.model.name, propertyName:modelProperty.name, isRelation:false);
                prop1.save(flush:true);
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
