

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Edit SmartsComputerSystem</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLinkTo(dir:'')}">Home</a></span>
    <span class="menuButton"><g:link class="list" action="list">SmartsComputerSystem List</g:link></span>
    <span class="menuButton"><g:link class="create" action="create">New SmartsComputerSystem</g:link></span>
</div>
<div class="body">
    <h1>Edit SmartsComputerSystem</h1>
    <g:if test="${flash.message}">
        <div class="message">${flash.message}</div>
    </g:if>
    <g:hasErrors bean="${smartsComputerSystem}">
        <div class="errors">
            <g:renderErrors bean="${smartsComputerSystem}" as="list"/>
        </div>
    </g:hasErrors>
    <g:hasErrors bean="${flash.errors}">
       <div class="errors">
            <g:renderErrors bean="${flash.errors}"/>
        </div>
    </g:hasErrors>
    <g:form method="post" >
        <input type="hidden" name="id" value="${smartsComputerSystem?.id}"/>
        <div class="dialog">
            <table>
                <tbody>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="name">name:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsComputerSystem,field:'name','errors')}">
                            <input type="text" id="name" name="name" value="${fieldValue(bean:smartsComputerSystem,field:'name')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="accessMode">accessMode:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsComputerSystem,field:'accessMode','errors')}">
                            <input type="text" id="accessMode" name="accessMode" value="${fieldValue(bean:smartsComputerSystem,field:'accessMode')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="className">className:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsComputerSystem,field:'className','errors')}">
                            <input type="text" id="className" name="className" value="${fieldValue(bean:smartsComputerSystem,field:'className')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="composedOf">composedOf:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsComputerSystem,field:'composedOf','errors')}">
                            
<ul>
<g:each var="c" in="${smartsComputerSystem?.composedOf?}">
    <li style="margin-bottom:3px;">
        <g:link controller="smartsComputerSystemComponent" action="show" id="${c.id}">${c}</g:link>
        <g:link class="delete" action="removeRelation" params="['id':smartsComputerSystem?.id, 'relationName':'composedOf', 'relatedObjectId':c.id]"></g:link>
    </li>
</g:each>
</ul>
<g:link params="['id':smartsComputerSystem?.id, 'relationName':'composedOf']" action="addTo">Add SmartsComputerSystemComponent</g:link>

                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="connectedVia">connectedVia:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsComputerSystem,field:'connectedVia','errors')}">
                            
<ul>
<g:each var="c" in="${smartsComputerSystem?.connectedVia?}">
    <li style="margin-bottom:3px;">
        <g:link controller="rsLink" action="show" id="${c.id}">${c}</g:link>
        <g:link class="delete" action="removeRelation" params="['id':smartsComputerSystem?.id, 'relationName':'connectedVia', 'relatedObjectId':c.id]"></g:link>
    </li>
</g:each>
</ul>
<g:link params="['id':smartsComputerSystem?.id, 'relationName':'connectedVia']" action="addTo">Add RsLink</g:link>

                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="connectedViaVlan">connectedViaVlan:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsComputerSystem,field:'connectedViaVlan','errors')}">
                            
<ul>
<g:each var="c" in="${smartsComputerSystem?.connectedViaVlan?}">
    <li style="margin-bottom:3px;">
        <g:link controller="smartsVlan" action="show" id="${c.id}">${c}</g:link>
        <g:link class="delete" action="removeRelation" params="['id':smartsComputerSystem?.id, 'relationName':'connectedViaVlan', 'relatedObjectId':c.id]"></g:link>
    </li>
</g:each>
</ul>
<g:link params="['id':smartsComputerSystem?.id, 'relationName':'connectedViaVlan']" action="addTo">Add SmartsVlan</g:link>

                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="description">description:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsComputerSystem,field:'description','errors')}">
                            <input type="text" id="description" name="description" value="${fieldValue(bean:smartsComputerSystem,field:'description')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="discoveredFirstAt">discoveredFirstAt:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsComputerSystem,field:'discoveredFirstAt','errors')}">
                            <input type="text" id="discoveredFirstAt" name="discoveredFirstAt" value="${fieldValue(bean:smartsComputerSystem,field:'discoveredFirstAt')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="discoveredLastAt">discoveredLastAt:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsComputerSystem,field:'discoveredLastAt','errors')}">
                            <input type="text" id="discoveredLastAt" name="discoveredLastAt" value="${fieldValue(bean:smartsComputerSystem,field:'discoveredLastAt')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="discoveryErrorInfo">discoveryErrorInfo:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsComputerSystem,field:'discoveryErrorInfo','errors')}">
                            <input type="text" id="discoveryErrorInfo" name="discoveryErrorInfo" value="${fieldValue(bean:smartsComputerSystem,field:'discoveryErrorInfo')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="discoveryTime">discoveryTime:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsComputerSystem,field:'discoveryTime','errors')}">
                            <input type="text" id="discoveryTime" name="discoveryTime" value="${fieldValue(bean:smartsComputerSystem,field:'discoveryTime')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="displayName">displayName:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsComputerSystem,field:'displayName','errors')}">
                            <input type="text" id="displayName" name="displayName" value="${fieldValue(bean:smartsComputerSystem,field:'displayName')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="geocodes">geocodes:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsComputerSystem,field:'geocodes','errors')}">
                            <input type="text" id="geocodes" name="geocodes" value="${fieldValue(bean:smartsComputerSystem,field:'geocodes')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="hostsAccessPoints">hostsAccessPoints:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsComputerSystem,field:'hostsAccessPoints','errors')}">
                            
