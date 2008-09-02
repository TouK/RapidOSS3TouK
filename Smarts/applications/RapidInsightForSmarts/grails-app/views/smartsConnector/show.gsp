<%@ page import="connector.SmartsListeningNotificationConnector" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <title>Show SmartsConnector</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLinkTo(dir:'/admin.gsp')}">Home</a></span>
    <span class="menuButton"><g:link class="list" action="list">SmartsConnector List</g:link></span>
    <span class="menuButton"><g:link class="create" action="create">New SmartsConnector</g:link></span>
</div>
<div class="body">
    <h1>Show SmartsConnector</h1>
    <g:if test="${flash.message}">
        <div class="message">${flash.message}</div>
    </g:if>
    <g:hasErrors bean="${flash.errors}">
       <div class="errors">
            <g:renderErrors bean="${flash.errors}"/>
        </div>
    </g:hasErrors>
    <div class="dialog">
        <table>
            <tbody>

                
                <tr class="prop">
                    <td valign="top" class="name">Id:</td>
                    
                    <td valign="top" class="value">${smartsConnector.id}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">Name:</td>
                    
                    <td valign="top" class="value">${smartsConnector.name}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">Smarts Connection Configuration Data:</td>
                    
                    <td valign="top" class="value"><g:link controller="smartsConnectionTemplate" action="show" id="${smartsConnector?.connectionTemplate?.id}">${smartsConnector?.connectionTemplate}</g:link></td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">Domain Name:</td>
                    
                    <td valign="top" class="value">${smartsConnector?.ds?.connection?.domain}</td>
                    
                </tr>

                <tr class="prop">
                    <td valign="top" class="name">Domain Type:</td>

                    <td valign="top" class="value">${smartsConnector?.ds?.connection?.domainType}</td>

                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">Log Level:</td>
                    
                    <td valign="top" class="value">${smartsConnector.logLevel}</td>
                    
                </tr>

                <%
                        if (smartsConnector instanceof SmartsListeningNotificationConnector) {

                %>
                    <tr class="prop">
                        <td valign="top" class="name">Notification List:</td>

                        <td valign="top" class="value">${smartsConnector.notificationList}</td>

                    </tr>
                    <tr class="prop">
                        <td valign="top" class="name">Tails Mode:</td>

                        <td valign="top" class="value">${smartsConnector.tailMode}</td>

                    </tr>
                <%
                    }
                %>
                
            </tbody>
        </table>
    </div>
    <div class="buttons">
        <g:form>
            <input type="hidden" name="id" value="${smartsConnector?.id}"/>
            <span class="button"><g:actionSubmit class="edit" value="Edit"/></span>
            <span class="button"><g:actionSubmit class="delete" onclick="return confirm('Are you sure?');" value="Delete"/></span>
        </g:form>
    </div>
</div>
</body>
</html>
