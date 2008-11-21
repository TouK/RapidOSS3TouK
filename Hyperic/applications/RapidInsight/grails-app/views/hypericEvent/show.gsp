

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Show HypericEvent</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLinkTo(dir:'')}">Home</a></span>
    <span class="menuButton"><g:link class="list" action="list">HypericEvent List</g:link></span>
    <span class="menuButton"><g:link class="create" action="create">New HypericEvent</g:link></span>
</div>
<div class="body">
    <h1>Show HypericEvent</h1>
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
                    
                    <td valign="top" class="value">${hypericEvent.id}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">aid:</td>
                    
                    <td valign="top" class="value">${hypericEvent.aid}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">fixed:</td>
                    
                    <td valign="top" class="value">${hypericEvent.fixed}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">name:</td>
                    
                    <td valign="top" class="value">${hypericEvent.name}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">owner:</td>
                    
                    <td valign="top" class="value"><g:link controller="resource" action="show" id="${hypericEvent?.owner?.id}">${hypericEvent?.owner}</g:link></td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">timestamp:</td>
                    
                    <td valign="top" class="value">${hypericEvent.timestamp}</td>
                    
                </tr>
                
            </tbody>
        </table>
    </div>
    <div class="buttons">
        <g:form>
            <input type="hidden" name="id" value="${hypericEvent?.id}"/>
            <span class="button"><g:actionSubmit class="edit" value="Edit"/></span>
            <span class="button"><g:actionSubmit class="delete" onclick="return confirm('Are you sure?');" value="Delete"/></span>
        </g:form>
    </div>
</div>
</body>
</html>
