

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Show OpenNmsGraph</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLinkTo(dir:'')}">Home</a></span>
    <span class="menuButton"><g:link class="list" action="list">OpenNmsGraph List</g:link></span>
    <span class="menuButton"><g:link class="create" action="create">New OpenNmsGraph</g:link></span>
</div>
<div class="body">
    <h1>Show OpenNmsGraph</h1>
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
                    
                    <td valign="top" class="value">${openNmsGraph.id}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">url:</td>
                    
                    <td valign="top" class="value">${openNmsGraph.url}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">graphOf:</td>
                    
                    <td valign="top" class="value"><g:link controller="openNmsObject" action="show" id="${openNmsGraph?.graphOf?.id}">${openNmsGraph?.graphOf}</g:link></td>
                    
                </tr>
                
            </tbody>
        </table>
    </div>
    <div class="buttons">
        <g:form>
            <input type="hidden" name="id" value="${openNmsGraph?.id}"/>
            <span class="button"><g:actionSubmit class="edit" value="Edit"/></span>
            <span class="button"><g:actionSubmit class="delete" onclick="return confirm('Are you sure?');" value="Delete"/></span>
        </g:form>
    </div>
</div>
</body>
</html>
