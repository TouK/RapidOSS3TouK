

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Show EdgeNode</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLinkTo(dir:'')}">Home</a></span>
    <span class="menuButton"><g:link class="list" action="list">EdgeNode List</g:link></span>
    <span class="menuButton"><g:link class="create" action="create">New EdgeNode</g:link></span>
</div>
<div class="body">
    <h1>Show EdgeNode</h1>
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
                    
                    <td valign="top" class="value">${edgeNode.id}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">from:</td>
                    
                    <td valign="top" class="value">${edgeNode.from}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">mapName:</td>
                    
                    <td valign="top" class="value">${edgeNode.mapName}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">to:</td>
                    
                    <td valign="top" class="value">${edgeNode.to}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">username:</td>
                    
                    <td valign="top" class="value">${edgeNode.username}</td>
                    
                </tr>
                
            </tbody>
        </table>
    </div>
    <div class="buttons">
        <g:form>
            <input type="hidden" name="id" value="${edgeNode?.id}"/>
            <span class="button"><g:actionSubmit class="edit" value="Edit"/></span>
            <span class="button"><g:actionSubmit class="delete" onclick="return confirm('Are you sure?');" value="Delete"/></span>
        </g:form>
    </div>
</div>
</body>
</html>
