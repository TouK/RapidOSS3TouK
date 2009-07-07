
<%@ page import="connection.JabberConnection" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <meta name="layout" content="adminLayout" />
        <title>Create JabberConnection</title>
    </head>
    <body>
        <div class="nav">
            <span class="menuButton"><a class="home" href="${createLinkTo(dir:'admin.gsp')}">Home</a></span>
            <span class="menuButton"><g:link class="list" action="list">JabberConnection List</g:link></span>
        </div>
        <div class="body">
            <h1>Create JabberConnection</h1>
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <g:hasErrors bean="${jabberConnection}">
            <div class="errors">
                <g:renderErrors bean="${jabberConnection}" as="list" />
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
                                <td valign="top" class="value ${hasErrors(bean:jabberConnection,field:'name','errors')}">
                                    <input type="text" class="inputtextfield" id="name" name="name" value="${fieldValue(bean:jabberConnection,field:'name')}"/>
                                </td>
                            </tr>

                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="port">Port:</label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean:jabberConnection,field:'port','errors')}">
                                    <input type="text" class="inputtextfield" id="port" name="port" value="${fieldValue(bean:jabberConnection,field:'port')}"/>
                                </td>
                            </tr>

                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="host">Host:</label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean:jabberConnection,field:'host','errors')}">
                                    <input type="text" class="inputtextfield" id="host" name="host" value="${fieldValue(bean:jabberConnection,field:'host')}"/>
                                </td>
                            </tr> 

                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="serviceName">Service Name:</label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean:jabberConnection,field:'serviceName','errors')}">
                                    <input class="inputtextfield" id="serviceName" name="serviceName" value="${fieldValue(bean:jabberConnection,field:'serviceName')}"/>
                                </td>
                            </tr>

                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="username">Username:</label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean:jabberConnection,field:'username','errors')}">
                                    <input type="text" class="inputtextfield" id="username" name="username" value="${fieldValue(bean:jabberConnection,field:'username')}" autocomplete="off" />
                                </td>
                            </tr>

                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="userPassword">Password:</label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean:jabberConnection,field:'userPassword','errors')}">
                                    <input type="text" class="inputtextfield" id="userPassword" name="userPassword" value="${fieldValue(bean:jabberConnection,field:'userPassword')}" autocomplete="off" />
                                </td>
                            </tr>

                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="minTimeout">Min Timeout:</label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean:jabberConnection,field:'minTimeout','errors')}">
                                    <input type="text" class="inputtextfield" id="minTimeout" name="minTimeout" value="${fieldValue(bean:jabberConnection,field:'minTimeout')}"/>
                                </td>
                            </tr>

                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="maxTimeout">Max Timeout:</label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean:jabberConnection,field:'maxTimeout','errors')}">
                                    <input type="maxTimeout" class="inputtextfield" id="maxTimeout" name="maxTimeout" value="${fieldValue(bean:jabberConnection,field:'maxTimeout')}"/>
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
