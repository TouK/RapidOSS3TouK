

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Show RsIp</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLinkTo(dir:'')}">Home</a></span>
    <span class="menuButton"><g:link class="list" action="list">RsIp List</g:link></span>
    <span class="menuButton"><g:link class="create" action="create">New RsIp</g:link></span>
</div>
<div class="body">
    <h1>Show RsIp</h1>
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
                    
                    <td valign="top" class="value">${rsIp.id}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">name:</td>
                    
                    <td valign="top" class="value">${rsIp.name}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">address:</td>
                    
                    <td valign="top" class="value">${rsIp.address}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">computerSystemName:</td>
                    
                    <td valign="top" class="value">${rsIp.computerSystemName}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">creationClassName:</td>
                    
                    <td valign="top" class="value">${rsIp.creationClassName}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">description:</td>
                    
                    <td valign="top" class="value">${rsIp.description}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">displayName:</td>
                    
                    <td valign="top" class="value">${rsIp.displayName}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">hostedBy:</td>
                    
                    <td valign="top" class="value"><g:link controller="rsComputerSystem" action="show" id="${rsIp?.hostedBy?.id}">${rsIp?.hostedBy}</g:link></td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">interfaceAdminStatus:</td>
                    
                    <td valign="top" class="value">${rsIp.interfaceAdminStatus}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">interfaceMode:</td>
                    
                    <td valign="top" class="value">${rsIp.interfaceMode}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">interfaceName:</td>
                    
                    <td valign="top" class="value">${rsIp.interfaceName}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">interfaceOperStatus:</td>
                    
                    <td valign="top" class="value">${rsIp.interfaceOperStatus}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">interfaceType:</td>
                    
                    <td valign="top" class="value">${rsIp.interfaceType}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">ipStatus:</td>
                    
                    <td valign="top" class="value">${rsIp.ipStatus}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">isManaged:</td>
                    
                    <td valign="top" class="value">${rsIp.isManaged}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">layeredOver:</td>
                    
                    <td valign="top" style="text-align:left;" class="value">
                        <ul>
                            <g:each var="l" in="${rsIp.layeredOver}">
                                <li><g:link controller="rsComputerSystemComponent" action="show" id="${l.id}">${l}</g:link></li>
                            </g:each>
                        </ul>
                    </td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">memberOfGroup:</td>
                    
                    <td valign="top" style="text-align:left;" class="value">
                        <ul>
                            <g:each var="m" in="${rsIp.memberOfGroup}">
                                <li><g:link controller="rsGroup" action="show" id="${m.id}">${m}</g:link></li>
                            </g:each>
                        </ul>
                    </td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">netmask:</td>
                    
                    <td valign="top" class="value">${rsIp.netmask}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">networkNumber:</td>
                    
                    <td valign="top" class="value">${rsIp.networkNumber}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">partOf:</td>
                    
                    <td valign="top" class="value"><g:link controller="rsComputerSystem" action="show" id="${rsIp?.partOf?.id}">${rsIp?.partOf}</g:link></td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">responsive:</td>
                    
                    <td valign="top" class="value">${rsIp.responsive}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">rsDatasource:</td>
                    
                    <td valign="top" class="value">${rsIp.rsDatasource}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">status:</td>
                    
                    <td valign="top" class="value">${rsIp.status}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">tag:</td>
                    
                    <td valign="top" class="value">${rsIp.tag}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">underlying:</td>
                    
                    <td valign="top" style="text-align:left;" class="value">
                        <ul>
                            <g:each var="u" in="${rsIp.underlying}">
                                <li><g:link controller="rsComputerSystemComponent" action="show" id="${u.id}">${u}</g:link></li>
                            </g:each>
                        </ul>
                    </td>
                    
                </tr>
                
            </tbody>
        </table>
    </div>
    <div class="buttons">
        <g:form>
            <input type="hidden" name="id" value="${rsIp?.id}"/>
            <span class="button"><g:actionSubmit class="edit" value="Edit"/></span>
            <span class="button"><g:actionSubmit class="delete" onclick="return confirm('Are you sure?');" value="Delete"/></span>
        </g:form>
    </div>
</div>
</body>
</html>
