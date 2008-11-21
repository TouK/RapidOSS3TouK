

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Edit SmartsHSRPGroup</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLinkTo(dir:'')}">Home</a></span>
    <span class="menuButton"><g:link class="list" action="list">SmartsHSRPGroup List</g:link></span>
    <span class="menuButton"><g:link class="create" action="create">New SmartsHSRPGroup</g:link></span>
</div>
<div class="body">
    <h1>Edit SmartsHSRPGroup</h1>
    <g:if test="${flash.message}">
        <div class="message">${flash.message}</div>
    </g:if>
    <g:hasErrors bean="${smartsHSRPGroup}">
        <div class="errors">
            <g:renderErrors bean="${smartsHSRPGroup}" as="list"/>
        </div>
    </g:hasErrors>
    <g:hasErrors bean="${flash.errors}">
       <div class="errors">
            <g:renderErrors bean="${flash.errors}"/>
        </div>
    </g:hasErrors>
    <g:form method="post" >
        <input type="hidden" name="id" value="${smartsHSRPGroup?.id}"/>
        <div class="dialog">
            <table>
                <tbody>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="name">name:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsHSRPGroup,field:'name','errors')}">
                            <input type="text" id="name" name="name" value="${fieldValue(bean:smartsHSRPGroup,field:'name')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="activeInterfaceName">activeInterfaceName:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsHSRPGroup,field:'activeInterfaceName','errors')}">
                            <input type="text" id="activeInterfaceName" name="activeInterfaceName" value="${fieldValue(bean:smartsHSRPGroup,field:'activeInterfaceName')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="activeSystemName">activeSystemName:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsHSRPGroup,field:'activeSystemName','errors')}">
                            <input type="text" id="activeSystemName" name="activeSystemName" value="${fieldValue(bean:smartsHSRPGroup,field:'activeSystemName')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="atRiskThreshold">atRiskThreshold:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsHSRPGroup,field:'atRiskThreshold','errors')}">
                            <input type="text" id="atRiskThreshold" name="atRiskThreshold" value="${fieldValue(bean:smartsHSRPGroup,field:'atRiskThreshold')}" />
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="className">className:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsHSRPGroup,field:'className','errors')}">
                            <input type="text" id="className" name="className" value="${fieldValue(bean:smartsHSRPGroup,field:'className')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="computerSystems">computerSystems:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsHSRPGroup,field:'computerSystems','errors')}">
                            
<ul>
<g:each var="c" in="${smartsHSRPGroup?.computerSystems?}">
    <li style="margin-bottom:3px;">
        <g:link controller="smartsComputerSystem" action="show" id="${c.id}">${c}</g:link>
        <g:link class="delete" action="removeRelation" params="['id':smartsHSRPGroup?.id, 'relationName':'computerSystems', 'relatedObjectId':c.id]"></g:link>
    </li>
</g:each>
</ul>
<g:link params="['id':smartsHSRPGroup?.id, 'relationName':'computerSystems']" action="addTo">Add SmartsComputerSystem</g:link>

                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="description">description:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsHSRPGroup,field:'description','errors')}">
                            <input type="text" id="description" name="description" value="${fieldValue(bean:smartsHSRPGroup,field:'description')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="displayName">displayName:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsHSRPGroup,field:'displayName','errors')}">
                            <input type="text" id="displayName" name="displayName" value="${fieldValue(bean:smartsHSRPGroup,field:'displayName')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="endPoints">endPoints:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsHSRPGroup,field:'endPoints','errors')}">
                            
<ul>
<g:each var="e" in="${smartsHSRPGroup?.endPoints?}">
    <li style="margin-bottom:3px;">
        <g:link controller="smartsHSRPEndpoint" action="show" id="${e.id}">${e}</g:link>
        <g:link class="delete" action="removeRelation" params="['id':smartsHSRPGroup?.id, 'relationName':'endPoints', 'relatedObjectId':e.id]"></g:link>
    </li>