<ul>
<g:each var="h" in="${smartsComputerSystem?.hostsAccessPoints?}">
    <li style="margin-bottom:3px;">
        <g:link controller="smartsIp" action="show" id="${h.id}">${h}</g:link>
        <g:link class="delete" action="removeRelation" params="['id':smartsComputerSystem?.id, 'relationName':'hostsAccessPoints', 'relatedObjectId':h.id]"></g:link>
    </li>
</g:each>
</ul>
<g:link params="['id':smartsComputerSystem?.id, 'relationName':'hostsAccessPoints']" action="addTo">Add SmartsIp</g:link>

                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="hsrpGroup">hsrpGroup:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsComputerSystem,field:'hsrpGroup','errors')}">
                            <g:select optionKey="id" from="${SmartsHSRPGroup.list()}" name="hsrpGroup.id" value="${smartsComputerSystem?.hsrpGroup?.id}" noSelection="['null':'']"></g:select>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="ipNetworks">ipNetworks:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsComputerSystem,field:'ipNetworks','errors')}">
                            
<ul>
<g:each var="i" in="${smartsComputerSystem?.ipNetworks?}">
    <li style="margin-bottom:3px;">
        <g:link controller="smartsIpNetwork" action="show" id="${i.id}">${i}</g:link>
        <g:link class="delete" action="removeRelation" params="['id':smartsComputerSystem?.id, 'relationName':'ipNetworks', 'relatedObjectId':i.id]"></g:link>
    </li>
</g:each>
</ul>
<g:link params="['id':smartsComputerSystem?.id, 'relationName':'ipNetworks']" action="addTo">Add SmartsIpNetwork</g:link>

                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="isManaged">isManaged:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsComputerSystem,field:'isManaged','errors')}">
                            <g:checkBox name="isManaged" value="${smartsComputerSystem?.isManaged}" ></g:checkBox>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="location">location:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsComputerSystem,field:'location','errors')}">
                            <input type="text" id="location" name="location" value="${fieldValue(bean:smartsComputerSystem,field:'location')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="managementServer">managementServer:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsComputerSystem,field:'managementServer','errors')}">
                            <input type="text" id="managementServer" name="managementServer" value="${fieldValue(bean:smartsComputerSystem,field:'managementServer')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="memberOfGroup">memberOfGroup:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsComputerSystem,field:'memberOfGroup','errors')}">
                            
<ul>
<g:each var="m" in="${smartsComputerSystem?.memberOfGroup?}">
    <li style="margin-bottom:3px;">
        <g:link controller="rsGroup" action="show" id="${m.id}">${m}</g:link>
        <g:link class="delete" action="removeRelation" params="['id':smartsComputerSystem?.id, 'relationName':'memberOfGroup', 'relatedObjectId':m.id]"></g:link>
    </li>
