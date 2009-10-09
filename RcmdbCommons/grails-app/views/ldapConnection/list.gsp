
<%@ page import="connection.LdapConnection" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <meta name="layout" content="adminLayout" />
        <title>LdapConnection List </title>
    </head>
    <body>
        <div class="nav">
            <span class="menuButton"><g:link class="create" action="create">New LdapConnection</g:link></span>
        </div>
        <div class="body">
            <h1>LdapConnection List</h1>
            <g:render template="/common/messages" model="[flash:flash]"></g:render>
            <div class="list">
                <table>
                    <thead>
                        <tr>

                   	        <g:sortableColumn property="name" title="Name" />


                   	        <g:sortableColumn property="contextFactory" title="Context Factory" />

                   	        <g:sortableColumn property="url" title="Url" />

                   	        <g:sortableColumn property="username" title="Username" />
                            <th></th>
                            <th></th>

                        </tr>
                    </thead>
                    <tbody>
                    <g:each in="${ldapConnectionList}" status="i" var="ldapConnection">
                        <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">

                            <td><g:link action="show" id="${ldapConnection.id}">${ldapConnection.name?.encodeAsHTML()}</g:link></td>

                            <td>${ldapConnection.contextFactory?.encodeAsHTML()}</td>

                            <td>${ldapConnection.url?.encodeAsHTML()}</td>

                            <td>${ldapConnection.username?.encodeAsHTML()}</td>
                            <td><g:link action="test" controller="ldapConnection" id="${ldapConnection.id}" class="testConnection">Test Connection</g:link></td>
                            <td><g:link action="edit" controller="ldapConnection" id="${ldapConnection.id}" class="edit">Edit</g:link></td>

                        </tr>
                    </g:each>
                    </tbody>
                </table>
            </div>
            <div class="paginateButtons">
                <g:paginate total="${LdapConnection.count()}" />
            </div>
        </div>
    </body>
</html>
