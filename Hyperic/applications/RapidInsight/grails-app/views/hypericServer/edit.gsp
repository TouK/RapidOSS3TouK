

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Edit HypericServer</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLinkTo(dir:'')}">Home</a></span>
    <span class="menuButton"><g:link class="list" action="list">HypericServer List</g:link></span>
    <span class="menuButton"><g:link class="create" action="create">New HypericServer</g:link></span>
</div>
<div class="body">
    <h1>Edit HypericServer</h1>
    <g:if test="${flash.message}">
        <div class="message">${flash.message}</div>
    </g:if>
    <g:hasErrors bean="${hypericServer}">
        <div class="errors">
            <g:renderErrors bean="${hypericServer}" as="list"/>
        </div>
    </g:hasErrors>
    <g:hasErrors bean="${flash.errors}">
       <div class="errors">
            <g:renderErrors bean="${flash.errors}"/>
        </div>
    </g:hasErrors>
    <g:form method="post" >
        <input type="hidden" name="id" value="${hypericServer?.id}"/>
        <div class="dialog">
            <table>
                <tbody>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="name">name:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:hypericServer,field:'name','errors')}">
                            <input type="text" id="name" name="name" value="${fieldValue(bean:hypericServer,field:'name')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="availability">availability:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:hypericServer,field:'availability','errors')}">
                            <input type="text" id="availability" name="availability" value="${fieldValue(bean:hypericServer,field:'availability')}" />
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="className">className:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:hypericServer,field:'className','errors')}">
                            <input type="text" id="className" name="className" value="${fieldValue(bean:hypericServer,field:'className')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="description">description:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:hypericServer,field:'description','errors')}">
                            <input type="text" id="description" name="description" value="${fieldValue(bean:hypericServer,field:'description')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="displayName">displayName:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:hypericServer,field:'displayName','errors')}">
                            <input type="text" id="displayName" name="displayName" value="${fieldValue(bean:hypericServer,field:'displayName')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="hypericName">hypericName:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:hypericServer,field:'hypericName','errors')}">
                            <input type="text" id="hypericName" name="hypericName" value="${fieldValue(bean:hypericServer,field:'hypericName')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="isManaged">isManaged:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:hypericServer,field:'isManaged','errors')}">
                            <g:checkBox name="isManaged" value="${hypericServer?.isManaged}" ></g:checkBox>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="location">location:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:hypericServer,field:'location','errors')}">
                            <input type="text" id="location" name="location" value="${fieldValue(bean:hypericServer,field:'location')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="memberOfGroup">memberOfGroup:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:hypericServer,field:'memberOfGroup','errors')}">
                            
<ul>
<g:each var="m" in="${hypericServer?.memberOfGroup?}">
    <li style="margin-bottom:3px;">
        <g:link controller="rsGroup" action="show" id="${m.id}">${m}</g:link>
        <g:link class="delete" action="removeRelation" params="['id':hypericServer?.id, 'relationName':'memberOfGroup', 'relatedObjectId':m.id]"></g:link>
    </li>
</g:each>
</ul>
<g:link params="['id':hypericServer?.id, 'relationName':'memberOfGroup']" action="addTo">Add RsGroup</g:link>

                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="platform">platform:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:hypericServer,field:'platform','errors')}">
                            <g:select optionKey="id" from="${HypericPlatform.list()}" name="platform.id" value="${hypericServer?.platform?.id}" noSelection="['null':'']"></g:select>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="rsDatasource">rsDatasource:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:hypericServer,field:'rsDatasource','errors')}">
                            <input type="text" id="rsDatasource" name="rsDatasource" value="${fieldValue(bean:hypericServer,field:'rsDatasource')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="services">services:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:hypericServer,field:'services','errors')}">
                            
<ul>
<g:each var="s" in="${hypericServer?.services?}">
    <li style="margin-bottom:3px;">
        <g:link controller="hypericService" action="show" id="${s.id}">${s}</g:link>
        <g:link class="delete" action="removeRelation" params="['id':hypericServer?.id, 'relationName':'services', 'relatedObjectId':s.id]"></g:link>
    </li>
</g:each>
</ul>
<g:link params="['id':hypericServer?.id, 'relationName':'services']" action="addTo">Add HypericService</g:link>

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
