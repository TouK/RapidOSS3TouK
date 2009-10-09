<%@ page import="datasource.EmailDatasource" %>

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="adminLayout"/>
    <title>Create EmailDatasource</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLinkTo(dir:'')}">Home</a></span>
    <span class="menuButton"><g:link class="list" action="list">EmailDatasource List</g:link></span>
</div>
<div class="body">
    <h1>Create EmailDatasource</h1>
    <g:render template="/common/messages" model="[flash:flash, beans:[emailDatasource]]"></g:render>
    <g:form action="save" method="post" >
        <div class="dialog">
            <table>
                <tbody>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="name">Name:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:emailDatasource,field:'name','errors')}">
                            <input type="text" class="inputtextfield" id="name" name="name" value="${fieldValue(bean:emailDatasource,field:'name')}"/>
                        </td>
                    </tr>

                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="connection">Connection:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:emailDatasource,field:'connection','errors')}">
                            <g:select class="inputtextfield1" optionKey="id" from="${connection.EmailConnection.list()}" name="connection.id" value="${emailDatasource?.connection?.id}" ></g:select>
                        </td>
                    </tr>

                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="reconnectInterval">Reconnect Interval:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:emailDatasource,field:'reconnectInterval','errors')}">
                            <input type="text" class="inputtextfield" id="reconnectInterval" name="reconnectInterval" value="${fieldValue(bean:emailDatasource,field:'reconnectInterval')}" />sec.
                        </td>
                    </tr>
                    
                </tbody>
            </table>
        </div>
        <div class="buttons">
            <span class="button"><input class="save" type="submit" value="Create"/></span>
        </div>
    </g:form>
</div>
</body>
</html>
