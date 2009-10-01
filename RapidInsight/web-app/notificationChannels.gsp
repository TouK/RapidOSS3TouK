<%@ page import="connector.EmailConnector; connector.JabberConnector; connector.SmsConnector; connector.AolConnector; connector.SametimeConnector" %>

<html>
<head>
    <meta name="layout" content="adminLayout"/>
</head>
<body>
<g:render template="/common/messages" model="[flash:flash, beans:[]]"></g:render>
<div class="body">
    <br>
    <div style="margin-top:0px;">
        <table>
            <tr>
              <td>
                <div>
                  <span style="color:#006DBA;font-size:14px;font-weight:bold;margin:0.8em 0pt 0.3em;">Email Connectors</span>
                  <span class="menuButton"><g:link class="create" controller="emailConnector" action="create">New Email Connector</g:link></span>
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
                                    <th>Smtp Host</th>
                                    <th>Smtp Port</th>
                                    <th></th>
                                    <th></th>
                                </tr>
                            </thead>
                            <tbody>
                                <g:each in="${EmailConnector.list([sort:'name'])}" status="i" var="emailConnector">
                                    <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                                        <g:set var="emailConnection" value="${emailConnector?.ds?.connection}"></g:set>
                                        <td><g:link action="show" controller="emailConnector" id="${emailConnector.id}">${emailConnector.name?.encodeAsHTML()}</g:link></td>
                                        <td>${emailConnection?.smtpHost?.encodeAsHTML()}</td>
                                        <td>${emailConnection?.smtpPort?.encodeAsHTML()}</td>
                                        <td><g:link action="testConnection" controller="emailConnector" id="${emailConnector.id}" class="testConnection">Test Connection</g:link></td>
                                        <td><g:link action="edit" controller="emailConnector" id="${emailConnector.id}" class="edit">Edit</g:link></td>
                                    </tr>
                                </g:each>
                            </tbody>
                        </table>
                    </div>
                </td>
            </tr>
        </table>
    </div>
    <div style="margin-top:20px;">
        <table>
            <tr>
              <td>
                <div>
                  <span style="color:#006DBA;font-size:14px;font-weight:bold;margin:0.8em 0pt 0.3em;">Jabber Connectors</span>
                  <span class="menuButton"><g:link class="create" controller="jabberConnector" action="create">New Jabber Connector</g:link></span>
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
                                    <th>Host</th>
                                    <th>Port</th>
                                    <th>Service Name</th>
                                    <th></th>
                                    <th></th>
                                </tr>
                            </thead>
                            <tbody>
                                <g:each in="${JabberConnector.list([sort:'name'])}" status="i" var="jabberConnector">
                                    <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                                        <g:set var="jabberConnection" value="${jabberConnector?.ds?.connection}"></g:set>
                                        <td><g:link action="show" controller="jabberConnector" id="${jabberConnector.id}">${jabberConnector.name?.encodeAsHTML()}</g:link></td>
                                        <td>${jabberConnection?.host?.encodeAsHTML()}</td>
                                        <td>${jabberConnection?.port?.encodeAsHTML()}</td>
                                        <td>${jabberConnection?.serviceName?.encodeAsHTML()}</td>
                                        <td><g:link action="testConnection" controller="jabberConnector" id="${jabberConnector.id}" class="testConnection">Test Connection</g:link></td>
                                        <td><g:link action="edit" controller="jabberConnector" id="${jabberConnector.id}" class="edit">Edit</g:link></td>
                                    </tr>
                                </g:each>
                            </tbody>
                        </table>
                    </div>
                </td>
            </tr>
        </table>
    </div>
    <div style="margin-top:20px;">
        <table>
            <tr>
              <td>
                <div>
                  <span style="color:#006DBA;font-size:14px;font-weight:bold;margin:0.8em 0pt 0.3em;">Aol Connectors</span>
                  <span class="menuButton"><g:link class="create" controller="aolConnector" action="create">New Aol Connector</g:link></span>
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
                                    <th>Host</th>
                                    <th>Port</th>
                                    <th></th>
                                    <th></th>
                                </tr>
                            </thead>
                            <tbody>
                                <g:each in="${AolConnector.list([sort:'name'])}" status="i" var="aolConnector">
                                    <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                                        <g:set var="aolConnection" value="${aolConnector?.ds?.connection}"></g:set>
                                        <td><g:link action="show" controller="aolConnector" id="${aolConnector.id}">${aolConnector.name?.encodeAsHTML()}</g:link></td>
                                        <td>${aolConnection?.host?.encodeAsHTML()}</td>
                                        <td>${aolConnection?.port?.encodeAsHTML()}</td>
                                        <td><g:link action="testConnection" controller="aolConnector" id="${aolConnector.id}" class="testConnection">Test Connection</g:link></td>
                                        <td><g:link action="edit" controller="aolConnector" id="${aolConnector.id}" class="edit">Edit</g:link></td>
                                    </tr>
                                </g:each>
                            </tbody>
                        </table>
                    </div>
                </td>
            </tr>
        </table>
    </div>
    <div style="margin-top:20px;">
        <table>
            <tr>
              <td>
                <div>
                  <span style="color:#006DBA;font-size:14px;font-weight:bold;margin:0.8em 0pt 0.3em;">Sametime Connectors</span>
                  <span class="menuButton"><g:link class="create" controller="sametimeConnector" action="create">New Sametime Connector</g:link></span>
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
                                    <th>Host</th>
                                    <th>Community</th>
                                    <th></th>
                                    <th></th>
                                </tr>
                            </thead>
                            <tbody>
                                <g:each in="${SametimeConnector.list([sort:'name'])}" status="i" var="sametimeConnector">
                                    <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                                        <g:set var="sametimeConnection" value="${sametimeConnector?.ds?.connection}"></g:set>
                                        <td><g:link action="show" controller="sametimeConnector" id="${sametimeConnector.id}">${sametimeConnector.name?.encodeAsHTML()}</g:link></td>
                                        <td>${sametimeConnection?.host?.encodeAsHTML()}</td>
                                        <td>${sametimeConnection?.community?.encodeAsHTML()}</td>
                                        <td><g:link action="testConnection" controller="sametimeConnector" id="${sametimeConnector.id}" class="testConnection">Test Connection</g:link></td>
                                        <td><g:link action="edit" controller="sametimeConnector" id="${sametimeConnector.id}" class="edit">Edit</g:link></td>
                                    </tr>
                                </g:each>
                            </tbody>
                        </table>
                    </div>
                </td>
            </tr>
        </table>
    </div>
    <div style="margin-top:20px;">
        <table>
            <tr>
              <td>
                <div>
                  <span style="color:#006DBA;font-size:14px;font-weight:bold;margin:0.8em 0pt 0.3em;">Sms Connectors</span>
                  <span class="menuButton"><g:link class="create" controller="smsConnector" action="create">New Sms Connector</g:link></span>
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
                                    <th>Host</th>
                                    <th>Port</th>
                                    <th></th>
                                    <th></th>
                                </tr>
                            </thead>
                            <tbody>
                                <g:each in="${SmsConnector.list([sort:'name'])}" status="i" var="smsConnector">
                                    <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                                        <g:set var="smsConnection" value="${smsConnector?.ds?.connection}"></g:set>
                                        <td><g:link action="show" controller="smsConnector" id="${smsConnector.id}">${smsConnector.name?.encodeAsHTML()}</g:link></td>
                                        <td>${smsConnection?.host?.encodeAsHTML()}</td>
                                        <td>${smsConnection?.port?.encodeAsHTML()}</td>
                                        <td><g:link action="testConnection" controller="smsConnector" id="${smsConnector.id}" class="testConnection">Test Connection</g:link></td>
                                        <td><g:link action="edit" controller="smsConnector" id="${smsConnector.id}" class="edit">Edit</g:link></td>
                                    </tr>
                                </g:each>
                            </tbody>
                        </table>
                    </div>
                </td>
            </tr>
        </table>
    </div>
</div>

</body>
</html>