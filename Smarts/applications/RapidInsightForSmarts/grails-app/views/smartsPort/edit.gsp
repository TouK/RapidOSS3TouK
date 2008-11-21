

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Edit SmartsPort</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLinkTo(dir:'')}">Home</a></span>
    <span class="menuButton"><g:link class="list" action="list">SmartsPort List</g:link></span>
    <span class="menuButton"><g:link class="create" action="create">New SmartsPort</g:link></span>
</div>
<div class="body">
    <h1>Edit SmartsPort</h1>
    <g:if test="${flash.message}">
        <div class="message">${flash.message}</div>
    </g:if>
    <g:hasErrors bean="${smartsPort}">
        <div class="errors">
            <g:renderErrors bean="${smartsPort}" as="list"/>
        </div>
    </g:hasErrors>
    <g:hasErrors bean="${flash.errors}">
       <div class="errors">
            <g:renderErrors bean="${flash.errors}"/>
        </div>
    </g:hasErrors>
    <g:form method="post" >
        <input type="hidden" name="id" value="${smartsPort?.id}"/>
        <div class="dialog">
            <table>
                <tbody>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="name">name:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsPort,field:'name','errors')}">
                            <input type="text" id="name" name="name" value="${fieldValue(bean:smartsPort,field:'name')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="adminStatus">adminStatus:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsPort,field:'adminStatus','errors')}">
                            <input type="text" id="adminStatus" name="adminStatus" value="${fieldValue(bean:smartsPort,field:'adminStatus')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="cardName">cardName:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsPort,field:'cardName','errors')}">
                            <input type="text" id="cardName" name="cardName" value="${fieldValue(bean:smartsPort,field:'cardName')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="className">className:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsPort,field:'className','errors')}">
                            <input type="text" id="className" name="className" value="${fieldValue(bean:smartsPort,field:'className')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="computerSystemName">computerSystemName:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsPort,field:'computerSystemName','errors')}">
                            <input type="text" id="computerSystemName" name="computerSystemName" value="${fieldValue(bean:smartsPort,field:'computerSystemName')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="connectedVia">connectedVia:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsPort,field:'connectedVia','errors')}">
                            <g:select optionKey="id" from="${SmartsLink.list()}" name="connectedVia.id" value="${smartsPort?.connectedVia?.id}" noSelection="['null':'']"></g:select>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="description">description:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsPort,field:'description','errors')}">
                            <input type="text" id="description" name="description" value="${fieldValue(bean:smartsPort,field:'description')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="designatedBridge">designatedBridge:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsPort,field:'designatedBridge','errors')}">
                            <input type="text" id="designatedBridge" name="designatedBridge" value="${fieldValue(bean:smartsPort,field:'designatedBridge')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="designatedPort">designatedPort:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsPort,field:'designatedPort','errors')}">
                            <input type="text" id="designatedPort" name="designatedPort" value="${fieldValue(bean:smartsPort,field:'designatedPort')}" />
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="deviceID">deviceID:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsPort,field:'deviceID','errors')}">
                            <input type="text" id="deviceID" name="deviceID" value="${fieldValue(bean:smartsPort,field:'deviceID')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="displayClassName">displayClassName:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsPort,field:'displayClassName','errors')}">
                            <input type="text" id="displayClassName" name="displayClassName" value="${fieldValue(bean:smartsPort,field:'displayClassName')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="displayName">displayName:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsPort,field:'displayName','errors')}">
                            <input type="text" id="displayName" name="displayName" value="${fieldValue(bean:smartsPort,field:'displayName')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="duplexMode">duplexMode:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsPort,field:'duplexMode','errors')}">
                            <input type="text" id="duplexMode" name="duplexMode" value="${fieldValue(bean:smartsPort,field:'duplexMode')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="duplexSource">duplexSource:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsPort,field:'duplexSource','errors')}">
                            <input type="text" id="duplexSource" name="duplexSource" value="${fieldValue(bean:smartsPort,field:'duplexSource')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="interfaceAlias">interfaceAlias:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsPort,field:'interfaceAlias','errors')}">
                            <input type="text" id="interfaceAlias" name="interfaceAlias" value="${fieldValue(bean:smartsPort,field:'interfaceAlias')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="interfaceCode">interfaceCode:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsPort,field:'interfaceCode','errors')}">
                            <input type="text" id="interfaceCode" name="interfaceCode" value="${fieldValue(bean:smartsPort,field:'interfaceCode')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="interfaceNumber">interfaceNumber:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsPort,field:'interfaceNumber','errors')}">
                            <input type="text" id="interfaceNumber" name="interfaceNumber" value="${fieldValue(bean:smartsPort,field:'interfaceNumber')}" />
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="isConnectedToManagedSystem">isConnectedToManagedSystem:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsPort,field:'isConnectedToManagedSystem','errors')}">
                            <g:checkBox name="isConnectedToManagedSystem" value="${smartsPort?.isConnectedToManagedSystem}" ></g:checkBox>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="isConnectedToSystem">isConnectedToSystem:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsPort,field:'isConnectedToSystem','errors')}">
                            <g:checkBox name="isConnectedToSystem" value="${smartsPort?.isConnectedToSystem}" ></g:checkBox>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="isConnector">isConnector:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsPort,field:'isConnector','errors')}">
                            <g:checkBox name="isConnector" value="${smartsPort?.isConnector}" ></g:checkBox>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="isFlapping">isFlapping:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsPort,field:'isFlapping','errors')}">
                            <g:checkBox name="isFlapping" value="${smartsPort?.isFlapping}" ></g:checkBox>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="isManaged">isManaged:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsPort,field:'isManaged','errors')}">
                            <g:checkBox name="isManaged" value="${smartsPort?.isManaged}" ></g:checkBox>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="isNetworkAdapterNotOperating">isNetworkAdapterNotOperating:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsPort,field:'isNetworkAdapterNotOperating','errors')}">
                            <g:checkBox name="isNetworkAdapterNotOperating" value="${smartsPort?.isNetworkAdapterNotOperating}" ></g:checkBox>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="lastChangedAt">lastChangedAt:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsPort,field:'lastChangedAt','errors')}">
                            <input type="text" id="lastChangedAt" name="lastChangedAt" value="${fieldValue(bean:smartsPort,field:'lastChangedAt')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="layeredOver">layeredOver:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsPort,field:'layeredOver','errors')}">
                            
