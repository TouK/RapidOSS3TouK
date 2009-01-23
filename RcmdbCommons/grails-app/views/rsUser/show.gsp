
<%@ page import="auth.*" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <meta name="layout" content="adminLayout" />
        <title>Show User</title>
    </head>
    <body>
        <div class="nav">
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
                        <tr class="prop">
                            <td valign="top" class="name">Groups:</td>

                            <td valign="top" style="text-align:left;" class="value">
                                <ul>
                                    <g:each var="group" in="${rsUser?.groups}">
                                        <li><g:link controller="group" action="show" id="${group.id}">${group}</g:link></li>
                                    </g:each>
                                </ul>
                            </td>

                        </tr>
                    </tbody>
                </table>
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
