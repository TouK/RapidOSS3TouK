
<%@ page import="datasource.DatabaseDatasource" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <meta name="layout" content="main" />
        <title>Show DatabaseDatasource</title>
    </head>
    <body>
        <div class="nav">
            <span class="menuButton"><a class="home" href="${createLinkTo(dir:'')}">Home</a></span>
            <span class="menuButton"><g:link class="list" action="list">DatabaseDatasource List</g:link></span>
            <span class="menuButton"><g:link class="create" action="create">New DatabaseDatasource</g:link></span>
        </div>
        <div class="body">
            <h1>Show DatabaseDatasource</h1>
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <div class="dialog">
                <table>
                    <tbody>
                    
                        <tr class="prop">
                            <td valign="top" class="name">Name:</td>
                            
                            <td valign="top" class="value">${databaseDatasource.name}</td>
                            
                        </tr>
                    
                        <tr class="prop">
                            <td valign="top" class="name">Connection:</td>
                            
                            <td valign="top" class="value"><g:link controller="databaseConnection" action="show" id="${databaseDatasource?.connection?.id}">${databaseDatasource?.connection}</g:link></td>
                            
                        </tr>
                    
                    </tbody>
                </table>
            </div>
            <div class="buttons">
                <g:form>
                    <input type="hidden" name="id" value="${databaseDatasource?.id}" />
                    <span class="button"><g:actionSubmit class="edit" value="Edit" /></span>
                    <span class="button"><g:actionSubmit class="delete" onclick="return confirm('Are you sure?');" value="Delete" /></span>
                </g:form>
            </div>
        </div>
    </body>
</html>
