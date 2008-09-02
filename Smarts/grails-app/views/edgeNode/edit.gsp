

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Edit EdgeNode</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLinkTo(dir:'')}">Home</a></span>
    <span class="menuButton"><g:link class="list" action="list">EdgeNode List</g:link></span>
    <span class="menuButton"><g:link class="create" action="create">New EdgeNode</g:link></span>
</div>
<div class="body">
    <h1>Edit EdgeNode</h1>
    <g:if test="${flash.message}">
        <div class="message">${flash.message}</div>
    </g:if>
    <g:hasErrors bean="${edgeNode}">
        <div class="errors">
            <g:renderErrors bean="${edgeNode}" as="list"/>
        </div>
    </g:hasErrors>
    <g:hasErrors bean="${flash.errors}">
       <div class="errors">
            <g:renderErrors bean="${flash.errors}"/>
        </div>
    </g:hasErrors>
    <g:form method="post" >
        <input type="hidden" name="id" value="${edgeNode?.id}"/>
        <div class="dialog">
            <table>
                <tbody>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="id">id:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:edgeNode,field:'id','errors')}">
                            <input type="text" id="id" name="id" value="${fieldValue(bean:edgeNode,field:'id')}" />
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="from">from:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:edgeNode,field:'from','errors')}">
                            <input type="text" id="from" name="from" value="${fieldValue(bean:edgeNode,field:'from')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="mapName">mapName:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:edgeNode,field:'mapName','errors')}">
                            <input type="text" id="mapName" name="mapName" value="${fieldValue(bean:edgeNode,field:'mapName')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="to">to:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:edgeNode,field:'to','errors')}">
                            <input type="text" id="to" name="to" value="${fieldValue(bean:edgeNode,field:'to')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="username">username:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:edgeNode,field:'username','errors')}">
                            <input type="text" id="username" name="username" value="${fieldValue(bean:edgeNode,field:'username')}"/>
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
