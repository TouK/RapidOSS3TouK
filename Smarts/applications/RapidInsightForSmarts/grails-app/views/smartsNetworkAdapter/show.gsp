

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Show SmartsNetworkAdapter</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLinkTo(dir:'')}">Home</a></span>
    <span class="menuButton"><g:link class="list" action="list">SmartsNetworkAdapter List</g:link></span>
    <span class="menuButton"><g:link class="create" action="create">New SmartsNetworkAdapter</g:link></span>
</div>
<div class="body">
    <h1>Show SmartsNetworkAdapter</h1>
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
                    
                    <td valign="top" class="value">${smartsNetworkAdapter.id}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">name:</td>
                    
                    <td valign="top" class="value">${smartsNetworkAdapter.name}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">adminStatus:</td>
                    
                    <td valign="top" class="value">${smartsNetworkAdapter.adminStatus}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">cardName:</td>
                    
                    <td valign="top" class="value">${smartsNetworkAdapter.cardName}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">className:</td>
                    
                    <td valign="top" class="value">${smartsNetworkAdapter.className}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">computerSystemName:</td>
                    
                    <td valign="top" class="value">${smartsNetworkAdapter.computerSystemName}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">connectedVia:</td>
                    
                    <td valign="top" class="value"><g:link controller="smartsLink" action="show" id="${smartsNetworkAdapter?.connectedVia?.id}">${smartsNetworkAdapter?.connectedVia}</g:link></td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">description:</td>
                    
                    <td valign="top" class="value">${smartsNetworkAdapter.description}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">deviceID:</td>
                    
                    <td valign="top" class="value">${smartsNetworkAdapter.deviceID}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">displayClassName:</td>
                    
                    <td valign="top" class="value">${smartsNetworkAdapter.displayClassName}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">displayName:</td>
                    
                    <td valign="top" class="value">${smartsNetworkAdapter.displayName}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">duplexMode:</td>
                    
                    <td valign="top" class="value">${smartsNetworkAdapter.duplexMode}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">duplexSource:</td>
                    
                    <td valign="top" class="value">${smartsNetworkAdapter.duplexSource}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">interfaceAlias:</td>
                    
                    <td valign="top" class="value">${smartsNetworkAdapter.interfaceAlias}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">interfaceCode:</td>
                    
                    <td valign="top" class="value">${smartsNetworkAdapter.interfaceCode}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">interfaceNumber:</td>
                    
                    <td valign="top" class="value">${smartsNetworkAdapter.interfaceNumber}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">isConnector:</td>
                    
                    <td valign="top" class="value">${smartsNetworkAdapter.isConnector}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">isFlapping:</td>
                    
                    <td valign="top" class="value">${smartsNetworkAdapter.isFlapping}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">isManaged:</td>
                    
                    <td valign="top" class="value">${smartsNetworkAdapter.isManaged}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">isNetworkAdapterNotOperating:</td>
                    
                    <td valign="top" class="value">${smartsNetworkAdapter.isNetworkAdapterNotOperating}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">lastChangedAt:</td>
                    
                    <td valign="top" class="value">${smartsNetworkAdapter.lastChangedAt}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">layeredOver:</td>
                    
                    <td valign="top" style="text-align:left;" class="value">
                        <ul>
                            <g:each var="l" in="${smartsNetworkAdapter.layeredOver}">
                                <li><g:link controller="smartsComputerSystemComponent" action="show" id="${l.id}">${l}</g:link></li>
                            </g:each>
                        </ul>
                    </td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">maxSpeed:</td>
                    
                    <td valign="top" class="value">${smartsNetworkAdapter.maxSpeed}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">maxTransferUnit:</td>
                    
                    <td valign="top" class="value">${smartsNetworkAdapter.maxTransferUnit}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">maximumUptime:</td>
                    
                    <td valign="top" class="value">${smartsNetworkAdapter.maximumUptime}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">memberOfGroup:</td>
                    
                    <td valign="top" style="text-align:left;" class="value">
                        <ul>
                            <g:each var="m" in="${smartsNetworkAdapter.memberOfGroup}">
                                <li><g:link controller="rsGroup" action="show" id="${m.id}">${m}</g:link></li>
                            </g:each>
                        </ul>
                    </td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">mib2IfType:</td>
                    
                    <td valign="top" class="value">${smartsNetworkAdapter.mib2IfType}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">mode:</td>
                    
                    <td valign="top" class="value">${smartsNetworkAdapter.mode}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">operStatus:</td>
                    
                    <td valign="top" class="value">${smartsNetworkAdapter.operStatus}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">partOf:</td>
                    
                    <td valign="top" class="value"><g:link controller="smartsComputerSystem" action="show" id="${smartsNetworkAdapter?.partOf?.id}">${smartsNetworkAdapter?.partOf}</g:link></td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">peerSystemName:</td>
                    
                    <td valign="top" class="value">${smartsNetworkAdapter.peerSystemName}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">peerSystemType:</td>
                    
                    <td valign="top" class="value">${smartsNetworkAdapter.peerSystemType}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">realizedBy:</td>
                    
                    <td valign="top" class="value"><g:link controller="smartsCard" action="show" id="${smartsNetworkAdapter?.realizedBy?.id}">${smartsNetworkAdapter?.realizedBy}</g:link></td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">rsDatasource:</td>
                    
                    <td valign="top" class="value">${smartsNetworkAdapter.rsDatasource}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">status:</td>
                    
                    <td valign="top" class="value">${smartsNetworkAdapter.status}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">systemModel:</td>
                    
                    <td valign="top" class="value">${smartsNetworkAdapter.systemModel}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">systemName:</td>
                    
                    <td valign="top" class="value">${smartsNetworkAdapter.systemName}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">systemObjectID:</td>
                    
                    <td valign="top" class="value">${smartsNetworkAdapter.systemObjectID}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">systemType:</td>
                    
                    <td valign="top" class="value">${smartsNetworkAdapter.systemType}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">systemVendor:</td>
                    
                    <td valign="top" class="value">${smartsNetworkAdapter.systemVendor}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">tag:</td>
                    
                    <td valign="top" class="value">${smartsNetworkAdapter.tag}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">type:</td>
                    
                    <td valign="top" class="value">${smartsNetworkAdapter.type}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">underlying:</td>
                    
                    <td valign="top" style="text-align:left;" class="value">
                        <ul>
                            <g:each var="u" in="${smartsNetworkAdapter.underlying}">
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
            <input type="hidden" name="id" value="${smartsNetworkAdapter?.id}"/>
            <span class="button"><g:actionSubmit class="edit" value="Edit"/></span>
            <span class="button"><g:actionSubmit class="delete" onclick="return confirm('Are you sure?');" value="Delete"/></span>
        </g:form>
    </div>
</div>
</body>
</html>
