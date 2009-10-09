
<%@ page import="connection.RapidInsightConnection" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <meta name="layout" content="adminLayout" />
        <title>Edit RapidInsightConnection</title>
    </head>
    <body>
        <div class="nav">
            <span class="menuButton"><a class="home" href="${createLinkTo(dir:'admin.gsp')}">Home</a></span>
            <span class="menuButton"><g:link class="list" action="list">RapidInsightConnection List</g:link></span>
            <span class="menuButton"><g:link class="create" action="create">New RapidInsightConnection</g:link></span>
        </div>
        <div class="body">
            <h1>Edit RapidInsightConnection</h1>
            <g:render template="/common/messages" model="[flash:flash, beans:[rapidInsightConnection]]"></g:render>
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
                                    <input type="text" class="inputtextfield" id="username" name="username" value="${fieldValue(bean:rapidInsightConnection,field:'username')}" autocomplete="off" />
                                </td>
                            </tr> 
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="password">Password:</label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean:rapidInsightConnection,field:'userPassword','errors')}">
                                    <input type="text" class="inputtextfield" id="userPassword" name="userPassword" value="${fieldValue(bean:rapidInsightConnection,field:'userPassword')}" autocomplete="off" />
                                </td>
                            </tr>
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="minTimeout">Min Timeout:</label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean:rapidInsightConnection,field:'minTimeout','errors')}">
                                    <input type="text" class="inputtextfield" id="minTimeout" name="minTimeout" value="${fieldValue(bean:rapidInsightConnection,field:'minTimeout')}"/>
                                </td>
                            </tr>

                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="maxTimeout">Max Timeout:</label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean:rapidInsightConnection,field:'maxTimeout','errors')}">
                                    <input type="maxTimeout" class="inputtextfield" id="maxTimeout" name="maxTimeout" value="${fieldValue(bean:rapidInsightConnection,field:'maxTimeout')}"/>
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
