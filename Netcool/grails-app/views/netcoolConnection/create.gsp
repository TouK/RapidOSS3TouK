<%@ page import="connection.NetcoolConnection" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <title>Create NetcoolConnection</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLinkTo(dir: 'admin.gsp')}">Home</a></span>
    <span class="menuButton"><g:link class="list" action="list">NetcoolConnection List</g:link></span>
</div>
<div class="body">
    <h1>Create NetcoolConnection</h1>
    <g:if test="${flash.message}">
        <div class="message">${flash.message}</div>
    </g:if>
    <g:hasErrors bean="${netcoolConnection}">
        <div class="errors">
            <g:renderErrors bean="${netcoolConnection}" as="list"/>
        </div>
    </g:hasErrors>
    <g:form action="save" method="post">
        <div class="dialog">
            <table>
                <tbody>

                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="name">Name:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean: netcoolConnection, field: 'name', 'errors')}">
                            <input type="text" class="inputtextfield" id="name" name="name" value="${fieldValue(bean: netcoolConnection, field: 'name')}"/>
                        </td>
                    </tr>

                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="host">Host:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean: netcoolConnection, field: 'host', 'errors')}">
                            <input type="text" class="inputtextfieldl" id="host" name="host" value="${fieldValue(bean: netcoolConnection, field: 'host')}"/>
                        </td>
                    </tr>

                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="port">Port:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean: netcoolConnection, field: 'port', 'errors')}">
                            <input type="text" class="inputtextfieldl" id="port" name="port" value="${fieldValue(bean: netcoolConnection, field: 'port')}"/>
                        </td>
                    </tr>

                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="username">Username:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean: netcoolConnection, field: 'username', 'errors')}">
                            <input type="text" class="inputtextfield" id="username" name="username" value="${fieldValue(bean: netcoolConnection, field: 'username')}"/>
                        </td>
                    </tr>

                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="userPassword">Password:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean: netcoolConnection, field: 'userPassword', 'errors')}">
                            <input type="password" class="inputtextfield" id="userPassword" name="userPassword" value="${fieldValue(bean: netcoolConnection, field: 'userPassword')}"/>
                        </td>
                    </tr>
                </tbody>
            </table>
        </div>
        <div class="buttons">
            <span class="button"><input class="save" type="submit" value="Create"/></span>
        </div>
    </g:form>
</div>
</body>
</html>
