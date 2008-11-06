package auth

import org.jsecurity.crypto.hash.Sha1Hash
import org.jsecurity.SecurityUtils
import com.ifountain.rcmdb.domain.util.ControllerUtils

class RsUserController {

    def index = {redirect(action: list, params: params)}

    // the delete, save and update actions only accept POST requests
    def allowedMethods = []

    def list = {
        if (!params.max) params.max = 10
        [userList: RsUser.list(params)]
    }

    def show = {
        def rsUser = RsUser.get(id: params.id)

        if (!rsUser) {
            flash.message = "User not found with id ${params.id}"
            redirect(action: list)
        }
        else {return [rsUser: rsUser]}
    }

    def delete = {
        def rsUser = RsUser.get(id: params.id)
        if (rsUser) {
            try {
                rsUser.remove()
                flash.message = "User ${params.id} deleted"
                redirect(action: list)
            }
            catch (e) {
                def errors = [message(code: "default.couldnot.delete", args: [RsUser.class.getName(), rsUser])]
                flash.errors = errors;
                redirect(action: show, id: rsUser.id)
            }
        }
        else {
            flash.message = "User not found with id ${params.id}"
            redirect(action: list)
        }
    }

    def edit = {
        def rsUser = RsUser.get(id: params.id)

        if (!rsUser) {
            flash.message = "User not found with id ${params.id}"
            redirect(action: list)
        }
        else {
            return [rsUser: rsUser]
        }
    }

    def changePassword = {
        def rsUser = RsUser.get(username: params.username)
        if (rsUser) {
            def password1 = params["password1"];
            def password2 = params["password2"];
            if (!SecurityUtils.subject.hasRole("Administrator")) {
                def oldPassword = params["oldPassword"];
                if (new Sha1Hash(oldPassword).toHex() != rsUser.passwordHash) {
                    addError("default.oldpassword.dont.match", []);
                    withFormat {
                        xml {render(text: errorsToXml(errors), contentType: "text/xml")}
                    }
                    return;
                }
            }
            if (password1 != password2) {
                addError("default.passwords.dont.match", []);
                withFormat {
                    xml {render(text: errorsToXml(errors), contentType: "text/xml")}
                }
                return;
            }
            if (password1 && password1 != "") {
                def passHash = new Sha1Hash(password1).toHex();
                rsUser.update(passwordHash: passHash);
                if (!rsUser.hasErrors()) {
                    withFormat {
                        xml {render(text: ControllerUtils.convertSuccessToXml("Password changed."), contentType: "text/xml")}
                    }
                }
                else {
                    withFormat {
                        xml {render(text: errorsToXml(searchQuery.errors), contentType: "text/xml")}
                    }
                }
            }
            else {
                addError("default.null.message", ["passwordHash", RsUser.class.getName()]);
                withFormat {
                    xml {render(text: errorsToXml(errors), contentType: "text/xml")}
                }
                return;
            }
        }
        else {
            addError("default.object.not.found", [RsUser.class.name, params.username]);
            withFormat {
                xml {render(text: errorsToXml(errors), contentType: "text/xml")}
            }
        }
    }

    def update = {
        def rsUser = RsUser.get(id: params.id)

        if (rsUser) {
            def password1 = params["password1"];
            def password2 = params["password2"];
            if (password1 != password2) {
                def errors = [message(code: "default.passwords.dont.match", args: [])]
                flash.errors = errors;
                redirect(action: show, id: rsUser.id)
                return;
            }
            if (password1 && password1 != "") {
                rsUser.passwordHash = new Sha1Hash(password1).toHex();
            }
            rsUser.update([username: params["username"]])
            if (!rsUser.hasErrors()) {
                flash.message = "User ${params.id} updated"
                redirect(action: show, id: rsUser.id)
            }
            else {
                render(view: 'edit', model: [rsUser: rsUser])
            }
        }
        else {
            flash.message = "User not found with id ${params.id}"
            redirect(action: edit, id: params.id)
        }
    }

    def create = {
        def rsUser = new RsUser()
        rsUser.properties = params
        return ['rsUser': rsUser]
    }

    def save = {
        def password1 = params["password1"];
        def password2 = params["password2"];
        if (password1 != password2) {
            def errors = [message(code: "default.passwords.dont.match", args: [])]
            flash.errors = errors;
            render(view: 'create', model: [rsUser: new RsUser(username: params["username"])])
            return;
        }

        def rsUser = RsUser.add(username: params["username"], passwordHash: new Sha1Hash(password1).toHex());
        if (!rsUser.hasErrors()) {
            flash.message = "User ${rsUser.id} created"
            redirect(action: show, id: rsUser.id)
        }
        else {
            render(view: 'create', model: [rsUser: rsUser])
        }
    }

    def createUser = {
        def password1 = params["password1"];
        def password2 = params["password2"];
        if (!password1 || !password2 || password1 != password2) {
            addError("default.passwords.dont.match", []);
            render(text: errorsToXml(errors), contentType: "text/xml")
            return;
        }
        def groupname = params.groupname;
        if (!groupname || groupname.trim().length() == 0) {
            addError("default.missing.mandatory.parameter", ["groupname"]);
            render(text: errorsToXml(errors), contentType: "text/xml")
            return;
        }
        def group = Group.get(name: groupname);
        if (group) {
            def rsUser = RsUser.add(username: params["username"], passwordHash: new Sha1Hash(password1).toHex());
            if (!rsUser.hasErrors()) {
                rsUser.addRelation(groups: group);
                render(text: ControllerUtils.convertSuccessToXml("User ${params['username']} created"), contentType: "text/xml")
            }
            else {
                render(text: errorsToXml(rsUser.errors), contentType: "text/xml")
            }
        }
        else {
            addError("default.object.not.found", [Group.class.name, groupname]);
            render(text: errorsToXml(errors), contentType: "text/xml")
        }
    }
}