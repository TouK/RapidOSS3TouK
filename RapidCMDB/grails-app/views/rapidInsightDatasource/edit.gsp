
<%@ page import="datasource.RapidInsightDatasource" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <meta name="layout" content="main" />
        <title>Edit RapidInsightDatasource</title>
    </head>
    <body>
        <div class="nav">
            <span class="menuButton"><a class="home" href="${createLinkTo(dir:'admin.gsp')}">Home</a></span>
            <span class="menuButton"><g:link class="list" action="list">RapidInsightDatasource List</g:link></span>
            <span class="menuButton"><g:link class="create" action="create">New RapidInsightDatasource</g:link></span>
        </div>
        <div class="body">
            <h1>Edit RapidInsightDatasource</h1>
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <g:hasErrors bean="${rapidInsightDatasource}">
            <div class="errors">
                <g:renderErrors bean="${rapidInsightDatasource}" as="list" />
            </div>
            </g:hasErrors>
            <g:form method="post" >
                <input type="hidden" name="id" value="${rapidInsightDatasource?.id}" />
                <div class="dialog">
                    <table>
                        <tbody>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="name">Name:</label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean:rapidInsightDatasource,field:'name','errors')}">
                                    <input type="text" class="inputtextfield" id="name" name="name" value="${fieldValue(bean:rapidInsightDatasource,field:'name')}"/>
                                </td>
                            </tr> 
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="connection">Connection:</label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean:rapidInsightDatasource,field:'connection','errors')}">
                                    <g:select class="inputtextfield" optionKey="id" from="${connection.RapidInsightConnection.list()}" name="connection.id" value="${rapidInsightDatasource?.connection?.id}" ></g:select>
                                </td>
                            </tr>
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="reconnectInterval">Reconnect Interval:</label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean:rapidInsightDatasource,field:'reconnectInterval','errors')}">
                                    <input type="text" class="inputtextfield" id="reconnectInterval" name="reconnectInterval" value="${fieldValue(bean:rapidInsightDatasource,field:'reconnectInterval')}" /> sec.
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
