<%@ page import="script.CmdbScript;" %>
<%
	def errorMessage;
	def successMessage = "Successfull";
    def scriptName = params.scriptName;
    try{
       CmdbScript.runScript(scriptName, [params:params, web:[session:session]]);
       flash.message = successMessage;
    }
    catch(e){
       errorMessage = e.getMessage();
       flash.errors = new RapidBindException(flash, "flash");
       flash.errors.reject("default.custom.error", [errorMessage] as Object[],"")
    }
    response.sendRedirect(params.redirectUrl);
%>