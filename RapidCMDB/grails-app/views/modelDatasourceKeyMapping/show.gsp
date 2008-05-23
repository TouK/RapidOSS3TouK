<%@ page import="model.*" %>

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Show ModelDatasourceKeyMapping</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLinkTo(dir: 'modelDatasource/show/' + modelDatasourceKeyMapping?.datasource?.id)}">${modelDatasourceKeyMapping?.datasource?.datasource?.name}</a></span>
    <span class="menuButton"><a class="logout" href="${createLinkTo(dir: 'auth/logout')}">Logout</a></span>
</div>
<div class="body">
    <h1>Show ModelDatasourceKeyMapping</h1>
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
                    <td valign="top" class="name">Name In Datasource:</td>

                    <td valign="top" class="value">${modelDatasourceKeyMapping.nameInDatasource}</td>
                </tr>
                <tr class="prop">
                    <td valign="top" class="name">Property:</td>

                    <td valign="top" class="value"><g:link controller="modelProperty" action="show" id="${modelDatasourceKeyMapping?.property?.id}">${modelDatasourceKeyMapping?.property}</g:link></td>

                </tr>

            </tbody>
        </table>
    </div>
    <div class="buttons">
        <g:form>
            <input type="hidden" name="id" value="${modelDatasourceKeyMapping?.id}"/>
            <span class="button"><g:actionSubmit class="edit" value="Edit"/></span>
            <span class="button"><g:actionSubmit class="delete" onclick="return confirm('Are you sure?');" value="Delete"/></span>
        </g:form>
    </div>
</div>
</body>
</html>
