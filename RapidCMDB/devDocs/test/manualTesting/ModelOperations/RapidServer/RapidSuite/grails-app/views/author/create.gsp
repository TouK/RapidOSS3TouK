

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Create Author</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLinkTo(dir:'')}">Home</a></span>
    <span class="menuButton"><g:link class="list" action="list">Author List</g:link></span>
</div>
<div class="body">
    <h1>Create Author</h1>
    <g:if test="${flash.message}">
        <div class="message">${flash.message}</div>
    </g:if>
    <g:hasErrors bean="${author}">
        <div class="errors">
            <g:renderErrors bean="${author}" as="list"/>
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
                        <td valign="top" class="value ${hasErrors(bean:author,field:'name','errors')}">
                            <input type="text" id="name" name="name" value="${fieldValue(bean:author,field:'name')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="address">address:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:author,field:'address','errors')}">
                            <input type="text" id="address" name="address" value="${fieldValue(bean:author,field:'address')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="birthDate">birthDate:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:author,field:'birthDate','errors')}">
                            <g:datePicker name="birthDate" value="${author?.birthDate}" noSelection="['':'']"></g:datePicker>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="email">email:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:author,field:'email','errors')}">
                            <input type="text" id="email" name="email" value="${fieldValue(bean:author,field:'email')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="numberOfBooks">numberOfBooks:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:author,field:'numberOfBooks','errors')}">
                            <input type="text" id="numberOfBooks" name="numberOfBooks" value="${fieldValue(bean:author,field:'numberOfBooks')}" />
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
