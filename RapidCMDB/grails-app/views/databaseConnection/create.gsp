
<%@ page import="connection.DatabaseConnection" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <meta name="layout" content="main" />
        <title>Create DatabaseConnection</title>         
    </head>
    <body>
        <div class="nav">
            <span class="menuButton"><a class="home" href="${createLinkTo(dir:'admin.gsp')}">Home</a></span>
            <span class="menuButton"><g:link class="list" action="list">DatabaseConnection List</g:link></span>
             <span class="menuButton"><a class="logout" href="${createLinkTo(dir: 'auth/logout')}">Logout</a></span>
        </div>
        <div class="body">
            <h1>Create DatabaseConnection</h1>
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <g:hasErrors bean="${databaseConnection}">
            <div class="errors">
                <g:renderErrors bean="${databaseConnection}" as="list" />
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
                                <td valign="top" class="value ${hasErrors(bean:databaseConnection,field:'name','errors')}">
                                    <input type="text" class="inputtextfield" id="name" name="name" value="${fieldValue(bean:databaseConnection,field:'name')}"/>
                                </td>
                            </tr> 
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="connectionClass">Connection Class:</label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean:databaseConnection,field:'connectionClass','errors')}">
                                    <input type="text"  class="inputtextfieldl" id="connectionClass" name="connectionClass" value="${fieldValue(bean:databaseConnection,field:'connectionClass')}"/>
                                </td>
                            </tr>
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="maxNumberOfConnections">Max. Active:</label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean:databaseConnection,field:'maxNumberOfConnections','errors')}">
                                    <input type="text" class="inputtextfield" id="maxNumberOfConnections" name="maxNumberOfConnections" value="${fieldValue(bean:databaseConnection,field:'maxNumberOfConnections')}" />
                                </td>
                            </tr> 
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="driver">Driver:</label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean:databaseConnection,field:'driver','errors')}">
                                    <input type="text" class="inputtextfieldl id="driver" name="driver" value="${fieldValue(bean:databaseConnection,field:'driver')}"/>
                                </td>
                            </tr> 
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="url">Url:</label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean:databaseConnection,field:'url','errors')}">
                                    <input type="text" class="inputtextfieldl" id="url" name="url" value="${fieldValue(bean:databaseConnection,field:'url')}"/>
                                </td>
                            </tr> 
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="username">Username:</label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean:databaseConnection,field:'username','errors')}">
                                    <input type="text" class="inputtextfield" id="username" name="username" value="${fieldValue(bean:databaseConnection,field:'username')}"/>
                                </td>
                            </tr> 
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="password">Password:</label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean:databaseConnection,field:'userPassword','errors')}">
                                    <input type="password" class="inputtextfield" id="userPassword" name="userPassword" value="${fieldValue(bean:databaseConnection,field:'userPassword')}"/>
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