</g:each>
</ul>
<g:link params="['id':smartsHSRPGroup?.id, 'relationName':'endPoints']" action="addTo">Add SmartsHSRPEndpoint</g:link>

                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="groupNumber">groupNumber:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsHSRPGroup,field:'groupNumber','errors')}">
                            <input type="text" id="groupNumber" name="groupNumber" value="${fieldValue(bean:smartsHSRPGroup,field:'groupNumber')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="hsrpEpStateChanged">hsrpEpStateChanged:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsHSRPGroup,field:'hsrpEpStateChanged','errors')}">
                            <g:checkBox name="hsrpEpStateChanged" value="${smartsHSRPGroup?.hsrpEpStateChanged}" ></g:checkBox>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="isAnyComponentDown">isAnyComponentDown:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsHSRPGroup,field:'isAnyComponentDown','errors')}">
                            <g:checkBox name="isAnyComponentDown" value="${smartsHSRPGroup?.isAnyComponentDown}" ></g:checkBox>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="isAnyHSRPEndpointActive">isAnyHSRPEndpointActive:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsHSRPGroup,field:'isAnyHSRPEndpointActive','errors')}">
                            <g:checkBox name="isAnyHSRPEndpointActive" value="${smartsHSRPGroup?.isAnyHSRPEndpointActive}" ></g:checkBox>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="isEveryComponentDown">isEveryComponentDown:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsHSRPGroup,field:'isEveryComponentDown','errors')}">
                            <g:checkBox name="isEveryComponentDown" value="${smartsHSRPGroup?.isEveryComponentDown}" ></g:checkBox>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="isEveryHSRPEndpointReady">isEveryHSRPEndpointReady:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsHSRPGroup,field:'isEveryHSRPEndpointReady','errors')}">
                            <g:checkBox name="isEveryHSRPEndpointReady" value="${smartsHSRPGroup?.isEveryHSRPEndpointReady}" ></g:checkBox>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="isGroupPartOfSingleUnresponsiveSystem">isGroupPartOfSingleUnresponsiveSystem:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsHSRPGroup,field:'isGroupPartOfSingleUnresponsiveSystem','errors')}">
                            <g:checkBox name="isGroupPartOfSingleUnresponsiveSystem" value="${smartsHSRPGroup?.isGroupPartOfSingleUnresponsiveSystem}" ></g:checkBox>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="isManaged">isManaged:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsHSRPGroup,field:'isManaged','errors')}">
                            <g:checkBox name="isManaged" value="${smartsHSRPGroup?.isManaged}" ></g:checkBox>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="isVirtualIPUnresponsive">isVirtualIPUnresponsive:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsHSRPGroup,field:'isVirtualIPUnresponsive','errors')}">
                            <g:checkBox name="isVirtualIPUnresponsive" value="${smartsHSRPGroup?.isVirtualIPUnresponsive}" ></g:checkBox>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="memberOfGroup">memberOfGroup:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsHSRPGroup,field:'memberOfGroup','errors')}">
                            
<ul>
<g:each var="m" in="${smartsHSRPGroup?.memberOfGroup?}">
    <li style="margin-bottom:3px;">
        <g:link controller="rsGroup" action="show" id="${m.id}">${m}</g:link>
        <g:link class="delete" action="removeRelation" params="['id':smartsHSRPGroup?.id, 'relationName':'memberOfGroup', 'relatedObjectId':m.id]"></g:link>
    </li>
</g:each>
</ul>
<g:link params="['id':smartsHSRPGroup?.id, 'relationName':'memberOfGroup']" action="addTo">Add RsGroup</g:link>

                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="numberOfComponents">numberOfComponents:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsHSRPGroup,field:'numberOfComponents','errors')}">
                            <input type="text" id="numberOfComponents" name="numberOfComponents" value="${fieldValue(bean:smartsHSRPGroup,field:'numberOfComponents')}" />
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="numberOfFaultyComponents">numberOfFaultyComponents:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsHSRPGroup,field:'numberOfFaultyComponents','errors')}">
                            <input type="text" id="numberOfFaultyComponents" name="numberOfFaultyComponents" value="${fieldValue(bean:smartsHSRPGroup,field:'numberOfFaultyComponents')}" />
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="rsDatasource">rsDatasource:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsHSRPGroup,field:'rsDatasource','errors')}">
                            <input type="text" id="rsDatasource" name="rsDatasource" value="${fieldValue(bean:smartsHSRPGroup,field:'rsDatasource')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="virtualIP">virtualIP:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsHSRPGroup,field:'virtualIP','errors')}">
                            <input type="text" id="virtualIP" name="virtualIP" value="${fieldValue(bean:smartsHSRPGroup,field:'virtualIP')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="virtualMAC">virtualMAC:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsHSRPGroup,field:'virtualMAC','errors')}">
                            <input type="text" id="virtualMAC" name="virtualMAC" value="${fieldValue(bean:smartsHSRPGroup,field:'virtualMAC')}"/>
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
