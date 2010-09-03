<%@ page import="auth.Role; java.text.SimpleDateFormat; message.RsMessageRuleCalendar" %>
<%--
  Created by IntelliJ IDEA.
  User: Sezgin Kucukkaraaslan
  Date: Mar 16, 2010
  Time: 2:26:21 PM
--%>
<%
    RsMessageRuleCalendar rsMessageRuleCalendar = new RsMessageRuleCalendar();
    def startingHour = 0;
    def startingMinute = 0;
    def endingHour = 0;
    def endingMinute = 0;
    def days = [];
    def exceptionDays = [];
    def dayDisplays = ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat']
    def actionUrl = "rsMessageRuleCalendar/save"
    if (params.mode == 'edit') {
        rsMessageRuleCalendar = RsMessageRuleCalendar.get(id: params.calendarId)
        if (!rsMessageRuleCalendar) {
            println "No RsMessageRuleCalendar exists with id ${params.calendarId}"
            return;
        }

        def startingTimeMap=RsMessageRuleCalendar.getHourAndMinuteFromTime(rsMessageRuleCalendar.starting);
        startingHour=startingTimeMap.hour;
        startingMinute=startingTimeMap.minute;
        def endingTimeMap=RsMessageRuleCalendar.getHourAndMinuteFromTime(rsMessageRuleCalendar.ending);
        endingHour=endingTimeMap.hour;
        endingMinute=endingTimeMap.minute;
        
        actionUrl = "rsMessageRuleCalendar/update"
        if (rsMessageRuleCalendar.exceptions != "") exceptionDays = Arrays.asList(rsMessageRuleCalendar.exceptions.split(','))
        if (rsMessageRuleCalendar.days != "") days = Arrays.asList(rsMessageRuleCalendar.days.split(','))
    }
%>
<script type="text/javascript">
    window.refreshCalendars = function(){
        var dataComponent = YAHOO.rapidjs.Components['calendars'];
        dataComponent.poll();
    }
    window.removeSelectedExceptions = function(){
        var exceptionsEl = document.getElementById('calendarExceptions');
        var i;
        for (i = exceptionsEl.length - 1; i>=0; i--) {
            if (exceptionsEl.options[i].selected) {
                exceptionsEl.remove(i);
            }
        }
        var inputValueArray = [];
        var options =exceptionsEl.options
        for(var i=0; i< options.length; i++){
            inputValueArray.push(options[i].text)
        }
        document.getElementById('calendarExceptionsInput').value = inputValueArray.join(',');
    }
</script>
<div>
    <rui:formRemote componentId="${params.componentId}" onSuccess="window.refreshCalendars" method="POST" action="${actionUrl}">
        <table>
            <tr><td valign="top">
                <table>
                    <tr class="prop">
                        <td valign="top" class="name"><label>Name:</label></td>
                        <td valign="top"><input type="text" name="name" value="${fieldValue(bean: rsMessageRuleCalendar, field: 'name')}" style="width:140px"/></td>
                    </tr>
                    <tr class="prop">
                        <td valign="top" class="name"><label>Start Time:</label></td>
                        <td valign="top">
                            <select name="startingHour">
                                <g:each var="hour" in="${(0..<24)}">
                                    <g:if test="${startingHour == hour}">
                                        <option value="${hour}" selected="true">${hour < 10 ? '0' + hour : hour}</option>
                                    </g:if>
                                    <g:else>
                                        <option value="${hour}">${hour < 10 ? '0' + hour : hour}</option>
                                    </g:else>
                                </g:each>
                            </select>:
                            <select name="startingMinute">
                                <g:each var="minute" in="${(0..<60)}">
                                    <g:if test="${startingMinute == minute}">
                                        <option value="${minute}" selected="true">${minute < 10 ? '0' + minute : minute}</option>
                                    </g:if>
                                    <g:else>
                                        <option value="${minute}">${minute < 10 ? '0' + minute : minute}</option>
                                    </g:else>
                                </g:each>
                            </select>
                        </td>
                    </tr>
                    <tr class="prop">
                        <td valign="top" class="name"><label>End Time:</label></td>
                        <td valign="top">
                            <select name="endingHour">
                                <g:each var="hour" in="${(0..<24)}">
                                    <g:if test="${endingHour == hour}">
                                        <option value="${hour}" selected="true">${hour < 10 ? '0' + hour : hour}</option>
                                    </g:if>
                                    <g:else>
                                        <option value="${hour}">${hour < 10 ? '0' + hour : hour}</option>
                                    </g:else>
                                </g:each>
                            </select>:
                            <select name="endingMinute">
                                <g:each var="minute" in="${(0..<60)}">
                                    <g:if test="${endingMinute == minute}">
                                        <option value="${minute}" selected="true">${minute < 10 ? '0' + minute : minute}</option>
                                    </g:if>
                                    <g:else>
                                        <option value="${minute}">${minute < 10 ? '0' + minute : minute}</option>
                                    </g:else>
                                </g:each>
                            </select>
                        </td>
                    </tr>
                    <tr class="prop">
                        <td valign="top" class="name"><label>On:</label></td>
                        <td valign="top"><select name="days" multiple="true" style="width:140px" size="7">
                            <g:each var="day" in="${dayDisplays}" status="i">
                                <g:if test="${days.contains((i + 1).toString())}">
                                    <option value="${i + 1}" selected="true">${day}</option>
                                </g:if>
                                <g:else>
                                    <option value="${i + 1}">${day}</option>
                                </g:else>
                            </g:each>
                        </select></td>
                    </tr>
                    <jsec:hasRole name="${Role.ADMINISTRATOR}">
                        <tr class="prop">
                            <td valign="top" class="name"><label>Public:</label></td>
                            <td valign="top"><g:checkBox name="isPublic" value="${rsMessageRuleCalendar.isPublic}"></g:checkBox></td>
                        </tr>
                    </jsec:hasRole>
                </table>
            </td>
                <td valign="top">
                    <table>
                        <tr class="prop">
                            <td valign="top" class="name"><label>Exceptions:</label></td>
                            <td valign="top"><select name="exceptionDays" multiple="true" style="width:120px;height:100px;overflow:auto" id="calendarExceptions">
                                <g:each var="exceptionDay" in="${exceptionDays}">
                                    <option value="${exceptionDay}">${exceptionDay}</option>
                                </g:each>
                            </select></td>
                        </tr>
                        <tr><td/><td><button style="font-size:11px;width:120px" onclick="window.showExceptionCalendar(document.getElementById('calendarExceptions'), document.getElementById('calendarExceptionsInput'), this)" type="button">Add Exception</button></td></tr>
                        <tr><td/><td><button style="font-size:11px;width:120px" onclick="window.removeSelectedExceptions()" type="button">Remove Selected</button></td></tr>
                    </table>
                </td>
            </tr>
        </table>
        <g:if test="${params.mode == 'edit'}">
            <input type="hidden" name="id" value="${rsMessageRuleCalendar.id}"/>
        </g:if>
        <input id="calendarExceptionsInput" type="hidden" name="exceptions" value="${exceptionDays.join(',').encodeAsHTML()}"/>
    </rui:formRemote>
</div>