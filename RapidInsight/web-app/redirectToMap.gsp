<%--
  Created by IntelliJ IDEA.
  User: Sezgin Kucukkaraaslan
  Date: Oct 22, 2008
  Time: 1:49:46 PM
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
    <head>
    <%
        def objectName = params.name;
        def relatedObject = objectName;
        def object = RsTopologyObject.get(name:objectName);
        if(object instanceof RsLink){
           relatedObject = object.a_ComputerSystemName; 
        }

        def rsClassName=params.rsClassName?params.rsClassName:"";
        def mapType=params.mapType?params.mapType:"";
        response.sendRedirect(URLUtils.createURL("index/maps.gsp", [name:relatedObject,rsClassName:rsClassName,mapType:mapType]))
    %>
     </head>
</html>