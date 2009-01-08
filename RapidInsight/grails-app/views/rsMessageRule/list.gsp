<%--
  Created by IntelliJ IDEA.
  User: admin
  Date: Jan 7, 2009
  Time: 6:14:57 PM
  To change this template use File | Settings | File Templates.
--%>
<g:render template="header" model="[:]"/>
<div class="nav">
    <span class="menuButton"><g:link class="create" action="create">New RsMessageRule</g:link></span>
</div>
       <div class="body">
            <h1>Your Notification List</h1>  
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
                            <td><g:link action="edit" id="${rule.id}">${search.SearchQuery.get(id:rule.searchQueryId)?.name?.encodeAsHTML()}</g:link></td>
                            <td>${rule.delay?.encodeAsHTML()}</td>
                            <td>${rule.clearAction?.encodeAsHTML()}</td>
                            <td>${rule.destinationType?.encodeAsHTML()}</td>
                            
                            <%
                                def isSubscribed = ListeningAdapterManager.getInstance().isSubscribed(smartsConnector.ds);
                                if (isSubscribed) {
                            %>
                            <td><g:link action="stopConnector" controller="smartsConnector" id="${smartsConnector.id}" class="stop">Stop</g:link></td>
                            <%
                                }
                                else {
                            %>
                            <td><g:link action="startConnector" controller="smartsConnector" id="${smartsConnector.id}" class="start">Start</g:link></td>
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
