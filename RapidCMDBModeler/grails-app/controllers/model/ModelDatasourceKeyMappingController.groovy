package model;
import com.ifountain.rcmdb.domain.util.ControllerUtils;
class ModelDatasourceKeyMappingController {

	def index = {redirect(action: list, params: params)}
    def list = {
        if(!params.max) params.max = 10
        [ modelDatasourceKeyMappingList: ModelDatasourceKeyMapping.list( params ) ]
    }

    def edit = {
        def modelDatasourceKeyMapping = ModelDatasourceKeyMapping.get( [id:params.id] )

        if(!modelDatasourceKeyMapping) {
            flash.message = "ModelDatasourceKeyMapping not found with id \${params.id}"
            redirect(action:list)
        }
        else {
            return [ modelDatasourceKeyMapping : modelDatasourceKeyMapping ]
        }
    }

    def create = {
        def modelDatasourceKeyMapping = new ModelDatasourceKeyMapping()
        modelDatasourceKeyMapping.properties = params
        return ['modelDatasourceKeyMapping':modelDatasourceKeyMapping]
    }
    def show = {
        def modelDatasourceKeyMapping = ModelDatasourceKeyMapping.get(id:params.id)
        if (!modelDatasourceKeyMapping) {
            flash.message = "ModelDatasourceKeyMapping not found with id ${params.id}"
            redirect(action: list)
        }
        else {return [modelDatasourceKeyMapping: modelDatasourceKeyMapping]}
    }

     def save = {
        def modelDatasourceKeyMapping = ModelDatasourceKeyMapping.add(ControllerUtils.getClassProperties(params, ModelDatasourceKeyMapping))
        if(!modelDatasourceKeyMapping.hasErrors()) {
            flash.message = "ModelDatasourceKeyMapping ${modelDatasourceKeyMapping} created"
            redirect(action:show,controller:'modelDatasource', id:modelDatasourceKeyMapping.datasource?.id)
        }
        else {
            render(view:'create',model:[modelDatasourceKeyMapping:modelDatasourceKeyMapping])
        }
    }

    def delete = {
        def modelDatasourceKeyMapping = ModelDatasourceKeyMapping.get( id:params.id )
        if(modelDatasourceKeyMapping) {
            def modelDatasourceId = modelDatasourceKeyMapping.datasource?.id;
            def keyMappingName = modelDatasourceKeyMapping.toString();
            try{
                modelDatasourceKeyMapping.remove()
                flash.message = "ModelDatasourceKeyMapping ${keyMappingName} deleted"
                redirect(action:show, controller:'modelDatasource', id:modelDatasourceId)
            }
            catch(e){
                def errors =[message(code:"default.couldnot.delete", args:[ModelDatasourceKeyMapping.class.getName(), modelDatasourceKeyMapping])]
                flash.errors = errors;
                redirect(action:show, id:modelDatasourceKeyMapping.id)
            }

        }
        else {
            flash.message = "ModelDatasourceKeyMapping not found with id ${params.id}"
            redirect(action:list)
        }
    }

    def update = {
        def modelDatasourceKeyMapping = ModelDatasourceKeyMapping.get( id:params.id )
        if(modelDatasourceKeyMapping) {
            modelDatasourceKeyMapping.update(ControllerUtils.getClassProperties(params, ModelDatasourceKeyMapping));
            if(!modelDatasourceKeyMapping.hasErrors()) {
                def modelDatasourceId = modelDatasourceKeyMapping.datasource?.id;
                flash.message = "ModelDatasourceKeyMapping ${modelDatasourceKeyMapping} updated"
                redirect(action:"show",controller:'modelDatasource', id:modelDatasourceId)
            }
            else {
                render(view:'edit',model:[modelDatasourceKeyMapping:modelDatasourceKeyMapping])
            }
        }
        else {
            flash.message = "ModelDatasourceKeyMapping not found with id ${params.id}"
            redirect(action:edit,id:params.id)
        }
    }
}
