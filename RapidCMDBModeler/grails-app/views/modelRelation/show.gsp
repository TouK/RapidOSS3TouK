<%@ page import="model.*" %>

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="adminLayout" />
    <title>Show ModelRelation</title>
</head>
<body>
<div class="nav">
    <%
        if (params["reverse"] != null) {
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
    <h1>Show ModelRelation</h1>
    <g:render template="/common/messages" model="[flash:flash]"></g:render>
    <%
       def relationName;
       def relationType;
       def to;
       def reverseName;
       if(params["reverse"] != null){
          relationName = modelRelation.secondName;
          relationType = modelRelation.secondCardinality + "To" + modelRelation.firstCardinality;
          to = modelRelation.firstModel;
          reverseName = modelRelation.firstName;
      }
      else{
          relationName = modelRelation.firstName;
          relationType = modelRelation.firstCardinality + "To" + modelRelation.secondCardinality;
          to = modelRelation.secondModel;
          reverseName = modelRelation.secondName;
      }
    %>
    <div class="dialog">
        <table>
            <tbody>
                <tr class="prop">
                    <td valign="top" class="name">Name:</td>
                    <td valign="top" class="value">${relationName}</td>
                </tr>

                <tr class="prop">
                    <td valign="top" class="name">Type:</td>
                    <td valign="top" class="value">${relationType}</td>
                </tr>

                <tr class="prop">
                    <td valign="top" class="name">To:</td>
                    <td valign="top" class="value">${to}</td>
                </tr>

                <tr class="prop">
                    <td valign="top" class="name">Reverse Relation Name:</td>

                    <td valign="top" class="value">${reverseName}</td>

                </tr>

            </tbody>
        </table>
    </div>
    <div class="buttons">
        <g:form>
            <input type="hidden" name="id" value="${modelRelation?.id}"/>
            <%
               if(params["reverse"] != null){
                   %>
                 <input type="hidden" name="reverse" value="true"/>
            <%
               }
            %>
            <span class="button"><g:actionSubmit class="edit" value="Edit"/></span>
            <span class="button"><g:actionSubmit class="delete" onclick="return confirm('Are you sure?');" value="Delete"/></span>
        </g:form>
    </div>
</div>
</body>
</html>
