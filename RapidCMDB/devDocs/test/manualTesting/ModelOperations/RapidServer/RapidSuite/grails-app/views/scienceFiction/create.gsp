

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Create ScienceFiction</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLinkTo(dir:'')}">Home</a></span>
    <span class="menuButton"><g:link class="list" action="list">ScienceFiction List</g:link></span>
</div>
<div class="body">
    <h1>Create ScienceFiction</h1>
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
    <g:form action="save" method="post" >
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
            <span class="button"><input class="save" type="submit" value="Create"/></span>
        </div>
    </g:form>
</div>
</body>
</html>
