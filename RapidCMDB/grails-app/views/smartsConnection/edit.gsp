
<%@ page import="connection.SmartsConnection" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <meta name="layout" content="main" />
        <title>Edit SmartsConnection</title>
    </head>
    <body>
        <div class="nav">
            <span class="menuButton"><a class="home" href="${createLinkTo(dir:'admin.gsp')}">Home</a></span>
            <span class="menuButton"><g:link class="list" action="list">SmartsConnection List</g:link></span>
            <span class="menuButton"><g:link class="create" action="create">New SmartsConnection</g:link></span>
            <span class="menuButton"><a class="logout" href="${createLinkTo(dir: 'auth/logout')}">Logout</a></span>
        </div>
        <div class="body">
            <h1>Edit SmartsConnection</h1>
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <g:hasErrors bean="${smartsConnection}">
            <div class="errors">
                <g:renderErrors bean="${smartsConnection}" as="list" />
            </div>
            </g:hasErrors>
            <g:form method="post" >
                <input type="hidden" name="id" value="${smartsConnection?.id}" />
                <div class="dialog">
                    <table>
                        <tbody>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="name">Name:</label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean:smartsConnection,field:'name','errors')}">
                                    <input type="text" class="inputtextfield" id="name" name="name" value="${fieldValue(bean:smartsConnection,field:'name')}"/>
                                </td>
                            </tr> 
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="connectionClass">Connection Class:</label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean:smartsConnection,field:'connectionClass','errors')}">
                                    <input type="text" class="inputtextfieldl" id="connectionClass" name="connectionClass" value="${fieldValue(bean:smartsConnection,field:'connectionClass')}"/>
                                </td>
                            </tr> 
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="broker">Broker:</label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean:smartsConnection,field:'broker','errors')}">
                                    <input type="text" class="inputtextfield" id="broker" name="broker" value="${fieldValue(bean:smartsConnection,field:'broker')}"/>
                                </td>
                            </tr> 
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="domain">Domain:</label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean:smartsConnection,field:'domain','errors')}">
                                    <input type="text" class="inputtextfieldl" id="domain" name="domain" value="${fieldValue(bean:smartsConnection,field:'domain')}"/>
                                </td>
                            </tr> 
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="username">Username:</label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean:smartsConnection,field:'username','errors')}">
                                    <input type="text" class="inputtextfield" id="username" name="username" value="${fieldValue(bean:smartsConnection,field:'username')}"/>
                                </td>
                            </tr> 
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="password">Password:</label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean:smartsConnection,field:'userPassword','errors')}">
                                    <input type="password" class="inputtextfield" id="userPassword" name="userPassword" value="${fieldValue(bean:smartsConnection,field:'userPassword')}"/>
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
