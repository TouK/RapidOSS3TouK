
<%@ page import="connection.RepositoryConnection" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <meta name="layout" content="adminLayout" />
        <title>RepositoryConnection List</title>
    </head>
    <body>
        <div class="nav">
            <span class="menuButton"><a class="home" href="${createLinkTo(dir:'admin.gsp')}">Home</a></span>
            <span class="menuButton"><g:link class="create" action="create">New RepositoryConnection</g:link></span>
        </div>
        <div class="body">
            <h1>RepositoryConnection List</h1>
            <g:render template="/common/messages" model="[flash:flash]"></g:render>
            <div class="list">
                <table>
                    <thead>
                        <tr>
                   	        <g:sortableColumn property="name" title="Name" />

                        </tr>
                    </thead>
                    <tbody>
                    <g:each in="${repositoryConnectionList}" status="i" var="repositoryConnection">
                        <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">

                            <td><g:link action="show" id="${repositoryConnection.id}">${repositoryConnection.name?.encodeAsHTML()}</g:link></td>

                        </tr>
                    </g:each>
                    </tbody>
                </table>
            </div>
            <div class="paginateButtons">
                <g:paginate total="${RepositoryConnection.count()}" />
            </div>
        </div>
    </body>
</html>
