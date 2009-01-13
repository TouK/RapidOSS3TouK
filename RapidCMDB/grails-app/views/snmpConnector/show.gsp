<%@ page import="com.ifountain.rcmdb.datasource.ListeningAdapterManager" %><html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="adminLayout"/>
    <title>Show SnmpConnector</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><g:link class="list" action="list">SnmpConnector List</g:link></span>
    <span class="menuButton"><g:link class="create" action="create">New SnmpConnector</g:link></span>
</div>
<div class="body">
    <h1>Show SnmpConnector</h1>
    <g:if test="${flash.message}">
        <div class="message">${flash.message}</div>
    </g:if>
    <g:hasErrors bean="${flash.errors}">
        <div class="errors">
            <g:renderErrors bean="${flash.errors}"/>
        </div>
    </g:hasErrors>
    <div class="dialog">
        <table>
            <tbody>

                <tr class="prop">
                    <td valign="top" class="name">Name:</td>

                    <td valign="top" class="value">${snmpConnector.name}</td>

                </tr>

                <tr class="prop">
                    <td valign="top" class="name">Host:</td>

                    <td valign="top" class="value">${snmpConnector?.connection?.host}</td>

                </tr>

                <tr class="prop">
                    <td valign="top" class="name">Port:</td>
                    <td valign="top" class="value">${snmpConnector?.connection?.port}</td>
                </tr>
                <tr class="prop">
                    <td valign="top" class="name">Script File:</td>
                    <td valign="top" class="value">${snmpConnector?.script?.scriptFile}</td>
                </tr>
                <tr class="prop">
                    <td valign="top" class="name">Log Level:</td>

                    <td valign="top" class="value">${snmpConnector?.script?.logLevel}</td>
                </tr>
                <tr class="prop">
                    <td valign="top" class="name">Use Own Log File:</td>

                    <td valign="top" class="value">${snmpConnector?.script?.logFileOwn}</td>
                </tr>
                <tr class="prop">
                    <td valign="top" class="name">Static Parameter:</td>

                    <td valign="top" class="value">${snmpConnector?.script?.staticParam}</td>
                </tr>
            </tbody>
        </table>
    </div>
    <div class="buttons">
        <g:form style="display:inline">
            <input type="hidden" name="id" value="${snmpConnector?.id}"/>
            <span class="button"><g:actionSubmit class="edit" value="Edit"/></span>
            <span class="button"><g:actionSubmit class="delete" onclick="return confirm('Are you sure?');" value="Delete"/></span>
             <%

                if (ListeningAdapterManager.getInstance().isSubscribed(snmpConnector?.script.listeningDatasource)) {
            %>
            <span class="button"><g:actionSubmit class="close" value="Stop" action="StopConnector"/></span>
            <%
                }
                else {
            %>
            <span class="button"><g:actionSubmit class="run" value="Start" action="StartConnector"/></span>
            <%
                }
            %>
        </g:form>
        <g:form style="display:inline" controller="script">
            <input type="hidden" name="id" value="${snmpConnector?.script?.name}"/>
            <input type="hidden" name="targetURI" value="/snmpConnector/show/${snmpConnector.id}"/>
            <span class="button"><g:actionSubmit class="refresh" value="Reload Script"/></span>
        </g:form>
    </div>
</div>
</body>
</html>
