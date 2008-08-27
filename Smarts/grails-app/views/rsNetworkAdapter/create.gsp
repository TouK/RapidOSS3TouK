

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Create RsNetworkAdapter</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLinkTo(dir:'')}">Home</a></span>
    <span class="menuButton"><g:link class="list" action="list">RsNetworkAdapter List</g:link></span>
</div>
<div class="body">
    <h1>Create RsNetworkAdapter</h1>
    <g:if test="${flash.message}">
        <div class="message">${flash.message}</div>
    </g:if>
    <g:hasErrors bean="${rsNetworkAdapter}">
        <div class="errors">
            <g:renderErrors bean="${rsNetworkAdapter}" as="list"/>
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
                        <td valign="top" class="value ${hasErrors(bean:rsNetworkAdapter,field:'name','errors')}">
                            <input type="text" id="name" name="name" value="${fieldValue(bean:rsNetworkAdapter,field:'name')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="adminStatus">adminStatus:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsNetworkAdapter,field:'adminStatus','errors')}">
                            <input type="text" id="adminStatus" name="adminStatus" value="${fieldValue(bean:rsNetworkAdapter,field:'adminStatus')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="cardName">cardName:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsNetworkAdapter,field:'cardName','errors')}">
                            <input type="text" id="cardName" name="cardName" value="${fieldValue(bean:rsNetworkAdapter,field:'cardName')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="computerSystemName">computerSystemName:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsNetworkAdapter,field:'computerSystemName','errors')}">
                            <input type="text" id="computerSystemName" name="computerSystemName" value="${fieldValue(bean:rsNetworkAdapter,field:'computerSystemName')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="creationClassName">creationClassName:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsNetworkAdapter,field:'creationClassName','errors')}">
                            <input type="text" id="creationClassName" name="creationClassName" value="${fieldValue(bean:rsNetworkAdapter,field:'creationClassName')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="description">description:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsNetworkAdapter,field:'description','errors')}">
                            <input type="text" id="description" name="description" value="${fieldValue(bean:rsNetworkAdapter,field:'description')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="deviceID">deviceID:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsNetworkAdapter,field:'deviceID','errors')}">
                            <input type="text" id="deviceID" name="deviceID" value="${fieldValue(bean:rsNetworkAdapter,field:'deviceID')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="displayClassName">displayClassName:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsNetworkAdapter,field:'displayClassName','errors')}">
                            <input type="text" id="displayClassName" name="displayClassName" value="${fieldValue(bean:rsNetworkAdapter,field:'displayClassName')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="displayName">displayName:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsNetworkAdapter,field:'displayName','errors')}">
                            <input type="text" id="displayName" name="displayName" value="${fieldValue(bean:rsNetworkAdapter,field:'displayName')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="duplexMode">duplexMode:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsNetworkAdapter,field:'duplexMode','errors')}">
                            <input type="text" id="duplexMode" name="duplexMode" value="${fieldValue(bean:rsNetworkAdapter,field:'duplexMode')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="duplexSource">duplexSource:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsNetworkAdapter,field:'duplexSource','errors')}">
                            <input type="text" id="duplexSource" name="duplexSource" value="${fieldValue(bean:rsNetworkAdapter,field:'duplexSource')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="interfaceAlias">interfaceAlias:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsNetworkAdapter,field:'interfaceAlias','errors')}">
                            <input type="text" id="interfaceAlias" name="interfaceAlias" value="${fieldValue(bean:rsNetworkAdapter,field:'interfaceAlias')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="interfaceCode">interfaceCode:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsNetworkAdapter,field:'interfaceCode','errors')}">
                            <input type="text" id="interfaceCode" name="interfaceCode" value="${fieldValue(bean:rsNetworkAdapter,field:'interfaceCode')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="interfaceNumber">interfaceNumber:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsNetworkAdapter,field:'interfaceNumber','errors')}">
                            <input type="text" id="interfaceNumber" name="interfaceNumber" value="${fieldValue(bean:rsNetworkAdapter,field:'interfaceNumber')}" />
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="isConnector">isConnector:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsNetworkAdapter,field:'isConnector','errors')}">
                            <g:checkBox name="isConnector" value="${rsNetworkAdapter?.isConnector}" ></g:checkBox>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="isFlapping">isFlapping:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsNetworkAdapter,field:'isFlapping','errors')}">
                            <g:checkBox name="isFlapping" value="${rsNetworkAdapter?.isFlapping}" ></g:checkBox>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="isManaged">isManaged:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsNetworkAdapter,field:'isManaged','errors')}">
                            <g:checkBox name="isManaged" value="${rsNetworkAdapter?.isManaged}" ></g:checkBox>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="isNetworkAdapterNotOperating">isNetworkAdapterNotOperating:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsNetworkAdapter,field:'isNetworkAdapterNotOperating','errors')}">
                            <g:checkBox name="isNetworkAdapterNotOperating" value="${rsNetworkAdapter?.isNetworkAdapterNotOperating}" ></g:checkBox>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="lastChangedAt">lastChangedAt:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsNetworkAdapter,field:'lastChangedAt','errors')}">
                            <input type="text" id="lastChangedAt" name="lastChangedAt" value="${fieldValue(bean:rsNetworkAdapter,field:'lastChangedAt')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="maxSpeed">maxSpeed:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsNetworkAdapter,field:'maxSpeed','errors')}">
                            <input type="text" id="maxSpeed" name="maxSpeed" value="${fieldValue(bean:rsNetworkAdapter,field:'maxSpeed')}" />
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="maxTransferUnit">maxTransferUnit:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsNetworkAdapter,field:'maxTransferUnit','errors')}">
                            <input type="text" id="maxTransferUnit" name="maxTransferUnit" value="${fieldValue(bean:rsNetworkAdapter,field:'maxTransferUnit')}" />
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="maximumUptime">maximumUptime:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsNetworkAdapter,field:'maximumUptime','errors')}">
                            <input type="text" id="maximumUptime" name="maximumUptime" value="${fieldValue(bean:rsNetworkAdapter,field:'maximumUptime')}" />
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="mib2IfType">mib2IfType:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsNetworkAdapter,field:'mib2IfType','errors')}">
                            <input type="text" id="mib2IfType" name="mib2IfType" value="${fieldValue(bean:rsNetworkAdapter,field:'mib2IfType')}" />
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="mode">mode:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsNetworkAdapter,field:'mode','errors')}">
                            <input type="text" id="mode" name="mode" value="${fieldValue(bean:rsNetworkAdapter,field:'mode')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="operStatus">operStatus:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsNetworkAdapter,field:'operStatus','errors')}">
                            <input type="text" id="operStatus" name="operStatus" value="${fieldValue(bean:rsNetworkAdapter,field:'operStatus')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="peerSystemName">peerSystemName:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsNetworkAdapter,field:'peerSystemName','errors')}">
                            <input type="text" id="peerSystemName" name="peerSystemName" value="${fieldValue(bean:rsNetworkAdapter,field:'peerSystemName')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="peerSystemType">peerSystemType:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsNetworkAdapter,field:'peerSystemType','errors')}">
                            <input type="text" id="peerSystemType" name="peerSystemType" value="${fieldValue(bean:rsNetworkAdapter,field:'peerSystemType')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="status">status:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsNetworkAdapter,field:'status','errors')}">
                            <input type="text" id="status" name="status" value="${fieldValue(bean:rsNetworkAdapter,field:'status')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="systemModel">systemModel:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsNetworkAdapter,field:'systemModel','errors')}">
                            <input type="text" id="systemModel" name="systemModel" value="${fieldValue(bean:rsNetworkAdapter,field:'systemModel')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="systemName">systemName:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsNetworkAdapter,field:'systemName','errors')}">
                            <input type="text" id="systemName" name="systemName" value="${fieldValue(bean:rsNetworkAdapter,field:'systemName')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="systemObjectID">systemObjectID:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsNetworkAdapter,field:'systemObjectID','errors')}">
                            <input type="text" id="systemObjectID" name="systemObjectID" value="${fieldValue(bean:rsNetworkAdapter,field:'systemObjectID')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="systemType">systemType:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsNetworkAdapter,field:'systemType','errors')}">
                            <input type="text" id="systemType" name="systemType" value="${fieldValue(bean:rsNetworkAdapter,field:'systemType')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="systemVendor">systemVendor:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsNetworkAdapter,field:'systemVendor','errors')}">
                            <input type="text" id="systemVendor" name="systemVendor" value="${fieldValue(bean:rsNetworkAdapter,field:'systemVendor')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="tag">tag:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsNetworkAdapter,field:'tag','errors')}">
                            <input type="text" id="tag" name="tag" value="${fieldValue(bean:rsNetworkAdapter,field:'tag')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="type">type:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsNetworkAdapter,field:'type','errors')}">
                            <input type="text" id="type" name="type" value="${fieldValue(bean:rsNetworkAdapter,field:'type')}"/>
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
