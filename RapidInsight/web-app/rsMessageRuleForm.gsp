<%@ page import="message.RsMessageRule" %>
<%@ page import="auth.RsUser" %>
<%@ page import="search.SearchQueryGroup" %>
<%@ page import="search.SearchQuery" %>

<%
    def mode = params.mode;
    def componentId = params.componentId;
    def rsMessageRule=new RsMessageRule();
    def actionUrl="rsMessageRule/save"

    if(mode=='edit')
    {
        actionUrl="rsMessageRule/update"
        if(params.ruleId)
        {
            rsMessageRule=RsMessageRule.get(id:params.ruleId);
            if(rsMessageRule==null)
            {
                println "Message Rule with id ${params.ruleId} does not exist";
                return;
            }
            
        }
    }

%>

    <script type="text/javascript">
    window.refreshDataComponent = function(){
        var dataComponent = YAHOO.rapidjs.Components['ruleTree'];
        dataComponent.poll();
    }
    </script>

<div class="yui-navset yui-navset-top" style="margin-top:5px">    
    <div style="display:block;margin-top:10px;">
        <rui:formRemote method="POST" action="${actionUrl}" componentId="${params.componentId}" onSuccess="window.refreshDataComponent">
            <table cellspacing="5" cellpadding="5">
                <tr>
                    <td width="200"><label>Search Query:</label></td>
                    <td >
                        <%
                            def username = session.username;
                            def filterType="event";
                            def queryGroups = SearchQueryGroup.searchEvery("( type:${filterType.exactQuery()} OR type:${"default".exactQuery()} ) AND  ( ( username:${RsUser.RSADMIN.exactQuery()} AND isPublic:true) OR (username:${username.exactQuery()}) )");
                        %>
                        <select name="searchQueryId" class="inputtextfield1">
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
                    </td>
                </tr>
                <tr>
                    <td ><label>Destination Type:</label></td>
                    <td >
                        <g:select id="destinationType" name="destinationType" from="${rsMessageRule.constraints.destinationType.inList.collect{it.encodeAsHTML()}}" value="${fieldValue(bean:rsMessageRule,field:'destinationType')}" ></g:select>
                    </td>
                </tr>
                <tr>
                    <td ><label>Delay:</label></td>
                    <td ><input type="text" class="inputtextfield" id="delay" name="delay" value="${fieldValue(bean:rsMessageRule,field:'delay')}"/> seconds</td>
                </tr>
                <tr>
                    <td valign="top"><label>Notify about Clear Events:</label></td>
                    <td ><g:checkBox name="clearAction" value="${rsMessageRule?.clearAction}"></g:checkBox></td>
                </tr>
                <tr>
                    <td valign="top"><label>Enabled:</label></td>
                    <td ><g:checkBox name="enabled" value="${rsMessageRule?.enabled}"></g:checkBox></td>
                </tr>
            </table>
            <g:if test="${mode=='edit' && params.ruleId}">
                <input type="hidden" name="id" value="${params.ruleId}" />
            </g:if>
        </rui:formRemote>
    </div>
</div>