<%@ page import="connection.EmailConnection" %>

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="adminLayout"/>
    <title>Show EmailConnection</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLinkTo(dir:'')}">Home</a></span>
    <span class="menuButton"><g:link class="list" action="list">EmailConnection List</g:link></span>
    <span class="menuButton"><g:link class="create" action="create">New EmailConnection</g:link></span>
</div>
<div class="body">
    <h1>Show EmailConnection</h1>
    <g:render template="/common/messages" model="[flash:flash]"></g:render>
    <div class="dialog">
        <table>
            <tbody>

                

                 <tr class="prop">
                    <td valign="top" class="name">Name:</td>

                    <td valign="top" class="value">${emailConnection.name}</td>

                </tr>
                

                
                <tr class="prop">
                    <td valign="top" class="name">smtpHost:</td>
                    
                    <td valign="top" class="value">${emailConnection.smtpHost}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">smtpPort:</td>
                    
                    <td valign="top" class="value">${emailConnection.smtpPort}</td>
                    
                </tr>
                <tr class="prop">
                    <td valign="top" class="name">Protocol:</td>

                    <td valign="top" class="value">${emailConnection.protocol}</td>

                </tr>

                 <tr class="prop">
                    <td valign="top" class="name">Username:</td>

                    <td valign="top" class="value">${emailConnection.username}</td>

                </tr>

                <tr class="prop">
                    <td valign="top" class="name">Password:</td>
                    
                    <td valign="top" class="value">${emailConnection.userPassword}</td>
                    
                </tr>
                


                <tr class="prop">
                    <td valign="top" class="name">Min Timeout:</td>

                    <td valign="top" class="value">${emailConnection?.minTimeout}</td>

                </tr>
                <tr class="prop">
                    <td valign="top" class="name">Max Timeout:</td>

                    <td valign="top" class="value">${emailConnection?.maxTimeout}</td>

                </tr>
                
            </tbody>
        </table>
    </div>
    <div class="buttons">
        <g:form>
            <input type="hidden" name="id" value="${emailConnection?.id}"/>
            <span class="button"><g:actionSubmit class="edit" value="Edit"/></span>
            <span class="button"><g:actionSubmit class="delete" onclick="return confirm('Are you sure?');" value="Delete"/></span>
        </g:form>
    </div>
</div>
</body>
</html>
