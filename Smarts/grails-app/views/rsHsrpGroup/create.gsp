

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Create RsHsrpGroup</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLinkTo(dir:'')}">Home</a></span>
    <span class="menuButton"><g:link class="list" action="list">RsHsrpGroup List</g:link></span>
</div>
<div class="body">
    <h1>Create RsHsrpGroup</h1>
    <g:if test="${flash.message}">
        <div class="message">${flash.message}</div>
    </g:if>
    <g:hasErrors bean="${rsHsrpGroup}">
        <div class="errors">
            <g:renderErrors bean="${rsHsrpGroup}" as="list"/>
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
                            <label for="name">name:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsHsrpGroup,field:'name','errors')}">
                            <input type="text" id="name" name="name" value="${fieldValue(bean:rsHsrpGroup,field:'name')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="activeInterfaceName">activeInterfaceName:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsHsrpGroup,field:'activeInterfaceName','errors')}">
                            <input type="text" id="activeInterfaceName" name="activeInterfaceName" value="${fieldValue(bean:rsHsrpGroup,field:'activeInterfaceName')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="activeSystemName">activeSystemName:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsHsrpGroup,field:'activeSystemName','errors')}">
                            <input type="text" id="activeSystemName" name="activeSystemName" value="${fieldValue(bean:rsHsrpGroup,field:'activeSystemName')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="atRiskThreshold">atRiskThreshold:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsHsrpGroup,field:'atRiskThreshold','errors')}">
                            <input type="text" id="atRiskThreshold" name="atRiskThreshold" value="${fieldValue(bean:rsHsrpGroup,field:'atRiskThreshold')}" />
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="creationClassName">creationClassName:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsHsrpGroup,field:'creationClassName','errors')}">
                            <input type="text" id="creationClassName" name="creationClassName" value="${fieldValue(bean:rsHsrpGroup,field:'creationClassName')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="description">description:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsHsrpGroup,field:'description','errors')}">
                            <input type="text" id="description" name="description" value="${fieldValue(bean:rsHsrpGroup,field:'description')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="displayName">displayName:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsHsrpGroup,field:'displayName','errors')}">
                            <input type="text" id="displayName" name="displayName" value="${fieldValue(bean:rsHsrpGroup,field:'displayName')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="groupNumber">groupNumber:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsHsrpGroup,field:'groupNumber','errors')}">
                            <input type="text" id="groupNumber" name="groupNumber" value="${fieldValue(bean:rsHsrpGroup,field:'groupNumber')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="hsrpEpStateChanged">hsrpEpStateChanged:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsHsrpGroup,field:'hsrpEpStateChanged','errors')}">
                            <g:checkBox name="hsrpEpStateChanged" value="${rsHsrpGroup?.hsrpEpStateChanged}" ></g:checkBox>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="isAnyComponentDown">isAnyComponentDown:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsHsrpGroup,field:'isAnyComponentDown','errors')}">
                            <g:checkBox name="isAnyComponentDown" value="${rsHsrpGroup?.isAnyComponentDown}" ></g:checkBox>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="isAnyHSRPEndpointActive">isAnyHSRPEndpointActive:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsHsrpGroup,field:'isAnyHSRPEndpointActive','errors')}">
                            <g:checkBox name="isAnyHSRPEndpointActive" value="${rsHsrpGroup?.isAnyHSRPEndpointActive}" ></g:checkBox>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="isEveryComponentDown">isEveryComponentDown:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsHsrpGroup,field:'isEveryComponentDown','errors')}">
                            <g:checkBox name="isEveryComponentDown" value="${rsHsrpGroup?.isEveryComponentDown}" ></g:checkBox>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="isEveryHSRPEndpointReady">isEveryHSRPEndpointReady:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsHsrpGroup,field:'isEveryHSRPEndpointReady','errors')}">
                            <g:checkBox name="isEveryHSRPEndpointReady" value="${rsHsrpGroup?.isEveryHSRPEndpointReady}" ></g:checkBox>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="isGroupPartOfSingleUnresponsiveSystem">isGroupPartOfSingleUnresponsiveSystem:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsHsrpGroup,field:'isGroupPartOfSingleUnresponsiveSystem','errors')}">
                            <g:checkBox name="isGroupPartOfSingleUnresponsiveSystem" value="${rsHsrpGroup?.isGroupPartOfSingleUnresponsiveSystem}" ></g:checkBox>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="isManaged">isManaged:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsHsrpGroup,field:'isManaged','errors')}">
                            <g:checkBox name="isManaged" value="${rsHsrpGroup?.isManaged}" ></g:checkBox>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="isVirtualIPUnresponsive">isVirtualIPUnresponsive:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsHsrpGroup,field:'isVirtualIPUnresponsive','errors')}">
                            <g:checkBox name="isVirtualIPUnresponsive" value="${rsHsrpGroup?.isVirtualIPUnresponsive}" ></g:checkBox>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="numberOfComponents">numberOfComponents:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsHsrpGroup,field:'numberOfComponents','errors')}">
                            <input type="text" id="numberOfComponents" name="numberOfComponents" value="${fieldValue(bean:rsHsrpGroup,field:'numberOfComponents')}" />
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="numberOfFaultyComponents">numberOfFaultyComponents:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsHsrpGroup,field:'numberOfFaultyComponents','errors')}">
                            <input type="text" id="numberOfFaultyComponents" name="numberOfFaultyComponents" value="${fieldValue(bean:rsHsrpGroup,field:'numberOfFaultyComponents')}" />
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="rsDatasource">rsDatasource:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsHsrpGroup,field:'rsDatasource','errors')}">
                            <input type="text" id="rsDatasource" name="rsDatasource" value="${fieldValue(bean:rsHsrpGroup,field:'rsDatasource')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="virtualIP">virtualIP:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsHsrpGroup,field:'virtualIP','errors')}">
                            <input type="text" id="virtualIP" name="virtualIP" value="${fieldValue(bean:rsHsrpGroup,field:'virtualIP')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="virtualMAC">virtualMAC:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsHsrpGroup,field:'virtualMAC','errors')}">
                            <input type="text" id="virtualMAC" name="virtualMAC" value="${fieldValue(bean:rsHsrpGroup,field:'virtualMAC')}"/>
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
