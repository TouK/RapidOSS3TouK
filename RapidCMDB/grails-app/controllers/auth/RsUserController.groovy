package auth;  
import com.ifountain.rcmdb.domain.util.ControllerUtils;
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
        def rsUser = RsUser.get( id:params.id )

        if(!rsUser) {
            flash.message = "User not found with id ${params.id}"
            redirect(action:list)
        }
        else { return [ rsUser : rsUser ] }
    }

    def delete = {
        def rsUser = RsUser.get( id:params.id )
        if(rsUser) {
            try{
                rsUser.remove()
                flash.message = "User ${params.id} deleted"
                redirect(action:list)
            }
            catch(e){
                def errors =[message(code:"default.couldnot.delete", args:[RsUser.class.getName(), rsUser])]
                flash.errors = errors;
                redirect(action:show, id:rsUser.id) 
            }
        }
        else {
            flash.message = "User not found with id ${params.id}"
            redirect(action:list)
        }
    }

    def edit = {
        def rsUser = RsUser.get( id:params.id )

        if(!rsUser) {
            flash.message = "User not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            return [ rsUser : rsUser ]
        }
    }

    def update = {
        def rsUser = RsUser.get( id:params.id )
        
        if(rsUser) {
	        def password1 = params["password1"];
		    def password2 = params["password2"];
		    if(password1 != password2){
			    def errors =[message(code:"default.passwords.dont.match", args:[])]
	            flash.errors = errors;
	            redirect(action:show,id:rsUser.id)
	            return;
			}
			if(password1 != ""){
				rsUser.passwordHash = new Sha1Hash(password1).toHex();	
			}
            rsUser.update([username:params["username"]])
            if(!rsUser.hasErrors()) {
                flash.message = "User ${params.id} updated"
                redirect(action:show,id:rsUser.id)
            }
            else {
                render(view:'edit',model:[rsUser:rsUser])
            }
        }
        else {
            flash.message = "User not found with id ${params.id}"
            redirect(action:edit,id:params.id)
        }
    }

    def create = {
        def rsUser = new RsUser()
        rsUser.properties = params
        return ['rsUser':rsUser]
    }

    def save = {
	    def password1 = params["password1"];
	    def password2 = params["password2"];
	    if(password1 != password2){
		    def errors =[message(code:"default.passwords.dont.match", args:[])]
            flash.errors = errors;
            render(view:'create',model:[rsUser:new RsUser(username:params["username"])])
            return;
		}
		
        def rsUser = RsUser.add(username: params["username"], passwordHash: new Sha1Hash(password1).toHex());
        if(!rsUser.hasErrors()) {
            flash.message = "User ${rsUser.id} created"
            redirect(action:show,id:rsUser.id)
        }
        else {
            render(view:'create',model:[rsUser:rsUser])
        }
    }
}