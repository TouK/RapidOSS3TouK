

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Edit RsVlan</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLinkTo(dir:'')}">Home</a></span>
    <span class="menuButton"><g:link class="list" action="list">RsVlan List</g:link></span>
    <span class="menuButton"><g:link class="create" action="create">New RsVlan</g:link></span>
</div>
<div class="body">
    <h1>Edit RsVlan</h1>
    <g:if test="${flash.message}">
        <div class="message">${flash.message}</div>
    </g:if>
    <g:hasErrors bean="${rsVlan}">
        <div class="errors">
            <g:renderErrors bean="${rsVlan}" as="list"/>
        </div>
    </g:hasErrors>
    <g:hasErrors bean="${flash.errors}">
       <div class="errors">
            <g:renderErrors bean="${flash.errors}"/>
        </div>
    </g:hasErrors>
    <g:form method="post" >
        <input type="hidden" name="id" value="${rsVlan?.id}"/>
        <div class="dialog">
            <table>
                <tbody>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="name">name:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsVlan,field:'name','errors')}">
                            <input type="text" id="name" name="name" value="${fieldValue(bean:rsVlan,field:'name')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="creationClassName">creationClassName:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsVlan,field:'creationClassName','errors')}">
                            <input type="text" id="creationClassName" name="creationClassName" value="${fieldValue(bean:rsVlan,field:'creationClassName')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="description">description:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsVlan,field:'description','errors')}">
                            <input type="text" id="description" name="description" value="${fieldValue(bean:rsVlan,field:'description')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="displayName">displayName:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsVlan,field:'displayName','errors')}">
                            <input type="text" id="displayName" name="displayName" value="${fieldValue(bean:rsVlan,field:'displayName')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="isManaged">isManaged:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsVlan,field:'isManaged','errors')}">
                            <g:checkBox name="isManaged" value="${rsVlan?.isManaged}" ></g:checkBox>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="memberOfGroup">memberOfGroup:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsVlan,field:'memberOfGroup','errors')}">
                            
<ul>
<g:each var="m" in="${rsVlan?.memberOfGroup?}">
    <li style="margin-bottom:3px;">
        <g:link controller="rsGroup" action="show" id="${m.id}">${m}</g:link>
        <g:link class="delete" action="removeRelation" params="['id':rsVlan?.id, 'relationName':'memberOfGroup', 'relatedObjectId':m.id]"></g:link>
    </li>
</g:each>
</ul>
<g:link params="['id':rsVlan?.id, 'relationName':'memberOfGroup']" action="addTo">Add RsGroup</g:link>

                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="rsDatasource">rsDatasource:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsVlan,field:'rsDatasource','errors')}">
                            <input type="text" id="rsDatasource" name="rsDatasource" value="${fieldValue(bean:rsVlan,field:'rsDatasource')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="vlanKey">vlanKey:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsVlan,field:'vlanKey','errors')}">
                            <input type="text" id="vlanKey" name="vlanKey" value="${fieldValue(bean:rsVlan,field:'vlanKey')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="vlanNumber">vlanNumber:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsVlan,field:'vlanNumber','errors')}">
                            <input type="text" id="vlanNumber" name="vlanNumber" value="${fieldValue(bean:rsVlan,field:'vlanNumber')}" />
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
