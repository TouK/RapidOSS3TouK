
<%@ page import="connection.RepositoryConnection" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <meta name="layout" content="adminLayout" />
        <title>Edit RepositoryConnection</title>
    </head>
    <body>
        <div class="nav">
            <span class="menuButton"><g:link class="list" action="list">RepositoryConnection List</g:link></span>
            <span class="menuButton"><g:link class="create" action="create">New RepositoryConnection</g:link></span>
        </div>
        <div class="body">
            <h1>Edit RepositoryConnection</h1>
            <g:render template="/common/messages" model="[flash:flash, beans:[repositoryConnection]]"></g:render>
            <g:form method="post" >
                <input type="hidden" name="id" value="${repositoryConnection?.id}" />
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
                    <span class="button"><g:actionSubmit class="save" value="Update" /></span>
                    <span class="button"><g:actionSubmit class="delete" onclick="return confirm('Are you sure?');" value="Delete" /></span>
                </div>
            </g:form>
        </div>
    </body>
</html>
