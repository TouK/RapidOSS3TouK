package auth;  

import org.jsecurity.crypto.hash.Sha1Hash         
class RsUserController {
    
    def index = { redirect(action:list,params:params) }

    // the delete, save and update actions only accept POST requests
    def allowedMethods = [delete:'POST', save:'POST', update:'POST']

    def list = {
        if(!params.max) params.max = 10
        [ userList: RsUser.list( params ) ]
    }

    def show = {
        def user = RsUser.get( params.id )

        if(!user) {
            flash.message = "User not found with id ${params.id}"
            redirect(action:list)
        }
        else { return [ user : user ] }
    }

    def delete = {
        def user = RsUser.get( params.id )
        if(user) {
            try{
                user.delete(flush:true)
                flash.message = "User ${params.id} deleted"
                redirect(action:list)
            }
            catch(e){
                def errors =[message(code:"default.couldnot.delete", args:[RsUser.class.getName(), user])]
                flash.errors = errors;
                redirect(action:show, id:user.id) 
            }
        }
        else {
            flash.message = "User not found with id ${params.id}"
            redirect(action:list)
        }
    }

    def edit = {
        def user = RsUser.get( params.id )

        if(!user) {
            flash.message = "User not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            return [ user : user ]
        }
    }

    def update = {
        def user = User.get( params.id )
        
        if(user) {
	        def password1 = params["password1"];
		    def password2 = params["password2"];
		    if(password1 != password2){
			    def errors =[message(code:"default.passwords.dont.match", args:[])]
	            flash.errors = errors;
	            redirect(action:show,id:user.id)
	            return;
			}
			if(password1 != ""){
				user.passwordHash = new Sha1Hash(password1).toHex();	
			}
            user.username = params["username"];
            if(!user.hasErrors() && user.save()) {
                flash.message = "User ${params.id} updated"
                redirect(action:show,id:user.id)
            }
            else {
                render(view:'edit',model:[user:user])
            }
        }
        else {
            flash.message = "User not found with id ${params.id}"
            redirect(action:edit,id:params.id)
        }
    }

    def create = {
        def user = new RsUser()
        user.properties = params
        return ['user':user]
    }

    def save = {
	    def password1 = params["password1"];
	    def password2 = params["password2"];
	    if(password1 != password2){
		    def errors =[message(code:"default.passwords.dont.match", args:[])]
            flash.errors = errors;
            render(view:'create',model:[user:new RsUser(username:params["username"])])
            return;
		}
		
        def user = new RsUser(username: params["username"], passwordHash: new Sha1Hash(password1).toHex());
        if(!user.hasErrors() && user.save()) {
            flash.message = "User ${user.id} created"
            redirect(action:show,id:user.id)
        }
        else {
            render(view:'create',model:[user:user])
        }
    }
}