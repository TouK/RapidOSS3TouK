

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Show RsHsrpGroup</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLinkTo(dir:'')}">Home</a></span>
    <span class="menuButton"><g:link class="list" action="list">RsHsrpGroup List</g:link></span>
    <span class="menuButton"><g:link class="create" action="create">New RsHsrpGroup</g:link></span>
</div>
<div class="body">
    <h1>Show RsHsrpGroup</h1>
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
                    
                    <td valign="top" class="value">${rsHsrpGroup.id}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">name:</td>
                    
                    <td valign="top" class="value">${rsHsrpGroup.name}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">activeInterfaceName:</td>
                    
                    <td valign="top" class="value">${rsHsrpGroup.activeInterfaceName}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">activeSystemName:</td>
                    
                    <td valign="top" class="value">${rsHsrpGroup.activeSystemName}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">atRiskThreshold:</td>
                    
                    <td valign="top" class="value">${rsHsrpGroup.atRiskThreshold}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">creationClassName:</td>
                    
                    <td valign="top" class="value">${rsHsrpGroup.creationClassName}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">description:</td>
                    
                    <td valign="top" class="value">${rsHsrpGroup.description}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">displayName:</td>
                    
                    <td valign="top" class="value">${rsHsrpGroup.displayName}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">groupNumber:</td>
                    
                    <td valign="top" class="value">${rsHsrpGroup.groupNumber}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">hsrpEpStateChanged:</td>
                    
                    <td valign="top" class="value">${rsHsrpGroup.hsrpEpStateChanged}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">isAnyComponentDown:</td>
                    
                    <td valign="top" class="value">${rsHsrpGroup.isAnyComponentDown}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">isAnyHSRPEndpointActive:</td>
                    
                    <td valign="top" class="value">${rsHsrpGroup.isAnyHSRPEndpointActive}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">isEveryComponentDown:</td>
                    
                    <td valign="top" class="value">${rsHsrpGroup.isEveryComponentDown}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">isEveryHSRPEndpointReady:</td>
                    
                    <td valign="top" class="value">${rsHsrpGroup.isEveryHSRPEndpointReady}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">isGroupPartOfSingleUnresponsiveSystem:</td>
                    
                    <td valign="top" class="value">${rsHsrpGroup.isGroupPartOfSingleUnresponsiveSystem}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">isManaged:</td>
                    
                    <td valign="top" class="value">${rsHsrpGroup.isManaged}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">isVirtualIPUnresponsive:</td>
                    
                    <td valign="top" class="value">${rsHsrpGroup.isVirtualIPUnresponsive}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">numberOfComponents:</td>
                    
                    <td valign="top" class="value">${rsHsrpGroup.numberOfComponents}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">numberOfFaultyComponents:</td>
                    
                    <td valign="top" class="value">${rsHsrpGroup.numberOfFaultyComponents}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">virtualIP:</td>
                    
                    <td valign="top" class="value">${rsHsrpGroup.virtualIP}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">virtualMAC:</td>
                    
                    <td valign="top" class="value">${rsHsrpGroup.virtualMAC}</td>
                    
                </tr>
                
            </tbody>
        </table>
    </div>
    <div class="buttons">
        <g:form>
            <input type="hidden" name="id" value="${rsHsrpGroup?.id}"/>
            <span class="button"><g:actionSubmit class="edit" value="Edit"/></span>
            <span class="button"><g:actionSubmit class="delete" onclick="return confirm('Are you sure?');" value="Delete"/></span>
        </g:form>
    </div>
</div>
</body>
</html>
