<%@ page import="connector.JiraConnector;connection.JiraConnection;datasource.JiraDatasource" %><html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="adminLayout"/>
    <title>JiraConnector List</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><g:link class="create" action="create">New JiraConnector</g:link></span>
</div>
<div class="body">
    <h1>JiraConnector List</h1>
    <g:render template="/common/messages" model="[flash:flash]"></g:render>
    <div class="list">
    <table>
    <thead>
        <tr>
            <th>Name</th>
            <th>Username</th>
            <th>Reconnect Interval</th>
            <th></th>
            <th></th>
        </tr>
    </thead>
    <tbody>

        <g:each in="${JiraConnector.list()}" status="i" var="jiraConnector">
            <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                <%
                	def name = JiraConnector.getConnectionName(jiraConnector.name);
                    def jiraConnection = JiraConnection.get(name: name);
                    name = JiraConnector.getDatasourceName(jiraConnector.name);
                    def jiraDatasource = JiraDatasource.get(name: name);
                %>
                <td><g:link action="show" controller="jiraConnector" id="${jiraConnector.id}">${jiraConnector.name?.encodeAsHTML()}</g:link></td>
				<td>${jiraConnection?.username?.encodeAsHTML()}</td>
				<td>${jiraDatasource?.reconnectInterval?.encodeAsHTML()}</td>
                <td><g:link action="testConnection" controller="jiraConnector" id="${jiraConnector.id}" class="testConnection">Test Connection</g:link></td>
                <td><g:link action="edit" controller="jiraConnector" id="${jiraConnector.id}" class="edit">Edit</g:link></td>
            </tr>
        </g:each>
    </tbody>
</table>

    </div>
    <div class="paginateButtons">
        <g:paginate total="${JiraConnector.count()}"/>
    </div>
</div>
</body>
</html>
