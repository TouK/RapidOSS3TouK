package datasource;
import com.ifountain.rcmdb.domain.util.ControllerUtils;


reclass NetcoolColumnController {
    def final static PROPS_TO_BE_EXCLUDED = ["id":"id","_action_Update":"_action_Update","controller":"controller", "action":"action"]
    def index = { redirect(action:list,params:params) }

    // the delete, save and update actions only accept POST requests
    def allowedMethods = [delete:'POST', save:'POST', update:'POST']

    def list = {
        if(!params.max) params.max = 10
        [ netcoolColumnList: NetcoolColumn.list( params ) ]
    }

    def show = {
        def netcoolColumn = NetcoolColumn.get([id:params.id])

        if(!netcoolColumn) {
            flash.message = "NetcoolColumn not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            if(netcoolColumn.class != NetcoolColumn)
            {
                def controllerName = netcoolColumn.class.name;
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
                return [ netcoolColumn : netcoolColumn ]
            }
        }
    }

    def delete = {
        def netcoolColumn = NetcoolColumn.get( [id:params.id])
        if(netcoolColumn) {
            try{
                netcoolColumn.remove()
                flash.message = "NetcoolColumn ${params.id} deleted"
                redirect(action:list)
            }
            catch(e){
                def errors =[message(code:"default.couldnot.delete", args:[NetcoolColumn, netcoolColumn])]
                flash.errors = errors;
                redirect(action:show, id:netcoolColumn.id)
            }

        }
        else {
            flash.message = "NetcoolColumn not found with id ${params.id}"
            redirect(action:list)
        }
    }

    def edit = {
        def netcoolColumn = NetcoolColumn.get( [id:params.id] )

        if(!netcoolColumn) {
            flash.message = "NetcoolColumn not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            return [ netcoolColumn : netcoolColumn ]
        }
    }


    def update = {
        def netcoolColumn = NetcoolColumn.get( [id:params.id] )
        if(netcoolColumn) {
            netcoolColumn.update(ControllerUtils.getClassProperties(params, NetcoolColumn));
            if(!netcoolColumn.hasErrors()) {
                flash.message = "NetcoolColumn ${params.id} updated"
                redirect(action:show,id:netcoolColumn.id)
            }
            else {
                render(view:'edit',model:[netcoolColumn:netcoolColumn])
            }
        }
        else {
            flash.message = "NetcoolColumn not found with id ${params.id}"
            redirect(action:edit,id:params.id)
        }
    }

    def create = {
        def netcoolColumn = new NetcoolColumn()
        netcoolColumn.properties = params
        return ['netcoolColumn':netcoolColumn]
    }

    def save = {
        def netcoolColumn = NetcoolColumn.add(ControllerUtils.getClassProperties(params, NetcoolColumn))
        if(!netcoolColumn.hasErrors()) {
            flash.message = "NetcoolColumn ${netcoolColumn.id} created"
            redirect(action:show,id:netcoolColumn.id)
        }
        else {
            render(view:'create',model:[netcoolColumn:netcoolColumn])
        }
    }

    def addTo = {
        def netcoolColumn = NetcoolColumn.get( [id:params.id] )
        if(!netcoolColumn){
            flash.message = "NetcoolColumn not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;
            if(relationName){
                def otherClass = com.ifountain.rcmdb.domain.util.DomainClassUtils.getStaticMapVariable(netcoolColumn.class, "relations")[relationName].type;
                def relatedObjectList = [];
                if(otherClass){
                    relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                }
                return [netcoolColumn:netcoolColumn, relationName:relationName, relatedObjectList:relatedObjectList]
            }
            else{
               flash.message = "No relation name specified for add relation action"
               redirect(action:edit,id:netcoolColumn.id)
            }
        }
    }

    def addRelation = {
        def netcoolColumn = NetcoolColumn.get( [id:params.id] )
        if(!netcoolColumn) {
            flash.message = "NetcoolColumn not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;
            def otherClass = com.ifountain.rcmdb.domain.util.DomainClassUtils.getStaticMapVariable(netcoolColumn.class, "relations")[relationName].type;
            if(otherClass){
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if(res){
                      def relationMap = [:];
                      relationMap[relationName] = res;
                      netcoolColumn.addRelation(relationMap);
                      if(netcoolColumn.hasErrors()){
                          def relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                          render(view:'addTo',model:[netcoolColumn:netcoolColumn, relationName:relationName, relatedObjectList:relatedObjectList])
                      }
                      else{
                          flash.message = "NetcoolColumn ${params.id} updated"
                          redirect(action:edit,id:netcoolColumn.id)
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
        def netcoolColumn = NetcoolColumn.get( [id:params.id] )
        if(!netcoolColumn) {
            flash.message = "NetcoolColumn not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;
            def otherClass = com.ifountain.rcmdb.domain.util.DomainClassUtils.getStaticMapVariable(netcoolColumn.class, "relations")[relationName].type;
            if(otherClass){
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if(res){
                      def relationMap = [:];
                      relationMap[relationName] = res;
                      netcoolColumn.removeRelation(relationMap);
                      if(netcoolColumn.hasErrors()){
                          render(view:'edit',model:[netcoolColumn:netcoolColumn])
                      }
                      else{
                          flash.message = "NetcoolColumn ${params.id} updated"
                          redirect(action:edit,id:netcoolColumn.id)
                      }
                }
                else{
                    flash.message = otherClass.getName() + " not found with id ${params.relatedObjectId}"
                    redirect(action:edit,id:netcoolColumn.id)
                }
            }
            else{
                flash.message = "No relation exist with name ${relationName}"
                redirect(action:edit,id:netcoolColumn.id)
            }
        }
    }

    def reloadOperations = {
        def modelClass = grailsApplication.getClassForName("datasource.NetcoolColumn")
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