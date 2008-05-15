<%@ page import="model.*" %>

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Create ModelRelation</title>
</head>
<body>
<div class="nav">
    <%
        if (params["firstModel.id"]) {
    %>
    <span class="menuButton"><a class="home" href="${createLinkTo(dir: 'model/show/' + params["firstModel.id"])}">${Model.get(params["firstModel.id"])?.name}</a></span>
    <%
        }
        else {
    %>
    <span class="menuButton"><a class="home" href="${createLinkTo(dir: 'model/show/' + modelId)}">${Model.get(modelId)?.name}</a></span>
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
            <g:renderErrors bean="${modelRelation}" as="list"/>
        </div>
    </g:hasErrors>
    <g:form action="save" method="post">
        <div class="dialog">
            <table>
                <tbody>

                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="cardinality">Type:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean: modelRelation, field: 'firstCardinality', 'errors') || hasErrors(bean: modelRelation, field: 'secondCardinality', 'errors')}">
                            <%
                                def cardinalityList = ["OneToOne", "OneToMany", "ManyToOne", "ManyToMany"];
                                def cardinalityValue;
                                if (modelRelation != null) {
                                    if (modelRelation?.firstModel?.id == params["firstModel.id"].toLong()) {
                                        cardinalityValue = modelRelation?.firstName + "To" + modelRelation?.secondName;
                                    }
                                    else {
                                        cardinalityValue = modelRelation?.secondName + "To" + modelRelation?.firstName;
                                    }
                                }

                            %>
                            <g:select  class="inputtextfield" id="cardinality" name="cardinality" from="${cardinalityList.collect{it.encodeAsHTML()}}" value="${cardinalityValue}"></g:select>
                        </td>
                    </tr>
                    <%
                      def firstModelId;
                      if(params["firstModel.id"]){
                          firstModelId = params["firstModel.id"];
                      }
                      else{
                         firstModelId = modelId;
                      }
                    %>
                    <input type="hidden" name="firstModel.id" value="${firstModelId}"/>
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="firstName">Name:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean: modelRelation, field: 'firstName', 'errors')}">
                            <input type="text"  class="inputtextfield" id="firstName" name="firstName" value="${fieldValue(bean: modelRelation, field: 'firstName')}"/>
                        </td>
                    </tr>
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="secondModel">To:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean: modelRelation, field: 'secondModel', 'errors')}">
                            <g:select  class="inputtextfield" optionKey="id" from="${Model.list()}" name="secondModel.id" value="${modelRelation?.secondModel?.id}"></g:select>
                        </td>
                    </tr>
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="secondName">Reverse Relation Name:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean: modelRelation, field: 'secondName', 'errors')}">
                            <input type="text"  class="inputtextfield" id="secondName" name="secondName" value="${fieldValue(bean: modelRelation, field: 'secondName')}"/>
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
</body>
</html>
