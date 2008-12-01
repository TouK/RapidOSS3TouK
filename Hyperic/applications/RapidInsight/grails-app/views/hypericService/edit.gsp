

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Edit HypericService</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLinkTo(dir:'')}">Home</a></span>
    <span class="menuButton"><g:link class="list" action="list">HypericService List</g:link></span>
    <span class="menuButton"><g:link class="create" action="create">New HypericService</g:link></span>
</div>
<div class="body">
    <h1>Edit HypericService</h1>
    <g:if test="${flash.message}">
        <div class="message">${flash.message}</div>
    </g:if>
    <g:hasErrors bean="${hypericService}">
        <div class="errors">
            <g:renderErrors bean="${hypericService}" as="list"/>
        </div>
    </g:hasErrors>
    <g:hasErrors bean="${flash.errors}">
       <div class="errors">
            <g:renderErrors bean="${flash.errors}"/>
        </div>
    </g:hasErrors>
    <g:form method="post" >
        <input type="hidden" name="id" value="${hypericService?.id}"/>
        <div class="dialog">
            <table>
                <tbody>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="name">name:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:hypericService,field:'name','errors')}">
                            <input type="text" id="name" name="name" value="${fieldValue(bean:hypericService,field:'name')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="availability">availability:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:hypericService,field:'availability','errors')}">
                            <input type="text" id="availability" name="availability" value="${fieldValue(bean:hypericService,field:'availability')}" />
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="className">className:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:hypericService,field:'className','errors')}">
                            <input type="text" id="className" name="className" value="${fieldValue(bean:hypericService,field:'className')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="description">description:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:hypericService,field:'description','errors')}">
                            <input type="text" id="description" name="description" value="${fieldValue(bean:hypericService,field:'description')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="displayName">displayName:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:hypericService,field:'displayName','errors')}">
                            <input type="text" id="displayName" name="displayName" value="${fieldValue(bean:hypericService,field:'displayName')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="hypericName">hypericName:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:hypericService,field:'hypericName','errors')}">
                            <input type="text" id="hypericName" name="hypericName" value="${fieldValue(bean:hypericService,field:'hypericName')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="isManaged">isManaged:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:hypericService,field:'isManaged','errors')}">
                            <g:checkBox name="isManaged" value="${hypericService?.isManaged}" ></g:checkBox>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="location">location:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:hypericService,field:'location','errors')}">
                            <input type="text" id="location" name="location" value="${fieldValue(bean:hypericService,field:'location')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="memberOfGroup">memberOfGroup:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:hypericService,field:'memberOfGroup','errors')}">
                            
<ul>
<g:each var="m" in="${hypericService?.memberOfGroup?}">
    <li style="margin-bottom:3px;">
        <g:link controller="rsGroup" action="show" id="${m.id}">${m}</g:link>
        <g:link class="delete" action="removeRelation" params="['id':hypericService?.id, 'relationName':'memberOfGroup', 'relatedObjectId':m.id]"></g:link>
    </li>
</g:each>
</ul>
<g:link params="['id':hypericService?.id, 'relationName':'memberOfGroup']" action="addTo">Add RsGroup</g:link>

                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="rsDatasource">rsDatasource:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:hypericService,field:'rsDatasource','errors')}">
                            <input type="text" id="rsDatasource" name="rsDatasource" value="${fieldValue(bean:hypericService,field:'rsDatasource')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="server">server:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:hypericService,field:'server','errors')}">
                            <g:select optionKey="id" from="${HypericServer.list()}" name="server.id" value="${hypericService?.server?.id}" noSelection="['null':'']"></g:select>
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
