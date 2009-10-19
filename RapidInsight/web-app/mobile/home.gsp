<%@ page import="com.ifountain.rcmdb.mobile.MobileUtils" %>
<g:if test="${MobileUtils.isIphone(request)}">
     <%
         response.sendRedirect(createLinkTo(dir:'mobile/iphone', file:'home.gsp'));
     %>
</g:if>
<g:else>
    <%
         response.sendRedirect(createLinkTo(dir:'mobile/simple', file:'home.gsp'));
     %>
</g:else>
