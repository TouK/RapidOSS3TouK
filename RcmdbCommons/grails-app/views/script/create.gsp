<%@ page import="datasource.RepositoryDatasource; datasource.BaseListeningDatasource; script.CmdbScript" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="adminLayout"/>
    <title>Create Script</title>
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
    var listenToRepositoryRow = document.getElementById('listenToRepositoryRow');
    var staticParamRow = document.getElementById('staticParamRow');
    var typeSelect = document.getElementById("type");
    var scriptType = typeSelect.options[typeSelect.selectedIndex].value;
    datasourceRow.style.display = (scriptType == "Listening"? "":"none");
    listenToRepositoryRow.style.display = (scriptType == "Listening"? "":"none");
    scheduleTypeRow.style.display = (scriptType == "Scheduled"? "":"none");
    startDelayRow.style.display = (scriptType == "Scheduled"? "":"none");
    periodRow.style.display = (scriptType == "Scheduled"? "":"none");
    cronRow.style.display = (scriptType == "Scheduled"? "":"none");
    enabledRow.style.display = (scriptType == "Scheduled"? "":"none");

    scheduleTypeChanged();
    listenToRepositoryChanged();
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
    function listenToRepositoryChanged(){
       var datasourceInput = document.getElementById('listeningDatasource');
       var listenToRepositoryInput = document.getElementById('listenToRepository');
       if(listenToRepositoryInput.checked){
        datasourceInput.disabled = true;
       }
       else{
         datasourceInput.disabled = false;
       }
    }
    </script>
</head>
<body onload="render()">
<div class="nav">
    <span class="menuButton"><g:link class="list" action="list">Script List</g:link></span>
</div>
<div class="body">
    <h1>Create Script</h1>
    <g:render template="/common/messages" model="[flash:flash, beans:[cmdbScript]]"></g:render>
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
                            <label for="scriptFile">Script File:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean: cmdbScript, field: 'scriptFile', 'errors')}">
                            <input type="text" class="inputtextfield" id="scriptFile" name="scriptFile" value="${fieldValue(bean: cmdbScript, field: 'scriptFile')}"/>
                        </td>
                    </tr>
                    <tr class="prop" id="logLevelRow">
                        <td valign="top" class="name">
                            <label for="logLevel">Log Level:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean: cmdbScript, field: 'logLevel', 'errors')}">
                            <g:select id="logLevel" name="logLevel" from="${cmdbScript.constraints.logLevel.inList.collect{it.encodeAsHTML()}}" value="${fieldValue(bean:cmdbScript,field:'logLevel')}"></g:select>
                        </td>
                    </tr>
                    <tr class="prop" id="logFileOwnRow">
                        <td valign="top" class="name">
                            <label for="logFileOwn">Use Own Log File:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean: cmdbScript, field: 'logFileOwn', 'errors')}">
                            <g:checkBox name="logFileOwn" value="${cmdbScript?.logFileOwn}"></g:checkBox>
                        </td>
                    </tr>

                    <tr class="prop" id="enabledForAllGroups">
                        <td valign="top" class="name">
                            <label for="enabledForAllGroups">Allow All To Execute:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean: cmdbScript, field: 'enabledForAllGroups', 'errors')}">
                            <g:checkBox name="enabledForAllGroups" value="${cmdbScript?.enabledForAllGroups}"></g:checkBox>
                        </td>
                    </tr>

                    <tr class="prop">
                        <td valign="top" class="name" colspan="2">
                            Allowed Groups:
                        </td>
                    </tr>
                    <tr>
                        <td valign="top" class="name" colspan="2">
                            <g:render template="/common/listToList" model="[id:'allowedGroups', inputName:'allowedGroups.id', valueProperty:'id', displayProperty:'name', fromListTitle:'Available Groups', toListTitle:'Allowed Groups', fromListContent:availableGroups, toListContent:cmdbScript?.allowedGroups]"></g:render>
                        </td>
                    </tr>

                    <tr class="prop" id="staticParamRow">
                        <td valign="top" class="name">
                            <label for="staticParam">Static Parameter:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean: cmdbScript, field: 'staticParam', 'errors')}">
                            <input type="text" class="inputtextfield" id="staticParam" name="staticParam" value="${fieldValue(bean: cmdbScript, field: 'staticParam')}"/>
                        </td>
                    </tr>
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="type">Type:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean: cmdbScript, field: 'type', 'errors')}">
                            <g:select class="inputtextfield" id="type" name="type" from="${cmdbScript.constraints.type.inList.collect{it.encodeAsHTML()}}"
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

                    <tr class="prop" id="periodRow">
                        <td valign="top" class="name">
                            <label for="period">Period:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean: cmdbScript, field: 'period', 'errors')}">
                            <input type="text" id="period" name="period" value="${fieldValue(bean: cmdbScript, field: 'period')}"/>
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

                    <tr class="prop" id="enabledRow">
                        <td valign="top" class="name">
                            <label for="enabled">Enabled:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean: cmdbScript, field: 'enabled', 'errors')}">
                            <g:checkBox name="enabled" value="${cmdbScript?.enabled}"></g:checkBox>
                        </td>
                    </tr>
                    <tr class="prop" id="listenToRepositoryRow">
                        <td valign="top" class="name">
                            <label for="listenToRepository">Listen To RapidInsight Repository:</label>
                        </td>
                        <td valign="top">
                            <g:checkBox id="listenToRepository" name="listenToRepository" value="${cmdbScript?.listeningDatasource instanceof RepositoryDatasource}" onclick="listenToRepositoryChanged()"></g:checkBox>
                        </td>
                    </tr>

                    <tr class="prop" id="datasourceRow">
                        <td valign="top" class="name">
                            <label for="listeningDatasource">Datasource:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean: cmdbScript, field: 'listeningDatasource', 'errors')}">
                            <%
                                def dsList = BaseListeningDatasource.list().findAll{!(it instanceof RepositoryDatasource)}
                            %>
                            <g:select id="listeningDatasource" optionKey="id" from="${dsList}" name="listeningDatasource.id" value="${cmdbScript?.listeningDatasource?.id}" noSelection="['null':'']"></g:select>
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
