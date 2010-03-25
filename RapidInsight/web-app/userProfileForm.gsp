<%@ page import="auth.ChannelUserInformation; auth.RsUser" %>
<%--
  Created by IntelliJ IDEA.
  User: Sezgin Kucukkaraaslan
  Date: Apr 3, 2009
  Time: 5:58:16 PM
--%>
<%
    def rsUser = RsUser.get(username: session.username);
    def defaultDestinations = ChannelUserInformation.search("userId:${rsUser.id} AND isDefault:true", [max: 1]).results
    def defaultDestinationType;
    if (defaultDestinations.size() > 0) {
        defaultDestinationType = defaultDestinations[0].type
    }
%>
<rui:formRemote method="POST" action="rsUser/changeProfile?format=xml" componentId="${params.componentId}">
    <table>
        <tr><td width="50%"><label>Old Password:</label></td><td width="50%"><input type="password" name="oldPassword" style="width:175px"/></td></tr>
        <tr><td width="50%"><label>New Password:</label></td><td width="50%"><input type="password" name="password1" style="width:175px"/></td></tr>
        <tr><td width="50%"><label>Confirm Password:</label></td><td width="50%"><input type="password" name="password2" style="width:175px"/></td></tr>
        <g:set var="channelTypes" value="${RsUser.getEditableChannelTypes()}"></g:set>
        <g:each in="${channelTypes}" var="channelType">
            <tr><td width="50%"><label>${channelType}:</label></td><td width="50%"><input type="text" name="${channelType}" style="width:175px" value="${rsUser.retrieveChannelInformation(channelType)?.destination}"/></td></tr>
        </g:each>
        <g:if test="${channelTypes.size() > 0}">
            <tr><td width="50%"><label>Default Destination:</label></td><td width="50%"><select name="defaultDestination" style="width:175px">
                <option value=""></option>
                <g:each var="channelType" in="${channelTypes}">
                    <g:if test="${channelType == defaultDestinationType}">
                        <option value="${channelType}" selected="true">${channelType}</option>
                    </g:if>
                    <g:else>
                        <option value="${channelType}">${channelType}</option>
                    </g:else>
                </g:each>
            </select></td></tr>
        </g:if>
    </table>
    <input type="hidden" name="username" value="${rsUser.username}">
</rui:formRemote>