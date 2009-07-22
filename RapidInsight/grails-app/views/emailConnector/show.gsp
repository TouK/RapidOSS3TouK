<%@ page import="connector.EmailConnector" %>

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="adminLayout"/>
    <title>Show EmailConnector</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLinkTo(dir:'')}">Home</a></span>
    <span class="menuButton"><g:link class="list" action="list">EmailConnector List</g:link></span>
    <span class="menuButton"><g:link class="create" action="create">New EmailConnector</g:link></span>
</div>
<div class="body">
    <h1>Show EmailConnector</h1>
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

                    <td valign="top" class="value">${emailConnector.name}</td>

                </tr>



                <tr class="prop">
                    <td valign="top" class="name">smtpHost:</td>

                    <td valign="top" class="value">${emailConnector.ds.connection.smtpHost}</td>

                </tr>

                <tr class="prop">
                    <td valign="top" class="name">smtpPort:</td>

                    <td valign="top" class="value">${emailConnector.ds.connection.smtpPort}</td>

                </tr>
                <tr class="prop">
                    <td valign="top" class="name">Protocol:</td>

                    <td valign="top" class="value">${emailConnector.ds.connection.protocol}</td>

                </tr>

                 <tr class="prop">
                    <td valign="top" class="name">Username:</td>

                    <td valign="top" class="value">${emailConnector.ds.connection.username}</td>

                </tr>

                <tr class="prop">
                    <td valign="top" class="name">Password:</td>

                    <td valign="top" class="value">${emailConnector.ds.connection.userPassword}</td>

                </tr>



                <tr class="prop">
                    <td valign="top" class="name">Min Timeout:</td>

                    <td valign="top" class="value">${emailConnector.ds.connection?.minTimeout}</td>

                </tr>
                <tr class="prop">
                    <td valign="top" class="name">Max Timeout:</td>

                    <td valign="top" class="value">${emailConnector.ds.connection?.maxTimeout}</td>

                </tr>

            </tbody>
        </table>
    </div>
    <div class="buttons">
        <g:form>
            <input type="hidden" name="id" value="${emailConnector?.id}"/>
            <span class="button"><g:actionSubmit class="edit" value="Edit"/></span>
            <span class="button"><g:actionSubmit class="delete" onclick="return confirm('Are you sure?');" value="Delete"/></span>
        </g:form>
    </div>
</div>
</body>
</html>
