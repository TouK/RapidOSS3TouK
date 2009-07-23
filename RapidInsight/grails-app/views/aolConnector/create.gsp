<%@ page import="connector.AolConnector" %>

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="adminLayout" />
    <title>Create AolConnector</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><g:link class="list" action="list">AolConnector List</g:link></span>
</div>
<div class="body">
    <h1>Create AolConnector</h1>
    <g:render template="/common/messages" model="[flash:flash, beans:[aolConnector,aolConnection,aolDatasource]]"></g:render>
    <g:form action="save" method="post" >
        <div class="dialog">
            <table>
                <tbody>

                     <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="name">Name:</label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean:aolConnection,field:'name','errors')}">
                                    <input type="text" class="inputtextfield" id="name" name="name" value="${fieldValue(bean:aolConnection,field:'name')}"/>
                                </td>
                            </tr>

                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="host">Host:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:aolConnection,field:'host','errors')}">
                            <input type="text" class="inputtextfield" id="host" name="host" value="${fieldValue(bean:aolConnection,field:'host')}"/>
                        </td>
                    </tr>

                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="port">Port:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:aolConnection,field:'port','errors')}">
                            <input type="text" class="inputtextfield" id="port" name="port" value="${fieldValue(bean:aolConnection,field:'port')}"/>
                        </td>
                    </tr>

                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="username">Username:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:aolConnection,field:'username','errors')}">
                            <input type="text" class="inputtextfield" id="username" name="username" value="${fieldValue(bean:aolConnection,field:'username')}" autocomplete="off" />
                        </td>
                    </tr>

                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="userPassword">Password:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:aolConnection,field:'userPassword','errors')}">
                            <input type="text" class="inputtextfield" id="userPassword" name="userPassword" value="${fieldValue(bean:aolConnection,field:'userPassword')}" autocomplete="off" />
                        </td>
                    </tr>

                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="minTimeout">Min Timeout:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:aolConnection,field:'minTimeout','errors')}">
                            <input type="text" class="inputtextfield" id="minTimeout" name="minTimeout" value="${fieldValue(bean:aolConnection,field:'minTimeout')}"/>
                        </td>
                    </tr>

                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="maxTimeout">Max Timeout:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:aolConnection,field:'maxTimeout','errors')}">
                            <input type="maxTimeout" class="inputtextfield" id="maxTimeout" name="maxTimeout" value="${fieldValue(bean:aolConnection,field:'maxTimeout')}"/>
                        </td>
                    </tr>

                </tbody>
            </table>
        </div>
        <div class="buttons">
            <span class="button"><input class="save" type="submit" value="Create"/></span>
        </div>
    </g:form>
</div>
</body>
</html>
