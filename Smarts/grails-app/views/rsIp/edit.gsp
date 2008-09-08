

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Edit RsIp</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLinkTo(dir:'')}">Home</a></span>
    <span class="menuButton"><g:link class="list" action="list">RsIp List</g:link></span>
    <span class="menuButton"><g:link class="create" action="create">New RsIp</g:link></span>
</div>
<div class="body">
    <h1>Edit RsIp</h1>
    <g:if test="${flash.message}">
        <div class="message">${flash.message}</div>
    </g:if>
    <g:hasErrors bean="${rsIp}">
        <div class="errors">
            <g:renderErrors bean="${rsIp}" as="list"/>
        </div>
    </g:hasErrors>
    <g:hasErrors bean="${flash.errors}">
       <div class="errors">
            <g:renderErrors bean="${flash.errors}"/>
        </div>
    </g:hasErrors>
    <g:form method="post" >
        <input type="hidden" name="id" value="${rsIp?.id}"/>
        <div class="dialog">
            <table>
                <tbody>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="name">name:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsIp,field:'name','errors')}">
                            <input type="text" id="name" name="name" value="${fieldValue(bean:rsIp,field:'name')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="address">address:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsIp,field:'address','errors')}">
                            <input type="text" id="address" name="address" value="${fieldValue(bean:rsIp,field:'address')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="computerSystemName">computerSystemName:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsIp,field:'computerSystemName','errors')}">
                            <input type="text" id="computerSystemName" name="computerSystemName" value="${fieldValue(bean:rsIp,field:'computerSystemName')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="creationClassName">creationClassName:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsIp,field:'creationClassName','errors')}">
                            <input type="text" id="creationClassName" name="creationClassName" value="${fieldValue(bean:rsIp,field:'creationClassName')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="description">description:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsIp,field:'description','errors')}">
                            <input type="text" id="description" name="description" value="${fieldValue(bean:rsIp,field:'description')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="displayName">displayName:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsIp,field:'displayName','errors')}">
                            <input type="text" id="displayName" name="displayName" value="${fieldValue(bean:rsIp,field:'displayName')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="hostedBy">hostedBy:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsIp,field:'hostedBy','errors')}">
                            <g:select optionKey="id" from="${RsComputerSystem.list()}" name="hostedBy.id" value="${rsIp?.hostedBy?.id}" noSelection="['null':'']"></g:select>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="interfaceAdminStatus">interfaceAdminStatus:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsIp,field:'interfaceAdminStatus','errors')}">
                            <input type="text" id="interfaceAdminStatus" name="interfaceAdminStatus" value="${fieldValue(bean:rsIp,field:'interfaceAdminStatus')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="interfaceMode">interfaceMode:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsIp,field:'interfaceMode','errors')}">
                            <input type="text" id="interfaceMode" name="interfaceMode" value="${fieldValue(bean:rsIp,field:'interfaceMode')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="interfaceName">interfaceName:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsIp,field:'interfaceName','errors')}">
                            <input type="text" id="interfaceName" name="interfaceName" value="${fieldValue(bean:rsIp,field:'interfaceName')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="interfaceOperStatus">interfaceOperStatus:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsIp,field:'interfaceOperStatus','errors')}">
                            <input type="text" id="interfaceOperStatus" name="interfaceOperStatus" value="${fieldValue(bean:rsIp,field:'interfaceOperStatus')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="interfaceType">interfaceType:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsIp,field:'interfaceType','errors')}">
                            <input type="text" id="interfaceType" name="interfaceType" value="${fieldValue(bean:rsIp,field:'interfaceType')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="ipStatus">ipStatus:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsIp,field:'ipStatus','errors')}">
                            <input type="text" id="ipStatus" name="ipStatus" value="${fieldValue(bean:rsIp,field:'ipStatus')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="isManaged">isManaged:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsIp,field:'isManaged','errors')}">
                            <g:checkBox name="isManaged" value="${rsIp?.isManaged}" ></g:checkBox>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="layeredOver">layeredOver:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsIp,field:'layeredOver','errors')}">
                            
<ul>
<g:each var="l" in="${rsIp?.layeredOver?}">
    <li style="margin-bottom:3px;">
        <g:link controller="rsComputerSystemComponent" action="show" id="${l.id}">${l}</g:link>
        <g:link class="delete" action="removeRelation" params="['id':rsIp?.id, 'relationName':'layeredOver', 'relatedObjectId':l.id]"></g:link>
    </li>
</g:each>
</ul>
<g:link params="['id':rsIp?.id, 'relationName':'layeredOver']" action="addTo">Add RsComputerSystemComponent</g:link>

                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="memberOfGroup">memberOfGroup:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsIp,field:'memberOfGroup','errors')}">
                            
<ul>
<g:each var="m" in="${rsIp?.memberOfGroup?}">
    <li style="margin-bottom:3px;">
        <g:link controller="rsGroup" action="show" id="${m.id}">${m}</g:link>
        <g:link class="delete" action="removeRelation" params="['id':rsIp?.id, 'relationName':'memberOfGroup', 'relatedObjectId':m.id]"></g:link>
    </li>
</g:each>
</ul>
<g:link params="['id':rsIp?.id, 'relationName':'memberOfGroup']" action="addTo">Add RsGroup</g:link>

                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="netmask">netmask:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsIp,field:'netmask','errors')}">
                            <input type="text" id="netmask" name="netmask" value="${fieldValue(bean:rsIp,field:'netmask')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="networkNumber">networkNumber:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsIp,field:'networkNumber','errors')}">
                            <input type="text" id="networkNumber" name="networkNumber" value="${fieldValue(bean:rsIp,field:'networkNumber')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="partOf">partOf:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsIp,field:'partOf','errors')}">
                            <g:select optionKey="id" from="${RsComputerSystem.list()}" name="partOf.id" value="${rsIp?.partOf?.id}" noSelection="['null':'']"></g:select>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="responsive">responsive:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsIp,field:'responsive','errors')}">
                            <g:checkBox name="responsive" value="${rsIp?.responsive}" ></g:checkBox>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="rsDatasource">rsDatasource:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsIp,field:'rsDatasource','errors')}">
                            <input type="text" id="rsDatasource" name="rsDatasource" value="${fieldValue(bean:rsIp,field:'rsDatasource')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="status">status:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsIp,field:'status','errors')}">
                            <input type="text" id="status" name="status" value="${fieldValue(bean:rsIp,field:'status')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="tag">tag:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsIp,field:'tag','errors')}">
                            <input type="text" id="tag" name="tag" value="${fieldValue(bean:rsIp,field:'tag')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="underlying">underlying:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsIp,field:'underlying','errors')}">
                            
<ul>
<g:each var="u" in="${rsIp?.underlying?}">
    <li style="margin-bottom:3px;">
        <g:link controller="rsComputerSystemComponent" action="show" id="${u.id}">${u}</g:link>
        <g:link class="delete" action="removeRelation" params="['id':rsIp?.id, 'relationName':'underlying', 'relatedObjectId':u.id]"></g:link>
    </li>
</g:each>
</ul>
<g:link params="['id':rsIp?.id, 'relationName':'underlying']" action="addTo">Add RsComputerSystemComponent</g:link>

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
