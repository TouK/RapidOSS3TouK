

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Show SmartsVlan</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLinkTo(dir:'')}">Home</a></span>
    <span class="menuButton"><g:link class="list" action="list">SmartsVlan List</g:link></span>
    <span class="menuButton"><g:link class="create" action="create">New SmartsVlan</g:link></span>
</div>
<div class="body">
    <h1>Show SmartsVlan</h1>
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
                    
                    <td valign="top" class="value">${smartsVlan.id}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">name:</td>
                    
                    <td valign="top" class="value">${smartsVlan.name}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">className:</td>
                    
                    <td valign="top" class="value">${smartsVlan.className}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">connectedPorts:</td>
                    
                    <td valign="top" style="text-align:left;" class="value">
                        <ul>
                            <g:each var="c" in="${smartsVlan.connectedPorts}">
                                <li><g:link controller="smartsPort" action="show" id="${c.id}">${c}</g:link></li>
                            </g:each>
                        </ul>
                    </td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">connectedSystems:</td>
                    
                    <td valign="top" style="text-align:left;" class="value">
                        <ul>
                            <g:each var="c" in="${smartsVlan.connectedSystems}">
                                <li><g:link controller="smartsComputerSystem" action="show" id="${c.id}">${c}</g:link></li>
                            </g:each>
                        </ul>
                    </td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">description:</td>
                    
                    <td valign="top" class="value">${smartsVlan.description}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">displayName:</td>
                    
                    <td valign="top" class="value">${smartsVlan.displayName}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">isManaged:</td>
                    
                    <td valign="top" class="value">${smartsVlan.isManaged}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">layeredOver:</td>
                    
                    <td valign="top" style="text-align:left;" class="value">
                        <ul>
                            <g:each var="l" in="${smartsVlan.layeredOver}">
                                <li><g:link controller="smartsComputerSystem" action="show" id="${l.id}">${l}</g:link></li>
                            </g:each>
                        </ul>
                    </td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">memberOfGroup:</td>
                    
                    <td valign="top" style="text-align:left;" class="value">
                        <ul>
                            <g:each var="m" in="${smartsVlan.memberOfGroup}">
                                <li><g:link controller="rsGroup" action="show" id="${m.id}">${m}</g:link></li>
                            </g:each>
                        </ul>
                    </td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">memberSystems:</td>
                    
                    <td valign="top" style="text-align:left;" class="value">
                        <ul>
                            <g:each var="m" in="${smartsVlan.memberSystems}">
                                <li><g:link controller="smartsComputerSystem" action="show" id="${m.id}">${m}</g:link></li>
                            </g:each>
                        </ul>
                    </td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">rsDatasource:</td>
                    
                    <td valign="top" class="value">${smartsVlan.rsDatasource}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">trunkCables:</td>
                    
                    <td valign="top" style="text-align:left;" class="value">
                        <ul>
                            <g:each var="t" in="${smartsVlan.trunkCables}">
                                <li><g:link controller="smartsLink" action="show" id="${t.id}">${t}</g:link></li>
                            </g:each>
                        </ul>
                    </td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">vlanKey:</td>
                    
                    <td valign="top" class="value">${smartsVlan.vlanKey}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">vlanNumber:</td>
                    
                    <td valign="top" class="value">${smartsVlan.vlanNumber}</td>
                    
                </tr>
                
            </tbody>
        </table>
    </div>
    <div class="buttons">
        <g:form>
            <input type="hidden" name="id" value="${smartsVlan?.id}"/>
            <span class="button"><g:actionSubmit class="edit" value="Edit"/></span>
            <span class="button"><g:actionSubmit class="delete" onclick="return confirm('Are you sure?');" value="Delete"/></span>
        </g:form>
    </div>
</div>
</body>
</html>
