
<%@ page import="datasource.SametimeDatasource" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <meta name="layout" content="adminLayout" />
        <title>Create SametimeDatasource</title>
    </head>
    <body>
        <div class="nav">
            <span class="menuButton"><a class="home" href="${createLinkTo(dir:'admin.gsp')}">Home</a></span>
            <span class="menuButton"><g:link class="list" action="list">SametimeDatasource List</g:link></span>
        </div>
        <div class="body">
            <h1>Create SametimeDatasource</h1>
            <g:render template="/common/messages" model="[flash:flash, beans:[sametimeDatasource]]"></g:render>
            <g:form action="save" method="post" >
                <div class="dialog">
                    <table>
                        <tbody>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="name">Name:</label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean:sametimeDatasource,field:'name','errors')}">
                                    <input type="text" class="inputtextfield" id="name" name="name" value="${fieldValue(bean:sametimeDatasource,field:'name')}"/>
                                </td>
                            </tr> 
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="connection">Connection:</label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean:sametimeDatasource,field:'connection','errors')}">
                                    <g:select class="inputtextfield" optionKey="id" from="${connection.SametimeConnection.list()}" name="connection.id" value="${sametimeDatasource?.connection?.id}" ></g:select>
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
