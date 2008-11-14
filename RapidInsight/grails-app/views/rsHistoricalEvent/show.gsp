

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Show RsHistoricalEvent</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLinkTo(dir:'')}">Home</a></span>
    <span class="menuButton"><g:link class="list" action="list">RsHistoricalEvent List</g:link></span>
    <span class="menuButton"><g:link class="create" action="create">New RsHistoricalEvent</g:link></span>
</div>
<div class="body">
    <h1>Show RsHistoricalEvent</h1>
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
                    
                    <td valign="top" class="value">${rsHistoricalEvent.id}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">acknowledged:</td>
                    
                    <td valign="top" class="value">${rsHistoricalEvent.acknowledged}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">active:</td>
                    
                    <td valign="top" class="value">${rsHistoricalEvent.active}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">firstNotifiedAt:</td>
                    
                    <td valign="top" class="value">${rsHistoricalEvent.firstNotifiedAt}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">lastChangedAt:</td>
                    
                    <td valign="top" class="value">${rsHistoricalEvent.lastChangedAt}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">lastClearedAt:</td>
                    
                    <td valign="top" class="value">${rsHistoricalEvent.lastClearedAt}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">lastNotifiedAt:</td>
                    
                    <td valign="top" class="value">${rsHistoricalEvent.lastNotifiedAt}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">name:</td>
                    
                    <td valign="top" class="value">${rsHistoricalEvent.name}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">owner:</td>
                    
                    <td valign="top" class="value">${rsHistoricalEvent.owner}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">rsDatasource:</td>
                    
                    <td valign="top" class="value">${rsHistoricalEvent.rsDatasource}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">severity:</td>
                    
                    <td valign="top" class="value">${rsHistoricalEvent.severity}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">source:</td>
                    
                    <td valign="top" class="value">${rsHistoricalEvent.source}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">state:</td>
                    
                    <td valign="top" class="value">${rsHistoricalEvent.state}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">willExpireAt:</td>
                    
                    <td valign="top" class="value">${rsHistoricalEvent.willExpireAt}</td>
                    
                </tr>
                
            </tbody>
        </table>
    </div>
    <div class="buttons">
        <g:form>
            <input type="hidden" name="id" value="${rsHistoricalEvent?.id}"/>
            <span class="button"><g:actionSubmit class="edit" value="Edit"/></span>
            <span class="button"><g:actionSubmit class="delete" onclick="return confirm('Are you sure?');" value="Delete"/></span>
        </g:form>
    </div>
</div>
</body>
</html>
