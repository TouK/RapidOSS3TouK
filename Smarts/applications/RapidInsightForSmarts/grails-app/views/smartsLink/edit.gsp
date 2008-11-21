

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Edit SmartsLink</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLinkTo(dir:'')}">Home</a></span>
    <span class="menuButton"><g:link class="list" action="list">SmartsLink List</g:link></span>
    <span class="menuButton"><g:link class="create" action="create">New SmartsLink</g:link></span>
</div>
<div class="body">
    <h1>Edit SmartsLink</h1>
    <g:if test="${flash.message}">
        <div class="message">${flash.message}</div>
    </g:if>
    <g:hasErrors bean="${smartsLink}">
        <div class="errors">
            <g:renderErrors bean="${smartsLink}" as="list"/>
        </div>
    </g:hasErrors>
    <g:hasErrors bean="${flash.errors}">
       <div class="errors">
            <g:renderErrors bean="${flash.errors}"/>
        </div>
    </g:hasErrors>
    <g:form method="post" >
        <input type="hidden" name="id" value="${smartsLink?.id}"/>
        <div class="dialog">
            <table>
                <tbody>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="name">name:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsLink,field:'name','errors')}">
                            <input type="text" id="name" name="name" value="${fieldValue(bean:smartsLink,field:'name')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="a_AdminStatus">a_AdminStatus:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsLink,field:'a_AdminStatus','errors')}">
                            <input type="text" id="a_AdminStatus" name="a_AdminStatus" value="${fieldValue(bean:smartsLink,field:'a_AdminStatus')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="a_ComputerSystemName">a_ComputerSystemName:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsLink,field:'a_ComputerSystemName','errors')}">
                            <input type="text" id="a_ComputerSystemName" name="a_ComputerSystemName" value="${fieldValue(bean:smartsLink,field:'a_ComputerSystemName')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="a_DisplayName">a_DisplayName:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsLink,field:'a_DisplayName','errors')}">
                            <input type="text" id="a_DisplayName" name="a_DisplayName" value="${fieldValue(bean:smartsLink,field:'a_DisplayName')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="a_DuplexMode">a_DuplexMode:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsLink,field:'a_DuplexMode','errors')}">
                            <input type="text" id="a_DuplexMode" name="a_DuplexMode" value="${fieldValue(bean:smartsLink,field:'a_DuplexMode')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="a_IsFlapping">a_IsFlapping:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsLink,field:'a_IsFlapping','errors')}">
                            <g:checkBox name="a_IsFlapping" value="${smartsLink?.a_IsFlapping}" ></g:checkBox>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="a_MaxSpeed">a_MaxSpeed:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsLink,field:'a_MaxSpeed','errors')}">
                            <input type="text" id="a_MaxSpeed" name="a_MaxSpeed" value="${fieldValue(bean:smartsLink,field:'a_MaxSpeed')}" />
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="a_Mode">a_Mode:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsLink,field:'a_Mode','errors')}">
                            <input type="text" id="a_Mode" name="a_Mode" value="${fieldValue(bean:smartsLink,field:'a_Mode')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="a_Name">a_Name:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsLink,field:'a_Name','errors')}">
                            <input type="text" id="a_Name" name="a_Name" value="${fieldValue(bean:smartsLink,field:'a_Name')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="a_OperStatus">a_OperStatus:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsLink,field:'a_OperStatus','errors')}">
                            <input type="text" id="a_OperStatus" name="a_OperStatus" value="${fieldValue(bean:smartsLink,field:'a_OperStatus')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="className">className:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsLink,field:'className','errors')}">
                            <input type="text" id="className" name="className" value="${fieldValue(bean:smartsLink,field:'className')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="connectedSystem">connectedSystem:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsLink,field:'connectedSystem','errors')}">
                            
<ul>
<g:each var="c" in="${smartsLink?.connectedSystem?}">
    <li style="margin-bottom:3px;">
        <g:link controller="rsComputerSystem" action="show" id="${c.id}">${c}</g:link>
        <g:link class="delete" action="removeRelation" params="['id':smartsLink?.id, 'relationName':'connectedSystem', 'relatedObjectId':c.id]"></g:link>
    </li>
</g:each>
</ul>
<g:link params="['id':smartsLink?.id, 'relationName':'connectedSystem']" action="addTo">Add RsComputerSystem</g:link>

                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="connectedSystemsUnresponsive">connectedSystemsUnresponsive:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsLink,field:'connectedSystemsUnresponsive','errors')}">
                            <g:checkBox name="connectedSystemsUnresponsive" value="${smartsLink?.connectedSystemsUnresponsive}" ></g:checkBox>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="connectedTo">connectedTo:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsLink,field:'connectedTo','errors')}">
                            
