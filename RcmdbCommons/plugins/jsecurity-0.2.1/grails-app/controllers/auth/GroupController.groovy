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
import com.ifountain.rcmdb.domain.util.DomainClassUtils
import com.ifountain.rcmdb.exception.MessageSourceException;
/**
 * Created by IntelliJ IDEA.
 * User: Sezgin Kucukkaraaslan
 * Date: Sep 26, 2008
 * Time: 2:03:41 PM
 * To change this template use File | Settings | File Templates.
 */
class GroupController {
    def index = {redirect(action: list, params: params)}

    // the delete, save and update actions only accept POST requests
    def allowedMethods = [delete: 'POST', save: 'POST', update: 'POST']

    def list = {
        if (!params.max) params.max = 100
        if (!params.sort) params.sort = "name"
        [groupList: Group.search("alias:*", params).results]
    }

    def show = {
        def group = Group.get([id: params.id])

        if (!group) {
            flash.message = "Group not found with id ${params.id}"
            redirect(action: list)
        }
        else {
            if (group.class != Group)
            {
                def controllerName = group.class.simpleName;
                if (controllerName.length() == 1)
                {
                    controllerName = controllerName.toLowerCase();
                }
                else
                {
                    controllerName = controllerName.substring(0, 1).toLowerCase() + controllerName.substring(1);
                }
                redirect(action: show, controller: controllerName, id: params.id)
            }
            else
            {
                return [group: group]
            }
        }
    }

    def delete = {
        def group = Group.get([id: params.id])
        if (group) {
            try{
                Group.removeGroup(group)
                flash.message = "Group ${params.id} deleted"
                redirect(action: list)
            }
            catch(e){
                addError("default.custom.error", [e.getMessage()])
                flash.errors = this.errors;
                redirect(action: list)
            }
        }
        else {
            flash.message = "Group not found with id ${params.id}"
            redirect(action: list)
        }
    }

    def edit = {
        def group = Group.get([id: params.id])

        if (!group) {
            flash.message = "Group not found with id ${params.id}"
            redirect(action: list)
        }
        else {
            def users = group.users.sort{it.username};
            def availableUsers=availableUsersForGroupUsers(users)
            return [group: group, availableUsers: availableUsers, groupUsers:users]
        }
    }



    def update = {
        def group = Group.get([id: params.id])
        if (group) {
            def groupProps=ControllerUtils.getClassProperties(params, Group);


            def exception=null;

            try{
                Group.updateGroup(group,groupProps);
            }
            catch(e)
            {
                exception=e;
            }

            if(exception!=null)
            {
                addExceptionToError(exception);
                flash.errors = this.errors;

                def tmpGroup = new Group();

                groupProps.each {String propName, value ->
                    tmpGroup.setProperty(propName, value, false);
                }

                render(view: 'create', model: [group: tmpGroup,availableUsers:availableUsersForGroupUsers(groupProps.users),groupUsers:groupProps.users])
                return;
            }
            else
            {
                if (!group.hasErrors()) {
                    flash.message = "Group ${params.id} updated"
                    redirect(action: show, id: group.id)
                }
                else {
                    render(view: 'edit', model: [group: group,availableUsers:availableUsersForGroupUsers(groupProps.users),groupUsers:groupProps.users])
                }
            }
        }
        else {
            flash.message = "Group not found with id ${params.id}"
            redirect(action: edit, id: params.id)
        }
    }

    def create = {
        def group = new Group()
        group.properties = params
        return ['group': group, availableUsers: RsUser.list([sort:"username"]),groupUsers:[]]
    }
    def addExceptionToError = { exception ->
        if(exception instanceof MessageSourceException)
        {
            addError(exception.getCode(),Arrays.asList(exception.getArgs()))
        }
        else
        {
            addError("default.custom.error", [e.getMessage()])
        }
    }
    def save = {
        def groupProps=ControllerUtils.getClassProperties(params, Group);


        def group=null;
        def exception=null;

        try{
            group = Group.addUniqueGroup(groupProps)
        }
        catch(e)
        {
            exception=e;
        }


        if(exception!=null)
        {
            addExceptionToError(exception);
            
            flash.errors = this.errors;

            def tmpGroup = new Group();
            
            groupProps.each {String propName, value ->
                tmpGroup.setProperty(propName, value, false);
            }

            render(view: 'create', model: [group: tmpGroup,availableUsers:availableUsersForGroupUsers(groupProps.users),groupUsers:groupProps.users])
            return;
        }
        else
        {

            if (!group.hasErrors()) {
                flash.message = "Group ${group.id} created"
                redirect(action: show, id: group.id)
            }
            else {
                render(view: 'create', model: [group: group,availableUsers:availableUsersForGroupUsers(groupProps.users),groupUsers:groupProps.users])
            }
        }
    }

    def availableUsersForGroupUsers(users)
    {
        def availableUsers = RsUser.list([sort:"username"]);
        def groupUserNames = [:];
        users.each {
            groupUserNames[it.username] = it;
        };
        return availableUsers.findAll {!groupUserNames.containsKey(it.username)}
    }


    def reloadOperations = {
        def modelClass = grailsApplication.getClassForName("Group")
        if (modelClass)
        {
            try
            {
                modelClass.metaClass.invokeStaticMethod(modelClass, "reloadOperations", [] as Object[]);
                flash.message = "Model operations reloaded"
                redirect(action: list)
            } catch (t)
            {
                flash.message = "Exception occurred while reloading model operations Reason:${t.toString()}"
                redirect(action: list)
            }
        }
        else
        {
            flash.message = "Model currently not loaded by application. You should reload application."
            redirect(action: list)
        }
    }
}