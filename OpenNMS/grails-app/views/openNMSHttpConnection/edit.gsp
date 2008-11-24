<%@ page import="connection.OpenNMSHttpConnection" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <meta name="layout" content="adminLayout" />
        <title>Edit OpenNMSHttpConnection</title>
    </head>
    <body>
        <div class="nav">
            <span class="menuButton"><a class="home" href="${createLinkTo(dir:'admin.gsp')}">Home</a></span>
            <span class="menuButton"><g:link class="list" action="list">OpenNMSHttpConnection List</g:link></span>
            <span class="menuButton"><g:link class="create" action="create">New OpenNMSHttpConnection</g:link></span>
        </div>
        <div class="body">
            <h1>Edit OpenNMSHttpConnection</h1>
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <g:hasErrors bean="${openNMSHttpConnection}">
            <div class="errors">
                <g:renderErrors bean="${openNMSHttpConnection}" as="list" />
            </div>
            </g:hasErrors>
            <g:form method="post" >
                <input type="hidden" name="id" value="${openNMSHttpConnection?.id}" />
                <div class="dialog">
                    <table>
                        <tbody>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="name">Name:</label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean:openNMSHttpConnection,field:'name','errors')}">
                                    <input type="text" id="name" name="name" value="${fieldValue(bean:openNMSHttpConnection,field:'name')}"/>
                                </td>
                            </tr> 
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="maxNumberOfConnections">Max. Active:</label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean:openNMSHttpConnection,field:'maxNumberOfConnections','errors')}">
                                    <input type="text" class="inputtextfield" id="maxNumberOfConnections" name="maxNumberOfConnections" value="${fieldValue(bean:openNMSHttpConnection,field:'maxNumberOfConnections')}" />
                                </td>
                            </tr> 
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="baseUrl">Base Url:</label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean:openNMSHttpConnection,field:'baseUrl','errors')}">
                                    <input type="text" class="inputtextfieldl" id="baseUrl" name="baseUrl" value="${fieldValue(bean:openNMSHttpConnection,field:'baseUrl')}"/>
                                </td>
                            </tr> 
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="username">Username:</label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean:openNMSHttpConnection,field:'username','errors')}">
                                    <input type="text" class="inputtextfield" id="username" name="username" value="${fieldValue(bean:openNMSHttpConnection,field:'username')}"/>
                                </td>
                            </tr> 
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="password">Password:</label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean:openNMSHttpConnection,field:'userPassword','errors')}">
                                    <input type="password" class="inputtextfield" id="userPassword" name="userPassword" value="${fieldValue(bean:openNMSHttpConnection,field:'userPassword')}"/>
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
