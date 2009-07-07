<%@ page import="connection.EmailConnection" %>

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="adminLayout" />
    <title>Create EmailConnection</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLinkTo(dir:'')}">Home</a></span>
    <span class="menuButton"><g:link class="list" action="list">EmailConnection List</g:link></span>
</div>
<div class="body">
    <h1>Create EmailConnection</h1>
    <g:if test="${flash.message}">
        <div class="message">${flash.message}</div>
    </g:if>
    <g:hasErrors bean="${emailConnection}">
        <div class="errors">
            <g:renderErrors bean="${emailConnection}" as="list"/>
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
                                    <label for="name">Name:</label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean:emailConnection,field:'name','errors')}">
                                    <input type="text" class="inputtextfield" id="name" name="name" value="${fieldValue(bean:emailConnection,field:'name')}"/>
                                </td>
                            </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="smtpHost">smtpHost:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:emailConnection,field:'smtpHost','errors')}">
                            <input type="text" class="inputtextfield" id="smtpHost" name="smtpHost" value="${fieldValue(bean:emailConnection,field:'smtpHost')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="smtpPort">smtpPort:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:emailConnection,field:'smtpPort','errors')}">
                            <input type="text" class="inputtextfield" id="smtpPort" name="smtpPort" value="${fieldValue(bean:emailConnection,field:'smtpPort')}"/>
                        </td>
                    </tr>

                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="protocol">Protocol:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:emailConnection,field:'protocol','errors')}">
                            <g:select id="protocol" name="protocol" from="${emailConnection.constraints.protocol.inList.collect{it.encodeAsHTML()}}" value="${fieldValue(bean:emailConnection,field:'protocol')}"></g:select>
                        </td>
                    </tr>
                    

                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="username">Username:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:emailConnection,field:'username','errors')}">
                            <input type="text" class="inputtextfield" id="username" name="username" value="${fieldValue(bean:emailConnection,field:'username')}" autocomplete="off" />
                        </td>
                    </tr>

                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="userPassword">Password:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:emailConnection,field:'userPassword','errors')}">
                            <input type="text" class="inputtextfield" id="userPassword" name="userPassword" value="${fieldValue(bean:emailConnection,field:'userPassword')}" autocomplete="off" />
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="minTimeout">Min Timeout:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:emailConnection,field:'minTimeout','errors')}">
                            <input type="text" class="inputtextfield" id="minTimeout" name="minTimeout" value="${fieldValue(bean:emailConnection,field:'minTimeout')}"/>
                        </td>
                    </tr>

                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="maxTimeout">Max Timeout:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:emailConnection,field:'maxTimeout','errors')}">
                            <input type="maxTimeout" class="inputtextfield" id="maxTimeout" name="maxTimeout" value="${fieldValue(bean:emailConnection,field:'maxTimeout')}"/>
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
