

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <meta name="layout" content="main" />
        <title>Create ModelRelation</title>         
    </head>
    <body>
        <div class="nav">
            <%
                if (params["model.id"] != null) {
            %>
            <span class="menuButton"><a class="home" href="${createLinkTo(dir: 'model/show/' + params["model.id"])}">${Model.get(params["model.id"])?.name}</a></span>
            <%
                }
                else
                {
            %>
            <span class="menuButton"><a class="home" href="${createLinkTo(dir:'')}">Home</a></span>
            <span class="menuButton"><g:link class="list" action="list">ModelRelation List</g:link></span>
            <%
                }
            %>
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
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label for="name">Name:</label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean:modelRelation,field:'name','errors')}">
                                    <input type="text" id="name" name="name" value="${fieldValue(bean:modelRelation,field:'name')}"/>
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
                                    <label for="toModel">To Model:</label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean:modelRelation,field:'toModel','errors')}">
                                    <g:select optionKey="id" from="${Model.list()}" name="toModel.id" value="${modelRelation?.toModel?.id}" ></g:select>
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
