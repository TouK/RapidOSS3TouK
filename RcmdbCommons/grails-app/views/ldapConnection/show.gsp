<%@ page import="connection.LdapConnection" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="adminLayout" />
    <title>Show LdapConnection </title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><g:link class="list" action="list">LdapConnection List</g:link></span>
    <span class="menuButton"><g:link class="create" action="create">New LdapConnection</g:link></span>
</div>
<div class="body">
    <h1>Show LdapConnection</h1>
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
                    <td valign="top" class="name">Name:</td>

                    <td valign="top" class="value">${ldapConnection?.name}</td>

                </tr>

              
                <tr class="prop">
                    <td valign="top" class="name">Context Factory:</td>

                    <td valign="top" class="value">${ldapConnection?.contextFactory}</td>

                </tr>

                <tr class="prop">
                    <td valign="top" class="name">Url:</td>

                    <td valign="top" class="value">${ldapConnection?.url}</td>

                </tr>

                <tr class="prop">
                    <td valign="top" class="name">Username:</td>

                    <td valign="top" class="value">${ldapConnection?.username}</td>

                </tr>

                <tr class="prop">
                    <td valign="top" class="name">Min Timeout:</td>

                    <td valign="top" class="value">${ldapConnection?.minTimeout}</td>

                </tr>
                <tr class="prop">
                    <td valign="top" class="name">Max Timeout:</td>

                    <td valign="top" class="value">${ldapConnection?.maxTimeout}</td>

                </tr>

              </tbody>
        </table>
    </div>
    <div class="buttons">
        <g:form>
            <input type="hidden" name="id" value="${ldapConnection?.id}"/>
            <span class="button"><g:actionSubmit class="edit" value="Edit"/></span>
            <span class="button"><g:actionSubmit class="delete" onclick="return confirm('Are you sure?');" value="Delete"/></span>
        </g:form>
    </div>
</div>
</body>
</html>
