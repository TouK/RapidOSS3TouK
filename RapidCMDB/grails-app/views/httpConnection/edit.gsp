
<%@ page import="connection.HttpConnection" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <title>Edit HttpConnection</title>
    </head>
    <body>
        <div class="nav">
            <span class="menuButton"><a class="home" href="${createLinkTo(dir:'admin.gsp')}">Home</a></span>
            <span class="menuButton"><g:link class="list" action="list">HttpConnection List</g:link></span>
            <span class="menuButton"><g:link class="create" action="create">New HttpConnection</g:link></span>
        </div>
        <div class="body">
            <h1>Edit HttpConnection</h1>
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <g:hasErrors bean="${httpConnection}">
            <div class="errors">
                <g:renderErrors bean="${httpConnection}" as="list" />
            </div>
            </g:hasErrors>
            <g:form method="post" >
                <input type="hidden" name="id" value="${httpConnection?.id}" />
                <div class="dialog">
                    <table>
                        <tbody>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="name">Name:</label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean:httpConnection,field:'name','errors')}">
                                    <input type="text" class="inputtextfield" id="name" name="name" value="${fieldValue(bean:httpConnection,field:'name')}"/>
                                </td>
                            </tr> 
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="connectionClass">Connection Class:</label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean:httpConnection,field:'connectionClass','errors')}">
                                    <input type="text" class="inputtextfieldl" id="connectionClass" name="connectionClass" value="${fieldValue(bean:httpConnection,field:'connectionClass')}"/>
                                </td>
                            </tr>

                              <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="maxNumberOfConnections">Max. Active:</label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean:httpConnection,field:'maxNumberOfConnections','errors')}">
                                    <input type="text" class="inputtextfield" id="maxNumberOfConnections" name="maxNumberOfConnections" value="${fieldValue(bean:httpConnection,field:'maxNumberOfConnections')}" />
                                </td>
                            </tr> 
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="baseUrl">Base Url:</label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean:httpConnection,field:'baseUrl','errors')}">
                                    <input type="text" class="inputtextfieldl" id="baseUrl" name="baseUrl" value="${fieldValue(bean:httpConnection,field:'baseUrl')}"/>
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