<ul>
<g:each var="c" in="${smartsLink?.connectedTo?}">
    <li style="margin-bottom:3px;">
        <g:link controller="smartsNetworkAdapter" action="show" id="${c.id}">${c}</g:link>
        <g:link class="delete" action="removeRelation" params="['id':smartsLink?.id, 'relationName':'connectedTo', 'relatedObjectId':c.id]"></g:link>
    </li>
</g:each>
</ul>
<g:link params="['id':smartsLink?.id, 'relationName':'connectedTo']" action="addTo">Add SmartsNetworkAdapter</g:link>

                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="description">description:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsLink,field:'description','errors')}">
                            <input type="text" id="description" name="description" value="${fieldValue(bean:smartsLink,field:'description')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="displayName">displayName:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsLink,field:'displayName','errors')}">
                            <input type="text" id="displayName" name="displayName" value="${fieldValue(bean:smartsLink,field:'displayName')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="isManaged">isManaged:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsLink,field:'isManaged','errors')}">
                            <g:checkBox name="isManaged" value="${smartsLink?.isManaged}" ></g:checkBox>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="memberOfGroup">memberOfGroup:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsLink,field:'memberOfGroup','errors')}">
                            
<ul>
<g:each var="m" in="${smartsLink?.memberOfGroup?}">
    <li style="margin-bottom:3px;">
        <g:link controller="rsGroup" action="show" id="${m.id}">${m}</g:link>
        <g:link class="delete" action="removeRelation" params="['id':smartsLink?.id, 'relationName':'memberOfGroup', 'relatedObjectId':m.id]"></g:link>
    </li>
</g:each>
</ul>
<g:link params="['id':smartsLink?.id, 'relationName':'memberOfGroup']" action="addTo">Add RsGroup</g:link>

                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="rsDatasource">rsDatasource:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsLink,field:'rsDatasource','errors')}">
                            <input type="text" id="rsDatasource" name="rsDatasource" value="${fieldValue(bean:smartsLink,field:'rsDatasource')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="vlans">vlans:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsLink,field:'vlans','errors')}">
                            
<ul>
<g:each var="v" in="${smartsLink?.vlans?}">
    <li style="margin-bottom:3px;">
        <g:link controller="smartsVlan" action="show" id="${v.id}">${v}</g:link>
        <g:link class="delete" action="removeRelation" params="['id':smartsLink?.id, 'relationName':'vlans', 'relatedObjectId':v.id]"></g:link>
    </li>
</g:each>
</ul>
<g:link params="['id':smartsLink?.id, 'relationName':'vlans']" action="addTo">Add SmartsVlan</g:link>

                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="z_AdminStatus">z_AdminStatus:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsLink,field:'z_AdminStatus','errors')}">
                            <input type="text" id="z_AdminStatus" name="z_AdminStatus" value="${fieldValue(bean:smartsLink,field:'z_AdminStatus')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="z_ComputerSystemName">z_ComputerSystemName:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsLink,field:'z_ComputerSystemName','errors')}">
                            <input type="text" id="z_ComputerSystemName" name="z_ComputerSystemName" value="${fieldValue(bean:smartsLink,field:'z_ComputerSystemName')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="z_DisplayName">z_DisplayName:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsLink,field:'z_DisplayName','errors')}">
                            <input type="text" id="z_DisplayName" name="z_DisplayName" value="${fieldValue(bean:smartsLink,field:'z_DisplayName')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="z_DuplexMode">z_DuplexMode:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsLink,field:'z_DuplexMode','errors')}">
                            <input type="text" id="z_DuplexMode" name="z_DuplexMode" value="${fieldValue(bean:smartsLink,field:'z_DuplexMode')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="z_IsFlapping">z_IsFlapping:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsLink,field:'z_IsFlapping','errors')}">
                            <g:checkBox name="z_IsFlapping" value="${smartsLink?.z_IsFlapping}" ></g:checkBox>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="z_MaxSpeed">z_MaxSpeed:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsLink,field:'z_MaxSpeed','errors')}">
                            <input type="text" id="z_MaxSpeed" name="z_MaxSpeed" value="${fieldValue(bean:smartsLink,field:'z_MaxSpeed')}" />
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="z_Mode">z_Mode:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsLink,field:'z_Mode','errors')}">
                            <input type="text" id="z_Mode" name="z_Mode" value="${fieldValue(bean:smartsLink,field:'z_Mode')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="z_Name">z_Name:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsLink,field:'z_Name','errors')}">
                            <input type="text" id="z_Name" name="z_Name" value="${fieldValue(bean:smartsLink,field:'z_Name')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="z_OperStatus">z_OperStatus:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsLink,field:'z_OperStatus','errors')}">
                            <input type="text" id="z_OperStatus" name="z_OperStatus" value="${fieldValue(bean:smartsLink,field:'z_OperStatus')}"/>
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
