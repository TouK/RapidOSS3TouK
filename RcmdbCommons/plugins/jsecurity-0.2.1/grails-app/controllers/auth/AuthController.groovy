package auth

import org.jsecurity.SecurityUtils
import org.jsecurity.authc.AuthenticationException
import org.jsecurity.authc.UsernamePasswordToken
import com.ifountain.rcmdb.util.ExecutionContextManagerUtils
import com.ifountain.rcmdb.mobile.MobileUtils
import com.ifountain.rcmdb.domain.util.ControllerUtils

class AuthController {
    def jsecSecurityManager

    def index = { redirect(action: 'login', params: params) }

    def login = {
        if(MobileUtils.isMobile(request)) {
        	redirect(action:'mobilelogin', params: params)
        	return;
        }
        if(params.format == "xml"){
            render(contentType:'text/xml') {
                Authenticate()
                {
                    Url(params.targetUri);
                }
            }
        }
        else{
            return [ username: params.login, rememberMe: (params.rememberMe != null), targetUri: params.targetUri ]
        }
    }

    def mobilelogin = {
    	return[username:params.login, targetUri: params.targetUri, flash : params.flash]
    }

    def signIn = {
        if(params.login==null)
        {
            params.login="";
        }
        if(params.password==null)
        {
           params.password="";
        }
        
        def authToken = new UsernamePasswordToken(params.login, params.password)

        // Support for "remember me"
        if (params.rememberMe) {
            authToken.rememberMe = true
        }

        try{
            // Perform the actual login. An AuthenticationException
            // will be thrown if the username is unrecognised or the
            // password is incorrect.
            def delagteObject = this.jsecSecurityManager.login(authToken)

            // If a controller redirected to this page, redirect back
            // to it. Otherwise redirect to the root URI.
            def targetUri = params.targetUri ?: "/"

            log.info "Redirecting to '${targetUri}'."
            session.username = delagteObject.principal;

            ExecutionContextManagerUtils.addUsernameToCurrentContext (session.username)
            def statClass=this.class.classLoader.loadClass("Statistics");
            statClass.record("user.login","");

            if(params.format == "xml"){
                render(contentType:'text/xml') {
                    Successful("Successfully logged in.")
                }
            }
            else{
                redirect(uri: targetUri)
            }

        }
        catch (AuthenticationException ex){
            // Authentication failed, so display the appropriate message
            // on the login page.
            log.info "Authentication failure for user '${params.login}'."
            flash.message = message(code: "default.custom.error",args:["Login Failed : ${ex.getMessage()}"])


            // Keep the username and "remember me" setting so that the
            // user doesn't have to enter them again.
            def m = [ login: params.login ]
            if (params.rememberMe) {
                m['rememberMe'] = true
            }

            // Remember the target URI too.
            if (params.targetUri) {
                m['targetUri'] = params.targetUri
            }

            //ri mobile needs flash message in params
            m['flash'] = flash.message

            // Now redirect back to the login page.
            if(params.format == "xml"){
                render(text: ControllerUtils.convertErrorToXml("Login Failed : ${ex.getMessage()}"), contentType: "text/xml");
            }
            else{
                redirect(action: 'login', params: m)
            }

        }
    }

    def logout = {
        // Log the user out of the application.
        SecurityUtils.subject?.logout()

        def targetUri = params.targetUri ?: "/"
        redirect(uri: targetUri)
    }

    def unauthorized = {
        addError("not.authorized");
        withFormat {
            html{render 'You do not have permission to access this page.'}
            xml {render(text: errorsToXml(errors), contentType: "text/xml")}

        }
    }
}
