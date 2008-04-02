package model;

class ModelDatasourceController {

    def scaffold = ModelDatasource;
    def show = {
        def modelDatasource = ModelDatasource.get(params.id)
        if (!modelDatasource) {
            flash.message = "ModelDatasource not found with id ${params.id}"
            redirect(action: list)
        }
        else {return [modelDatasource: modelDatasource]}
    }


    def save = {
        def modelDatasource = new ModelDatasource(params);
        if(!modelDatasource.hasErrors() && modelDatasource.save()) {
            flash.message = "ModelDatasource ${modelDatasource} created"
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
            def modelDatasourceName = modelDatasource.toString();
            try{
                modelDatasource.delete(flush:true)
                flash.message = "ModelDatasource ${modelDatasourceName} deleted"
            }
            catch(e){
                def errors =[message(code:"model.couldnot.delete", args:[ModelDatasource.class.getName(), modelDatasource, e.getMessage()])]
                flash.errors = errors;
            }
            redirect(action:"show", controller:'model', id:modelId)
        }
        else {
            flash.message = "ModelDatasource not found with id ${params.id}"
            redirect(action:"list", controller:'modelDatasource')
        }
    }

    def update = {
        def modelDatasource = ModelDatasource.get( params.id )
        if(modelDatasource) {
            modelDatasource.properties = params
            if(!modelDatasource.hasErrors() && modelDatasource.save()) {
                flash.message = "ModelDatasource ${modelDatasource} updated"
                redirect(action:"show",controller:'model', id:_getModelId(modelDatasource))
            }
            else {
                render(view:'edit',model:[modelDatasource:modelDatasource])
            }
        }
        else {
            flash.message = "ModelDatasource not found with id ${params.id}"
            redirect(action:edit,id:params.id)
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
