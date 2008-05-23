
<%@ page import="auth.*" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <meta name="layout" content="main" />
        <title>Show User</title>
    </head>
    <body>
        <div class="nav">
            <span class="menuButton"><a class="home" href="${createLinkTo(dir:'')}">Home</a></span>
            <span class="menuButton"><g:link class="list" action="list">User List</g:link></span>
        </div>
        <div class="body">
            <h1>Show User</h1>
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
             <g:if test="${flash.errors}">
		        <div class="errors">
		            <ul>
		                <g:each var="error" in="${flash?.errors}">
		                    <li>${error}</li>
		                </g:each>
		            </ul>
		        </div>
		    </g:if>
            <div class="dialog">
                <table>
                    <tbody>
                        <tr class="prop">
                            <td valign="top" class="name">Username:</td>
                            <td valign="top" class="value">${rsUser.username}</td>
                        </tr>
                    </tbody>
                </table>
            </div>
            
            
            <div style="margin-top:20px;">
        	<span style="color:#006DBA;font-size:16px;font-weight:normal;margin:0.8em 0pt 0.3em;">Role List</span>
        	<span class="menuButton"><g:link controller="userRoleRel" params="['rsUser.id':rsUser?.id]" class="create" action="create">Assign Role</g:link></span>
            
	    <div class="list">
		<table>
		    <thead>
			<tr>
				<th>Role</th>
			</tr>
		    </thead>

		    <tbody>
		    <g:each in="${UserRoleRel.findAllByRsUser(rsUser)}" status="i" var="userRoleRel">
			<tr class="${(i % 2) == 0 ? 'odd' : 'even'}">

			    <td><g:link action="show" controller="userRoleRel" id="${userRoleRel.id}">${userRoleRel.role?.name.encodeAsHTML()}</g:link></td>

			</tr>
		    </g:each>
		    </tbody>
		</table>
	    </div>
	    </div>

            
            
                <div class="buttons" style="margin-top:30px;">
                <g:form>
                    <input type="hidden" name="id" value="${rsUser?.id}" />
                    <span class="button"><g:actionSubmit class="edit" value="Edit" /></span>
                    <span class="button"><g:actionSubmit class="delete" onclick="return confirm('Are you sure?');" value="Delete" /></span>
                </g:form>
            </div>
        </div>
    </body>
</html>
