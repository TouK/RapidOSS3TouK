

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Show SmartsPort</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLinkTo(dir:'')}">Home</a></span>
    <span class="menuButton"><g:link class="list" action="list">SmartsPort List</g:link></span>
    <span class="menuButton"><g:link class="create" action="create">New SmartsPort</g:link></span>
</div>
<div class="body">
    <h1>Show SmartsPort</h1>
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
                    
                    <td valign="top" class="value">${smartsPort.id}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">name:</td>
                    
                    <td valign="top" class="value">${smartsPort.name}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">adminStatus:</td>
                    
                    <td valign="top" class="value">${smartsPort.adminStatus}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">cardName:</td>
                    
                    <td valign="top" class="value">${smartsPort.cardName}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">className:</td>
                    
                    <td valign="top" class="value">${smartsPort.className}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">computerSystemName:</td>
                    
                    <td valign="top" class="value">${smartsPort.computerSystemName}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">connectedVia:</td>
                    
                    <td valign="top" class="value"><g:link controller="smartsLink" action="show" id="${smartsPort?.connectedVia?.id}">${smartsPort?.connectedVia}</g:link></td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">description:</td>
                    
                    <td valign="top" class="value">${smartsPort.description}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">designatedBridge:</td>
                    
                    <td valign="top" class="value">${smartsPort.designatedBridge}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">designatedPort:</td>
                    
                    <td valign="top" class="value">${smartsPort.designatedPort}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">deviceID:</td>
                    
                    <td valign="top" class="value">${smartsPort.deviceID}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">displayClassName:</td>
                    
                    <td valign="top" class="value">${smartsPort.displayClassName}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">displayName:</td>
                    
                    <td valign="top" class="value">${smartsPort.displayName}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">duplexMode:</td>
                    
                    <td valign="top" class="value">${smartsPort.duplexMode}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">duplexSource:</td>
                    
                    <td valign="top" class="value">${smartsPort.duplexSource}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">interfaceAlias:</td>
                    
                    <td valign="top" class="value">${smartsPort.interfaceAlias}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">interfaceCode:</td>
                    
                    <td valign="top" class="value">${smartsPort.interfaceCode}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">interfaceNumber:</td>
                    
                    <td valign="top" class="value">${smartsPort.interfaceNumber}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">isConnectedToManagedSystem:</td>
                    
                    <td valign="top" class="value">${smartsPort.isConnectedToManagedSystem}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">isConnectedToSystem:</td>
                    
                    <td valign="top" class="value">${smartsPort.isConnectedToSystem}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">isConnector:</td>
                    
                    <td valign="top" class="value">${smartsPort.isConnector}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">isFlapping:</td>
                    
                    <td valign="top" class="value">${smartsPort.isFlapping}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">isManaged:</td>
                    
                    <td valign="top" class="value">${smartsPort.isManaged}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">isNetworkAdapterNotOperating:</td>
                    
                    <td valign="top" class="value">${smartsPort.isNetworkAdapterNotOperating}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">lastChangedAt:</td>
                    
                    <td valign="top" class="value">${smartsPort.lastChangedAt}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">layeredOver:</td>
                    
                    <td valign="top" style="text-align:left;" class="value">
                        <ul>
                            <g:each var="l" in="${smartsPort.layeredOver}">
                                <li><g:link controller="smartsComputerSystemComponent" action="show" id="${l.id}">${l}</g:link></li>
                            </g:each>
                        </ul>
                    </td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">managedState:</td>
                    
                    <td valign="top" class="value">${smartsPort.managedState}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">maxSpeed:</td>
                    
                    <td valign="top" class="value">${smartsPort.maxSpeed}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">maxTransferUnit:</td>
                    
                    <td valign="top" class="value">${smartsPort.maxTransferUnit}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">maximumUptime:</td>
                    
                    <td valign="top" class="value">${smartsPort.maximumUptime}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">memberOfGroup:</td>
                    
                    <td valign="top" style="text-align:left;" class="value">
                        <ul>
                            <g:each var="m" in="${smartsPort.memberOfGroup}">
                                <li><g:link controller="rsGroup" action="show" id="${m.id}">${m}</g:link></li>
                            </g:each>
                        </ul>
                    </td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">mib2IfType:</td>
                    
                    <td valign="top" class="value">${smartsPort.mib2IfType}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">mode:</td>
                    
                    <td valign="top" class="value">${smartsPort.mode}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">operStatus:</td>
                    
                    <td valign="top" class="value">${smartsPort.operStatus}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">partOf:</td>
                    
                    <td valign="top" class="value"><g:link controller="smartsComputerSystem" action="show" id="${smartsPort?.partOf?.id}">${smartsPort?.partOf}</g:link></td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">partOfVlan:</td>
                    
                    <td valign="top" style="text-align:left;" class="value">
                        <ul>
                            <g:each var="p" in="${smartsPort.partOfVlan}">
                                <li><g:link controller="smartsVlan" action="show" id="${p.id}">${p}</g:link></li>
                            </g:each>
                        </ul>
                    </td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">peerSystemName:</td>
                    
                    <td valign="top" class="value">${smartsPort.peerSystemName}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">peerSystemType:</td>
                    
                    <td valign="top" class="value">${smartsPort.peerSystemType}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">portKey:</td>
                    
                    <td valign="top" class="value">${smartsPort.portKey}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">portNumber:</td>
                    
                    <td valign="top" class="value">${smartsPort.portNumber}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">portType:</td>
                    
                    <td valign="top" class="value">${smartsPort.portType}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">realizedBy:</td>
                    
                    <td valign="top" class="value"><g:link controller="smartsCard" action="show" id="${smartsPort?.realizedBy?.id}">${smartsPort?.realizedBy}</g:link></td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">rsDatasource:</td>
                    
                    <td valign="top" class="value">${smartsPort.rsDatasource}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">status:</td>
                    
                    <td valign="top" class="value">${smartsPort.status}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">systemModel:</td>
                    
                    <td valign="top" class="value">${smartsPort.systemModel}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">systemName:</td>
                    
                    <td valign="top" class="value">${smartsPort.systemName}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">systemObjectID:</td>
                    
                    <td valign="top" class="value">${smartsPort.systemObjectID}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">systemType:</td>
                    
                    <td valign="top" class="value">${smartsPort.systemType}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">systemVendor:</td>
                    
                    <td valign="top" class="value">${smartsPort.systemVendor}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">tag:</td>
                    
                    <td valign="top" class="value">${smartsPort.tag}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">type:</td>
                    
                    <td valign="top" class="value">${smartsPort.type}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">underlying:</td>
                    
                    <td valign="top" style="text-align:left;" class="value">
                        <ul>
                            <g:each var="u" in="${smartsPort.underlying}">
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
            <input type="hidden" name="id" value="${smartsPort?.id}"/>
            <span class="button"><g:actionSubmit class="edit" value="Edit"/></span>
            <span class="button"><g:actionSubmit class="delete" onclick="return confirm('Are you sure?');" value="Delete"/></span>
        </g:form>
    </div>
</div>
</body>
</html>
