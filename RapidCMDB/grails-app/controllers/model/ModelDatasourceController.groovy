package model;

class ModelDatasourceController {

    def scaffold = ModelDatasource;

    def save = {
        def modelDatasource = new ModelDatasource(params);
        if(!modelDatasource.hasErrors() && modelDatasource.save()) {
            flash.message = "ModelDatasource ${modelDatasource.id} created"
            redirect(action:"show", controller:'model', id:_getModelId(modelDatasource))
        }
        else {
            _render(view:'create',model:[modelDatasource:modelDatasource])
        }
    }

    def delete = {
        def modelDatasource = ModelDatasource.get( params.id )
        if(modelDatasource) {
            def modelId = _getModelId(modelDatasource);
            modelDatasource.delete()
            flash.message = "ModelDatasource ${params.id} deleted"
            redirect(action:"show", controller:'model', id:modelId)
        }
        else {
            flash.message = "ModelDatasource not found with id ${params.id}"
            redirect(action:"list", controller:'modelDatasource')
        }
    }

    def _render = {Map params ->
        def view = params.view;
        def model = params.model;
        render(view:view, model:model);
    }
    def _getModelId = {modelDatasource ->
        return modelDatasource.model?.id;
    }
}
