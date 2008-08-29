

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Create Service</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLinkTo(dir:'')}">Home</a></span>
    <span class="menuButton"><g:link class="list" action="list">Service List</g:link></span>
</div>
<div class="body">
    <h1>Create Service</h1>
    <g:if test="${flash.message}">
        <div class="message">${flash.message}</div>
    </g:if>
    <g:hasErrors bean="${service}">
        <div class="errors">
            <g:renderErrors bean="${service}" as="list"/>
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
                            <label for="resource_name">resource_name:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:service,field:'resource_name','errors')}">
                            <input type="text" id="resource_name" name="resource_name" value="${fieldValue(bean:service,field:'resource_name')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="serviceOf">serviceOf:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:service,field:'serviceOf','errors')}">
                            <g:select optionKey="id" from="${Server.list()}" name="serviceOf.id" value="${service?.serviceOf?.id}" noSelection="['null':'']"></g:select>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="status">status:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:service,field:'status','errors')}">
                            <input type="text" id="status" name="status" value="${fieldValue(bean:service,field:'status')}"/>
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
