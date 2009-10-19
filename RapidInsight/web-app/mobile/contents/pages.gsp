<%@ page import="com.ifountain.rcmdb.mobile.MobileUtils" %>
<%--
  Created by IntelliJ IDEA.
  User: Sezgin Kucukkaraaslan
  Date: Sep 14, 2009
  Time: 6:00:23 PM
--%>
<%
    def gspFolder = "simple"
    if(MobileUtils.isIphone(request)){
        gspFolder = "iphone";
    }
%>
<li><rui:link url="mobile/${gspFolder}/queries.gsp" params="${[filterType:'event', listURI:'mobile/' + gspFolder+ '/event.gsp']}">Events</rui:link></li>
<li><rui:link url="mobile/${gspFolder}/queries.gsp" params="${[filterType:'historicalEvent', listURI:'mobile/' + gspFolder+ '/historicalEvent.gsp']}">Historical Events</rui:link></li>
<li><rui:link url="mobile/${gspFolder}/queries.gsp" params="${[filterType:'topology', listURI:'mobile/' + gspFolder+ '/inventory.gsp']}">Inventory</rui:link></li>