

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Create RsPort</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLinkTo(dir:'')}">Home</a></span>
    <span class="menuButton"><g:link class="list" action="list">RsPort List</g:link></span>
</div>
<div class="body">
    <h1>Create RsPort</h1>
    <g:if test="${flash.message}">
        <div class="message">${flash.message}</div>
    </g:if>
    <g:hasErrors bean="${rsPort}">
        <div class="errors">
            <g:renderErrors bean="${rsPort}" as="list"/>
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
                        <td valign="top" class="value ${hasErrors(bean:rsPort,field:'name','errors')}">
                            <input type="text" id="name" name="name" value="${fieldValue(bean:rsPort,field:'name')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="adminStatus">adminStatus:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsPort,field:'adminStatus','errors')}">
                            <input type="text" id="adminStatus" name="adminStatus" value="${fieldValue(bean:rsPort,field:'adminStatus')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="cardName">cardName:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsPort,field:'cardName','errors')}">
                            <input type="text" id="cardName" name="cardName" value="${fieldValue(bean:rsPort,field:'cardName')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="computerSystemName">computerSystemName:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsPort,field:'computerSystemName','errors')}">
                            <input type="text" id="computerSystemName" name="computerSystemName" value="${fieldValue(bean:rsPort,field:'computerSystemName')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="creationClassName">creationClassName:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsPort,field:'creationClassName','errors')}">
                            <input type="text" id="creationClassName" name="creationClassName" value="${fieldValue(bean:rsPort,field:'creationClassName')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="description">description:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsPort,field:'description','errors')}">
                            <input type="text" id="description" name="description" value="${fieldValue(bean:rsPort,field:'description')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="designatedBridge">designatedBridge:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsPort,field:'designatedBridge','errors')}">
                            <input type="text" id="designatedBridge" name="designatedBridge" value="${fieldValue(bean:rsPort,field:'designatedBridge')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="designatedPort">designatedPort:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsPort,field:'designatedPort','errors')}">
                            <input type="text" id="designatedPort" name="designatedPort" value="${fieldValue(bean:rsPort,field:'designatedPort')}" />
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="deviceID">deviceID:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsPort,field:'deviceID','errors')}">
                            <input type="text" id="deviceID" name="deviceID" value="${fieldValue(bean:rsPort,field:'deviceID')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="displayClassName">displayClassName:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsPort,field:'displayClassName','errors')}">
                            <input type="text" id="displayClassName" name="displayClassName" value="${fieldValue(bean:rsPort,field:'displayClassName')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="displayName">displayName:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsPort,field:'displayName','errors')}">
                            <input type="text" id="displayName" name="displayName" value="${fieldValue(bean:rsPort,field:'displayName')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="duplexMode">duplexMode:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsPort,field:'duplexMode','errors')}">
                            <input type="text" id="duplexMode" name="duplexMode" value="${fieldValue(bean:rsPort,field:'duplexMode')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="duplexSource">duplexSource:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsPort,field:'duplexSource','errors')}">
                            <input type="text" id="duplexSource" name="duplexSource" value="${fieldValue(bean:rsPort,field:'duplexSource')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="interfaceAlias">interfaceAlias:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsPort,field:'interfaceAlias','errors')}">
                            <input type="text" id="interfaceAlias" name="interfaceAlias" value="${fieldValue(bean:rsPort,field:'interfaceAlias')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="interfaceCode">interfaceCode:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsPort,field:'interfaceCode','errors')}">
                            <input type="text" id="interfaceCode" name="interfaceCode" value="${fieldValue(bean:rsPort,field:'interfaceCode')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="interfaceNumber">interfaceNumber:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsPort,field:'interfaceNumber','errors')}">
                            <input type="text" id="interfaceNumber" name="interfaceNumber" value="${fieldValue(bean:rsPort,field:'interfaceNumber')}" />
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="isConnectedToManagedSystem">isConnectedToManagedSystem:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsPort,field:'isConnectedToManagedSystem','errors')}">
                            <g:checkBox name="isConnectedToManagedSystem" value="${rsPort?.isConnectedToManagedSystem}" ></g:checkBox>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="isConnectedToSystem">isConnectedToSystem:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsPort,field:'isConnectedToSystem','errors')}">
                            <g:checkBox name="isConnectedToSystem" value="${rsPort?.isConnectedToSystem}" ></g:checkBox>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="isConnector">isConnector:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsPort,field:'isConnector','errors')}">
                            <g:checkBox name="isConnector" value="${rsPort?.isConnector}" ></g:checkBox>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="isFlapping">isFlapping:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsPort,field:'isFlapping','errors')}">
                            <g:checkBox name="isFlapping" value="${rsPort?.isFlapping}" ></g:checkBox>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="isManaged">isManaged:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsPort,field:'isManaged','errors')}">
                            <g:checkBox name="isManaged" value="${rsPort?.isManaged}" ></g:checkBox>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="isNetworkAdapterNotOperating">isNetworkAdapterNotOperating:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsPort,field:'isNetworkAdapterNotOperating','errors')}">
                            <g:checkBox name="isNetworkAdapterNotOperating" value="${rsPort?.isNetworkAdapterNotOperating}" ></g:checkBox>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="lastChangedAt">lastChangedAt:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsPort,field:'lastChangedAt','errors')}">
                            <input type="text" id="lastChangedAt" name="lastChangedAt" value="${fieldValue(bean:rsPort,field:'lastChangedAt')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="managedState">managedState:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsPort,field:'managedState','errors')}">
                            <input type="text" id="managedState" name="managedState" value="${fieldValue(bean:rsPort,field:'managedState')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="maxSpeed">maxSpeed:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsPort,field:'maxSpeed','errors')}">
                            <input type="text" id="maxSpeed" name="maxSpeed" value="${fieldValue(bean:rsPort,field:'maxSpeed')}" />
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="maxTransferUnit">maxTransferUnit:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsPort,field:'maxTransferUnit','errors')}">
                            <input type="text" id="maxTransferUnit" name="maxTransferUnit" value="${fieldValue(bean:rsPort,field:'maxTransferUnit')}" />
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="maximumUptime">maximumUptime:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsPort,field:'maximumUptime','errors')}">
                            <input type="text" id="maximumUptime" name="maximumUptime" value="${fieldValue(bean:rsPort,field:'maximumUptime')}" />
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="mib2IfType">mib2IfType:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsPort,field:'mib2IfType','errors')}">
                            <input type="text" id="mib2IfType" name="mib2IfType" value="${fieldValue(bean:rsPort,field:'mib2IfType')}" />
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="mode">mode:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsPort,field:'mode','errors')}">
                            <input type="text" id="mode" name="mode" value="${fieldValue(bean:rsPort,field:'mode')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="operStatus">operStatus:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsPort,field:'operStatus','errors')}">
                            <input type="text" id="operStatus" name="operStatus" value="${fieldValue(bean:rsPort,field:'operStatus')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="peerSystemName">peerSystemName:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsPort,field:'peerSystemName','errors')}">
                            <input type="text" id="peerSystemName" name="peerSystemName" value="${fieldValue(bean:rsPort,field:'peerSystemName')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="peerSystemType">peerSystemType:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsPort,field:'peerSystemType','errors')}">
                            <input type="text" id="peerSystemType" name="peerSystemType" value="${fieldValue(bean:rsPort,field:'peerSystemType')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="portKey">portKey:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsPort,field:'portKey','errors')}">
                            <input type="text" id="portKey" name="portKey" value="${fieldValue(bean:rsPort,field:'portKey')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="portNumber">portNumber:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsPort,field:'portNumber','errors')}">
                            <input type="text" id="portNumber" name="portNumber" value="${fieldValue(bean:rsPort,field:'portNumber')}" />
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="portType">portType:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsPort,field:'portType','errors')}">
                            <input type="text" id="portType" name="portType" value="${fieldValue(bean:rsPort,field:'portType')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="rsDatasource">rsDatasource:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsPort,field:'rsDatasource','errors')}">
                            <input type="text" id="rsDatasource" name="rsDatasource" value="${fieldValue(bean:rsPort,field:'rsDatasource')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="status">status:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsPort,field:'status','errors')}">
                            <input type="text" id="status" name="status" value="${fieldValue(bean:rsPort,field:'status')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="systemModel">systemModel:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsPort,field:'systemModel','errors')}">
                            <input type="text" id="systemModel" name="systemModel" value="${fieldValue(bean:rsPort,field:'systemModel')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="systemName">systemName:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsPort,field:'systemName','errors')}">
                            <input type="text" id="systemName" name="systemName" value="${fieldValue(bean:rsPort,field:'systemName')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="systemObjectID">systemObjectID:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsPort,field:'systemObjectID','errors')}">
                            <input type="text" id="systemObjectID" name="systemObjectID" value="${fieldValue(bean:rsPort,field:'systemObjectID')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="systemType">systemType:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsPort,field:'systemType','errors')}">
                            <input type="text" id="systemType" name="systemType" value="${fieldValue(bean:rsPort,field:'systemType')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="systemVendor">systemVendor:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsPort,field:'systemVendor','errors')}">
                            <input type="text" id="systemVendor" name="systemVendor" value="${fieldValue(bean:rsPort,field:'systemVendor')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="tag">tag:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsPort,field:'tag','errors')}">
                            <input type="text" id="tag" name="tag" value="${fieldValue(bean:rsPort,field:'tag')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="type">type:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsPort,field:'type','errors')}">
                            <input type="text" id="type" name="type" value="${fieldValue(bean:rsPort,field:'type')}"/>
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
