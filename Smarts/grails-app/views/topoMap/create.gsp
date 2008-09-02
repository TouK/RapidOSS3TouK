

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Create TopoMap</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLinkTo(dir:'')}">Home</a></span>
    <span class="menuButton"><g:link class="list" action="list">TopoMap List</g:link></span>
</div>
<div class="body">
    <h1>Create TopoMap</h1>
    <g:if test="${flash.message}">
        <div class="message">${flash.message}</div>
    </g:if>
    <g:hasErrors bean="${topoMap}">
        <div class="errors">
            <g:renderErrors bean="${topoMap}" as="list"/>
        </div>
    </g:hasErrors>
    <g:hasErrors bean="${flash.errors}">
       <div class="errors">
            <g:renderErrors bean="${flash.errors}"/>
        </div>
    </g:hasErrors>
    <g:form action="save" method="post" >
        <div class="dialog">
            <table>
                <tbody>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="mapName">mapName:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:topoMap,field:'mapName','errors')}">
                            <input type="text" id="mapName" name="mapName" value="${fieldValue(bean:topoMap,field:'mapName')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="username">username:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:topoMap,field:'username','errors')}">
                            <input type="text" id="username" name="username" value="${fieldValue(bean:topoMap,field:'username')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="consistOfDevices">consistOfDevices:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:topoMap,field:'consistOfDevices','errors')}">
                            
<ul>
<g:each var="c" in="${topoMap?.consistOfDevices?}">
    <li style="margin-bottom:3px;">
        <g:link controller="mapNode" action="show" id="${c.id}">${c}</g:link>
        <g:link class="delete" action="removeRelation" params="['id':topoMap?.id, 'relationName':'consistOfDevices', 'relatedObjectId':c.id]"></g:link>
    </li>
</g:each>
</ul>
<g:link params="['id':topoMap?.id, 'relationName':'consistOfDevices']" action="addTo">Add MapNode</g:link>

                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="group">group:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:topoMap,field:'group','errors')}">
                            <g:select optionKey="id" from="${MapGroup.list()}" name="group.id" value="${topoMap?.group?.id}" noSelection="['null':'']"></g:select>
                        </td>
                    </tr>
                    
                </tbody>
            </table>
        </div>
        <div class="buttons">
            <span class="button"><input class="save" type="submit" value="Create"/></span>
        </div>
    </g:form>
</div>
</body>
</html>
