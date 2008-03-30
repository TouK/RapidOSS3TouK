package model;
import com.ifountain.domain.ModelGenerator

class ModelController {
    def static String MODEL_DOESNOT_EXIST = "Model does not exist";
    def scaffold = model.Model;

    def generate = {
        if(params.id)
        {
            def model = Model.get(params.id);
            if(model)
            {
                try
                {
                    ModelGenerator.getInstance().generateModel (model);
                    flash.message = "Model $model.name genareted succcessfully"
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
