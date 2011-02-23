<%@ page import=" java.sql.Timestamp; java.text.SimpleDateFormat;" %>
<%
	def message=message.RsMessage.get(id:params.messageId);	
%>	
<g:if test="${!message}">
	Notification not found.
</g:if>
<g:else>
	<%
	    SimpleDateFormat format = new SimpleDateFormat("dd MMM HH:mm:ss")
		def dateProps=["sentAt","firstSentAt","insertedAt"];
		def props=message.asMap();
		["rsInsertedAt","rsUpdatedAt","sendAfter","rsOwner"].each{ props.remove(it) };
		def propNames=props.keySet().sort();
	%>
   <div class="ri-object-details" style="width:100%">
	<table  cellspacing="2" cellpadding="2" width="100%">
	    <tbody>
	    	<g:each var="propName" status="i" in="${propNames}">
	    		<tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
		            <td valign="top" class="name" width="150">${propName}:</td>
		            <g:if test="${!dateProps.contains(propName)}">
			            <td valign="top" class="value">${props[propName]}</td>
			        </g:if>   
			        <g:else>
			        	<td valign="top" class="value">
                        <% print props[propName] == 0 ? "never" : format.format(new Timestamp(props[propName]));%>
			        	</td>
			        </g:else>			        
		        </tr>	
    		</g:each>	        
	    </tbody>
	</table>
    </div>
</g:else>