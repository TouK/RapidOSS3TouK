

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Create Book</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLinkTo(dir:'')}">Home</a></span>
    <span class="menuButton"><g:link class="list" action="list">Book List</g:link></span>
</div>
<div class="body">
    <h1>Create Book</h1>
    <g:if test="${flash.message}">
        <div class="message">${flash.message}</div>
    </g:if>
    <g:hasErrors bean="${book}">
        <div class="errors">
            <g:renderErrors bean="${book}" as="list"/>
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
                        <td valign="top" class="value ${hasErrors(bean:book,field:'name','errors')}">
                            <input type="text" id="name" name="name" value="${fieldValue(bean:book,field:'name')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="description">description:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:book,field:'description','errors')}">
                            <input type="text" id="description" name="description" value="${fieldValue(bean:book,field:'description')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="publishDate">publishDate:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:book,field:'publishDate','errors')}">
                            <g:datePicker name="publishDate" value="${book?.publishDate}" noSelection="['':'']"></g:datePicker>
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
