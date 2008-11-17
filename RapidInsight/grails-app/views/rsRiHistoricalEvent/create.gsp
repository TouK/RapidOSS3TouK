

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Create RsRiHistoricalEvent</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLinkTo(dir:'')}">Home</a></span>
    <span class="menuButton"><g:link class="list" action="list">RsRiHistoricalEvent List</g:link></span>
</div>
<div class="body">
    <h1>Create RsRiHistoricalEvent</h1>
    <g:if test="${flash.message}">
        <div class="message">${flash.message}</div>
    </g:if>
    <g:hasErrors bean="${rsRiHistoricalEvent}">
        <div class="errors">
            <g:renderErrors bean="${rsRiHistoricalEvent}" as="list"/>
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
                        <td valign="top" class="value ${hasErrors(bean:rsRiHistoricalEvent,field:'name','errors')}">
                            <input type="text" id="name" name="name" value="${fieldValue(bean:rsRiHistoricalEvent,field:'name')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="acknowledged">acknowledged:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsRiHistoricalEvent,field:'acknowledged','errors')}">
                            <g:checkBox name="acknowledged" value="${rsRiHistoricalEvent?.acknowledged}" ></g:checkBox>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="changedAt">changedAt:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsRiHistoricalEvent,field:'changedAt','errors')}">
                            <input type="text" id="changedAt" name="changedAt" value="${fieldValue(bean:rsRiHistoricalEvent,field:'changedAt')}" />
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="clearedAt">clearedAt:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsRiHistoricalEvent,field:'clearedAt','errors')}">
                            <input type="text" id="clearedAt" name="clearedAt" value="${fieldValue(bean:rsRiHistoricalEvent,field:'clearedAt')}" />
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="count">count:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsRiHistoricalEvent,field:'count','errors')}">
                            <input type="text" id="count" name="count" value="${fieldValue(bean:rsRiHistoricalEvent,field:'count')}" />
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="createdAt">createdAt:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsRiHistoricalEvent,field:'createdAt','errors')}">
                            <input type="text" id="createdAt" name="createdAt" value="${fieldValue(bean:rsRiHistoricalEvent,field:'createdAt')}" />
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="description">description:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsRiHistoricalEvent,field:'description','errors')}">
                            <input type="text" id="description" name="description" value="${fieldValue(bean:rsRiHistoricalEvent,field:'description')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="elementName">elementName:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsRiHistoricalEvent,field:'elementName','errors')}">
                            <input type="text" id="elementName" name="elementName" value="${fieldValue(bean:rsRiHistoricalEvent,field:'elementName')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="identifier">identifier:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsRiHistoricalEvent,field:'identifier','errors')}">
                            <input type="text" id="identifier" name="identifier" value="${fieldValue(bean:rsRiHistoricalEvent,field:'identifier')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="owner">owner:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsRiHistoricalEvent,field:'owner','errors')}">
                            <input type="text" id="owner" name="owner" value="${fieldValue(bean:rsRiHistoricalEvent,field:'owner')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="rsDatasource">rsDatasource:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsRiHistoricalEvent,field:'rsDatasource','errors')}">
                            <input type="text" id="rsDatasource" name="rsDatasource" value="${fieldValue(bean:rsRiHistoricalEvent,field:'rsDatasource')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="severity">severity:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsRiHistoricalEvent,field:'severity','errors')}">
                            <input type="text" id="severity" name="severity" value="${fieldValue(bean:rsRiHistoricalEvent,field:'severity')}" />
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="source">source:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsRiHistoricalEvent,field:'source','errors')}">
                            <input type="text" id="source" name="source" value="${fieldValue(bean:rsRiHistoricalEvent,field:'source')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="state">state:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsRiHistoricalEvent,field:'state','errors')}">
                            <input type="text" id="state" name="state" value="${fieldValue(bean:rsRiHistoricalEvent,field:'state')}" />
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="willExpireAt">willExpireAt:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsRiHistoricalEvent,field:'willExpireAt','errors')}">
                            <input type="text" id="willExpireAt" name="willExpireAt" value="${fieldValue(bean:rsRiHistoricalEvent,field:'willExpireAt')}" />
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
