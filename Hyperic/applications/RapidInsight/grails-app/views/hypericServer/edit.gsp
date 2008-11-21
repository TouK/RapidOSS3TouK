

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Edit HypericServer</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLinkTo(dir:'')}">Home</a></span>
    <span class="menuButton"><g:link class="list" action="list">HypericServer List</g:link></span>
    <span class="menuButton"><g:link class="create" action="create">New HypericServer</g:link></span>
</div>
<div class="body">
    <h1>Edit HypericServer</h1>
    <g:if test="${flash.message}">
        <div class="message">${flash.message}</div>
    </g:if>
    <g:hasErrors bean="${hypericServer}">
        <div class="errors">
            <g:renderErrors bean="${hypericServer}" as="list"/>
        </div>
    </g:hasErrors>
    <g:hasErrors bean="${flash.errors}">
       <div class="errors">
            <g:renderErrors bean="${flash.errors}"/>
        </div>
    </g:hasErrors>
    <g:form method="post" >
        <input type="hidden" name="id" value="${hypericServer?.id}"/>
        <div class="dialog">
            <table>
                <tbody>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="username">username:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:hypericServer,field:'username','errors')}">
                            <input type="text" id="username" name="username" value="${fieldValue(bean:hypericServer,field:'username')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="event_timestamp">event_timestamp:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:hypericServer,field:'event_timestamp','errors')}">
                            <input type="text" id="event_timestamp" name="event_timestamp" value="${fieldValue(bean:hypericServer,field:'event_timestamp')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="password">password:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:hypericServer,field:'password','errors')}">
                            <input type="text" id="password" name="password" value="${fieldValue(bean:hypericServer,field:'password')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="relation_timestamp">relation_timestamp:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:hypericServer,field:'relation_timestamp','errors')}">
                            <input type="text" id="relation_timestamp" name="relation_timestamp" value="${fieldValue(bean:hypericServer,field:'relation_timestamp')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="status_timestamp">status_timestamp:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:hypericServer,field:'status_timestamp','errors')}">
                            <input type="text" id="status_timestamp" name="status_timestamp" value="${fieldValue(bean:hypericServer,field:'status_timestamp')}"/>
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
