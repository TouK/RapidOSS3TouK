

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Show OpenNmsNode</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLinkTo(dir:'')}">Home</a></span>
    <span class="menuButton"><g:link class="list" action="list">OpenNmsNode List</g:link></span>
    <span class="menuButton"><g:link class="create" action="create">New OpenNmsNode</g:link></span>
</div>
<div class="body">
    <h1>Show OpenNmsNode</h1>
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
                    
                    <td valign="top" class="value">${openNmsNode.id}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">openNmsId:</td>
                    
                    <td valign="top" class="value">${openNmsNode.openNmsId}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">createdAt:</td>
                    
                    <td valign="top" class="value">${openNmsNode.createdAt}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">domainName:</td>
                    
                    <td valign="top" class="value">${openNmsNode.domainName}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">dpName:</td>
                    
                    <td valign="top" class="value">${openNmsNode.dpName}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">foreignId:</td>
                    
                    <td valign="top" class="value">${openNmsNode.foreignId}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">foreignSource:</td>
                    
                    <td valign="top" class="value">${openNmsNode.foreignSource}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">graphs:</td>
                    
                    <td valign="top" style="text-align:left;" class="value">
                        <ul>
                            <g:each var="g" in="${openNmsNode.graphs}">
                                <li><g:link controller="openNmsGraph" action="show" id="${g.id}">${g}</g:link></li>
                            </g:each>
                        </ul>
                    </td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">ipInterfaces:</td>
                    
                    <td valign="top" style="text-align:left;" class="value">
                        <ul>
                            <g:each var="i" in="${openNmsNode.ipInterfaces}">
                                <li><g:link controller="openNmsIpInterface" action="show" id="${i.id}">${i}</g:link></li>
                            </g:each>
                        </ul>
                    </td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">lastPolledAt:</td>
                    
                    <td valign="top" class="value">${openNmsNode.lastPolledAt}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">name:</td>
                    
                    <td valign="top" class="value">${openNmsNode.name}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">netbiosName:</td>
                    
                    <td valign="top" class="value">${openNmsNode.netbiosName}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">operatingSystem:</td>
                    
                    <td valign="top" class="value">${openNmsNode.operatingSystem}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">sysContact:</td>
                    
                    <td valign="top" class="value">${openNmsNode.sysContact}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">sysDescription:</td>
                    
                    <td valign="top" class="value">${openNmsNode.sysDescription}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">sysLocation:</td>
                    
                    <td valign="top" class="value">${openNmsNode.sysLocation}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">sysName:</td>
                    
                    <td valign="top" class="value">${openNmsNode.sysName}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">sysOid:</td>
                    
                    <td valign="top" class="value">${openNmsNode.sysOid}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">type:</td>
                    
                    <td valign="top" class="value">${openNmsNode.type}</td>
                    
                </tr>
                
            </tbody>
        </table>
    </div>
    <div class="buttons">
        <g:form>
            <input type="hidden" name="id" value="${openNmsNode?.id}"/>
            <span class="button"><g:actionSubmit class="edit" value="Edit"/></span>
            <span class="button"><g:actionSubmit class="delete" onclick="return confirm('Are you sure?');" value="Delete"/></span>
        </g:form>
    </div>
</div>
</body>
</html>
