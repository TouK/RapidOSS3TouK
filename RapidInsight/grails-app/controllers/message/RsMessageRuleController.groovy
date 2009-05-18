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
import org.codehaus.groovy.grails.web.metaclass.RenderDynamicMethod
import groovy.xml.MarkupBuilder
import auth.ChannelUserInformation
import org.jsecurity.SecurityUtils
import auth.Role;


class RsMessageRuleController {

    def index = {redirect(action: list, params: params)}

    // the delete, save and update actions only accept POST requests
    def allowedMethods = [save: 'POST', update: 'POST']

    def list = {
        def userId = auth.RsUser.get(username: session.username)?.id
        def myRules = message.RsMessageRule.searchEvery("userId:${userId}", [sort: "id", order: "asc"])
        def ruleGroups = [];
        ruleGroups.add(["id": "enabledRules", "name": "Enabled Rules", 'nodeType': 'group', "rules": []]);
        ruleGroups.add(["id": "disabledRules", "name": "Disabled Rules", 'nodeType': 'group', "rules": []]);
        myRules.each {rule ->

            def searchQuery = search.SearchQuery.get(id: rule.searchQueryId);
            def ruleProps = [:]
            ruleProps.id = rule.id;
            ruleProps.delay = rule.delay;
            ruleProps.clearAction = rule.clearAction;
            ruleProps.enabled = rule.enabled;
            ruleProps.name = searchQuery ? searchQuery.name : rule.searchQueryId;
            ruleProps.nodeType = 'rule';

            if (rule.enabled)
            {
                ruleGroups[0].rules.add(ruleProps);
            }
            else
            {
                ruleGroups[1].rules.add(ruleProps);
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
    def getRuleXml(rule)
    {

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
            def user = auth.RsUser.get(username: session.username)
            def destination = getDestination(user, params.destinationType)
            def isAdmin = SecurityUtils.subject.hasRole(Role.ADMINISTRATOR)
            if (!isAdmin && (destination == null ||destination == ""))
            {
                addError("default.couldnot.create", [RsMessageRule, "Your destination for ${params.destinationType} is not defined"])
                render(text: errorsToXml(this.errors), contentType: "text/xml")
                return;
            }

            rsMessageRule.update(ControllerUtils.getClassProperties(params, RsMessageRule));
            if (!rsMessageRule.hasErrors()) {
                render(text: ControllerUtils.convertSuccessToXml("RsMessageRule ${rsMessageRule.id} updated"), contentType: "text/xml")
            }
            else {
                render(text: errorsToXml(rsMessageRule.errors), contentType: "text/xml")
            }
        }
        else {
            addError("default.couldnot.create", [RsMessageRule, "RsMessageRule not found with id ${params.id}"])
            render(text: errorsToXml(this.errors), contentType: "text/xml")
        }
    }


    def save = {
        def user = auth.RsUser.get(username: session.username)
        def destination = getDestination(user, params.destinationType)
        def isAdmin = SecurityUtils.subject.hasRole(Role.ADMINISTRATOR)
        if (!isAdmin && (destination == null ||destination == ""))
        {
            addError("default.couldnot.create", [RsMessageRule, "Your destination for ${params.destinationType} is not defined"])
            render(text: errorsToXml(this.errors), contentType: "text/xml")
            return;
        }
        params.userId = user.id
        if (params.userId != null)
        {
            params.userId = String.valueOf(params.userId)
        }

        def rsMessageRule = RsMessageRule.add(ControllerUtils.getClassProperties(params, RsMessageRule))
        if (!rsMessageRule.hasErrors()) {

            render(text: ControllerUtils.convertSuccessToXml("RsMessageRule ${rsMessageRule.id} created"), contentType: "text/xml")

        }
        else {
            render(text: errorsToXml(searchQuery.errors), contentType: "text/xml")

        }
    }

    def getDestination(user, destinationType) {
        def userChannelInfoType = RsMessageRule.getDestinationConfig()[destinationType];
        if (userChannelInfoType) {
            return ChannelUserInformation.get(userId: user.id, type: userChannelInfoType)?.destination
        }
        return null;
    }
    def enableRule = {
        def rsMessageRule = RsMessageRule.get([id: params.id])
        if (rsMessageRule) {
            rsMessageRule.update(enabled: true);
            if (!rsMessageRule.hasErrors()) {
                render(text: ControllerUtils.convertSuccessToXml("RsMessageRule ${rsMessageRule.id} updated"), contentType: "text/xml")
            }
            else {
                render(text: errorsToXml(rsMessageRule.errors), contentType: "text/xml")
            }
        }
        else {
            addError("default.couldnot.create", [RsMessageRule, "RsMessageRule not found with id ${params.id}"])
            render(text: errorsToXml(this.errors), contentType: "text/xml")
        }
    }
    def disableRule = {
        def rsMessageRule = RsMessageRule.get([id: params.id])
        if (rsMessageRule) {
            rsMessageRule.update(enabled: false);
            if (!rsMessageRule.hasErrors()) {
                render(text: ControllerUtils.convertSuccessToXml("RsMessageRule ${rsMessageRule.id} updated"), contentType: "text/xml")
            }
            else {
                render(text: errorsToXml(rsMessageRule.errors), contentType: "text/xml")
            }
        }
        else {
            addError("default.couldnot.create", [RsMessageRule, "RsMessageRule not found with id ${params.id}"])
            render(text: errorsToXml(this.errors), contentType: "text/xml")
        }
    }
}