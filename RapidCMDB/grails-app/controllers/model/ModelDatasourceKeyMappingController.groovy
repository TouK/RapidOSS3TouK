package model;

class ModelDatasourceKeyMappingController {

    def scaffold = ModelDatasourceKeyMapping;
    def show = {
        def modelDatasourceKeyMapping = ModelDatasourceKeyMapping.get(params.id)
        if (!modelDatasourceKeyMapping) {
            flash.message = "ModelDatasourceKeyMapping not found with id ${params.id}"
            redirect(action: list)
        }
        else {return [modelDatasourceKeyMapping: modelDatasourceKeyMapping]}
    }

     def save = {
        def modelDatasourceKeyMapping = new ModelDatasourceKeyMapping(params)
        if(!modelDatasourceKeyMapping.hasErrors() && modelDatasourceKeyMapping.save()) {
            flash.message = "ModelDatasourceKeyMapping ${modelDatasourceKeyMapping} created"
            redirect(action:show,controller:'modelDatasource', id:modelDatasourceKeyMapping.datasource?.id)
        }
        else {
            render(view:'create',model:[modelDatasourceKeyMapping:modelDatasourceKeyMapping])
        }
    }

    def delete = {
        def modelDatasourceKeyMapping = ModelDatasourceKeyMapping.get( params.id )
        if(modelDatasourceKeyMapping) {
            def modelDatasourceId = modelDatasourceKeyMapping.datasource?.id;
            def keyMappingName = modelDatasourceKeyMapping.toString();
            modelDatasourceKeyMapping.delete()
            flash.message = "ModelDatasourceKeyMapping ${keyMappingName} deleted"
            redirect(action:show, controller:'modelDatasource', id:modelDatasourceId)
        }
        else {
            flash.message = "ModelDatasourceKeyMapping not found with id ${params.id}"
            redirect(action:list)
        }
    }

    def update = {
        def modelDatasourceKeyMapping = ModelDatasourceKeyMapping.get( params.id )
        if(modelDatasourceKeyMapping) {
            modelDatasourceKeyMapping.properties = params
            if(!modelDatasourceKeyMapping.hasErrors() && modelDatasourceKeyMapping.save()) {
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
