
<%@ page import="datasource.ApgDatabaseDatasource" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <title>Create ApgDatabaseDatasource</title>
    </head>
    <body>
        <div class="nav">
            <span class="menuButton"><a class="home" href="${createLinkTo(dir:'admin.gsp')}">Home</a></span>
            <span class="menuButton"><g:link class="list" action="list">ApgDatabaseDatasource List</g:link></span>
        </div>
        <div class="body">
            <h1>Create ApgDatabaseDatasource</h1>
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <g:hasErrors bean="${apgDatabaseDatasource}">
            <div class="errors">
                <g:renderErrors bean="${apgDatabaseDatasource}" as="list" />
            </div>
            </g:hasErrors>
            <g:form action="save" method="post" >
                <div class="dialog">
                    <table>
                        <tbody>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="name">Name:</label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean:apgDatabaseDatasource,field:'name','errors')}">
                                    <input type="text" class="inputtextfield" id="name" name="name" value="${fieldValue(bean:apgDatabaseDatasource,field:'name')}"/>
                                </td>
                            </tr> 
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="connection">Connection:</label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean:apgDatabaseDatasource,field:'connection','errors')}">
                                    <g:select class="inputtextfield" optionKey="id" from="${connection.ApgConnection.list()}" name="connection.id" value="${apgDatabaseDatasource?.connection?.id}" ></g:select>
                                </td>
                            </tr>

                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="reconnectInterval">Reconnect Interval:</label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean:apgDatabaseDatasource,field:'reconnectInterval','errors')}">
                                    <input type="text" class="inputtextfield" id="reconnectInterval" name="reconnectInterval" value="${fieldValue(bean:apgDatabaseDatasource,field:'reconnectInterval')}" />sec.
                                </td>
                            </tr> 
                        
                        </tbody>
                    </table>
                </div>
                <div class="buttons">
                    <span class="button"><input class="save" type="submit" value="Create" /></span>
                </div>
            </g:form>
        </div>
    </body>
</html>
