

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Edit SmartsHsrpGroup</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLinkTo(dir:'')}">Home</a></span>
    <span class="menuButton"><g:link class="list" action="list">SmartsHsrpGroup List</g:link></span>
    <span class="menuButton"><g:link class="create" action="create">New SmartsHsrpGroup</g:link></span>
</div>
<div class="body">
    <h1>Edit SmartsHsrpGroup</h1>
    <g:if test="${flash.message}">
        <div class="message">${flash.message}</div>
    </g:if>
    <g:hasErrors bean="${smartsHsrpGroup}">
        <div class="errors">
            <g:renderErrors bean="${smartsHsrpGroup}" as="list"/>
        </div>
    </g:hasErrors>
    <g:hasErrors bean="${flash.errors}">
       <div class="errors">
            <g:renderErrors bean="${flash.errors}"/>
        </div>
    </g:hasErrors>
    <g:form method="post" >
        <input type="hidden" name="id" value="${smartsHsrpGroup?.id}"/>
        <div class="dialog">
            <table>
                <tbody>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="name">name:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsHsrpGroup,field:'name','errors')}">
                            <input type="text" id="name" name="name" value="${fieldValue(bean:smartsHsrpGroup,field:'name')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="activeInterfaceName">activeInterfaceName:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsHsrpGroup,field:'activeInterfaceName','errors')}">
                            <input type="text" id="activeInterfaceName" name="activeInterfaceName" value="${fieldValue(bean:smartsHsrpGroup,field:'activeInterfaceName')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="activeSystemName">activeSystemName:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsHsrpGroup,field:'activeSystemName','errors')}">
                            <input type="text" id="activeSystemName" name="activeSystemName" value="${fieldValue(bean:smartsHsrpGroup,field:'activeSystemName')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="atRiskThreshold">atRiskThreshold:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsHsrpGroup,field:'atRiskThreshold','errors')}">
                            <input type="text" id="atRiskThreshold" name="atRiskThreshold" value="${fieldValue(bean:smartsHsrpGroup,field:'atRiskThreshold')}" />
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="className">className:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsHsrpGroup,field:'className','errors')}">
                            <input type="text" id="className" name="className" value="${fieldValue(bean:smartsHsrpGroup,field:'className')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="description">description:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsHsrpGroup,field:'description','errors')}">
                            <input type="text" id="description" name="description" value="${fieldValue(bean:smartsHsrpGroup,field:'description')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="displayName">displayName:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsHsrpGroup,field:'displayName','errors')}">
                            <input type="text" id="displayName" name="displayName" value="${fieldValue(bean:smartsHsrpGroup,field:'displayName')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="groupNumber">groupNumber:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsHsrpGroup,field:'groupNumber','errors')}">
                            <input type="text" id="groupNumber" name="groupNumber" value="${fieldValue(bean:smartsHsrpGroup,field:'groupNumber')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="hsrpEpStateChanged">hsrpEpStateChanged:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsHsrpGroup,field:'hsrpEpStateChanged','errors')}">
                            <g:checkBox name="hsrpEpStateChanged" value="${smartsHsrpGroup?.hsrpEpStateChanged}" ></g:checkBox>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="isAnyComponentDown">isAnyComponentDown:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsHsrpGroup,field:'isAnyComponentDown','errors')}">
                            <g:checkBox name="isAnyComponentDown" value="${smartsHsrpGroup?.isAnyComponentDown}" ></g:checkBox>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="isAnyHSRPEndpointActive">isAnyHSRPEndpointActive:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsHsrpGroup,field:'isAnyHSRPEndpointActive','errors')}">
                            <g:checkBox name="isAnyHSRPEndpointActive" value="${smartsHsrpGroup?.isAnyHSRPEndpointActive}" ></g:checkBox>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="isEveryComponentDown">isEveryComponentDown:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsHsrpGroup,field:'isEveryComponentDown','errors')}">
                            <g:checkBox name="isEveryComponentDown" value="${smartsHsrpGroup?.isEveryComponentDown}" ></g:checkBox>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="isEveryHSRPEndpointReady">isEveryHSRPEndpointReady:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsHsrpGroup,field:'isEveryHSRPEndpointReady','errors')}">
                            <g:checkBox name="isEveryHSRPEndpointReady" value="${smartsHsrpGroup?.isEveryHSRPEndpointReady}" ></g:checkBox>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="isGroupPartOfSingleUnresponsiveSystem">isGroupPartOfSingleUnresponsiveSystem:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsHsrpGroup,field:'isGroupPartOfSingleUnresponsiveSystem','errors')}">
                            <g:checkBox name="isGroupPartOfSingleUnresponsiveSystem" value="${smartsHsrpGroup?.isGroupPartOfSingleUnresponsiveSystem}" ></g:checkBox>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="isManaged">isManaged:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsHsrpGroup,field:'isManaged','errors')}">
                            <g:checkBox name="isManaged" value="${smartsHsrpGroup?.isManaged}" ></g:checkBox>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="isVirtualIPUnresponsive">isVirtualIPUnresponsive:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsHsrpGroup,field:'isVirtualIPUnresponsive','errors')}">
                            <g:checkBox name="isVirtualIPUnresponsive" value="${smartsHsrpGroup?.isVirtualIPUnresponsive}" ></g:checkBox>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="memberOfGroup">memberOfGroup:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsHsrpGroup,field:'memberOfGroup','errors')}">
                            
<ul>
<g:each var="m" in="${smartsHsrpGroup?.memberOfGroup?}">
    <li style="margin-bottom:3px;">
        <g:link controller="rsGroup" action="show" id="${m.id}">${m}</g:link>
        <g:link class="delete" action="removeRelation" params="['id':smartsHsrpGroup?.id, 'relationName':'memberOfGroup', 'relatedObjectId':m.id]"></g:link>
    </li>
</g:each>
</ul>
<g:link params="['id':smartsHsrpGroup?.id, 'relationName':'memberOfGroup']" action="addTo">Add RsGroup</g:link>

                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="numberOfComponents">numberOfComponents:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsHsrpGroup,field:'numberOfComponents','errors')}">
                            <input type="text" id="numberOfComponents" name="numberOfComponents" value="${fieldValue(bean:smartsHsrpGroup,field:'numberOfComponents')}" />
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="numberOfFaultyComponents">numberOfFaultyComponents:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsHsrpGroup,field:'numberOfFaultyComponents','errors')}">
                            <input type="text" id="numberOfFaultyComponents" name="numberOfFaultyComponents" value="${fieldValue(bean:smartsHsrpGroup,field:'numberOfFaultyComponents')}" />
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="rsDatasource">rsDatasource:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsHsrpGroup,field:'rsDatasource','errors')}">
                            <input type="text" id="rsDatasource" name="rsDatasource" value="${fieldValue(bean:smartsHsrpGroup,field:'rsDatasource')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="virtualIP">virtualIP:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsHsrpGroup,field:'virtualIP','errors')}">
                            <input type="text" id="virtualIP" name="virtualIP" value="${fieldValue(bean:smartsHsrpGroup,field:'virtualIP')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="virtualMAC">virtualMAC:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsHsrpGroup,field:'virtualMAC','errors')}">
                            <input type="text" id="virtualMAC" name="virtualMAC" value="${fieldValue(bean:smartsHsrpGroup,field:'virtualMAC')}"/>
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
