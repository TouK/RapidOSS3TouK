

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Show RsInterface</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLinkTo(dir:'')}">Home</a></span>
    <span class="menuButton"><g:link class="list" action="list">RsInterface List</g:link></span>
    <span class="menuButton"><g:link class="create" action="create">New RsInterface</g:link></span>
</div>
<div class="body">
    <h1>Show RsInterface</h1>
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
                    
                    <td valign="top" class="value">${rsInterface.id}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">name:</td>
                    
                    <td valign="top" class="value">${rsInterface.name}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">adminStatus:</td>
                    
                    <td valign="top" class="value">${rsInterface.adminStatus}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">cardName:</td>
                    
                    <td valign="top" class="value">${rsInterface.cardName}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">computerSystemName:</td>
                    
                    <td valign="top" class="value">${rsInterface.computerSystemName}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">creationClassName:</td>
                    
                    <td valign="top" class="value">${rsInterface.creationClassName}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">description:</td>
                    
                    <td valign="top" class="value">${rsInterface.description}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">deviceID:</td>
                    
                    <td valign="top" class="value">${rsInterface.deviceID}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">displayClassName:</td>
                    
                    <td valign="top" class="value">${rsInterface.displayClassName}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">displayName:</td>
                    
                    <td valign="top" class="value">${rsInterface.displayName}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">duplexMode:</td>
                    
                    <td valign="top" class="value">${rsInterface.duplexMode}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">duplexSource:</td>
                    
                    <td valign="top" class="value">${rsInterface.duplexSource}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">hasIPAddresses:</td>
                    
                    <td valign="top" class="value">${rsInterface.hasIPAddresses}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">hasIPv6Addresses:</td>
                    
                    <td valign="top" class="value">${rsInterface.hasIPv6Addresses}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">interfaceAlias:</td>
                    
                    <td valign="top" class="value">${rsInterface.interfaceAlias}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">interfaceCode:</td>
                    
                    <td valign="top" class="value">${rsInterface.interfaceCode}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">interfaceKey:</td>
                    
                    <td valign="top" class="value">${rsInterface.interfaceKey}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">interfaceNumber:</td>
                    
                    <td valign="top" class="value">${rsInterface.interfaceNumber}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">isConnector:</td>
                    
                    <td valign="top" class="value">${rsInterface.isConnector}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">isFlapping:</td>
                    
                    <td valign="top" class="value">${rsInterface.isFlapping}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">isManaged:</td>
                    
                    <td valign="top" class="value">${rsInterface.isManaged}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">isNetworkAdapterNotOperating:</td>
                    
                    <td valign="top" class="value">${rsInterface.isNetworkAdapterNotOperating}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">lastChangedAt:</td>
                    
                    <td valign="top" class="value">${rsInterface.lastChangedAt}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">layeredOver:</td>
                    
                    <td valign="top" class="value">${rsInterface.layeredOver}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">maxSpeed:</td>
                    
                    <td valign="top" class="value">${rsInterface.maxSpeed}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">maxTransferUnit:</td>
                    
                    <td valign="top" class="value">${rsInterface.maxTransferUnit}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">maximumUptime:</td>
                    
                    <td valign="top" class="value">${rsInterface.maximumUptime}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">memberOfGroup:</td>
                    
                    <td valign="top" class="value">${rsInterface.memberOfGroup}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">mib2IfType:</td>
                    
                    <td valign="top" class="value">${rsInterface.mib2IfType}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">mode:</td>
                    
                    <td valign="top" class="value">${rsInterface.mode}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">operStatus:</td>
                    
                    <td valign="top" class="value">${rsInterface.operStatus}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">peerSystemName:</td>
                    
                    <td valign="top" class="value">${rsInterface.peerSystemName}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">peerSystemType:</td>
                    
                    <td valign="top" class="value">${rsInterface.peerSystemType}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">rsDatasource:</td>
                    
                    <td valign="top" class="value">${rsInterface.rsDatasource}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">status:</td>
                    
                    <td valign="top" class="value">${rsInterface.status}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">systemModel:</td>
                    
                    <td valign="top" class="value">${rsInterface.systemModel}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">systemName:</td>
                    
                    <td valign="top" class="value">${rsInterface.systemName}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">systemObjectID:</td>
                    
                    <td valign="top" class="value">${rsInterface.systemObjectID}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">systemType:</td>
                    
                    <td valign="top" class="value">${rsInterface.systemType}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">systemVendor:</td>
                    
                    <td valign="top" class="value">${rsInterface.systemVendor}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">tag:</td>
                    
                    <td valign="top" class="value">${rsInterface.tag}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">type:</td>
                    
                    <td valign="top" class="value">${rsInterface.type}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">underlying:</td>
                    
                    <td valign="top" class="value">${rsInterface.underlying}</td>
                    
                </tr>
                
            </tbody>
        </table>
    </div>
    <div class="buttons">
        <g:form>
            <input type="hidden" name="id" value="${rsInterface?.id}"/>
            <span class="button"><g:actionSubmit class="edit" value="Edit"/></span>
            <span class="button"><g:actionSubmit class="delete" onclick="return confirm('Are you sure?');" value="Delete"/></span>
        </g:form>
    </div>
</div>
</body>
</html>
