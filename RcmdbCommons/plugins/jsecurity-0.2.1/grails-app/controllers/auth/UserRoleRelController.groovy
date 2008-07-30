package auth

import com.ifountain.rcmdb.domain.util.ControllerUtils

class UserRoleRelController {
    
    def index = { redirect(action:list,params:params) }

    // the delete, save and update actions only accept POST requests
    def allowedMethods = []

    def list = {
        if(!params.max) params.max = 10
        [ userRoleRelList: UserRoleRel.list( params ) ]
    }

    def show = {
        def userRoleRel = UserRoleRel.get( id:params.id )

        if(!userRoleRel) {
            flash.message = "UserRoleRel not found with id ${params.id}"
            redirect(action:list)
        }
        else { return [ userRoleRel : userRoleRel ] }
    }

    def delete = {
        def userRoleRel = UserRoleRel.get( id:params.id )
        if(userRoleRel) {
	        try{
		        def userId = userRoleRel.rsUser?.id;
                userRoleRel.remove()
                flash.message = "Role ${userRoleRel.role} unassigned"
                redirect(action: "show", controller: 'rsUser', id: userId)
            }
            catch(e){
                def errors =[message(code:"default.couldnot.delete", args:[UserRoleRel.class.getName(), userRoleRel])]
                flash.errors = errors;
                redirect(action:show, id:userRoleRel.id) 
            }
        }
        else {
            flash.message = "Role Assignment not found with id ${params.id}"
            redirect(action:list)
        }
    }

    def edit = {
        def userRoleRel = UserRoleRel.get( id:params.id )

        if(!userRoleRel) {
            flash.message = "UserRoleRel not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            return [ userRoleRel : userRoleRel ]
        }
    }

    def update = {
        def userRoleRel = UserRoleRel.get( id:params.id )
        if(userRoleRel) {
            userRoleRel.update(ControllerUtils.getClassProperties(params, UserRoleRel));
            if(!userRoleRel.hasErrors()) {
                flash.message = "UserRoleRel ${params.id} updated"
                redirect(action:show,id:userRoleRel.id)
            }
            else {
                render(view:'edit',model:[userRoleRel:userRoleRel])
            }
        }
        else {
            flash.message = "UserRoleRel not found with id ${params.id}"
            redirect(action:edit,id:params.id)
        }
    }

    def create = {
        def userRoleRel = new UserRoleRel()
        userRoleRel.properties = params
        return ['userRoleRel':userRoleRel, 'userId':params["rsUser.id"]]
    }

    def save = {
        def userRoleRel = UserRoleRel.add(ControllerUtils.getClassProperties(params, UserRoleRel));
        if(!userRoleRel.hasErrors()) {
            flash.message = "UserRoleRel ${userRoleRel.id} created"
            redirect(action: show, controller: 'rsUser', id: userRoleRel.rsUser?.id)
        }
        else {
            render(view:'create',model:[userRoleRel:userRoleRel, userId:params["rsUser.id"]])
        }
    }
}