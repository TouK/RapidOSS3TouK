<%@ page import="datasource.JabberDatasource" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="adminLayout" />
    <title>Show JabberDatasource</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLinkTo(dir: 'admin.gsp')}">Home</a></span>
    <span class="menuButton"><g:link class="list" action="list">JabberDatasource List</g:link></span>
    <span class="menuButton"><g:link class="create" action="create">New JabberDatasource</g:link></span>
</div>
<div class="body">
    <h1>Show JabberDatasource</h1>
    <g:render template="/common/messages" model="[flash:flash]"></g:render>
    <div class="dialog">
        <table>
            <tbody>

              

                <tr class="prop">
                    <td valign="top" class="name">Name:</td>

                    <td valign="top" class="value">${jabberDatasource.name}</td>

                </tr>
                <tr class="prop">
                    <td valign="top" class="name">Connection:</td>

                    <td valign="top" class="value"><g:link controller="jabberConnection" action="show" id="${jabberDatasource?.connection?.id}">${jabberDatasource?.connection}</g:link></td>

                </tr>

            </tbody>
        </table>
    </div>
    <div class="buttons">
        <g:form>
            <input type="hidden" name="id" value="${jabberDatasource?.id}"/>
            <span class="button"><g:actionSubmit class="edit" value="Edit"/></span>
            <span class="button"><g:actionSubmit class="delete" onclick="return confirm('Are you sure?');" value="Delete"/></span>
        </g:form>
    </div>
</div>
</body>
</html>
