<%--
  Created by IntelliJ IDEA.
  User: iFountain
  Date: Nov 25, 2008
  Time: 3:05:10 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page import="datasource.HttpDatasource;org.apache.commons.httpclient.util.URIUtil;org.apache.commons.httpclient.util.ParameterParser"  %>

<html>
<head>

</head>
<body>

<%
    def openNmsGraphDs=HttpDatasource.get(name:"opennmsds");
    openNmsGraphDs.doGetRequest("j_acegi_security_check", ["j_username":"admin","j_password":"admin"]);



    def openNmsObject=OpenNmsObject.get(id:params.id);
    if(openNmsObject!=null)
    {
       if(openNmsObject.graphs!=null)
       {
         for(graph in openNmsObject.graphs)
         {
            def url=graph.url;
            def queryParams=new ParameterParser().parse(URIUtil.getQuery(url),'&' as char);
            def params=[:]
            for(param in queryParams)
            {
            	params[param.name]=param.value;
            }
            params["start"]="1227538040382"
            params["end"]="1227624440382"
            def image=openNmsGraphDs.adapter.doGetRequest(url,params);
            %>
             Graph url ${graph.url} <br>
             <hr>
             ${image}
             <hr>
            <%
            }
       }
    }
    else{
    %>
     OpenNmsObject with id ${params.id} does not exist.
    <%
    }
%>





</body>
</html>