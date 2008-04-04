package model;

class ModelRelationController {
    def scaffold = ModelRelation;

     def show = {
        def modelRelation = ModelRelation.get(params.id)
        if (!modelRelation) {
            flash.message = "ModelRelation not found with id ${params.id}"
            redirect(action: list)
        }
        else {return [modelRelation: modelRelation]}
    }
    
    def save = {
        def cardinalities = params["cardinality"]?.split("To");
        if(cardinalities != null){
            params["firstCardinality"] = cardinalities[0];
            params["secondCardinality"] = cardinalities[1];
        }
        def modelRelation = new ModelRelation(params)
        if(!modelRelation.hasErrors() && modelRelation.save()) {
            flash.message = "Relation created"
            redirect(action:show,controller:'model', id:modelRelation.firstModel?.id)
        }
        else {
            render(view:'create',model:[modelRelation:modelRelation, 'modelId':params["firstModel.id"]])
        }
    }
    def update = {
        def cardinalities = params["cardinality"]?.split("To");
        if(cardinalities != null){
            params["firstCardinality"] = cardinalities[0];
            params["secondCardinality"] = cardinalities[1];
        }
        def modelRelation = ModelRelation.get( params.id )
        if(modelRelation) {
            def isReverse = false;
            def redirectModelId = modelRelation.firstModel?.id;
            if(params["reverse"] != null){
                isReverse = true;
                redirectModelId = modelRelation.secondModel?.id;

            }
            modelRelation.properties = params
            if(!modelRelation.hasErrors() && modelRelation.save()) {
                flash.message = "ModelRelation ${params.id} updated"
                redirect(action:show,controller:'model', id:redirectModelId)
            }
            else {
                render(view:'edit',model:[modelRelation:modelRelation], 'isReverse':isReverse)
            }
        }
        else {
            flash.message = "ModelRelation not found with id ${params.id}"
            redirect(action:edit,id:params.id)
        }
    }

    def delete = {
        def modelRelation = ModelRelation.get( params.id )
        if(modelRelation) {
            def modelId = modelRelation.firstModel?.id;
            modelRelation.delete()
            flash.message = "Relation deleted"
            redirect(action:show, controller:'model', id:modelId)
        }
        else {
            flash.message = "Relation not found."
            redirect(action:list)
        }
    }

    def create = {
        def modelRelation = new ModelRelation()
        modelRelation.properties = params
        return ['modelRelation':modelRelation]
    }
}
