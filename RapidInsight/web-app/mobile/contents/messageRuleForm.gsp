<%@ page import="org.apache.commons.lang.StringUtils; com.ifountain.rcmdb.mobile.MobileUtils; message.RsMessageRule" %>
<%@ page import="auth.RsUser" %>
<%@ page import="search.SearchQueryGroup" %>
<%@ page import="search.SearchQuery" %>

<%
    def destinationGroups = RsMessageRule.getDestinationGroupsForUser(session.username);
    def errorTargetURI = request.uri.toString().substringAfter("RapidSuite");
    def mode = params.mode;
    def rsMessageRule = new RsMessageRule();
    def actionUrl = "save"
    def gspFolder = "simple"
    if (MobileUtils.isIphone(request)) {
        gspFolder = "iphone";
    }
    if (mode == 'edit')
    {
        actionUrl = "update"
        if (params.ruleId)
        {
            rsMessageRule = RsMessageRule.get(id: params.ruleId);
            if (rsMessageRule == null)
            {
                println "Message Rule with id ${params.ruleId} does not exist";
                return;
            }
        }
    }
%>
<div id="messageRuleForm" class="panel">
    <g:render template="/common/messages" model="[flash:flash, beans:[]]"></g:render>
    <div class="form-wrp">
        <g:form method="POST" controller="rsMessageRule" action="${actionUrl}">
            <fieldset>
                <%
                    def username = session.username;
                    def filterType = "event";
                    def queryGroups = SearchQueryGroup.searchEvery("( type:${filterType.exactQuery()} OR type:${"default".exactQuery()} ) AND  ( ( username:${RsUser.RSADMIN.exactQuery()} AND isPublic:true) OR (username:${username.exactQuery()}) )");
                %>
                <g:if test="${MobileUtils.isIphone(request)}">
                    <div class="row">
                        <label>Search Query:</label>
                        <select name="searchQueryId">
                            <g:each in="${queryGroups}" var="group">
                                <optgroup label="${group.name}">
                                    <g:each in="${group.queries}" var="query">
                                        <g:if test="${query.type==filterType}">
                                            <g:if test="${rsMessageRule.searchQueryId==query.id}">
                                                <option value="${query.id}" selected="selected">${query.name}</option>
                                            </g:if>
                                            <g:else>
                                                <option value="${query.id}">${query.name}</option>
                                            </g:else>
                                        </g:if>
                                    </g:each>
                                </optgroup>
                            </g:each>
                        </select>
                    </div>
                    <div class="row">
                        <label>Destination Type:</label>
                        <select name="destinationType">
                            <g:each in="${destinationGroups}" var="group">
                                <optgroup label="${group.name}">
                                    <g:each in="${group.destinationNames}" var="destinationName">
                                        <g:if test="${rsMessageRule.destinationType==destinationName}">
                                            <option value="${destinationName}" selected="selected">${destinationName}</option>
                                        </g:if>
                                        <g:else>
                                            <option value="${destinationName}">${destinationName}</option>
                                        </g:else>
                                    </g:each>
                                </optgroup>
                            </g:each>
                        </select>
                    </div>
                    <div class="row">
                        <label>Delay:</label>
                        <input type="text" name="delay" value="${fieldValue(bean: rsMessageRule, field: 'delay')}" style="padding:2; width:140px"/> seconds
                    </div>

                    <div class="row">
                        <label>Notify about Clear Events:</label>
                        <g:checkBox name="sendClearEventType" value="${rsMessageRule?.sendClearEventType}"></g:checkBox>
                    </div>
                    <div class="row">
                        <label>Enabled:</label>
                        <g:checkBox name="enabled" value="${rsMessageRule?.enabled}"></g:checkBox>
                    </div>
                </g:if>
                <g:else>
                    <div class="row"><label>Search Query:</label></div>
                    <div class="row"><select name="searchQueryId">
                        <g:each in="${queryGroups}" var="group">
                            <optgroup label="${group.name}">
                                <g:each in="${group.queries}" var="query">
                                    <g:if test="${query.type==filterType}">
                                        <g:if test="${rsMessageRule.searchQueryId==query.id}">
                                            <option value="${query.id}" selected="selected">${query.name}</option>
                                        </g:if>
                                        <g:else>
                                            <option value="${query.id}">${query.name}</option>
                                        </g:else>
                                    </g:if>
                                </g:each>
                            </optgroup>
                        </g:each>
                    </select></div>
                    <div class="row"><label>Destination Type:</label></div>
                    <div class="row"><select name="destinationType">
                        <g:each in="${destinationGroups}" var="group">
                            <optgroup label="${group.name}">
                                <g:each in="${group.destinationNames}" var="destinationName">
                                    <g:if test="${rsMessageRule.destinationType==destinationName}">
                                        <option value="${destinationName}" selected="selected">${destinationName}</option>
                                    </g:if>
                                    <g:else>
                                        <option value="${destinationName}">${destinationName}</option>
                                    </g:else>
                                </g:each>
                            </optgroup>
                        </g:each>
                    </select></div>

                    <div class="row"><label>Delay:</label></div>
                    <div class="row"><input type="text" id="delay" name="delay" value="${fieldValue(bean: rsMessageRule, field: 'delay')}"/> seconds</div>

                    <div class="row"><label>Notify about Clear Events:</label></div>
                    <div class="row"><g:checkBox name="sendClearEventType" value="${rsMessageRule?.sendClearEventType}"></g:checkBox></div>

                    <div class="row"><label>Enabled:</label></div>
                    <div class="row"><g:checkBox name="enabled" value="${rsMessageRule?.enabled}"></g:checkBox></div>
                </g:else>
                <g:if test="${mode=='edit' && params.ruleId}">
                    <input type="hidden" name="id" value="${params.ruleId}"/>
                </g:if>
                <input type="hidden" name="targetURI" value="/mobile/${gspFolder}/notifications.gsp"/>
                <input type="hidden" name="errorTargetURI" value="${errorTargetURI.encodeAsHTML()}"/>
                <input type="hidden" name="format" value="html"/>
                <input type="hidden" name="mobile" value="true"/>
            </fieldset>
            <g:if test="${MobileUtils.isIphone(request)}">
                <a class="whiteButton" type="submit" href="#">Save</a>
            </g:if>
            <g:else>
                <div class="row"><input id="loginButton" type="submit" value="Save"/></div>
            </g:else>
        </g:form>
    </div>
</div>