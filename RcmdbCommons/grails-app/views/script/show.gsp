<%@ page import="script.CmdbScript" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="adminLayout" />
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
                    <td valign="top" class="name">Id:</td>

                    <td valign="top" class="value">${cmdbScript.id}</td>

                </tr>

                <tr class="prop">
                    <td valign="top" class="name">Name:</td>

                    <td valign="top" class="value">${cmdbScript.name}</td>

                </tr>

                <tr class="prop">
                    <td valign="top" class="name">Script File:</td>

                    <td valign="top" class="value">${cmdbScript.scriptFile}</td>

                </tr>
                <tr class="prop">
                    <td valign="top" class="name">Log Level:</td>

                    <td valign="top" class="value">${cmdbScript.logLevel}</td>
                </tr>
                <tr class="prop">
                    <td valign="top" class="name">Use Own Log File:</td>

                    <td valign="top" class="value">${cmdbScript.logFileOwn}</td>
                </tr>
                 <tr class="prop">
                    <td valign="top" class="name">Static Parameter:</td>

                    <td valign="top" class="value">${cmdbScript.staticParam}</td>

                </tr>
                <tr class="prop">
                    <td valign="top" class="name">Type:</td>

                    <td valign="top" class="value">${cmdbScript.type}</td>

                </tr>
                <%
                    if (cmdbScript.type == CmdbScript.SCHEDULED) {
                %>
                <tr class="prop">
                    <td valign="top" class="name">Schedule Type:</td>

                    <td valign="top" class="value">${cmdbScript.scheduleType}</td>

                </tr>

                <tr class="prop">
                    <td valign="top" class="name">Start Delay:</td>

                    <td valign="top" class="value">${cmdbScript.startDelay}</td>

                </tr>

                <%
                        if (cmdbScript.scheduleType == CmdbScript.PERIODIC) {

                %>
                <tr class="prop">
                    <td valign="top" class="name">Period:</td>

                    <td valign="top" class="value">${cmdbScript.period}</td>

                </tr>
                <%
                    }
                    else {
                %>
                <tr class="prop">
                    <td valign="top" class="name">Cron Expression:</td>

                    <td valign="top" class="value">${cmdbScript.cronExpression}</td>

                </tr>
                <%
                        }
                %>

                <tr class="prop">
                    <td valign="top" class="name">Enabled:</td>

                    <td valign="top" class="value">${cmdbScript.enabled}</td>

                </tr>

                <%
                    }
                    else if (cmdbScript.type == CmdbScript.LISTENING) {
                %>
                <tr class="prop">
                    <td valign="top" class="name">Datasource:</td>

                    <td valign="top" class="value">${cmdbScript.listeningDatasource}</td>

                </tr>               
                <tr class="prop">
                    <td valign="top" class="name">Log File:</td>

                    <td valign="top" class="value">${cmdbScript.logFile}</td>
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
                    if (cmdbScript.listeningDatasource.isStartable()) {
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
        </g:form>
    </div>
</div>
</body>
</html>
