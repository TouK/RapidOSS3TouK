

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Create SmartsIpNetwork</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLinkTo(dir:'')}">Home</a></span>
    <span class="menuButton"><g:link class="list" action="list">SmartsIpNetwork List</g:link></span>
</div>
<div class="body">
    <h1>Create SmartsIpNetwork</h1>
    <g:if test="${flash.message}">
        <div class="message">${flash.message}</div>
    </g:if>
    <g:hasErrors bean="${smartsIpNetwork}">
        <div class="errors">
            <g:renderErrors bean="${smartsIpNetwork}" as="list"/>
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
                        <td valign="top" class="value ${hasErrors(bean:smartsIpNetwork,field:'name','errors')}">
                            <input type="text" id="name" name="name" value="${fieldValue(bean:smartsIpNetwork,field:'name')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="className">className:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsIpNetwork,field:'className','errors')}">
                            <input type="text" id="className" name="className" value="${fieldValue(bean:smartsIpNetwork,field:'className')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="description">description:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsIpNetwork,field:'description','errors')}">
                            <input type="text" id="description" name="description" value="${fieldValue(bean:smartsIpNetwork,field:'description')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="displayName">displayName:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsIpNetwork,field:'displayName','errors')}">
                            <input type="text" id="displayName" name="displayName" value="${fieldValue(bean:smartsIpNetwork,field:'displayName')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="isManaged">isManaged:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsIpNetwork,field:'isManaged','errors')}">
                            <g:checkBox name="isManaged" value="${smartsIpNetwork?.isManaged}" ></g:checkBox>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="netmask">netmask:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsIpNetwork,field:'netmask','errors')}">
                            <input type="text" id="netmask" name="netmask" value="${fieldValue(bean:smartsIpNetwork,field:'netmask')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="networkNumber">networkNumber:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsIpNetwork,field:'networkNumber','errors')}">
                            <input type="text" id="networkNumber" name="networkNumber" value="${fieldValue(bean:smartsIpNetwork,field:'networkNumber')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="rsDatasource">rsDatasource:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:smartsIpNetwork,field:'rsDatasource','errors')}">
                            <input type="text" id="rsDatasource" name="rsDatasource" value="${fieldValue(bean:smartsIpNetwork,field:'rsDatasource')}"/>
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
