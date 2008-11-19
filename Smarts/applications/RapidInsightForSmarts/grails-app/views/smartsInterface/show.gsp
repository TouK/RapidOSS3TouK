

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Show SmartsInterface</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLinkTo(dir:'')}">Home</a></span>
    <span class="menuButton"><g:link class="list" action="list">SmartsInterface List</g:link></span>
    <span class="menuButton"><g:link class="create" action="create">New SmartsInterface</g:link></span>
</div>
<div class="body">
    <h1>Show SmartsInterface</h1>
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
                    
                    <td valign="top" class="value">${smartsInterface.id}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">name:</td>
                    
                    <td valign="top" class="value">${smartsInterface.name}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">adminStatus:</td>
                    
                    <td valign="top" class="value">${smartsInterface.adminStatus}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">cardName:</td>
                    
                    <td valign="top" class="value">${smartsInterface.cardName}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">className:</td>
                    
                    <td valign="top" class="value">${smartsInterface.className}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">computerSystemName:</td>
                    
                    <td valign="top" class="value">${smartsInterface.computerSystemName}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">connectedVia:</td>
                    
                    <td valign="top" class="value"><g:link controller="smartsLink" action="show" id="${smartsInterface?.connectedVia?.id}">${smartsInterface?.connectedVia}</g:link></td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">description:</td>
                    
                    <td valign="top" class="value">${smartsInterface.description}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">deviceID:</td>
                    
                    <td valign="top" class="value">${smartsInterface.deviceID}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">displayClassName:</td>
                    
                    <td valign="top" class="value">${smartsInterface.displayClassName}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">displayName:</td>
                    
                    <td valign="top" class="value">${smartsInterface.displayName}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">duplexMode:</td>
                    
                    <td valign="top" class="value">${smartsInterface.duplexMode}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">duplexSource:</td>
                    
                    <td valign="top" class="value">${smartsInterface.duplexSource}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">hasIPAddresses:</td>
                    
                    <td valign="top" class="value">${smartsInterface.hasIPAddresses}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">hasIPv6Addresses:</td>
                    
                    <td valign="top" class="value">${smartsInterface.hasIPv6Addresses}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">interfaceAlias:</td>
                    
                    <td valign="top" class="value">${smartsInterface.interfaceAlias}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">interfaceCode:</td>
                    
                    <td valign="top" class="value">${smartsInterface.interfaceCode}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">interfaceKey:</td>
                    
                    <td valign="top" class="value">${smartsInterface.interfaceKey}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">interfaceNumber:</td>
                    
                    <td valign="top" class="value">${smartsInterface.interfaceNumber}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">isConnector:</td>
                    
                    <td valign="top" class="value">${smartsInterface.isConnector}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">isFlapping:</td>
                    
                    <td valign="top" class="value">${smartsInterface.isFlapping}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">isManaged:</td>
                    
                    <td valign="top" class="value">${smartsInterface.isManaged}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">isNetworkAdapterNotOperating:</td>
                    
                    <td valign="top" class="value">${smartsInterface.isNetworkAdapterNotOperating}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">lastChangedAt:</td>
                    
                    <td valign="top" class="value">${smartsInterface.lastChangedAt}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">layeredOver:</td>
                    
                    <td valign="top" style="text-align:left;" class="value">
                        <ul>
                            <g:each var="l" in="${smartsInterface.layeredOver}">
                                <li><g:link controller="smartsComputerSystemComponent" action="show" id="${l.id}">${l}</g:link></li>
                            </g:each>
                        </ul>
                    </td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">maxSpeed:</td>
                    
                    <td valign="top" class="value">${smartsInterface.maxSpeed}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">maxTransferUnit:</td>
                    
                    <td valign="top" class="value">${smartsInterface.maxTransferUnit}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">maximumUptime:</td>
                    
                    <td valign="top" class="value">${smartsInterface.maximumUptime}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">memberOfGroup:</td>
                    
                    <td valign="top" style="text-align:left;" class="value">
                        <ul>
                            <g:each var="m" in="${smartsInterface.memberOfGroup}">
                                <li><g:link controller="rsGroup" action="show" id="${m.id}">${m}</g:link></li>
                            </g:each>
                        </ul>
                    </td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">mib2IfType:</td>
                    
                    <td valign="top" class="value">${smartsInterface.mib2IfType}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">mode:</td>
                    
                    <td valign="top" class="value">${smartsInterface.mode}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">operStatus:</td>
                    
                    <td valign="top" class="value">${smartsInterface.operStatus}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">partOf:</td>
                    
                    <td valign="top" class="value"><g:link controller="smartsComputerSystem" action="show" id="${smartsInterface?.partOf?.id}">${smartsInterface?.partOf}</g:link></td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">peerSystemName:</td>
                    
                    <td valign="top" class="value">${smartsInterface.peerSystemName}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">peerSystemType:</td>
                    
                    <td valign="top" class="value">${smartsInterface.peerSystemType}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">realizedBy:</td>
                    
                    <td valign="top" class="value"><g:link controller="smartsCard" action="show" id="${smartsInterface?.realizedBy?.id}">${smartsInterface?.realizedBy}</g:link></td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">rsDatasource:</td>
                    
                    <td valign="top" class="value">${smartsInterface.rsDatasource}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">status:</td>
                    
                    <td valign="top" class="value">${smartsInterface.status}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">systemModel:</td>
                    
                    <td valign="top" class="value">${smartsInterface.systemModel}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">systemName:</td>
                    
                    <td valign="top" class="value">${smartsInterface.systemName}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">systemObjectID:</td>
                    
                    <td valign="top" class="value">${smartsInterface.systemObjectID}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">systemType:</td>
                    
                    <td valign="top" class="value">${smartsInterface.systemType}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">systemVendor:</td>
                    
                    <td valign="top" class="value">${smartsInterface.systemVendor}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">tag:</td>
                    
                    <td valign="top" class="value">${smartsInterface.tag}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">type:</td>
                    
                    <td valign="top" class="value">${smartsInterface.type}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">underlying:</td>
                    
                    <td valign="top" style="text-align:left;" class="value">
                        <ul>
                            <g:each var="u" in="${smartsInterface.underlying}">
                                <li><g:link controller="smartsComputerSystemComponent" action="show" id="${u.id}">${u}</g:link></li>
                            </g:each>
                        </ul>
                    </td>
                    
                </tr>
                
            </tbody>
        </table>
    </div>
    <div class="buttons">
        <g:form>
            <input type="hidden" name="id" value="${smartsInterface?.id}"/>
            <span class="button"><g:actionSubmit class="edit" value="Edit"/></span>
            <span class="button"><g:actionSubmit class="delete" onclick="return confirm('Are you sure?');" value="Delete"/></span>
        </g:form>
    </div>
</div>
</body>
</html>
