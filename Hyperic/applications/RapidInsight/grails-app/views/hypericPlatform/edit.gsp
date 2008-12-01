

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Edit HypericPlatform</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLinkTo(dir:'')}">Home</a></span>
    <span class="menuButton"><g:link class="list" action="list">HypericPlatform List</g:link></span>
    <span class="menuButton"><g:link class="create" action="create">New HypericPlatform</g:link></span>
</div>
<div class="body">
    <h1>Edit HypericPlatform</h1>
    <g:if test="${flash.message}">
        <div class="message">${flash.message}</div>
    </g:if>
    <g:hasErrors bean="${hypericPlatform}">
        <div class="errors">
            <g:renderErrors bean="${hypericPlatform}" as="list"/>
        </div>
    </g:hasErrors>
    <g:hasErrors bean="${flash.errors}">
       <div class="errors">
            <g:renderErrors bean="${flash.errors}"/>
        </div>
    </g:hasErrors>
    <g:form method="post" >
        <input type="hidden" name="id" value="${hypericPlatform?.id}"/>
        <div class="dialog">
            <table>
                <tbody>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="name">name:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:hypericPlatform,field:'name','errors')}">
                            <input type="text" id="name" name="name" value="${fieldValue(bean:hypericPlatform,field:'name')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="availability">availability:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:hypericPlatform,field:'availability','errors')}">
                            <input type="text" id="availability" name="availability" value="${fieldValue(bean:hypericPlatform,field:'availability')}" />
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="className">className:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:hypericPlatform,field:'className','errors')}">
                            <input type="text" id="className" name="className" value="${fieldValue(bean:hypericPlatform,field:'className')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="description">description:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:hypericPlatform,field:'description','errors')}">
                            <input type="text" id="description" name="description" value="${fieldValue(bean:hypericPlatform,field:'description')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="displayName">displayName:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:hypericPlatform,field:'displayName','errors')}">
                            <input type="text" id="displayName" name="displayName" value="${fieldValue(bean:hypericPlatform,field:'displayName')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="hypericName">hypericName:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:hypericPlatform,field:'hypericName','errors')}">
                            <input type="text" id="hypericName" name="hypericName" value="${fieldValue(bean:hypericPlatform,field:'hypericName')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="isManaged">isManaged:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:hypericPlatform,field:'isManaged','errors')}">
                            <g:checkBox name="isManaged" value="${hypericPlatform?.isManaged}" ></g:checkBox>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="location">location:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:hypericPlatform,field:'location','errors')}">
                            <input type="text" id="location" name="location" value="${fieldValue(bean:hypericPlatform,field:'location')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="memberOfGroup">memberOfGroup:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:hypericPlatform,field:'memberOfGroup','errors')}">
                            
<ul>
<g:each var="m" in="${hypericPlatform?.memberOfGroup?}">
    <li style="margin-bottom:3px;">
        <g:link controller="rsGroup" action="show" id="${m.id}">${m}</g:link>
        <g:link class="delete" action="removeRelation" params="['id':hypericPlatform?.id, 'relationName':'memberOfGroup', 'relatedObjectId':m.id]"></g:link>
    </li>
</g:each>
</ul>
<g:link params="['id':hypericPlatform?.id, 'relationName':'memberOfGroup']" action="addTo">Add RsGroup</g:link>

                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="rsDatasource">rsDatasource:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:hypericPlatform,field:'rsDatasource','errors')}">
                            <input type="text" id="rsDatasource" name="rsDatasource" value="${fieldValue(bean:hypericPlatform,field:'rsDatasource')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="servers">servers:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:hypericPlatform,field:'servers','errors')}">
                            
<ul>
<g:each var="s" in="${hypericPlatform?.servers?}">
    <li style="margin-bottom:3px;">
        <g:link controller="hypericServer" action="show" id="${s.id}">${s}</g:link>
        <g:link class="delete" action="removeRelation" params="['id':hypericPlatform?.id, 'relationName':'servers', 'relatedObjectId':s.id]"></g:link>
    </li>
</g:each>
</ul>
<g:link params="['id':hypericPlatform?.id, 'relationName':'servers']" action="addTo">Add HypericServer</g:link>

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
