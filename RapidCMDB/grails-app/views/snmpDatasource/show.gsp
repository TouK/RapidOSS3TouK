<%@ page import="datasource.SnmpDatasource" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Show SnmpDatasource</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLinkTo(dir:'admin.gsp')}">Home</a></span>
    <span class="menuButton"><g:link class="list" action="list">SnmpDatasource List</g:link></span>
    <span class="menuButton"><g:link class="create" action="create">New SnmpDatasource</g:link></span>
<div class="body">
    <h1>Show SnmpDatasource</h1>
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
                    <td valign="top" class="name">Id:</td>

                    <td valign="top" class="value">${snmpDatasource.id}</td>

                </tr>

                <tr class="prop">
                    <td valign="top" class="name">Name:</td>

                    <td valign="top" class="value">${snmpDatasource.name}</td>

                </tr>

                <tr class="prop">
                    <td valign="top" class="name">Script:</td>

                    <td valign="top" class="value"><g:link controller="script" action="show" id="${script?.id}">${snmpDatasource?.scriptName}</g:link></td>

                </tr>

                <tr class="prop">
                    <td valign="top" class="name">Connection:</td>

                    <td valign="top" class="value"><g:link controller="snmpConnection" action="show" id="${snmpDatasource?.connection?.id}">${snmpDatasource?.connection}</g:link></td>

                </tr>

            </tbody>
        </table>
    </div>
    <div class="buttons">
        <g:form>
            <input type="hidden" name="id" value="${snmpDatasource?.id}"/>
            <span class="button"><g:actionSubmit class="edit" value="Edit"/></span>
            <span class="button"><g:actionSubmit class="delete" onclick="return confirm('Are you sure?');" value="Delete"/></span>
            <g:if test="${snmpDatasource?.isOpen()}">
                <span class="button"><g:actionSubmit class="close" value="Stop" /></span>
            </g:if>
            <g:else>
                <span class="button"><g:actionSubmit class="run" value="Start" /></span>
            </g:else>
        </g:form>
    </div>
</div>
</body>
</html>
