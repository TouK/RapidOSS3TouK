<%@ page import="auth.Role; com.ifountain.rcmdb.mobile.MobileUtils; search.SearchQuery; message.RsMessageRule; auth.RsUser" %>
<%--
  Created by IntelliJ IDEA.
  User: Sezgin Kucukkaraaslan
  Date: Dec 9, 2009
  Time: 3:01:56 PM
--%>
<%
    def gspFolder = "simple"
    if (MobileUtils.isIphone(request)) {
        gspFolder = "iphone";
    }
    def ruleQuery = " ( users:${session.username.exactQuery} AND ruleType:self )"
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
%>
<ul class="list notifications" id="notifications">
    <g:render template="/common/messages" model="[flash:flash, beans:[]]"></g:render>
    <g:each var="ruleGroup" in="${ruleGroups}">
        <li class="group">${ruleGroup.name}</li>
        <g:each var="rule" in="${ruleGroup.rules}">
          <li><table><tr>
              <td width="100%"><rui:link url="mobile/${gspFolder}/messageRuleForm.gsp" params="${[ruleId:rule.id, mode:'edit']}">${rule.name}</rui:link></td>
              <g:if test="${rule.enabled}">
                <td width="0%"><g:link controller="rsMessageRule" action="disableRule" id="${rule.id}" params="${[targetURI:'/mobile/' + gspFolder + '/notifications.gsp', format:'html', mobile:'true']}">Disable</g:link></td>
              </g:if>
              <g:else>
                <td width="0%"><g:link controller="rsMessageRule" action="enableRule" id="${rule.id}" params="${[targetURI:'/mobile/' + gspFolder + '/notifications.gsp', format:'html', mobile:'true']}">Enable</g:link></td>
              </g:else>
          </tr></table></li>
        </g:each>
    </g:each>
</ul>

