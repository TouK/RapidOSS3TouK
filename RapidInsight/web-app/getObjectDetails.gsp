<%
    def objectName = params.name;
    def domainObject = RsTopologyObject.get(name: objectName);
    if (domainObject != null) {
        response.sendRedirect("defaultObjectDetails.gsp?name=${objectName}&componentId=${params.componentId}")
    }
    else {
%>
Object ${name} does not exist.
<%
    }
%>