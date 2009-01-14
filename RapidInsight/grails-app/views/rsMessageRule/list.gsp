<%--
  Created by IntelliJ IDEA.
  User: admin
  Date: Jan 7, 2009
  Time: 6:14:57 PM
  To change this template use File | Settings | File Templates.
--%>
<g:render template="header" model="[:]"/>
<div class="nav">
    <span class="menuButton"><g:link class="create" action="create">New Notification Rule</g:link></span>
</div>
       <div class="body">
            <h1>Your Notification Rule List</h1>

        <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
        </g:if>
        <g:hasErrors bean="${flash.errors}">
            <div class="errors">
                <g:renderErrors bean="${flash.errors}"/>
            </div>
        </g:hasErrors>
            <div class="list">
                <table>
                    <thead>
                        <tr>
                            <th>Query</th>
                            <th>Delay</th>
                            <th>Clear Messages</th>
                            <th>Destination</th>
                            <th></th>
                        </tr>
                    </thead>
                    <tbody>
                      <%
                         def userId=auth.RsUser.get(username:session.username)?.id
                         def myRules= message.RsMessageRule.searchEvery("userId:${userId}",[sort:"id",order:"asc"])                         
                      %>

                    <g:each in="${myRules}" status="i" var="rule">
                        <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                            <td>
                            <g:link action="edit" id="${rule.id}">
                                <g:set var="queryName" value="${search.SearchQuery.get(id:rule.searchQueryId)?.name}"/>
                                <g:if test="${queryName}">${queryName?.encodeAsHTML()}</g:if>
                                <g:else>id:${rule.searchQueryId}</g:else>
                            </g:link>
                            </td>
                            <td>${rule.delay?.encodeAsHTML()}</td>
                            <td>${rule.clearAction?.encodeAsHTML()}</td>
                            <td>${rule.destinationType?.encodeAsHTML()}</td>
                            
                            <%
                                
                                if (rule.enabled) {
                            %>
                            <td><g:link action="disableRule" controller="rsMessageRule" id="${rule.id}" class="stop">Disable</g:link></td>
                            <%
                                }
                                else {
                            %>
                            <td><g:link action="enableRule" controller="rsMessageRule" id="${rule.id}" class="start">Enable</g:link></td>
                            <%
                                }
                            %>
                        </tr>
                    </g:each>
                    </tbody>
                </table>
            </div>
        </div>


<g:render template="footer" model="[:]"/>
