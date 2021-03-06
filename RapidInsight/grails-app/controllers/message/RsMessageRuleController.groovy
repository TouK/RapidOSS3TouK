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
import groovy.xml.MarkupBuilder
import auth.ChannelUserInformation
import auth.Role
import auth.RsUser


class RsMessageRuleController {

    def index = {redirect(action: list, params: params)}

    // the delete, save and update actions only accept POST requests
    def allowedMethods = [save: 'POST', update: 'POST']

    def list = {
        def ruleQuery = " ( users:${session.username.exactQuery()} AND ruleType:self )"
        def isAdminUser=false;
        if(RsUser.hasRole(session.username, Role.ADMINISTRATOR)){
            isAdminUser=true;
            ruleQuery += " OR ruleType:public OR ruleType:system";
        }
        def myRules = message.RsMessageRule.searchEvery(ruleQuery, [sort: "id", order: "asc"])
        def ruleGroups = [];
        ruleGroups.add(["id": "enabledRules", "name": "My Enabled Rules", 'nodeType': 'group', "rules": []]);
        ruleGroups.add(["id": "disabledRules", "name": "My Disabled Rules", 'nodeType': 'group', "rules": []]);
        if(isAdminUser){
            ruleGroups.add(["id": "publicEnabledRules", "name": "Other Users & Groups Enabled Rules", 'nodeType': 'group', "rules": []]);
            ruleGroups.add(["id": "publicDisabledRules", "name": "Other Users & Groups Disabled Rules", 'nodeType': 'group', "rules": []]);

            ruleGroups.add(["id": "systemEnabledRules", "name": "System Enabled Rules", 'nodeType': 'group', "rules": []]);
            ruleGroups.add(["id": "systemDisabledRules", "name": "System Disabled Rules", 'nodeType': 'group', "rules": []]);
        }
        myRules.each {rule ->

            def searchQuery = search.SearchQuery.get(id: rule.searchQueryId);
            def ruleProps = [:]
            ruleProps.id = rule.id;
            ruleProps.delay = rule.delay;
            ruleProps.sendClearEventType = rule.sendClearEventType;
            ruleProps.enabled = rule.enabled;
            ruleProps.name = searchQuery ? searchQuery.name : rule.searchQueryId;
            ruleProps.destinationType = rule.destinationType;
            ruleProps.ruleType = rule.ruleType;
            ruleProps.nodeType = 'rule';
            ruleProps.users = rule.users;
            ruleProps.groups = rule.groups;
            ruleProps.addedByUser = rule.addedByUser;

            if(rule.ruleType == "self")
            {
		        if (rule.enabled)
		        {
		            ruleGroups[0].rules.add(ruleProps);
		        }
		        else
		        {
		            ruleGroups[1].rules.add(ruleProps);
		        }
            }
            else if(rule.ruleType == "public")
            {
		        if (rule.enabled)
		        {
		            ruleGroups[2].rules.add(ruleProps);
		        }
		        else
		        {
		            ruleGroups[3].rules.add(ruleProps);
		        }
            }
            else if(rule.ruleType == "system")
            {
		        if (rule.enabled)
		        {
		            ruleGroups[4].rules.add(ruleProps);
		        }
		        else
		        {
		            ruleGroups[5].rules.add(ruleProps);
		        }
            }
        }

        def sw = new StringWriter();
        def builder = new MarkupBuilder(sw);
        builder.Rules() {
            ruleGroups.each {ruleGroup ->
                def groupProps = ruleGroup.clone();
                groupProps.remove("rules");
                builder.Rule(groupProps) {
                    ruleGroup.rules.each {ruleProps ->
                        builder.Rule(ruleProps);
                    }
                }

            }
        }
        render(contentType: "text/xml", text: sw.toString())

    }

    def delete = {
        def rsMessageRule = RsMessageRule.get([id: params.id])
        if (rsMessageRule) {
            rsMessageRule.remove()
            render(text: ControllerUtils.convertSuccessToXml("RsMessageRule ${rsMessageRule.id} deleted"), contentType: "text/xml")
        }
        else {
            addError("default.couldnot.delete", [RsMessageRule, "RsMessageRule not found with id ${params.id}"])
            render(text: errorsToXml(this.errors), contentType: "text/xml");
        }
    }



