<%@ page import="datasource.NetcoolConversionParameter; java.sql.Timestamp; java.text.SimpleDateFormat" %>
<%
    def eventId = params.id;
    def netcoolEvent = NetcoolEvent.get(id: eventId)
    if (netcoolEvent != null) {
        def journals = NetcoolJournal.search("serverserial:${netcoolEvent.serverserial} AND servername:${netcoolEvent.servername}").results;
%>
<div class="yui-navset yui-navset-top">
    <ul class="yui-nav">
        <li><a href="#" onclick="window.html.show('getDetails.gsp?type=NetcoolEvent&id=${netcoolEvent?.id}');"><em>Event</em></a></li>
        <li class="selected"><a href="#"><em>Journal</em></a></li>
    </ul>
    <div style="display:block;padding-top:5px;padding-left:5px">
        <%

            def userConversions = NetcoolConversionParameter.search("columnName:OwnerUID").results;
            def userMap = [:];
            userConversions.each{
               userMap.put(it.value, it.conversion); 
            }
            journals.each{
                SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
                def date = format.format(new Timestamp(it.chrono));
                def userId = it.keyfield.split(":")[1].toInteger();
                def user = userId;
                if(userMap.containsKey(userId)){
                    user = userMap.get(userId);
                }
                %>
                <div style="padding-left:25px;background:url( images/rapidjs/component/tools/dark-green-circle.png) no-repeat 0 0;">
                    ${user}: ${date}<br>
                    ${it.text}<br>
                </div>
        <%
            }
               
        %>

    </div>
</div>
<%
    }
    else {
%>
No data found
<%
    }
%>


