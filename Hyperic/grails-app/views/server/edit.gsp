

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Edit Server</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLinkTo(dir:'')}">Home</a></span>
    <span class="menuButton"><g:link class="list" action="list">Server List</g:link></span>
    <span class="menuButton"><g:link class="create" action="create">New Server</g:link></span>
</div>
<div class="body">
    <h1>Edit Server</h1>
    <g:if test="${flash.message}">
        <div class="message">${flash.message}</div>
    </g:if>
    <g:hasErrors bean="${server}">
        <div class="errors">
            <g:renderErrors bean="${server}" as="list"/>
        </div>
    </g:hasErrors>
    <g:hasErrors bean="${flash.errors}">
       <div class="errors">
            <g:renderErrors bean="${flash.errors}"/>
        </div>
    </g:hasErrors>
    <g:form method="post" >
        <input type="hidden" name="id" value="${server?.id}"/>
        <div class="dialog">
            <table>
                <tbody>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="resource_name">resource_name:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:server,field:'resource_name','errors')}">
                            <input type="text" id="resource_name" name="resource_name" value="${fieldValue(bean:server,field:'resource_name')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="hasServices">hasServices:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:server,field:'hasServices','errors')}">
                            
<ul>
<g:each var="h" in="${server?.hasServices?}">
    <li style="margin-bottom:3px;">
        <g:link controller="service" action="show" id="${h.id}">${h}</g:link>
        <g:link class="delete" action="removeRelation" params="['id':server?.id, 'relationName':'hasServices', 'relatedObjectId':h.id]"></g:link>
    </li>
</g:each>
</ul>
<g:link params="['id':server?.id, 'relationName':'hasServices']" action="addTo">Add Service</g:link>

                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="hypericEvents">hypericEvents:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:server,field:'hypericEvents','errors')}">
                            
<ul>
<g:each var="h" in="${server?.hypericEvents?}">
    <li style="margin-bottom:3px;">
        <g:link controller="hypericEvent" action="show" id="${h.id}">${h}</g:link>
        <g:link class="delete" action="removeRelation" params="['id':server?.id, 'relationName':'hypericEvents', 'relatedObjectId':h.id]"></g:link>
    </li>
</g:each>
</ul>
<g:link params="['id':server?.id, 'relationName':'hypericEvents']" action="addTo">Add HypericEvent</g:link>

                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="serverOf">serverOf:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:server,field:'serverOf','errors')}">
                            <g:select optionKey="id" from="${Platform.list()}" name="serverOf.id" value="${server?.serverOf?.id}" noSelection="['null':'']"></g:select>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="status">status:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:server,field:'status','errors')}">
                            <input type="text" id="status" name="status" value="${fieldValue(bean:server,field:'status')}"/>
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
