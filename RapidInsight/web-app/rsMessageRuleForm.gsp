<%@ page import="auth.Group; message.RsMessageRule" %>
<%@ page import="auth.RsUser" %>
<%@ page import="search.SearchQueryGroup" %>
<%@ page import="search.SearchQuery" %>

<%
    def ruleType = params.ruleType;
    def calendars = RsMessageRule.getCalendars(session.username);
    def mode = params.mode;
    def rsMessageRule = new RsMessageRule();
    def actionUrl = "rsMessageRule/save"
    def availableUsers;
    def ruleUsers;
    def availableGroups;
    def ruleGroups;
    if (mode == 'edit')
    {
        actionUrl = "rsMessageRule/update"
        rsMessageRule = RsMessageRule.get(id: params.ruleId);
        if (rsMessageRule == null)
        {
            println "Message Rule with id ${params.ruleId} does not exist";
            return;
        }
        ruleType = rsMessageRule.ruleType;
        ruleUsers = Arrays.asList(rsMessageRule.users.split(','));
        def usersMap = [:]
        ruleUsers.each {
            if (it.trim() != "") {
                usersMap.put(it, [username: it]);
            }
        }
        availableUsers = RsUser.getPropertyValues("alias:*", ["username"], [sort: "username"]).findAll {!usersMap.containsKey(it.username)}
        ruleUsers = usersMap.values();

        ruleGroups = Arrays.asList(rsMessageRule.groups.split(','));
        def groupsMap = [:]
        ruleGroups.each {
            if (it.trim() != "") {
                groupsMap.put(it, [name: it]);
            }
        }
        availableGroups = Group.getPropertyValues("alias:*", ["name"], [sort: "name"]).findAll {!groupsMap.containsKey(it.name)}
        ruleGroups = groupsMap.values();
    }
    else {
        availableUsers = RsUser.getPropertyValues("alias:*", ["username"], [sort: "username"])
        availableGroups = Group.getPropertyValues("alias:*", ["name"], [sort: "name"])
        ruleUsers = [];
        ruleGroups = [];
    }
    def destinationNames = ruleType == 'system' ? RsMessageRule.getNonChannelDestinationNames() : RsMessageRule.getChannelDestinationNames()
%>
<script type="text/javascript">
window.refreshDataComponent = function(){
var dataComponent = YAHOO.rapidjs.Components['ruleTree'];
dataComponent.poll();
}
</script>
<div>
    <div>
        <rui:formRemote method="POST" action="${actionUrl}" componentId="${params.componentId}" onSuccess="window.refreshDataComponent">
            <table>
                <tr class="prop">
                    <td valign="top" class="name"><label>Search Query:</label></td>
                    <td valign="top">
                        <%
                            def username = session.username;
                            def filterType = "event";
                            def queryGroups = SearchQueryGroup.searchEvery("( type:${filterType.exactQuery()} OR type:${"default".exactQuery()} ) AND  ( ( username:${RsUser.RSADMIN.exactQuery()} AND isPublic:true) OR (username:${username.exactQuery()}) )");
                        %>
                        <select name="searchQueryId" class="inputtextfield1">
                            <g:each in="${queryGroups}" var="group">
                                <optgroup label="${group.name}">
                                    <g:each in="${group.queries}" var="query">
                                        <g:if test="${query.type==filterType && (query.username == username || query.isPublic)}">
                                            <g:if test="${rsMessageRule.searchQueryId==query.id}">
                                                <option value="${query.id}" selected="selected">${query.name}</option>
                                            </g:if>
                                            <g:else>
                                                <option value="${query.id}">${query.name}</option>
                                            </g:else>
                                        </g:if>
                                    </g:each>
                                </optgroup>
                            </g:each>
                        </select>
                    </td>
                </tr>
                <tr class="prop">
                    <td valign="top" class="name"><label>Destination Type:</label></td>
                    <td valign="top">
                        <select name="destinationType" class="inputtextfield1">
                            <g:if test="${ruleType != 'system'}">
                                <g:if test="${rsMessageRule.destinationType == RsMessageRule.DEFAULT_DESTINATION}">
                                    <option value="${RsMessageRule.DEFAULT_DESTINATION}" selected="selected">Default</option>
                                </g:if>
                                <g:else>
                                    <option value="${RsMessageRule.DEFAULT_DESTINATION}">Default</option>
                                </g:else>
                            </g:if>
                            <g:each in="${destinationNames}" var="destinationName">
                                <g:if test="${rsMessageRule.destinationType==destinationName}">
                                    <option value="${destinationName}" selected="selected">${destinationName}</option>
                                </g:if>
                                <g:else>
                                    <option value="${destinationName}">${destinationName}</option>
                                </g:else>
                            </g:each>
                        </select>
                    </td>
                </tr>
                <tr class="prop">
                    <td valign="top" class="name"><label>Calendar:</label></td>
                    <td valign="top">
                        <select name="calendarId">
                            <option value="0" selected="selected">Any time</option>
                            <g:each in="${calendars}" var="calendar">
                                <g:if test="${rsMessageRule.calendarId==calendar.id}">
                                    <option value="${calendar.id}" selected="selected">${calendar.name}</option>
                                </g:if>
                                <g:else>
                                    <option value="${calendar.id}">${calendar.name}</option>
                                </g:else>
                            </g:each>
                        </select>
                    </td>
                </tr>
                <tr class="prop">
                    <td valign="top" class="name"><label>Delay:</label></td>
                    <td valign="top"><input type="text" class="inputtextfield" name="delay" value="${fieldValue(bean: rsMessageRule, field: 'delay')}"/> seconds</td>
                </tr>
                <tr class="prop">
                    <td valign="top" class="name"><label>Notify about Clear Events:</label></td>
                    <td valign="top"><g:checkBox name="sendClearEventType" value="${rsMessageRule?.sendClearEventType}"></g:checkBox></td>
                </tr>
                <tr class="prop">
                    <td valign="top" class="name"><label>Enabled:</label></td>
                    <td valign="top"><g:checkBox name="enabled" value="${rsMessageRule?.enabled}"></g:checkBox></td>
                </tr>
            </table>
            <g:if test="${ruleType == 'public'}">
                <table cellspacing="10">
                    <tr><th style="text-align:left;font-size:13px;color:#083772">Groups</th><th style="text-align:left;font-size:13px;color:#083772">Users</th></tr>
                    <tr>
                        <td>
                            <g:render template="/common/listToList" model="[id:'groups', inputName:'groups', valueProperty:'name', nameProperty:'name', fromListTitle:'Available Groups', toListTitle:'Rule Groups', fromListContent:availableGroups, toListContent:ruleGroups, width:100]"></g:render>
                        </td>
                        <td>
                            <g:render template="/common/listToList" model="[id:'users', inputName:'users', valueProperty:'username', nameProperty:'username', fromListTitle:'Available Users', toListTitle:'Rule Users', fromListContent:availableUsers, toListContent:ruleUsers, width:100]"></g:render>
                        </td>
                    </tr>
                </table>
            </g:if>
            <g:if test="${mode=='edit' && params.ruleId}">
                <input type="hidden" name="id" value="${params.ruleId}"/>
            </g:if>
            <input type="hidden" name="format" value="xml"/>
            <input type="hidden" name="ruleType" value="${ruleType}"/>
        </rui:formRemote>
    </div>
</div>