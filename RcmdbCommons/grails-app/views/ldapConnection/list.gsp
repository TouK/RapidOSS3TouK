
<%@ page import="connection.LdapConnection" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <title>LdapConnection List  </title>
    </head>
    <body>
        <div class="nav">
            <span class="menuButton"><a class="home" href="${createLinkTo(dir:'admin.gsp')}">Home</a></span>
            <span class="menuButton"><g:link class="create" action="create">New LdapConnection</g:link></span>
        </div>
        <div class="body">
            <h1>LdapConnection List</h1>
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <div class="list">
                <table>
                    <thead>
                        <tr>

                   	        <g:sortableColumn property="name" title="Name" />


                   	        <g:sortableColumn property="contextFactory" title="Context Factory" />

                   	        <g:sortableColumn property="url" title="Url" />

                   	        <g:sortableColumn property="username" title="Username" />

                        </tr>
                    </thead>
                    <tbody>
                    <g:each in="${ldapConnectionList}" status="i" var="ldapConnection">
                        <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">

                            <td><g:link action="show" id="${ldapConnection.id}">${ldapConnection.name?.encodeAsHTML()}</g:link></td>

                            <td>${ldapConnection.contextFactory?.encodeAsHTML()}</td>

                            <td>${ldapConnection.url?.encodeAsHTML()}</td>

                            <td>${ldapConnection.username?.encodeAsHTML()}</td>

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
