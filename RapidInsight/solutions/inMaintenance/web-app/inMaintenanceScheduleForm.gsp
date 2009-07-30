<%
    def mode = params.mode;
    def componentId = params.componentId

    def objectName = params.name?params.name:'';
    def schedules=RsInMaintenanceSchedule.searchEvery("objectName:${objectName.exactQuery()}",[sort:"starting",order:"asc"]);

    def currentYear=Calendar.getInstance().get(Calendar.YEAR);
    def calendarYears=[currentYear,currentYear+1];

%>
<div class="yui-navset yui-navset-top" style="margin-top:5px">
    <ul class="yui-nav">
        <li >
            <a onclick="YAHOO.rapidjs.Components['${componentId}'].show(createURL('inMaintenanceForm.gsp', {name:'${objectName}'}));">
                <em>In Maintenance</em>
            </a>
        </li>
        <li class="selected">
            <a onclick="YAHOO.rapidjs.Components['${componentId}'].show(createURL('inMaintenanceScheduleForm.gsp', {name:'${objectName}'}));">
                <em>Schedule</em>
            </a>
        </li>
    </ul>
    <div style="display:block;margin-top:10px;">


        <rui:formRemote method="POST" action="script/run/putInMaintenance?format=xml" componentId="${params.componentId}" >
            <table>
                <tr><td colspan="2"><span class="r-buttontoolbar-text">New Schedule</span></td></tr>
                <tr><td width="20%"><label>Object Name:</label></td><td width="80%"><input type="textbox" name="objectName" style="width:100px" value="${objectName.encodeAsHTML()}"/></td></tr>
                <tr><td width="20%"><label>Start Time:</label></td><td width="80%"><g:datePicker name="startTime" value="${new Date()}" precision="minute" years="${calendarYears}"/></td></tr>
                <tr><td width="20%"><label>End Time:</label></td><td width="80%"><g:datePicker name="endTime" value="${new Date()}" precision="minute" years="${calendarYears}"/></td></tr>                
                <tr><td width="20%" valign="top"><label>Info:</label></td><td width="80%"><textarea rows="2" cols="60" name="info"></textarea></td></tr>
            </table>
            <input type="hidden" name="model" value="schedule"/>
            <input type="hidden" name="mode" value="create"/>
        </rui:formRemote>


        <script type="text/javascript">
        window.refreshScheduleDialog = function(){
            YAHOO.rapidjs.Components['${componentId}'].show(createURL('inMaintenanceScheduleForm.gsp', {name:'${objectName}'}));
        }
        </script>
        <g:if test="${schedules.size()>0}">
            <g:each var="schedule" in="${schedules}">
                <rui:formRemote method="POST" action="script/run/putInMaintenance?format=xml" componentId="${params.componentId}" formId="scheduleform_${schedule.id}" useDefaultButtons="false" onSuccess="window.refreshScheduleDialog">
                <input type="hidden" name="model" value="schedule"/>
                <input type="hidden" name="mode" value="delete"/>
                <input type="hidden" name="scheduleid" value="${schedule.id}"/>
                </rui:formRemote>
            </g:each>

            <table width="100%">
            <tr><td colspan="5"><span class="r-buttontoolbar-text">Existing Schedules</span></td></tr>
            <g:each var="schedule" in="${schedules}">
                <tr><td colspan="7"><b>Info :</b> ${schedule.info}</td></tr>
                <tr>
                    <td><b>Starting:</b></td>
                    <td>${schedule.starting}</td>
                    <td><b>Ending:</b></td>
                    <td>${schedule.ending}</td>
                    <td><b>Active:</b></td>
                    <td>${schedule.active}</td>
                    <td>                        
                        <span class="yui-button yui-push-button default">
                            <span class="first-child">                                
                                <button type="button" onclick="YAHOO.rapidjs.Components['scheduleform_${schedule.id}'].submit()">Delete</button>
                            </span>
                        </span>
                    </td>
                </tr>
                <tr><td colspan="5" height="3"></td></tr>
            </g:each>
            </table>
        </g:if>
    </div>
</div>
