<%--
  Created by IntelliJ IDEA.
  User: Sezgin Kucukkaraaslan
  Date: Apr 29, 2010
  Time: 2:51:21 PM
--%>

<%@ page import="script.CmdbScript;" %>
<%
    try{
       def result = CmdbScript.runScript(scriptName, [params:params, web:[session:session]]);
       %><div>${result}<div><%
    }
    catch(e){
       def error = e.getMessage();
       %><div class="error">${error}<div><%
    }
%>