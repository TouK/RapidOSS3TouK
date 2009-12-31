<%
    //Configured for Connection Class
    def domainClass = grailsApplication.getDomainClass(connection.class.name)
    def domainObject = connection;
    def logicalName = domainClass.logicalPropertyName;
    def properties = domainClass.clazz.getNonFederatedPropertyList();
    def propsToBeExcluded = ["id", "rsInsertedAt", "rsUpdatedAt","connectionClass","rsOwner","maxNumberOfConnections"]

    def beforePropNamesInOrder=["name"];
    def afterPropNamesInOrder=["username","userPassword","minTimeout","maxTimeout"];

    def propLabels=[
            "name":"Name",
            "username":"Username",
            "userPassword":"Password",
            "maxNumberOfConnections":"Max Active",
            "minTimeout":"Min Timeout",
            "maxTimeout":"Max Timeout"
    ];
    def propsInOrder=[];

    beforePropNamesInOrder.each{ propName ->
    def prop=properties.find{it.name == propName};
        if(prop)
        {
            propsInOrder.add(prop)
        }
    }
    //list all model props in the middle , except excluded and in order list
    properties.each { prop ->
      if(!propsToBeExcluded.contains(prop.name) && !beforePropNamesInOrder.contains(prop.name) && !afterPropNamesInOrder.contains(prop.name))
      {
         propsInOrder.add(prop);
      }
    }

    afterPropNamesInOrder.each{ propName ->
    def prop=properties.find{it.name == propName};
        if(prop)
        {
            propsInOrder.add(prop)
        }
    }

    propsInOrder.each{  prop ->
        def propName=prop.name;
        if(!propLabels.containsKey(propName))
        {
            String firstChar=propName.getAt(0);
            def propLabel=propName.replaceFirst(firstChar,firstChar.toUpperCase())
            propLabels[propName]=propLabel;
        }

    }

    
%>

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="adminLayout" />
    <title>Create ${connector.type}Connector</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><g:link class="list" action="list">${connector.type}Connector List</g:link></span>
</div>
<div class="body">
    <h1>Create ${connector.type}Connector</h1>
    <g:render template="/common/messages" model="[flash:flash, beans:[connector,connection,datasource,script]]"></g:render>
    <g:form action="save" method="post" >
        <input type="hidden" name="type" value="${connector?.type}">
        <div class="dialog">
            <table>
                <tbody>

                     <g:each var="prop" in="${propsInOrder}">
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <label>${propLabels[prop.name]}:</label>
                                </td>
                                <td valign="top">
                                    <%
                                        def autocomplete="on";
                                        if(prop.name=="username" || prop.name=="userPassword")
                                        {
                                            autocomplete="off";
                                        }
                                    %>
                                    <rui:include template="browserArtifacts/browserEditor.gsp" model='${[property:prop, domainClass:domainClass, cp:domainClass.constrainedProperties[prop.name], domainObject:domainObject,autocomplete:"${autocomplete}"]}'></rui:include>
                                </td>
                            </tr>
                    </g:each>

                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="scriptFile">Sender Script File:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:script,field:'scriptFile','errors')}">
                            <input type="text" class="inputtextfield" id="scriptFile" name="scriptFile" value="${fieldValue(bean:script,field:'scriptFile')}"/>
                        </td>
                    </tr>


                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="period">Sender Script Period:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:script,field:'period','errors')}">
                            <input type="text" class="inputtextfield" id="period" name="period" value="${fieldValue(bean:script,field:'period')}"/> sec.
                        </td>
                    </tr>

                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="logLevel">Log Level:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean: script, field: 'logLevel', 'errors')}">
                            <g:select class="inputtextfield" id="logLevel" name="logLevel" from="${script.constraints.logLevel.inList.collect{it.encodeAsHTML()}}" value="${fieldValue(bean:script,field:'logLevel')}"></g:select>
                        </td>
                    </tr>

                    <tr class="prop" >
                        <td valign="top" class="name">
                            <label for="showAsDestination">Show As Destination:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean: connector, field: 'showAsDestination', 'errors')}">
                            <g:checkBox name="showAsDestination" value="${connector?.showAsDestination}" ></g:checkBox>
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
