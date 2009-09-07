<%@ page import="script.CmdbScript;" %>
<%
	def errorMessage;
	def successMessage = "Successfull";
    def scriptName = params.scriptName;
    try{
       CmdbScript.runScript(scriptName, [params:params, web:[session:session]]);
    }
    catch(e){
       errorMessage = e.getMessage(); 
    }
%>

<g:if test="${errorMessage}">
	<div>
	<div id="messageArea" class="error">
		${errorMessage.encodeAsHTML()}
	</div>
	</div>
</g:if>
<g:else>
	<div>
	<div id="messageArea" class="success">
		${successMessage.encodeAsHTML()}
	</div>
	</div>
</g:else>