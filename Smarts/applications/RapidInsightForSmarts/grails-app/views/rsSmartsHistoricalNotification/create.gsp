

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Create RsSmartsHistoricalNotification</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLinkTo(dir:'')}">Home</a></span>
    <span class="menuButton"><g:link class="list" action="list">RsSmartsHistoricalNotification List</g:link></span>
</div>
<div class="body">
    <h1>Create RsSmartsHistoricalNotification</h1>
    <g:if test="${flash.message}">
        <div class="message">${flash.message}</div>
    </g:if>
    <g:hasErrors bean="${rsSmartsHistoricalNotification}">
        <div class="errors">
            <g:renderErrors bean="${rsSmartsHistoricalNotification}" as="list"/>
        </div>
    </g:hasErrors>
    <g:hasErrors bean="${flash.errors}">
       <div class="errors">
            <g:renderErrors bean="${flash.errors}"/>
        </div>
    </g:hasErrors>
    <g:form action="save" method="post" >
        <div class="dialog">
            <table>
                <tbody>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="name">name:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsSmartsHistoricalNotification,field:'name','errors')}">
                            <input type="text" id="name" name="name" value="${fieldValue(bean:rsSmartsHistoricalNotification,field:'name')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="acknowledged">acknowledged:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsSmartsHistoricalNotification,field:'acknowledged','errors')}">
                            <g:checkBox name="acknowledged" value="${rsSmartsHistoricalNotification?.acknowledged}" ></g:checkBox>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="category">category:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsSmartsHistoricalNotification,field:'category','errors')}">
                            <input type="text" id="category" name="category" value="${fieldValue(bean:rsSmartsHistoricalNotification,field:'category')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="certainty">certainty:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsSmartsHistoricalNotification,field:'certainty','errors')}">
                            <input type="text" id="certainty" name="certainty" value="${fieldValue(bean:rsSmartsHistoricalNotification,field:'certainty')}" />
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="changedAt">changedAt:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsSmartsHistoricalNotification,field:'changedAt','errors')}">
                            <input type="text" id="changedAt" name="changedAt" value="${fieldValue(bean:rsSmartsHistoricalNotification,field:'changedAt')}" />
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="classDisplayName">classDisplayName:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsSmartsHistoricalNotification,field:'classDisplayName','errors')}">
                            <input type="text" id="classDisplayName" name="classDisplayName" value="${fieldValue(bean:rsSmartsHistoricalNotification,field:'classDisplayName')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="className">className:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsSmartsHistoricalNotification,field:'className','errors')}">
                            <input type="text" id="className" name="className" value="${fieldValue(bean:rsSmartsHistoricalNotification,field:'className')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="clearedAt">clearedAt:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsSmartsHistoricalNotification,field:'clearedAt','errors')}">
                            <input type="text" id="clearedAt" name="clearedAt" value="${fieldValue(bean:rsSmartsHistoricalNotification,field:'clearedAt')}" />
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="count">count:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsSmartsHistoricalNotification,field:'count','errors')}">
                            <input type="text" id="count" name="count" value="${fieldValue(bean:rsSmartsHistoricalNotification,field:'count')}" />
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="createdAt">createdAt:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsSmartsHistoricalNotification,field:'createdAt','errors')}">
                            <input type="text" id="createdAt" name="createdAt" value="${fieldValue(bean:rsSmartsHistoricalNotification,field:'createdAt')}" />
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="creationClassName">creationClassName:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsSmartsHistoricalNotification,field:'creationClassName','errors')}">
                            <input type="text" id="creationClassName" name="creationClassName" value="${fieldValue(bean:rsSmartsHistoricalNotification,field:'creationClassName')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="description">description:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsSmartsHistoricalNotification,field:'description','errors')}">
                            <input type="text" id="description" name="description" value="${fieldValue(bean:rsSmartsHistoricalNotification,field:'description')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="displayName">displayName:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsSmartsHistoricalNotification,field:'displayName','errors')}">
                            <input type="text" id="displayName" name="displayName" value="${fieldValue(bean:rsSmartsHistoricalNotification,field:'displayName')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="elementClassName">elementClassName:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsSmartsHistoricalNotification,field:'elementClassName','errors')}">
                            <input type="text" id="elementClassName" name="elementClassName" value="${fieldValue(bean:rsSmartsHistoricalNotification,field:'elementClassName')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="elementName">elementName:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsSmartsHistoricalNotification,field:'elementName','errors')}">
                            <input type="text" id="elementName" name="elementName" value="${fieldValue(bean:rsSmartsHistoricalNotification,field:'elementName')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="eventDisplayName">eventDisplayName:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsSmartsHistoricalNotification,field:'eventDisplayName','errors')}">
                            <input type="text" id="eventDisplayName" name="eventDisplayName" value="${fieldValue(bean:rsSmartsHistoricalNotification,field:'eventDisplayName')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="eventName">eventName:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsSmartsHistoricalNotification,field:'eventName','errors')}">
                            <input type="text" id="eventName" name="eventName" value="${fieldValue(bean:rsSmartsHistoricalNotification,field:'eventName')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="eventState">eventState:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsSmartsHistoricalNotification,field:'eventState','errors')}">
                            <input type="text" id="eventState" name="eventState" value="${fieldValue(bean:rsSmartsHistoricalNotification,field:'eventState')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="eventText">eventText:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsSmartsHistoricalNotification,field:'eventText','errors')}">
                            <input type="text" id="eventText" name="eventText" value="${fieldValue(bean:rsSmartsHistoricalNotification,field:'eventText')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="eventType">eventType:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsSmartsHistoricalNotification,field:'eventType','errors')}">
                            <input type="text" id="eventType" name="eventType" value="${fieldValue(bean:rsSmartsHistoricalNotification,field:'eventType')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="impact">impact:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsSmartsHistoricalNotification,field:'impact','errors')}">
                            <input type="text" id="impact" name="impact" value="${fieldValue(bean:rsSmartsHistoricalNotification,field:'impact')}" />
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="inMaintenance">inMaintenance:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsSmartsHistoricalNotification,field:'inMaintenance','errors')}">
                            <g:checkBox name="inMaintenance" value="${rsSmartsHistoricalNotification?.inMaintenance}" ></g:checkBox>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="instanceDisplayName">instanceDisplayName:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsSmartsHistoricalNotification,field:'instanceDisplayName','errors')}">
                            <input type="text" id="instanceDisplayName" name="instanceDisplayName" value="${fieldValue(bean:rsSmartsHistoricalNotification,field:'instanceDisplayName')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="instanceName">instanceName:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsSmartsHistoricalNotification,field:'instanceName','errors')}">
                            <input type="text" id="instanceName" name="instanceName" value="${fieldValue(bean:rsSmartsHistoricalNotification,field:'instanceName')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="isProblem">isProblem:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsSmartsHistoricalNotification,field:'isProblem','errors')}">
                            <g:checkBox name="isProblem" value="${rsSmartsHistoricalNotification?.isProblem}" ></g:checkBox>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="isRoot">isRoot:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsSmartsHistoricalNotification,field:'isRoot','errors')}">
                            <g:checkBox name="isRoot" value="${rsSmartsHistoricalNotification?.isRoot}" ></g:checkBox>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="lastNotifiedAt">lastNotifiedAt:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsSmartsHistoricalNotification,field:'lastNotifiedAt','errors')}">
                            <input type="text" id="lastNotifiedAt" name="lastNotifiedAt" value="${fieldValue(bean:rsSmartsHistoricalNotification,field:'lastNotifiedAt')}" />
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="owner">owner:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsSmartsHistoricalNotification,field:'owner','errors')}">
                            <input type="text" id="owner" name="owner" value="${fieldValue(bean:rsSmartsHistoricalNotification,field:'owner')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="rsDatasource">rsDatasource:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsSmartsHistoricalNotification,field:'rsDatasource','errors')}">
                            <input type="text" id="rsDatasource" name="rsDatasource" value="${fieldValue(bean:rsSmartsHistoricalNotification,field:'rsDatasource')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="severity">severity:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsSmartsHistoricalNotification,field:'severity','errors')}">
                            <input type="text" id="severity" name="severity" value="${fieldValue(bean:rsSmartsHistoricalNotification,field:'severity')}" />
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="source">source:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsSmartsHistoricalNotification,field:'source','errors')}">
                            <input type="text" id="source" name="source" value="${fieldValue(bean:rsSmartsHistoricalNotification,field:'source')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="sourceDomainName">sourceDomainName:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsSmartsHistoricalNotification,field:'sourceDomainName','errors')}">
                            <input type="text" id="sourceDomainName" name="sourceDomainName" value="${fieldValue(bean:rsSmartsHistoricalNotification,field:'sourceDomainName')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="state">state:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsSmartsHistoricalNotification,field:'state','errors')}">
                            <input type="text" id="state" name="state" value="${fieldValue(bean:rsSmartsHistoricalNotification,field:'state')}" />
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="troubleTicketID">troubleTicketID:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsSmartsHistoricalNotification,field:'troubleTicketID','errors')}">
                            <input type="text" id="troubleTicketID" name="troubleTicketID" value="${fieldValue(bean:rsSmartsHistoricalNotification,field:'troubleTicketID')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="userDefined1">userDefined1:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsSmartsHistoricalNotification,field:'userDefined1','errors')}">
                            <input type="text" id="userDefined1" name="userDefined1" value="${fieldValue(bean:rsSmartsHistoricalNotification,field:'userDefined1')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="userDefined10">userDefined10:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsSmartsHistoricalNotification,field:'userDefined10','errors')}">
                            <input type="text" id="userDefined10" name="userDefined10" value="${fieldValue(bean:rsSmartsHistoricalNotification,field:'userDefined10')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="userDefined2">userDefined2:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsSmartsHistoricalNotification,field:'userDefined2','errors')}">
                            <input type="text" id="userDefined2" name="userDefined2" value="${fieldValue(bean:rsSmartsHistoricalNotification,field:'userDefined2')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="userDefined3">userDefined3:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsSmartsHistoricalNotification,field:'userDefined3','errors')}">
                            <input type="text" id="userDefined3" name="userDefined3" value="${fieldValue(bean:rsSmartsHistoricalNotification,field:'userDefined3')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="userDefined4">userDefined4:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsSmartsHistoricalNotification,field:'userDefined4','errors')}">
                            <input type="text" id="userDefined4" name="userDefined4" value="${fieldValue(bean:rsSmartsHistoricalNotification,field:'userDefined4')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="userDefined5">userDefined5:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsSmartsHistoricalNotification,field:'userDefined5','errors')}">
                            <input type="text" id="userDefined5" name="userDefined5" value="${fieldValue(bean:rsSmartsHistoricalNotification,field:'userDefined5')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="userDefined6">userDefined6:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsSmartsHistoricalNotification,field:'userDefined6','errors')}">
                            <input type="text" id="userDefined6" name="userDefined6" value="${fieldValue(bean:rsSmartsHistoricalNotification,field:'userDefined6')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="userDefined7">userDefined7:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsSmartsHistoricalNotification,field:'userDefined7','errors')}">
                            <input type="text" id="userDefined7" name="userDefined7" value="${fieldValue(bean:rsSmartsHistoricalNotification,field:'userDefined7')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="userDefined8">userDefined8:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsSmartsHistoricalNotification,field:'userDefined8','errors')}">
                            <input type="text" id="userDefined8" name="userDefined8" value="${fieldValue(bean:rsSmartsHistoricalNotification,field:'userDefined8')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="userDefined9">userDefined9:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsSmartsHistoricalNotification,field:'userDefined9','errors')}">
                            <input type="text" id="userDefined9" name="userDefined9" value="${fieldValue(bean:rsSmartsHistoricalNotification,field:'userDefined9')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="willExpireAt">willExpireAt:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsSmartsHistoricalNotification,field:'willExpireAt','errors')}">
                            <input type="text" id="willExpireAt" name="willExpireAt" value="${fieldValue(bean:rsSmartsHistoricalNotification,field:'willExpireAt')}" />
                        </td>
                    </tr>
                    
                </tbody>
            </table>
        </div>
        <div class="buttons">
            <span class="button"><input class="save" type="submit" value="Create"/></span>
        </div>
    </g:form>
</div>
</body>
</html>
