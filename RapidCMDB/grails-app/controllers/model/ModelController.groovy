package model;
import com.ifountain.domain.ModelGenerator
import com.ifountain.domain.ModelUtils;

class ModelController {
    def static String MODEL_DOESNOT_EXIST = "Model does not exist";
    def scaffold = model.Model;

    def save = {
        def model = new Model(params)
        if(!model.hasErrors() && model.save()) {
            flash.message = "Model ${model.id} created"
            redirect(action:show,id:model.id)
        }
        else {
            render(view:'create',model:[model:model])
        }
    }

    def show = {
        def model = Model.get(params.id)
        if (!model) {
            flash.message = MODEL_DOESNOT_EXIST
            redirect(action: list)
        }
        else {return [model: model]}
    }

    def delete = {
        if(params.id)
        {
            def model = Model.get( params.id )
            if(model) {
                def dependeeModels = ModelUtils.getDependeeModels(model) ;
                try{
                    model.delete(flush:true)
                }
                catch(e)
                {
                    def errors =[message(code:"default.couldnot.delete", args:[Model.class.getName(), model])]
                    flash.errors = errors;
                    redirect(action:show, controller:'model', id:model?.id)
                    return;

                }
                ModelUtils.deleteModelArtefacts(System.getProperty("base.dir"), model.name);
                try
                {
                    dependeeModels.each{key,value->
                        value.refresh();
                        ModelGenerator.getInstance().generateModel (value);
                    }
                    flash.message = "Model ${params.id} deleted"
                    redirect(action:list, controller:'model');
                }
                catch(Exception e)
                {
                    flash.message = "Model deleted but and unexpected exception occured while generating dependent models. Reason:${e.getMessage()}";
                    redirect(action:list, controller:'model')
                }

            }
            else {
                flash.message = MODEL_DOESNOT_EXIST
                redirect(action:list, controller:'model')
            }
        }
        else
        {
            redirect(action:list, controller:'model')
        }
    }

    def generate = {
        if(params.id)
        {
            def model = Model.get(params.id);
            if(model)
            {
                try
                {
                    ModelGenerator.getInstance().generateModel (model);
                    flash.message = "Model $model.name genarated successfully"
                    redirect(action:show,controller:'model', id:model?.id)
                }
                catch(Exception e)
                {
                    flash.message = e.getMessage();
                    redirect(action:show,controller:'model', id:model?.id)
                }
            }
            else
            {
                flash.message = MODEL_DOESNOT_EXIST
                redirect(action:list, controller:'model')
            }
        }
        else
        {
            redirect(action:list, controller:'model')
        }
    }     
}
