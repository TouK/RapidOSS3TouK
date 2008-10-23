<%@ page import="connector.SmartsListeningNotificationConnector; connector.SmartsListeningTopologyConnector" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <title>Edit SmartsConnector</title>
    <script>
        function render(){
            typeChanged();
        }
        function typeChanged(){
            var notificationList = document.getElementById('notificationListRow');
            var tailMode = document.getElementById('tailModeRow');
            var typeSelect = document.getElementById("type");
            var connectortype = typeSelect.options[typeSelect.selectedIndex].value;
            notificationList.style.display = (connectortype == "Notification"? "":"none");
            tailMode.style.display = (connectortype == "Notification"? "":"none");
        }

    </script>
</head>
<body onload="render()">
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLinkTo(dir:'/admin.gsp')}">Home</a></span>
    <span class="menuButton"><g:link class="list" action="list">SmartsConnector List</g:link></span>
    <span class="menuButton"><g:link class="create" action="create">New SmartsConnector</g:link></span>
</div>
<div class="body">
    <h1>Edit SmartsConnector</h1>
    <g:if test="${flash.message}">
        <div class="message">${flash.message}</div>
    </g:if>
    <g:hasErrors bean="${smartsConnector}">
        <div class="errors">
            <g:renderErrors bean="${smartsConnector}" as="list"/>
        </div>
    </g:hasErrors>
    <g:hasErrors bean="${flash.errors}">
       <div class="errors">
            <g:renderErrors bean="${flash.errors}"/>
        </div>
    </g:hasErrors>
    <g:form method="post" >
        <input type="hidden" name="id" value="${smartsConnector?.id}"/>
        <div class="dialog">
            <table>
                <tbody>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="name">Name:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsConnector,field:'name','errors')}">
                            <input type="text" id="name" name="name" value="${fieldValue(bean:smartsConnector,field:'name')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="connectionTemplate">Smarts Connection Configuration Data:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsConnector,field:'connectionTemplate','errors')}">
                            <g:select optionKey="id" from="${connection.SmartsConnectionTemplate.list()}" name="connectionTemplate.id" value="${smartsConnector?.connectionTemplate?.id}" ></g:select>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="domain">Domain Name:</label>
                        </td>
                        <td valign="top" class="value">
                            <input type="text" id="domain" name="domain" value="${smartsConnector?.ds?.connection?.domain}"/>
                        </td>
                    </tr>

                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="domainType">Domain Type:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsConnector?.ds?.connection,field:'domainType','errors')}">
                            <g:select id="domainType" name="domainType" from="${smartsConnector?.ds?.connection?.constraints.domainType.inList.collect{it.encodeAsHTML()}}" value="${fieldValue(bean:smartsConnector?.ds?.connection,field:'domainType')}" ></g:select>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="logLevel">Log Level:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsConnector?.ds?.listeningScript,field:'logLevel','errors')}">
                            <g:select id="logLevel" name="logLevel" from="${smartsConnector?.ds?.listeningScript?.constraints.logLevel.inList.collect{it.encodeAsHTML()}}" value="${fieldValue(bean:smartsConnector?.ds?.listeningScript,field:'logLevel')}" ></g:select>
                        </td>
                    </tr>

                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="reconnectInterval">Reconnect Interval:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsConnector,field:'reconnectInterval','errors')}">
                            <input type="text" class="inputtextfield" id="reconnectInterval" name="reconnectInterval" value="${fieldValue(bean:smartsConnector,field:'reconnectInterval')}" /> sec.
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="type">Type:</label>
                        </td>
                        <td valign="top" class="value">
                            <g:select class="inputtextfield" id="type" name="type" from="${['Topology', 'Notification']}"
                                    value="${smartsConnector instanceof SmartsListeningTopologyConnector?'Topology':'Notification'}" onchange="typeChanged()"></g:select>
                        </td>
                    </tr>

                    <%
                        def isNotficationConnector = smartsConnector instanceof SmartsListeningNotificationConnector;
                    %>
                    <tr class="prop" id="notificationListRow">
                        <td valign="top" class="name">
                            <label for="notificationList">Notification List:</label>
                        </td>
                        <td valign="top" class="value ${isNotficationConnector?hasErrors(bean:smartsConnector,field:'notificationList','errors'):''}">
                            <input type="text" id="notificationList" name="notificationList" value="${isNotficationConnector?fieldValue(bean:smartsConnector,field:'notificationList'):''}"/>
                        </td>
                    </tr>

                    <tr class="prop" id="tailModeRow">
                        <td valign="top" class="name">
                            <label for="tailMode">Tail Mode:</label>
                        </td>
                        <td valign="top" class="value ${isNotficationConnector?hasErrors(bean:smartsConnector,field:'tailMode','errors'):''}">
                            <g:checkBox name="tailMode" value="${isNotficationConnector?smartsConnector?.tailMode:''}" ></g:checkBox>
                        </td>
                    </tr>
                    
                </tbody>
            </table>
        </div>
        <div class="buttons">
            <span class="button"><g:actionSubmit class="save" value="Update"/></span>
            <span class="button"><g:actionSubmit class="delete" onclick="return confirm('Are you sure?');" value="Delete"/></span>
        </div>
    </g:form>
</div>
</body>
</html>
