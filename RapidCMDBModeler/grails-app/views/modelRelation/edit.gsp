<%@ page import="model.*" %>

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="adminLayout" />
    <title>Edit ModelRelation</title>
</head>
<body>
<div class="nav">
    <%
        if (params["reverse"] != null || isOwner) {
    %>
    <span class="menuButton"><a class="home" href="${createLinkTo(dir: 'model/show/' + modelRelation?.secondModel?.id)}">${modelRelation?.secondModel}</a></span>
    <%
        }
        else {
    %>
    <span class="menuButton"><a class="home" href="${createLinkTo(dir: 'model/show/' + modelRelation?.firstModel?.id)}">${modelRelation?.firstModel}</a></span>
    <%
        }
    %>
</div>
<div class="body">
    <h1>Edit ModelRelation</h1>
    <g:if test="${flash.message}">
        <div class="message">${flash.message}</div>
    </g:if>
    <g:hasErrors bean="${modelRelation}">
        <div class="errors">
            <g:renderErrors bean="${modelRelation}" as="list"/>
        </div>
    </g:hasErrors>
    <g:form method="post">
        <input type="hidden" name="id" value="${modelRelation?.id}"/>
        <div class="dialog">
            <table>
                <tbody>

                    <%
                        def cardinalityList = ["OneToOne", "OneToMany", "ManyToOne", "ManyToMany"];
                        def cardinalityValue;
                        def relationName;
                        def relationNameField;
                        def to;
                        def toField;
                        def reverseName;
                        def reverseNameField
                        if (modelRelation != null) {
                            if (params["reverse"] == null && !isOwner) {
                                relationName = modelRelation?.firstName;
                                relationNameField = "firstName";
                                to = modelRelation?.secondModel;
                                toField = "secondModel";
                                reverseName = modelRelation?.secondName;
                                reverseNameField = "secondName";
                                cardinalityValue = modelRelation?.firstCardinality + "To" + modelRelation?.secondCardinality;
                            }
                            else {
                                relationName = modelRelation?.secondName;
                                relationNameField = "secondName";
                                to = modelRelation?.firstModel;
                                toField = "firstModel";
                                reverseName = modelRelation?.firstName;
                                reverseNameField = "firstName";
                                cardinalityValue = modelRelation?.secondCardinality + "To" + modelRelation?.firstCardinality;
                            }
                        }

                    %>
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="cardinality">Type:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean: modelRelation, field: 'firstCardinality', 'errors') || hasErrors(bean: modelRelation, field: 'secondCardinality', 'errors')}">
                            <g:select class="inputtextfield" id="cardinality" name="cardinality" from="${cardinalityList.collect{it.encodeAsHTML()}}" value="${cardinalityValue}"></g:select>
                        </td>
                    </tr>

                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="${relationNameField}">Relation Name:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean: modelRelation, field: relationNameField, 'errors')}">
                            <input type="text" class="inputtextfield" id="${relationNameField}" name="${relationNameField}" value="${relationName}"/>
                        </td>
                    </tr>
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="${toField}">To:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean: modelRelation, field: toField, 'errors')}">
                            <g:select class="inputtextfield" optionKey="id" from="${Model.list()}" name="${toField}.id" value="${to?.id}"></g:select>
                        </td>
                    </tr>
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="${reverseNameField}">Reverse Relation Name:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean: modelRelation, field: reverseNameField, 'errors')}">
                            <input type="text" class="inputtextfield" id="${reverseNameField}" name="${reverseNameField}" value="${reverseName}"/>
                        </td>
                    </tr>

                </tbody>
            </table>
        </div>
        <div class="buttons">
            <%
               if(params["reverse"] != null || isOwner){
                   %>
                 <input type="hidden" name="reverse" value="true"/>
            <%
               }
            %>
            <span class="button"><g:actionSubmit class="save" value="Update"/></span>
            <span class="button"><g:actionSubmit class="delete" onclick="return confirm('Are you sure?');" value="Delete"/></span>
        </div>
    </g:form>
</div>
</body>
</html>
