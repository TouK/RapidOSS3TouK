package model;
import com.ifountain.domain.ModelGenerator

class ModelController {

    def scaffold = model.Model;

    def generate = {
        if(params.id)
        {
            def model = Model.get(params.id);
            if(model)
            {
                ModelGenerator.getInstance().generateModel (model);
                flash.message = "Model $model.name genareted succcessfully"
                redirect(action:show,controller:'model', id:model?.id)
            }
            else
            {
                flash.message = "Model does not exist"
                redirect(action:list)
            }
        }
        else
        {
            flash.message = "Model id not specified"
            redirect(action:list)
        }
    }
}
