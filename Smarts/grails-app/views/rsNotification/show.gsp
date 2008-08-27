

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Show RsEvent</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLinkTo(dir:'')}">Home</a></span>
    <span class="menuButton"><g:link class="list" action="list">RsEvent List</g:link></span>
    <span class="menuButton"><g:link class="create" action="create">New RsEvent</g:link></span>
</div>
<div class="body">
    <h1>Show RsEvent</h1>
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
                    
                    <td valign="top" class="value">${rsNotification.id}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">className:</td>
                    
                    <td valign="top" class="value">${rsNotification.className}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">eventName:</td>
                    
                    <td valign="top" class="value">${rsNotification.eventName}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">instanceName:</td>
                    
                    <td valign="top" class="value">${rsNotification.instanceName}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">acknowledged:</td>
                    
                    <td valign="top" class="value">${rsNotification.acknowledged}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">active:</td>
                    
                    <td valign="top" class="value">${rsNotification.active}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">category:</td>
                    
                    <td valign="top" class="value">${rsNotification.category}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">certainty:</td>
                    
                    <td valign="top" class="value">${rsNotification.certainty}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">classDisplayName:</td>
                    
                    <td valign="top" class="value">${rsNotification.classDisplayName}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">creationClassName:</td>
                    
                    <td valign="top" class="value">${rsNotification.creationClassName}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">description:</td>
                    
                    <td valign="top" class="value">${rsNotification.description}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">displayName:</td>
                    
                    <td valign="top" class="value">${rsNotification.displayName}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">elementClassName:</td>
                    
                    <td valign="top" class="value">${rsNotification.elementClassName}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">elementName:</td>
                    
                    <td valign="top" class="value">${rsNotification.elementName}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">eventDisplayName:</td>
                    
                    <td valign="top" class="value">${rsNotification.eventDisplayName}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">eventState:</td>
                    
                    <td valign="top" class="value">${rsNotification.eventState}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">eventText:</td>
                    
                    <td valign="top" class="value">${rsNotification.eventText}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">eventType:</td>
                    
                    <td valign="top" class="value">${rsNotification.eventType}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">firstNotifiedAt:</td>
                    
                    <td valign="top" class="value">${rsNotification.firstNotifiedAt}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">impact:</td>
                    
                    <td valign="top" class="value">${rsNotification.impact}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">inMaintenance:</td>
                    
                    <td valign="top" class="value">${rsNotification.inMaintenance}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">instanceDisplayName:</td>
                    
                    <td valign="top" class="value">${rsNotification.instanceDisplayName}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">isRoot:</td>
                    
                    <td valign="top" class="value">${rsNotification.isRoot}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">lastChangedAt:</td>
                    
                    <td valign="top" class="value">${rsNotification.lastChangedAt}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">lastClearedAt:</td>
                    
                    <td valign="top" class="value">${rsNotification.lastClearedAt}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">lastCreatedAt:</td>
                    
                    <td valign="top" class="value">${rsNotification.lastCreatedAt}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">lastNotifiedAt:</td>
                    
                    <td valign="top" class="value">${rsNotification.lastNotifiedAt}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">name:</td>
                    
                    <td valign="top" class="value">${rsNotification.name}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">occurrenceCount:</td>
                    
                    <td valign="top" class="value">${rsNotification.occurrenceCount}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">owner:</td>
                    
                    <td valign="top" class="value">${rsNotification.owner}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">severity:</td>
                    
                    <td valign="top" class="value">${rsNotification.severity}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">sourceDomainName:</td>
                    
                    <td valign="top" class="value">${rsNotification.sourceDomainName}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">troubleTicketID:</td>
                    
                    <td valign="top" class="value">${rsNotification.troubleTicketID}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">userDefined1:</td>
                    
                    <td valign="top" class="value">${rsNotification.userDefined1}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">userDefined10:</td>
                    
                    <td valign="top" class="value">${rsNotification.userDefined10}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">userDefined2:</td>
                    
                    <td valign="top" class="value">${rsNotification.userDefined2}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">userDefined3:</td>
                    
                    <td valign="top" class="value">${rsNotification.userDefined3}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">userDefined4:</td>
                    
                    <td valign="top" class="value">${rsNotification.userDefined4}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">userDefined5:</td>
                    
                    <td valign="top" class="value">${rsNotification.userDefined5}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">userDefined6:</td>
                    
                    <td valign="top" class="value">${rsNotification.userDefined6}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">userDefined7:</td>
                    
                    <td valign="top" class="value">${rsNotification.userDefined7}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">userDefined8:</td>
                    
                    <td valign="top" class="value">${rsNotification.userDefined8}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">userDefined9:</td>
                    
                    <td valign="top" class="value">${rsNotification.userDefined9}</td>
                    
                </tr>
                
            </tbody>
        </table>
    </div>
    <div class="buttons">
        <g:form>
            <input type="hidden" name="id" value="${rsNotification?.id}"/>
            <span class="button"><g:actionSubmit class="edit" value="Edit"/></span>
            <span class="button"><g:actionSubmit class="delete" onclick="return confirm('Are you sure?');" value="Delete"/></span>
        </g:form>
    </div>
</div>
</body>
</html>
