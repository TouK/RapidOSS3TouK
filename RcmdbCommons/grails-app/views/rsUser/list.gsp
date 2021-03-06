
<%@ page import="auth.RsUser" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <meta name="layout" content="adminLayout" />
        <title>User List</title>
    </head>
    <body>
        <div class="nav">
            <span class="menuButton"><g:link class="create" action="create">New User</g:link></span>
        </div>
        <div class="body">
            <h1>User List</h1>
            <g:render template="/common/messages" model="[flash:flash]"></g:render>

            <div class="list" style="margin-top:10px;margin-bottom:10px;">
            <form method="get">
            	Search : <input type="text" name="query" value="${params.query?params.query:''}" autocomplete="off" />
            	<g:each var="paramName" in="${params.keySet()}">
            		<g:if test="${paramName!='query' && paramName!='offset'}">
            			<input type="hidden" name="${paramName}" value="${params[paramName]}" />
            		</g:if>
            	</g:each>
            	<input type="submit" value="Search"/>
            </form>
            </div>

            <div class="list">
                <table>
                    <thead>
                        <tr>
                   	        <g:sortableColumn property="username" title="Username" params="${[query:params.query]}" />
                            <th></th>
                        </tr>
                    </thead>
                    <tbody>
                    <g:each in="${userList}" status="i" var="rsUser">
                        <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">

                            <td><g:link action="show" id="${rsUser.id}">${rsUser.username?.encodeAsHTML()}</g:link></td>
                            <td><g:link action="edit" id="${rsUser.id}" class="edit">Edit</g:link></td>
                        </tr>
                    </g:each>
                    </tbody>
                </table>
            </div>
            <div class="paginateButtons">
                <g:paginate total="${RsUser.countHits(searchQuery)}"  params="${[query:params.query]}" />
            </div>
        </div>
    </body>
</html>
