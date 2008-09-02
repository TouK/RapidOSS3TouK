

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Show MapNode</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLinkTo(dir:'')}">Home</a></span>
    <span class="menuButton"><g:link class="list" action="list">MapNode List</g:link></span>
    <span class="menuButton"><g:link class="create" action="create">New MapNode</g:link></span>
</div>
<div class="body">
    <h1>Show MapNode</h1>
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
                    
                    <td valign="top" class="value">${mapNode.id}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">mapName:</td>
                    
                    <td valign="top" class="value">${mapNode.mapName}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">nodeIdentifier:</td>
                    
                    <td valign="top" class="value">${mapNode.nodeIdentifier}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">username:</td>
                    
                    <td valign="top" class="value">${mapNode.username}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">belongsToMap:</td>
                    
                    <td valign="top" class="value"><g:link controller="map" action="show" id="${mapNode?.belongsToMap?.id}">${mapNode?.belongsToMap}</g:link></td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">xlocation:</td>
                    
                    <td valign="top" class="value">${mapNode.xlocation}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">ylocation:</td>
                    
                    <td valign="top" class="value">${mapNode.ylocation}</td>
                    
                </tr>
                
            </tbody>
        </table>
    </div>
    <div class="buttons">
        <g:form>
            <input type="hidden" name="id" value="${mapNode?.id}"/>
            <span class="button"><g:actionSubmit class="edit" value="Edit"/></span>
            <span class="button"><g:actionSubmit class="delete" onclick="return confirm('Are you sure?');" value="Delete"/></span>
        </g:form>
    </div>
</div>
</body>
</html>
