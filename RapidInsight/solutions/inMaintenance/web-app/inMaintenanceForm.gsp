<style type="text/css">
#maintenanceFormCalendarDlg .bd form {
clear:left;
}
/* Have calendar squeeze upto bd bounding box */
#maintenanceFormCalendarDlg .bd {
padding:0;
}

#maintenanceFormCalendarDlg .hd {
text-align:left;
}

/* Center buttons in the footer */
#maintenanceFormCalendarDlg .ft .button-group {
text-align:center;
}

/* Prevent border-collapse:collapse from bleeding through in IE6, IE7 */
#maintenanceFormCalendarDlg_c.yui-overlay-hidden table {
*display:none;
}
/* Remove calendar's border and set padding in ems instead of px, so we can specify an width in ems for the container */
#maintenanceFormCalendar {
border:none;
padding:1em;
}
#maintenanceSchedulesTable .yui-dt-data .delete .yui-dt-liner{
background: url('${createLinkTo(dir: "images/rapidjs/component/tools", file: "cross.png")}') no-repeat center;
cursor:pointer;
}

</style>
<div class="yui-navset yui-navset-top" id="inMaintenanceForm">
    <ul class="yui-nav">
        <li class="selected"><a><em>In Maintenance</em></a></li>
        <li><a><em>Schedule</em></a></li>
    </ul>
    <div style="display:block;margin-top:10px;" class="view">
        <form>
            <table>
                <tr class="prop"><td valign="top" class="name"><label>Object Name:</label></td><td valign="top"><input type="textbox" name="objectName"/></td></tr>
                <tr class="prop"><td valign="top" class="name"><label>In Maintenance:</label></td><td valign="top"><input type="checkbox" name="inMaintenance"/></td></tr>
                <tr class="prop"><td valign="top" class="name"><label>For:</label></td><td valign="top"><input type="textbox" name="minutes" value=""/> Minutes</td></tr>
                <tr class="prop"><td valign="top" class="name"><label>Info:</label></td><td valign="top"><textarea rows="2" cols="60" name="info"></textarea></td></tr>
            </table>
            <input type="hidden" name="maintenanceType" value="maintenance">
        </form>
    </div>
    <div style="display:block;margin-top:5px;display:none" class="view">
        <div>Schedules:</div>
        <div id="maintenanceSchedulesTable"></div>
        <form>
            <input type="hidden" name="objectName">
            <input type="hidden" name="maintenanceType" value="schedule">
            <table>
                <tr class="prop"><td valign="top" class="name"><label>Schedule type:</label></td><td valign="top"><select name="scheduleType">
                    <option value="1">Once</option>
                    <option value="2">Daily</option>
                    <option value="3">Weekly</option>
                    <option value="4">Monthly</option>
                </select></td></tr>
                <tr class="prop"><td valign="top" class="name"><label>Info:</label></td><td valign="top"><textarea rows="2" cols="60" name="info"></textarea></td></tr>
            </table>
            <div class="scheduleType">
                <table><tr class="prop"><td valign="top" class="name"><label>Starting:</label></td>
                    <td valign="top">
                        <input name="starting"/>
                        <button type="button" style="padding:0" class="calendarButton"><img src="${createLinkTo(dir: 'images/rapidjs/component/tools', file: 'calendar.png')}" width="16px" height="16px"/></button>
                        <select name="starting_hour"></select>:<select name="starting_minute"></select>
                    </td>
                </tr>
                    <tr class="prop"><td valign="top" class="name"><label>Ending:</label></td>
                        <td valign="top">
                            <input name="ending"/>
                            <button type="button" style="padding:0" class="calendarButton"><img src="${createLinkTo(dir: 'images/rapidjs/component/tools', file: 'calendar.png')}" width="16px" height="16px"/></button>
                            <select name="ending_hour"></select>:<select name="ending_minute"></select>
                        </td>
                    </tr></table>
            </div>
            <table style="margin-top:5">
                <tr>
                    <td valign="top"><div class="scheduleType" style="display:none">
                        <div style="padding:5;color:#083772;font-weight:bold">Maintenace interval:</div>
                        <table><tr class="prop"><td valign="top" class="name"><label>Starting:</label></td>
                            <td valign="top">
                                <select name="maintStarting_hour"></select>:<select name="maintStarting_minute"></select>
                            </td>
                        </tr>
                            <tr class="prop"><td valign="top" class="name"><label>Ending:</label></td>
                                <td valign="top">
                                    <select name="maintEnding_hour"></select>:<select name="maintEnding_minute"></select>
                                </td>
                            </tr></table>
                        <div style="padding:5;color:#083772;font-weight:bold">How long the maintenance is scheduled:</div>
                        <table><tr class="prop"><td valign="top" class="name"><label>Starting:</label></td>
                            <td valign="top">
                                <input name="schedStarting"/>
                                <button type="button" style="padding:0" class="calendarButton"><img src="${createLinkTo(dir: 'images/rapidjs/component/tools', file: 'calendar.png')}" width="16px" height="16px"/></button>
                            </td>
                        </tr>
                            <tr class="prop"><td valign="top" class="name"><label>Ending:</label></td>
                                <td valign="top">
                                    <input name="schedEnding" style="width:100px"/>
                                    <button type="button" style="padding:0" class="calendarButton"><img src="${createLinkTo(dir: 'images/rapidjs/component/tools', file: 'calendar.png')}" width="16px" height="16px"/></button>
                                </td>
                            </tr></table>
                    </div></td>
                    <td valign="top"><div>
                        <div class="scheduleType" style="display:none"></div>
                        <div class="scheduleType" style="display:none;padding-left:10">
                            <div style="padding:5;color:#083772;font-weight:bold">Every:</div>
                            <select multiple="multiple" name="daysOfWeek" style="width:130;height:150">
                                <option value="1">Sunday</option>
                                <option value="2" selected="true">Monday</option>
                                <option value="3">Tuesday</option>
                                <option value="4">Wednesday</option>
                                <option value="5">Thursday</option>
                                <option value="6">Friday</option>
                                <option value="7">Saturday</option>
                            </select>
                        </div>
                        <div class="scheduleType" style="display:none;padding-left:10">
                            <div style="padding:5;color:#083772;font-weight:bold">Every month on the:</div>
                            <select multiple="multiple" name="daysOfMonth" style="width:130;height:150">
                                <option value="1" selected="true">1st day</option><option value="2">2nd day</option>
                                <option value="3">3rd day</option><option value="4">4th day</option>
                                <option value="5">5th day</option><option value="6">6th day</option>
                                <option value="7">7th day</option><option value="8">8th day</option>
                                <option value="9">9th day</option><option value="10">10th day</option>
                                <option value="11">11th day</option><option value="12">12th day</option>
                                <option value="13">13th day</option><option value="14">14th day</option>
                                <option value="15">15th day</option><option value="16">16th day</option>
                                <option value="17">17th day</option><option value="18">18th day</option>
                                <option value="19">19th day</option><option value="20">20th day</option>
                                <option value="21">21th day</option><option value="22">22th day</option>
                                <option value="23">23th day</option><option value="24">24th day</option>
                                <option value="25">25th day</option><option value="26">26th day</option>
                                <option value="27">27th day</option><option value="28">28th day</option>
                                <option value="29">29th day</option><option value="30">30th day</option>
                                <option value="31">31th day</option>
                            </select>
                        </div>
                    </div></td>
                    <td valign="top">
                        <div class="scheduleType" style="display:none;padding-left:10">
                            <div style="padding:5;color:#083772;font-weight:bold;text-decoration:underline;cursor:pointer">Calculate fire times:</div>
                            <select multiple="multiple" style="width:155;height:150"></select>
                        </div>
                    </td>
                </tr>
            </table>
        </form>
    </div>
</div>
<script type="text/javascript" src="${createLinkTo(file: 'inMaintenance.js')}"></script>