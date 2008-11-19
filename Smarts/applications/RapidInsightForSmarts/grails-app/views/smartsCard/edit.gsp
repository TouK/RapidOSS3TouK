

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Edit SmartsCard</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLinkTo(dir:'')}">Home</a></span>
    <span class="menuButton"><g:link class="list" action="list">SmartsCard List</g:link></span>
    <span class="menuButton"><g:link class="create" action="create">New SmartsCard</g:link></span>
</div>
<div class="body">
    <h1>Edit SmartsCard</h1>
    <g:if test="${flash.message}">
        <div class="message">${flash.message}</div>
    </g:if>
    <g:hasErrors bean="${smartsCard}">
        <div class="errors">
            <g:renderErrors bean="${smartsCard}" as="list"/>
        </div>
    </g:hasErrors>
    <g:hasErrors bean="${flash.errors}">
       <div class="errors">
            <g:renderErrors bean="${flash.errors}"/>
        </div>
    </g:hasErrors>
    <g:form method="post" >
        <input type="hidden" name="id" value="${smartsCard?.id}"/>
        <div class="dialog">
            <table>
                <tbody>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="name">name:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsCard,field:'name','errors')}">
                            <input type="text" id="name" name="name" value="${fieldValue(bean:smartsCard,field:'name')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="className">className:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsCard,field:'className','errors')}">
                            <input type="text" id="className" name="className" value="${fieldValue(bean:smartsCard,field:'className')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="computerSystemName">computerSystemName:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsCard,field:'computerSystemName','errors')}">
                            <input type="text" id="computerSystemName" name="computerSystemName" value="${fieldValue(bean:smartsCard,field:'computerSystemName')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="description">description:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsCard,field:'description','errors')}">
                            <input type="text" id="description" name="description" value="${fieldValue(bean:smartsCard,field:'description')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="displayName">displayName:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsCard,field:'displayName','errors')}">
                            <input type="text" id="displayName" name="displayName" value="${fieldValue(bean:smartsCard,field:'displayName')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="isManaged">isManaged:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsCard,field:'isManaged','errors')}">
                            <g:checkBox name="isManaged" value="${smartsCard?.isManaged}" ></g:checkBox>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="layeredOver">layeredOver:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsCard,field:'layeredOver','errors')}">
                            
<ul>
<g:each var="l" in="${smartsCard?.layeredOver?}">
    <li style="margin-bottom:3px;">
        <g:link controller="smartsComputerSystemComponent" action="show" id="${l.id}">${l}</g:link>
        <g:link class="delete" action="removeRelation" params="['id':smartsCard?.id, 'relationName':'layeredOver', 'relatedObjectId':l.id]"></g:link>
    </li>
</g:each>
</ul>
<g:link params="['id':smartsCard?.id, 'relationName':'layeredOver']" action="addTo">Add SmartsComputerSystemComponent</g:link>

                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="memberOfGroup">memberOfGroup:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsCard,field:'memberOfGroup','errors')}">
                            
<ul>
<g:each var="m" in="${smartsCard?.memberOfGroup?}">
    <li style="margin-bottom:3px;">
        <g:link controller="rsGroup" action="show" id="${m.id}">${m}</g:link>
        <g:link class="delete" action="removeRelation" params="['id':smartsCard?.id, 'relationName':'memberOfGroup', 'relatedObjectId':m.id]"></g:link>
    </li>
</g:each>
</ul>
<g:link params="['id':smartsCard?.id, 'relationName':'memberOfGroup']" action="addTo">Add RsGroup</g:link>

                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="partOf">partOf:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsCard,field:'partOf','errors')}">
                            <g:select optionKey="id" from="${SmartsComputerSystem.list()}" name="partOf.id" value="${smartsCard?.partOf?.id}" noSelection="['null':'']"></g:select>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="realizes">realizes:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsCard,field:'realizes','errors')}">
                            
<ul>
<g:each var="r" in="${smartsCard?.realizes?}">
    <li style="margin-bottom:3px;">
        <g:link controller="smartsNetworkAdapter" action="show" id="${r.id}">${r}</g:link>
        <g:link class="delete" action="removeRelation" params="['id':smartsCard?.id, 'relationName':'realizes', 'relatedObjectId':r.id]"></g:link>
    </li>
</g:each>
</ul>
<g:link params="['id':smartsCard?.id, 'relationName':'realizes']" action="addTo">Add SmartsNetworkAdapter</g:link>

                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="rsDatasource">rsDatasource:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsCard,field:'rsDatasource','errors')}">
                            <input type="text" id="rsDatasource" name="rsDatasource" value="${fieldValue(bean:smartsCard,field:'rsDatasource')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="serialNumber">serialNumber:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsCard,field:'serialNumber','errors')}">
                            <input type="text" id="serialNumber" name="serialNumber" value="${fieldValue(bean:smartsCard,field:'serialNumber')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="standbyStatus">standbyStatus:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsCard,field:'standbyStatus','errors')}">
                            <input type="text" id="standbyStatus" name="standbyStatus" value="${fieldValue(bean:smartsCard,field:'standbyStatus')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="status">status:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsCard,field:'status','errors')}">
                            <input type="text" id="status" name="status" value="${fieldValue(bean:smartsCard,field:'status')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="tag">tag:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsCard,field:'tag','errors')}">
                            <input type="text" id="tag" name="tag" value="${fieldValue(bean:smartsCard,field:'tag')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="type">type:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsCard,field:'type','errors')}">
                            <input type="text" id="type" name="type" value="${fieldValue(bean:smartsCard,field:'type')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="underlying">underlying:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsCard,field:'underlying','errors')}">
                            
<ul>
<g:each var="u" in="${smartsCard?.underlying?}">
    <li style="margin-bottom:3px;">
        <g:link controller="smartsComputerSystemComponent" action="show" id="${u.id}">${u}</g:link>
        <g:link class="delete" action="removeRelation" params="['id':smartsCard?.id, 'relationName':'underlying', 'relatedObjectId':u.id]"></g:link>
    </li>
</g:each>
</ul>
<g:link params="['id':smartsCard?.id, 'relationName':'underlying']" action="addTo">Add SmartsComputerSystemComponent</g:link>

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
