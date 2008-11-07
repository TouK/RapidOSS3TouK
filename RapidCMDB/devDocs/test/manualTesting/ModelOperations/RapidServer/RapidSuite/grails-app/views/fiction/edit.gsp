

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Edit Fiction</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLinkTo(dir:'')}">Home</a></span>
    <span class="menuButton"><g:link class="list" action="list">Fiction List</g:link></span>
    <span class="menuButton"><g:link class="create" action="create">New Fiction</g:link></span>
</div>
<div class="body">
    <h1>Edit Fiction</h1>
    <g:if test="${flash.message}">
        <div class="message">${flash.message}</div>
    </g:if>
    <g:hasErrors bean="${fiction}">
        <div class="errors">
            <g:renderErrors bean="${fiction}" as="list"/>
        </div>
    </g:hasErrors>
    <g:hasErrors bean="${flash.errors}">
       <div class="errors">
            <g:renderErrors bean="${flash.errors}"/>
        </div>
    </g:hasErrors>
    <g:form method="post" >
        <input type="hidden" name="id" value="${fiction?.id}"/>
        <div class="dialog">
            <table>
                <tbody>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="name">name:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:fiction,field:'name','errors')}">
                            <input type="text" id="name" name="name" value="${fieldValue(bean:fiction,field:'name')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="authors">authors:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:fiction,field:'authors','errors')}">
                            
<ul>
<g:each var="a" in="${fiction?.authors?}">
    <li style="margin-bottom:3px;">
        <g:link controller="author" action="show" id="${a.id}">${a}</g:link>
        <g:link class="delete" action="removeRelation" params="['id':fiction?.id, 'relationName':'authors', 'relatedObjectId':a.id]"></g:link>
    </li>
</g:each>
</ul>
<g:link params="['id':fiction?.id, 'relationName':'authors']" action="addTo">Add Author</g:link>

                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="description">description:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:fiction,field:'description','errors')}">
                            <input type="text" id="description" name="description" value="${fieldValue(bean:fiction,field:'description')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="mainCharacter">mainCharacter:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:fiction,field:'mainCharacter','errors')}">
                            <g:select optionKey="id" from="${Person.list()}" name="mainCharacter.id" value="${fiction?.mainCharacter?.id}" noSelection="['null':'']"></g:select>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="mainCharacterName">mainCharacterName:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:fiction,field:'mainCharacterName','errors')}">
                            <input type="text" id="mainCharacterName" name="mainCharacterName" value="${fieldValue(bean:fiction,field:'mainCharacterName')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="publishDate">publishDate:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:fiction,field:'publishDate','errors')}">
                            <g:datePicker name="publishDate" value="${fiction?.publishDate}" noSelection="['':'']"></g:datePicker>
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
