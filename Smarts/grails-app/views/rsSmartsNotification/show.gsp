

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Show RsSmartsNotification</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLinkTo(dir:'')}">Home</a></span>
    <span class="menuButton"><g:link class="list" action="list">RsSmartsNotification List</g:link></span>
    <span class="menuButton"><g:link class="create" action="create">New RsSmartsNotification</g:link></span>
</div>
<div class="body">
    <h1>Show RsSmartsNotification</h1>
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
                    
                    <td valign="top" class="value">${rsSmartsNotification.id}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">acknowledged:</td>
                    
                    <td valign="top" class="value">${rsSmartsNotification.acknowledged}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">active:</td>
                    
                    <td valign="top" class="value">${rsSmartsNotification.active}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">category:</td>
                    
                    <td valign="top" class="value">${rsSmartsNotification.category}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">causedBy:</td>
                    
                    <td valign="top" style="text-align:left;" class="value">
                        <ul>
                            <g:each var="c" in="${rsSmartsNotification.causedBy}">
                                <li><g:link controller="rsSmartsNotification" action="show" id="${c.id}">${c}</g:link></li>
                            </g:each>
                        </ul>
                    </td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">causes:</td>
                    
                    <td valign="top" style="text-align:left;" class="value">
                        <ul>
                            <g:each var="c" in="${rsSmartsNotification.causes}">
                                <li><g:link controller="rsSmartsNotification" action="show" id="${c.id}">${c}</g:link></li>
                            </g:each>
                        </ul>
                    </td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">certainty:</td>
                    
                    <td valign="top" class="value">${rsSmartsNotification.certainty}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">classDisplayName:</td>
                    
                    <td valign="top" class="value">${rsSmartsNotification.classDisplayName}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">className:</td>
                    
                    <td valign="top" class="value">${rsSmartsNotification.className}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">creationClassName:</td>
                    
                    <td valign="top" class="value">${rsSmartsNotification.creationClassName}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">description:</td>
                    
                    <td valign="top" class="value">${rsSmartsNotification.description}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">displayName:</td>
                    
                    <td valign="top" class="value">${rsSmartsNotification.displayName}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">elementClassName:</td>
                    
                    <td valign="top" class="value">${rsSmartsNotification.elementClassName}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">elementName:</td>
                    
                    <td valign="top" class="value">${rsSmartsNotification.elementName}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">eventDisplayName:</td>
                    
                    <td valign="top" class="value">${rsSmartsNotification.eventDisplayName}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">eventName:</td>
                    
                    <td valign="top" class="value">${rsSmartsNotification.eventName}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">eventState:</td>
                    
                    <td valign="top" class="value">${rsSmartsNotification.eventState}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">eventText:</td>
                    
                    <td valign="top" class="value">${rsSmartsNotification.eventText}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">eventType:</td>
                    
                    <td valign="top" class="value">${rsSmartsNotification.eventType}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">firstNotifiedAt:</td>
                    
                    <td valign="top" class="value">${rsSmartsNotification.firstNotifiedAt}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">impact:</td>
                    
                    <td valign="top" class="value">${rsSmartsNotification.impact}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">inMaintenance:</td>
                    
                    <td valign="top" class="value">${rsSmartsNotification.inMaintenance}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">instanceDisplayName:</td>
                    
                    <td valign="top" class="value">${rsSmartsNotification.instanceDisplayName}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">instanceName:</td>
                    
                    <td valign="top" class="value">${rsSmartsNotification.instanceName}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">isProblem:</td>
                    
                    <td valign="top" class="value">${rsSmartsNotification.isProblem}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">isRoot:</td>
                    
                    <td valign="top" class="value">${rsSmartsNotification.isRoot}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">lastChangedAt:</td>
                    
                    <td valign="top" class="value">${rsSmartsNotification.lastChangedAt}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">lastClearedAt:</td>
                    
                    <td valign="top" class="value">${rsSmartsNotification.lastClearedAt}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">lastCreatedAt:</td>
                    
                    <td valign="top" class="value">${rsSmartsNotification.lastCreatedAt}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">lastNotifiedAt:</td>
                    
                    <td valign="top" class="value">${rsSmartsNotification.lastNotifiedAt}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">occurrenceCount:</td>
                    
                    <td valign="top" class="value">${rsSmartsNotification.occurrenceCount}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">owner:</td>
                    
                    <td valign="top" class="value">${rsSmartsNotification.owner}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">rsDatasource:</td>
                    
                    <td valign="top" class="value">${rsSmartsNotification.rsDatasource}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">severity:</td>
                    
                    <td valign="top" class="value">${rsSmartsNotification.severity}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">sourceDomainName:</td>
                    
                    <td valign="top" class="value">${rsSmartsNotification.sourceDomainName}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">troubleTicketID:</td>
                    
                    <td valign="top" class="value">${rsSmartsNotification.troubleTicketID}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">userDefined1:</td>
                    
                    <td valign="top" class="value">${rsSmartsNotification.userDefined1}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">userDefined10:</td>
                    
                    <td valign="top" class="value">${rsSmartsNotification.userDefined10}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">userDefined2:</td>
                    
                    <td valign="top" class="value">${rsSmartsNotification.userDefined2}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">userDefined3:</td>
                    
                    <td valign="top" class="value">${rsSmartsNotification.userDefined3}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">userDefined4:</td>
                    
                    <td valign="top" class="value">${rsSmartsNotification.userDefined4}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">userDefined5:</td>
                    
                    <td valign="top" class="value">${rsSmartsNotification.userDefined5}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">userDefined6:</td>
                    
                    <td valign="top" class="value">${rsSmartsNotification.userDefined6}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">userDefined7:</td>
                    
                    <td valign="top" class="value">${rsSmartsNotification.userDefined7}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">userDefined8:</td>
                    
                    <td valign="top" class="value">${rsSmartsNotification.userDefined8}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">userDefined9:</td>
                    
                    <td valign="top" class="value">${rsSmartsNotification.userDefined9}</td>
                    
                </tr>
                
            </tbody>
        </table>
    </div>
    <div class="buttons">
        <g:form>
            <input type="hidden" name="id" value="${rsSmartsNotification?.id}"/>
            <span class="button"><g:actionSubmit class="edit" value="Edit"/></span>
            <span class="button"><g:actionSubmit class="delete" onclick="return confirm('Are you sure?');" value="Delete"/></span>
        </g:form>
    </div>
</div>
</body>
</html>
