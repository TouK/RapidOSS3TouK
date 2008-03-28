package model;

class ModelPropertyController {

    def scaffold = ModelProperty;
     def save = {
        if(params.name)
        {
            def firstChar = params.name.substring (0,1)
            def remaining = params.name.substring (1);
            params.name = firstChar.toLowerCase()+remaining;
        }
        def modelProperty = new ModelProperty(params)
        if(!modelProperty.hasErrors() && modelProperty.save()) {
            flash.message = "ModelProperty ${modelProperty} created"
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
            def modelPropertyName = modelProperty.toString();
            modelProperty.delete()
            flash.message = "Property ${modelPropertyName} deleted"
            redirect(action:show, controller:'model', id:modelId)
        }
        else {
            flash.message = "Property not found"
            redirect(action:list)
        }
    }

    def update = {
        def modelProperty = ModelProperty.get( params.id )
        if(modelProperty) {
            modelProperty.properties = params
            if(!modelProperty.hasErrors() && modelProperty.save()) {
                flash.message = "ModelProperty ${modelProperty} updated"
                redirect(action:"show",controller:'model', id:_getModelId(modelProperty))
            }
            else {
                render(view:'edit',model:[modelProperty:modelProperty])
            }
        }
        else {
            flash.message = "ModelProperty not found with id ${params.id}"
            redirect(action:edit,id:params.id)
        }
    }

     def _getModelId = {modelProperty ->
        return modelProperty.model?.id;
    }
}
