
<%@ page import="auth.UserRoleRel" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <meta name="layout" content="main" />
        <title>Create UserRoleRel</title>         
    </head>
    <body>
        <div class="nav">
            <span class="menuButton"><a class="home" href="${createLinkTo(dir: 'rsUser/show/' + userRoleRel?.rsUser?.id)}">${userRoleRel?.rsUser?.username}</a></span>
            <span class="menuButton"><a class="logout" href="${createLinkTo(dir: 'auth/logout')}">Logout</a></span>
        </div>
        <div class="body">
            <h1>Assign Role</h1>
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <g:hasErrors bean="${userRoleRel}">
            <div class="errors">
                <g:renderErrors bean="${userRoleRel}" as="list" />
            </div>
            </g:hasErrors>
            <g:form action="save" method="post" >
                <div class="dialog">
                    <table>
                        <tbody>
                        	<%
		                        if (userId != null) {
		                    %>
		                    <input type="hidden" name="rsUser.id" value="${userId}"/>
		                    <%
		                        }
		                        
		                    %>
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="role">Role:</label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean:userRoleRel,field:'role','errors')}">
                                    <g:select optionKey="id" from="${auth.Role.list()}" name="role.id" value="${userRoleRel?.role?.id}" ></g:select>
                                </td>
                            </tr> 
                       
                        </tbody>
                    </table>
                </div>
                <div class="buttons">
                    <span class="button"><input class="save" type="submit" value="Assign" /></span>
                </div>
            </g:form>
        </div>
    </body>
</html>
