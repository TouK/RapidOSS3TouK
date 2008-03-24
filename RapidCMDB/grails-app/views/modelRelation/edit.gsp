<%@ page import="model.*" %>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <meta name="layout" content="main" />
        <title>Edit ModelRelation</title>
    </head>
    <body>
        <div class="nav">
            <span class="menuButton"><a class="home" href="${createLinkTo(dir: 'model/show/' + modelRelation.fromModel.id)}">${modelRelation.fromModel?.name}</a></span>
        </div>
        <div class="body">
            <h1>Edit ModelRelation</h1>
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <g:hasErrors bean="${modelRelation}">
            <div class="errors">
                <g:renderErrors bean="${modelRelation}" as="list" />
            </div>
            </g:hasErrors>
            <g:form method="post" >
                <input type="hidden" name="id" value="${modelRelation?.id}" />
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
                    <span class="button"><g:actionSubmit class="save" value="Update" /></span>
                    <span class="button"><g:actionSubmit class="delete" onclick="return confirm('Are you sure?');" value="Delete" /></span>
                </div>
            </g:form>
        </div>
    </body>
</html>
