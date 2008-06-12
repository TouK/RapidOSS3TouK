<%@ page import="model.*" %>

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Show ModelProperty</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLinkTo(dir: 'model/show/' + modelProperty?.model?.id)}">${modelProperty?.model}</a></span>
    <span class="menuButton"><a class="logout" href="${createLinkTo(dir: 'auth/logout')}">Logout</a></span>
</div>
<div class="body">
    <h1>Show ModelProperty</h1>
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

                    <td valign="top" class="value">${modelProperty.name}</td>

                </tr>

                <tr class="prop">
                    <td valign="top" class="name">Static Datasource Name:</td>

                    <td valign="top" class="value"><g:link controller="modelDatasource" action="show" id="${modelProperty?.propertyDatasource?.id}">${modelProperty?.propertyDatasource?.datasource?.name}</g:link></td>

                </tr>

                <tr class="prop">
                    <td valign="top" class="name">Dynamic Datasource (specified in property):</td>

                    <td valign="top" class="value"><g:link controller="modelProperty" action="show" id="${modelProperty?.propertySpecifyingDatasource?.id}">${modelProperty?.propertySpecifyingDatasource}</g:link></td>

                </tr>

                <tr class="prop">
                    <td valign="top" class="name">Type:</td>

                    <td valign="top" class="value">${modelProperty.type}</td>

                </tr>

                <tr class="prop">
                    <td valign="top" class="name">Default Value:</td>

                    <td valign="top" class="value">${modelProperty.defaultValue}</td>

                </tr>

                <tr class="prop">
                    <td valign="top" class="name">Lazy:</td>

                    <td valign="top" class="value">${modelProperty.lazy}</td>

                </tr>

                <tr class="prop">
                    <td valign="top" class="name">Name In Datasource:</td>

                    <td valign="top" class="value">${modelProperty.nameInDatasource}</td>

                </tr>

            </tbody>
        </table>
    </div>
    <div class="buttons">
        <g:form>
            <input type="hidden" name="id" value="${modelProperty?.id}"/>
            <span class="button"><g:actionSubmit class="edit" value="Edit"/></span>
            <span class="button"><g:actionSubmit class="delete" onclick="return confirm('Are you sure?');" value="Delete"/></span>
        </g:form>
    </div>
</div>
</body>
</html>
