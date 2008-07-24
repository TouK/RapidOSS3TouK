

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Show NetcoolJournal</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLinkTo(dir:'')}">Home</a></span>
    <span class="menuButton"><g:link class="list" action="list">NetcoolJournal List</g:link></span>
    <span class="menuButton"><g:link class="create" action="create">New NetcoolJournal</g:link></span>
</div>
<div class="body">
    <h1>Show NetcoolJournal</h1>
    <g:if test="${flash.message}">
        <div class="message">${flash.message}</div>
    </g:if>
    <g:if test="${flash.errors}">
        <div class="errors">
            <ul>
                <g:each var="error" in="${flash?.errors}">
                    <li>${error}</li>
                </g:each>
            </ul>
        </div>
    </g:if>
    <div class="dialog">
        <table>
            <tbody>

                
                <tr class="prop">
                    <td valign="top" class="name">id:</td>
                    
                    <td valign="top" class="value">${netcoolJournal.id}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">keyfield:</td>
                    
                    <td valign="top" class="value">${netcoolJournal.keyfield}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">servername:</td>
                    
                    <td valign="top" class="value">${netcoolJournal.servername}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">chrono:</td>
                    
                    <td valign="top" class="value">${netcoolJournal.chrono}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">connectorname:</td>
                    
                    <td valign="top" class="value">${netcoolJournal.connectorname}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">serverserial:</td>
                    
                    <td valign="top" class="value">${netcoolJournal.serverserial}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">text:</td>
                    
                    <td valign="top" class="value">${netcoolJournal.text}</td>
                    
                </tr>
                
            </tbody>
        </table>
    </div>
    <div class="buttons">
        <g:form>
            <input type="hidden" name="id" value="${netcoolJournal?.id}"/>
            <span class="button"><g:actionSubmit class="edit" value="Edit"/></span>
            <span class="button"><g:actionSubmit class="delete" onclick="return confirm('Are you sure?');" value="Delete"/></span>
        </g:form>
    </div>
</div>
</body>
</html>
