

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Show SmartsHSRPGroup</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLinkTo(dir:'')}">Home</a></span>
    <span class="menuButton"><g:link class="list" action="list">SmartsHSRPGroup List</g:link></span>
    <span class="menuButton"><g:link class="create" action="create">New SmartsHSRPGroup</g:link></span>
</div>
<div class="body">
    <h1>Show SmartsHSRPGroup</h1>
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
                    
                    <td valign="top" class="value">${smartsHSRPGroup.id}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">name:</td>
                    
                    <td valign="top" class="value">${smartsHSRPGroup.name}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">activeInterfaceName:</td>
                    
                    <td valign="top" class="value">${smartsHSRPGroup.activeInterfaceName}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">activeSystemName:</td>
                    
                    <td valign="top" class="value">${smartsHSRPGroup.activeSystemName}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">atRiskThreshold:</td>
                    
                    <td valign="top" class="value">${smartsHSRPGroup.atRiskThreshold}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">className:</td>
                    
                    <td valign="top" class="value">${smartsHSRPGroup.className}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">computerSystems:</td>
                    
                    <td valign="top" style="text-align:left;" class="value">
                        <ul>
                            <g:each var="c" in="${smartsHSRPGroup.computerSystems}">
                                <li><g:link controller="smartsComputerSystem" action="show" id="${c.id}">${c}</g:link></li>
                            </g:each>
                        </ul>
                    </td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">description:</td>
                    
                    <td valign="top" class="value">${smartsHSRPGroup.description}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">displayName:</td>
                    
                    <td valign="top" class="value">${smartsHSRPGroup.displayName}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">endPoints:</td>
                    
                    <td valign="top" style="text-align:left;" class="value">
                        <ul>
                            <g:each var="e" in="${smartsHSRPGroup.endPoints}">
                                <li><g:link controller="smartsHSRPEndpoint" action="show" id="${e.id}">${e}</g:link></li>
                            </g:each>
                        </ul>
                    </td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">groupNumber:</td>
                    
                    <td valign="top" class="value">${smartsHSRPGroup.groupNumber}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">hsrpEpStateChanged:</td>
                    
                    <td valign="top" class="value">${smartsHSRPGroup.hsrpEpStateChanged}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">isAnyComponentDown:</td>
                    
                    <td valign="top" class="value">${smartsHSRPGroup.isAnyComponentDown}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">isAnyHSRPEndpointActive:</td>
                    
                    <td valign="top" class="value">${smartsHSRPGroup.isAnyHSRPEndpointActive}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">isEveryComponentDown:</td>
                    
                    <td valign="top" class="value">${smartsHSRPGroup.isEveryComponentDown}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">isEveryHSRPEndpointReady:</td>
                    
                    <td valign="top" class="value">${smartsHSRPGroup.isEveryHSRPEndpointReady}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">isGroupPartOfSingleUnresponsiveSystem:</td>
                    
                    <td valign="top" class="value">${smartsHSRPGroup.isGroupPartOfSingleUnresponsiveSystem}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">isManaged:</td>
                    
                    <td valign="top" class="value">${smartsHSRPGroup.isManaged}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">isVirtualIPUnresponsive:</td>
                    
                    <td valign="top" class="value">${smartsHSRPGroup.isVirtualIPUnresponsive}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">memberOfGroup:</td>
                    
                    <td valign="top" style="text-align:left;" class="value">
                        <ul>
                            <g:each var="m" in="${smartsHSRPGroup.memberOfGroup}">
                                <li><g:link controller="rsGroup" action="show" id="${m.id}">${m}</g:link></li>
                            </g:each>
                        </ul>
                    </td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">numberOfComponents:</td>
                    
                    <td valign="top" class="value">${smartsHSRPGroup.numberOfComponents}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">numberOfFaultyComponents:</td>
                    
                    <td valign="top" class="value">${smartsHSRPGroup.numberOfFaultyComponents}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">rsDatasource:</td>
                    
                    <td valign="top" class="value">${smartsHSRPGroup.rsDatasource}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">virtualIP:</td>
                    
                    <td valign="top" class="value">${smartsHSRPGroup.virtualIP}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">virtualMAC:</td>
                    
                    <td valign="top" class="value">${smartsHSRPGroup.virtualMAC}</td>
                    
                </tr>
                
            </tbody>
        </table>
    </div>
    <div class="buttons">
        <g:form>
            <input type="hidden" name="id" value="${smartsHSRPGroup?.id}"/>
            <span class="button"><g:actionSubmit class="edit" value="Edit"/></span>
            <span class="button"><g:actionSubmit class="delete" onclick="return confirm('Are you sure?');" value="Delete"/></span>
        </g:form>
    </div>
</div>
</body>
</html>
