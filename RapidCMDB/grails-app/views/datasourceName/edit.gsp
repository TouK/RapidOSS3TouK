
<%@ page import="model.DatasourceName" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <meta name="layout" content="adminLayout" />
        <title>Edit DatasourceName</title>
    </head>
    <body>
        <div class="nav">
            <span class="menuButton"><a class="home" href="${createLinkTo(dir:'admin.gsp')}">Home</a></span>
            <span class="menuButton"><g:link class="list" action="list">DatasourceName List</g:link></span>
            <span class="menuButton"><g:link class="create" action="create">New DatasourceName</g:link></span>
        </div>
        <div class="body">
            <h1>Edit DatasourceName</h1>
            <g:render template="/common/messages" model="[flash:flash, beans:[datasourceName]]"></g:render>
            <g:form method="post" >
                <input type="hidden" name="id" value="${datasourceName?.id}" />
                <div class="dialog">
                    <table>
                        <tbody>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="name">Name:</label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean:datasourceName,field:'name','errors')}">
                                    <input type="text" id="name" name="name" value="${fieldValue(bean:datasourceName,field:'name')}"/>
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
