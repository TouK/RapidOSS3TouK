import groovy.text.SimpleTemplateEngine
import com.ifountain.domain.DomainGenerator

class ModelController {

    def scaffold = Model;

    def generate = {
        if(params.id)
        {
            def model = Model.get(params.id);
            if(model)
            {
                DomainGenerator.getInstance().generateModel (model);
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
