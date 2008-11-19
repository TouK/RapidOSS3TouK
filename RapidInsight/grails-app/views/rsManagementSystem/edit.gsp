

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Edit RsManagementSystem</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLinkTo(dir:'')}">Home</a></span>
    <span class="menuButton"><g:link class="list" action="list">RsManagementSystem List</g:link></span>
    <span class="menuButton"><g:link class="create" action="create">New RsManagementSystem</g:link></span>
</div>
<div class="body">
    <h1>Edit RsManagementSystem</h1>
    <g:if test="${flash.message}">
        <div class="message">${flash.message}</div>
    </g:if>
    <g:hasErrors bean="${rsManagementSystem}">
        <div class="errors">
            <g:renderErrors bean="${rsManagementSystem}" as="list"/>
        </div>
    </g:hasErrors>
    <g:hasErrors bean="${flash.errors}">
       <div class="errors">
            <g:renderErrors bean="${flash.errors}"/>
        </div>
    </g:hasErrors>
    <g:form method="post" >
        <input type="hidden" name="id" value="${rsManagementSystem?.id}"/>
        <div class="dialog">
            <table>
                <tbody>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="name">name:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsManagementSystem,field:'name','errors')}">
                            <input type="text" id="name" name="name" value="${fieldValue(bean:rsManagementSystem,field:'name')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="lastPolledAt">lastPolledAt:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsManagementSystem,field:'lastPolledAt','errors')}">
                            <input type="text" id="lastPolledAt" name="lastPolledAt" value="${fieldValue(bean:rsManagementSystem,field:'lastPolledAt')}" />
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
