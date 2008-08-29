

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Create HypericEvent</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLinkTo(dir:'')}">Home</a></span>
    <span class="menuButton"><g:link class="list" action="list">HypericEvent List</g:link></span>
</div>
<div class="body">
    <h1>Create HypericEvent</h1>
    <g:if test="${flash.message}">
        <div class="message">${flash.message}</div>
    </g:if>
    <g:hasErrors bean="${hypericEvent}">
        <div class="errors">
            <g:renderErrors bean="${hypericEvent}" as="list"/>
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
                            <label for="aid">aid:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:hypericEvent,field:'aid','errors')}">
                            <input type="text" id="aid" name="aid" value="${fieldValue(bean:hypericEvent,field:'aid')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="fixed">fixed:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:hypericEvent,field:'fixed','errors')}">
                            <input type="text" id="fixed" name="fixed" value="${fieldValue(bean:hypericEvent,field:'fixed')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="name">name:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:hypericEvent,field:'name','errors')}">
                            <input type="text" id="name" name="name" value="${fieldValue(bean:hypericEvent,field:'name')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="owner">owner:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:hypericEvent,field:'owner','errors')}">
                            <g:select optionKey="id" from="${Resource.list()}" name="owner.id" value="${hypericEvent?.owner?.id}" noSelection="['null':'']"></g:select>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="timestamp">timestamp:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:hypericEvent,field:'timestamp','errors')}">
                            <input type="text" id="timestamp" name="timestamp" value="${fieldValue(bean:hypericEvent,field:'timestamp')}"/>
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
