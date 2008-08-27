

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Show RsNetworkAdapter</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLinkTo(dir:'')}">Home</a></span>
    <span class="menuButton"><g:link class="list" action="list">RsNetworkAdapter List</g:link></span>
    <span class="menuButton"><g:link class="create" action="create">New RsNetworkAdapter</g:link></span>
</div>
<div class="body">
    <h1>Show RsNetworkAdapter</h1>
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
                    
                    <td valign="top" class="value">${rsNetworkAdapter.id}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">name:</td>
                    
                    <td valign="top" class="value">${rsNetworkAdapter.name}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">adminStatus:</td>
                    
                    <td valign="top" class="value">${rsNetworkAdapter.adminStatus}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">cardName:</td>
                    
                    <td valign="top" class="value">${rsNetworkAdapter.cardName}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">computerSystemName:</td>
                    
                    <td valign="top" class="value">${rsNetworkAdapter.computerSystemName}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">creationClassName:</td>
                    
                    <td valign="top" class="value">${rsNetworkAdapter.creationClassName}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">description:</td>
                    
                    <td valign="top" class="value">${rsNetworkAdapter.description}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">deviceID:</td>
                    
                    <td valign="top" class="value">${rsNetworkAdapter.deviceID}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">displayClassName:</td>
                    
                    <td valign="top" class="value">${rsNetworkAdapter.displayClassName}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">displayName:</td>
                    
                    <td valign="top" class="value">${rsNetworkAdapter.displayName}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">duplexMode:</td>
                    
                    <td valign="top" class="value">${rsNetworkAdapter.duplexMode}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">duplexSource:</td>
                    
                    <td valign="top" class="value">${rsNetworkAdapter.duplexSource}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">interfaceAlias:</td>
                    
                    <td valign="top" class="value">${rsNetworkAdapter.interfaceAlias}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">interfaceCode:</td>
                    
                    <td valign="top" class="value">${rsNetworkAdapter.interfaceCode}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">interfaceNumber:</td>
                    
                    <td valign="top" class="value">${rsNetworkAdapter.interfaceNumber}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">isConnector:</td>
                    
                    <td valign="top" class="value">${rsNetworkAdapter.isConnector}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">isFlapping:</td>
                    
                    <td valign="top" class="value">${rsNetworkAdapter.isFlapping}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">isManaged:</td>
                    
                    <td valign="top" class="value">${rsNetworkAdapter.isManaged}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">isNetworkAdapterNotOperating:</td>
                    
                    <td valign="top" class="value">${rsNetworkAdapter.isNetworkAdapterNotOperating}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">lastChangedAt:</td>
                    
                    <td valign="top" class="value">${rsNetworkAdapter.lastChangedAt}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">maxSpeed:</td>
                    
                    <td valign="top" class="value">${rsNetworkAdapter.maxSpeed}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">maxTransferUnit:</td>
                    
                    <td valign="top" class="value">${rsNetworkAdapter.maxTransferUnit}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">maximumUptime:</td>
                    
                    <td valign="top" class="value">${rsNetworkAdapter.maximumUptime}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">mib2IfType:</td>
                    
                    <td valign="top" class="value">${rsNetworkAdapter.mib2IfType}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">mode:</td>
                    
                    <td valign="top" class="value">${rsNetworkAdapter.mode}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">operStatus:</td>
                    
                    <td valign="top" class="value">${rsNetworkAdapter.operStatus}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">peerSystemName:</td>
                    
                    <td valign="top" class="value">${rsNetworkAdapter.peerSystemName}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">peerSystemType:</td>
                    
                    <td valign="top" class="value">${rsNetworkAdapter.peerSystemType}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">status:</td>
                    
                    <td valign="top" class="value">${rsNetworkAdapter.status}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">systemModel:</td>
                    
                    <td valign="top" class="value">${rsNetworkAdapter.systemModel}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">systemName:</td>
                    
                    <td valign="top" class="value">${rsNetworkAdapter.systemName}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">systemObjectID:</td>
                    
                    <td valign="top" class="value">${rsNetworkAdapter.systemObjectID}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">systemType:</td>
                    
                    <td valign="top" class="value">${rsNetworkAdapter.systemType}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">systemVendor:</td>
                    
                    <td valign="top" class="value">${rsNetworkAdapter.systemVendor}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">tag:</td>
                    
                    <td valign="top" class="value">${rsNetworkAdapter.tag}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">type:</td>
                    
                    <td valign="top" class="value">${rsNetworkAdapter.type}</td>
                    
                </tr>
                
            </tbody>
        </table>
    </div>
    <div class="buttons">
        <g:form>
            <input type="hidden" name="id" value="${rsNetworkAdapter?.id}"/>
            <span class="button"><g:actionSubmit class="edit" value="Edit"/></span>
            <span class="button"><g:actionSubmit class="delete" onclick="return confirm('Are you sure?');" value="Delete"/></span>
        </g:form>
    </div>
</div>
</body>
</html>
