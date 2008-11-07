

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Show Fiction</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLinkTo(dir:'')}">Home</a></span>
    <span class="menuButton"><g:link class="list" action="list">Fiction List</g:link></span>
    <span class="menuButton"><g:link class="create" action="create">New Fiction</g:link></span>
</div>
<div class="body">
    <h1>Show Fiction</h1>
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
                    
                    <td valign="top" class="value">${fiction.id}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">name:</td>
                    
                    <td valign="top" class="value">${fiction.name}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">authors:</td>
                    
                    <td valign="top" style="text-align:left;" class="value">
                        <ul>
                            <g:each var="a" in="${fiction.authors}">
                                <li><g:link controller="author" action="show" id="${a.id}">${a}</g:link></li>
                            </g:each>
                        </ul>
                    </td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">description:</td>
                    
                    <td valign="top" class="value">${fiction.description}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">mainCharacter:</td>
                    
                    <td valign="top" class="value"><g:link controller="person" action="show" id="${fiction?.mainCharacter?.id}">${fiction?.mainCharacter}</g:link></td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">mainCharacterName:</td>
                    
                    <td valign="top" class="value">${fiction.mainCharacterName}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">publishDate:</td>
                    
                    <td valign="top" class="value">${fiction.publishDate}</td>
                    
                </tr>
                
            </tbody>
        </table>
    </div>
    <div class="buttons">
        <g:form>
            <input type="hidden" name="id" value="${fiction?.id}"/>
            <span class="button"><g:actionSubmit class="edit" value="Edit"/></span>
            <span class="button"><g:actionSubmit class="delete" onclick="return confirm('Are you sure?');" value="Delete"/></span>
        </g:form>
    </div>
</div>
</body>
</html>
