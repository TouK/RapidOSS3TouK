

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Show RsComputerSystemComponent</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLinkTo(dir:'')}">Home</a></span>
    <span class="menuButton"><g:link class="list" action="list">RsComputerSystemComponent List</g:link></span>
    <span class="menuButton"><g:link class="create" action="create">New RsComputerSystemComponent</g:link></span>
</div>
<div class="body">
    <h1>Show RsComputerSystemComponent</h1>
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
                    
                    <td valign="top" class="value">${rsComputerSystemComponent.id}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">name:</td>
                    
                    <td valign="top" class="value">${rsComputerSystemComponent.name}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">computerSystemName:</td>
                    
                    <td valign="top" class="value">${rsComputerSystemComponent.computerSystemName}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">creationClassName:</td>
                    
                    <td valign="top" class="value">${rsComputerSystemComponent.creationClassName}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">description:</td>
                    
                    <td valign="top" class="value">${rsComputerSystemComponent.description}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">displayName:</td>
                    
                    <td valign="top" class="value">${rsComputerSystemComponent.displayName}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">isManaged:</td>
                    
                    <td valign="top" class="value">${rsComputerSystemComponent.isManaged}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">layeredOver:</td>
                    
                    <td valign="top" style="text-align:left;" class="value">
                        <ul>
                            <g:each var="l" in="${rsComputerSystemComponent.layeredOver}">
                                <li><g:link controller="rsComputerSystemComponent" action="show" id="${l.id}">${l}</g:link></li>
                            </g:each>
                        </ul>
                    </td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">memberOfGroup:</td>
                    
                    <td valign="top" style="text-align:left;" class="value">
                        <ul>
                            <g:each var="m" in="${rsComputerSystemComponent.memberOfGroup}">
                                <li><g:link controller="rsGroup" action="show" id="${m.id}">${m}</g:link></li>
                            </g:each>
                        </ul>
                    </td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">partOf:</td>
                    
                    <td valign="top" class="value"><g:link controller="rsComputerSystem" action="show" id="${rsComputerSystemComponent?.partOf?.id}">${rsComputerSystemComponent?.partOf}</g:link></td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">rsDatasource:</td>
                    
                    <td valign="top" class="value">${rsComputerSystemComponent.rsDatasource}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">tag:</td>
                    
                    <td valign="top" class="value">${rsComputerSystemComponent.tag}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">underlying:</td>
                    
                    <td valign="top" style="text-align:left;" class="value">
                        <ul>
                            <g:each var="u" in="${rsComputerSystemComponent.underlying}">
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
            <input type="hidden" name="id" value="${rsComputerSystemComponent?.id}"/>
            <span class="button"><g:actionSubmit class="edit" value="Edit"/></span>
            <span class="button"><g:actionSubmit class="delete" onclick="return confirm('Are you sure?');" value="Delete"/></span>
        </g:form>
    </div>
</div>
</body>
</html>
