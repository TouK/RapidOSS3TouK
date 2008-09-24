

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Create RsRcmdbEvent</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLinkTo(dir:'')}">Home</a></span>
    <span class="menuButton"><g:link class="list" action="list">RsRcmdbEvent List</g:link></span>
</div>
<div class="body">
    <h1>Create RsRcmdbEvent</h1>
    <g:if test="${flash.message}">
        <div class="message">${flash.message}</div>
    </g:if>
    <g:hasErrors bean="${rsRcmdbEvent}">
        <div class="errors">
            <g:renderErrors bean="${rsRcmdbEvent}" as="list"/>
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
                        <td valign="top" class="value ${hasErrors(bean:rsRcmdbEvent,field:'name','errors')}">
                            <input type="text" id="name" name="name" value="${fieldValue(bean:rsRcmdbEvent,field:'name')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="acknowledged">acknowledged:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsRcmdbEvent,field:'acknowledged','errors')}">
                            <g:checkBox name="acknowledged" value="${rsRcmdbEvent?.acknowledged}" ></g:checkBox>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="active">active:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsRcmdbEvent,field:'active','errors')}">
                            <g:checkBox name="active" value="${rsRcmdbEvent?.active}" ></g:checkBox>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="count">count:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsRcmdbEvent,field:'count','errors')}">
                            <input type="text" id="count" name="count" value="${fieldValue(bean:rsRcmdbEvent,field:'count')}" />
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="eventName">eventName:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsRcmdbEvent,field:'eventName','errors')}">
                            <input type="text" id="eventName" name="eventName" value="${fieldValue(bean:rsRcmdbEvent,field:'eventName')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="firstNotifiedAt">firstNotifiedAt:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsRcmdbEvent,field:'firstNotifiedAt','errors')}">
                            <g:datePicker name="firstNotifiedAt" value="${rsRcmdbEvent?.firstNotifiedAt}" noSelection="['':'']"></g:datePicker>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="lastChangedAt">lastChangedAt:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsRcmdbEvent,field:'lastChangedAt','errors')}">
                            <g:datePicker name="lastChangedAt" value="${rsRcmdbEvent?.lastChangedAt}" noSelection="['':'']"></g:datePicker>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="lastClearedAt">lastClearedAt:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsRcmdbEvent,field:'lastClearedAt','errors')}">
                            <g:datePicker name="lastClearedAt" value="${rsRcmdbEvent?.lastClearedAt}" noSelection="['':'']"></g:datePicker>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="lastNotifiedAt">lastNotifiedAt:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsRcmdbEvent,field:'lastNotifiedAt','errors')}">
                            <g:datePicker name="lastNotifiedAt" value="${rsRcmdbEvent?.lastNotifiedAt}" noSelection="['':'']"></g:datePicker>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="owner">owner:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsRcmdbEvent,field:'owner','errors')}">
                            <input type="text" id="owner" name="owner" value="${fieldValue(bean:rsRcmdbEvent,field:'owner')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="severity">severity:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsRcmdbEvent,field:'severity','errors')}">
                            <input type="text" id="severity" name="severity" value="${fieldValue(bean:rsRcmdbEvent,field:'severity')}" />
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="source">source:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsRcmdbEvent,field:'source','errors')}">
                            <input type="text" id="source" name="source" value="${fieldValue(bean:rsRcmdbEvent,field:'source')}"/>
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
