
<%@ page import="datasource.TcpListeningDatasource" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <meta name="layout" content="adminLayout" />
        <title>TcpListeningDatasource List</title>
    </head>
    <body>
        <div class="nav">
            <span class="menuButton"><a class="home" href="${createLinkTo(dir:'admin.gsp')}">Home</a></span>
            <span class="menuButton"><g:link class="create" action="create">New TcpListeningDatasource</g:link></span>
        </div>
        <div class="body">
            <h1>TcpListeningDatasource List</h1>
            <g:render template="/common/messages" model="[flash:flash]"></g:render>
            <div class="list">
                <table>
                    <thead>
                        <tr>
                        
                   	       
                        
                   	        <g:sortableColumn property="name" title="Name" />
                        
                   	        <th>Connection</th>
                   	    
                        </tr>
                    </thead>
                    <tbody>
                    <g:each in="${tcpListeningDatasourceList}" status="i" var="tcpListeningDatasource">
                        <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                        
                            <td><g:link action="show" id="${tcpListeningDatasource.id}">${tcpListeningDatasource.name?.encodeAsHTML()}</g:link></td>
                        

                            <td>${tcpListeningDatasource.connection?.encodeAsHTML()}</td>
                        
                        </tr>
                    </g:each>
                    </tbody>
                </table>
            </div>
            <div class="paginateButtons">
                <g:paginate total="${TcpListeningDatasource.count()}" />
            </div>
        </div>
    </body>
</html>
