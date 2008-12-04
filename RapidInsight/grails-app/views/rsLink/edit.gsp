

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Edit RsLink</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLinkTo(dir:'')}">Home</a></span>
    <span class="menuButton"><g:link class="list" action="list">RsLink List</g:link></span>
    <span class="menuButton"><g:link class="create" action="create">New RsLink</g:link></span>
</div>
<div class="body">
    <h1>Edit RsLink</h1>
    <g:if test="${flash.message}">
        <div class="message">${flash.message}</div>
    </g:if>
    <g:hasErrors bean="${rsLink}">
        <div class="errors">
            <g:renderErrors bean="${rsLink}" as="list"/>
        </div>
    </g:hasErrors>
    <g:hasErrors bean="${flash.errors}">
       <div class="errors">
            <g:renderErrors bean="${flash.errors}"/>
        </div>
    </g:hasErrors>
    <g:form method="post" >
        <input type="hidden" name="id" value="${rsLink?.id}"/>
        <div class="dialog">
            <table>
                <tbody>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="name">name:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsLink,field:'name','errors')}">
                            <input type="text" id="name" name="name" value="${fieldValue(bean:rsLink,field:'name')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="a_ComputerSystemName">a_ComputerSystemName:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsLink,field:'a_ComputerSystemName','errors')}">
                            <input type="text" id="a_ComputerSystemName" name="a_ComputerSystemName" value="${fieldValue(bean:rsLink,field:'a_ComputerSystemName')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="a_Name">a_Name:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsLink,field:'a_Name','errors')}">
                            <input type="text" id="a_Name" name="a_Name" value="${fieldValue(bean:rsLink,field:'a_Name')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="className">className:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsLink,field:'className','errors')}">
                            <input type="text" id="className" name="className" value="${fieldValue(bean:rsLink,field:'className')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="connectedSystems">connectedSystems:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsLink,field:'connectedSystems','errors')}">
                            
<ul>
<g:each var="c" in="${rsLink?.connectedSystems?}">
    <li style="margin-bottom:3px;">
        <g:link controller="rsComputerSystem" action="show" id="${c.id}">${c}</g:link>
        <g:link class="delete" action="removeRelation" params="['id':rsLink?.id, 'relationName':'connectedSystems', 'relatedObjectId':c.id]"></g:link>
    </li>
</g:each>
</ul>
<g:link params="['id':rsLink?.id, 'relationName':'connectedSystems']" action="addTo">Add RsComputerSystem</g:link>

                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="description">description:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsLink,field:'description','errors')}">
                            <input type="text" id="description" name="description" value="${fieldValue(bean:rsLink,field:'description')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="displayName">displayName:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsLink,field:'displayName','errors')}">
                            <input type="text" id="displayName" name="displayName" value="${fieldValue(bean:rsLink,field:'displayName')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="isManaged">isManaged:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsLink,field:'isManaged','errors')}">
                            <g:checkBox name="isManaged" value="${rsLink?.isManaged}" ></g:checkBox>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="memberOfGroup">memberOfGroup:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsLink,field:'memberOfGroup','errors')}">
                            
<ul>
<g:each var="m" in="${rsLink?.memberOfGroup?}">
    <li style="margin-bottom:3px;">
        <g:link controller="rsGroup" action="show" id="${m.id}">${m}</g:link>
        <g:link class="delete" action="removeRelation" params="['id':rsLink?.id, 'relationName':'memberOfGroup', 'relatedObjectId':m.id]"></g:link>
    </li>
</g:each>
</ul>
<g:link params="['id':rsLink?.id, 'relationName':'memberOfGroup']" action="addTo">Add RsGroup</g:link>

                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="rsDatasource">rsDatasource:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsLink,field:'rsDatasource','errors')}">
                            <input type="text" id="rsDatasource" name="rsDatasource" value="${fieldValue(bean:rsLink,field:'rsDatasource')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="z_ComputerSystemName">z_ComputerSystemName:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsLink,field:'z_ComputerSystemName','errors')}">
                            <input type="text" id="z_ComputerSystemName" name="z_ComputerSystemName" value="${fieldValue(bean:rsLink,field:'z_ComputerSystemName')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="z_Name">z_Name:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsLink,field:'z_Name','errors')}">
                            <input type="text" id="z_Name" name="z_Name" value="${fieldValue(bean:rsLink,field:'z_Name')}"/>
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
