

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Edit ScienceFiction</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLinkTo(dir:'')}">Home</a></span>
    <span class="menuButton"><g:link class="list" action="list">ScienceFiction List</g:link></span>
    <span class="menuButton"><g:link class="create" action="create">New ScienceFiction</g:link></span>
</div>
<div class="body">
    <h1>Edit ScienceFiction</h1>
    <g:if test="${flash.message}">
        <div class="message">${flash.message}</div>
    </g:if>
    <g:hasErrors bean="${scienceFiction}">
        <div class="errors">
            <g:renderErrors bean="${scienceFiction}" as="list"/>
        </div>
    </g:hasErrors>
    <g:hasErrors bean="${flash.errors}">
       <div class="errors">
            <g:renderErrors bean="${flash.errors}"/>
        </div>
    </g:hasErrors>
    <g:form method="post" >
        <input type="hidden" name="id" value="${scienceFiction?.id}"/>
        <div class="dialog">
            <table>
                <tbody>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="name">name:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:scienceFiction,field:'name','errors')}">
                            <input type="text" id="name" name="name" value="${fieldValue(bean:scienceFiction,field:'name')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="authors">authors:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:scienceFiction,field:'authors','errors')}">
                            
<ul>
<g:each var="a" in="${scienceFiction?.authors?}">
    <li style="margin-bottom:3px;">
        <g:link controller="author" action="show" id="${a.id}">${a}</g:link>
        <g:link class="delete" action="removeRelation" params="['id':scienceFiction?.id, 'relationName':'authors', 'relatedObjectId':a.id]"></g:link>
    </li>
</g:each>
</ul>
<g:link params="['id':scienceFiction?.id, 'relationName':'authors']" action="addTo">Add Author</g:link>

                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="description">description:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:scienceFiction,field:'description','errors')}">
                            <input type="text" id="description" name="description" value="${fieldValue(bean:scienceFiction,field:'description')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="mainCharacter">mainCharacter:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:scienceFiction,field:'mainCharacter','errors')}">
                            <g:select optionKey="id" from="${Person.list()}" name="mainCharacter.id" value="${scienceFiction?.mainCharacter?.id}" noSelection="['null':'']"></g:select>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="mainCharacterName">mainCharacterName:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:scienceFiction,field:'mainCharacterName','errors')}">
                            <input type="text" id="mainCharacterName" name="mainCharacterName" value="${fieldValue(bean:scienceFiction,field:'mainCharacterName')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="publishDate">publishDate:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:scienceFiction,field:'publishDate','errors')}">
                            <g:datePicker name="publishDate" value="${scienceFiction?.publishDate}" noSelection="['':'']"></g:datePicker>
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
