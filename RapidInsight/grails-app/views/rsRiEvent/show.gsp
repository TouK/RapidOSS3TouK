

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Show RsRiEvent</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLinkTo(dir:'')}">Home</a></span>
    <span class="menuButton"><g:link class="list" action="list">RsRiEvent List</g:link></span>
    <span class="menuButton"><g:link class="create" action="create">New RsRiEvent</g:link></span>
</div>
<div class="body">
    <h1>Show RsRiEvent</h1>
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
                    
                    <td valign="top" class="value">${rsRiEvent.id}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">name:</td>
                    
                    <td valign="top" class="value">${rsRiEvent.name}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">acknowledged:</td>
                    
                    <td valign="top" class="value">${rsRiEvent.acknowledged}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">changedAt:</td>
                    
                    <td valign="top" class="value">${rsRiEvent.changedAt}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">clearedAt:</td>
                    
                    <td valign="top" class="value">${rsRiEvent.clearedAt}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">count:</td>
                    
                    <td valign="top" class="value">${rsRiEvent.count}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">createdAt:</td>
                    
                    <td valign="top" class="value">${rsRiEvent.createdAt}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">description:</td>
                    
                    <td valign="top" class="value">${rsRiEvent.description}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">elementId:</td>
                    
                    <td valign="top" class="value">${rsRiEvent.elementId}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">elementName:</td>
                    
                    <td valign="top" class="value">${rsRiEvent.elementName}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">identifier:</td>
                    
                    <td valign="top" class="value">${rsRiEvent.identifier}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">owner:</td>
                    
                    <td valign="top" class="value">${rsRiEvent.owner}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">rsDatasource:</td>
                    
                    <td valign="top" class="value">${rsRiEvent.rsDatasource}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">severity:</td>
                    
                    <td valign="top" class="value">${rsRiEvent.severity}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">source:</td>
                    
                    <td valign="top" class="value">${rsRiEvent.source}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">state:</td>
                    
                    <td valign="top" class="value">${rsRiEvent.state}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">willExpireAt:</td>
                    
                    <td valign="top" class="value">${rsRiEvent.willExpireAt}</td>
                    
                </tr>
                
            </tbody>
        </table>
    </div>
    <div class="buttons">
        <g:form>
            <input type="hidden" name="id" value="${rsRiEvent?.id}"/>
            <span class="button"><g:actionSubmit class="edit" value="Edit"/></span>
            <span class="button"><g:actionSubmit class="delete" onclick="return confirm('Are you sure?');" value="Delete"/></span>
        </g:form>
    </div>
</div>
</body>
</html>
