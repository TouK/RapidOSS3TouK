package message

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
import com.ifountain.rcmdb.domain.util.ControllerUtils
import com.ifountain.rcmdb.domain.util.DomainClassUtils
import org.codehaus.groovy.grails.web.metaclass.RenderDynamicMethod;


class RsMessageRuleController {

    def getTemplateName(){
        return "/../../../../web-app/index/notifications"
    }
    def index = { redirect(action:list,params:params) }

    // the delete, save and update actions only accept POST requests
    def allowedMethods = [delete:'POST', save:'POST', update:'POST']

    def list = {
        render(template:getTemplateName(), model:[currentAction:'list']);
    }

    def show = {
        def rsMessageRule = RsMessageRule.get([id:params.id])

        if(!rsMessageRule) {
            flash.message = "RsMessageRule not found with id ${params.id}"
            render(template:getTemplateName(), model:[currentAction:'list']);
        }
        else {
            if(rsMessageRule.class != RsMessageRule)
            {
                def controllerName = rsMessageRule.class.simpleName;
                if(controllerName.length() == 1)
                {
                    controllerName = controllerName.toLowerCase();
                }
                else
                {
                    controllerName = controllerName.substring(0,1).toLowerCase()+controllerName.substring(1);
                }
                render(template:getTemplateName(), model:[currentAction:'show', id:rsMessageRule.id]);
            }
            else
            {
                render(template:getTemplateName(), model:[currentAction:'show', rsMessageRule:rsMessageRule]);
            }
        }
    }

    def delete = {
        def rsMessageRule = RsMessageRule.get( [id:params.id])
        if(rsMessageRule) {
            try{
                rsMessageRule.remove()
                flash.message = "RsMessageRule ${params.id} deleted"
                render(template:getTemplateName(), model:[currentAction:'list']);
            }
            catch(e){
                addError("default.couldnot.delete", [RsMessageRule, rsMessageRule])
                flash.errors = this.errors;
                render(template:getTemplateName(), model:[currentAction:'list']);
            }

        }
        else {
            flash.message = "RsMessageRule not found with id ${params.id}"
            render(template:getTemplateName(), model:[currentAction:'list']);
        }
    }

    def edit = {
        def rsMessageRule = RsMessageRule.get( [id:params.id] )

        if(!rsMessageRule) {
            flash.message = "RsMessageRule not found with id ${params.id}"
            render(template:getTemplateName(), model:[currentAction:'list']);
        }
        else {
            render(template:getTemplateName(),model:[currentAction:'edit', rsMessageRule:rsMessageRule])
        }
    }


    def update = {
        def rsMessageRule = RsMessageRule.get( [id:params.id] )
        if(rsMessageRule) {
            def user=auth.RsUser.get(username:session.username)
            if(user.email == null || user.email=="")
            {
                addError("default.couldnot.create", [RsMessageRule, "Your email address is not entered"])
                flash.errors = this.errors;
                render(template:getTemplateName(),model:[currentAction:'edit', rsMessageRule:rsMessageRule])
                return;
            }
        
            rsMessageRule.update(ControllerUtils.getClassProperties(params, RsMessageRule));
            if(!rsMessageRule.hasErrors()) {
                flash.message = "RsMessageRule ${params.id} updated"
                render(template:getTemplateName(), model:[currentAction:'list']);
            }
            else {
                render(template:getTemplateName(),model:[currentAction:'edit', rsMessageRule:rsMessageRule])
            }
        }
        else {
            flash.message = "RsMessageRule not found with id ${params.id}"
            render(template:getTemplateName(), model:[currentAction:'list']);
        }
    }

    def create = {
        def rsMessageRule = new RsMessageRule()
        rsMessageRule.properties = params
        render(template:getTemplateName(), model:[currentAction:'create', rsMessageRule:rsMessageRule]);
    }

    def save = {
        def user=auth.RsUser.get(username:session.username)
        if(user.email == null || user.email=="")
        {
            addError("default.couldnot.create", [RsMessageRule, "Your email address is not entered"])
            flash.errors = this.errors;
            render(template:getTemplateName(),model:[currentAction:'create', rsMessageRule:new RsMessageRule()])
            return;
        }
        params.userId=user.id
        if(params.userId!=null)
        {
           params.userId=String.valueOf(params.userId) 
        }
        
        def rsMessageRule = RsMessageRule.add(ControllerUtils.getClassProperties(params, RsMessageRule))
        if(!rsMessageRule.hasErrors()) {
            flash.message = "RsMessageRule ${rsMessageRule.id} created"
            render(template:getTemplateName(), model:[currentAction:'list'])
        }
        else {
            render(template:getTemplateName(),model:[rsMessageRule:rsMessageRule, currentAction:'create'])
        }
    }
    def enableRule = {
        def rsMessageRule = RsMessageRule.get( [id:params.id] )
        if(rsMessageRule) {
            rsMessageRule.update(enabled:true);
            if(!rsMessageRule.hasErrors()) {
                flash.message = "RsMessageRule ${params.id} enabled"
                render(template:getTemplateName(), model:[currentAction:'list']);
            }
            else {
                flash.errors = rsMessageRule.errors;
                render(template:getTemplateName(), model:[currentAction:'list']);
            }
        }
        else {
            flash.message = "RsMessageRule not found with id ${params.id}"
            render(template:getTemplateName(), model:[currentAction:'list']);
        }
    }
    def disableRule = {
        def rsMessageRule = RsMessageRule.get( [id:params.id] )
        if(rsMessageRule) {
            rsMessageRule.update(enabled:false);
            if(!rsMessageRule.hasErrors()) {
                flash.message = "RsMessageRule ${params.id} disabled"
                render(template:getTemplateName(), model:[currentAction:'list']);
            }
            else {
                flash.errors = rsMessageRule.errors;
                render(template:getTemplateName(), model:[currentAction:'list']);
            }
        }
        else {
            flash.message = "RsMessageRule not found with id ${params.id}"
            render(template:getTemplateName(), model:[currentAction:'list']);
        }
    }
}