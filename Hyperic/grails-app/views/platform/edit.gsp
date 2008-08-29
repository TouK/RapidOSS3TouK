

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Edit Platform</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLinkTo(dir:'')}">Home</a></span>
    <span class="menuButton"><g:link class="list" action="list">Platform List</g:link></span>
    <span class="menuButton"><g:link class="create" action="create">New Platform</g:link></span>
</div>
<div class="body">
    <h1>Edit Platform</h1>
    <g:if test="${flash.message}">
        <div class="message">${flash.message}</div>
    </g:if>
    <g:hasErrors bean="${platform}">
        <div class="errors">
            <g:renderErrors bean="${platform}" as="list"/>
        </div>
    </g:hasErrors>
    <g:hasErrors bean="${flash.errors}">
       <div class="errors">
            <g:renderErrors bean="${flash.errors}"/>
        </div>
    </g:hasErrors>
    <g:form method="post" >
        <input type="hidden" name="id" value="${platform?.id}"/>
        <div class="dialog">
            <table>
                <tbody>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="resource_name">resource_name:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:platform,field:'resource_name','errors')}">
                            <input type="text" id="resource_name" name="resource_name" value="${fieldValue(bean:platform,field:'resource_name')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="hasServers">hasServers:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:platform,field:'hasServers','errors')}">
                            
<ul>
<g:each var="h" in="${platform?.hasServers?}">
    <li style="margin-bottom:3px;">
        <g:link controller="server" action="show" id="${h.id}">${h}</g:link>
        <g:link class="delete" action="removeRelation" params="['id':platform?.id, 'relationName':'hasServers', 'relatedObjectId':h.id]"></g:link>
    </li>
</g:each>
</ul>
<g:link params="['id':platform?.id, 'relationName':'hasServers']" action="addTo">Add Server</g:link>

                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="hypericEvents">hypericEvents:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:platform,field:'hypericEvents','errors')}">
                            
<ul>
<g:each var="h" in="${platform?.hypericEvents?}">
    <li style="margin-bottom:3px;">
        <g:link controller="hypericEvent" action="show" id="${h.id}">${h}</g:link>
        <g:link class="delete" action="removeRelation" params="['id':platform?.id, 'relationName':'hypericEvents', 'relatedObjectId':h.id]"></g:link>
    </li>
</g:each>
</ul>
<g:link params="['id':platform?.id, 'relationName':'hypericEvents']" action="addTo">Add HypericEvent</g:link>

                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="status">status:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:platform,field:'status','errors')}">
                            <input type="text" id="status" name="status" value="${fieldValue(bean:platform,field:'status')}"/>
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
