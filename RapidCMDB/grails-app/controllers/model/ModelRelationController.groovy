package model;

class ModelRelationController {

    def scaffold = ModelRelation;
    def save = {
        def modelRelation = new ModelRelation(params)
        if(!modelRelation.hasErrors() && modelRelation.save()) {
            flash.message = "Relation ${modelRelation.name} created"
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
            flash.message = "Relation ${modelRelation.name} deleted"
            redirect(action:show, controller:'model', id:modelId)
        }
        else {
            flash.message = "Relation not found."
            redirect(action:list)
        }
    }
}
