

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Show RsHistoricalNotification</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLinkTo(dir:'')}">Home</a></span>
    <span class="menuButton"><g:link class="list" action="list">RsHistoricalNotification List</g:link></span>
    <span class="menuButton"><g:link class="create" action="create">New RsHistoricalNotification</g:link></span>
</div>
<div class="body">
    <h1>Show RsHistoricalNotification</h1>
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
                    
                    <td valign="top" class="value">${rsHistoricalNotification.id}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">name:</td>
                    
                    <td valign="top" class="value">${rsHistoricalNotification.name}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">acknowledged:</td>
                    
                    <td valign="top" class="value">${rsHistoricalNotification.acknowledged}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">active:</td>
                    
                    <td valign="top" class="value">${rsHistoricalNotification.active}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">category:</td>
                    
                    <td valign="top" class="value">${rsHistoricalNotification.category}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">certainty:</td>
                    
                    <td valign="top" class="value">${rsHistoricalNotification.certainty}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">classDisplayName:</td>
                    
                    <td valign="top" class="value">${rsHistoricalNotification.classDisplayName}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">className:</td>
                    
                    <td valign="top" class="value">${rsHistoricalNotification.className}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">creationClassName:</td>
                    
                    <td valign="top" class="value">${rsHistoricalNotification.creationClassName}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">description:</td>
                    
                    <td valign="top" class="value">${rsHistoricalNotification.description}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">displayName:</td>
                    
                    <td valign="top" class="value">${rsHistoricalNotification.displayName}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">elementClassName:</td>
                    
                    <td valign="top" class="value">${rsHistoricalNotification.elementClassName}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">elementName:</td>
                    
                    <td valign="top" class="value">${rsHistoricalNotification.elementName}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">eventDisplayName:</td>
                    
                    <td valign="top" class="value">${rsHistoricalNotification.eventDisplayName}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">eventName:</td>
                    
                    <td valign="top" class="value">${rsHistoricalNotification.eventName}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">eventState:</td>
                    
                    <td valign="top" class="value">${rsHistoricalNotification.eventState}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">eventText:</td>
                    
                    <td valign="top" class="value">${rsHistoricalNotification.eventText}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">eventType:</td>
                    
                    <td valign="top" class="value">${rsHistoricalNotification.eventType}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">firstNotifiedAt:</td>
                    
                    <td valign="top" class="value">${rsHistoricalNotification.firstNotifiedAt}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">impact:</td>
                    
                    <td valign="top" class="value">${rsHistoricalNotification.impact}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">inMaintenance:</td>
                    
                    <td valign="top" class="value">${rsHistoricalNotification.inMaintenance}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">instanceDisplayName:</td>
                    
                    <td valign="top" class="value">${rsHistoricalNotification.instanceDisplayName}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">instanceName:</td>
                    
                    <td valign="top" class="value">${rsHistoricalNotification.instanceName}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">isManaged:</td>
                    
                    <td valign="top" class="value">${rsHistoricalNotification.isManaged}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">isRoot:</td>
                    
                    <td valign="top" class="value">${rsHistoricalNotification.isRoot}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">lastChangedAt:</td>
                    
                    <td valign="top" class="value">${rsHistoricalNotification.lastChangedAt}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">lastClearedAt:</td>
                    
                    <td valign="top" class="value">${rsHistoricalNotification.lastClearedAt}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">lastCreatedAt:</td>
                    
                    <td valign="top" class="value">${rsHistoricalNotification.lastCreatedAt}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">lastNotifiedAt:</td>
                    
                    <td valign="top" class="value">${rsHistoricalNotification.lastNotifiedAt}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">occurrenceCount:</td>
                    
                    <td valign="top" class="value">${rsHistoricalNotification.occurrenceCount}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">owner:</td>
                    
                    <td valign="top" class="value">${rsHistoricalNotification.owner}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">severity:</td>
                    
                    <td valign="top" class="value">${rsHistoricalNotification.severity}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">sourceDomainName:</td>
                    
                    <td valign="top" class="value">${rsHistoricalNotification.sourceDomainName}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">troubleTicketID:</td>
                    
                    <td valign="top" class="value">${rsHistoricalNotification.troubleTicketID}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">userDefined1:</td>
                    
                    <td valign="top" class="value">${rsHistoricalNotification.userDefined1}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">userDefined10:</td>
                    
                    <td valign="top" class="value">${rsHistoricalNotification.userDefined10}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">userDefined2:</td>
                    
                    <td valign="top" class="value">${rsHistoricalNotification.userDefined2}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">userDefined3:</td>
                    
                    <td valign="top" class="value">${rsHistoricalNotification.userDefined3}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">userDefined4:</td>
                    
                    <td valign="top" class="value">${rsHistoricalNotification.userDefined4}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">userDefined5:</td>
                    
                    <td valign="top" class="value">${rsHistoricalNotification.userDefined5}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">userDefined6:</td>
                    
                    <td valign="top" class="value">${rsHistoricalNotification.userDefined6}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">userDefined7:</td>
                    
                    <td valign="top" class="value">${rsHistoricalNotification.userDefined7}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">userDefined8:</td>
                    
                    <td valign="top" class="value">${rsHistoricalNotification.userDefined8}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">userDefined9:</td>
                    
                    <td valign="top" class="value">${rsHistoricalNotification.userDefined9}</td>
                    
                </tr>
                
            </tbody>
        </table>
    </div>
    <div class="buttons">
        <g:form>
            <input type="hidden" name="id" value="${rsHistoricalNotification?.id}"/>
            <span class="button"><g:actionSubmit class="edit" value="Edit"/></span>
            <span class="button"><g:actionSubmit class="delete" onclick="return confirm('Are you sure?');" value="Delete"/></span>
        </g:form>
    </div>
</div>
</body>
</html>
