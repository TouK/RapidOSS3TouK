<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="adminLayout" />
    <title>Show HypericConnector</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLinkTo(dir:'/admin.gsp')}">Home</a></span>
    <span class="menuButton"><g:link class="list" action="list">HypericConnector List</g:link></span>
    <span class="menuButton"><g:link class="create" action="create">New HypericConnector</g:link></span>
</div>
<div class="body">
    <h1>Show HypericConnector</h1>
    <g:if test="${flash.message}">
        <div class="message">${flash.message}</div>
    </g:if>
    <g:hasErrors bean="${flash.errors}">
       <div class="errors">
            <g:renderErrors bean="${flash.errors}"/>
        </div>
    </g:hasErrors>
    <div class="dialog">
        <table>
            <tbody>

                <tr class="prop">
                    <td valign="top" class="name">Name:</td>
                    
                    <td valign="top" class="value">${hypericConnector.name}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">Hyperic Connection:</td>
                    
                    <td valign="top" class="value"><g:link controller="hypericConnection" action="show" id="${hypericConnector?.connection?.id}">${hypericConnector?.connection}</g:link></td>
                    
                </tr>
                <tr class="prop">
                    <td valign="top" class="name">Type:</td>

                    <td valign="top" class="value">${hypericConnector?.type}</td>

                </tr>
                <tr class="prop">
                    <td valign="top" class="name">Period:</td>

                    <td valign="top" class="value">${hypericConnector?.script?.period}</td>

                </tr>

                <tr class="prop">
                    <td valign="top" class="name">Log Level:</td>
                    <td valign="top" class="value">${hypericConnector?.script?.logLevel}</td>
                </tr>
                
            </tbody>
        </table>
    </div>
    <div class="buttons">
        <g:form>
            <input type="hidden" name="id" value="${hypericConnector?.id}"/>
            <span class="button"><g:actionSubmit class="edit" value="Edit"/></span>
            <span class="button"><g:actionSubmit class="delete" onclick="return confirm('Are you sure?');" value="Delete"/></span>
        </g:form>
    </div>
</div>
</body>
</html>
