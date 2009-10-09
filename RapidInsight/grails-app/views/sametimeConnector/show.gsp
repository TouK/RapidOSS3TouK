<%@ page import="connector.SametimeConnector" %>

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="adminLayout"/>
    <title>Show SametimeConnector</title>
</head>
<body>
<div class="nav">    
    <span class="menuButton"><g:link class="list" action="list">SametimeConnector List</g:link></span>
    <span class="menuButton"><g:link class="create" action="create">New SametimeConnector</g:link></span>
</div>
<div class="body">
    <h1>Show SametimeConnector</h1>
    <g:render template="/common/messages" model="[flash:flash]"></g:render>
    <div class="dialog">
        <table>
            <tbody>

                 <g:set var="sametimeConnection" value="${sametimeConnector?.ds?.connection}"></g:set>

                 <tr class="prop">
                    <td valign="top" class="name">Name:</td>

                    <td valign="top" class="value">${sametimeConnector.name}</td>

                </tr>



                <tr class="prop">
                    <td valign="top" class="name">Host:</td>

                    <td valign="top" class="value">${sametimeConnection.host}</td>

                </tr>


                <tr class="prop">
                    <td valign="top" class="name">Community:</td>

                    <td valign="top" class="value">${sametimeConnection.community}</td>

                </tr>

                 <tr class="prop">
                    <td valign="top" class="name">Username:</td>

                    <td valign="top" class="value">${sametimeConnection.username}</td>

                </tr>

                <tr class="prop">
                    <td valign="top" class="name">Password:</td>

                    <td valign="top" class="value">${sametimeConnection.userPassword}</td>

                </tr>



                <tr class="prop">
                    <td valign="top" class="name">Min Timeout:</td>

                    <td valign="top" class="value">${sametimeConnection?.minTimeout}</td>

                </tr>
                <tr class="prop">
                    <td valign="top" class="name">Max Timeout:</td>

                    <td valign="top" class="value">${sametimeConnection?.maxTimeout}</td>

                </tr>

            </tbody>
        </table>
    </div>
    <div class="buttons">
        <g:form>
            <input type="hidden" name="id" value="${sametimeConnector?.id}"/>
            <span class="button"><g:actionSubmit class="edit" value="Edit"/></span>
            <span class="button"><g:actionSubmit class="delete" onclick="return confirm('Are you sure?');" value="Delete"/></span>
        </g:form>
    </div>
</div>
</body>
</html>
