

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Edit RsService</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLinkTo(dir:'')}">Home</a></span>
    <span class="menuButton"><g:link class="list" action="list">RsService List</g:link></span>
    <span class="menuButton"><g:link class="create" action="create">New RsService</g:link></span>
</div>
<div class="body">
    <h1>Edit RsService</h1>
    <g:if test="${flash.message}">
        <div class="message">${flash.message}</div>
    </g:if>
    <g:hasErrors bean="${rsService}">
        <div class="errors">
            <g:renderErrors bean="${rsService}" as="list"/>
        </div>
    </g:hasErrors>
    <g:hasErrors bean="${flash.errors}">
       <div class="errors">
            <g:renderErrors bean="${flash.errors}"/>
        </div>
    </g:hasErrors>
    <g:form method="post" >
        <input type="hidden" name="id" value="${rsService?.id}"/>
        <div class="dialog">
            <table>
                <tbody>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="name">name:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsService,field:'name','errors')}">
                            <input type="text" id="name" name="name" value="${fieldValue(bean:rsService,field:'name')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="derivedStatus">derivedStatus:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsService,field:'derivedStatus','errors')}">
                            <input type="text" id="derivedStatus" name="derivedStatus" value="${fieldValue(bean:rsService,field:'derivedStatus')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="interval">interval:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsService,field:'interval','errors')}">
                            <input type="text" id="interval" name="interval" value="${fieldValue(bean:rsService,field:'interval')}" />
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="lastChangedAt">lastChangedAt:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsService,field:'lastChangedAt','errors')}">
                            <input type="text" id="lastChangedAt" name="lastChangedAt" value="${fieldValue(bean:rsService,field:'lastChangedAt')}" />
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="observedStatus">observedStatus:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsService,field:'observedStatus','errors')}">
                            <input type="text" id="observedStatus" name="observedStatus" value="${fieldValue(bean:rsService,field:'observedStatus')}"/>
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
