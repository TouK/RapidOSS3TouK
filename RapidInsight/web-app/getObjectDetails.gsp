<%
    def objectName = params.name;
    def notExistGsp = params.notExistGsp;
    def domainObject = RsTopologyObject.get(name: objectName);
    if (domainObject != null) {
        request.getRequestDispatcher("defaultObjectDetails.gsp").forward(request, response);
    }
    else {
      if(notExistGsp != null)
      {
        request.getRequestDispatcher(notExistGsp).forward(request, response);        
      }
      else
      {
%>
Object ${name} does not exist.
<%

      }
    }
%>