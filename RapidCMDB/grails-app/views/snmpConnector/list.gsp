<%@ page import="connector.SnmpConnector" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="adminLayout"/>
    <title>SnmpConnector List</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><g:link class="create" action="create">New SnmpConnector</g:link></span>
</div>
<div class="body">
    <h1>SnmpConnector List</h1>
    <g:render template="/common/messages" model="[flash:flash, beans:[]]"></g:render>
    <div class="list">
        <table>
            <thead>
                <tr>

                    <g:sortableColumn property="name" title="Name"/>
                    <th>Host</th>
                    <th>Port</th>                    
                    <th>Script File</th>
                    <th>Log Level</th>
                    <th></th>
                    <th></th>
                </tr>
            </thead>
            <tbody>
                <g:each in="${snmpConnectorList}" status="i" var="snmpConnector">
                    <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">

                        <td><g:link action="show" id="${snmpConnector.id}">${snmpConnector.name?.encodeAsHTML()}</g:link></td>

                        <td>${snmpConnector.connection?.host?.encodeAsHTML()}</td>

                        <td>${snmpConnector.connection?.port?.encodeAsHTML()}</td>                        
                        <td>${snmpConnector.script?.scriptFile?.encodeAsHTML()}</td>
                        <td>
                             <g:form>
                                 <g:select name="logLevel" from="${snmpConnector.script.constraints.logLevel.inList.collect{it.encodeAsHTML()}}" value="${fieldValue(bean:snmpConnector.script,field:'logLevel')}"></g:select>
                                 <g:actionSubmit value="Update Log Level" action="updateLogLevel"/>
                                 <input type="hidden" name="id" value="${snmpConnector?.id}"/>
                             </g:form>
                         </td>
                        <%
                            def isSubscribed = snmpConnector.script.listeningDatasource.isFree();
                            if (isSubscribed) {
                        %>
                           <td><g:link action="startConnector" controller="snmpConnector" id="${snmpConnector.id}" class="start">Start</g:link></td>
                        <%
                            }
                            else {
                        %>
                            <td><g:link action="stopConnector" controller="snmpConnector" id="${snmpConnector.id}" class="stop">Stop</g:link></td>

                        <%
                            }
                        %>
                         <td><g:link action="edit" id="${snmpConnector.id}" class="Edit">Edit</g:link></td>

                    </tr>
                </g:each>
            </tbody>
        </table>
    </div>
    <div class="paginateButtons">
        <g:paginate total="${SnmpConnector.count()}"/>
    </div>
</div>
</body>
</html>
