

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Show RsEventJournal</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLinkTo(dir:'')}">Home</a></span>
    <span class="menuButton"><g:link class="list" action="list">RsEventJournal List</g:link></span>
    <span class="menuButton"><g:link class="create" action="create">New RsEventJournal</g:link></span>
</div>
<div class="body">
    <h1>Show RsEventJournal</h1>
    <g:if test="${flash.message}">
        <div class="message">${flash.message}</div>
    </g:if>
    <g:hasErrors bean="${flash.errors}">
       <div class="errors">
            <g:renderErrors bean="${flash.errors}"/>
        </div>
    </g:hasErrors>
    <div class="dialog">
        <table>
            <tbody>

                
                <tr class="prop">
                    <td valign="top" class="name">id:</td>
                    
                    <td valign="top" class="value">${rsEventJournal.id}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">details:</td>
                    
                    <td valign="top" class="value">${rsEventJournal.details}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">eventId:</td>
                    
                    <td valign="top" class="value">${rsEventJournal.eventId}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">eventName:</td>
                    
                    <td valign="top" class="value">${rsEventJournal.eventName}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">rsTime:</td>
                    
                    <td valign="top" class="value">${rsEventJournal.rsTime}</td>
                    
                </tr>
                
            </tbody>
        </table>
    </div>
    <div class="buttons">
        <g:form>
            <input type="hidden" name="id" value="${rsEventJournal?.id}"/>
            <span class="button"><g:actionSubmit class="edit" value="Edit"/></span>
            <span class="button"><g:actionSubmit class="delete" onclick="return confirm('Are you sure?');" value="Delete"/></span>
        </g:form>
    </div>
</div>
</body>
</html>
