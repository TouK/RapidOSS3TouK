

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Show Service</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLinkTo(dir:'')}">Home</a></span>
    <span class="menuButton"><g:link class="list" action="list">Service List</g:link></span>
    <span class="menuButton"><g:link class="create" action="create">New Service</g:link></span>
</div>
<div class="body">
    <h1>Show Service</h1>
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
                    
                    <td valign="top" class="value">${service.id}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">resource_name:</td>
                    
                    <td valign="top" class="value">${service.resource_name}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">hypericEvents:</td>
                    
                    <td valign="top" style="text-align:left;" class="value">
                        <ul>
                            <g:each var="h" in="${service.hypericEvents}">
                                <li><g:link controller="hypericEvent" action="show" id="${h.id}">${h}</g:link></li>
                            </g:each>
                        </ul>
                    </td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">serviceOf:</td>
                    
                    <td valign="top" class="value"><g:link controller="server" action="show" id="${service?.serviceOf?.id}">${service?.serviceOf}</g:link></td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">status:</td>
                    
                    <td valign="top" class="value">${service.status}</td>
                    
                </tr>
                
            </tbody>
        </table>
    </div>
    <div class="buttons">
        <g:form>
            <input type="hidden" name="id" value="${service?.id}"/>
            <span class="button"><g:actionSubmit class="edit" value="Edit"/></span>
            <span class="button"><g:actionSubmit class="delete" onclick="return confirm('Are you sure?');" value="Delete"/></span>
        </g:form>
    </div>
</div>
</body>
</html>
