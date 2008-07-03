package model;
import com.ifountain.rcmdb.domain.util.ControllerUtils;
class ModelDatasourceController {

	def index = {redirect(action: list, params: params)}
    def list = {
        if(!params.max) params.max = 10
        [ modelDatasourceList: ModelDatasource.list( params ) ]
    }

    def edit = {
        def modelDatasource = ModelDatasource.get( [id:params.id] )

        if(!modelDatasource) {
            flash.message = "ModelDatasource not found with id \${params.id}"
            redirect(action:list)
        }
        else {
            return [ modelDatasource : modelDatasource ]
        }
    }

    def create = {
        def modelDatasource = new ModelDatasource()
        modelDatasource.properties = params
        return ['modelDatasource':modelDatasource]
    }
    def show = {
        def keyMappingSortProp = params.keyMappingSortProp != null ? params.keyMappingSortProp : "property"
        def keyMappingSortOrder = params.keyMappingSortOrder != null ? params.keyMappingSortOrder : "asc"
        def modelDatasource = ModelDatasource.get(id:params.id)
        if (!modelDatasource) {
            flash.message = "ModelDatasource not found with id ${params.id}"
            redirect(action: list)
        }
        else {return [modelDatasource: modelDatasource, keyMappingSortOrder:keyMappingSortOrder, keyMappingSortProp:keyMappingSortProp]}
    }


    def save = {
        def modelDatasource = ModelDatasource.add(ControllerUtils.getClassProperties(params, ModelDatasource));
        if(!modelDatasource.hasErrors()) {
            flash.message = "ModelDatasource ${modelDatasource} created"
            redirect(action:"show", controller:'model', id:_getModelId(modelDatasource))
        }
        else {
            _render(view:'create',model:[modelDatasource:modelDatasource])
        }
    }

    def delete = {
        def modelDatasource = ModelDatasource.get( id:params.id )
        if(modelDatasource) {
            def modelId = _getModelId(modelDatasource);
            def modelDatasourceName = modelDatasource.toString();
            try{
                modelDatasource.remove()
                flash.message = "ModelDatasource ${modelDatasourceName} deleted"
                redirect(action:"show", controller:'model', id:modelId)
            }
            catch(e){
                def errors =[message(code:"default.couldnot.delete", args:[ModelDatasource.class.getName(), modelDatasource])]
                flash.errors = errors;
                redirect(action:show, id:modelDatasource.id)
            }

        }
        else {
            flash.message = "ModelDatasource not found with id ${params.id}"
            redirect(action:"list", controller:'modelDatasource')
        }
    }

    def update = {
        def modelDatasource = ModelDatasource.get( id:params.id )
        if(modelDatasource) {
            modelDatasource.update(ControllerUtils.getClassProperties(params, ModelDatasource));
            if(!modelDatasource.hasErrors()) {
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
