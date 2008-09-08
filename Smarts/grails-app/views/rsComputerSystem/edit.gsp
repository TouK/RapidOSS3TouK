

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Edit RsComputerSystem</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLinkTo(dir:'')}">Home</a></span>
    <span class="menuButton"><g:link class="list" action="list">RsComputerSystem List</g:link></span>
    <span class="menuButton"><g:link class="create" action="create">New RsComputerSystem</g:link></span>
</div>
<div class="body">
    <h1>Edit RsComputerSystem</h1>
    <g:if test="${flash.message}">
        <div class="message">${flash.message}</div>
    </g:if>
    <g:hasErrors bean="${rsComputerSystem}">
        <div class="errors">
            <g:renderErrors bean="${rsComputerSystem}" as="list"/>
        </div>
    </g:hasErrors>
    <g:hasErrors bean="${flash.errors}">
       <div class="errors">
            <g:renderErrors bean="${flash.errors}"/>
        </div>
    </g:hasErrors>
    <g:form method="post" >
        <input type="hidden" name="id" value="${rsComputerSystem?.id}"/>
        <div class="dialog">
            <table>
                <tbody>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="name">name:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsComputerSystem,field:'name','errors')}">
                            <input type="text" id="name" name="name" value="${fieldValue(bean:rsComputerSystem,field:'name')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="accessMode">accessMode:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsComputerSystem,field:'accessMode','errors')}">
                            <input type="text" id="accessMode" name="accessMode" value="${fieldValue(bean:rsComputerSystem,field:'accessMode')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="composedOf">composedOf:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsComputerSystem,field:'composedOf','errors')}">
                            
<ul>
<g:each var="c" in="${rsComputerSystem?.composedOf?}">
    <li style="margin-bottom:3px;">
        <g:link controller="rsComputerSystemComponent" action="show" id="${c.id}">${c}</g:link>
        <g:link class="delete" action="removeRelation" params="['id':rsComputerSystem?.id, 'relationName':'composedOf', 'relatedObjectId':c.id]"></g:link>
    </li>
</g:each>
</ul>
<g:link params="['id':rsComputerSystem?.id, 'relationName':'composedOf']" action="addTo">Add RsComputerSystemComponent</g:link>

                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="connectedVia">connectedVia:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsComputerSystem,field:'connectedVia','errors')}">
                            
<ul>
<g:each var="c" in="${rsComputerSystem?.connectedVia?}">
    <li style="margin-bottom:3px;">
        <g:link controller="rsLink" action="show" id="${c.id}">${c}</g:link>
        <g:link class="delete" action="removeRelation" params="['id':rsComputerSystem?.id, 'relationName':'connectedVia', 'relatedObjectId':c.id]"></g:link>
    </li>
</g:each>
</ul>
<g:link params="['id':rsComputerSystem?.id, 'relationName':'connectedVia']" action="addTo">Add RsLink</g:link>

                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="creationClassName">creationClassName:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsComputerSystem,field:'creationClassName','errors')}">
                            <input type="text" id="creationClassName" name="creationClassName" value="${fieldValue(bean:rsComputerSystem,field:'creationClassName')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="description">description:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsComputerSystem,field:'description','errors')}">
                            <input type="text" id="description" name="description" value="${fieldValue(bean:rsComputerSystem,field:'description')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="discoveredFirstAt">discoveredFirstAt:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsComputerSystem,field:'discoveredFirstAt','errors')}">
                            <input type="text" id="discoveredFirstAt" name="discoveredFirstAt" value="${fieldValue(bean:rsComputerSystem,field:'discoveredFirstAt')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="discoveredLastAt">discoveredLastAt:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsComputerSystem,field:'discoveredLastAt','errors')}">
                            <input type="text" id="discoveredLastAt" name="discoveredLastAt" value="${fieldValue(bean:rsComputerSystem,field:'discoveredLastAt')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="discoveryErrorInfo">discoveryErrorInfo:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsComputerSystem,field:'discoveryErrorInfo','errors')}">
                            <input type="text" id="discoveryErrorInfo" name="discoveryErrorInfo" value="${fieldValue(bean:rsComputerSystem,field:'discoveryErrorInfo')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="discoveryTime">discoveryTime:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsComputerSystem,field:'discoveryTime','errors')}">
                            <input type="text" id="discoveryTime" name="discoveryTime" value="${fieldValue(bean:rsComputerSystem,field:'discoveryTime')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="displayName">displayName:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsComputerSystem,field:'displayName','errors')}">
                            <input type="text" id="displayName" name="displayName" value="${fieldValue(bean:rsComputerSystem,field:'displayName')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="hostsAccessPoints">hostsAccessPoints:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsComputerSystem,field:'hostsAccessPoints','errors')}">
                            
<ul>
<g:each var="h" in="${rsComputerSystem?.hostsAccessPoints?}">
    <li style="margin-bottom:3px;">
        <g:link controller="rsIp" action="show" id="${h.id}">${h}</g:link>
        <g:link class="delete" action="removeRelation" params="['id':rsComputerSystem?.id, 'relationName':'hostsAccessPoints', 'relatedObjectId':h.id]"></g:link>
    </li>
</g:each>
</ul>
<g:link params="['id':rsComputerSystem?.id, 'relationName':'hostsAccessPoints']" action="addTo">Add RsIp</g:link>

                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="ipNetworks">ipNetworks:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsComputerSystem,field:'ipNetworks','errors')}">
                            
<ul>
<g:each var="i" in="${rsComputerSystem?.ipNetworks?}">
    <li style="margin-bottom:3px;">
        <g:link controller="rsIpNetwork" action="show" id="${i.id}">${i}</g:link>
        <g:link class="delete" action="removeRelation" params="['id':rsComputerSystem?.id, 'relationName':'ipNetworks', 'relatedObjectId':i.id]"></g:link>
    </li>
