
<%@ page import="connection.SmartsConnection" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <meta name="layout" content="main" />
        <title>Create SmartsConnection</title>         
    </head>
    <body>
        <div class="nav">
            <span class="menuButton"><a class="home" href="${createLinkTo(dir:'')}">Home</a></span>
            <span class="menuButton"><g:link class="list" action="list">SmartsConnection List</g:link></span>
        </div>
        <div class="body">
            <h1>Create SmartsConnection</h1>
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <g:hasErrors bean="${smartsConnection}">
            <div class="errors">
                <g:renderErrors bean="${smartsConnection}" as="list" />
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
                                <td valign="top" class="value ${hasErrors(bean:smartsConnection,field:'name','errors')}">
                                    <input type="text" id="name" name="name" value="${fieldValue(bean:smartsConnection,field:'name')}"/>
                                </td>
                            </tr> 
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="connectionClass">Connection Class:</label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean:smartsConnection,field:'connectionClass','errors')}">
                                    <input type="text" id="connectionClass" name="connectionClass" value="${fieldValue(bean:smartsConnection,field:'connectionClass')}"/>
                                </td>
                            </tr> 
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="broker">Broker:</label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean:smartsConnection,field:'broker','errors')}">
                                    <input type="text" id="broker" name="broker" value="${fieldValue(bean:smartsConnection,field:'broker')}"/>
                                </td>
                            </tr> 
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="domain">Domain:</label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean:smartsConnection,field:'domain','errors')}">
                                    <input type="text" id="domain" name="domain" value="${fieldValue(bean:smartsConnection,field:'domain')}"/>
                                </td>
                            </tr> 
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="username">Username:</label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean:smartsConnection,field:'username','errors')}">
                                    <input type="text" id="username" name="username" value="${fieldValue(bean:smartsConnection,field:'username')}"/>
                                </td>
                            </tr> 
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="password">Password:</label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean:smartsConnection,field:'password','errors')}">
                                    <input type="text" id="password" name="password" value="${fieldValue(bean:smartsConnection,field:'password')}"/>
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
