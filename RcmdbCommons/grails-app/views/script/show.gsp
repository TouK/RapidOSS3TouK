<%@ page import="datasource.RepositoryDatasource; script.CmdbScript" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="adminLayout"/>
    <title>Show Script</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><g:link class="list" action="list">Script List</g:link></span>
    <span class="menuButton"><g:link class="create" action="create">New Script</g:link></span>
</div>
<div class="body">
    <h1>Show Script</h1>
    <g:render template="/common/messages" model="[flash:flash]"></g:render>
    <div class="dialog">
        <table>
            <tbody>

                <tr class="prop">
                    <td valign="top" class="name" id="idLabel">Id:</td>

                    <td valign="top" class="value" id="id">${cmdbScript.id}</td>

                </tr>

                <tr class="prop">
                    <td valign="top" class="name" id="nameLabel">Name:</td>

                    <td valign="top" class="value" id="name">${cmdbScript.name}</td>

                </tr>

                <tr class="prop">
                    <td valign="top" class="name" id="scriptFileLabel">Script File:</td>

                    <td valign="top" class="value" id="scriptFile">${cmdbScript.scriptFile}</td>

                </tr>
                <tr class="prop">
                    <td valign="top" class="name" id="logLevelLabel">Log Level:</td>

                    <td valign="top" class="value" id="logLevel">${cmdbScript.logLevel}</td>
                </tr>
                <tr class="prop">
                    <td valign="top" class="name" id="logFileOwnLabel">Use Own Log File:</td>

                    <td valign="top" class="value" id="logFileOwn">${cmdbScript.logFileOwn}</td>
                </tr>
                <tr class="prop">
                    <td valign="top" class="name" id="staticParamLabel">Static Parameter:</td>

                    <td valign="top" class="value" id="staticParam">${cmdbScript.staticParam}</td>

                </tr>
                <tr class="prop">
                    <td valign="top" class="name" id="typeLabel">Type:</td>

                    <td valign="top" class="value" id="type">${cmdbScript.type}</td>

                </tr>
                <%
                    if (cmdbScript.type == CmdbScript.SCHEDULED) {
                %>
                <tr class="prop">
                    <td valign="top" class="name" id="scheduleTypeLabel">Schedule Type:</td>

                    <td valign="top" class="value" id="scheduleType">${cmdbScript.scheduleType}</td>

                </tr>

                <tr class="prop">
                    <td valign="top" class="name" id="startDelayLabel">Start Delay:</td>

                    <td valign="top" class="value" id="startDelay">${cmdbScript.startDelay}</td>

                </tr>

                <%
                        if (cmdbScript.scheduleType == CmdbScript.PERIODIC) {

                %>
                <tr class="prop">
                    <td valign="top" class="name" id="periodLabel">Period:</td>

                    <td valign="top" class="value" id="period">${cmdbScript.period}</td>

                </tr>
                <%
                    }
                    else {
                %>
                <tr class="prop">
                    <td valign="top" class="name" id="cronExpressionLabel">Cron Expression:</td>

                    <td valign="top" class="value" id="cronExpression">${cmdbScript.cronExpression}</td>

                </tr>
                <%
                        }
                %>

                <tr class="prop">
                    <td valign="top" class="name" id="enabledLabel">Enabled:</td>

                    <td valign="top" class="value" id="enabled">${cmdbScript.enabled}</td>

                </tr>

                <%
                    }
                    else if (cmdbScript.type == CmdbScript.LISTENING) {
                        if (cmdbScript.listeningDatasource instanceof RepositoryDatasource) {
                %>
                <tr class="prop">
                    <td valign="top" class="name">Listen To RapidInsight Repository:</td>
                    <td valign="top" class="value">true</td>
                </tr>

                <%

                    }
                    else {
                %>
                <tr class="prop">
                    <td valign="top" class="name" id="listeningDatasourceLabel">Datasource:</td>
                    <td valign="top" class="value" id="listeningDatasource">${cmdbScript.listeningDatasource}</td>
                </tr>

                <%

                        }
                %>


                <tr class="prop">
                    <td valign="top" class="name" id="logFileLabel">Log File:</td>

                    <td valign="top" class="value" id="logFile">${cmdbScript.logFile}</td>
                </tr>
                <%
                        }
                %>

            </tbody>
        </table>
    </div>
    <div class="buttons">
        <g:form style="display:inline">
            <input type="hidden" name="id" value="${cmdbScript?.id}"/>
            <span class="button"><g:actionSubmit class="edit" value="Edit"/></span>
            <span class="button"><g:actionSubmit class="delete" onclick="return confirm('Are you sure?');" value="Delete"/></span>
        </g:form>
        <g:form style="display:inline">
            <input type="hidden" name="id" value="${cmdbScript?.name}"/>
            <span class="button"><g:actionSubmit class="refresh" value="Reload"/></span>
            <%
                if (cmdbScript.type == CmdbScript.LISTENING && cmdbScript.listeningDatasource) {
                    if (cmdbScript.listeningDatasource.isFree()) {
            %>
            <span class="button"><g:actionSubmit class="run" value="Start"/></span>
            <%
                }
                else {
            %>
            <span class="button"><g:actionSubmit class="close" value="Stop"/></span>
            <%
                    }
                }
                else {
            %>
            <span class="button"><g:actionSubmit class="run" value="Run"/></span>
            <%
                }
            %>
            <g:if test="${cmdbScript.type != CmdbScript.LISTENING}">
                <span class="button"><g:actionSubmit class="close" action="stopRunningScripts" value="Mark For Stop"/></span>
            </g:if>
        </g:form>
    </div>
</div>
</body>
</html>
