<%
    //Configured for Connection Class
    def connection=connector.ds.connection;
    def script=connector.script;
    def domainClass = grailsApplication.getDomainClass(connection.class.name)
    def domainObject = connection;
    def logicalName = domainClass.logicalPropertyName;
    def properties = domainClass.clazz.getNonFederatedPropertyList();
    def propsToBeExcluded = ["id", "rsInsertedAt", "rsUpdatedAt","connectionClass","rsOwner","maxNumberOfConnections","username","userPassword"]

    def beforePropNamesInOrder=["name"];
    def afterPropNamesInOrder=["minTimeout","maxTimeout"];

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
    <meta name="layout" content="adminLayout"/>
    <title>Show ${connector.type}Connector</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><g:link class="list" action="list">${connector.type}Connector List</g:link></span>
    <span class="menuButton"><g:link class="create" action="create" params='[type:"${connector.type}"]'>New ${connector.type}Connector</g:link></span>
</div>
<div class="body">
    <h1>Show ${connector.type}Connector</h1>
    <g:render template="/common/messages" model="[flash:flash]"></g:render>
    <div class="dialog">
        <table>
            <tbody>
                 <g:each var="prop" in="${propsInOrder}">
                     <tr class="prop">
                        <td valign="top" class="name">${propLabels[prop.name]}:</td>

                        <td valign="top" class="value">${connection?.getProperty(prop.name)}</td>

                    </tr>
                </g:each>


                 <tr class="prop">
                    <td valign="top" class="name">Sender Script File:</td>

                    <td valign="top" class="value">${script?.scriptFile}</td>

                </tr>

                <tr class="prop">
                    <td valign="top" class="name">Sender Script Period:</td>

                    <td valign="top" class="value">${script?.period} sec.</td>

                </tr>

                <tr class="prop">
                    <td valign="top" class="name">Log Level:</td>

                    <td valign="top" class="value">${script?.logLevel}</td>

                </tr>

                <tr class="prop">
                    <td valign="top" class="name">Show As Destination:</td>

                    <td valign="top" class="value">${connector?.showAsDestination}</td>

                </tr>

            </tbody>
        </table>
    </div>
    <div class="buttons">
        <g:form style="display:inline">
            <input type="hidden" name="id" value="${connector?.id}"/>
            <span class="button"><g:actionSubmit class="edit" value="Edit"/></span>
            <span class="button"><g:actionSubmit class="delete" onclick="return confirm('Are you sure?');" value="Delete"/></span>
        </g:form>
        
            <%
                def connScript = script;
                def targetURI="/notificationConnector/show/${connector.id}";
            %>
            <g:if test="${connScript?.enabled}">
                <g:link action="disable" controller="script" id="${connScript.name}" class="stop" params="[targetURI:targetURI]">Stop</g:link>
            </g:if>
            <g:else>
                <g:link action="enable" controller="script" id="${connScript.name}" class="start" params="[targetURI:targetURI]">Start</g:link>
            </g:else>

        <g:form style="display:inline" controller="script">
            <input type="hidden" name="id" value="${connScript.name}"/>
            <input type="hidden" name="targetURI" value="${targetURI}"/>
            <span class="button"><g:actionSubmit class="refresh" value="Reload Script" action="Reload"/></span>
        </g:form>
    </div>
</div>
</body>
</html>
