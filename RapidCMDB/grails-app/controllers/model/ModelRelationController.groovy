package model;

class ModelRelationController {

    def scaffold = ModelRelation;
    def save = {
        if(params.fromName)
        {
            def firstChar = params.fromName.substring (0,1)
            def remaining = params.fromName.substring (1);
            params.fromName = firstChar.toLowerCase()+remaining;
        }
        if(params.toName)
        {
            def firstChar = params.toName.substring (0,1)
            def remaining = params.toName.substring (1);
            params.toName = firstChar.toLowerCase()+remaining;
        }
        def modelRelation = new ModelRelation(params)
        if(!modelRelation.hasErrors() && modelRelation.save()) {
            flash.message = "Relation created"
            redirect(action:show,controller:'model', id:modelRelation.fromModel?.id)
        }
        else {
            render(view:'create',model:[modelRelation:modelRelation])
        }
    }
    def delete = {
        def modelRelation = ModelRelation.get( params.id )
        if(modelRelation) {
            def modelId = modelRelation.fromModel?.id;
            modelRelation.delete()
            flash.message = "Relation deleted"
            redirect(action:show, controller:'model', id:modelId)
        }
        else {
            flash.message = "Relation not found."
            redirect(action:list)
        }
    }
}
