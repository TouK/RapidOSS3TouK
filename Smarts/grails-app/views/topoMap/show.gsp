

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Show TopoMap</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLinkTo(dir:'')}">Home</a></span>
    <span class="menuButton"><g:link class="list" action="list">TopoMap List</g:link></span>
    <span class="menuButton"><g:link class="create" action="create">New TopoMap</g:link></span>
</div>
<div class="body">
    <h1>Show TopoMap</h1>
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
                    
                    <td valign="top" class="value">${topoMap.id}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">mapName:</td>
                    
                    <td valign="top" class="value">${topoMap.mapName}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">username:</td>
                    
                    <td valign="top" class="value">${topoMap.username}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">consistOfDevices:</td>
                    
                    <td valign="top" style="text-align:left;" class="value">
                        <ul>
                            <g:each var="c" in="${topoMap.consistOfDevices}">
                                <li><g:link controller="mapNode" action="show" id="${c.id}">${c}</g:link></li>
                            </g:each>
                        </ul>
                    </td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">group:</td>
                    
                    <td valign="top" class="value"><g:link controller="mapGroup" action="show" id="${topoMap?.group?.id}">${topoMap?.group}</g:link></td>
                    
                </tr>
                
            </tbody>
        </table>
    </div>
    <div class="buttons">
        <g:form>
            <input type="hidden" name="id" value="${topoMap?.id}"/>
            <span class="button"><g:actionSubmit class="edit" value="Edit"/></span>
            <span class="button"><g:actionSubmit class="delete" onclick="return confirm('Are you sure?');" value="Delete"/></span>
        </g:form>
    </div>
</div>
</body>
</html>
