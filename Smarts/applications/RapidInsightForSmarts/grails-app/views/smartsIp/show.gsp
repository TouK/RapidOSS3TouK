

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Show SmartsIp</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLinkTo(dir:'')}">Home</a></span>
    <span class="menuButton"><g:link class="list" action="list">SmartsIp List</g:link></span>
    <span class="menuButton"><g:link class="create" action="create">New SmartsIp</g:link></span>
</div>
<div class="body">
    <h1>Show SmartsIp</h1>
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
                    
                    <td valign="top" class="value">${smartsIp.id}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">name:</td>
                    
                    <td valign="top" class="value">${smartsIp.name}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">address:</td>
                    
                    <td valign="top" class="value">${smartsIp.address}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">className:</td>
                    
                    <td valign="top" class="value">${smartsIp.className}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">computerSystemName:</td>
                    
                    <td valign="top" class="value">${smartsIp.computerSystemName}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">description:</td>
                    
                    <td valign="top" class="value">${smartsIp.description}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">displayName:</td>
                    
                    <td valign="top" class="value">${smartsIp.displayName}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">hostedBy:</td>
                    
                    <td valign="top" class="value"><g:link controller="smartsComputerSystem" action="show" id="${smartsIp?.hostedBy?.id}">${smartsIp?.hostedBy}</g:link></td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">interfaceAdminStatus:</td>
                    
                    <td valign="top" class="value">${smartsIp.interfaceAdminStatus}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">interfaceMode:</td>
                    
                    <td valign="top" class="value">${smartsIp.interfaceMode}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">interfaceName:</td>
                    
                    <td valign="top" class="value">${smartsIp.interfaceName}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">interfaceOperStatus:</td>
                    
                    <td valign="top" class="value">${smartsIp.interfaceOperStatus}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">interfaceType:</td>
                    
                    <td valign="top" class="value">${smartsIp.interfaceType}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">ipStatus:</td>
                    
                    <td valign="top" class="value">${smartsIp.ipStatus}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">isManaged:</td>
                    
                    <td valign="top" class="value">${smartsIp.isManaged}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">layeredOver:</td>
                    
                    <td valign="top" style="text-align:left;" class="value">
                        <ul>
                            <g:each var="l" in="${smartsIp.layeredOver}">
                                <li><g:link controller="smartsComputerSystemComponent" action="show" id="${l.id}">${l}</g:link></li>
                            </g:each>
                        </ul>
                    </td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">memberOfGroup:</td>
                    
                    <td valign="top" style="text-align:left;" class="value">
                        <ul>
                            <g:each var="m" in="${smartsIp.memberOfGroup}">
                                <li><g:link controller="rsGroup" action="show" id="${m.id}">${m}</g:link></li>
                            </g:each>
                        </ul>
                    </td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">netmask:</td>
                    
                    <td valign="top" class="value">${smartsIp.netmask}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">networkNumber:</td>
                    
                    <td valign="top" class="value">${smartsIp.networkNumber}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">partOf:</td>
                    
                    <td valign="top" class="value"><g:link controller="smartsComputerSystem" action="show" id="${smartsIp?.partOf?.id}">${smartsIp?.partOf}</g:link></td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">responsive:</td>
                    
                    <td valign="top" class="value">${smartsIp.responsive}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">rsDatasource:</td>
                    
                    <td valign="top" class="value">${smartsIp.rsDatasource}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">status:</td>
                    
                    <td valign="top" class="value">${smartsIp.status}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">tag:</td>
                    
                    <td valign="top" class="value">${smartsIp.tag}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">underlying:</td>
                    
                    <td valign="top" style="text-align:left;" class="value">
                        <ul>
                            <g:each var="u" in="${smartsIp.underlying}">
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
            <input type="hidden" name="id" value="${smartsIp?.id}"/>
            <span class="button"><g:actionSubmit class="edit" value="Edit"/></span>
            <span class="button"><g:actionSubmit class="delete" onclick="return confirm('Are you sure?');" value="Delete"/></span>
        </g:form>
    </div>
</div>
</body>
</html>
