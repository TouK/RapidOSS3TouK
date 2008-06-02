<%@ page import="script.CmdbScript" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Show Script</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLinkTo(dir: '')}">Home</a></span>
    <span class="menuButton"><g:link class="list" action="list">Script List</g:link></span>
    <span class="menuButton"><g:link class="create" action="create">New Script</g:link></span>
    <span class="menuButton"><a class="logout" href="${createLinkTo(dir: 'auth/logout')}">Logout</a></span>
</div>
<div class="body">
    <h1>Show Script</h1>
    <g:if test="${flash.message}">
        <div class="message">${flash.message}</div>
    </g:if>
    <div class="dialog">
        <table>
            <tbody>

                <tr class="prop">
                    <td valign="top" class="name">Id:</td>

                    <td valign="top" class="value">${cmdbScript.id}</td>

                </tr>

                <tr class="prop">
                    <td valign="top" class="name">Name:</td>

                    <td valign="top" class="value">${cmdbScript.name}</td>

                </tr>

                <tr class="prop">
                    <td valign="top" class="name">Scheduled:</td>

                    <td valign="top" class="value">${cmdbScript.scheduled}</td>

                </tr>
                <%
                    if (cmdbScript.scheduled) {
                %>
                    <tr class="prop">
                        <td valign="top" class="name">Schedule Type:</td>

                        <td valign="top" class="value">${cmdbScript.scheduleType}</td>

                    </tr>

                    <tr class="prop">
                        <td valign="top" class="name">Start Delay:</td>

                        <td valign="top" class="value">${cmdbScript.startDelay}</td>
    
                    </tr>

                    <%
                      if(cmdbScript.scheduleType == CmdbScript.PERIODIC){

                    %>
                        <tr class="prop">
                            <td valign="top" class="name">Period:</td>

                            <td valign="top" class="value">${cmdbScript.period}</td>

                        </tr>
                    <%
                      }
                      else{
                     %>
                        <tr class="prop">
                            <td valign="top" class="name">Cron Expression:</td>

                            <td valign="top" class="value">${cmdbScript.cronExpression}</td>

                        </tr>
                    <%      
                      }
                    %>

                    <tr class="prop">
                        <td valign="top" class="name">Enabled:</td>

                        <td valign="top" class="value">${cmdbScript.enabled}</td>

                    </tr>
                <%
                    }
                %>

            </tbody>
        </table>
    </div>
    <div class="buttons">
        <g:form style="display:inline">
            <input type="hidden" name="id" value="${cmdbScript?.id}"/>
            <span class="button"><g:actionSubmit class="edit" value="Edit"/></span>
            <span class="button"><g:actionSubmit class="delete" onclick="return confirm('Are you sure?');" value="Delete"/></span>
        </g:form>
        <g:form style="display:inline">
            <input type="hidden" name="id" value="${cmdbScript?.name}"/>
            <span class="button"><g:actionSubmit class="refresh" value="Reload"/></span>
            <span class="button"><g:actionSubmit class="run" value="Run"/></span>
        </g:form>
    </div>
</div>
</body>
</html>
