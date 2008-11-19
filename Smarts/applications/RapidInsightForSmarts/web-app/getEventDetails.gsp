<%@ page import="com.ifountain.rcmdb.domain.util.DomainClassUtils; java.sql.Timestamp; java.text.SimpleDateFormat; com.ifountain.rcmdb.util.RapidCMDBConstants; org.codehaus.groovy.grails.commons.GrailsDomainClass; org.codehaus.groovy.grails.commons.ApplicationHolder" %>
<%
    def notificationName = params.name;
    def domainObject = RsEvent.get(name: notificationName);
    def redirectedGsp;
    if (domainObject != null) {
        if (domainObject.getClass().getName() == "SmartsNotification") {
            redirectedGsp = "smartsEventDetails.gsp";
        }
        else {
            redirectedGsp = "defaultEventDetails.gsp";
        }
        response.sendRedirect("${redirectedGsp}?name=${notificationName}&componentId=${params.componentId}")
    }
    else {
%>
Event ${notificationName} does not exist.
<%
    }
%>
