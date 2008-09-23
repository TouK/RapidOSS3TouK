

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Edit RsEventJournal</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLinkTo(dir:'')}">Home</a></span>
    <span class="menuButton"><g:link class="list" action="list">RsEventJournal List</g:link></span>
    <span class="menuButton"><g:link class="create" action="create">New RsEventJournal</g:link></span>
</div>
<div class="body">
    <h1>Edit RsEventJournal</h1>
    <g:if test="${flash.message}">
        <div class="message">${flash.message}</div>
    </g:if>
    <g:hasErrors bean="${rsEventJournal}">
        <div class="errors">
            <g:renderErrors bean="${rsEventJournal}" as="list"/>
        </div>
    </g:hasErrors>
    <g:hasErrors bean="${flash.errors}">
       <div class="errors">
            <g:renderErrors bean="${flash.errors}"/>
        </div>
    </g:hasErrors>
    <g:form method="post" >
        <input type="hidden" name="id" value="${rsEventJournal?.id}"/>
        <div class="dialog">
            <table>
                <tbody>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="id">id:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsEventJournal,field:'id','errors')}">
                            <input type="text" id="id" name="id" value="${fieldValue(bean:rsEventJournal,field:'id')}" />
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="details">details:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsEventJournal,field:'details','errors')}">
                            <input type="text" id="details" name="details" value="${fieldValue(bean:rsEventJournal,field:'details')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="eventId">eventId:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsEventJournal,field:'eventId','errors')}">
                            <input type="text" id="eventId" name="eventId" value="${fieldValue(bean:rsEventJournal,field:'eventId')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="eventName">eventName:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsEventJournal,field:'eventName','errors')}">
                            <input type="text" id="eventName" name="eventName" value="${fieldValue(bean:rsEventJournal,field:'eventName')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="rsTime">rsTime:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsEventJournal,field:'rsTime','errors')}">
                            <g:datePicker name="rsTime" value="${rsEventJournal?.rsTime}" noSelection="['':'']"></g:datePicker>
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
