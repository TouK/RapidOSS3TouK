package datasource

import com.ifountain.rcmdb.domain.util.ControllerUtils
import java.text.SimpleDateFormat;
class NetcoolDatasourceController {
    def final static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm")
    def final static PROPS_TO_BE_EXCLUDED = ["id":"id","_action_Update":"_action_Update","controller":"controller", "action":"action"]
    def index = { redirect(action:list,params:params) }

    // the delete, save and update actions only accept POST requests
    def allowedMethods = [delete:'POST', save:'POST', update:'POST']

    def list = {
        if(!params.max) params.max = 10
        [ netcoolDatasourceList: NetcoolDatasource.list( params ) ]
    }

    def show = {
        def netcoolDatasource = NetcoolDatasource.get([id:params.id])

        if(!netcoolDatasource) {
            flash.message = "NetcoolDatasource not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            if(netcoolDatasource.class != NetcoolDatasource)
            {
                def controllerName = netcoolDatasource.class.name;
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
                return [ netcoolDatasource : netcoolDatasource ]
            }
        }
    }

    def delete = {
        def netcoolDatasource = NetcoolDatasource.get( [id:params.id])
        if(netcoolDatasource) {
            try{
                netcoolDatasource.remove()
                flash.message = "NetcoolDatasource ${params.id} deleted"
                redirect(action:list)
            }
            catch(e){
                def errors =[message(code:"default.couldnot.delete", args:[NetcoolDatasource, netcoolDatasource])]
                flash.errors = errors;
                redirect(action:show, id:netcoolDatasource.id)
            }

        }
        else {
            flash.message = "NetcoolDatasource not found with id ${params.id}"
            redirect(action:list)
        }
    }

    def edit = {
        def netcoolDatasource = NetcoolDatasource.get( [id:params.id] )

        if(!netcoolDatasource) {
            flash.message = "NetcoolDatasource not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            return [ netcoolDatasource : netcoolDatasource ]
        }
    }


    def update = {
        def netcoolDatasource = NetcoolDatasource.get( [id:params.id] )
        if(netcoolDatasource) {
            netcoolDatasource.update(ControllerUtils.getClassProperties(params, NetcoolDatasource));
            if(!netcoolDatasource.hasErrors()) {
                flash.message = "NetcoolDatasource ${params.id} updated"
                redirect(action:show,id:netcoolDatasource.id)
            }
            else {
                render(view:'edit',model:[netcoolDatasource:netcoolDatasource])
            }
        }
        else {
            flash.message = "NetcoolDatasource not found with id ${params.id}"
            redirect(action:edit,id:params.id)
        }
    }

    def create = {
        def netcoolDatasource = new NetcoolDatasource()
        netcoolDatasource.properties = params
        return ['netcoolDatasource':netcoolDatasource]
    }

    def save = {
        def netcoolDatasource = NetcoolDatasource.add(ControllerUtils.getClassProperties(params, NetcoolDatasource))
        if(!netcoolDatasource.hasErrors()) {
            flash.message = "NetcoolDatasource ${netcoolDatasource.id} created"
            redirect(action:show,id:netcoolDatasource.id)
        }
        else {
            render(view:'create',model:[netcoolDatasource:netcoolDatasource])
        }
    }
}