</g:each>
</ul>
<g:link params="['id':smartsComputerSystem?.id, 'relationName':'memberOfGroup']" action="addTo">Add RsGroup</g:link>

                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="model">model:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsComputerSystem,field:'model','errors')}">
                            <input type="text" id="model" name="model" value="${fieldValue(bean:smartsComputerSystem,field:'model')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="numberOfIPs">numberOfIPs:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsComputerSystem,field:'numberOfIPs','errors')}">
                            <input type="text" id="numberOfIPs" name="numberOfIPs" value="${fieldValue(bean:smartsComputerSystem,field:'numberOfIPs')}" />
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="numberOfIPv6s">numberOfIPv6s:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsComputerSystem,field:'numberOfIPv6s','errors')}">
                            <input type="text" id="numberOfIPv6s" name="numberOfIPv6s" value="${fieldValue(bean:smartsComputerSystem,field:'numberOfIPv6s')}" />
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="numberOfInterfaces">numberOfInterfaces:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsComputerSystem,field:'numberOfInterfaces','errors')}">
                            <input type="text" id="numberOfInterfaces" name="numberOfInterfaces" value="${fieldValue(bean:smartsComputerSystem,field:'numberOfInterfaces')}" />
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="numberOfNetworkAdapters">numberOfNetworkAdapters:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsComputerSystem,field:'numberOfNetworkAdapters','errors')}">
                            <input type="text" id="numberOfNetworkAdapters" name="numberOfNetworkAdapters" value="${fieldValue(bean:smartsComputerSystem,field:'numberOfNetworkAdapters')}" />
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="numberOfPorts">numberOfPorts:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsComputerSystem,field:'numberOfPorts','errors')}">
                            <input type="text" id="numberOfPorts" name="numberOfPorts" value="${fieldValue(bean:smartsComputerSystem,field:'numberOfPorts')}" />
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="osVersion">osVersion:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsComputerSystem,field:'osVersion','errors')}">
                            <input type="text" id="osVersion" name="osVersion" value="${fieldValue(bean:smartsComputerSystem,field:'osVersion')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="partOf">partOf:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsComputerSystem,field:'partOf','errors')}">
                            
<ul>
<g:each var="p" in="${smartsComputerSystem?.partOf?}">
    <li style="margin-bottom:3px;">
        <g:link controller="smartsVlan" action="show" id="${p.id}">${p}</g:link>
        <g:link class="delete" action="removeRelation" params="['id':smartsComputerSystem?.id, 'relationName':'partOf', 'relatedObjectId':p.id]"></g:link>
    </li>
</g:each>
</ul>
<g:link params="['id':smartsComputerSystem?.id, 'relationName':'partOf']" action="addTo">Add SmartsVlan</g:link>

                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="primaryOwnerContact">primaryOwnerContact:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsComputerSystem,field:'primaryOwnerContact','errors')}">
                            <input type="text" id="primaryOwnerContact" name="primaryOwnerContact" value="${fieldValue(bean:smartsComputerSystem,field:'primaryOwnerContact')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="primaryOwnerName">primaryOwnerName:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsComputerSystem,field:'primaryOwnerName','errors')}">
                            <input type="text" id="primaryOwnerName" name="primaryOwnerName" value="${fieldValue(bean:smartsComputerSystem,field:'primaryOwnerName')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="readCommunity">readCommunity:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsComputerSystem,field:'readCommunity','errors')}">
                            <input type="text" id="readCommunity" name="readCommunity" value="${fieldValue(bean:smartsComputerSystem,field:'readCommunity')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="rsDatasource">rsDatasource:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsComputerSystem,field:'rsDatasource','errors')}">
                            <input type="text" id="rsDatasource" name="rsDatasource" value="${fieldValue(bean:smartsComputerSystem,field:'rsDatasource')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="snmpAddress">snmpAddress:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsComputerSystem,field:'snmpAddress','errors')}">
                            <input type="text" id="snmpAddress" name="snmpAddress" value="${fieldValue(bean:smartsComputerSystem,field:'snmpAddress')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="supportsSNMP">supportsSNMP:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsComputerSystem,field:'supportsSNMP','errors')}">
                            <g:checkBox name="supportsSNMP" value="${smartsComputerSystem?.supportsSNMP}" ></g:checkBox>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="systemName">systemName:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsComputerSystem,field:'systemName','errors')}">
                            <input type="text" id="systemName" name="systemName" value="${fieldValue(bean:smartsComputerSystem,field:'systemName')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="systemObjectID">systemObjectID:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsComputerSystem,field:'systemObjectID','errors')}">
                            <input type="text" id="systemObjectID" name="systemObjectID" value="${fieldValue(bean:smartsComputerSystem,field:'systemObjectID')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="underlying">underlying:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsComputerSystem,field:'underlying','errors')}">
                            
<ul>
<g:each var="u" in="${smartsComputerSystem?.underlying?}">
    <li style="margin-bottom:3px;">
        <g:link controller="smartsVlan" action="show" id="${u.id}">${u}</g:link>
        <g:link class="delete" action="removeRelation" params="['id':smartsComputerSystem?.id, 'relationName':'underlying', 'relatedObjectId':u.id]"></g:link>
    </li>
</g:each>
</ul>
<g:link params="['id':smartsComputerSystem?.id, 'relationName':'underlying']" action="addTo">Add SmartsVlan</g:link>

                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="vendor">vendor:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsComputerSystem,field:'vendor','errors')}">
                            <input type="text" id="vendor" name="vendor" value="${fieldValue(bean:smartsComputerSystem,field:'vendor')}"/>
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
