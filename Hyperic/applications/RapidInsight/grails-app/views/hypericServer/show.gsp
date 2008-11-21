

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Show HypericServer</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLinkTo(dir:'')}">Home</a></span>
    <span class="menuButton"><g:link class="list" action="list">HypericServer List</g:link></span>
    <span class="menuButton"><g:link class="create" action="create">New HypericServer</g:link></span>
</div>
<div class="body">
    <h1>Show HypericServer</h1>
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
                    <td valign="top" class="name">id:</td>
                    
                    <td valign="top" class="value">${hypericServer.id}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">username:</td>
                    
                    <td valign="top" class="value">${hypericServer.username}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">event_timestamp:</td>
                    
                    <td valign="top" class="value">${hypericServer.event_timestamp}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">password:</td>
                    
                    <td valign="top" class="value">${hypericServer.password}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">relation_timestamp:</td>
                    
                    <td valign="top" class="value">${hypericServer.relation_timestamp}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">status_timestamp:</td>
                    
                    <td valign="top" class="value">${hypericServer.status_timestamp}</td>
                    
                </tr>
                
            </tbody>
        </table>
    </div>
    <div class="buttons">
        <g:form>
            <input type="hidden" name="id" value="${hypericServer?.id}"/>
            <span class="button"><g:actionSubmit class="edit" value="Edit"/></span>
            <span class="button"><g:actionSubmit class="delete" onclick="return confirm('Are you sure?');" value="Delete"/></span>
        </g:form>
    </div>
</div>
</body>
</html>
