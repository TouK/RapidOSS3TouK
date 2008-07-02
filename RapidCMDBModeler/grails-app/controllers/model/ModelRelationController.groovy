package model;
import com.ifountain.rcmdb.domain.util.ControllerUtils;
class ModelRelationController {
    def list = {
        if(!params.max) params.max = 10
        [ modelRelationList: ModelRelation.list( params ) ]
    }

    def edit = {
        def modelRelation = ModelRelation.get( [id:params.id] )

        if(!modelRelation) {
            flash.message = "ModelRelation not found with id \${params.id}"
            redirect(action:list)
        }
        else {
            return [ modelRelation : modelRelation ]
        }
    }


     def show = {
        def modelRelation = ModelRelation.get(id:params.id)
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
        def modelRelation = ModelRelation.add(ControllerUtils.getClassProperties(params, ModelRelation))
        if(!modelRelation.hasErrors()) {
            flash.message = "Relation created"
            redirect(action:show,controller:'model', id:modelRelation.firstModel?.id)
        }
        else {
            render(view:'create',model:[modelRelation:modelRelation, 'modelId':params["firstModel.id"]])
        }
    }
    def update = {
        println params;
        def cardinalities = params["cardinality"]?.split("To");
        if(cardinalities != null){
            params["firstCardinality"] = cardinalities[0];
            params["secondCardinality"] = cardinalities[1];
        }
        def modelRelation = ModelRelation.get( id:params.id )
        if(modelRelation) {
            def isReverse = false;
            def redirectModelId = modelRelation.firstModel?.id;
            if(params["reverse"] != null){
                isReverse = true;
                params["firstCardinality"] = cardinalities[1];
                params["secondCardinality"] = cardinalities[0];
                redirectModelId = modelRelation.secondModel?.id;

            }
            modelRelation.update(ControllerUtils.getClassProperties(params, ModelRelation));
            if(!modelRelation.hasErrors()) {
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
        def modelRelation = ModelRelation.get( id:params.id )
        if(modelRelation) {
            def modelId = modelRelation.firstModel?.id;
            try{
                modelRelation.remove()
                flash.message = "Relation deleted"
                redirect(action:show, controller:'model', id:modelId)
            }
            catch(e){
                def errors =[message(code:"default.couldnot.delete", args:[ModelRelation.class.getName(), modelRelation])]
                flash.errors = errors;
                redirect(action:show, id:modelRelation.id)
            }
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
