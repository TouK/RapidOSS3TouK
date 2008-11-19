

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Show RsComputerSystem</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLinkTo(dir:'')}">Home</a></span>
    <span class="menuButton"><g:link class="list" action="list">RsComputerSystem List</g:link></span>
    <span class="menuButton"><g:link class="create" action="create">New RsComputerSystem</g:link></span>
</div>
<div class="body">
    <h1>Show RsComputerSystem</h1>
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
                    
                    <td valign="top" class="value">${rsComputerSystem.id}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">name:</td>
                    
                    <td valign="top" class="value">${rsComputerSystem.name}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">className:</td>
                    
                    <td valign="top" class="value">${rsComputerSystem.className}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">connectedVia:</td>
                    
                    <td valign="top" style="text-align:left;" class="value">
                        <ul>
                            <g:each var="c" in="${rsComputerSystem.connectedVia}">
                                <li><g:link controller="rsLink" action="show" id="${c.id}">${c}</g:link></li>
                            </g:each>
                        </ul>
                    </td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">description:</td>
                    
                    <td valign="top" class="value">${rsComputerSystem.description}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">displayName:</td>
                    
                    <td valign="top" class="value">${rsComputerSystem.displayName}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">geocodes:</td>
                    
                    <td valign="top" class="value">${rsComputerSystem.geocodes}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">isManaged:</td>
                    
                    <td valign="top" class="value">${rsComputerSystem.isManaged}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">location:</td>
                    
                    <td valign="top" class="value">${rsComputerSystem.location}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">memberOfGroup:</td>
                    
                    <td valign="top" style="text-align:left;" class="value">
                        <ul>
                            <g:each var="m" in="${rsComputerSystem.memberOfGroup}">
                                <li><g:link controller="rsGroup" action="show" id="${m.id}">${m}</g:link></li>
                            </g:each>
                        </ul>
                    </td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">model:</td>
                    
                    <td valign="top" class="value">${rsComputerSystem.model}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">osVersion:</td>
                    
                    <td valign="top" class="value">${rsComputerSystem.osVersion}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">primaryOwnerContact:</td>
                    
                    <td valign="top" class="value">${rsComputerSystem.primaryOwnerContact}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">primaryOwnerName:</td>
                    
                    <td valign="top" class="value">${rsComputerSystem.primaryOwnerName}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">readCommunity:</td>
                    
                    <td valign="top" class="value">${rsComputerSystem.readCommunity}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">rsDatasource:</td>
                    
                    <td valign="top" class="value">${rsComputerSystem.rsDatasource}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">snmpAddress:</td>
                    
                    <td valign="top" class="value">${rsComputerSystem.snmpAddress}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">systemName:</td>
                    
                    <td valign="top" class="value">${rsComputerSystem.systemName}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">systemObjectID:</td>
                    
                    <td valign="top" class="value">${rsComputerSystem.systemObjectID}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">vendor:</td>
                    
                    <td valign="top" class="value">${rsComputerSystem.vendor}</td>
                    
                </tr>
                
            </tbody>
        </table>
    </div>
    <div class="buttons">
        <g:form>
            <input type="hidden" name="id" value="${rsComputerSystem?.id}"/>
            <span class="button"><g:actionSubmit class="edit" value="Edit"/></span>
            <span class="button"><g:actionSubmit class="delete" onclick="return confirm('Are you sure?');" value="Delete"/></span>
        </g:form>
    </div>
</div>
</body>
</html>
