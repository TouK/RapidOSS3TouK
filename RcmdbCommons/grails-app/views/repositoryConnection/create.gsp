
<%@ page import="connection.RepositoryConnection" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <meta name="layout" content="adminLayout" />
        <title>Create RepositoryConnection</title>
    </head>
    <body>
        <div class="nav">
            <span class="menuButton"><g:link class="list" action="list">RepositoryConnection List</g:link></span>
        </div>
        <div class="body">
            <h1>Create RepositoryConnection</h1>
            <g:render template="/common/messages" model="[flash:flash, beans:[repositoryConnection]]"></g:render>
            <g:form action="save" method="post" >
                <div class="dialog">
                    <table>
                        <tbody>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="name">Name:</label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean:repositoryConnection,field:'name','errors')}">
                                    <input type="text" class="inputtextfield" id="name" name="name" value="${fieldValue(bean:repositoryConnection,field:'name')}"/>
                                </td>
                            </tr> 
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="maxNumberOfConnections">Max. Active:</label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean:repositoryConnection,field:'maxNumberOfConnections','errors')}">
                                    <input type="text" class="inputtextfield" id="maxNumberOfConnections" name="maxNumberOfConnections" value="${fieldValue(bean:repositoryConnection,field:'maxNumberOfConnections')}" />
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
