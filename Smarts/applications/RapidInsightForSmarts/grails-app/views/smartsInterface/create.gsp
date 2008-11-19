

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Create SmartsInterface</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLinkTo(dir:'')}">Home</a></span>
    <span class="menuButton"><g:link class="list" action="list">SmartsInterface List</g:link></span>
</div>
<div class="body">
    <h1>Create SmartsInterface</h1>
    <g:if test="${flash.message}">
        <div class="message">${flash.message}</div>
    </g:if>
    <g:hasErrors bean="${smartsInterface}">
        <div class="errors">
            <g:renderErrors bean="${smartsInterface}" as="list"/>
        </div>
    </g:hasErrors>
    <g:hasErrors bean="${flash.errors}">
       <div class="errors">
            <g:renderErrors bean="${flash.errors}"/>
        </div>
    </g:hasErrors>
    <g:form action="save" method="post" >
        <div class="dialog">
            <table>
                <tbody>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="name">name:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsInterface,field:'name','errors')}">
                            <input type="text" id="name" name="name" value="${fieldValue(bean:smartsInterface,field:'name')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="adminStatus">adminStatus:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsInterface,field:'adminStatus','errors')}">
                            <input type="text" id="adminStatus" name="adminStatus" value="${fieldValue(bean:smartsInterface,field:'adminStatus')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="cardName">cardName:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsInterface,field:'cardName','errors')}">
                            <input type="text" id="cardName" name="cardName" value="${fieldValue(bean:smartsInterface,field:'cardName')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="className">className:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsInterface,field:'className','errors')}">
                            <input type="text" id="className" name="className" value="${fieldValue(bean:smartsInterface,field:'className')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="computerSystemName">computerSystemName:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsInterface,field:'computerSystemName','errors')}">
                            <input type="text" id="computerSystemName" name="computerSystemName" value="${fieldValue(bean:smartsInterface,field:'computerSystemName')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="connectedVia">connectedVia:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsInterface,field:'connectedVia','errors')}">
                            <g:select optionKey="id" from="${SmartsLink.list()}" name="connectedVia.id" value="${smartsInterface?.connectedVia?.id}" noSelection="['null':'']"></g:select>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="description">description:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsInterface,field:'description','errors')}">
                            <input type="text" id="description" name="description" value="${fieldValue(bean:smartsInterface,field:'description')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="deviceID">deviceID:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsInterface,field:'deviceID','errors')}">
                            <input type="text" id="deviceID" name="deviceID" value="${fieldValue(bean:smartsInterface,field:'deviceID')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="displayClassName">displayClassName:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsInterface,field:'displayClassName','errors')}">
                            <input type="text" id="displayClassName" name="displayClassName" value="${fieldValue(bean:smartsInterface,field:'displayClassName')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="displayName">displayName:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsInterface,field:'displayName','errors')}">
                            <input type="text" id="displayName" name="displayName" value="${fieldValue(bean:smartsInterface,field:'displayName')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="duplexMode">duplexMode:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsInterface,field:'duplexMode','errors')}">
                            <input type="text" id="duplexMode" name="duplexMode" value="${fieldValue(bean:smartsInterface,field:'duplexMode')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="duplexSource">duplexSource:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsInterface,field:'duplexSource','errors')}">
                            <input type="text" id="duplexSource" name="duplexSource" value="${fieldValue(bean:smartsInterface,field:'duplexSource')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="hasIPAddresses">hasIPAddresses:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsInterface,field:'hasIPAddresses','errors')}">
                            <g:checkBox name="hasIPAddresses" value="${smartsInterface?.hasIPAddresses}" ></g:checkBox>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="hasIPv6Addresses">hasIPv6Addresses:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsInterface,field:'hasIPv6Addresses','errors')}">
                            <g:checkBox name="hasIPv6Addresses" value="${smartsInterface?.hasIPv6Addresses}" ></g:checkBox>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="interfaceAlias">interfaceAlias:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsInterface,field:'interfaceAlias','errors')}">
                            <input type="text" id="interfaceAlias" name="interfaceAlias" value="${fieldValue(bean:smartsInterface,field:'interfaceAlias')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="interfaceCode">interfaceCode:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsInterface,field:'interfaceCode','errors')}">
                            <input type="text" id="interfaceCode" name="interfaceCode" value="${fieldValue(bean:smartsInterface,field:'interfaceCode')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="interfaceKey">interfaceKey:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsInterface,field:'interfaceKey','errors')}">
                            <input type="text" id="interfaceKey" name="interfaceKey" value="${fieldValue(bean:smartsInterface,field:'interfaceKey')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="interfaceNumber">interfaceNumber:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsInterface,field:'interfaceNumber','errors')}">
                            <input type="text" id="interfaceNumber" name="interfaceNumber" value="${fieldValue(bean:smartsInterface,field:'interfaceNumber')}" />
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="isConnector">isConnector:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsInterface,field:'isConnector','errors')}">
                            <g:checkBox name="isConnector" value="${smartsInterface?.isConnector}" ></g:checkBox>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="isFlapping">isFlapping:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsInterface,field:'isFlapping','errors')}">
                            <g:checkBox name="isFlapping" value="${smartsInterface?.isFlapping}" ></g:checkBox>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="isManaged">isManaged:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsInterface,field:'isManaged','errors')}">
                            <g:checkBox name="isManaged" value="${smartsInterface?.isManaged}" ></g:checkBox>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="isNetworkAdapterNotOperating">isNetworkAdapterNotOperating:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsInterface,field:'isNetworkAdapterNotOperating','errors')}">
                            <g:checkBox name="isNetworkAdapterNotOperating" value="${smartsInterface?.isNetworkAdapterNotOperating}" ></g:checkBox>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="lastChangedAt">lastChangedAt:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsInterface,field:'lastChangedAt','errors')}">
                            <input type="text" id="lastChangedAt" name="lastChangedAt" value="${fieldValue(bean:smartsInterface,field:'lastChangedAt')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="maxSpeed">maxSpeed:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsInterface,field:'maxSpeed','errors')}">
                            <input type="text" id="maxSpeed" name="maxSpeed" value="${fieldValue(bean:smartsInterface,field:'maxSpeed')}" />
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="maxTransferUnit">maxTransferUnit:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsInterface,field:'maxTransferUnit','errors')}">
                            <input type="text" id="maxTransferUnit" name="maxTransferUnit" value="${fieldValue(bean:smartsInterface,field:'maxTransferUnit')}" />
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="maximumUptime">maximumUptime:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsInterface,field:'maximumUptime','errors')}">
                            <input type="text" id="maximumUptime" name="maximumUptime" value="${fieldValue(bean:smartsInterface,field:'maximumUptime')}" />
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="mib2IfType">mib2IfType:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsInterface,field:'mib2IfType','errors')}">
                            <input type="text" id="mib2IfType" name="mib2IfType" value="${fieldValue(bean:smartsInterface,field:'mib2IfType')}" />
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="mode">mode:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsInterface,field:'mode','errors')}">
                            <input type="text" id="mode" name="mode" value="${fieldValue(bean:smartsInterface,field:'mode')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="operStatus">operStatus:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsInterface,field:'operStatus','errors')}">
                            <input type="text" id="operStatus" name="operStatus" value="${fieldValue(bean:smartsInterface,field:'operStatus')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="partOf">partOf:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsInterface,field:'partOf','errors')}">
                            <g:select optionKey="id" from="${SmartsComputerSystem.list()}" name="partOf.id" value="${smartsInterface?.partOf?.id}" noSelection="['null':'']"></g:select>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="peerSystemName">peerSystemName:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsInterface,field:'peerSystemName','errors')}">
                            <input type="text" id="peerSystemName" name="peerSystemName" value="${fieldValue(bean:smartsInterface,field:'peerSystemName')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="peerSystemType">peerSystemType:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsInterface,field:'peerSystemType','errors')}">
                            <input type="text" id="peerSystemType" name="peerSystemType" value="${fieldValue(bean:smartsInterface,field:'peerSystemType')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="realizedBy">realizedBy:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsInterface,field:'realizedBy','errors')}">
                            <g:select optionKey="id" from="${SmartsCard.list()}" name="realizedBy.id" value="${smartsInterface?.realizedBy?.id}" noSelection="['null':'']"></g:select>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="rsDatasource">rsDatasource:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsInterface,field:'rsDatasource','errors')}">
                            <input type="text" id="rsDatasource" name="rsDatasource" value="${fieldValue(bean:smartsInterface,field:'rsDatasource')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="status">status:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsInterface,field:'status','errors')}">
                            <input type="text" id="status" name="status" value="${fieldValue(bean:smartsInterface,field:'status')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="systemModel">systemModel:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsInterface,field:'systemModel','errors')}">
                            <input type="text" id="systemModel" name="systemModel" value="${fieldValue(bean:smartsInterface,field:'systemModel')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="systemName">systemName:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsInterface,field:'systemName','errors')}">
                            <input type="text" id="systemName" name="systemName" value="${fieldValue(bean:smartsInterface,field:'systemName')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="systemObjectID">systemObjectID:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsInterface,field:'systemObjectID','errors')}">
                            <input type="text" id="systemObjectID" name="systemObjectID" value="${fieldValue(bean:smartsInterface,field:'systemObjectID')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="systemType">systemType:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsInterface,field:'systemType','errors')}">
                            <input type="text" id="systemType" name="systemType" value="${fieldValue(bean:smartsInterface,field:'systemType')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="systemVendor">systemVendor:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsInterface,field:'systemVendor','errors')}">
                            <input type="text" id="systemVendor" name="systemVendor" value="${fieldValue(bean:smartsInterface,field:'systemVendor')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="tag">tag:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsInterface,field:'tag','errors')}">
                            <input type="text" id="tag" name="tag" value="${fieldValue(bean:smartsInterface,field:'tag')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="type">type:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsInterface,field:'type','errors')}">
                            <input type="text" id="type" name="type" value="${fieldValue(bean:smartsInterface,field:'type')}"/>
                        </td>
                    </tr>
                    
                </tbody>
            </table>
        </div>
        <div class="buttons">
            <span class="button"><input class="save" type="submit" value="Create"/></span>
        </div>
    </g:form>
</div>
</body>
</html>
