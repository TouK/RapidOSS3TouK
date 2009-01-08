<%@ page import="message.RsMessageRule" %>
<g:render template="header" model="[:]"/>

<div class="nav">    
    <span class="menuButton"><g:link class="list" action="list">RsMessageRule List</g:link></span>
</div>
<div class="body">
    <h1>Create RsMessageRule</h1>
    <g:if test="${flash.message}">
        <div class="message">${flash.message}</div>
    </g:if>
    <g:hasErrors bean="${rsMessageRule}">
        <div class="errors">
            <g:renderErrors bean="${rsMessageRule}" as="list"/>
        </div>
    </g:hasErrors>
    <g:hasErrors bean="${flash.errors}">
       <div class="errors">
            <g:renderErrors bean="${flash.errors}"/>
        </div>
    </g:hasErrors>
    <g:form action="save" method="post" >
        <div class="dialog">
            <table>
                <tbody>
                   

                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="searchQueryId">Search Query:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsMessageRule,field:'searchQueryId','errors')}">
                            <%
                                def searchQueryList=[];
                                search.SearchQuery.list().each{
                                    searchQueryList.add([id:it.id,name:it.name])
                                }
                            %>
                            <g:select class="inputtextfield1" optionKey="id" optionValue="name" from="${searchQueryList}"  name="searchQueryId" value="${rsMessageRule.searchQueryId}"></g:select>
                        </td>
                    </tr>

                  <tr class="prop" >
                    <td valign="top" class="name">
                        <label for="destinationType">Destionation Type:</label>
                    </td>
                    <td valign="top" class="value ${hasErrors(bean: rsMessageRule, field: 'destinationType', 'errors')}">
                        <g:select id="destinationType" name="destinationType" from="${rsMessageRule.constraints.destinationType.inList.collect{it.encodeAsHTML()}}" value="${fieldValue(bean:rsMessageRule,field:'destinationType')}" ></g:select>
                    </td>
                </tr>


                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="delay">Delay:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:rsMessageRule,field:'delay','errors')}">
                            <input type="text" class="inputtextfield" id="delay" name="delay" value="${fieldValue(bean:rsMessageRule,field:'delay')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                            <td valign="top" class="name">
                                <label for="clearAction">Notify about Clear Events:</label>
                            </td>
                            <td valign="top" class="value ${hasErrors(bean: rsMessageRule, field: 'clearAction', 'errors')}">
                                <g:checkBox name="clearAction" value="${rsMessageRule?.clearAction}"></g:checkBox>
                            </td>
                        </tr>
                    
               <tr class="prop" >
                    <td valign="top" class="name">
                        <label for="enabled">Enabled:</label>
                    </td>
                    <td valign="top" class="value ${hasErrors(bean: rsMessageRule, field: 'enabled', 'errors')}">
                        <g:checkBox name="enabled" value="${rsMessageRule?.enabled}"></g:checkBox>
                    </td>
                </tr>


                    
                </tbody>
            </table>
        </div>
        <div class="buttons">
            <span class="button"><input class="save" type="submit" value="Create"/></span>
        </div>
    </g:form>
</div>


<g:render template="footer" model="[:]"/>

