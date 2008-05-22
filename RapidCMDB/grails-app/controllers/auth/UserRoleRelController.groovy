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
            userRoleRel.delete()
            flash.message = "UserRoleRel ${params.id} deleted"
            redirect(action:list)
        }
        else {
            flash.message = "UserRoleRel not found with id ${params.id}"
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
        return ['userRoleRel':userRoleRel]
    }

    def save = {
        def userRoleRel = new UserRoleRel(params)
        if(!userRoleRel.hasErrors() && userRoleRel.save()) {
            flash.message = "UserRoleRel ${userRoleRel.id} created"
            redirect(action:show,id:userRoleRel.id)
        }
        else {
            render(view:'create',model:[userRoleRel:userRoleRel])
        }
    }
}