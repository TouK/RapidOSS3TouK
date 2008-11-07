

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Create Person</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLinkTo(dir:'')}">Home</a></span>
    <span class="menuButton"><g:link class="list" action="list">Person List</g:link></span>
</div>
<div class="body">
    <h1>Create Person</h1>
    <g:if test="${flash.message}">
        <div class="message">${flash.message}</div>
    </g:if>
    <g:hasErrors bean="${person}">
        <div class="errors">
            <g:renderErrors bean="${person}" as="list"/>
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
                        <td valign="top" class="value ${hasErrors(bean:person,field:'name','errors')}">
                            <input type="text" id="name" name="name" value="${fieldValue(bean:person,field:'name')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="address">address:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:person,field:'address','errors')}">
                            <input type="text" id="address" name="address" value="${fieldValue(bean:person,field:'address')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="birthDate">birthDate:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:person,field:'birthDate','errors')}">
                            <g:datePicker name="birthDate" value="${person?.birthDate}" noSelection="['':'']"></g:datePicker>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="email">email:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:person,field:'email','errors')}">
                            <input type="text" id="email" name="email" value="${fieldValue(bean:person,field:'email')}"/>
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
