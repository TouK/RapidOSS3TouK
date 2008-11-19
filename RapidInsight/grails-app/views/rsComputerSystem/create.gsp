

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Create RsComputerSystem</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLinkTo(dir:'')}">Home</a></span>
    <span class="menuButton"><g:link class="list" action="list">RsComputerSystem List</g:link></span>
</div>
<div class="body">
    <h1>Create RsComputerSystem</h1>
    <g:if test="${flash.message}">
        <div class="message">${flash.message}</div>
    </g:if>
    <g:hasErrors bean="${rsComputerSystem}">
        <div class="errors">
            <g:renderErrors bean="${rsComputerSystem}" as="list"/>
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
                        <td valign="top" class="value ${hasErrors(bean:rsComputerSystem,field:'name','errors')}">
                            <input type="text" id="name" name="name" value="${fieldValue(bean:rsComputerSystem,field:'name')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="className">className:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsComputerSystem,field:'className','errors')}">
                            <input type="text" id="className" name="className" value="${fieldValue(bean:rsComputerSystem,field:'className')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="description">description:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsComputerSystem,field:'description','errors')}">
                            <input type="text" id="description" name="description" value="${fieldValue(bean:rsComputerSystem,field:'description')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="displayName">displayName:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsComputerSystem,field:'displayName','errors')}">
                            <input type="text" id="displayName" name="displayName" value="${fieldValue(bean:rsComputerSystem,field:'displayName')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="geocodes">geocodes:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsComputerSystem,field:'geocodes','errors')}">
                            <input type="text" id="geocodes" name="geocodes" value="${fieldValue(bean:rsComputerSystem,field:'geocodes')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="isManaged">isManaged:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsComputerSystem,field:'isManaged','errors')}">
                            <g:checkBox name="isManaged" value="${rsComputerSystem?.isManaged}" ></g:checkBox>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="location">location:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsComputerSystem,field:'location','errors')}">
                            <input type="text" id="location" name="location" value="${fieldValue(bean:rsComputerSystem,field:'location')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="model">model:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsComputerSystem,field:'model','errors')}">
                            <input type="text" id="model" name="model" value="${fieldValue(bean:rsComputerSystem,field:'model')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="osVersion">osVersion:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsComputerSystem,field:'osVersion','errors')}">
                            <input type="text" id="osVersion" name="osVersion" value="${fieldValue(bean:rsComputerSystem,field:'osVersion')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="primaryOwnerContact">primaryOwnerContact:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsComputerSystem,field:'primaryOwnerContact','errors')}">
                            <input type="text" id="primaryOwnerContact" name="primaryOwnerContact" value="${fieldValue(bean:rsComputerSystem,field:'primaryOwnerContact')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="primaryOwnerName">primaryOwnerName:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsComputerSystem,field:'primaryOwnerName','errors')}">
                            <input type="text" id="primaryOwnerName" name="primaryOwnerName" value="${fieldValue(bean:rsComputerSystem,field:'primaryOwnerName')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="readCommunity">readCommunity:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsComputerSystem,field:'readCommunity','errors')}">
                            <input type="text" id="readCommunity" name="readCommunity" value="${fieldValue(bean:rsComputerSystem,field:'readCommunity')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="rsDatasource">rsDatasource:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsComputerSystem,field:'rsDatasource','errors')}">
                            <input type="text" id="rsDatasource" name="rsDatasource" value="${fieldValue(bean:rsComputerSystem,field:'rsDatasource')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="snmpAddress">snmpAddress:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsComputerSystem,field:'snmpAddress','errors')}">
                            <input type="text" id="snmpAddress" name="snmpAddress" value="${fieldValue(bean:rsComputerSystem,field:'snmpAddress')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="systemName">systemName:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsComputerSystem,field:'systemName','errors')}">
                            <input type="text" id="systemName" name="systemName" value="${fieldValue(bean:rsComputerSystem,field:'systemName')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="systemObjectID">systemObjectID:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsComputerSystem,field:'systemObjectID','errors')}">
                            <input type="text" id="systemObjectID" name="systemObjectID" value="${fieldValue(bean:rsComputerSystem,field:'systemObjectID')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="vendor">vendor:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsComputerSystem,field:'vendor','errors')}">
                            <input type="text" id="vendor" name="vendor" value="${fieldValue(bean:rsComputerSystem,field:'vendor')}"/>
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