<ul>
<g:each var="l" in="${smartsPort?.layeredOver?}">
    <li style="margin-bottom:3px;">
        <g:link controller="smartsComputerSystemComponent" action="show" id="${l.id}">${l}</g:link>
        <g:link class="delete" action="removeRelation" params="['id':smartsPort?.id, 'relationName':'layeredOver', 'relatedObjectId':l.id]"></g:link>
    </li>
</g:each>
</ul>
<g:link params="['id':smartsPort?.id, 'relationName':'layeredOver']" action="addTo">Add SmartsComputerSystemComponent</g:link>

                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="managedState">managedState:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsPort,field:'managedState','errors')}">
                            <input type="text" id="managedState" name="managedState" value="${fieldValue(bean:smartsPort,field:'managedState')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="maxSpeed">maxSpeed:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsPort,field:'maxSpeed','errors')}">
                            <input type="text" id="maxSpeed" name="maxSpeed" value="${fieldValue(bean:smartsPort,field:'maxSpeed')}" />
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="maxTransferUnit">maxTransferUnit:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsPort,field:'maxTransferUnit','errors')}">
                            <input type="text" id="maxTransferUnit" name="maxTransferUnit" value="${fieldValue(bean:smartsPort,field:'maxTransferUnit')}" />
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="maximumUptime">maximumUptime:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsPort,field:'maximumUptime','errors')}">
                            <input type="text" id="maximumUptime" name="maximumUptime" value="${fieldValue(bean:smartsPort,field:'maximumUptime')}" />
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="memberOfGroup">memberOfGroup:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsPort,field:'memberOfGroup','errors')}">
                            
<ul>
<g:each var="m" in="${smartsPort?.memberOfGroup?}">
    <li style="margin-bottom:3px;">
        <g:link controller="rsGroup" action="show" id="${m.id}">${m}</g:link>
        <g:link class="delete" action="removeRelation" params="['id':smartsPort?.id, 'relationName':'memberOfGroup', 'relatedObjectId':m.id]"></g:link>
    </li>
</g:each>
</ul>
<g:link params="['id':smartsPort?.id, 'relationName':'memberOfGroup']" action="addTo">Add RsGroup</g:link>

                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="mib2IfType">mib2IfType:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsPort,field:'mib2IfType','errors')}">
                            <input type="text" id="mib2IfType" name="mib2IfType" value="${fieldValue(bean:smartsPort,field:'mib2IfType')}" />
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="mode">mode:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsPort,field:'mode','errors')}">
                            <input type="text" id="mode" name="mode" value="${fieldValue(bean:smartsPort,field:'mode')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="operStatus">operStatus:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsPort,field:'operStatus','errors')}">
                            <input type="text" id="operStatus" name="operStatus" value="${fieldValue(bean:smartsPort,field:'operStatus')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="partOf">partOf:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsPort,field:'partOf','errors')}">
                            <g:select optionKey="id" from="${SmartsComputerSystem.list()}" name="partOf.id" value="${smartsPort?.partOf?.id}" noSelection="['null':'']"></g:select>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="partOfVlan">partOfVlan:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsPort,field:'partOfVlan','errors')}">
                            
