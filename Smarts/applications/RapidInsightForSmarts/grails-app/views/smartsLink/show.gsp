

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Show SmartsLink</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLinkTo(dir:'')}">Home</a></span>
    <span class="menuButton"><g:link class="list" action="list">SmartsLink List</g:link></span>
    <span class="menuButton"><g:link class="create" action="create">New SmartsLink</g:link></span>
</div>
<div class="body">
    <h1>Show SmartsLink</h1>
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
                    
                    <td valign="top" class="value">${smartsLink.id}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">name:</td>
                    
                    <td valign="top" class="value">${smartsLink.name}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">a_AdminStatus:</td>
                    
                    <td valign="top" class="value">${smartsLink.a_AdminStatus}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">a_ComputerSystemName:</td>
                    
                    <td valign="top" class="value">${smartsLink.a_ComputerSystemName}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">a_DisplayName:</td>
                    
                    <td valign="top" class="value">${smartsLink.a_DisplayName}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">a_DuplexMode:</td>
                    
                    <td valign="top" class="value">${smartsLink.a_DuplexMode}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">a_IsFlapping:</td>
                    
                    <td valign="top" class="value">${smartsLink.a_IsFlapping}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">a_MaxSpeed:</td>
                    
                    <td valign="top" class="value">${smartsLink.a_MaxSpeed}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">a_Mode:</td>
                    
                    <td valign="top" class="value">${smartsLink.a_Mode}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">a_Name:</td>
                    
                    <td valign="top" class="value">${smartsLink.a_Name}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">a_OperStatus:</td>
                    
                    <td valign="top" class="value">${smartsLink.a_OperStatus}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">className:</td>
                    
                    <td valign="top" class="value">${smartsLink.className}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">connectedSystem:</td>
                    
                    <td valign="top" style="text-align:left;" class="value">
                        <ul>
                            <g:each var="c" in="${smartsLink.connectedSystem}">
                                <li><g:link controller="rsComputerSystem" action="show" id="${c.id}">${c}</g:link></li>
                            </g:each>
                        </ul>
                    </td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">connectedSystemsUnresponsive:</td>
                    
                    <td valign="top" class="value">${smartsLink.connectedSystemsUnresponsive}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">connectedTo:</td>
                    
                    <td valign="top" style="text-align:left;" class="value">
                        <ul>
                            <g:each var="c" in="${smartsLink.connectedTo}">
                                <li><g:link controller="smartsNetworkAdapter" action="show" id="${c.id}">${c}</g:link></li>
                            </g:each>
                        </ul>
                    </td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">description:</td>
                    
                    <td valign="top" class="value">${smartsLink.description}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">displayName:</td>
                    
                    <td valign="top" class="value">${smartsLink.displayName}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">isManaged:</td>
                    
                    <td valign="top" class="value">${smartsLink.isManaged}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">memberOfGroup:</td>
                    
                    <td valign="top" style="text-align:left;" class="value">
                        <ul>
                            <g:each var="m" in="${smartsLink.memberOfGroup}">
                                <li><g:link controller="rsGroup" action="show" id="${m.id}">${m}</g:link></li>
                            </g:each>
                        </ul>
                    </td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">rsDatasource:</td>
                    
                    <td valign="top" class="value">${smartsLink.rsDatasource}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">vlans:</td>
                    
                    <td valign="top" style="text-align:left;" class="value">
                        <ul>
                            <g:each var="v" in="${smartsLink.vlans}">
                                <li><g:link controller="smartsVlan" action="show" id="${v.id}">${v}</g:link></li>
                            </g:each>
                        </ul>
                    </td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">z_AdminStatus:</td>
                    
                    <td valign="top" class="value">${smartsLink.z_AdminStatus}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">z_ComputerSystemName:</td>
                    
                    <td valign="top" class="value">${smartsLink.z_ComputerSystemName}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">z_DisplayName:</td>
                    
                    <td valign="top" class="value">${smartsLink.z_DisplayName}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">z_DuplexMode:</td>
                    
                    <td valign="top" class="value">${smartsLink.z_DuplexMode}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">z_IsFlapping:</td>
                    
                    <td valign="top" class="value">${smartsLink.z_IsFlapping}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">z_MaxSpeed:</td>
                    
                    <td valign="top" class="value">${smartsLink.z_MaxSpeed}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">z_Mode:</td>
                    
                    <td valign="top" class="value">${smartsLink.z_Mode}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">z_Name:</td>
                    
                    <td valign="top" class="value">${smartsLink.z_Name}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">z_OperStatus:</td>
                    
                    <td valign="top" class="value">${smartsLink.z_OperStatus}</td>
                    
                </tr>
                
            </tbody>
        </table>
    </div>
    <div class="buttons">
        <g:form>
            <input type="hidden" name="id" value="${smartsLink?.id}"/>
            <span class="button"><g:actionSubmit class="edit" value="Edit"/></span>
            <span class="button"><g:actionSubmit class="delete" onclick="return confirm('Are you sure?');" value="Delete"/></span>
        </g:form>
    </div>
</div>
</body>
</html>
