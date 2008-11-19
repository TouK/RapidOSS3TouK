<%--
  Created by IntelliJ IDEA.
  User: sezgin
  Date: Nov 19, 2008
  Time: 10:45:09 AM
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
    <head>
    <%
        def objectName = params.name;
        def relatedObject = objectName;
        def object = RsTopologyObject.get(name:objectName);
        if(object instanceof SmartsComputerSystemComponent){
            relatedObject = object.computerSystemName;
        }
        else if(object instanceof RsLink){
           relatedObject = object.a_ComputerSystemName;
        }
        response.sendRedirect("maps.gsp?name=${relatedObject}")
    %>
     </head>
</html>