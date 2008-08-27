<%@ page import="datasource.BaseListeningDatasource; script.CmdbScript" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <title>Edit Script</title>
    <script>
    function render(){
        typeChanged();
    }
    function typeChanged(){
        var scheduleTypeRow = document.getElementById('scheduleTypeRow');
        var startDelayRow = document.getElementById('startDelayRow');
        var periodRow = document.getElementById('periodRow');
        var cronRow = document.getElementById('cronExpressionRow');
        var enabledRow = document.getElementById('enabledRow');
        var datasourceRow = document.getElementById('datasourceRow');
        var typeSelect = document.getElementById("type");
        var scriptType = typeSelect.options[typeSelect.selectedIndex].value;
        datasourceRow.style.display = (scriptType == "Listening"? "":"none");
        scheduleTypeRow.style.display = (scriptType == "Scheduled"? "":"none");
        startDelayRow.style.display = (scriptType == "Scheduled"? "":"none");
        periodRow.style.display = (scriptType == "Scheduled"? "":"none");
        cronRow.style.display = (scriptType == "Scheduled"? "":"none");
        enabledRow.style.display = (scriptType == "Scheduled"? "":"none");
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
    <span class="menuButton"><g:link class="create" action="create">New Script</g:link></span>
</div>
<div class="body">
    <h1>Edit Script</h1>
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
    <g:form method="post">
        <input type="hidden" name="id" value="${cmdbScript?.id}"/>
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
                            <label for="scriptFile">Script File:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean: cmdbScript, field: 'scriptFile', 'errors')}">
                            <input type="text" class="inputtextfield" id="scriptFile" name="scriptFile" value="${fieldValue(bean: cmdbScript, field: 'scriptFile')}"/>
                        </td>
                    </tr>

                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="type">Type:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:cmdbScript,field:'type','errors')}">
                            <g:select  class="inputtextfield" id="type" name="type" from="${cmdbScript.constraints.type.inList.collect{it.encodeAsHTML()}}"
                                    value="${fieldValue(bean:cmdbScript,field:'type')}" onchange="typeChanged()"></g:select>
                        </td>
                    </tr>

                    <tr class="prop" id="scheduleTypeRow">
                        <td valign="top" class="name">
                            <label for="scheduleType">Schedule Type:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean: cmdbScript, field: 'scheduleType', 'errors')}">
                            <g:select id="scheduleType" name="scheduleType" from="${cmdbScript.constraints.scheduleType.inList.collect{it.encodeAsHTML()}}" value="${fieldValue(bean:cmdbScript,field:'scheduleType')}" onchange="scheduleTypeChanged()"></g:select>
                        </td>
                    </tr>

                    <tr class="prop" id="startDelayRow">
                        <td valign="top" class="name">
                            <label for="startDelay">Start Delay:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean: cmdbScript, field: 'startDelay', 'errors')}">
                            <input type="text" id="startDelay" name="startDelay" value="${fieldValue(bean: cmdbScript, field: 'startDelay')}"/>
                        </td>
                    </tr>

                    <tr class="prop" id="cronExpressionRow">
                        <td valign="top" class="name">
                            <label for="cronExpression">Cron Expression:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean: cmdbScript, field: 'cronExpression', 'errors')}">
                            <input type="text" id="cronExpression" name="cronExpression" value="${fieldValue(bean: cmdbScript, field: 'cronExpression')}"/>
                        </td>
                    </tr>

                    <tr class="prop" id="periodRow">
                        <td valign="top" class="name">
                            <label for="period">Period:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean: cmdbScript, field: 'period', 'errors')}">
                            <input type="text" id="period" name="period" value="${fieldValue(bean: cmdbScript, field: 'period')}"/>
                        </td>
                    </tr>

                    <tr class="prop" id="enabledRow">
                        <td valign="top" class="name">
                            <label for="enabled">Enabled:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean: cmdbScript, field: 'enabled', 'errors')}">
                            <g:checkBox name="enabled" value="${cmdbScript?.enabled}"></g:checkBox>
                        </td>
                    </tr>

                    <tr class="prop" id="datasourceRow">
                        <td valign="top" class="name">
                            <label for="listeningDatasource">Datasource:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:cmdbScript,field:'listeningDatasource','errors')}">
                            <g:select optionKey="id" from="${BaseListeningDatasource.list()}" name="listeningDatasource.id" value="${cmdbScript?.listeningDatasource?.id}" noSelection="['null':'']"></g:select>
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
