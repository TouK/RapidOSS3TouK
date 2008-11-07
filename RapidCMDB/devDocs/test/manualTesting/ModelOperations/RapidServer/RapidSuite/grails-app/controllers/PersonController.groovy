import com.ifountain.rcmdb.domain.util.ControllerUtils
import com.ifountain.rcmdb.domain.util.DomainClassUtils;


class PersonController {
    def final static PROPS_TO_BE_EXCLUDED = ["id":"id","_action_Update":"_action_Update","controller":"controller", "action":"action"]
    def index = { redirect(action:list,params:params) }

    // the delete, save and update actions only accept POST requests
    def allowedMethods = [delete:'POST', save:'POST', update:'POST']

    def list = {
        if(!params.max) params.max = 10
        [ personList: Person.list( params ) ]
    }

    def show = {
        def person = Person.get([id:params.id])

        if(!person) {
            flash.message = "Person not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            if(person.class != Person)
            {
                def controllerName = person.class.simpleName;
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
                return [ person : person ]
            }
        }
    }

    def delete = {
        def person = Person.get( [id:params.id])
        if(person) {
            try{
                person.remove()
                flash.message = "Person ${params.id} deleted"
                redirect(action:list)
            }
            catch(e){
                addError("default.couldnot.delete", [Person, person])
                flash.errors = this.errors;
                redirect(action:show, id:person.id)
            }

        }
        else {
            flash.message = "Person not found with id ${params.id}"
            redirect(action:list)
        }
    }

    def edit = {
        def person = Person.get( [id:params.id] )

        if(!person) {
            flash.message = "Person not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            return [ person : person ]
        }
    }


    def update = {
        def person = Person.get( [id:params.id] )
        if(person) {
            person.update(ControllerUtils.getClassProperties(params, Person));
            if(!person.hasErrors()) {
                flash.message = "Person ${params.id} updated"
                redirect(action:show,id:person.id)
            }
            else {
                render(view:'edit',model:[person:person])
            }
        }
        else {
            flash.message = "Person not found with id ${params.id}"
            redirect(action:edit,id:params.id)
        }
    }

    def create = {
        def person = new Person()
        person.properties = params
        return ['person':person]
    }

    def save = {
        def person = Person.add(ControllerUtils.getClassProperties(params, Person))
        if(!person.hasErrors()) {
            flash.message = "Person ${person.id} created"
            redirect(action:show,id:person.id)
        }
        else {
            render(view:'create',model:[person:person])
        }
    }

    def addTo = {
        def person = Person.get( [id:params.id] )
        if(!person){
            flash.message = "Person not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;
            if(relationName){
                def otherClass = DomainClassUtils.getStaticMapVariable(Person, "relations")[relationName].type;
                def relatedObjectList = [];
                if(otherClass){
                    relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                }
                return [person:person, relationName:relationName, relatedObjectList:relatedObjectList]
            }
            else{
               flash.message = "No relation name specified for add relation action"
               redirect(action:edit,id:person.id)
            }
        }
    }



    def addRelation = {
        def person = Person.get( [id:params.id] )
        if(!person) {
            flash.message = "Person not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;
            def otherClass = DomainClassUtils.getStaticMapVariable(Person, "relations")[relationName].type;
            if(otherClass){
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if(res){
                      def relationMap = [:];
                      relationMap[relationName] = res;
                      person.addRelation(relationMap);
                      if(person.hasErrors()){
                          def relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                          render(view:'addTo',model:[person:person, relationName:relationName, relatedObjectList:relatedObjectList])
                      }
                      else{
                          flash.message = "Person ${params.id} updated"
                          redirect(action:edit,id:person.id)
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
        def person = Person.get( [id:params.id] )
        if(!person) {
            flash.message = "Person not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;

            def otherClass = com.ifountain.rcmdb.domain.util.DomainClassUtils.getStaticMapVariable(Person, "relations")[relationName].type;
            if(otherClass){
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if(res){
                      def relationMap = [:];
                      relationMap[relationName] = res;
                      person.removeRelation(relationMap);
                      if(person.hasErrors()){
                          render(view:'edit',model:[person:person])
                      }
                      else{
                          flash.message = "Person ${params.id} updated"
                          redirect(action:edit,id:person.id)
                      }
                }
                else{
                    flash.message = otherClass.getName() + " not found with id ${params.relatedObjectId}"
                    redirect(action:edit,id:person.id)
                }
            }
            else{
                flash.message = "No relation exist with name ${relationName}"
                redirect(action:edit,id:person.id)
            }
        }
    }

    def reloadOperations = {
        def modelClass = grailsApplication.getClassForName("Person")
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