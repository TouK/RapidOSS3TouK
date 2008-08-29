
<%@ page import="connection.SmartsConnectionTemplate" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <title>Show SmartsConnectionTemplate</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLinkTo(dir:'/admin.gsp')}">Home</a></span>
    <span class="menuButton"><g:link class="list" action="list">Smarts Connection Configuration Data List</g:link></span>
    <span class="menuButton"><g:link class="create" action="create">New Smarts Connection Configuration Data</g:link></span>
</div>
<div class="body">
    <h1>Show Smarts Connection Configuration Data</h1>
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
                    
                    <td valign="top" class="value">${smartsConnectionTemplate.id}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">Name:</td>
                    
                    <td valign="top" class="value">${smartsConnectionTemplate.name}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">Broker:</td>
                    
                    <td valign="top" class="value">${smartsConnectionTemplate.broker}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">Broker Password:</td>
                    
                    <td valign="top" class="value">${smartsConnectionTemplate.brokerPassword}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">Broker Username:</td>

                    <td valign="top" class="value">${smartsConnectionTemplate.brokerUsername}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">Password:</td>
                    
                    <td valign="top" class="value">${smartsConnectionTemplate.password}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">Username:</td>
                    
                    <td valign="top" class="value">${smartsConnectionTemplate.username}</td>
                    
                </tr>
                
            </tbody>
        </table>
    </div>
    <div class="buttons">
        <g:form>
            <input type="hidden" name="id" value="${smartsConnectionTemplate?.id}"/>
            <span class="button"><g:actionSubmit class="edit" value="Edit"/></span>
            <span class="button"><g:actionSubmit class="delete" onclick="return confirm('Are you sure?');" value="Delete"/></span>
        </g:form>
    </div>
</div>
</body>
</html>
