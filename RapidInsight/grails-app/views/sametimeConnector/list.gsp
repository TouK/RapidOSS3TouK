<%@ page import="connector.SametimeConnector" %>

<html>
<head>
<head>
    <meta name="layout" content="adminLayout"/>
</head>
<div class="nav">
    <span class="menuButton"><g:link class="create" action="create">New SametimeConnector</g:link></span>
</div>
<body class="body">
<h1>SametimeConnector List</h1>
<g:render template="/common/messages" model="[flash:flash]"></g:render>


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

</body>
</html>