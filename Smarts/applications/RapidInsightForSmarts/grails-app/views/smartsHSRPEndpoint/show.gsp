

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Show SmartsHSRPEndpoint</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLinkTo(dir:'')}">Home</a></span>
    <span class="menuButton"><g:link class="list" action="list">SmartsHSRPEndpoint List</g:link></span>
    <span class="menuButton"><g:link class="create" action="create">New SmartsHSRPEndpoint</g:link></span>
</div>
<div class="body">
    <h1>Show SmartsHSRPEndpoint</h1>
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
                    
                    <td valign="top" class="value">${smartsHSRPEndpoint.id}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">name:</td>
                    
                    <td valign="top" class="value">${smartsHSRPEndpoint.name}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">className:</td>
                    
                    <td valign="top" class="value">${smartsHSRPEndpoint.className}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">computerSystemName:</td>
                    
                    <td valign="top" class="value">${smartsHSRPEndpoint.computerSystemName}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">description:</td>
                    
                    <td valign="top" class="value">${smartsHSRPEndpoint.description}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">displayName:</td>
                    
                    <td valign="top" class="value">${smartsHSRPEndpoint.displayName}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">groupNumber:</td>
                    
                    <td valign="top" class="value">${smartsHSRPEndpoint.groupNumber}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">hsrpEndpointKey:</td>
                    
                    <td valign="top" class="value">${smartsHSRPEndpoint.hsrpEndpointKey}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">hsrpGroup:</td>
                    
                    <td valign="top" class="value"><g:link controller="smartsHSRPGroup" action="show" id="${smartsHSRPEndpoint?.hsrpGroup?.id}">${smartsHSRPEndpoint?.hsrpGroup}</g:link></td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">isManaged:</td>
                    
                    <td valign="top" class="value">${smartsHSRPEndpoint.isManaged}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">isReady:</td>
                    
                    <td valign="top" class="value">${smartsHSRPEndpoint.isReady}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">isSwitchOverActive:</td>
                    
                    <td valign="top" class="value">${smartsHSRPEndpoint.isSwitchOverActive}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">layeredOver:</td>
                    
                    <td valign="top" style="text-align:left;" class="value">
                        <ul>
                            <g:each var="l" in="${smartsHSRPEndpoint.layeredOver}">
                                <li><g:link controller="smartsComputerSystemComponent" action="show" id="${l.id}">${l}</g:link></li>
                            </g:each>
                        </ul>
                    </td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">memberOfGroup:</td>
                    
                    <td valign="top" style="text-align:left;" class="value">
                        <ul>
                            <g:each var="m" in="${smartsHSRPEndpoint.memberOfGroup}">
                                <li><g:link controller="rsGroup" action="show" id="${m.id}">${m}</g:link></li>
                            </g:each>
                        </ul>
                    </td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">numberOfComponents:</td>
                    
                    <td valign="top" class="value">${smartsHSRPEndpoint.numberOfComponents}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">numberOfFaultyComponents:</td>
                    
                    <td valign="top" class="value">${smartsHSRPEndpoint.numberOfFaultyComponents}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">partOf:</td>
                    
                    <td valign="top" class="value"><g:link controller="smartsComputerSystem" action="show" id="${smartsHSRPEndpoint?.partOf?.id}">${smartsHSRPEndpoint?.partOf}</g:link></td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">rsDatasource:</td>
                    
                    <td valign="top" class="value">${smartsHSRPEndpoint.rsDatasource}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">tag:</td>
                    
                    <td valign="top" class="value">${smartsHSRPEndpoint.tag}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">underlying:</td>
                    
                    <td valign="top" style="text-align:left;" class="value">
                        <ul>
                            <g:each var="u" in="${smartsHSRPEndpoint.underlying}">
                                <li><g:link controller="smartsComputerSystemComponent" action="show" id="${u.id}">${u}</g:link></li>
                            </g:each>
                        </ul>
                    </td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">virtualIP:</td>
                    
                    <td valign="top" class="value">${smartsHSRPEndpoint.virtualIP}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">virtualMAC:</td>
                    
                    <td valign="top" class="value">${smartsHSRPEndpoint.virtualMAC}</td>
                    
                </tr>
                
            </tbody>
        </table>
    </div>
    <div class="buttons">
        <g:form>
            <input type="hidden" name="id" value="${smartsHSRPEndpoint?.id}"/>
            <span class="button"><g:actionSubmit class="edit" value="Edit"/></span>
            <span class="button"><g:actionSubmit class="delete" onclick="return confirm('Are you sure?');" value="Delete"/></span>
        </g:form>
    </div>
</div>
</body>
</html>
