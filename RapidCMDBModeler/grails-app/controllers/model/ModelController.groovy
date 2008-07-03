package model

import com.ifountain.rcmdb.domain.generation.ModelGenerator
import org.apache.commons.io.FileUtils;
import com.ifountain.rcmdb.domain.util.ControllerUtils;
class ModelController {
    def static String MODEL_DOESNOT_EXIST = "Model does not exist";

    def index = {redirect(action: list, params: params)}
    def list = {
        if(!params.max) params.max = 10
        [ modelList: Model.list( params ) ]
    }

    def edit = {
        def model = Model.get( [id:params.id] )

        if(!model) {
            flash.message = "Model not found with id \${params.id}"
            redirect(action:list)
        }
        else {
            return [ model : model ]
        }
    }

    def create = {
        def model = new Model()
        model.properties = params
        return ['model':model]
    }

    def save = {
        def model = Model.add(ControllerUtils.getClassProperties(params, Model))
        if (!model.hasErrors()) {
            flash.message = "Model ${model.id} created"
            redirect(action: show, id: model.id)
        }
        else {
            render(view: 'create', model: [model: model])
        }
    }

    def update = {
        def model = Model.get(id:params.id)
        if (model) {
            //validating parentModel, should be implemented in validator
            if (model.parentModel && (params.parentModel.id == null || params.parentModel.id == "null")) {
                def keyMappingReference = false;
                def invalidDatasource = null;
                def invalidKeyMapping = null;
                def invalidProperty = null;
                model.datasources.each {ds ->
                    ds.keyMappings.each {keyMapping ->
                        if (keyMapping.property.model == model.parentModel) {
                            keyMappingReference = true;
                            invalidDatasource = ds;
                            invalidKeyMapping = keyMapping;
                            return;

                        }
                    }
                }
                if (keyMappingReference) {
                    def errors = [message(code: "model.parent.keymapping.reference", args: [invalidKeyMapping, invalidDatasource, model])]
                    flash.errors = errors;
                    render(view: 'edit', model: [model: model])
                    return;
                }

                def modelDatasourceReference = false;
                model.modelProperties.each {p ->
                    if (p.propertyDatasource?.model == model.parentModel) {
                        modelDatasourceReference = true;
                        invalidDatasource = p.propertyDatasource;
                        invalidProperty = p;
                        return;
                    }
                }
                if (modelDatasourceReference) {
                    def errors = [message(code: "model.parent.modelDatasource.reference", args: [invalidProperty, model, invalidDatasource])]
                    flash.errors = errors;
                    render(view: 'edit', model: [model: model])
                    return;
                }
            }
            model.update(ControllerUtils.getClassProperties(params, Model));
            if (!model.hasErrors()) {
                flash.message = "Model ${params.id} updated"
                redirect(action: show, id: model.id)
            }
            else {
                render(view: 'edit', model: [model: model])
            }
        }
        else {
            flash.message = "Model not found with id ${params.id}"
            redirect(action: edit, id: params.id)
        }
    }

    def show = {
        def modelPropertySortProp = params.modelPropertySortProp != null ? params.modelPropertySortProp : "name"
        def modelPropertySortOrder = params.modelPropertySortOrder != null ? params.modelPropertySortOrder : "asc"
        def modelDatasourceSortProp = params.modelDatasourceSortProp != null ? params.modelDatasourceSortProp : "datasource"
        def modelDatasourceSortOrder = params.modelDatasourceSortOrder != null ? params.modelDatasourceSortOrder : "asc"
        def modelRelationSortProp = params.modelRelationSortProp != null ? params.modelRelationSortProp : "name"
        def modelRelationSortOrder = params.modelRelationSortOrder != null ? params.modelRelationSortOrder : "asc"
        def modelOpertionSortProp = params.modelOpertionSortProp != null ? params.modelOpertionSortProp : "name"
        def modelOpertionSortOrder = params.modelOpertionSortOrder != null ? params.modelOpertionSortOrder : "asc"

        def model = Model.get(id:params.id)
        if (!model) {
            flash.message = MODEL_DOESNOT_EXIST
            redirect(action: list)
        }
        else {return [model: model,
                modelPropertySortProp: modelPropertySortProp, modelPropertySortOrder: modelPropertySortOrder,
                modelDatasourceSortProp: modelDatasourceSortProp, modelDatasourceSortOrder: modelDatasourceSortOrder,
                modelRelationSortProp: modelRelationSortProp, modelRelationSortOrder: modelRelationSortOrder,
                modelOpertionSortProp: modelOpertionSortProp, modelOpertionSortOrder: modelOpertionSortOrder
        ]}
    }

    def delete = {
        if (params.id)
        {
            def model = Model.get(id:params.id)
            if (model) {
                try {
                    model.remove()
                }
                catch (e)
                {
                    def errors = [message(code: "default.couldnot.delete", args: [Model.class.getName(), model])]
                    flash.errors = errors;
                    redirect(action: show, controller: 'model', id: model?.id)
                    return;

                }
                try
                {
                    def models = Model.list();
                    FileUtils.deleteDirectory (ModelGenerator.getInstance().getTempModelDir());
                    com.ifountain.rcmdb.domain.generation.ModelGeneratorAdapter.generateModels(models);
                    flash.message = "Model ${params.id} deleted"
                    redirect(action: list, controller: 'model');
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    flash.message = "Model deleted but and unexpected exception occured while generating dependent models. Reason:${e.getMessage()}";
                    redirect(action: list, controller: 'model')
                }

            }
            else {
                flash.message = MODEL_DOESNOT_EXIST
                redirect(action: list, controller: 'model')
            }
        }
        else
        {
            redirect(action: list, controller: 'model')
        }
    }


    def generate = {
            def models = Model.list();
            try
            {
                FileUtils.deleteDirectory (ModelGenerator.getInstance().getTempModelDir());
                com.ifountain.rcmdb.domain.generation.ModelGeneratorAdapter.generateModels(models);
                flash.message = "Models genarated successfully"
                redirect(action: list, controller: 'model')
            }
            catch (Exception e)
            {
                log.error("Exception occurred while generating models", e);
                flash.message = e.getMessage();
                redirect(action: list, controller: 'model')
            }
    }
}