    def update = {
        def rsMessageRule = RsMessageRule.get([id: params.id])
        if (rsMessageRule) {
            try {
                def ruleParams = ControllerUtils.getClassProperties(params, RsMessageRule);
                if(params.ruleType=='self')
                {
                    ruleParams.users=session.username;
                }
                RsMessageRule.updateMessageRuleForUser(rsMessageRule, ruleParams, session.username)
                if (!rsMessageRule.hasErrors()) {
                    withFormat {
                        html {
                            flash.message = "RsMessageRule ${rsMessageRule.id} updated";
                            redirect(uri: params.targetURI)
                        }
                        xml {
                            render(text: ControllerUtils.convertSuccessToXml("RsMessageRule ${rsMessageRule.id} updated"), contentType: "text/xml")
                        }
                    }
                }
                else {
                    withFormat {
                        html {
                            flash.errors = rsMessageRule.errors;
                            def targetURI = params.errorTargetURI ? params.errorTargetURI : params.targetURI
                            redirect(uri: targetURI);
                        }
                        xml {
                            render(text: errorsToXml(rsMessageRule.errors), contentType: "text/xml")
                        }
                    }
                }
            }
            catch (e)
            {
                addError("default.couldnot.create", [RsMessageRule, e.getMessage()])
                withFormat {
                    html {
                        flash.errors = errors;
                        def targetURI = params.errorTargetURI ? params.errorTargetURI : params.targetURI
                        redirect(uri: targetURI);
                    }
                    xml {
                        render(text: errorsToXml(this.errors), contentType: "text/xml")
                    }
                }
                return;
            }
        }
        else {
            addError("default.couldnot.create", [RsMessageRule, "RsMessageRule not found with id ${params.id}"])
            withFormat {
                html {
                    flash.errors = errors;
                    def targetURI = params.errorTargetURI ? params.errorTargetURI : params.targetURI
                    redirect(uri: targetURI);
                }
                xml {
                    render(text: errorsToXml(this.errors), contentType: "text/xml")
                }
            }
        }
    }


    def save = {
        try {
            def ruleParams = ControllerUtils.getClassProperties(params, RsMessageRule);
            ruleParams.addedByUser=session.username;            
            def rsMessageRule = RsMessageRule.addMessageRuleForUser(ruleParams, session.username)
            if (!rsMessageRule.hasErrors()) {
                withFormat {
                    html {
                        flash.message = "RsMessageRule ${rsMessageRule.id} created";
                        redirect(uri: params.targetURI)
                    }
                    xml {
                        render(text: ControllerUtils.convertSuccessToXml("RsMessageRule ${rsMessageRule.id} created"), contentType: "text/xml")
                    }
                }

            }
            else {
                withFormat {
                    html {
                        flash.errors = rsMessageRule.errors;
                        def targetURI = params.errorTargetURI ? params.errorTargetURI : params.targetURI
                        redirect(uri: targetURI);
                    }
                    xml {
                        render(text: errorsToXml(rsMessageRule.errors), contentType: "text/xml")
                    }
                }

            }
        }
        catch (e)
        {
            addError("default.couldnot.create", [RsMessageRule, e.getMessage()])
            withFormat {
                html {
                    flash.errors = errors;
                    def targetURI = params.errorTargetURI ? params.errorTargetURI : params.targetURI
                    redirect(uri: targetURI);
                }
                xml {
                    render(text: errorsToXml(this.errors), contentType: "text/xml")
                }
            }
            return;
        }
    }

    def enableRule = {
        def rsMessageRule = RsMessageRule.get([id: params.id])
        if (rsMessageRule) {
            rsMessageRule.update(enabled: true);
            if (!rsMessageRule.hasErrors()) {
                withFormat {
                    html {
                        flash.message = "RsMessageRule ${rsMessageRule.id} successfully enabled.";
                        redirect(uri: params.targetURI);
                    }
                    xml {
                        render(text: ControllerUtils.convertSuccessToXml("RsMessageRule ${rsMessageRule.id} updated"), contentType: "text/xml")
                    }
                }
            }
            else {
                withFormat {
                    html {
                        flash.errors = rsMessageRule.errors;
                        def targetURI = params.errorTargetURI ? params.errorTargetURI : params.targetURI
                        redirect(uri: targetURI);
                    }
                    xml {
                        render(text: errorsToXml(rsMessageRule.errors), contentType: "text/xml")
                    }
                }
            }
        }
        else {
            addError("default.couldnot.create", [RsMessageRule, "RsMessageRule not found with id ${params.id}"])
            withFormat {
                html {
                    flash.errors = errors;
                    def targetURI = params.errorTargetURI ? params.errorTargetURI : params.targetURI
                    redirect(uri: targetURI);
                }
                xml {
                    render(text: errorsToXml(this.errors), contentType: "text/xml")
                }
            }

        }
    }
    def disableRule = {
        def rsMessageRule = RsMessageRule.get([id: params.id])
        if (rsMessageRule) {
            rsMessageRule.update(enabled: false);
            if (!rsMessageRule.hasErrors()) {
                withFormat {
                    html {
                        flash.message = "RsMessageRule ${rsMessageRule.id} successfully disabled.";
                        redirect(uri: params.targetURI);
                    }
                    xml {
                        render(text: ControllerUtils.convertSuccessToXml("RsMessageRule ${rsMessageRule.id} updated"), contentType: "text/xml")
                    }
                }

            }
            else {
                withFormat {
                    html {
                        flash.errors = rsMessageRule.errors;
                        def targetURI = params.errorTargetURI ? params.errorTargetURI : params.targetURI
                        redirect(uri: targetURI);
                    }
                    xml {
                        render(text: errorsToXml(rsMessageRule.errors), contentType: "text/xml")
                    }
                }

            }
        }
        else {
            addError("default.couldnot.create", [RsMessageRule, "RsMessageRule not found with id ${params.id}"])
            withFormat {
                html {
                    flash.errors = errors;
                    def targetURI = params.errorTargetURI ? params.errorTargetURI : params.targetURI
                    redirect(uri: targetURI);
                }
                xml {
                    render(text: errorsToXml(this.errors), contentType: "text/xml")
                }
            }

        }
    }
}