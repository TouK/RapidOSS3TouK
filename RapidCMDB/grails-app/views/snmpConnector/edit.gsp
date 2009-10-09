<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="adminLayout" />
    <title>Edit SnmpConnector</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><g:link class="list" action="list">SnmpConnector List</g:link></span>
    <span class="menuButton"><g:link class="create" action="create">New SnmpConnector</g:link></span>
</div>
<div class="body">
    <h1>Edit SnmpConnector</h1>
    <g:render template="/common/messages" model="[flash:flash, beans:[snmpConnector,snmpConnection,script,datasource]]"></g:render>    
    <g:form method="post" >
        <input type="hidden" name="id" value="${snmpConnector?.id}"/>
        <div class="dialog">
            <table>
                <tbody>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="name">Name:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:snmpConnector,field:'name','errors')}">
                            <input type="text" id="name" name="name" value="${fieldValue(bean:snmpConnector,field:'name')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="host">Host:</label>
                        </td>
                        <td valign="top" class="value">
                            <input type="text" id="host" name="host" value="${snmpConnection?.host}"/>
                        </td>
                    </tr>

                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="port">Port:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:snmpConnection,field:'port','errors')}">
                             <input type="text" id="port" name="port" value="${snmpConnection?.port}"/>
                        </td>
                    </tr>
                 
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="scriptFile">Script File:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean: script, field: 'scriptFile', 'errors')}">
                            <input type="text" class="inputtextfield" id="scriptFile" name="scriptFile" value="${fieldValue(bean: script, field: 'scriptFile')}"/>
                        </td>
                    </tr>
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="logLevel">Log Level:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:script,field:'logLevel','errors')}">
                            <g:select id="logLevel" name="logLevel" from="${script.constraints.logLevel.inList.collect{it.encodeAsHTML()}}" value="${fieldValue(bean:script,field:'logLevel')}" ></g:select>
                        </td>
                    </tr>
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="logFileOwn">Use Own Log File:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean: script, field: 'logFileOwn', 'errors')}">
                            <g:checkBox name="logFileOwn" value="${script?.logFileOwn}"></g:checkBox>
                        </td>
                    </tr>
                     <tr class="prop">
                        <td valign="top" class="name">
                            <label for="staticParam">Static Parameter:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean: script, field: 'staticParam', 'errors')}">
                            <input type="text" id="staticParam" name="staticParam" value="${fieldValue(bean: script, field: 'staticParam')}"/>
                        </td>
                    </tr>
                    
                </tbody>
            </table>
        </div>
        <div class="buttons">
            <span class="button"><g:actionSubmit class="save" value="Update"/></span>
            <span class="button"><g:actionSubmit class="delete" onclick="return confirm('Are you sure?');" value="Delete"/></span>
            <%
                def isSubscribed = snmpConnector.script.listeningDatasource.isFree();
                if (isSubscribed) {
            %>
                <span class="button"><g:link action="startConnector" controller="snmpConnector" id="${snmpConnector.id}" class="start">Start</g:link></span>
            <%
                }
                else {
            %>
                <span class="button"><g:link action="stopConnector" controller="snmpConnector" id="${snmpConnector.id}" class="stop">Stop</g:link></span>
            <%
                }
            %>
            <span class="button"><g:link action="reload" controller="script" id="${snmpConnector?.script?.name}" class="refresh" params="${[targetURI:'/snmpConnector/edit/' + snmpConnector.id]}">Reload Script</g:link></span>
        </div>
    </g:form>
</div>
</body>
</html>