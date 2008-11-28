

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Edit OpenNmsService</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLinkTo(dir:'')}">Home</a></span>
    <span class="menuButton"><g:link class="list" action="list">OpenNmsService List</g:link></span>
    <span class="menuButton"><g:link class="create" action="create">New OpenNmsService</g:link></span>
</div>
<div class="body">
    <h1>Edit OpenNmsService</h1>
    <g:if test="${flash.message}">
        <div class="message">${flash.message}</div>
    </g:if>
    <g:hasErrors bean="${openNmsService}">
        <div class="errors">
            <g:renderErrors bean="${openNmsService}" as="list"/>
        </div>
    </g:hasErrors>
    <g:hasErrors bean="${flash.errors}">
       <div class="errors">
            <g:renderErrors bean="${flash.errors}"/>
        </div>
    </g:hasErrors>
    <g:form method="post" >
        <input type="hidden" name="id" value="${openNmsService?.id}"/>
        <div class="dialog">
            <table>
                <tbody>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="name">name:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:openNmsService,field:'name','errors')}">
                            <input type="text" id="name" name="name" value="${fieldValue(bean:openNmsService,field:'name')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="className">className:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:openNmsService,field:'className','errors')}">
                            <input type="text" id="className" name="className" value="${fieldValue(bean:openNmsService,field:'className')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="description">description:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:openNmsService,field:'description','errors')}">
                            <input type="text" id="description" name="description" value="${fieldValue(bean:openNmsService,field:'description')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="displayName">displayName:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:openNmsService,field:'displayName','errors')}">
                            <input type="text" id="displayName" name="displayName" value="${fieldValue(bean:openNmsService,field:'displayName')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="graphs">graphs:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:openNmsService,field:'graphs','errors')}">
                            
<ul>
<g:each var="g" in="${openNmsService?.graphs?}">
    <li style="margin-bottom:3px;">
        <g:link controller="openNmsGraph" action="show" id="${g.id}">${g}</g:link>
        <g:link class="delete" action="removeRelation" params="['id':openNmsService?.id, 'relationName':'graphs', 'relatedObjectId':g.id]"></g:link>
    </li>
</g:each>
</ul>
<g:link params="['id':openNmsService?.id, 'relationName':'graphs']" action="addTo">Add OpenNmsGraph</g:link>

                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="ipInterface">ipInterface:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:openNmsService,field:'ipInterface','errors')}">
                            <g:select optionKey="id" from="${OpenNmsIpInterface.list()}" name="ipInterface.id" value="${openNmsService?.ipInterface?.id}" noSelection="['null':'']"></g:select>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="isManaged">isManaged:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:openNmsService,field:'isManaged','errors')}">
                            <g:checkBox name="isManaged" value="${openNmsService?.isManaged}" ></g:checkBox>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="lastFailedAt">lastFailedAt:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:openNmsService,field:'lastFailedAt','errors')}">
                            <g:datePicker name="lastFailedAt" value="${openNmsService?.lastFailedAt}" noSelection="['':'']"></g:datePicker>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="lastGoodAt">lastGoodAt:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:openNmsService,field:'lastGoodAt','errors')}">
                            <g:datePicker name="lastGoodAt" value="${openNmsService?.lastGoodAt}" noSelection="['':'']"></g:datePicker>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="memberOfGroup">memberOfGroup:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:openNmsService,field:'memberOfGroup','errors')}">
                            
<ul>
<g:each var="m" in="${openNmsService?.memberOfGroup?}">
    <li style="margin-bottom:3px;">
        <g:link controller="rsGroup" action="show" id="${m.id}">${m}</g:link>
        <g:link class="delete" action="removeRelation" params="['id':openNmsService?.id, 'relationName':'memberOfGroup', 'relatedObjectId':m.id]"></g:link>
    </li>
</g:each>
</ul>
<g:link params="['id':openNmsService?.id, 'relationName':'memberOfGroup']" action="addTo">Add RsGroup</g:link>

                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="notify">notify:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:openNmsService,field:'notify','errors')}">
                            <input type="text" id="notify" name="notify" value="${fieldValue(bean:openNmsService,field:'notify')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="qualifier">qualifier:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:openNmsService,field:'qualifier','errors')}">
                            <input type="text" id="qualifier" name="qualifier" value="${fieldValue(bean:openNmsService,field:'qualifier')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="rsDatasource">rsDatasource:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:openNmsService,field:'rsDatasource','errors')}">
                            <input type="text" id="rsDatasource" name="rsDatasource" value="${fieldValue(bean:openNmsService,field:'rsDatasource')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="serviceName">serviceName:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:openNmsService,field:'serviceName','errors')}">
                            <input type="text" id="serviceName" name="serviceName" value="${fieldValue(bean:openNmsService,field:'serviceName')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="source">source:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:openNmsService,field:'source','errors')}">
                            <input type="text" id="source" name="source" value="${fieldValue(bean:openNmsService,field:'source')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="status">status:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:openNmsService,field:'status','errors')}">
                            <input type="text" id="status" name="status" value="${fieldValue(bean:openNmsService,field:'status')}"/>
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
