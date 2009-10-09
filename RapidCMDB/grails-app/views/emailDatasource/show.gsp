<%@ page import="datasource.EmailDatasource" %>

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="adminLayout"/>
    <title>Show EmailDatasource</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLinkTo(dir:'')}">Home</a></span>
    <span class="menuButton"><g:link class="list" action="list">EmailDatasource List</g:link></span>
    <span class="menuButton"><g:link class="create" action="create">New EmailDatasource</g:link></span>
</div>
<div class="body">
    <h1>Show EmailDatasource</h1>
    <g:render template="/common/messages" model="[flash:flash]"></g:render>
    <div class="dialog">
        <table>
            <tbody>

                


                <tr class="prop">
                    <td valign="top" class="name">Name:</td>

                    <td valign="top" class="value">${emailDatasource.name}</td>

                </tr>

                <tr class="prop">
                    <td valign="top" class="name">Connection:</td>

                    <td valign="top" class="value"><g:link controller="emailConnection" action="show" id="${emailDatasource?.connection?.id}">${emailDatasource?.connection}</g:link></td>

                </tr>

                <tr class="prop">
                    <td valign="top" class="name">Reconnect Interval:</td>

                    <td valign="top" class="value">${emailDatasource.reconnectInterval} sec.</td>

                </tr>
                
            </tbody>
        </table>
    </div>
    <div class="buttons">
        <g:form>
            <input type="hidden" name="id" value="${emailDatasource?.id}"/>
            <span class="button"><g:actionSubmit class="edit" value="Edit"/></span>
            <span class="button"><g:actionSubmit class="delete" onclick="return confirm('Are you sure?');" value="Delete"/></span>
        </g:form>
    </div>
</div>
</body>
</html>
