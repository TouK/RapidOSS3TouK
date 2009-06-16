
<%@ page import="datasource.AolDatasource" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <meta name="layout" content="adminLayout" />
        <title>Edit AolDatasource</title>
    </head>
    <body>
        <div class="nav">
            <span class="menuButton"><a class="home" href="${createLinkTo(dir:'admin.gsp')}">Home</a></span>
            <span class="menuButton"><g:link class="list" action="list">AolDatasource List</g:link></span>
            <span class="menuButton"><g:link class="create" action="create">New AolDatasource</g:link></span>
        </div>
        <div class="body">
            <h1>Edit AolDatasource</h1>
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <g:hasErrors bean="${aolDatasource}">
            <div class="errors">
                <g:renderErrors bean="${aolDatasource}" as="list" />
            </div>
            </g:hasErrors>
            <g:form method="post" >
                <input type="hidden" name="id" value="${aolDatasource?.id}" />
                <div class="dialog">
                    <table>
                        <tbody>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="name">Name:</label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean:aolDatasource,field:'name','errors')}">
                                    <input type="text" class="inputtextfield" id="name" name="name" value="${fieldValue(bean:aolDatasource,field:'name')}"/>
                                </td>
                            </tr> 
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="connection">Connection:</label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean:aolDatasource,field:'connection','errors')}">
                                    <g:select class="inputtextfield" optionKey="id" from="${connection.AolConnection.list()}" name="connection.id" value="${aolDatasource?.connection?.id}" ></g:select>
                                </td>
                            </tr> 
                        
                        </tbody>
                    </table>
                </div>
                <div class="buttons">
                    <span class="button"><g:actionSubmit class="save" value="Update" /></span>
                    <span class="button"><g:actionSubmit class="delete" onclick="return confirm('Are you sure?');" value="Delete" /></span>
                </div>
            </g:form>
        </div>
    </body>
</html>
