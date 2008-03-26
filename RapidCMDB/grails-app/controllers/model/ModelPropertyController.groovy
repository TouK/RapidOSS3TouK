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
