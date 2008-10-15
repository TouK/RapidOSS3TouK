package model;
          

import com.ifountain.rcmdb.domain.util.ControllerUtils;

class DatasourceNameController {
    def final static PROPS_TO_BE_EXCLUDED = ["id":"id","_action_Update":"_action_Update","controller":"controller", "action":"action"]
    def index = { redirect(action:list,params:params) }

    // the delete, save and update actions only accept POST requests
    def allowedMethods = [delete:'POST', save:'POST', update:'POST']

    def list = {
        if(!params.max) params.max = 10
        [ datasourceNameList: DatasourceName.list( params ) ]
    }

    def show = {
        def datasourceName = DatasourceName.get([id:params.id])

        if(!datasourceName) {
            flash.message = "DatasourceName not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            if(datasourceName.class != DatasourceName)
            {
                def controllerName = datasourceName.class.simpleName;
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
                return [ datasourceName : datasourceName ]
            }
        }
    }

    def delete = {
        def datasourceName = DatasourceName.get( id:params.id )
        if(datasourceName) {
            try{
                datasourceName.remove()
                flash.message = "DatasourceName ${params.id} deleted"
                redirect(action:list)
            }
            catch(e){
                def errors =[message(code:"default.couldnot.delete", args:[DatasourceName.class.getName(), datasourceName])]
                flash.errors = errors;
                redirect(action:show, id:datasourceName.id)
            }
        }
        else {
            flash.message = "DatasourceName not found with id ${params.id}"
            redirect(action:list)
        }
    }

    def edit = {
        def datasourceName = DatasourceName.get( [id:params.id] )

        if(!datasourceName) {
            flash.message = "DatasourceName not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            return [ datasourceName : datasourceName ]
        }
    }


    def update = {
        def datasourceName = DatasourceName.get( [id:params.id] )
        if(datasourceName) {
            datasourceName.update(ControllerUtils.getClassProperties(params, DatasourceName));
            if(!datasourceName.hasErrors()) {
                flash.message = "DatasourceName ${params.id} updated"
                redirect(action:show,id:datasourceName.id)
            }
            else {
                render(view:'edit',model:[datasourceName:datasourceName])
            }
        }
        else {
            flash.message = "DatasourceName not found with id ${params.id}"
            redirect(action:edit,id:params.id)
        }
    }

    def create = {
        def datasourceName = new DatasourceName()
        datasourceName.properties = params
        return ['datasourceName':datasourceName]
    }

    def save = {
        def datasourceName = DatasourceName.add(ControllerUtils.getClassProperties(params, DatasourceName))
        if(!datasourceName.hasErrors()) {
            flash.message = "DatasourceName ${datasourceName.id} created"
            redirect(action:show,id:datasourceName.id)
        }
        else {
            render(view:'create',model:[datasourceName:datasourceName])
        }
    }

}