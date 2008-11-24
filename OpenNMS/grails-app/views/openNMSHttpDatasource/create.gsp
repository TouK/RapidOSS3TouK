<%@ page import="datasource.OpenNMSHttpDatasource" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <meta name="layout" content="adminLayout" />
        <title>Create OpenNMSHttpDatasource</title>
    </head>
    <body>
        <div class="nav">
            <span class="menuButton"><a class="home" href="${createLinkTo(dir:'admin.gsp')}">Home</a></span>
            <span class="menuButton"><g:link class="list" action="list">OpenNMSHttpDatasource List</g:link></span>
        </div>
        <div class="body">
            <h1>Create OpenNMSHttpDatasource</h1>
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <g:hasErrors bean="${openNMSHttpDatasource}">
            <div class="errors">
                <g:renderErrors bean="${openNMSHttpDatasource}" as="list" />
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
                                <td valign="top" class="value ${hasErrors(bean:openNMSHttpDatasource,field:'name','errors')}">
                                    <input type="text" class="inputtextfield" id="name" name="name" value="${fieldValue(bean:openNMSHttpDatasource,field:'name')}"/>
                                </td>
                            </tr> 
                        
                            <tr class="prop">
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="connection">Connection:</label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean:openNMSHttpDatasource,field:'connection','errors')}">
                                    <g:select class="inputtextfield" optionKey="id" from="${connection.OpenNMSHttpConnection.list()}" name="connection.id" value="${openNMSHttpDatasource?.connection?.id}" ></g:select>
                                </td>
                            </tr>
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="reconnectInterval">Reconnect Interval:</label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean:openNMSHttpDatasource,field:'reconnectInterval','errors')}">
                                    <input type="text" class="inputtextfield" id="reconnectInterval" name="reconnectInterval" value="${fieldValue(bean:openNMSHttpDatasource,field:'reconnectInterval')}" />sec.
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
