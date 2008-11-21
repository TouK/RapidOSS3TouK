

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Edit SmartsVlan</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLinkTo(dir:'')}">Home</a></span>
    <span class="menuButton"><g:link class="list" action="list">SmartsVlan List</g:link></span>
    <span class="menuButton"><g:link class="create" action="create">New SmartsVlan</g:link></span>
</div>
<div class="body">
    <h1>Edit SmartsVlan</h1>
    <g:if test="${flash.message}">
        <div class="message">${flash.message}</div>
    </g:if>
    <g:hasErrors bean="${smartsVlan}">
        <div class="errors">
            <g:renderErrors bean="${smartsVlan}" as="list"/>
        </div>
    </g:hasErrors>
    <g:hasErrors bean="${flash.errors}">
       <div class="errors">
            <g:renderErrors bean="${flash.errors}"/>
        </div>
    </g:hasErrors>
    <g:form method="post" >
        <input type="hidden" name="id" value="${smartsVlan?.id}"/>
        <div class="dialog">
            <table>
                <tbody>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="name">name:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsVlan,field:'name','errors')}">
                            <input type="text" id="name" name="name" value="${fieldValue(bean:smartsVlan,field:'name')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="className">className:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsVlan,field:'className','errors')}">
                            <input type="text" id="className" name="className" value="${fieldValue(bean:smartsVlan,field:'className')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="connectedPorts">connectedPorts:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsVlan,field:'connectedPorts','errors')}">
                            
<ul>
<g:each var="c" in="${smartsVlan?.connectedPorts?}">
    <li style="margin-bottom:3px;">
        <g:link controller="smartsPort" action="show" id="${c.id}">${c}</g:link>
        <g:link class="delete" action="removeRelation" params="['id':smartsVlan?.id, 'relationName':'connectedPorts', 'relatedObjectId':c.id]"></g:link>
    </li>
</g:each>
</ul>
<g:link params="['id':smartsVlan?.id, 'relationName':'connectedPorts']" action="addTo">Add SmartsPort</g:link>

                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="connectedSystems">connectedSystems:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsVlan,field:'connectedSystems','errors')}">
                            
<ul>
<g:each var="c" in="${smartsVlan?.connectedSystems?}">
    <li style="margin-bottom:3px;">
        <g:link controller="smartsComputerSystem" action="show" id="${c.id}">${c}</g:link>
        <g:link class="delete" action="removeRelation" params="['id':smartsVlan?.id, 'relationName':'connectedSystems', 'relatedObjectId':c.id]"></g:link>
    </li>
</g:each>
</ul>
<g:link params="['id':smartsVlan?.id, 'relationName':'connectedSystems']" action="addTo">Add SmartsComputerSystem</g:link>

                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="description">description:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsVlan,field:'description','errors')}">
                            <input type="text" id="description" name="description" value="${fieldValue(bean:smartsVlan,field:'description')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="displayName">displayName:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsVlan,field:'displayName','errors')}">
                            <input type="text" id="displayName" name="displayName" value="${fieldValue(bean:smartsVlan,field:'displayName')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="isManaged">isManaged:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsVlan,field:'isManaged','errors')}">
                            <g:checkBox name="isManaged" value="${smartsVlan?.isManaged}" ></g:checkBox>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="layeredOver">layeredOver:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsVlan,field:'layeredOver','errors')}">
                            
<ul>
<g:each var="l" in="${smartsVlan?.layeredOver?}">
    <li style="margin-bottom:3px;">
        <g:link controller="smartsComputerSystem" action="show" id="${l.id}">${l}</g:link>
        <g:link class="delete" action="removeRelation" params="['id':smartsVlan?.id, 'relationName':'layeredOver', 'relatedObjectId':l.id]"></g:link>
    </li>
</g:each>
</ul>
<g:link params="['id':smartsVlan?.id, 'relationName':'layeredOver']" action="addTo">Add SmartsComputerSystem</g:link>

                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="memberOfGroup">memberOfGroup:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsVlan,field:'memberOfGroup','errors')}">
                            
<ul>
<g:each var="m" in="${smartsVlan?.memberOfGroup?}">
    <li style="margin-bottom:3px;">
        <g:link controller="rsGroup" action="show" id="${m.id}">${m}</g:link>
        <g:link class="delete" action="removeRelation" params="['id':smartsVlan?.id, 'relationName':'memberOfGroup', 'relatedObjectId':m.id]"></g:link>
    </li>
</g:each>
</ul>
<g:link params="['id':smartsVlan?.id, 'relationName':'memberOfGroup']" action="addTo">Add RsGroup</g:link>

                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="memberSystems">memberSystems:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsVlan,field:'memberSystems','errors')}">
                            
<ul>
<g:each var="m" in="${smartsVlan?.memberSystems?}">
    <li style="margin-bottom:3px;">
        <g:link controller="smartsComputerSystem" action="show" id="${m.id}">${m}</g:link>
        <g:link class="delete" action="removeRelation" params="['id':smartsVlan?.id, 'relationName':'memberSystems', 'relatedObjectId':m.id]"></g:link>
    </li>
</g:each>
</ul>
<g:link params="['id':smartsVlan?.id, 'relationName':'memberSystems']" action="addTo">Add SmartsComputerSystem</g:link>

                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="rsDatasource">rsDatasource:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsVlan,field:'rsDatasource','errors')}">
                            <input type="text" id="rsDatasource" name="rsDatasource" value="${fieldValue(bean:smartsVlan,field:'rsDatasource')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="trunkCables">trunkCables:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsVlan,field:'trunkCables','errors')}">
                            
<ul>
<g:each var="t" in="${smartsVlan?.trunkCables?}">
    <li style="margin-bottom:3px;">
        <g:link controller="smartsLink" action="show" id="${t.id}">${t}</g:link>
        <g:link class="delete" action="removeRelation" params="['id':smartsVlan?.id, 'relationName':'trunkCables', 'relatedObjectId':t.id]"></g:link>
    </li>
</g:each>
</ul>
<g:link params="['id':smartsVlan?.id, 'relationName':'trunkCables']" action="addTo">Add SmartsLink</g:link>

                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="vlanKey">vlanKey:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsVlan,field:'vlanKey','errors')}">
                            <input type="text" id="vlanKey" name="vlanKey" value="${fieldValue(bean:smartsVlan,field:'vlanKey')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="vlanNumber">vlanNumber:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsVlan,field:'vlanNumber','errors')}">
                            <input type="text" id="vlanNumber" name="vlanNumber" value="${fieldValue(bean:smartsVlan,field:'vlanNumber')}" />
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
