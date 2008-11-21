

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Create SmartsHSRPEndpoint</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLinkTo(dir:'')}">Home</a></span>
    <span class="menuButton"><g:link class="list" action="list">SmartsHSRPEndpoint List</g:link></span>
</div>
<div class="body">
    <h1>Create SmartsHSRPEndpoint</h1>
    <g:if test="${flash.message}">
        <div class="message">${flash.message}</div>
    </g:if>
    <g:hasErrors bean="${smartsHSRPEndpoint}">
        <div class="errors">
            <g:renderErrors bean="${smartsHSRPEndpoint}" as="list"/>
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
                        <td valign="top" class="value ${hasErrors(bean:smartsHSRPEndpoint,field:'name','errors')}">
                            <input type="text" id="name" name="name" value="${fieldValue(bean:smartsHSRPEndpoint,field:'name')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="className">className:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsHSRPEndpoint,field:'className','errors')}">
                            <input type="text" id="className" name="className" value="${fieldValue(bean:smartsHSRPEndpoint,field:'className')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="computerSystemName">computerSystemName:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsHSRPEndpoint,field:'computerSystemName','errors')}">
                            <input type="text" id="computerSystemName" name="computerSystemName" value="${fieldValue(bean:smartsHSRPEndpoint,field:'computerSystemName')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="description">description:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsHSRPEndpoint,field:'description','errors')}">
                            <input type="text" id="description" name="description" value="${fieldValue(bean:smartsHSRPEndpoint,field:'description')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="displayName">displayName:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsHSRPEndpoint,field:'displayName','errors')}">
                            <input type="text" id="displayName" name="displayName" value="${fieldValue(bean:smartsHSRPEndpoint,field:'displayName')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="groupNumber">groupNumber:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsHSRPEndpoint,field:'groupNumber','errors')}">
                            <input type="text" id="groupNumber" name="groupNumber" value="${fieldValue(bean:smartsHSRPEndpoint,field:'groupNumber')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="hsrpEndpointKey">hsrpEndpointKey:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsHSRPEndpoint,field:'hsrpEndpointKey','errors')}">
                            <input type="text" id="hsrpEndpointKey" name="hsrpEndpointKey" value="${fieldValue(bean:smartsHSRPEndpoint,field:'hsrpEndpointKey')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="hsrpGroup">hsrpGroup:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsHSRPEndpoint,field:'hsrpGroup','errors')}">
                            <g:select optionKey="id" from="${SmartsHSRPGroup.list()}" name="hsrpGroup.id" value="${smartsHSRPEndpoint?.hsrpGroup?.id}" noSelection="['null':'']"></g:select>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="isManaged">isManaged:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsHSRPEndpoint,field:'isManaged','errors')}">
                            <g:checkBox name="isManaged" value="${smartsHSRPEndpoint?.isManaged}" ></g:checkBox>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="isReady">isReady:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsHSRPEndpoint,field:'isReady','errors')}">
                            <input type="text" id="isReady" name="isReady" value="${fieldValue(bean:smartsHSRPEndpoint,field:'isReady')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="isSwitchOverActive">isSwitchOverActive:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsHSRPEndpoint,field:'isSwitchOverActive','errors')}">
                            <input type="text" id="isSwitchOverActive" name="isSwitchOverActive" value="${fieldValue(bean:smartsHSRPEndpoint,field:'isSwitchOverActive')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="numberOfComponents">numberOfComponents:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsHSRPEndpoint,field:'numberOfComponents','errors')}">
                            <input type="text" id="numberOfComponents" name="numberOfComponents" value="${fieldValue(bean:smartsHSRPEndpoint,field:'numberOfComponents')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="numberOfFaultyComponents">numberOfFaultyComponents:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsHSRPEndpoint,field:'numberOfFaultyComponents','errors')}">
                            <input type="text" id="numberOfFaultyComponents" name="numberOfFaultyComponents" value="${fieldValue(bean:smartsHSRPEndpoint,field:'numberOfFaultyComponents')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="partOf">partOf:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsHSRPEndpoint,field:'partOf','errors')}">
                            <g:select optionKey="id" from="${SmartsComputerSystem.list()}" name="partOf.id" value="${smartsHSRPEndpoint?.partOf?.id}" noSelection="['null':'']"></g:select>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="rsDatasource">rsDatasource:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsHSRPEndpoint,field:'rsDatasource','errors')}">
                            <input type="text" id="rsDatasource" name="rsDatasource" value="${fieldValue(bean:smartsHSRPEndpoint,field:'rsDatasource')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="tag">tag:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsHSRPEndpoint,field:'tag','errors')}">
                            <input type="text" id="tag" name="tag" value="${fieldValue(bean:smartsHSRPEndpoint,field:'tag')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="virtualIP">virtualIP:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsHSRPEndpoint,field:'virtualIP','errors')}">
                            <input type="text" id="virtualIP" name="virtualIP" value="${fieldValue(bean:smartsHSRPEndpoint,field:'virtualIP')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="virtualMAC">virtualMAC:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsHSRPEndpoint,field:'virtualMAC','errors')}">
                            <input type="text" id="virtualMAC" name="virtualMAC" value="${fieldValue(bean:smartsHSRPEndpoint,field:'virtualMAC')}"/>
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
