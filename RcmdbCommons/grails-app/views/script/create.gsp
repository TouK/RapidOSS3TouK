<%@ page import="script.CmdbScript" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Create Script</title>
    <script>
        function render(){
            scheduledChanged();
        }
        function scheduledChanged(){
            var scheduledBox = document.getElementById('scheduled');
            var scheduleTypeSelect = document.getElementById('scheduleType');
            var startDelayInput = document.getElementById('startDelay');
            var periodInput = document.getElementById('period');
            var cronInput = document.getElementById('cronExpression');
            var enabledInput = document.getElementById('enabled');
            scheduleTypeSelect.disabled = !scheduledBox.checked;
            startDelayInput.disabled = !scheduledBox.checked;
            periodInput.disabled = !scheduledBox.checked;
            cronInput.disabled = !scheduledBox.checked;
            enabledInput.disabled = !scheduledBox.checked;
            scheduleTypeChanged();
        }

        function scheduleTypeChanged(){
            var scheduleTypeSelect = document.getElementById('scheduleType');
            var periodInput = document.getElementById('period');
            var cronInput = document.getElementById('cronExpression');
            if(!scheduleTypeSelect.disabled){
                var scheduleType = scheduleTypeSelect.options[scheduleTypeSelect.selectedIndex].value;
                if(scheduleType == 'Cron'){
                    periodInput.disabled = true;
                    periodInput.value = "1";
                    cronInput.disabled = false;
                }
                else{
                    periodInput.disabled = false;
                    cronInput.disabled = true;
                    cronInput.value = "* * * * * ?";
                }
            }
        }
    </script>
</head>
<body onload="render()">
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLinkTo(dir: 'admin.gsp')}">Home</a></span>
    <span class="menuButton"><g:link class="list" action="list">Script List</g:link></span>
    <span class="menuButton"><a class="logout" href="${createLinkTo(dir: 'auth/logout')}">Logout</a></span>
</div>
<div class="body">
    <h1>Create Script</h1>
    <g:if test="${flash.message}">
        <div class="message">${flash.message}</div>
    </g:if>
    <g:if test="${flash.errors}">
        <div class="errors">
            <ul>
                <g:each var="error" in="${flash?.errors}">
                    <li>${error}</li>
                </g:each>
            </ul>
        </div>
    </g:if>
    <g:hasErrors bean="${cmdbScript}">
        <div class="errors">
            <g:renderErrors bean="${cmdbScript}" as="list"/>
        </div>
    </g:hasErrors>
    <g:form action="save" method="post">
        <div class="dialog">
            <table>
                <tbody>

                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="name">Name:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean: cmdbScript, field: 'name', 'errors')}">
                            <input type="text" class="inputtextfield" id="name" name="name" value="${fieldValue(bean: cmdbScript, field: 'name')}"/>
                        </td>
                    </tr>

                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="scheduled">Scheduled:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean: cmdbScript, field: 'scheduled', 'errors')}">
                            <g:checkBox name="scheduled" value="${cmdbScript?.scheduled}" onclick="scheduledChanged()"></g:checkBox>
                        </td>
                    </tr>
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="scheduleType">Schedule Type:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean: cmdbScript, field: 'scheduleType', 'errors')}">
                            <g:select id="scheduleType" name="scheduleType" from="${cmdbScript.constraints.scheduleType.inList.collect{it.encodeAsHTML()}}" value="${fieldValue(bean:cmdbScript,field:'scheduleType')}" onchange="scheduleTypeChanged()"></g:select>
                        </td>
                    </tr>

                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="startDelay">Start Delay:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean: cmdbScript, field: 'startDelay', 'errors')}">
                            <input type="text" id="startDelay" name="startDelay" value="${fieldValue(bean: cmdbScript, field: 'startDelay')}"/>
                        </td>
                    </tr>

                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="period">Period:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean: cmdbScript, field: 'period', 'errors')}">
                            <input type="text" id="period" name="period" value="${fieldValue(bean: cmdbScript, field: 'period')}"/>
                        </td>
                    </tr>

                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="cronExpression">Cron Expression:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean: cmdbScript, field: 'cronExpression', 'errors')}">
                            <input type="text" id="cronExpression" name="cronExpression" value="${fieldValue(bean: cmdbScript, field: 'cronExpression')}"/>
                        </td>
                    </tr>

                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="enabled">Enabled:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean: cmdbScript, field: 'enabled', 'errors')}">
                            <g:checkBox name="enabled" value="${cmdbScript?.enabled}"></g:checkBox>
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
