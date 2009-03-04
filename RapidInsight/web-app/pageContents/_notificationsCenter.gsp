<%--
  Created by IntelliJ IDEA.
  User: Sezgin Kucukkaraaslan
  Date: Mar 3, 2009
  Time: 6:12:16 PM
  To change this template use File | Settings | File Templates.
--%>
<%
	def templateName = currentAction? currentAction: 'list';

%>

 <link rel="stylesheet" href="${createLinkTo(dir: 'css', file: 'main.css')}"/>
 <link rel="stylesheet" href="${createLinkTo(file: 'rimain.css')}"/>
 <g:render template="/rsMessageRule/${templateName}" model="${binding.variables}"></g:render>