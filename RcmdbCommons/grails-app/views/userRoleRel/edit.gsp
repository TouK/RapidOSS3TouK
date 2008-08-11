
<%@ page import="auth.UserRoleRel" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <meta name="layout" content="main" />
        <title>Edit UserRoleRel</title>
    </head>
    <body>
        <div class="nav">
            <span class="menuButton"><a class="home" href="${createLinkTo(dir:'admin.gsp')}">Home</a></span>
            <span class="menuButton"><g:link class="list" action="list">UserRoleRel List</g:link></span>
            <span class="menuButton"><g:link class="create" action="create">New UserRoleRel</g:link></span>
        </div>
        <div class="body">
            <h1>Edit UserRoleRel</h1>
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <g:hasErrors bean="${userRoleRel}">
            <div class="errors">
                <g:renderErrors bean="${userRoleRel}" as="list" />
            </div>
            </g:hasErrors>
            <g:form method="post" >
                <input type="hidden" name="id" value="${userRoleRel?.id}" />
                <div class="dialog">
                    <table>
                        <tbody>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="role">Role:</label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean:userRoleRel,field:'role','errors')}">
                                    <g:select optionKey="id" from="${auth.Role.list()}" name="role.id" value="${userRoleRel?.role?.id}" ></g:select>
                                </td>
                            </tr> 
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="rsUser">User:</label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean:userRoleRel,field:'rsUser','errors')}">
                                    <g:select optionKey="id" from="${auth.RsUser.list()}" name="rsUser.id" value="${userRoleRel?.rsUser?.id}" ></g:select>
                                </td>
                            </tr> 
                        
                        </tbody>
                    </table>
                </div>
                <div class="buttons">
                    <span class="button"><g:actionSubmit class="save" value="Update" /></span>
                    <span class="button"><g:actionSubmit class="delete" onclick="return confirm('Are you sure?');" value="Delete" /></span>
                </div>
            </g:form>
        </div>
    </body>
</html>
