<%@ page import="connector.JabberConnector" %>

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="adminLayout"/>
    <title>Show JabberConnector</title>
</head>
<body>
<div class="nav">    
    <span class="menuButton"><g:link class="list" action="list">JabberConnector List</g:link></span>
    <span class="menuButton"><g:link class="create" action="create">New JabberConnector</g:link></span>
</div>
<div class="body">
    <h1>Show JabberConnector</h1>
    <g:render template="/common/messages" model="[flash:flash]"></g:render>
    <div class="dialog">
        <table>
            <tbody>

                 <g:set var="jabberConnection" value="${jabberConnector?.ds?.connection}"></g:set>

                 <tr class="prop">
                    <td valign="top" class="name">Name:</td>

                    <td valign="top" class="value">${jabberConnector.name}</td>

                </tr>



                <tr class="prop">
                    <td valign="top" class="name">Host:</td>

                    <td valign="top" class="value">${jabberConnection.host}</td>

                </tr>

                <tr class="prop">
                    <td valign="top" class="name">Port:</td>

                    <td valign="top" class="value">${jabberConnection.port}</td>

                </tr>
                <tr class="prop">
                    <td valign="top" class="name">Service Name:</td>

                    <td valign="top" class="value">${jabberConnection.serviceName}</td>

                </tr>

                 <tr class="prop">
                    <td valign="top" class="name">Username:</td>

                    <td valign="top" class="value">${jabberConnection.username}</td>

                </tr>

                <tr class="prop">
                    <td valign="top" class="name">Password:</td>

                    <td valign="top" class="value">${jabberConnection.userPassword}</td>

                </tr>



                <tr class="prop">
                    <td valign="top" class="name">Min Timeout:</td>

                    <td valign="top" class="value">${jabberConnection?.minTimeout}</td>

                </tr>
                <tr class="prop">
                    <td valign="top" class="name">Max Timeout:</td>

                    <td valign="top" class="value">${jabberConnection?.maxTimeout}</td>

                </tr>

            </tbody>
        </table>
    </div>
    <div class="buttons">
        <g:form>
            <input type="hidden" name="id" value="${jabberConnector?.id}"/>
            <span class="button"><g:actionSubmit class="edit" value="Edit"/></span>
            <span class="button"><g:actionSubmit class="delete" onclick="return confirm('Are you sure?');" value="Delete"/></span>
        </g:form>
    </div>
</div>
</body>
</html>
