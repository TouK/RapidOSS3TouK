

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Create RsComputerSystemComponent</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLinkTo(dir:'')}">Home</a></span>
    <span class="menuButton"><g:link class="list" action="list">RsComputerSystemComponent List</g:link></span>
</div>
<div class="body">
    <h1>Create RsComputerSystemComponent</h1>
    <g:if test="${flash.message}">
        <div class="message">${flash.message}</div>
    </g:if>
    <g:hasErrors bean="${rsComputerSystemComponent}">
        <div class="errors">
            <g:renderErrors bean="${rsComputerSystemComponent}" as="list"/>
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
                        <td valign="top" class="value ${hasErrors(bean:rsComputerSystemComponent,field:'name','errors')}">
                            <input type="text" id="name" name="name" value="${fieldValue(bean:rsComputerSystemComponent,field:'name')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="computerSystemName">computerSystemName:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsComputerSystemComponent,field:'computerSystemName','errors')}">
                            <input type="text" id="computerSystemName" name="computerSystemName" value="${fieldValue(bean:rsComputerSystemComponent,field:'computerSystemName')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="creationClassName">creationClassName:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsComputerSystemComponent,field:'creationClassName','errors')}">
                            <input type="text" id="creationClassName" name="creationClassName" value="${fieldValue(bean:rsComputerSystemComponent,field:'creationClassName')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="description">description:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsComputerSystemComponent,field:'description','errors')}">
                            <input type="text" id="description" name="description" value="${fieldValue(bean:rsComputerSystemComponent,field:'description')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="displayName">displayName:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsComputerSystemComponent,field:'displayName','errors')}">
                            <input type="text" id="displayName" name="displayName" value="${fieldValue(bean:rsComputerSystemComponent,field:'displayName')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="isManaged">isManaged:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsComputerSystemComponent,field:'isManaged','errors')}">
                            <g:checkBox name="isManaged" value="${rsComputerSystemComponent?.isManaged}" ></g:checkBox>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="rsDatasource">rsDatasource:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsComputerSystemComponent,field:'rsDatasource','errors')}">
                            <input type="text" id="rsDatasource" name="rsDatasource" value="${fieldValue(bean:rsComputerSystemComponent,field:'rsDatasource')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="tag">tag:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsComputerSystemComponent,field:'tag','errors')}">
                            <input type="text" id="tag" name="tag" value="${fieldValue(bean:rsComputerSystemComponent,field:'tag')}"/>
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
