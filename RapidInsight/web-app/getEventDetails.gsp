<%
    def notificationName = params.name;
    def domainObject = RsEvent.get(name: notificationName);
    if (domainObject != null) {
        response.sendRedirect("defaultEventDetails.gsp?name=${notificationName}&componentId=${params.componentId}")
    }
    else {
%>
Event ${notificationName} does not exist.
<%
    }
%>
