


package connection

import com.ifountain.rcmdb.domain.util.ControllerUtils
import java.text.SimpleDateFormat;
class NetcoolConnectionController {
    def final static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm")
    def final static PROPS_TO_BE_EXCLUDED = ["id":"id","_action_Update":"_action_Update","controller":"controller", "action":"action"]
    def index = { redirect(action:list,params:params) }

    // the delete, save and update actions only accept POST requests
    def allowedMethods = [delete:'POST', save:'POST', update:'POST']

    def list = {
        if(!params.max) params.max = 10
        [ netcoolConnectionList: NetcoolConnection.list( params ) ]
    }

    def show = {
        def netcoolConnection = NetcoolConnection.get([id:params.id])

        if(!netcoolConnection) {
            flash.message = "NetcoolConnection not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            if(netcoolConnection.class != NetcoolConnection)
            {
                def controllerName = netcoolConnection.class.simpleName;
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
                return [ netcoolConnection : netcoolConnection ]
            }
        }
    }

    def delete = {
        def netcoolConnection = NetcoolConnection.get( [id:params.id])
        if(netcoolConnection) {
            try{
                netcoolConnection.remove()
                flash.message = "NetcoolConnection ${params.id} deleted"
                redirect(action:list)
            }
            catch(e){
                def errors =[message(code:"default.couldnot.delete", args:[NetcoolConnection, netcoolConnection])]
                flash.errors = errors;
                redirect(action:show, id:netcoolConnection.id)
            }

        }
        else {
            flash.message = "NetcoolConnection not found with id ${params.id}"
            redirect(action:list)
        }
    }

    def edit = {
        def netcoolConnection = NetcoolConnection.get( [id:params.id] )

        if(!netcoolConnection) {
            flash.message = "NetcoolConnection not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            return [ netcoolConnection : netcoolConnection ]
        }
    }


    def update = {
        def netcoolConnection = NetcoolConnection.get( [id:params.id] )
        if(netcoolConnection) {
            netcoolConnection.update(ControllerUtils.getClassProperties(params, NetcoolConnection));
            if(!netcoolConnection.hasErrors()) {
                flash.message = "NetcoolConnection ${params.id} updated"
                redirect(action:show,id:netcoolConnection.id)
            }
            else {
                render(view:'edit',model:[netcoolConnection:netcoolConnection])
            }
        }
        else {
            flash.message = "NetcoolConnection not found with id ${params.id}"
            redirect(action:edit,id:params.id)
        }
    }

    def create = {
        def netcoolConnection = new NetcoolConnection()
        netcoolConnection.properties = params
        return ['netcoolConnection':netcoolConnection]
    }

    def save = {
        def netcoolConnection = NetcoolConnection.add(ControllerUtils.getClassProperties(params, NetcoolConnection))
        if(!netcoolConnection.hasErrors()) {
            flash.message = "NetcoolConnection ${netcoolConnection.id} created"
            redirect(action:show,id:netcoolConnection.id)
        }
        else {
            render(view:'create',model:[netcoolConnection:netcoolConnection])
        }
    }
}