<%@ page import="connection.SnmpConnection" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="adminLayout" />
    <title>Show SnmpConnection</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLinkTo(dir:'admin.gsp')}">Home</a></span>
    <span class="menuButton"><g:link class="list" action="list">SnmpConnection List</g:link></span>
    <span class="menuButton"><g:link class="create" action="create">New SnmpConnection</g:link></span>
</div>
<div class="body">
    <h1>Show SnmpConnection</h1>
    <g:render template="/common/messages" model="[flash:flash]"></g:render>
    <div class="dialog">
        <table>
            <tbody>

              

                <tr class="prop">
                    <td valign="top" class="name">Name:</td>

                    <td valign="top" class="value">${snmpConnection.name}</td>

                </tr>

                <tr class="prop">
                    <td valign="top" class="name">Host:</td>

                    <td valign="top" class="value">${snmpConnection.host}</td>

                </tr>

                <tr class="prop">
                    <td valign="top" class="name">Port:</td>

                    <td valign="top" class="value">${snmpConnection.port}</td>

                </tr>
                <tr class="prop">
                    <td valign="top" class="name">Min Timeout:</td>

                    <td valign="top" class="value">${snmpConnection?.minTimeout}</td>

                </tr>
                <tr class="prop">
                    <td valign="top" class="name">Max Timeout:</td>

                    <td valign="top" class="value">${snmpConnection?.maxTimeout}</td>

                </tr>

            </tbody>
        </table>
    </div>
    <div class="buttons">
        <g:form>
            <input type="hidden" name="id" value="${snmpConnection?.id}"/>
            <span class="button"><g:actionSubmit class="edit" value="Edit"/></span>
            <span class="button"><g:actionSubmit class="delete" onclick="return confirm('Are you sure?');" value="Delete"/></span>
        </g:form>
    </div>
</div>
</body>
</html>
