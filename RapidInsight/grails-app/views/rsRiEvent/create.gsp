

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Create RsRiEvent</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLinkTo(dir:'')}">Home</a></span>
    <span class="menuButton"><g:link class="list" action="list">RsRiEvent List</g:link></span>
</div>
<div class="body">
    <h1>Create RsRiEvent</h1>
    <g:if test="${flash.message}">
        <div class="message">${flash.message}</div>
    </g:if>
    <g:hasErrors bean="${rsRiEvent}">
        <div class="errors">
            <g:renderErrors bean="${rsRiEvent}" as="list"/>
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
                        <td valign="top" class="value ${hasErrors(bean:rsRiEvent,field:'name','errors')}">
                            <input type="text" id="name" name="name" value="${fieldValue(bean:rsRiEvent,field:'name')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="acknowledged">acknowledged:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsRiEvent,field:'acknowledged','errors')}">
                            <g:checkBox name="acknowledged" value="${rsRiEvent?.acknowledged}" ></g:checkBox>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="changedAt">changedAt:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsRiEvent,field:'changedAt','errors')}">
                            <input type="text" id="changedAt" name="changedAt" value="${fieldValue(bean:rsRiEvent,field:'changedAt')}" />
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="clearedAt">clearedAt:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsRiEvent,field:'clearedAt','errors')}">
                            <input type="text" id="clearedAt" name="clearedAt" value="${fieldValue(bean:rsRiEvent,field:'clearedAt')}" />
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="count">count:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsRiEvent,field:'count','errors')}">
                            <input type="text" id="count" name="count" value="${fieldValue(bean:rsRiEvent,field:'count')}" />
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="createdAt">createdAt:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsRiEvent,field:'createdAt','errors')}">
                            <input type="text" id="createdAt" name="createdAt" value="${fieldValue(bean:rsRiEvent,field:'createdAt')}" />
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="description">description:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsRiEvent,field:'description','errors')}">
                            <input type="text" id="description" name="description" value="${fieldValue(bean:rsRiEvent,field:'description')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="elementName">elementName:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsRiEvent,field:'elementName','errors')}">
                            <input type="text" id="elementName" name="elementName" value="${fieldValue(bean:rsRiEvent,field:'elementName')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="identifier">identifier:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsRiEvent,field:'identifier','errors')}">
                            <input type="text" id="identifier" name="identifier" value="${fieldValue(bean:rsRiEvent,field:'identifier')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="owner">owner:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsRiEvent,field:'owner','errors')}">
                            <input type="text" id="owner" name="owner" value="${fieldValue(bean:rsRiEvent,field:'owner')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="rsDatasource">rsDatasource:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsRiEvent,field:'rsDatasource','errors')}">
                            <input type="text" id="rsDatasource" name="rsDatasource" value="${fieldValue(bean:rsRiEvent,field:'rsDatasource')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="severity">severity:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsRiEvent,field:'severity','errors')}">
                            <input type="text" id="severity" name="severity" value="${fieldValue(bean:rsRiEvent,field:'severity')}" />
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="source">source:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsRiEvent,field:'source','errors')}">
                            <input type="text" id="source" name="source" value="${fieldValue(bean:rsRiEvent,field:'source')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="state">state:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsRiEvent,field:'state','errors')}">
                            <input type="text" id="state" name="state" value="${fieldValue(bean:rsRiEvent,field:'state')}" />
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="willExpireAt">willExpireAt:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsRiEvent,field:'willExpireAt','errors')}">
                            <input type="text" id="willExpireAt" name="willExpireAt" value="${fieldValue(bean:rsRiEvent,field:'willExpireAt')}" />
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
