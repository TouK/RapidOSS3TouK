

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Show ScienceFiction</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLinkTo(dir:'')}">Home</a></span>
    <span class="menuButton"><g:link class="list" action="list">ScienceFiction List</g:link></span>
    <span class="menuButton"><g:link class="create" action="create">New ScienceFiction</g:link></span>
</div>
<div class="body">
    <h1>Show ScienceFiction</h1>
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
                    
                    <td valign="top" class="value">${scienceFiction.id}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">name:</td>
                    
                    <td valign="top" class="value">${scienceFiction.name}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">authors:</td>
                    
                    <td valign="top" style="text-align:left;" class="value">
                        <ul>
                            <g:each var="a" in="${scienceFiction.authors}">
                                <li><g:link controller="author" action="show" id="${a.id}">${a}</g:link></li>
                            </g:each>
                        </ul>
                    </td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">description:</td>
                    
                    <td valign="top" class="value">${scienceFiction.description}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">mainCharacter:</td>
                    
                    <td valign="top" class="value"><g:link controller="person" action="show" id="${scienceFiction?.mainCharacter?.id}">${scienceFiction?.mainCharacter}</g:link></td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">mainCharacterName:</td>
                    
                    <td valign="top" class="value">${scienceFiction.mainCharacterName}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">publishDate:</td>
                    
                    <td valign="top" class="value">${scienceFiction.publishDate}</td>
                    
                </tr>
                
            </tbody>
        </table>
    </div>
    <div class="buttons">
        <g:form>
            <input type="hidden" name="id" value="${scienceFiction?.id}"/>
            <span class="button"><g:actionSubmit class="edit" value="Edit"/></span>
            <span class="button"><g:actionSubmit class="delete" onclick="return confirm('Are you sure?');" value="Delete"/></span>
        </g:form>
    </div>
</div>
</body>
</html>
