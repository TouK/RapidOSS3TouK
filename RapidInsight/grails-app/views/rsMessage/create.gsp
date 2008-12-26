

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Create RsMessage</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLinkTo(dir:'')}">Home</a></span>
    <span class="menuButton"><g:link class="list" action="list">RsMessage List</g:link></span>
</div>
<div class="body">
    <h1>Create RsMessage</h1>
    <g:if test="${flash.message}">
        <div class="message">${flash.message}</div>
    </g:if>
    <g:hasErrors bean="${rsMessage}">
        <div class="errors">
            <g:renderErrors bean="${rsMessage}" as="list"/>
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
                            <label for="eventId">eventId:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsMessage,field:'eventId','errors')}">
                            <input type="text" id="eventId" name="eventId" value="${fieldValue(bean:rsMessage,field:'eventId')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="to">to:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsMessage,field:'to','errors')}">
                            <input type="text" id="to" name="to" value="${fieldValue(bean:rsMessage,field:'to')}"/>
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
