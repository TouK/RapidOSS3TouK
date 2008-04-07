<%@ page import="connection.RapidInsightConnection" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Show RapidInsightConnection</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLinkTo(dir: '')}">Home</a></span>
    <span class="menuButton"><g:link class="list" action="list">RapidInsightConnection List</g:link></span>
    <span class="menuButton"><g:link class="create" action="create">New RapidInsightConnection</g:link></span>
</div>
<div class="body">
    <h1>Show RapidInsightConnection</h1>
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

                    <td valign="top" class="value">${rapidInsightConnection.name}</td>

                </tr>

                <tr class="prop">
                    <td valign="top" class="name">Connection Class:</td>

                    <td valign="top" class="value">${rapidInsightConnection.connectionClass}</td>

                </tr>

                <tr class="prop">
                    <td valign="top" class="name">Base Url:</td>

                    <td valign="top" class="value">${rapidInsightConnection.baseUrl}</td>

                </tr>

                <tr class="prop">
                    <td valign="top" class="name">Username:</td>

                    <td valign="top" class="value">${rapidInsightConnection.username}</td>

                </tr>

                <tr class="prop">
                    <td valign="top" class="name">Password:</td>

                    <td valign="top" class="value">${rapidInsightConnection.password}</td>

                </tr>

            </tbody>
        </table>
    </div>
    <div class="buttons">
        <g:form>
            <input type="hidden" name="id" value="${rapidInsightConnection?.id}"/>
            <span class="button"><g:actionSubmit class="edit" value="Edit"/></span>
            <span class="button"><g:actionSubmit class="delete" onclick="return confirm('Are you sure?');" value="Delete"/></span>
        </g:form>
    </div>
</div>
</body>
</html>
