

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Show RsIpNetwork</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLinkTo(dir:'')}">Home</a></span>
    <span class="menuButton"><g:link class="list" action="list">RsIpNetwork List</g:link></span>
    <span class="menuButton"><g:link class="create" action="create">New RsIpNetwork</g:link></span>
</div>
<div class="body">
    <h1>Show RsIpNetwork</h1>
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
                    
                    <td valign="top" class="value">${rsIpNetwork.id}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">name:</td>
                    
                    <td valign="top" class="value">${rsIpNetwork.name}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">connectedSystems:</td>
                    
                    <td valign="top" class="value">${rsIpNetwork.connectedSystems}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">connectedTo:</td>
                    
                    <td valign="top" class="value">${rsIpNetwork.connectedTo}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">creationClassName:</td>
                    
                    <td valign="top" class="value">${rsIpNetwork.creationClassName}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">description:</td>
                    
                    <td valign="top" class="value">${rsIpNetwork.description}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">displayName:</td>
                    
                    <td valign="top" class="value">${rsIpNetwork.displayName}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">isManaged:</td>
                    
                    <td valign="top" class="value">${rsIpNetwork.isManaged}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">netmask:</td>
                    
                    <td valign="top" class="value">${rsIpNetwork.netmask}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">networkNumber:</td>
                    
                    <td valign="top" class="value">${rsIpNetwork.networkNumber}</td>
                    
                </tr>
                
            </tbody>
        </table>
    </div>
    <div class="buttons">
        <g:form>
            <input type="hidden" name="id" value="${rsIpNetwork?.id}"/>
            <span class="button"><g:actionSubmit class="edit" value="Edit"/></span>
            <span class="button"><g:actionSubmit class="delete" onclick="return confirm('Are you sure?');" value="Delete"/></span>
        </g:form>
    </div>
</div>
</body>
</html>
