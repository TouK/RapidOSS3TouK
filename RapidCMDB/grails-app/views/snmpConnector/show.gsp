<html>
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
                    <td valign="top" class="name" id="nameLabel" >Name:</td>

                    <td valign="top" class="value" id="name" >${snmpConnector.name}</td>

                </tr>

                <tr class="prop">
                    <td valign="top" class="name" id="hostLabel"  >Host:</td>

                    <td valign="top" class="value" id="host" >${snmpConnector?.connection?.host}</td>

                </tr>

                <tr class="prop">
                    <td valign="top" class="name" id="portLabel"  >Port:</td>
                    <td valign="top" class="value" id="port" >${snmpConnector?.connection?.port}</td>
                </tr>
                <tr class="prop">
                    <td valign="top" class="name" id="scriptFileLabel" >Script File:</td>
                    <td valign="top" class="value" id="scriptFile" >${snmpConnector?.script?.scriptFile}</td>
                </tr>
                <tr class="prop">
                    <td valign="top" class="name" id="logLevelLabel"  >Log Level:</td>

                    <td valign="top" class="value" id="logLevel" >${snmpConnector?.script?.logLevel}</td>
                </tr>
                <tr class="prop">
                    <td valign="top" class="name" id="logFileOwnLabel" >Use Own Log File:</td>

                    <td valign="top" class="value" id="logFileOwn" >${snmpConnector?.script?.logFileOwn}</td>
                </tr>
                <tr class="prop">
                    <td valign="top" class="name" id="staticParamLabel" >Static Parameter:</td>

                    <td valign="top" class="value" id="staticParam" >${snmpConnector?.script?.staticParam}</td>
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

                if ((snmpConnector?.script.listeningDatasource.isFree())) {
            %>
            <span class="button"><g:actionSubmit class="run" value="Start" action="StartConnector"/></span>
            <%
                }
                else {
            %>
            <span class="button"><g:actionSubmit class="close" value="Stop" action="StopConnector"/></span>

            <%
                }
            %>
        </g:form>
        <g:form style="display:inline" controller="script">
            <input type="hidden" name="id" value="${snmpConnector?.script?.name}"/>
            <input type="hidden" name="targetURI" value="/snmpConnector/show/${snmpConnector.id}"/>
            <span class="button"><g:actionSubmit class="refresh" value="Reload Script" action="Reload"/></span>
        </g:form>
    </div>
</div>
</body>
</html>