<ul>
<g:each var="p" in="${smartsPort?.partOfVlan?}">
    <li style="margin-bottom:3px;">
        <g:link controller="smartsVlan" action="show" id="${p.id}">${p}</g:link>
        <g:link class="delete" action="removeRelation" params="['id':smartsPort?.id, 'relationName':'partOfVlan', 'relatedObjectId':p.id]"></g:link>
    </li>
</g:each>
</ul>
<g:link params="['id':smartsPort?.id, 'relationName':'partOfVlan']" action="addTo">Add SmartsVlan</g:link>

                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="peerSystemName">peerSystemName:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsPort,field:'peerSystemName','errors')}">
                            <input type="text" id="peerSystemName" name="peerSystemName" value="${fieldValue(bean:smartsPort,field:'peerSystemName')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="peerSystemType">peerSystemType:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsPort,field:'peerSystemType','errors')}">
                            <input type="text" id="peerSystemType" name="peerSystemType" value="${fieldValue(bean:smartsPort,field:'peerSystemType')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="portKey">portKey:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsPort,field:'portKey','errors')}">
                            <input type="text" id="portKey" name="portKey" value="${fieldValue(bean:smartsPort,field:'portKey')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="portNumber">portNumber:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsPort,field:'portNumber','errors')}">
                            <input type="text" id="portNumber" name="portNumber" value="${fieldValue(bean:smartsPort,field:'portNumber')}" />
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="portType">portType:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsPort,field:'portType','errors')}">
                            <input type="text" id="portType" name="portType" value="${fieldValue(bean:smartsPort,field:'portType')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="realizedBy">realizedBy:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsPort,field:'realizedBy','errors')}">
                            <g:select optionKey="id" from="${SmartsCard.list()}" name="realizedBy.id" value="${smartsPort?.realizedBy?.id}" noSelection="['null':'']"></g:select>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="rsDatasource">rsDatasource:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsPort,field:'rsDatasource','errors')}">
                            <input type="text" id="rsDatasource" name="rsDatasource" value="${fieldValue(bean:smartsPort,field:'rsDatasource')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="status">status:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsPort,field:'status','errors')}">
                            <input type="text" id="status" name="status" value="${fieldValue(bean:smartsPort,field:'status')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="systemModel">systemModel:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsPort,field:'systemModel','errors')}">
                            <input type="text" id="systemModel" name="systemModel" value="${fieldValue(bean:smartsPort,field:'systemModel')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="systemName">systemName:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsPort,field:'systemName','errors')}">
                            <input type="text" id="systemName" name="systemName" value="${fieldValue(bean:smartsPort,field:'systemName')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="systemObjectID">systemObjectID:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsPort,field:'systemObjectID','errors')}">
                            <input type="text" id="systemObjectID" name="systemObjectID" value="${fieldValue(bean:smartsPort,field:'systemObjectID')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="systemType">systemType:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsPort,field:'systemType','errors')}">
                            <input type="text" id="systemType" name="systemType" value="${fieldValue(bean:smartsPort,field:'systemType')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="systemVendor">systemVendor:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsPort,field:'systemVendor','errors')}">
                            <input type="text" id="systemVendor" name="systemVendor" value="${fieldValue(bean:smartsPort,field:'systemVendor')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="tag">tag:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsPort,field:'tag','errors')}">
                            <input type="text" id="tag" name="tag" value="${fieldValue(bean:smartsPort,field:'tag')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="type">type:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsPort,field:'type','errors')}">
                            <input type="text" id="type" name="type" value="${fieldValue(bean:smartsPort,field:'type')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="underlying">underlying:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsPort,field:'underlying','errors')}">
                            
<ul>
<g:each var="u" in="${smartsPort?.underlying?}">
    <li style="margin-bottom:3px;">
        <g:link controller="smartsComputerSystemComponent" action="show" id="${u.id}">${u}</g:link>
        <g:link class="delete" action="removeRelation" params="['id':smartsPort?.id, 'relationName':'underlying', 'relatedObjectId':u.id]"></g:link>
    </li>
</g:each>
</ul>
<g:link params="['id':smartsPort?.id, 'relationName':'underlying']" action="addTo">Add SmartsComputerSystemComponent</g:link>

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
