
<%@ page import="connection.TcpListeningConnection" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <meta name="layout" content="adminLayout" />
        <title>Edit TcpListeningConnection</title>
    </head>
    <body>
        <div class="nav">
            <span class="menuButton"><a class="home" href="${createLinkTo(dir:'admin.gsp')}">Home</a></span>
            <span class="menuButton"><g:link class="list" action="list">TcpListeningConnection List</g:link></span>
            <span class="menuButton"><g:link class="create" action="create">New TcpListeningConnection</g:link></span>
        </div>
        <div class="body">
            <h1>Edit TcpListeningConnection</h1>
            <g:render template="/common/messages" model="[flash:flash, beans:[tcpListeningConnection]]"></g:render>
            <g:form method="post" >
                <input type="hidden" name="id" value="${tcpListeningConnection?.id}" />
                <div class="dialog">
                    <table>
                        <tbody>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="name">Name:</label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean:tcpListeningConnection,field:'name','errors')}">
                                    <input type="text" class="inputtextfield" id="name" name="name" value="${fieldValue(bean:tcpListeningConnection,field:'name')}"/>
                                </td>
                            </tr>

                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="host">Host:</label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean:tcpListeningConnection,field:'host','errors')}">
                                    <input type="text" class="inputtextfield" id="host" name="host" value="${fieldValue(bean:tcpListeningConnection,field:'host')}"/>
                                </td>
                            </tr> 
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="port">Port:</label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean:tcpListeningConnection,field:'port','errors')}">
                                    <input type="text" class="inputtextfield" id="port" name="port" value="${fieldValue(bean:tcpListeningConnection,field:'port')}" />
                                </td>
                            </tr>
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="minTimeout">Min Timeout:</label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean:tcpListeningConnection,field:'minTimeout','errors')}">
                                    <input type="text" class="inputtextfield" id="minTimeout" name="minTimeout" value="${fieldValue(bean:tcpListeningConnection,field:'minTimeout')}"/>
                                </td>
                            </tr>

                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="maxTimeout">Max Timeout:</label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean:tcpListeningConnection,field:'maxTimeout','errors')}">
                                    <input type="maxTimeout" class="inputtextfield" id="maxTimeout" name="maxTimeout" value="${fieldValue(bean:tcpListeningConnection,field:'maxTimeout')}"/>
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