</g:each>
</ul>
<g:link params="['id':rsComputerSystem?.id, 'relationName':'ipNetworks']" action="addTo">Add RsIpNetwork</g:link>

                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="isManaged">isManaged:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsComputerSystem,field:'isManaged','errors')}">
                            <g:checkBox name="isManaged" value="${rsComputerSystem?.isManaged}" ></g:checkBox>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="location">location:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsComputerSystem,field:'location','errors')}">
                            <input type="text" id="location" name="location" value="${fieldValue(bean:rsComputerSystem,field:'location')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="managementServer">managementServer:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsComputerSystem,field:'managementServer','errors')}">
                            <input type="text" id="managementServer" name="managementServer" value="${fieldValue(bean:rsComputerSystem,field:'managementServer')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="memberOfGroup">memberOfGroup:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsComputerSystem,field:'memberOfGroup','errors')}">
                            
<ul>
<g:each var="m" in="${rsComputerSystem?.memberOfGroup?}">
    <li style="margin-bottom:3px;">
        <g:link controller="rsGroup" action="show" id="${m.id}">${m}</g:link>
        <g:link class="delete" action="removeRelation" params="['id':rsComputerSystem?.id, 'relationName':'memberOfGroup', 'relatedObjectId':m.id]"></g:link>
    </li>
</g:each>
</ul>
<g:link params="['id':rsComputerSystem?.id, 'relationName':'memberOfGroup']" action="addTo">Add RsGroup</g:link>

                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="model">model:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsComputerSystem,field:'model','errors')}">
                            <input type="text" id="model" name="model" value="${fieldValue(bean:rsComputerSystem,field:'model')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="numberOfIPs">numberOfIPs:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsComputerSystem,field:'numberOfIPs','errors')}">
                            <input type="text" id="numberOfIPs" name="numberOfIPs" value="${fieldValue(bean:rsComputerSystem,field:'numberOfIPs')}" />
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="numberOfIPv6s">numberOfIPv6s:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsComputerSystem,field:'numberOfIPv6s','errors')}">
                            <input type="text" id="numberOfIPv6s" name="numberOfIPv6s" value="${fieldValue(bean:rsComputerSystem,field:'numberOfIPv6s')}" />
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="numberOfInterfaces">numberOfInterfaces:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsComputerSystem,field:'numberOfInterfaces','errors')}">
                            <input type="text" id="numberOfInterfaces" name="numberOfInterfaces" value="${fieldValue(bean:rsComputerSystem,field:'numberOfInterfaces')}" />
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="numberOfNetworkAdapters">numberOfNetworkAdapters:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsComputerSystem,field:'numberOfNetworkAdapters','errors')}">
                            <input type="text" id="numberOfNetworkAdapters" name="numberOfNetworkAdapters" value="${fieldValue(bean:rsComputerSystem,field:'numberOfNetworkAdapters')}" />
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="numberOfPorts">numberOfPorts:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsComputerSystem,field:'numberOfPorts','errors')}">
                            <input type="text" id="numberOfPorts" name="numberOfPorts" value="${fieldValue(bean:rsComputerSystem,field:'numberOfPorts')}" />
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="osVersion">osVersion:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsComputerSystem,field:'osVersion','errors')}">
                            <input type="text" id="osVersion" name="osVersion" value="${fieldValue(bean:rsComputerSystem,field:'osVersion')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="primaryOwnerContact">primaryOwnerContact:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsComputerSystem,field:'primaryOwnerContact','errors')}">
                            <input type="text" id="primaryOwnerContact" name="primaryOwnerContact" value="${fieldValue(bean:rsComputerSystem,field:'primaryOwnerContact')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="primaryOwnerName">primaryOwnerName:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsComputerSystem,field:'primaryOwnerName','errors')}">
                            <input type="text" id="primaryOwnerName" name="primaryOwnerName" value="${fieldValue(bean:rsComputerSystem,field:'primaryOwnerName')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="readCommunity">readCommunity:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsComputerSystem,field:'readCommunity','errors')}">
                            <input type="text" id="readCommunity" name="readCommunity" value="${fieldValue(bean:rsComputerSystem,field:'readCommunity')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="rsDatasource">rsDatasource:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsComputerSystem,field:'rsDatasource','errors')}">
                            <input type="text" id="rsDatasource" name="rsDatasource" value="${fieldValue(bean:rsComputerSystem,field:'rsDatasource')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="snmpAddress">snmpAddress:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsComputerSystem,field:'snmpAddress','errors')}">
                            <input type="text" id="snmpAddress" name="snmpAddress" value="${fieldValue(bean:rsComputerSystem,field:'snmpAddress')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="supportsSNMP">supportsSNMP:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsComputerSystem,field:'supportsSNMP','errors')}">
                            <g:checkBox name="supportsSNMP" value="${rsComputerSystem?.supportsSNMP}" ></g:checkBox>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="systemName">systemName:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsComputerSystem,field:'systemName','errors')}">
                            <input type="text" id="systemName" name="systemName" value="${fieldValue(bean:rsComputerSystem,field:'systemName')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="systemObjectID">systemObjectID:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsComputerSystem,field:'systemObjectID','errors')}">
                            <input type="text" id="systemObjectID" name="systemObjectID" value="${fieldValue(bean:rsComputerSystem,field:'systemObjectID')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="vendor">vendor:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsComputerSystem,field:'vendor','errors')}">
                            <input type="text" id="vendor" name="vendor" value="${fieldValue(bean:rsComputerSystem,field:'vendor')}"/>
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
