<%@ page import="script.CmdbScript;org.apache.commons.lang.StringEscapeUtils" %>
<%
	def errorCaught = false;
	def errorMessage;
	def successMessage;
	
	def notificationName = params.name;
	if(params.acknowledge != null)
	{
		try {		
	    	def scriptParams = [name:notificationName, acknowledged:params.acknowledge];
    		successMessage = CmdbScript.runScript("acknowledge", [params:scriptParams, web:[session:session]]);
    	}
    	catch(Exception e) {
			errorCaught = true;
			errorMessage = e.getMessage();
    	}
	}
	
	if(params.ownership != null)
	{
		try {		
	    	def scriptParams = [name:notificationName, act:params.ownership];
    		successMessage = CmdbScript.runScript("setOwnership", [params:scriptParams, web:[session:session]]);
    	}
    	catch(Exception e) {
			errorCaught = true;
			errorMessage = e.getMessage();
    	}
	}
	
	if(successMessage != null )
	{
		successMessage = StringEscapeUtils.escapeXml(successMessage);
	}
%>

<g:if test="${errorCaught == true}">
	<div>
	<div id="messageArea" class="error">
		${errorMessage}
	</div>
	</div>
</g:if>
<g:else>
	<div>
	<div id="messageArea" class="success">
		${successMessage}
	</div>
	</div>
</g:else>