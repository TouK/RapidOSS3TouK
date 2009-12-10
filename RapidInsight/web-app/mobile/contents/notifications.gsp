<%@ page import="com.ifountain.rcmdb.mobile.MobileUtils; search.SearchQuery; message.RsMessageRule; auth.RsUser" %>
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
    def userId = RsUser.get(username: session.username)?.id
    def myRules = RsMessageRule.getPropertyValues("userId:${userId}", ["searchQueryId", "enabled"], [sort: "id", order: "asc"])
    def enabledRules = [];
    def disabledRules = [];
    myRules.each {rule ->
        def searchQuery = SearchQuery.get(id: rule.searchQueryId)
        rule.name = searchQuery ? searchQuery.name : rule.searchQueryId;
        if (rule.enabled) {
            enabledRules.add(rule);
        }
        else {
            disabledRules.add(rule);
        }
    }
%>
<ul class="list notifications" id="notifications">
    <g:render template="/common/messages" model="[flash:flash, beans:[]]"></g:render>
    <li class="group">Enabled Rules</li>
    <g:each var="rule" in="${enabledRules}">
        <li><table><tr>
            <td width="100%"><rui:link url="mobile/${gspFolder}/messageRuleForm.gsp" params="${[ruleId:rule.id, mode:'edit']}">${rule.name}</rui:link></td>
            <td width="0%"><g:link controller="rsMessageRule" action="disableRule" id="${rule.id}" params="${[targetURI:'/mobile/' + gspFolder + '/notifications.gsp', format:'html', mobile:'true']}">Disable</g:link></td>
        </tr></table></li>
    </g:each>
    <li class="group">Disabled Rules</li>
    <g:each var="rule" in="${disabledRules}">
        <li><table><tr>
            <td width="100%"><rui:link url="mobile/${gspFolder}/messageRuleForm.gsp" params="${[ruleId:rule.id, mode:'edit']}">${rule.name}</rui:link></td>
            <td width="0%"><g:link controller="rsMessageRule" action="enableRule" id="${rule.id}" params="${[targetURI:'/mobile/' + gspFolder + '/notifications.gsp', format:'html', mobile:'true']}">Enable</g:link></td>
        </tr></table></li>
    </g:each>
</ul>

