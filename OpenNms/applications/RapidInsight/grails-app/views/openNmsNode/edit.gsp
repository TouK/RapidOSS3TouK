

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Edit OpenNmsNode</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLinkTo(dir:'')}">Home</a></span>
    <span class="menuButton"><g:link class="list" action="list">OpenNmsNode List</g:link></span>
    <span class="menuButton"><g:link class="create" action="create">New OpenNmsNode</g:link></span>
</div>
<div class="body">
    <h1>Edit OpenNmsNode</h1>
    <g:if test="${flash.message}">
        <div class="message">${flash.message}</div>
    </g:if>
    <g:hasErrors bean="${openNmsNode}">
        <div class="errors">
            <g:renderErrors bean="${openNmsNode}" as="list"/>
        </div>
    </g:hasErrors>
    <g:hasErrors bean="${flash.errors}">
       <div class="errors">
            <g:renderErrors bean="${flash.errors}"/>
        </div>
    </g:hasErrors>
    <g:form method="post" >
        <input type="hidden" name="id" value="${openNmsNode?.id}"/>
        <div class="dialog">
            <table>
                <tbody>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="name">name:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:openNmsNode,field:'name','errors')}">
                            <input type="text" id="name" name="name" value="${fieldValue(bean:openNmsNode,field:'name')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="className">className:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:openNmsNode,field:'className','errors')}">
                            <input type="text" id="className" name="className" value="${fieldValue(bean:openNmsNode,field:'className')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="createdAt">createdAt:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:openNmsNode,field:'createdAt','errors')}">
                            <g:datePicker name="createdAt" value="${openNmsNode?.createdAt}" noSelection="['':'']"></g:datePicker>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="description">description:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:openNmsNode,field:'description','errors')}">
                            <input type="text" id="description" name="description" value="${fieldValue(bean:openNmsNode,field:'description')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="displayName">displayName:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:openNmsNode,field:'displayName','errors')}">
                            <input type="text" id="displayName" name="displayName" value="${fieldValue(bean:openNmsNode,field:'displayName')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="domainName">domainName:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:openNmsNode,field:'domainName','errors')}">
                            <input type="text" id="domainName" name="domainName" value="${fieldValue(bean:openNmsNode,field:'domainName')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="dpName">dpName:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:openNmsNode,field:'dpName','errors')}">
                            <input type="text" id="dpName" name="dpName" value="${fieldValue(bean:openNmsNode,field:'dpName')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="foreignId">foreignId:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:openNmsNode,field:'foreignId','errors')}">
                            <input type="text" id="foreignId" name="foreignId" value="${fieldValue(bean:openNmsNode,field:'foreignId')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="foreignSource">foreignSource:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:openNmsNode,field:'foreignSource','errors')}">
                            <input type="text" id="foreignSource" name="foreignSource" value="${fieldValue(bean:openNmsNode,field:'foreignSource')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="graphs">graphs:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:openNmsNode,field:'graphs','errors')}">
                            
<ul>
<g:each var="g" in="${openNmsNode?.graphs?}">
    <li style="margin-bottom:3px;">
        <g:link controller="openNmsGraph" action="show" id="${g.id}">${g}</g:link>
        <g:link class="delete" action="removeRelation" params="['id':openNmsNode?.id, 'relationName':'graphs', 'relatedObjectId':g.id]"></g:link>
    </li>
</g:each>
</ul>
<g:link params="['id':openNmsNode?.id, 'relationName':'graphs']" action="addTo">Add OpenNmsGraph</g:link>

                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="ipInterfaces">ipInterfaces:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:openNmsNode,field:'ipInterfaces','errors')}">
                            
<ul>
<g:each var="i" in="${openNmsNode?.ipInterfaces?}">
    <li style="margin-bottom:3px;">
        <g:link controller="openNmsIpInterface" action="show" id="${i.id}">${i}</g:link>
        <g:link class="delete" action="removeRelation" params="['id':openNmsNode?.id, 'relationName':'ipInterfaces', 'relatedObjectId':i.id]"></g:link>
    </li>
</g:each>
</ul>
<g:link params="['id':openNmsNode?.id, 'relationName':'ipInterfaces']" action="addTo">Add OpenNmsIpInterface</g:link>

                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="isManaged">isManaged:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:openNmsNode,field:'isManaged','errors')}">
                            <g:checkBox name="isManaged" value="${openNmsNode?.isManaged}" ></g:checkBox>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="lastPolledAt">lastPolledAt:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:openNmsNode,field:'lastPolledAt','errors')}">
                            <g:datePicker name="lastPolledAt" value="${openNmsNode?.lastPolledAt}" noSelection="['':'']"></g:datePicker>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="memberOfGroup">memberOfGroup:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:openNmsNode,field:'memberOfGroup','errors')}">
                            
<ul>
<g:each var="m" in="${openNmsNode?.memberOfGroup?}">
    <li style="margin-bottom:3px;">
        <g:link controller="rsGroup" action="show" id="${m.id}">${m}</g:link>
        <g:link class="delete" action="removeRelation" params="['id':openNmsNode?.id, 'relationName':'memberOfGroup', 'relatedObjectId':m.id]"></g:link>
    </li>
</g:each>
</ul>
<g:link params="['id':openNmsNode?.id, 'relationName':'memberOfGroup']" action="addTo">Add RsGroup</g:link>

                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="netbiosName">netbiosName:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:openNmsNode,field:'netbiosName','errors')}">
                            <input type="text" id="netbiosName" name="netbiosName" value="${fieldValue(bean:openNmsNode,field:'netbiosName')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="nodeName">nodeName:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:openNmsNode,field:'nodeName','errors')}">
                            <input type="text" id="nodeName" name="nodeName" value="${fieldValue(bean:openNmsNode,field:'nodeName')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="operatingSystem">operatingSystem:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:openNmsNode,field:'operatingSystem','errors')}">
                            <input type="text" id="operatingSystem" name="operatingSystem" value="${fieldValue(bean:openNmsNode,field:'operatingSystem')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="rsDatasource">rsDatasource:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:openNmsNode,field:'rsDatasource','errors')}">
                            <input type="text" id="rsDatasource" name="rsDatasource" value="${fieldValue(bean:openNmsNode,field:'rsDatasource')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="sysContact">sysContact:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:openNmsNode,field:'sysContact','errors')}">
                            <input type="text" id="sysContact" name="sysContact" value="${fieldValue(bean:openNmsNode,field:'sysContact')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="sysDescription">sysDescription:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:openNmsNode,field:'sysDescription','errors')}">
                            <input type="text" id="sysDescription" name="sysDescription" value="${fieldValue(bean:openNmsNode,field:'sysDescription')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="sysLocation">sysLocation:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:openNmsNode,field:'sysLocation','errors')}">
                            <input type="text" id="sysLocation" name="sysLocation" value="${fieldValue(bean:openNmsNode,field:'sysLocation')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="sysName">sysName:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:openNmsNode,field:'sysName','errors')}">
                            <input type="text" id="sysName" name="sysName" value="${fieldValue(bean:openNmsNode,field:'sysName')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="sysOid">sysOid:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:openNmsNode,field:'sysOid','errors')}">
                            <input type="text" id="sysOid" name="sysOid" value="${fieldValue(bean:openNmsNode,field:'sysOid')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="type">type:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:openNmsNode,field:'type','errors')}">
                            <input type="text" id="type" name="type" value="${fieldValue(bean:openNmsNode,field:'type')}"/>
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
