class ModelPropertyController {

    def scaffold = ModelProperty;
     def save = {
        def modelProperty = new ModelProperty(params)
        if(!modelProperty.hasErrors() && modelProperty.save()) {
            flash.message = "ModelProperty ${modelProperty.id} created"
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
            modelProperty.delete()
            flash.message = "Property ${modelProperty.name} deleted"
            redirect(action:show, controller:'model', id:modelId)
        }
        else {
            flash.message = "Property not found"
            redirect(action:list)
        }
    }
}
