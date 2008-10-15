import com.ifountain.rcmdb.domain.util.ControllerUtils
import com.ifountain.rcmdb.domain.util.DomainClassUtils;


class RsCardController {
    def final static PROPS_TO_BE_EXCLUDED = ["id":"id","_action_Update":"_action_Update","controller":"controller", "action":"action"]
    def index = { redirect(action:list,params:params) }

    // the delete, save and update actions only accept POST requests
    def allowedMethods = [delete:'POST', save:'POST', update:'POST']

    def list = {
        if(!params.max) params.max = 10
        [ rsCardList: RsCard.list( params ) ]
    }

    def show = {
        def rsCard = RsCard.get([id:params.id])

        if(!rsCard) {
            flash.message = "RsCard not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            if(rsCard.class != RsCard)
            {
                def controllerName = rsCard.class.simpleName;
                if(controllerName.length() == 1)
                {
                    controllerName = controllerName.toLowerCase();
                }
                else
                {
                    controllerName = controllerName.substring(0,1).toLowerCase()+controllerName.substring(1);
                }
                redirect(action:show, controller:controllerName, id:params.id)
            }
            else
            {
                return [ rsCard : rsCard ]
            }
        }
    }

    def delete = {
        def rsCard = RsCard.get( [id:params.id])
        if(rsCard) {
            try{
                rsCard.remove()
                flash.message = "RsCard ${params.id} deleted"
                redirect(action:list)
            }
            catch(e){
                addError("default.couldnot.delete", [RsCard, rsCard])
                flash.errors = this.errors;
                redirect(action:show, id:rsCard.id)
            }

        }
        else {
            flash.message = "RsCard not found with id ${params.id}"
            redirect(action:list)
        }
    }

    def edit = {
        def rsCard = RsCard.get( [id:params.id] )

        if(!rsCard) {
            flash.message = "RsCard not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            return [ rsCard : rsCard ]
        }
    }


    def update = {
        def rsCard = RsCard.get( [id:params.id] )
        if(rsCard) {
            rsCard.update(ControllerUtils.getClassProperties(params, RsCard));
            if(!rsCard.hasErrors()) {
                flash.message = "RsCard ${params.id} updated"
                redirect(action:show,id:rsCard.id)
            }
            else {
                render(view:'edit',model:[rsCard:rsCard])
            }
        }
        else {
            flash.message = "RsCard not found with id ${params.id}"
            redirect(action:edit,id:params.id)
        }
    }

    def create = {
        def rsCard = new RsCard()
        rsCard.properties = params
        return ['rsCard':rsCard]
    }

    def save = {
        def rsCard = RsCard.add(ControllerUtils.getClassProperties(params, RsCard))
        if(!rsCard.hasErrors()) {
            flash.message = "RsCard ${rsCard.id} created"
            redirect(action:show,id:rsCard.id)
        }
        else {
            render(view:'create',model:[rsCard:rsCard])
        }
    }

    def addTo = {
        def rsCard = RsCard.get( [id:params.id] )
        if(!rsCard){
            flash.message = "RsCard not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;
            if(relationName){
                def otherClass = DomainClassUtils.getStaticMapVariable(RsCard, "relations")[relationName].type;
                def relatedObjectList = [];
                if(otherClass){
                    relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                }
                return [rsCard:rsCard, relationName:relationName, relatedObjectList:relatedObjectList]
            }
            else{
               flash.message = "No relation name specified for add relation action"
               redirect(action:edit,id:rsCard.id)
            }
        }
    }



    def addRelation = {
        def rsCard = RsCard.get( [id:params.id] )
        if(!rsCard) {
            flash.message = "RsCard not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;
            def otherClass = DomainClassUtils.getStaticMapVariable(RsCard, "relations")[relationName].type;
            if(otherClass){
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if(res){
                      def relationMap = [:];
                      relationMap[relationName] = res;
                      rsCard.addRelation(relationMap);
                      if(rsCard.hasErrors()){
                          def relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                          render(view:'addTo',model:[rsCard:rsCard, relationName:relationName, relatedObjectList:relatedObjectList])
                      }
                      else{
                          flash.message = "RsCard ${params.id} updated"
                          redirect(action:edit,id:rsCard.id)
                      }

                }
                else{
                    flash.message = otherClass.getName() + " not found with id ${params.relatedObjectId}"
                    redirect(action:addTo, id:params.id, relationName:relationName)
                }
            }
            else{
                flash.message = "No relation exist with name ${relationName}"
                redirect(action:addTo, id:params.id, relationName:relationName)
            }
        }
    }

    def removeRelation = {
        def rsCard = RsCard.get( [id:params.id] )
        if(!rsCard) {
            flash.message = "RsCard not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;

            def otherClass = com.ifountain.rcmdb.domain.util.DomainClassUtils.getStaticMapVariable(RsCard, "relations")[relationName].type;
            if(otherClass){
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if(res){
                      def relationMap = [:];
                      relationMap[relationName] = res;
                      rsCard.removeRelation(relationMap);
                      if(rsCard.hasErrors()){
                          render(view:'edit',model:[rsCard:rsCard])
                      }
                      else{
                          flash.message = "RsCard ${params.id} updated"
                          redirect(action:edit,id:rsCard.id)
                      }
                }
                else{
                    flash.message = otherClass.getName() + " not found with id ${params.relatedObjectId}"
                    redirect(action:edit,id:rsCard.id)
                }
            }
            else{
                flash.message = "No relation exist with name ${relationName}"
                redirect(action:edit,id:rsCard.id)
            }
        }
    }

    def reloadOperations = {
        def modelClass = grailsApplication.getClassForName("RsCard")
        if (modelClass)
        {
            try
            {

                modelClass.metaClass.invokeStaticMethod(modelClass, "reloadOperations", [] as Object[]);
                flash.message = "Model operations reloaded"
                redirect(action:list)
            } catch (t)
            {
                flash.message = "Exception occurred while reloading model operations Reason:${t.toString()}"
                 redirect(action:list)
            }
        }
        else
        {
            flash.message = "Model currently not loaded by application. You should reload application."
            redirect(action:list)
        }
    }
}