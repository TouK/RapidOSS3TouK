/*
* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be
* noted in a separate copyright notice. All rights reserved.
* This file is part of RapidCMDB.
*
* RapidCMDB is free software; you can redistribute it and/or modify
* it under the terms version 2 of the GNU General Public License as
* published by the Free Software Foundation. This program is distributed
* in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
* even the implied warranty of MERCHANTABILITY or FITNESS FOR A
* PARTICULAR PURPOSE. See the GNU General Public License for more
* details.
*
* You should have received a copy of the GNU General Public License
* along with this program; if not, write to the Free Software
* Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
* USA.
*/
package auth

import org.jsecurity.SecurityUtils
import com.ifountain.rcmdb.domain.util.ControllerUtils
import com.ifountain.rcmdb.exception.MessageSourceException

class RsUserController {

    def index = {redirect(action: list, params: params)}

    // the delete, save and update actions only accept POST requests
    def allowedMethods = []

    def list = {
        flash.errors = null;
        if (!params.max) params.max = 100
        [userList: RsUser.list(params)]
    }

    def show = {
        flash.errors = null;
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
            if (session.username != rsUser.username)
            {
                rsUser.remove()
                flash.message = "User ${params.id} deleted"
                redirect(action: list)
            }
            else {
                addError("default.custom.error", ["Can not delete your own account"])
                flash.errors = this.errors;
                redirect(action: list)
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
            def availableGroups = availableGroupsForUserGroups(rsUser.groups);
            return [rsUser: rsUser, availableGroups: availableGroups,userGroups:rsUser.groups]
        }
    }

    def availableGroupsForUserGroups(userGroups)
    {

        def availableGroups = Group.list();
        def userGroupNames = [:];
        userGroups.each {
            userGroupNames[it.name] = it;
        };
        return availableGroups.findAll {!userGroupNames.containsKey(it.name)}
    }


    def changeProfileData = {
        def rsUser = RsUser.get(username: params.username)
        if (rsUser) {

            withFormat {
                xml {
                    render(contentType: "text/xml") {
                        Edit {
                            email(rsUser.email)
                        }
                    }

                }
            }
        }
        else {
            addError("default.object.not.found", [RsUser.class.name, params.username]);
            withFormat {
                xml {render(text: errorsToXml(errors), contentType: "text/xml")}
            }
        }
    }
    def changeProfile = {
        def rsUser = RsUser.get(username: params.username)
        if (rsUser) {
            def updateParams = [:]

            def password1 = params["password1"];
            def password2 = params["password2"];

            if (password1 != password2) {
                addError("default.passwords.dont.match", []);
                withFormat {
                    xml {render(text: errorsToXml(errors), contentType: "text/xml")}
                }
                return;
            }
            if (password1 && password1 != "") {
                def oldPassword = params["oldPassword"];
                if (! rsUser.isPasswordSame(oldPassword)) {
                    addError("default.oldpassword.dont.match", []);
                    withFormat {
                        xml {render(text: errorsToXml(errors), contentType: "text/xml")}
                    }
                    return;
                }
                updateParams.password = password1;
            }


            updateParams.email = params["email"]
            RsUser.updateUser(rsUser,updateParams)
            if (!rsUser.hasErrors()) {
                withFormat {
                    xml {render(text: ControllerUtils.convertSuccessToXml("Profile changed."), contentType: "text/xml")}
                }
            }
            else {
                withFormat {
                    xml {render(text: errorsToXml(searchQuery.errors), contentType: "text/xml")}
                }
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
            def exception=null;

            def userProps=ControllerUtils.getClassProperties(params, RsUser);
            if (params["password1"] != params["password2"]) {
                exception=new MessageSourceException("default.passwords.dont.match", [] as Object[]);
            }


            if(exception==null)
            {
                userProps.password= params["password1"];
                try{
                    rsUser=RsUser.updateUser(rsUser,userProps);
                }
                catch(MessageSourceException e)
                {
                    exception=e;
                }
                userProps.remove("password");

            }

            if(exception!=null)
            {
                addError(exception.getCode(),Arrays.asList(exception.getArgs()))
                flash.errors = this.errors;

                userProps.each {String propName, value ->
                    rsUser.setProperty(propName, value, false);
                }
                render(view: 'edit', model: [rsUser: rsUser, availableGroups:availableGroupsForUserGroups(userProps.groups),userGroups:userProps.groups])
                return;
            }
            else
            {
                if (!rsUser.hasErrors()) {
                    flash.message = "User ${params.id} updated"
                    redirect(action: show, id: rsUser.id)
                }
                else {
                    render(view: 'edit', model: [rsUser: rsUser, availableGroups:availableGroupsForUserGroups(userProps.groups),userGroups:userProps.groups])
                }
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
        return ['rsUser': rsUser, availableGroups: Group.list(),userGroups:[]]
    }

    def save = {
        def exception=null;

        def userProps=ControllerUtils.getClassProperties(params, RsUser);
        if (params["password1"] != params["password2"]) {
            exception=new MessageSourceException("default.passwords.dont.match", [] as Object[]);
        }


        def rsUser=null;

        if(exception==null)
        {
            userProps.password= params["password1"];
            try{
                rsUser=RsUser.addUniqueUser(userProps);
            }
            catch(MessageSourceException e)
            {
                exception=e;
            }
            userProps.remove("password");

        }

        if(exception!=null)
        {
            addError(exception.getCode(),Arrays.asList(exception.getArgs()))
            flash.errors = this.errors;
            userProps.remove("id");
            def tmpUser = new RsUser(userProps);
            render(view: 'create', model: [rsUser: tmpUser, availableGroups: availableGroupsForUserGroups(userProps.groups),userGroups:userProps.groups])
            return;
        }
        else
        {
            if (!rsUser.hasErrors()) {
               flash.message = "User ${rsUser.id} created"
               redirect(action: show, id: rsUser.id)
            }
            else {
                render(view: 'create', model: [rsUser: rsUser, availableGroups: availableGroupsForUserGroups(userProps.groups),userGroups:userProps.groups])
            }
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
            def userProps=[username: params["username"],password:password1,groups:[group]]
            def rsUser = RsUser.addUniqueUser(userProps);
            if (!rsUser.hasErrors()) {
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