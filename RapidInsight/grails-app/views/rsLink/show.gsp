

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Show RsLink</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLinkTo(dir:'')}">Home</a></span>
    <span class="menuButton"><g:link class="list" action="list">RsLink List</g:link></span>
    <span class="menuButton"><g:link class="create" action="create">New RsLink</g:link></span>
</div>
<div class="body">
    <h1>Show RsLink</h1>
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
                    
                    <td valign="top" class="value">${rsLink.id}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">name:</td>
                    
                    <td valign="top" class="value">${rsLink.name}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">a_AdminStatus:</td>
                    
                    <td valign="top" class="value">${rsLink.a_AdminStatus}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">a_ComputerSystemName:</td>
                    
                    <td valign="top" class="value">${rsLink.a_ComputerSystemName}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">a_DisplayName:</td>
                    
                    <td valign="top" class="value">${rsLink.a_DisplayName}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">a_DuplexMode:</td>
                    
                    <td valign="top" class="value">${rsLink.a_DuplexMode}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">a_IsFlapping:</td>
                    
                    <td valign="top" class="value">${rsLink.a_IsFlapping}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">a_MaxSpeed:</td>
                    
                    <td valign="top" class="value">${rsLink.a_MaxSpeed}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">a_Mode:</td>
                    
                    <td valign="top" class="value">${rsLink.a_Mode}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">a_Name:</td>
                    
                    <td valign="top" class="value">${rsLink.a_Name}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">a_OperStatus:</td>
                    
                    <td valign="top" class="value">${rsLink.a_OperStatus}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">connectedSystem:</td>
                    
                    <td valign="top" style="text-align:left;" class="value">
                        <ul>
                            <g:each var="c" in="${rsLink.connectedSystem}">
                                <li><g:link controller="rsComputerSystem" action="show" id="${c.id}">${c}</g:link></li>
                            </g:each>
                        </ul>
                    </td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">connectedSystemsUnresponsive:</td>
                    
                    <td valign="top" class="value">${rsLink.connectedSystemsUnresponsive}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">connectedTo:</td>
                    
                    <td valign="top" style="text-align:left;" class="value">
                        <ul>
                            <g:each var="c" in="${rsLink.connectedTo}">
                                <li><g:link controller="rsNetworkAdapter" action="show" id="${c.id}">${c}</g:link></li>
                            </g:each>
                        </ul>
                    </td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">creationClassName:</td>
                    
                    <td valign="top" class="value">${rsLink.creationClassName}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">description:</td>
                    
                    <td valign="top" class="value">${rsLink.description}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">displayName:</td>
                    
                    <td valign="top" class="value">${rsLink.displayName}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">isManaged:</td>
                    
                    <td valign="top" class="value">${rsLink.isManaged}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">memberOfGroup:</td>
                    
                    <td valign="top" style="text-align:left;" class="value">
                        <ul>
                            <g:each var="m" in="${rsLink.memberOfGroup}">
                                <li><g:link controller="rsGroup" action="show" id="${m.id}">${m}</g:link></li>
                            </g:each>
                        </ul>
                    </td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">rsDatasource:</td>
                    
                    <td valign="top" class="value">${rsLink.rsDatasource}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">z_AdminStatus:</td>
                    
                    <td valign="top" class="value">${rsLink.z_AdminStatus}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">z_ComputerSystemName:</td>
                    
                    <td valign="top" class="value">${rsLink.z_ComputerSystemName}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">z_DisplayName:</td>
                    
                    <td valign="top" class="value">${rsLink.z_DisplayName}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">z_DuplexMode:</td>
                    
                    <td valign="top" class="value">${rsLink.z_DuplexMode}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">z_IsFlapping:</td>
                    
                    <td valign="top" class="value">${rsLink.z_IsFlapping}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">z_MaxSpeed:</td>
                    
                    <td valign="top" class="value">${rsLink.z_MaxSpeed}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">z_Mode:</td>
                    
                    <td valign="top" class="value">${rsLink.z_Mode}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">z_Name:</td>
                    
                    <td valign="top" class="value">${rsLink.z_Name}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">z_OperStatus:</td>
                    
                    <td valign="top" class="value">${rsLink.z_OperStatus}</td>
                    
                </tr>
                
            </tbody>
        </table>
    </div>
    <div class="buttons">
        <g:form>
            <input type="hidden" name="id" value="${rsLink?.id}"/>
            <span class="button"><g:actionSubmit class="edit" value="Edit"/></span>
            <span class="button"><g:actionSubmit class="delete" onclick="return confirm('Are you sure?');" value="Delete"/></span>
        </g:form>
    </div>
</div>
</body>
</html>
