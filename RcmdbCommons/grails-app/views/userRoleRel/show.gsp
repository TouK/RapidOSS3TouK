
<%@ page import="auth.UserRoleRel" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <title>Show UserRoleRel</title>
    </head>
    <body>
        <div class="nav">
            <span class="menuButton"><a class="home" href="${createLinkTo(dir: 'rsUser/show/' + userRoleRel?.rsUser?.id)}">${userRoleRel?.rsUser?.username}</a></span>
        </div>
        <div class="body">
            <h1>Role</h1>
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <div class="dialog">
                <table>
                    <tbody>
                  
                        <tr class="prop">
                            <td valign="top" class="name">Role:</td>
                            
                            <td valign="top" class="value">${userRoleRel?.role?.name}</td>
                            
                        </tr>
                    </tbody>
                </table>
            </div>
            <div class="buttons">
                <g:form action="delete" method="post">
                    <input type="hidden" name="id" value="${userRoleRel?.id}" />
                    <span class="button"><g:actionSubmit class="delete" onclick="return confirm('Are you sure?');" value="Unassign" /></span>
                </g:form>
            </div>
        </div>
    </body>
</html>
