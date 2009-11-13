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

import com.ifountain.rcmdb.domain.util.ControllerUtils
import com.ifountain.rcmdb.exception.MessageSourceException

class RsUserController {

    def index = {redirect(action: list, params: params)}

    // the delete, save and update actions only accept POST requests
    def allowedMethods = []

    def list = {
        if (!params.max) params.max = 100
        if (!params.sort) params.sort = "username"
        [userList: RsUser.search("alias:*", params).results]
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
            try
            {
                RsUser.removeUser(rsUser)
                flash.message = "User ${params.id} deleted"
                redirect(action: list)
            }
            catch(e){
                addError("default.custom.error", [e.getMessage()])
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
            def userGroups =  rsUser.groups
            def availableGroups = availableGroupsForUserGroups(userGroups);
            def userChannels=getChannelInformationsBeansForUpdate(rsUser);
            return [rsUser: rsUser, availableGroups: availableGroups,userGroups:userGroups,userChannels:userChannels];
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


    def changeProfile = {
        def rsUser = RsUser.get(username: params.username)
        if (rsUser) {
            def oldUserProperties=null;

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
                def updateParams = [:]
                updateParams.password = password1;
                oldUserProperties=ControllerUtils.backupOldData(rsUser, updateParams);
                rsUser=RsUser.updateUser(rsUser,updateParams);
            }

            if(!rsUser.hasErrors())
            {
                def errorOccured=false;
                def errorXml=null;

                def addedInformations=addUserChannelInformationsFromParams(rsUser,RsUser.getEditableChannelTypes());
                addedInformations.each{ addedInfo ->
                    if(addedInfo.hasErrors())
                    {
                        errorOccured=true;
                        errorXml=errorsToXml(addedInfo.errors);
                    }
                }


                if(!errorOccured && !rsUser.hasErrors())
                {
                    withFormat {
                        xml {render(text: ControllerUtils.convertSuccessToXml("Profile changed."), contentType: "text/xml")}
                    }
                }
                else
                {
                     if(oldUserProperties !=null)
                     {
                         rsUser.update(oldUserProperties);
                     }

                     withFormat {
                        xml {render(text:errorXml, contentType: "text/xml")}
                     }
                }
            }
            else
            {
                withFormat {
                    xml {render(text: errorsToXml(rsUser.errors), contentType: "text/xml")}
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
            def userChannels=getChannelInformationsBeansForUpdate(rsUser);
            
            def exception=null;

            def oldUserProperties=null;


            def userProps=ControllerUtils.getClassProperties(params, RsUser);
            if (params["password1"] != params["password2"]) {
                exception=new MessageSourceException("default.passwords.dont.match", [] as Object[]);
            }


            if(exception==null)
            {

                userProps.password= params["password1"];
                if(userProps.password==null || userProps.password=="")
                {
                    userProps.remove("password");    
                }

                try{
                    def propsToSave=[:];
                    propsToSave.putAll(userProps);
                    propsToSave.remove("password");
                    propsToSave.put("passwordHash","");

                    oldUserProperties=ControllerUtils.backupOldData(rsUser, propsToSave);
                    rsUser=RsUser.updateUser(rsUser,userProps);                    
                }
                catch(e)
                {
                    exception=e;
                }
                userProps.remove("password");

            }

            if(exception!=null)
            {
                addExceptionToError(exception);
                flash.errors = this.errors;

                userProps.each {String propName, value ->
                    rsUser.setProperty(propName, value, false);
                }
                render(view: 'edit', model: [rsUser: rsUser, availableGroups:availableGroupsForUserGroups(userProps.groups),userGroups:userProps.groups,userChannels:userChannels])
                return;
            }
            else
            {

                if (!rsUser.hasErrors() ) {
                    def errorOccured=false;

                    def addedInformations=addUserChannelInformationsFromParams(rsUser,RsUser.getChannelTypes());
                    addedInformations.each{ addedInfo ->
                        if(addedInfo.hasErrors())
                        {
                            errorOccured=true;
                        }
                    }

                    if(!errorOccured && !rsUser.hasErrors() )
                    {
                         flash.message = "User ${params.id} updated"
                         redirect(action: show, id: rsUser.id)
                    }
                    else
                    {
                        if(oldUserProperties !=null)
                        {
                         rsUser.update(oldUserProperties);
                        }
                        render(view: 'edit', model: [rsUser: rsUser, availableGroups:availableGroupsForUserGroups(userProps.groups),userGroups:userProps.groups,userChannels:addedInformations])
                    }

                }
                else {
                    render(view: 'edit', model: [rsUser: rsUser, availableGroups:availableGroupsForUserGroups(userProps.groups),userGroups:userProps.groups,userChannels:userChannels])
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
        def userChannels=getChannelInformationsBeansForCreate();

        return ['rsUser': rsUser, availableGroups: Group.list(),userGroups:[],userChannels:userChannels];
    }

    def addUserChannelInformationsFromParams={ rsUser,channelTypes ->
        def channelInformationList=[];

        channelTypes.each{ channelType ->
            channelInformationList.add([type:channelType,destination:params[channelType]]);
        }

        def addedInformations=rsUser.addChannelInformationsAndRollBackIfErrorOccurs(channelInformationList);

        return  addedInformations;
    }

    def getChannelInformationsBeansForCreate={
        def userChannels=[];

        RsUser.getChannelTypes().each{ channelType  ->
            def channelInfo=new ChannelUserInformation();
            channelInfo.setPropertyWithoutUpdate("type",channelType);
            channelInfo.setPropertyWithoutUpdate("destination",params[channelType]);
            userChannels.add(channelInfo);
        }
        return userChannels;
    }
    def getChannelInformationsBeansForUpdate={ rsUser ->
        def userChannels=[];
        RsUser.getChannelTypes().each{ channelType  ->
            def channelInfo=rsUser.retrieveChannelInformation(channelType);
            if(channelInfo== null)
            {
                channelInfo=new ChannelUserInformation();
                channelInfo.setPropertyWithoutUpdate("type",channelType);
            }
            //only add if form is posted , params contains channelType, params does not contain channelType for edit view
            if(params.containsKey(channelType))
            {
                channelInfo.setPropertyWithoutUpdate("destination",params[channelType]);
            }

            userChannels.add(channelInfo);
        }
        return userChannels;
    }
    def addExceptionToError = { exception ->
        if(exception instanceof MessageSourceException)
        {
            addError(exception.getCode(),Arrays.asList(exception.getArgs()))
        }
        else
        {
            addError("default.custom.error", [exception.getMessage()])
        }
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
            catch(e)
            {
                exception=e;
            }
            userProps.remove("password");

        }

        if(exception!=null)
        {
            addExceptionToError(exception);
            flash.errors = this.errors;
            userProps.remove("id");
            def tmpUser = new RsUser();
            
            userProps.each {String propName, value ->
                tmpUser.setProperty(propName, value, false);
            }

            def userChannels=getChannelInformationsBeansForCreate();

            render(view: 'create', model: [rsUser: tmpUser, availableGroups: availableGroupsForUserGroups(userProps.groups),userGroups:userProps.groups,userChannels:userChannels])
            return;
        }
        else
        {
            if (!rsUser.hasErrors() ) {
                def errorOccured=false;

                def addedInformations=addUserChannelInformationsFromParams(rsUser,RsUser.getChannelTypes());
                addedInformations.each{ addedInfo ->
                    if(addedInfo.hasErrors())
                    {
                        errorOccured=true;
                    }
                }
                
                if(!errorOccured && !rsUser.hasErrors())
                {
                     flash.message = "User ${rsUser.id} created"
                     redirect(action: show, id: rsUser.id)
                }
                else
                {
                    rsUser.remove();
                    render(view: 'create', model: [rsUser: rsUser, availableGroups: availableGroupsForUserGroups(userProps.groups),userGroups:userProps.groups,userChannels:addedInformations])
                }

            }
            else {
                def userChannels=getChannelInformationsBeansForCreate();

                render(view: 'create', model: [rsUser: rsUser, availableGroups: availableGroupsForUserGroups(userProps.groups),userGroups:userProps.groups,userChannels:userChannels])
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