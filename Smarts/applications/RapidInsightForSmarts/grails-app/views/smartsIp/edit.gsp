

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Edit SmartsIp</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLinkTo(dir:'')}">Home</a></span>
    <span class="menuButton"><g:link class="list" action="list">SmartsIp List</g:link></span>
    <span class="menuButton"><g:link class="create" action="create">New SmartsIp</g:link></span>
</div>
<div class="body">
    <h1>Edit SmartsIp</h1>
    <g:if test="${flash.message}">
        <div class="message">${flash.message}</div>
    </g:if>
    <g:hasErrors bean="${smartsIp}">
        <div class="errors">
            <g:renderErrors bean="${smartsIp}" as="list"/>
        </div>
    </g:hasErrors>
    <g:hasErrors bean="${flash.errors}">
       <div class="errors">
            <g:renderErrors bean="${flash.errors}"/>
        </div>
    </g:hasErrors>
    <g:form method="post" >
        <input type="hidden" name="id" value="${smartsIp?.id}"/>
        <div class="dialog">
            <table>
                <tbody>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="name">name:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsIp,field:'name','errors')}">
                            <input type="text" id="name" name="name" value="${fieldValue(bean:smartsIp,field:'name')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="address">address:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsIp,field:'address','errors')}">
                            <input type="text" id="address" name="address" value="${fieldValue(bean:smartsIp,field:'address')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="className">className:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsIp,field:'className','errors')}">
                            <input type="text" id="className" name="className" value="${fieldValue(bean:smartsIp,field:'className')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="computerSystemName">computerSystemName:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsIp,field:'computerSystemName','errors')}">
                            <input type="text" id="computerSystemName" name="computerSystemName" value="${fieldValue(bean:smartsIp,field:'computerSystemName')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="description">description:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsIp,field:'description','errors')}">
                            <input type="text" id="description" name="description" value="${fieldValue(bean:smartsIp,field:'description')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="displayName">displayName:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsIp,field:'displayName','errors')}">
                            <input type="text" id="displayName" name="displayName" value="${fieldValue(bean:smartsIp,field:'displayName')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="hostedBy">hostedBy:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsIp,field:'hostedBy','errors')}">
                            <g:select optionKey="id" from="${SmartsComputerSystem.list()}" name="hostedBy.id" value="${smartsIp?.hostedBy?.id}" noSelection="['null':'']"></g:select>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="interfaceAdminStatus">interfaceAdminStatus:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsIp,field:'interfaceAdminStatus','errors')}">
                            <input type="text" id="interfaceAdminStatus" name="interfaceAdminStatus" value="${fieldValue(bean:smartsIp,field:'interfaceAdminStatus')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="interfaceMode">interfaceMode:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsIp,field:'interfaceMode','errors')}">
                            <input type="text" id="interfaceMode" name="interfaceMode" value="${fieldValue(bean:smartsIp,field:'interfaceMode')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="interfaceName">interfaceName:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsIp,field:'interfaceName','errors')}">
                            <input type="text" id="interfaceName" name="interfaceName" value="${fieldValue(bean:smartsIp,field:'interfaceName')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="interfaceOperStatus">interfaceOperStatus:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsIp,field:'interfaceOperStatus','errors')}">
                            <input type="text" id="interfaceOperStatus" name="interfaceOperStatus" value="${fieldValue(bean:smartsIp,field:'interfaceOperStatus')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="interfaceType">interfaceType:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsIp,field:'interfaceType','errors')}">
                            <input type="text" id="interfaceType" name="interfaceType" value="${fieldValue(bean:smartsIp,field:'interfaceType')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="ipStatus">ipStatus:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsIp,field:'ipStatus','errors')}">
                            <input type="text" id="ipStatus" name="ipStatus" value="${fieldValue(bean:smartsIp,field:'ipStatus')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="isManaged">isManaged:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsIp,field:'isManaged','errors')}">
                            <g:checkBox name="isManaged" value="${smartsIp?.isManaged}" ></g:checkBox>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="layeredOver">layeredOver:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsIp,field:'layeredOver','errors')}">
                            
<ul>
<g:each var="l" in="${smartsIp?.layeredOver?}">
    <li style="margin-bottom:3px;">
        <g:link controller="smartsComputerSystemComponent" action="show" id="${l.id}">${l}</g:link>
        <g:link class="delete" action="removeRelation" params="['id':smartsIp?.id, 'relationName':'layeredOver', 'relatedObjectId':l.id]"></g:link>
    </li>
</g:each>
</ul>
<g:link params="['id':smartsIp?.id, 'relationName':'layeredOver']" action="addTo">Add SmartsComputerSystemComponent</g:link>

                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="memberOfGroup">memberOfGroup:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsIp,field:'memberOfGroup','errors')}">
                            
<ul>
<g:each var="m" in="${smartsIp?.memberOfGroup?}">
    <li style="margin-bottom:3px;">
        <g:link controller="rsGroup" action="show" id="${m.id}">${m}</g:link>
        <g:link class="delete" action="removeRelation" params="['id':smartsIp?.id, 'relationName':'memberOfGroup', 'relatedObjectId':m.id]"></g:link>
    </li>
</g:each>
</ul>
<g:link params="['id':smartsIp?.id, 'relationName':'memberOfGroup']" action="addTo">Add RsGroup</g:link>

                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="netmask">netmask:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsIp,field:'netmask','errors')}">
                            <input type="text" id="netmask" name="netmask" value="${fieldValue(bean:smartsIp,field:'netmask')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="networkNumber">networkNumber:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsIp,field:'networkNumber','errors')}">
                            <input type="text" id="networkNumber" name="networkNumber" value="${fieldValue(bean:smartsIp,field:'networkNumber')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="partOf">partOf:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsIp,field:'partOf','errors')}">
                            <g:select optionKey="id" from="${SmartsComputerSystem.list()}" name="partOf.id" value="${smartsIp?.partOf?.id}" noSelection="['null':'']"></g:select>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="responsive">responsive:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsIp,field:'responsive','errors')}">
                            <g:checkBox name="responsive" value="${smartsIp?.responsive}" ></g:checkBox>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="rsDatasource">rsDatasource:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsIp,field:'rsDatasource','errors')}">
                            <input type="text" id="rsDatasource" name="rsDatasource" value="${fieldValue(bean:smartsIp,field:'rsDatasource')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="status">status:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsIp,field:'status','errors')}">
                            <input type="text" id="status" name="status" value="${fieldValue(bean:smartsIp,field:'status')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="tag">tag:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsIp,field:'tag','errors')}">
                            <input type="text" id="tag" name="tag" value="${fieldValue(bean:smartsIp,field:'tag')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="underlying">underlying:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsIp,field:'underlying','errors')}">
                            
<ul>
<g:each var="u" in="${smartsIp?.underlying?}">
    <li style="margin-bottom:3px;">
        <g:link controller="smartsComputerSystemComponent" action="show" id="${u.id}">${u}</g:link>
        <g:link class="delete" action="removeRelation" params="['id':smartsIp?.id, 'relationName':'underlying', 'relatedObjectId':u.id]"></g:link>
    </li>
</g:each>
</ul>
<g:link params="['id':smartsIp?.id, 'relationName':'underlying']" action="addTo">Add SmartsComputerSystemComponent</g:link>

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
