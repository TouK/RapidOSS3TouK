

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Edit RsHistoricalEvent</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLinkTo(dir:'')}">Home</a></span>
    <span class="menuButton"><g:link class="list" action="list">RsHistoricalEvent List</g:link></span>
    <span class="menuButton"><g:link class="create" action="create">New RsHistoricalEvent</g:link></span>
</div>
<div class="body">
    <h1>Edit RsHistoricalEvent</h1>
    <g:if test="${flash.message}">
        <div class="message">${flash.message}</div>
    </g:if>
    <g:hasErrors bean="${rsHistoricalEvent}">
        <div class="errors">
            <g:renderErrors bean="${rsHistoricalEvent}" as="list"/>
        </div>
    </g:hasErrors>
    <g:hasErrors bean="${flash.errors}">
       <div class="errors">
            <g:renderErrors bean="${flash.errors}"/>
        </div>
    </g:hasErrors>
    <g:form method="post" >
        <input type="hidden" name="id" value="${rsHistoricalEvent?.id}"/>
        <div class="dialog">
            <table>
                <tbody>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="id">id:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsHistoricalEvent,field:'id','errors')}">
                            <input type="text" id="id" name="id" value="${fieldValue(bean:rsHistoricalEvent,field:'id')}" />
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="acknowledged">acknowledged:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsHistoricalEvent,field:'acknowledged','errors')}">
                            <g:checkBox name="acknowledged" value="${rsHistoricalEvent?.acknowledged}" ></g:checkBox>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="active">active:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsHistoricalEvent,field:'active','errors')}">
                            <g:checkBox name="active" value="${rsHistoricalEvent?.active}" ></g:checkBox>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="count">count:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsHistoricalEvent,field:'count','errors')}">
                            <input type="text" id="count" name="count" value="${fieldValue(bean:rsHistoricalEvent,field:'count')}" />
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="eventName">eventName:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsHistoricalEvent,field:'eventName','errors')}">
                            <input type="text" id="eventName" name="eventName" value="${fieldValue(bean:rsHistoricalEvent,field:'eventName')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="firstNotifiedAt">firstNotifiedAt:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsHistoricalEvent,field:'firstNotifiedAt','errors')}">
                            <g:datePicker name="firstNotifiedAt" value="${rsHistoricalEvent?.firstNotifiedAt}" noSelection="['':'']"></g:datePicker>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="lastChangedAt">lastChangedAt:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsHistoricalEvent,field:'lastChangedAt','errors')}">
                            <g:datePicker name="lastChangedAt" value="${rsHistoricalEvent?.lastChangedAt}" noSelection="['':'']"></g:datePicker>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="lastClearedAt">lastClearedAt:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsHistoricalEvent,field:'lastClearedAt','errors')}">
                            <g:datePicker name="lastClearedAt" value="${rsHistoricalEvent?.lastClearedAt}" noSelection="['':'']"></g:datePicker>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="lastNotifiedAt">lastNotifiedAt:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsHistoricalEvent,field:'lastNotifiedAt','errors')}">
                            <g:datePicker name="lastNotifiedAt" value="${rsHistoricalEvent?.lastNotifiedAt}" noSelection="['':'']"></g:datePicker>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="name">name:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsHistoricalEvent,field:'name','errors')}">
                            <input type="text" id="name" name="name" value="${fieldValue(bean:rsHistoricalEvent,field:'name')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="owner">owner:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsHistoricalEvent,field:'owner','errors')}">
                            <input type="text" id="owner" name="owner" value="${fieldValue(bean:rsHistoricalEvent,field:'owner')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="severity">severity:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsHistoricalEvent,field:'severity','errors')}">
                            <input type="text" id="severity" name="severity" value="${fieldValue(bean:rsHistoricalEvent,field:'severity')}" />
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="source">source:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsHistoricalEvent,field:'source','errors')}">
                            <input type="text" id="source" name="source" value="${fieldValue(bean:rsHistoricalEvent,field:'source')}"/>
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
