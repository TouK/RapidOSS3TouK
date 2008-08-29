
<%@ page import="connection.SmartsConnectionTemplate" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <title>Edit SmartsConnectionTemplate</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLinkTo(dir:'/admin.gsp')}">Home</a></span>
    <span class="menuButton"><g:link class="list" action="list">Smarts Connection Configuration Data List</g:link></span>
    <span class="menuButton"><g:link class="create" action="create">New Smarts Connection Configuration Data</g:link></span>
</div>
<div class="body">
    <h1>Edit Smarts Connection Configuration Data</h1>
    <g:if test="${flash.message}">
        <div class="message">${flash.message}</div>
    </g:if>
    <g:hasErrors bean="${smartsConnectionTemplate}">
        <div class="errors">
            <g:renderErrors bean="${smartsConnectionTemplate}" as="list"/>
        </div>
    </g:hasErrors>
    <g:hasErrors bean="${flash.errors}">
       <div class="errors">
            <g:renderErrors bean="${flash.errors}"/>
        </div>
    </g:hasErrors>
    <g:form method="post" >
        <input type="hidden" name="id" value="${smartsConnectionTemplate?.id}"/>
        <div class="dialog">
            <table>
                <tbody>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="name">Name:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsConnectionTemplate,field:'name','errors')}">
                            <input type="text" id="name" name="name" value="${fieldValue(bean:smartsConnectionTemplate,field:'name')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="broker">Broker:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsConnectionTemplate,field:'broker','errors')}">
                            <input type="text" id="broker" name="broker" value="${fieldValue(bean:smartsConnectionTemplate,field:'broker')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="brokerPassword">Broker Password:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsConnectionTemplate,field:'brokerPassword','errors')}">
                            <input type="text" id="brokerPassword" name="brokerPassword" value="${fieldValue(bean:smartsConnectionTemplate,field:'brokerPassword')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="brokerUsername">Broker Username:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsConnectionTemplate,field:'brokerUsername','errors')}">
                            <input type="text" id="brokerUsername" name="brokerUsername" value="${fieldValue(bean:smartsConnectionTemplate,field:'brokerUsername')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="username">Username:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsConnectionTemplate,field:'username','errors')}">
                            <input type="text" id="username" name="username" value="${fieldValue(bean:smartsConnectionTemplate,field:'username')}"/>
                        </td>
                    </tr>

                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="password">Password:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsConnectionTemplate,field:'password','errors')}">
                            <input type="text" id="password" name="password" value="${fieldValue(bean:smartsConnectionTemplate,field:'password')}"/>
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
