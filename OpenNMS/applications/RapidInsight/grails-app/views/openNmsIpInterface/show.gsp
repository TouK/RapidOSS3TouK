

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Show OpenNmsIpInterface</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLinkTo(dir:'')}">Home</a></span>
    <span class="menuButton"><g:link class="list" action="list">OpenNmsIpInterface List</g:link></span>
    <span class="menuButton"><g:link class="create" action="create">New OpenNmsIpInterface</g:link></span>
</div>
<div class="body">
    <h1>Show OpenNmsIpInterface</h1>
    <g:if test="${flash.message}">
        <div class="message">${flash.message}</div>
    </g:if>
    <g:hasErrors bean="${flash.errors}">
       <div class="errors">
            <g:renderErrors bean="${flash.errors}"/>
        </div>
    </g:hasErrors>
    <div class="dialog">
        <table>
            <tbody>

                
                <tr class="prop">
                    <td valign="top" class="name">id:</td>
                    
                    <td valign="top" class="value">${openNmsIpInterface.id}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">openNmsId:</td>
                    
                    <td valign="top" class="value">${openNmsIpInterface.openNmsId}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">adminStatus:</td>
                    
                    <td valign="top" class="value">${openNmsIpInterface.adminStatus}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">graphs:</td>
                    
                    <td valign="top" style="text-align:left;" class="value">
                        <ul>
                            <g:each var="g" in="${openNmsIpInterface.graphs}">
                                <li><g:link controller="openNmsGraph" action="show" id="${g.id}">${g}</g:link></li>
                            </g:each>
                        </ul>
                    </td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">ifAlias:</td>
                    
                    <td valign="top" class="value">${openNmsIpInterface.ifAlias}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">ifDescription:</td>
                    
                    <td valign="top" class="value">${openNmsIpInterface.ifDescription}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">ifIndex:</td>
                    
                    <td valign="top" class="value">${openNmsIpInterface.ifIndex}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">ifName:</td>
                    
                    <td valign="top" class="value">${openNmsIpInterface.ifName}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">ifSpeed:</td>
                    
                    <td valign="top" class="value">${openNmsIpInterface.ifSpeed}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">ifType:</td>
                    
                    <td valign="top" class="value">${openNmsIpInterface.ifType}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">ipAddress:</td>
                    
                    <td valign="top" class="value">${openNmsIpInterface.ipAddress}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">lastPolledAt:</td>
                    
                    <td valign="top" class="value">${openNmsIpInterface.lastPolledAt}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">macAddress:</td>
                    
                    <td valign="top" class="value">${openNmsIpInterface.macAddress}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">netmask:</td>
                    
                    <td valign="top" class="value">${openNmsIpInterface.netmask}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">node:</td>
                    
                    <td valign="top" class="value"><g:link controller="openNmsNode" action="show" id="${openNmsIpInterface?.node?.id}">${openNmsIpInterface?.node}</g:link></td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">operStatus:</td>
                    
                    <td valign="top" class="value">${openNmsIpInterface.operStatus}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">services:</td>
                    
                    <td valign="top" style="text-align:left;" class="value">
                        <ul>
                            <g:each var="s" in="${openNmsIpInterface.services}">
                                <li><g:link controller="openNmsService" action="show" id="${s.id}">${s}</g:link></li>
                            </g:each>
                        </ul>
                    </td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">snmpInterfaceId:</td>
                    
                    <td valign="top" class="value">${openNmsIpInterface.snmpInterfaceId}</td>
                    
                </tr>
                
            </tbody>
        </table>
    </div>
    <div class="buttons">
        <g:form>
            <input type="hidden" name="id" value="${openNmsIpInterface?.id}"/>
            <span class="button"><g:actionSubmit class="edit" value="Edit"/></span>
            <span class="button"><g:actionSubmit class="delete" onclick="return confirm('Are you sure?');" value="Delete"/></span>
        </g:form>
    </div>
</div>
</body>
</html>
