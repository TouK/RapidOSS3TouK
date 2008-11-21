

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Show SmartsComputerSystem</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLinkTo(dir:'')}">Home</a></span>
    <span class="menuButton"><g:link class="list" action="list">SmartsComputerSystem List</g:link></span>
    <span class="menuButton"><g:link class="create" action="create">New SmartsComputerSystem</g:link></span>
</div>
<div class="body">
    <h1>Show SmartsComputerSystem</h1>
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
                    
                    <td valign="top" class="value">${smartsComputerSystem.id}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">name:</td>
                    
                    <td valign="top" class="value">${smartsComputerSystem.name}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">accessMode:</td>
                    
                    <td valign="top" class="value">${smartsComputerSystem.accessMode}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">className:</td>
                    
                    <td valign="top" class="value">${smartsComputerSystem.className}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">composedOf:</td>
                    
                    <td valign="top" style="text-align:left;" class="value">
                        <ul>
                            <g:each var="c" in="${smartsComputerSystem.composedOf}">
                                <li><g:link controller="smartsComputerSystemComponent" action="show" id="${c.id}">${c}</g:link></li>
                            </g:each>
                        </ul>
                    </td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">connectedVia:</td>
                    
                    <td valign="top" style="text-align:left;" class="value">
                        <ul>
                            <g:each var="c" in="${smartsComputerSystem.connectedVia}">
                                <li><g:link controller="rsLink" action="show" id="${c.id}">${c}</g:link></li>
                            </g:each>
                        </ul>
                    </td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">connectedViaVlan:</td>
                    
                    <td valign="top" style="text-align:left;" class="value">
                        <ul>
                            <g:each var="c" in="${smartsComputerSystem.connectedViaVlan}">
                                <li><g:link controller="smartsVlan" action="show" id="${c.id}">${c}</g:link></li>
                            </g:each>
                        </ul>
                    </td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">description:</td>
                    
                    <td valign="top" class="value">${smartsComputerSystem.description}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">discoveredFirstAt:</td>
                    
                    <td valign="top" class="value">${smartsComputerSystem.discoveredFirstAt}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">discoveredLastAt:</td>
                    
                    <td valign="top" class="value">${smartsComputerSystem.discoveredLastAt}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">discoveryErrorInfo:</td>
                    
                    <td valign="top" class="value">${smartsComputerSystem.discoveryErrorInfo}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">discoveryTime:</td>
                    
                    <td valign="top" class="value">${smartsComputerSystem.discoveryTime}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">displayName:</td>
                    
                    <td valign="top" class="value">${smartsComputerSystem.displayName}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">geocodes:</td>
                    
                    <td valign="top" class="value">${smartsComputerSystem.geocodes}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">hostsAccessPoints:</td>
                    
                    <td valign="top" style="text-align:left;" class="value">
                        <ul>
                            <g:each var="h" in="${smartsComputerSystem.hostsAccessPoints}">
                                <li><g:link controller="smartsIp" action="show" id="${h.id}">${h}</g:link></li>
                            </g:each>
                        </ul>
                    </td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">hsrpGroup:</td>
                    
                    <td valign="top" class="value"><g:link controller="smartsHSRPGroup" action="show" id="${smartsComputerSystem?.hsrpGroup?.id}">${smartsComputerSystem?.hsrpGroup}</g:link></td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">ipNetworks:</td>
                    
                    <td valign="top" style="text-align:left;" class="value">
                        <ul>
                            <g:each var="i" in="${smartsComputerSystem.ipNetworks}">
                                <li><g:link controller="smartsIpNetwork" action="show" id="${i.id}">${i}</g:link></li>
                            </g:each>
                        </ul>
                    </td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">isManaged:</td>
                    
                    <td valign="top" class="value">${smartsComputerSystem.isManaged}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">location:</td>
                    
                    <td valign="top" class="value">${smartsComputerSystem.location}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">managementServer:</td>
                    
                    <td valign="top" class="value">${smartsComputerSystem.managementServer}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">memberOfGroup:</td>
                    
                    <td valign="top" style="text-align:left;" class="value">
                        <ul>
                            <g:each var="m" in="${smartsComputerSystem.memberOfGroup}">
                                <li><g:link controller="rsGroup" action="show" id="${m.id}">${m}</g:link></li>
                            </g:each>
                        </ul>
                    </td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">model:</td>
                    
                    <td valign="top" class="value">${smartsComputerSystem.model}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">numberOfIPs:</td>
                    
                    <td valign="top" class="value">${smartsComputerSystem.numberOfIPs}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">numberOfIPv6s:</td>
                    
                    <td valign="top" class="value">${smartsComputerSystem.numberOfIPv6s}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">numberOfInterfaces:</td>
                    
                    <td valign="top" class="value">${smartsComputerSystem.numberOfInterfaces}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">numberOfNetworkAdapters:</td>
                    
                    <td valign="top" class="value">${smartsComputerSystem.numberOfNetworkAdapters}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">numberOfPorts:</td>
                    
                    <td valign="top" class="value">${smartsComputerSystem.numberOfPorts}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">osVersion:</td>
                    
                    <td valign="top" class="value">${smartsComputerSystem.osVersion}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">partOf:</td>
                    
                    <td valign="top" style="text-align:left;" class="value">
                        <ul>
                            <g:each var="p" in="${smartsComputerSystem.partOf}">
                                <li><g:link controller="smartsVlan" action="show" id="${p.id}">${p}</g:link></li>
                            </g:each>
                        </ul>
                    </td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">primaryOwnerContact:</td>
                    
                    <td valign="top" class="value">${smartsComputerSystem.primaryOwnerContact}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">primaryOwnerName:</td>
                    
                    <td valign="top" class="value">${smartsComputerSystem.primaryOwnerName}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">readCommunity:</td>
                    
                    <td valign="top" class="value">${smartsComputerSystem.readCommunity}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">rsDatasource:</td>
                    
                    <td valign="top" class="value">${smartsComputerSystem.rsDatasource}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">snmpAddress:</td>
                    
                    <td valign="top" class="value">${smartsComputerSystem.snmpAddress}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">supportsSNMP:</td>
                    
                    <td valign="top" class="value">${smartsComputerSystem.supportsSNMP}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">systemName:</td>
                    
                    <td valign="top" class="value">${smartsComputerSystem.systemName}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">systemObjectID:</td>
                    
                    <td valign="top" class="value">${smartsComputerSystem.systemObjectID}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">underlying:</td>
                    
                    <td valign="top" style="text-align:left;" class="value">
                        <ul>
                            <g:each var="u" in="${smartsComputerSystem.underlying}">
                                <li><g:link controller="smartsVlan" action="show" id="${u.id}">${u}</g:link></li>
                            </g:each>
                        </ul>
                    </td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">vendor:</td>
                    
                    <td valign="top" class="value">${smartsComputerSystem.vendor}</td>
                    
                </tr>
                
            </tbody>
        </table>
    </div>
    <div class="buttons">
        <g:form>
            <input type="hidden" name="id" value="${smartsComputerSystem?.id}"/>
            <span class="button"><g:actionSubmit class="edit" value="Edit"/></span>
            <span class="button"><g:actionSubmit class="delete" onclick="return confirm('Are you sure?');" value="Delete"/></span>
        </g:form>
    </div>
</div>
</body>
</html>
