

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Create OpenNmsIpInterface</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLinkTo(dir:'')}">Home</a></span>
    <span class="menuButton"><g:link class="list" action="list">OpenNmsIpInterface List</g:link></span>
</div>
<div class="body">
    <h1>Create OpenNmsIpInterface</h1>
    <g:if test="${flash.message}">
        <div class="message">${flash.message}</div>
    </g:if>
    <g:hasErrors bean="${openNmsIpInterface}">
        <div class="errors">
            <g:renderErrors bean="${openNmsIpInterface}" as="list"/>
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
                        <td valign="top" class="value ${hasErrors(bean:openNmsIpInterface,field:'name','errors')}">
                            <input type="text" id="name" name="name" value="${fieldValue(bean:openNmsIpInterface,field:'name')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="adminStatus">adminStatus:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:openNmsIpInterface,field:'adminStatus','errors')}">
                            <input type="text" id="adminStatus" name="adminStatus" value="${fieldValue(bean:openNmsIpInterface,field:'adminStatus')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="className">className:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:openNmsIpInterface,field:'className','errors')}">
                            <input type="text" id="className" name="className" value="${fieldValue(bean:openNmsIpInterface,field:'className')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="description">description:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:openNmsIpInterface,field:'description','errors')}">
                            <input type="text" id="description" name="description" value="${fieldValue(bean:openNmsIpInterface,field:'description')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="displayName">displayName:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:openNmsIpInterface,field:'displayName','errors')}">
                            <input type="text" id="displayName" name="displayName" value="${fieldValue(bean:openNmsIpInterface,field:'displayName')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="ifAlias">ifAlias:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:openNmsIpInterface,field:'ifAlias','errors')}">
                            <input type="text" id="ifAlias" name="ifAlias" value="${fieldValue(bean:openNmsIpInterface,field:'ifAlias')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="ifDescription">ifDescription:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:openNmsIpInterface,field:'ifDescription','errors')}">
                            <input type="text" id="ifDescription" name="ifDescription" value="${fieldValue(bean:openNmsIpInterface,field:'ifDescription')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="ifIndex">ifIndex:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:openNmsIpInterface,field:'ifIndex','errors')}">
                            <input type="text" id="ifIndex" name="ifIndex" value="${fieldValue(bean:openNmsIpInterface,field:'ifIndex')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="ifName">ifName:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:openNmsIpInterface,field:'ifName','errors')}">
                            <input type="text" id="ifName" name="ifName" value="${fieldValue(bean:openNmsIpInterface,field:'ifName')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="ifSpeed">ifSpeed:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:openNmsIpInterface,field:'ifSpeed','errors')}">
                            <input type="text" id="ifSpeed" name="ifSpeed" value="${fieldValue(bean:openNmsIpInterface,field:'ifSpeed')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="ifType">ifType:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:openNmsIpInterface,field:'ifType','errors')}">
                            <input type="text" id="ifType" name="ifType" value="${fieldValue(bean:openNmsIpInterface,field:'ifType')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="ipAddress">ipAddress:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:openNmsIpInterface,field:'ipAddress','errors')}">
                            <input type="text" id="ipAddress" name="ipAddress" value="${fieldValue(bean:openNmsIpInterface,field:'ipAddress')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="isManaged">isManaged:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:openNmsIpInterface,field:'isManaged','errors')}">
                            <g:checkBox name="isManaged" value="${openNmsIpInterface?.isManaged}" ></g:checkBox>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="lastPolledAt">lastPolledAt:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:openNmsIpInterface,field:'lastPolledAt','errors')}">
                            <g:datePicker name="lastPolledAt" value="${openNmsIpInterface?.lastPolledAt}" noSelection="['':'']"></g:datePicker>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="macAddress">macAddress:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:openNmsIpInterface,field:'macAddress','errors')}">
                            <input type="text" id="macAddress" name="macAddress" value="${fieldValue(bean:openNmsIpInterface,field:'macAddress')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="netmask">netmask:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:openNmsIpInterface,field:'netmask','errors')}">
                            <input type="text" id="netmask" name="netmask" value="${fieldValue(bean:openNmsIpInterface,field:'netmask')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="node">node:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:openNmsIpInterface,field:'node','errors')}">
                            <g:select optionKey="id" from="${OpenNmsNode.list()}" name="node.id" value="${openNmsIpInterface?.node?.id}" noSelection="['null':'']"></g:select>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="operStatus">operStatus:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:openNmsIpInterface,field:'operStatus','errors')}">
                            <input type="text" id="operStatus" name="operStatus" value="${fieldValue(bean:openNmsIpInterface,field:'operStatus')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="rsDatasource">rsDatasource:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:openNmsIpInterface,field:'rsDatasource','errors')}">
                            <input type="text" id="rsDatasource" name="rsDatasource" value="${fieldValue(bean:openNmsIpInterface,field:'rsDatasource')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="snmpInterfaceId">snmpInterfaceId:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:openNmsIpInterface,field:'snmpInterfaceId','errors')}">
                            <input type="text" id="snmpInterfaceId" name="snmpInterfaceId" value="${fieldValue(bean:openNmsIpInterface,field:'snmpInterfaceId')}"/>
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
