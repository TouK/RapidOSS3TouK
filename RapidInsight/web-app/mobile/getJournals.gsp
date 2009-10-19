<%@ page import="com.ifountain.rcmdb.mobile.MobileUtils" %>
<%
    def gspFolder = "simple";
    if(MobileUtils.isIphone(request)){
        gspFolder = "iphone";
    }
    request.getRequestDispatcher("${gspFolder}/defaultEventJournals.gsp").forward(request, response);
%>
