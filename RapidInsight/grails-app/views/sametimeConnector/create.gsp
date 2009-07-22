<%@ page import="connector.SametimeConnector" %>

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="adminLayout" />
    <title>Create SametimeConnector</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><g:link class="list" action="list">SametimeConnector List</g:link></span>
</div>
<div class="body">
    <h1>Create SametimeConnector</h1>
    <g:render template="/common/messages" model="[flash:flash, beans:[sametimeConnector,sametimeConnection,sametimeDatasource]]"></g:render>
    <g:form action="save" method="post" >
        <div class="dialog">
            <table>
                <tbody>

                     <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="name">Name:</label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean:sametimeConnection,field:'name','errors')}">
                                    <input type="text" class="inputtextfield" id="name" name="name" value="${fieldValue(bean:sametimeConnection,field:'name')}"/>
                                </td>
                            </tr>

                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="host">Host:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:sametimeConnection,field:'host','errors')}">
                            <input type="text" class="inputtextfield" id="host" name="host" value="${fieldValue(bean:sametimeConnection,field:'host')}"/>
                        </td>
                    </tr>

                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="community">Community:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:sametimeConnection,field:'community','errors')}">
                            <input type="text" class="inputtextfield" id="community" name="community" value="${fieldValue(bean:sametimeConnection,field:'community')}"/>
                        </td>
                    </tr>



                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="username">Username:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:sametimeConnection,field:'username','errors')}">
                            <input type="text" class="inputtextfield" id="username" name="username" value="${fieldValue(bean:sametimeConnection,field:'username')}" autocomplete="off" />
                        </td>
                    </tr>

                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="userPassword">Password:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:sametimeConnection,field:'userPassword','errors')}">
                            <input type="text" class="inputtextfield" id="userPassword" name="userPassword" value="${fieldValue(bean:sametimeConnection,field:'userPassword')}" autocomplete="off" />
                        </td>
                    </tr>

                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="minTimeout">Min Timeout:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:sametimeConnection,field:'minTimeout','errors')}">
                            <input type="text" class="inputtextfield" id="minTimeout" name="minTimeout" value="${fieldValue(bean:sametimeConnection,field:'minTimeout')}"/>
                        </td>
                    </tr>

                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="maxTimeout">Max Timeout:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:sametimeConnection,field:'maxTimeout','errors')}">
                            <input type="maxTimeout" class="inputtextfield" id="maxTimeout" name="maxTimeout" value="${fieldValue(bean:sametimeConnection,field:'maxTimeout')}"/>
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
