<%
    Statistics.record("ui.objectDetails","");

    def objectName = params.name;
    def domainObject = RsTopologyObject.get(name: objectName);
    if (domainObject != null) {
        request.getRequestDispatcher("defaultObjectDetails.gsp").forward(request, response);
    }
    else {
%>
Object ${objectName} does not exist.
<%
    }
%>