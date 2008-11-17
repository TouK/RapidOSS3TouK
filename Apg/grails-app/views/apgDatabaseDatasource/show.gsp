<%@ page import="datasource.ApgDatabaseDatasource" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <title>Show ApgDatabaseDatasource</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLinkTo(dir:'admin.gsp')}">Home</a></span>
    <span class="menuButton"><g:link class="list" action="list">ApgDatabaseDatasource List</g:link></span>
    <span class="menuButton"><g:link class="create" action="create">New ApgDatabaseDatasource</g:link></span>
</div>
<div class="body">
    <h1>Show ApgDatabaseDatasource</h1>
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

                    <td valign="top" class="value">${apgDatabaseDatasource.name}</td>

                </tr>

                <tr class="prop">
                    <td valign="top" class="name">Connection:</td>

                    <td valign="top" class="value"><g:link controller="apgConnection" action="show" id="${apgDatabaseDatasource?.connection?.id}">${apgDatabaseDatasource?.connection}</g:link></td>

                </tr>

                <tr class="prop">
                    <td valign="top" class="name">Reconnect Interval:</td>

                    <td valign="top" class="value">${apgDatabaseDatasource.reconnectInterval} sec.</td>

                </tr>

            </tbody>
        </table>
    </div>
    <div class="buttons">
        <g:form>
            <input type="hidden" name="id" value="${apgDatabaseDatasource?.id}"/>
            <span class="button"><g:actionSubmit class="edit" value="Edit"/></span>
            <span class="button"><g:actionSubmit class="delete" onclick="return confirm('Are you sure?');" value="Delete"/></span>
        </g:form>
    </div>
</div>
</body>
</html>
