

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Show OpenNmsService</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLinkTo(dir:'')}">Home</a></span>
    <span class="menuButton"><g:link class="list" action="list">OpenNmsService List</g:link></span>
    <span class="menuButton"><g:link class="create" action="create">New OpenNmsService</g:link></span>
</div>
<div class="body">
    <h1>Show OpenNmsService</h1>
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
                    
                    <td valign="top" class="value">${openNmsService.id}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">name:</td>
                    
                    <td valign="top" class="value">${openNmsService.name}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">className:</td>
                    
                    <td valign="top" class="value">${openNmsService.className}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">description:</td>
                    
                    <td valign="top" class="value">${openNmsService.description}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">displayName:</td>
                    
                    <td valign="top" class="value">${openNmsService.displayName}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">graphs:</td>
                    
                    <td valign="top" style="text-align:left;" class="value">
                        <ul>
                            <g:each var="g" in="${openNmsService.graphs}">
                                <li><g:link controller="openNmsGraph" action="show" id="${g.id}">${g}</g:link></li>
                            </g:each>
                        </ul>
                    </td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">ipInterface:</td>
                    
                    <td valign="top" class="value"><g:link controller="openNmsIpInterface" action="show" id="${openNmsService?.ipInterface?.id}">${openNmsService?.ipInterface}</g:link></td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">isManaged:</td>
                    
                    <td valign="top" class="value">${openNmsService.isManaged}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">lastFailedAt:</td>
                    
                    <td valign="top" class="value">${openNmsService.lastFailedAt}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">lastGoodAt:</td>
                    
                    <td valign="top" class="value">${openNmsService.lastGoodAt}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">memberOfGroup:</td>
                    
                    <td valign="top" style="text-align:left;" class="value">
                        <ul>
                            <g:each var="m" in="${openNmsService.memberOfGroup}">
                                <li><g:link controller="rsGroup" action="show" id="${m.id}">${m}</g:link></li>
                            </g:each>
                        </ul>
                    </td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">notify:</td>
                    
                    <td valign="top" class="value">${openNmsService.notify}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">qualifier:</td>
                    
                    <td valign="top" class="value">${openNmsService.qualifier}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">rsDatasource:</td>
                    
                    <td valign="top" class="value">${openNmsService.rsDatasource}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">serviceName:</td>
                    
                    <td valign="top" class="value">${openNmsService.serviceName}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">source:</td>
                    
                    <td valign="top" class="value">${openNmsService.source}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">status:</td>
                    
                    <td valign="top" class="value">${openNmsService.status}</td>
                    
                </tr>
                
            </tbody>
        </table>
    </div>
    <div class="buttons">
        <g:form>
            <input type="hidden" name="id" value="${openNmsService?.id}"/>
            <span class="button"><g:actionSubmit class="edit" value="Edit"/></span>
            <span class="button"><g:actionSubmit class="delete" onclick="return confirm('Are you sure?');" value="Delete"/></span>
        </g:form>
    </div>
</div>
</body>
</html>
