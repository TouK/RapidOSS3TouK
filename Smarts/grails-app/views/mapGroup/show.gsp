

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Show MapGroup</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLinkTo(dir:'')}">Home</a></span>
    <span class="menuButton"><g:link class="list" action="list">MapGroup List</g:link></span>
    <span class="menuButton"><g:link class="create" action="create">New MapGroup</g:link></span>
</div>
<div class="body">
    <h1>Show MapGroup</h1>
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
                    
                    <td valign="top" class="value">${mapGroup.id}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">groupName:</td>
                    
                    <td valign="top" class="value">${mapGroup.groupName}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">username:</td>
                    
                    <td valign="top" class="value">${mapGroup.username}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">maps:</td>
                    
                    <td valign="top" style="text-align:left;" class="value">
                        <ul>
                            <g:each var="m" in="${mapGroup.maps}">
                                <li><g:link controller="map" action="show" id="${m.id}">${m}</g:link></li>
                            </g:each>
                        </ul>
                    </td>
                    
                </tr>
                
            </tbody>
        </table>
    </div>
    <div class="buttons">
        <g:form>
            <input type="hidden" name="id" value="${mapGroup?.id}"/>
            <span class="button"><g:actionSubmit class="edit" value="Edit"/></span>
            <span class="button"><g:actionSubmit class="delete" onclick="return confirm('Are you sure?');" value="Delete"/></span>
        </g:form>
    </div>
</div>
</body>
</html>
