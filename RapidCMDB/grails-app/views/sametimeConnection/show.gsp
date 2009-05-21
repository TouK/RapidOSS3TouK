<%@ page import="connection.SametimeConnection" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="adminLayout"/>
    <title>Show SametimeConnection</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLinkTo(dir: 'admin.gsp')}">Home</a></span>
    <span class="menuButton"><g:link class="list" action="list">SametimeConnection List</g:link></span>
    <span class="menuButton"><g:link class="create" action="create">New SametimeConnection</g:link></span>
</div>
<div class="body">
    <h1>Show SametimeConnection</h1>
    <g:render template="/common/messages" model="[flash:flash]"></g:render>
    <div class="dialog">
        <table>
            <tbody>

                <tr class="prop">
                    <td valign="top" class="name">Name:</td>

                    <td valign="top" class="value">${sametimeConnection.name}</td>

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
            <input type="hidden" name="id" value="${sametimeConnection?.id}"/>
            <span class="button"><g:actionSubmit class="edit" value="Edit"/></span>
            <span class="button"><g:actionSubmit class="delete" onclick="return confirm('Are you sure?');" value="Delete"/></span>
        </g:form>
    </div>
</div>
</body>
</html>
