

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Edit RsEvent</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLinkTo(dir:'')}">Home</a></span>
    <span class="menuButton"><g:link class="list" action="list">RsEvent List</g:link></span>
    <span class="menuButton"><g:link class="create" action="create">New RsEvent</g:link></span>
</div>
<div class="body">
    <h1>Edit RsEvent</h1>
    <g:if test="${flash.message}">
        <div class="message">${flash.message}</div>
    </g:if>
    <g:hasErrors bean="${rsEvent}">
        <div class="errors">
            <g:renderErrors bean="${rsEvent}" as="list"/>
        </div>
    </g:hasErrors>
    <g:hasErrors bean="${flash.errors}">
       <div class="errors">
            <g:renderErrors bean="${flash.errors}"/>
        </div>
    </g:hasErrors>
    <g:form method="post" >
        <input type="hidden" name="id" value="${rsEvent?.id}"/>
        <div class="dialog">
            <table>
                <tbody>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="className">className:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsEvent,field:'className','errors')}">
                            <input type="text" id="className" name="className" value="${fieldValue(bean:rsEvent,field:'className')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="eventName">eventName:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsEvent,field:'eventName','errors')}">
                            <input type="text" id="eventName" name="eventName" value="${fieldValue(bean:rsEvent,field:'eventName')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="instanceName">instanceName:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsEvent,field:'instanceName','errors')}">
                            <input type="text" id="instanceName" name="instanceName" value="${fieldValue(bean:rsEvent,field:'instanceName')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="acknowledged">acknowledged:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsEvent,field:'acknowledged','errors')}">
                            <g:checkBox name="acknowledged" value="${rsEvent?.acknowledged}" ></g:checkBox>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="active">active:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsEvent,field:'active','errors')}">
                            <g:checkBox name="active" value="${rsEvent?.active}" ></g:checkBox>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="category">category:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsEvent,field:'category','errors')}">
                            <input type="text" id="category" name="category" value="${fieldValue(bean:rsEvent,field:'category')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="certainty">certainty:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsEvent,field:'certainty','errors')}">
                            <input type="text" id="certainty" name="certainty" value="${fieldValue(bean:rsEvent,field:'certainty')}" />
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="classDisplayName">classDisplayName:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsEvent,field:'classDisplayName','errors')}">
                            <input type="text" id="classDisplayName" name="classDisplayName" value="${fieldValue(bean:rsEvent,field:'classDisplayName')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="creationClassName">creationClassName:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsEvent,field:'creationClassName','errors')}">
                            <input type="text" id="creationClassName" name="creationClassName" value="${fieldValue(bean:rsEvent,field:'creationClassName')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="description">description:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsEvent,field:'description','errors')}">
                            <input type="text" id="description" name="description" value="${fieldValue(bean:rsEvent,field:'description')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="displayName">displayName:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsEvent,field:'displayName','errors')}">
                            <input type="text" id="displayName" name="displayName" value="${fieldValue(bean:rsEvent,field:'displayName')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="elementClassName">elementClassName:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsEvent,field:'elementClassName','errors')}">
                            <input type="text" id="elementClassName" name="elementClassName" value="${fieldValue(bean:rsEvent,field:'elementClassName')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="elementName">elementName:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsEvent,field:'elementName','errors')}">
                            <input type="text" id="elementName" name="elementName" value="${fieldValue(bean:rsEvent,field:'elementName')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="eventDisplayName">eventDisplayName:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsEvent,field:'eventDisplayName','errors')}">
                            <input type="text" id="eventDisplayName" name="eventDisplayName" value="${fieldValue(bean:rsEvent,field:'eventDisplayName')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="eventState">eventState:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsEvent,field:'eventState','errors')}">
                            <input type="text" id="eventState" name="eventState" value="${fieldValue(bean:rsEvent,field:'eventState')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="eventText">eventText:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsEvent,field:'eventText','errors')}">
                            <input type="text" id="eventText" name="eventText" value="${fieldValue(bean:rsEvent,field:'eventText')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="eventType">eventType:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsEvent,field:'eventType','errors')}">
                            <input type="text" id="eventType" name="eventType" value="${fieldValue(bean:rsEvent,field:'eventType')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="firstNotifiedAt">firstNotifiedAt:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsEvent,field:'firstNotifiedAt','errors')}">
                            <input type="text" id="firstNotifiedAt" name="firstNotifiedAt" value="${fieldValue(bean:rsEvent,field:'firstNotifiedAt')}" />
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="impact">impact:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsEvent,field:'impact','errors')}">
                            <input type="text" id="impact" name="impact" value="${fieldValue(bean:rsEvent,field:'impact')}" />
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="inMaintenance">inMaintenance:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsEvent,field:'inMaintenance','errors')}">
                            <g:checkBox name="inMaintenance" value="${rsEvent?.inMaintenance}" ></g:checkBox>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="instanceDisplayName">instanceDisplayName:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsEvent,field:'instanceDisplayName','errors')}">
                            <input type="text" id="instanceDisplayName" name="instanceDisplayName" value="${fieldValue(bean:rsEvent,field:'instanceDisplayName')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="isRoot">isRoot:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsEvent,field:'isRoot','errors')}">
                            <g:checkBox name="isRoot" value="${rsEvent?.isRoot}" ></g:checkBox>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="lastChangedAt">lastChangedAt:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsEvent,field:'lastChangedAt','errors')}">
                            <input type="text" id="lastChangedAt" name="lastChangedAt" value="${fieldValue(bean:rsEvent,field:'lastChangedAt')}" />
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="lastClearedAt">lastClearedAt:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsEvent,field:'lastClearedAt','errors')}">
                            <input type="text" id="lastClearedAt" name="lastClearedAt" value="${fieldValue(bean:rsEvent,field:'lastClearedAt')}" />
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="lastCreatedAt">lastCreatedAt:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsEvent,field:'lastCreatedAt','errors')}">
                            <input type="text" id="lastCreatedAt" name="lastCreatedAt" value="${fieldValue(bean:rsEvent,field:'lastCreatedAt')}" />
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="lastNotifiedAt">lastNotifiedAt:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsEvent,field:'lastNotifiedAt','errors')}">
                            <input type="text" id="lastNotifiedAt" name="lastNotifiedAt" value="${fieldValue(bean:rsEvent,field:'lastNotifiedAt')}" />
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="name">name:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsEvent,field:'name','errors')}">
                            <input type="text" id="name" name="name" value="${fieldValue(bean:rsEvent,field:'name')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="occurrenceCount">occurrenceCount:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsEvent,field:'occurrenceCount','errors')}">
                            <input type="text" id="occurrenceCount" name="occurrenceCount" value="${fieldValue(bean:rsEvent,field:'occurrenceCount')}" />
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="owner">owner:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsEvent,field:'owner','errors')}">
                            <input type="text" id="owner" name="owner" value="${fieldValue(bean:rsEvent,field:'owner')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="severity">severity:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsEvent,field:'severity','errors')}">
                            <input type="text" id="severity" name="severity" value="${fieldValue(bean:rsEvent,field:'severity')}" />
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="sourceDomainName">sourceDomainName:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsEvent,field:'sourceDomainName','errors')}">
                            <input type="text" id="sourceDomainName" name="sourceDomainName" value="${fieldValue(bean:rsEvent,field:'sourceDomainName')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="troubleTicketID">troubleTicketID:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsEvent,field:'troubleTicketID','errors')}">
                            <input type="text" id="troubleTicketID" name="troubleTicketID" value="${fieldValue(bean:rsEvent,field:'troubleTicketID')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="userDefined1">userDefined1:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsEvent,field:'userDefined1','errors')}">
                            <input type="text" id="userDefined1" name="userDefined1" value="${fieldValue(bean:rsEvent,field:'userDefined1')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="userDefined10">userDefined10:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsEvent,field:'userDefined10','errors')}">
                            <input type="text" id="userDefined10" name="userDefined10" value="${fieldValue(bean:rsEvent,field:'userDefined10')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="userDefined2">userDefined2:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsEvent,field:'userDefined2','errors')}">
                            <input type="text" id="userDefined2" name="userDefined2" value="${fieldValue(bean:rsEvent,field:'userDefined2')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="userDefined3">userDefined3:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsEvent,field:'userDefined3','errors')}">
                            <input type="text" id="userDefined3" name="userDefined3" value="${fieldValue(bean:rsEvent,field:'userDefined3')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="userDefined4">userDefined4:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsEvent,field:'userDefined4','errors')}">
                            <input type="text" id="userDefined4" name="userDefined4" value="${fieldValue(bean:rsEvent,field:'userDefined4')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="userDefined5">userDefined5:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsEvent,field:'userDefined5','errors')}">
                            <input type="text" id="userDefined5" name="userDefined5" value="${fieldValue(bean:rsEvent,field:'userDefined5')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="userDefined6">userDefined6:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsEvent,field:'userDefined6','errors')}">
                            <input type="text" id="userDefined6" name="userDefined6" value="${fieldValue(bean:rsEvent,field:'userDefined6')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="userDefined7">userDefined7:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsEvent,field:'userDefined7','errors')}">
                            <input type="text" id="userDefined7" name="userDefined7" value="${fieldValue(bean:rsEvent,field:'userDefined7')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="userDefined8">userDefined8:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsEvent,field:'userDefined8','errors')}">
                            <input type="text" id="userDefined8" name="userDefined8" value="${fieldValue(bean:rsEvent,field:'userDefined8')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="userDefined9">userDefined9:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsEvent,field:'userDefined9','errors')}">
                            <input type="text" id="userDefined9" name="userDefined9" value="${fieldValue(bean:rsEvent,field:'userDefined9')}"/>
                        </td>
                    </tr>
                    
                </tbody>
            </table>
        </div>
        <div class="buttons">
            <span class="button"><g:actionSubmit class="save" value="Update"/></span>
            <span class="button"><g:actionSubmit class="delete" onclick="return confirm('Are you sure?');" value="Delete"/></span>
        </div>
    </g:form>
</div>
</body>
</html>
