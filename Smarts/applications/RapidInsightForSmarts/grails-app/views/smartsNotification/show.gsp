

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Show SmartsNotification</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLinkTo(dir:'')}">Home</a></span>
    <span class="menuButton"><g:link class="list" action="list">SmartsNotification List</g:link></span>
    <span class="menuButton"><g:link class="create" action="create">New SmartsNotification</g:link></span>
</div>
<div class="body">
    <h1>Show SmartsNotification</h1>
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
                    
                    <td valign="top" class="value">${smartsNotification.id}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">name:</td>
                    
                    <td valign="top" class="value">${smartsNotification.name}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">acknowledged:</td>
                    
                    <td valign="top" class="value">${smartsNotification.acknowledged}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">category:</td>
                    
                    <td valign="top" class="value">${smartsNotification.category}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">causedBy:</td>
                    
                    <td valign="top" style="text-align:left;" class="value">
                        <ul>
                            <g:each var="c" in="${smartsNotification.causedBy}">
                                <li><g:link controller="smartsNotification" action="show" id="${c.id}">${c}</g:link></li>
                            </g:each>
                        </ul>
                    </td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">causes:</td>
                    
                    <td valign="top" style="text-align:left;" class="value">
                        <ul>
                            <g:each var="c" in="${smartsNotification.causes}">
                                <li><g:link controller="smartsNotification" action="show" id="${c.id}">${c}</g:link></li>
                            </g:each>
                        </ul>
                    </td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">certainty:</td>
                    
                    <td valign="top" class="value">${smartsNotification.certainty}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">changedAt:</td>
                    
                    <td valign="top" class="value">${smartsNotification.changedAt}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">classDisplayName:</td>
                    
                    <td valign="top" class="value">${smartsNotification.classDisplayName}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">className:</td>
                    
                    <td valign="top" class="value">${smartsNotification.className}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">clearedAt:</td>
                    
                    <td valign="top" class="value">${smartsNotification.clearedAt}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">count:</td>
                    
                    <td valign="top" class="value">${smartsNotification.count}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">createdAt:</td>
                    
                    <td valign="top" class="value">${smartsNotification.createdAt}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">creationClassName:</td>
                    
                    <td valign="top" class="value">${smartsNotification.creationClassName}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">description:</td>
                    
                    <td valign="top" class="value">${smartsNotification.description}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">displayName:</td>
                    
                    <td valign="top" class="value">${smartsNotification.displayName}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">elementClassName:</td>
                    
                    <td valign="top" class="value">${smartsNotification.elementClassName}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">elementName:</td>
                    
                    <td valign="top" class="value">${smartsNotification.elementName}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">eventDisplayName:</td>
                    
                    <td valign="top" class="value">${smartsNotification.eventDisplayName}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">eventName:</td>
                    
                    <td valign="top" class="value">${smartsNotification.eventName}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">eventState:</td>
                    
                    <td valign="top" class="value">${smartsNotification.eventState}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">eventText:</td>
                    
                    <td valign="top" class="value">${smartsNotification.eventText}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">eventType:</td>
                    
                    <td valign="top" class="value">${smartsNotification.eventType}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">impact:</td>
                    
                    <td valign="top" class="value">${smartsNotification.impact}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">inMaintenance:</td>
                    
                    <td valign="top" class="value">${smartsNotification.inMaintenance}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">instanceDisplayName:</td>
                    
                    <td valign="top" class="value">${smartsNotification.instanceDisplayName}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">instanceName:</td>
                    
                    <td valign="top" class="value">${smartsNotification.instanceName}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">isProblem:</td>
                    
                    <td valign="top" class="value">${smartsNotification.isProblem}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">isRoot:</td>
                    
                    <td valign="top" class="value">${smartsNotification.isRoot}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">lastNotifiedAt:</td>
                    
                    <td valign="top" class="value">${smartsNotification.lastNotifiedAt}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">owner:</td>
                    
                    <td valign="top" class="value">${smartsNotification.owner}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">rsDatasource:</td>
                    
                    <td valign="top" class="value">${smartsNotification.rsDatasource}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">severity:</td>
                    
                    <td valign="top" class="value">${smartsNotification.severity}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">source:</td>
                    
                    <td valign="top" class="value">${smartsNotification.source}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">sourceDomainName:</td>
                    
                    <td valign="top" class="value">${smartsNotification.sourceDomainName}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">state:</td>
                    
                    <td valign="top" class="value">${smartsNotification.state}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">troubleTicketID:</td>
                    
                    <td valign="top" class="value">${smartsNotification.troubleTicketID}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">userDefined1:</td>
                    
                    <td valign="top" class="value">${smartsNotification.userDefined1}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">userDefined10:</td>
                    
                    <td valign="top" class="value">${smartsNotification.userDefined10}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">userDefined2:</td>
                    
                    <td valign="top" class="value">${smartsNotification.userDefined2}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">userDefined3:</td>
                    
                    <td valign="top" class="value">${smartsNotification.userDefined3}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">userDefined4:</td>
                    
                    <td valign="top" class="value">${smartsNotification.userDefined4}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">userDefined5:</td>
                    
                    <td valign="top" class="value">${smartsNotification.userDefined5}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">userDefined6:</td>
                    
                    <td valign="top" class="value">${smartsNotification.userDefined6}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">userDefined7:</td>
                    
                    <td valign="top" class="value">${smartsNotification.userDefined7}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">userDefined8:</td>
                    
                    <td valign="top" class="value">${smartsNotification.userDefined8}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">userDefined9:</td>
                    
                    <td valign="top" class="value">${smartsNotification.userDefined9}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">willExpireAt:</td>
                    
                    <td valign="top" class="value">${smartsNotification.willExpireAt}</td>
                    
                </tr>
                
            </tbody>
        </table>
    </div>
    <div class="buttons">
        <g:form>
            <input type="hidden" name="id" value="${smartsNotification?.id}"/>
            <span class="button"><g:actionSubmit class="edit" value="Edit"/></span>
            <span class="button"><g:actionSubmit class="delete" onclick="return confirm('Are you sure?');" value="Delete"/></span>
        </g:form>
    </div>
</div>
</body>
</html>
