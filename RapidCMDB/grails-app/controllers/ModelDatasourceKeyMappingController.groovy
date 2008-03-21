class ModelDatasourceKeyMappingController {

    def scaffold = ModelDatasourceKeyMapping;

     def save = {
        def modelDatasourceKeyMapping = new ModelDatasourceKeyMapping(params)
        if(!modelDatasourceKeyMapping.hasErrors() && modelDatasourceKeyMapping.save()) {
            flash.message = "ModelDatasourceKeyMapping ${modelDatasourceKeyMapping.id} created"
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
            modelDatasourceKeyMapping.delete()
            flash.message = "ModelDatasourceKeyMapping ${params.id} deleted"
            redirect(action:show, controller:'modelDatasource', id:modelDatasourceId)
        }
        else {
            flash.message = "ModelDatasourceKeyMapping not found with id ${params.id}"
            redirect(action:list)
        }
    }
}
