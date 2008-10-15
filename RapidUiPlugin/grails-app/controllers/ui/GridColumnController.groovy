package ui;


class GridColumnController {
    def index = { redirect(action:list,params:params) }

    // the delete, save and update actions only accept POST requests
    def allowedMethods = [delete:'POST', save:'POST', update:'POST']

    def list = {
        if(!params.max) params.max = 10
        [ gridColumnList: GridColumn.list( params ) ]
    }

    def show = {
        def gridColumn = GridColumn.get([id:params.id])

        if(!gridColumn) {
            flash.message = "GridColumn not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            if(gridColumn.class != GridColumn)
            {
                def controllerName = gridColumn.class.simpleName;
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
                return [ gridColumn : gridColumn ]
            }
        }
    }

    def delete = {
        def gridColumn = GridColumn.get( [id:params.id])
        if(gridColumn) {
            try{
                gridColumn.remove()
                flash.message = "GridColumn ${params.id} deleted"
                redirect(action:list)
            }
            catch(e){
                addError("default.couldnot.delete", [GridColumn, gridColumn])
                flash.errors = this.errors;
                redirect(action:show, id:gridColumn.id)
            }

        }
        else {
            flash.message = "GridColumn not found with id ${params.id}"
            redirect(action:list)
        }
    }

    def edit = {
        def gridColumn = GridColumn.get( [id:params.id] )

        if(!gridColumn) {
            flash.message = "GridColumn not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            return [ gridColumn : gridColumn ]
        }
    }


    def update = {
        def gridColumn = GridColumn.get( [id:params.id] )
        if(gridColumn) {
            gridColumn.update(com.ifountain.rcmdb.domain.util.ControllerUtils.getClassProperties(params, GridColumn));
            if(!gridColumn.hasErrors()) {
                flash.message = "GridColumn ${params.id} updated"
                redirect(action:show,id:gridColumn.id)
            }
            else {
                render(view:'edit',model:[gridColumn:gridColumn])
            }
        }
        else {
            flash.message = "GridColumn not found with id ${params.id}"
            redirect(action:edit,id:params.id)
        }
    }

    def create = {
        def gridColumn = new GridColumn()
        gridColumn.properties = params
        return ['gridColumn':gridColumn]
    }

    def save = {
        def gridColumn = GridColumn.add(com.ifountain.rcmdb.domain.util.ControllerUtils.getClassProperties(params, GridColumn))
        if(!gridColumn.hasErrors()) {
            flash.message = "GridColumn ${gridColumn.id} created"
            redirect(action:show,id:gridColumn.id)
        }
        else {
            render(view:'create',model:[gridColumn:gridColumn])
        }
    }

    def addTo = {
        def gridColumn = GridColumn.get( [id:params.id] )
        if(!gridColumn){
            flash.message = "GridColumn not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;
            if(relationName){
                def otherClass = gridColumn.hasMany[relationName];
                def relatedObjectList = [];
                if(otherClass){
                    relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                }
                return [gridColumn:gridColumn, relationName:relationName, relatedObjectList:relatedObjectList]
            }
            else{
               flash.message = "No relation name specified for add relation action"
               redirect(action:edit,id:gridColumn.id)
            }
        }
    }

    def addRelation = {
        def gridColumn = GridColumn.get( [id:params.id] )
        if(!gridColumn) {
            flash.message = "GridColumn not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;
            def otherClass = gridColumn.hasMany[relationName];
            if(otherClass){
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if(res){
                      def relationMap = [:];
                      relationMap[relationName] = res;
                      gridColumn.addRelation(relationMap);
                      if(gridColumn.hasErrors()){
                          def relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                          render(view:'addTo',model:[gridColumn:gridColumn, relationName:relationName, relatedObjectList:relatedObjectList])
                      }
                      else{
                          flash.message = "GridColumn ${params.id} updated"
                          redirect(action:edit,id:gridColumn.id)
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
        def gridColumn = GridColumn.get( [id:params.id] )
        if(!gridColumn) {
            flash.message = "GridColumn not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;
            def otherClass = gridColumn.hasMany[relationName];
            if(otherClass){
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if(res){
                      def relationMap = [:];
                      relationMap[relationName] = res;
                      gridColumn.removeRelation(relationMap);
                      if(gridColumn.hasErrors()){
                          render(view:'edit',model:[gridColumn:gridColumn])
                      }
                      else{
                          flash.message = "GridColumn ${params.id} updated"
                          redirect(action:edit,id:gridColumn.id)
                      }
                }
                else{
                    flash.message = otherClass.getName() + " not found with id ${params.relatedObjectId}"
                    redirect(action:edit,id:gridColumn.id)
                }
            }
            else{
                flash.message = "No relation exist with name ${relationName}"
                redirect(action:edit,id:gridColumn.id)
            }
        }
    }

    def reloadOperations = {
        def modelClass = grailsApplication.getClassForName("GridColumn")
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