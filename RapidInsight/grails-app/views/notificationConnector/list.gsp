<%@ page import="connector.NotificationConnector;"%>
<html>
<head>
    <meta name="layout" content="adminLayout"/>
</head>
<body>
<g:render template="/common/messages" model="[flash:flash, beans:[]]"></g:render>
<div class="body">

    <div style="margin-top:20px;">
            <table>
                <tr>
                  <td>
                    <div>
                      <span style="color:#006DBA;font-size:14px;font-weight:bold;margin:0.8em 0pt 0.3em;">Message Generator Script</span>                      
                    </div>
                  </td>
                </tr>
                <tr>
                    <td>
                         <div class="list">
                            <table>
                                <thead>
                                    <tr>
                                        <th>Name</th>
                                        <th>Log Level</th>
                                        <th></th>
                                        <th></th>
                                    </tr>
                                </thead>
                                <tbody>
                                        <tr class="odd">
                                            <%
                                                def messageGeneratorScript = script.CmdbScript.get(name: "messageGenerator");
                                            %>
                                            <g:if test="${messageGeneratorScript}">
                                                <td><g:link action="show" controller="script" id="${messageGeneratorScript.id}">${messageGeneratorScript.name?.encodeAsHTML()}</g:link></td>

                                                <td>
                                                    <g:form controller="script">
                                                        <g:select name="logLevel" from="${messageGeneratorScript.constraints.logLevel.inList.collect{it.encodeAsHTML()}}" value="${fieldValue(bean:messageGeneratorScript,field:'logLevel')}"></g:select>
                                                        <g:actionSubmit value="Update Log Level" action="updateLogLevel"/>
                                                        <input type="hidden" name="id" value="${messageGeneratorScript?.name}"/>
                                                        <input type="hidden" name="targetURI" value="/notificationConnector/list"/>
                                                    </g:form>
                                                </td>
                                                <td>
                                                    <g:if test="${messageGeneratorScript?.enabled}">
                                                        <g:link action="disable" controller="script" id="${messageGeneratorScript.name}" class="stop" params="[targetURI:'/notificationConnector/list']">Stop</g:link>
                                                    </g:if>
                                                    <g:else>
                                                        <g:link action="enable" controller="script" id="${messageGeneratorScript.name}" class="start" params="[targetURI:'/notificationConnector/list']">Start</g:link>
                                                    </g:else>
                                                </td>
                                                <td><g:link action="edit" controller="script" id="${messageGeneratorScript.id}" class="edit">Edit</g:link></td>
                                            </g:if>
                                            <g:else>
                                                <td colspan="4"><b>! messageGenerator script does not exist. Please add it as a scheduled script.</b></td>
                                            </g:else>
                                        </tr>
                                </tbody>
                            </table>
                        </div>
                    </td>
                </tr>
            </table>
        </div>

    <%
        def connectorTypes=[];
        
        def emailConnectorType=[type:"Email"];
        emailConnectorType.columns=[[name:"smtpHost",label:"Smtp Host"],[name:"smtpPort",label:"Smtp Port"]];

        def jabberConnectorType=[type:"Jabber"];
        jabberConnectorType.columns=[[name:"host",label:"Host"],[name:"port",label:"Port"],[name:"serviceName",label:"Service Name"]];

        def aolConnectorType=[type:"Aol"];
        aolConnectorType.columns=[[name:"host",label:"Host"],[name:"port",label:"Port"]];

        def sametimeConnectorType=[type:"Sametime"];
        sametimeConnectorType.columns=[[name:"host",label:"Host"],[name:"community",label:"Community"]];

        def smsConnectorType=[type:"Sms"];
        smsConnectorType.columns=[[name:"host",label:"Host"],[name:"port",label:"Port"]];

        connectorTypes.add(emailConnectorType);
        connectorTypes.add(jabberConnectorType);
        connectorTypes.add(aolConnectorType);
        connectorTypes.add(sametimeConnectorType);
        connectorTypes.add(smsConnectorType);

        %>
    <g:each in="${connectorTypes}" var="connectorTypeData">
        <g:set var="type" value="${connectorTypeData.type}"></g:set>
        <%
            def type=connectorTypeData.type;
            def connectors=NotificationConnector.searchEvery("type:${type}",[sort:'name']);
        %>
        <div style="margin-top:20px;">
            <table>
                <tr>
                  <td>
                    <div>
                      <span style="color:#006DBA;font-size:14px;font-weight:bold;margin:0.8em 0pt 0.3em;">${type} Connectors</span>
                      <span class="menuButton"><g:link class="create" controller="notificationConnector" action="create" params='[type:"${type}"]'>New ${type} Connector</g:link></span>
                    </div>
                  </td>
                </tr>
                <tr>
                    <td>
                         <div class="list">
                            <table>
                                <thead>
                                    <tr>
                                        <th>Name</th>
                                        <g:each in="${connectorTypeData.columns}" var="column">
                                             <th>${column.label}</th>
                                        </g:each>
                                        <th>Log Level</th>
                                        <th></th>
                                        <th></th>
                                        <th></th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <g:each in="${connectors}" status="i" var="connector">
                                        <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                                            <g:set var="connection" value="${connector?.ds?.connection}"></g:set>
                                            <td><g:link action="show" controller="notificationConnector" id="${connector.id}">${connector.name?.encodeAsHTML()}</g:link></td>
                                            <g:each in="${connectorTypeData.columns}" var="column">
                                                 <td>${connection?.getProperty(column.name)}</td>
                                            </g:each>
                                            <%
                                                def connScript = script.CmdbScript.get(name: NotificationConnector.getScriptName(connector.name));
                                            %>
                                            <td>
                                                <g:form controller="script">
                                                    <g:select name="logLevel" from="${connScript.constraints.logLevel.inList.collect{it.encodeAsHTML()}}" value="${fieldValue(bean:connScript,field:'logLevel')}"></g:select>
                                                    <g:actionSubmit value="Update Log Level" action="updateLogLevel"/>
                                                    <input type="hidden" name="id" value="${connScript?.name}"/>
                                                    <input type="hidden" name="targetURI" value="/notificationConnector/list"/>
                                                </g:form>
                                            </td>
                                            <td>
                                                <g:if test="${connScript?.enabled}">
                                                    <g:link action="disable" controller="script" id="${connScript.name}" class="stop" params="[targetURI:'/notificationConnector/list']">Stop</g:link>
                                                </g:if>
                                                <g:else>
                                                    <g:link action="enable" controller="script" id="${connScript.name}" class="start" params="[targetURI:'/notificationConnector/list']">Start</g:link>
                                                </g:else>
                                            </td>
                                            <td><g:link action="testConnection" controller="notificationConnector" id="${connector.id}" class="testConnection">Test Connection</g:link></td>
                                            <td><g:link action="edit" controller="notificationConnector" id="${connector.id}" class="edit">Edit</g:link></td>
                                        </tr>
                                    </g:each>
                                </tbody>
                            </table>
                        </div>
                    </td>
                </tr>
            </table>
        </div>
    </g:each>

</div>

</body>
</html>