

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Edit MapNode</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLinkTo(dir:'')}">Home</a></span>
    <span class="menuButton"><g:link class="list" action="list">MapNode List</g:link></span>
    <span class="menuButton"><g:link class="create" action="create">New MapNode</g:link></span>
</div>
<div class="body">
    <h1>Edit MapNode</h1>
    <g:if test="${flash.message}">
        <div class="message">${flash.message}</div>
    </g:if>
    <g:hasErrors bean="${mapNode}">
        <div class="errors">
            <g:renderErrors bean="${mapNode}" as="list"/>
        </div>
    </g:hasErrors>
    <g:hasErrors bean="${flash.errors}">
       <div class="errors">
            <g:renderErrors bean="${flash.errors}"/>
        </div>
    </g:hasErrors>
    <g:form method="post" >
        <input type="hidden" name="id" value="${mapNode?.id}"/>
        <div class="dialog">
            <table>
                <tbody>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="mapName">mapName:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:mapNode,field:'mapName','errors')}">
                            <input type="text" id="mapName" name="mapName" value="${fieldValue(bean:mapNode,field:'mapName')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="nodeIdentifier">nodeIdentifier:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:mapNode,field:'nodeIdentifier','errors')}">
                            <input type="text" id="nodeIdentifier" name="nodeIdentifier" value="${fieldValue(bean:mapNode,field:'nodeIdentifier')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="username">username:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:mapNode,field:'username','errors')}">
                            <input type="text" id="username" name="username" value="${fieldValue(bean:mapNode,field:'username')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="belongsToMap">belongsToMap:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:mapNode,field:'belongsToMap','errors')}">
                            <g:select optionKey="id" from="${Map.list()}" name="belongsToMap.id" value="${mapNode?.belongsToMap?.id}" noSelection="['null':'']"></g:select>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="xlocation">xlocation:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:mapNode,field:'xlocation','errors')}">
                            <input type="text" id="xlocation" name="xlocation" value="${fieldValue(bean:mapNode,field:'xlocation')}" />
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="ylocation">ylocation:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:mapNode,field:'ylocation','errors')}">
                            <input type="text" id="ylocation" name="ylocation" value="${fieldValue(bean:mapNode,field:'ylocation')}" />
                        </td>
                    </tr>
                    
                </tbody>
            </table>
        </div>
        <div class="buttons">
            <span class="button"><g:actionSubmit class="save" value="Update"/></span>
            <span class="button"><g:actionSubmit class="delete" onclick="return confirm('Are you sure?');" value="Delete"/></span>
        </div>
    </g:form>
</div>
</body>
</html>
