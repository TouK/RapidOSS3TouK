
<%@ page import="connection.RapidInsightConnection" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <meta name="layout" content="main" />
        <title>Edit RapidInsightConnection</title>
    </head>
    <body>
        <div class="nav">
            <span class="menuButton"><a class="home" href="${createLinkTo(dir:'admin.gsp')}">Home</a></span>
            <span class="menuButton"><g:link class="list" action="list">RapidInsightConnection List</g:link></span>
            <span class="menuButton"><g:link class="create" action="create">New RapidInsightConnection</g:link></span>
            <span class="menuButton"><a class="logout" href="${createLinkTo(dir: 'auth/logout')}">Logout</a></span>
        </div>
        <div class="body">
            <h1>Edit RapidInsightConnection</h1>
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <g:hasErrors bean="${rapidInsightConnection}">
            <div class="errors">
                <g:renderErrors bean="${rapidInsightConnection}" as="list" />
            </div>
            </g:hasErrors>
            <g:form method="post" >
                <input type="hidden" name="id" value="${rapidInsightConnection?.id}" />
                <div class="dialog">
                    <table>
                        <tbody>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="name">Name:</label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean:rapidInsightConnection,field:'name','errors')}">
                                    <input type="text" id="name" name="name" value="${fieldValue(bean:rapidInsightConnection,field:'name')}"/>
                                </td>
                            </tr> 
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="connectionClass">Connection Class:</label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean:rapidInsightConnection,field:'connectionClass','errors')}">
                                    <input type="text" class="inputtextfield" id="connectionClass" name="connectionClass" value="${fieldValue(bean:rapidInsightConnection,field:'connectionClass')}"/>
                                </td>
                            </tr>

                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="maxNumberOfConnections">Max. Active:</label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean:rapidInsightConnection,field:'maxNumberOfConnections','errors')}">
                                    <input type="text" class="inputtextfield" id="maxNumberOfConnections" name="maxNumberOfConnections" value="${fieldValue(bean:rapidInsightConnection,field:'maxNumberOfConnections')}" />
                                </td>
                            </tr> 
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="baseUrl">Base Url:</label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean:rapidInsightConnection,field:'baseUrl','errors')}">
                                    <input type="text" class="inputtextfieldl" id="baseUrl" name="baseUrl" value="${fieldValue(bean:rapidInsightConnection,field:'baseUrl')}"/>
                                </td>
                            </tr> 
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="username">Username:</label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean:rapidInsightConnection,field:'username','errors')}">
                                    <input type="text" class="inputtextfield" id="username" name="username" value="${fieldValue(bean:rapidInsightConnection,field:'username')}"/>
                                </td>
                            </tr> 
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="password">Password:</label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean:rapidInsightConnection,field:'userPassword','errors')}">
                                    <input type="password" class="inputtextfield" id="userPassword" name="userPassword" value="${fieldValue(bean:rapidInsightConnection,field:'userPassword')}"/>
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
