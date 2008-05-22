package auth;           
class UserRoleRelController {
    
    def index = { redirect(action:list,params:params) }

    // the delete, save and update actions only accept POST requests
    def allowedMethods = [delete:'POST', save:'POST', update:'POST']

    def list = {
        if(!params.max) params.max = 10
        [ userRoleRelList: UserRoleRel.list( params ) ]
    }

    def show = {
        def userRoleRel = UserRoleRel.get( params.id )

        if(!userRoleRel) {
            flash.message = "UserRoleRel not found with id ${params.id}"
            redirect(action:list)
        }
        else { return [ userRoleRel : userRoleRel ] }
    }

    def delete = {
        def userRoleRel = UserRoleRel.get( params.id )
        if(userRoleRel) {
	        try{
		        def userId = userRoleRel.user?.id;
                userRoleRel.delete(flush:true)
                flash.message = "Role ${userRoleRel.role} unassigned"
                redirect(action: show, controller: 'user', id: userId)
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
        def userRoleRel = UserRoleRel.get( params.id )

        if(!userRoleRel) {
            flash.message = "UserRoleRel not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            return [ userRoleRel : userRoleRel ]
        }
    }

    def update = {
        def userRoleRel = UserRoleRel.get( params.id )
        if(userRoleRel) {
            userRoleRel.properties = params
            if(!userRoleRel.hasErrors() && userRoleRel.save()) {
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
        return ['userRoleRel':userRoleRel, 'userId':params["user.id"]]
    }

    def save = {
        def userRoleRel = new UserRoleRel(params)
        if(!userRoleRel.hasErrors() && userRoleRel.save()) {
            flash.message = "UserRoleRel ${userRoleRel.id} created"
            redirect(action: show, controller: 'user', id: userRoleRel.user?.id)
        }
        else {
            render(view:'create',model:[userRoleRel:userRoleRel, userId:params["user.id"]])
        }
    }
}