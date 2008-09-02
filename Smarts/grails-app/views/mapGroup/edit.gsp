

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Edit MapGroup</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLinkTo(dir:'')}">Home</a></span>
    <span class="menuButton"><g:link class="list" action="list">MapGroup List</g:link></span>
    <span class="menuButton"><g:link class="create" action="create">New MapGroup</g:link></span>
</div>
<div class="body">
    <h1>Edit MapGroup</h1>
    <g:if test="${flash.message}">
        <div class="message">${flash.message}</div>
    </g:if>
    <g:hasErrors bean="${mapGroup}">
        <div class="errors">
            <g:renderErrors bean="${mapGroup}" as="list"/>
        </div>
    </g:hasErrors>
    <g:hasErrors bean="${flash.errors}">
       <div class="errors">
            <g:renderErrors bean="${flash.errors}"/>
        </div>
    </g:hasErrors>
    <g:form method="post" >
        <input type="hidden" name="id" value="${mapGroup?.id}"/>
        <div class="dialog">
            <table>
                <tbody>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="groupName">groupName:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:mapGroup,field:'groupName','errors')}">
                            <input type="text" id="groupName" name="groupName" value="${fieldValue(bean:mapGroup,field:'groupName')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="username">username:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:mapGroup,field:'username','errors')}">
                            <input type="text" id="username" name="username" value="${fieldValue(bean:mapGroup,field:'username')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="maps">maps:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:mapGroup,field:'maps','errors')}">
                            
<ul>
<g:each var="m" in="${mapGroup?.maps?}">
    <li style="margin-bottom:3px;">
        <g:link controller="map" action="show" id="${m.id}">${m}</g:link>
        <g:link class="delete" action="removeRelation" params="['id':mapGroup?.id, 'relationName':'maps', 'relatedObjectId':m.id]"></g:link>
    </li>
</g:each>
</ul>
<g:link params="['id':mapGroup?.id, 'relationName':'maps']" action="addTo">Add Map</g:link>

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
