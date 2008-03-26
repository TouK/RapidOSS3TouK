<%@ page import="model.*" %>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <meta name="layout" content="main" />
        <title>Create ModelRelation</title>         
    </head>
    <body>
        <div class="nav">
            <span class="menuButton">From <a class="home" href="${createLinkTo(dir: 'model/show/' + params["model.id"])}">${Model.get(params["model.id"])?.name} To ?</a></span>
        </div>
        <div class="body">
            <h1>Create ModelRelation</h1>
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <g:hasErrors bean="${modelRelation}">
            <div class="errors">
                <g:renderErrors bean="${modelRelation}" as="list" />
            </div>
            </g:hasErrors>
            <g:form action="save" method="post" >
                <div class="dialog">
                    <table>
                        <tbody>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="cardinality">Cardinality:</label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean:modelRelation,field:'cardinality','errors')}">
                                    <g:select id="cardinality" name="cardinality" from="${modelRelation.constraints.cardinality.inList.collect{it.encodeAsHTML()}}" value="${fieldValue(bean:modelRelation,field:'cardinality')}" ></g:select>
                                </td>
                            </tr> 
                            <%
                                if (params["model.id"] != null) {
                            %>
                            <input type="hidden" name="fromModel.id" value="${params["model.id"]}"/>
                            <%
                                }
                                else {
                            %>
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="model">From Model:</label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: modelRelation, field: 'fromModel', 'errors')}">
                                    <g:select optionKey="id" from="${Model.list()}" name="model.id" value="${modelRelation?.fromModel?.id}"></g:select>
                                </td>
                            </tr>
                            <%
                                }
                            %>
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="fromName">Relation Name:</label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean:modelRelation,field:'fromName','errors')}">
                                    <input type="text" id="fromName" name="fromName" value="${fieldValue(bean:modelRelation,field:'fromName')}"/>
                                </td>
                            </tr>
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="toModel">To Model:</label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean:modelRelation,field:'toModel','errors')}">
                                    <g:select optionKey="id" from="${Model.list()}" name="toModel.id" value="${modelRelation?.toModel?.id}" ></g:select>
                                </td>
                            </tr>
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="toName">Reverse Relation Name:</label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean:modelRelation,field:'toName','errors')}">
                                    <input type="text" id="toName" name="toName" value="${fieldValue(bean:modelRelation,field:'toName')}"/>
                                </td>
                            </tr>
                        
                        </tbody>
                    </table>
                </div>
                <div class="buttons">
                    <span class="button"><input class="save" type="submit" value="Create" /></span>
                </div>
            </g:form>
        </div>
    </body>
</html>
