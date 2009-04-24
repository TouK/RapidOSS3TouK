<html>
<head>
    <meta name="layout" content="indexLayout" />
</head>
<body>

<rui:searchList id="objectList" url="../rsBrowser/searchWithQuery?format=xml" rootTag="Objects" contentPath="Object" keyAttribute="id"
    lineSize="3" title="Objects" queryParameter="query" totalCountAttribute="total" offsetAttribute="offset" sortOrderAttribute="sortOrder"
    pollingInterval="0" defaultFields='${["id", "name"]}' showMax='6' defaultQuery=""
    defaultSearchClass="connection.Connection" searchClassesUrl="../rsBrowser/getSearchClasses" timeout="30"

        onRowDoubleClicked="${['browseAction']}"

>
    <rui:slMenuItems>
        <%
browseVisible="true"
%>

        <rui:slMenuItem id="browse" label="Browse" visible="${browseVisible}" action="${['browseAction']}">

        </rui:slMenuItem>

    </rui:slMenuItems>
    <rui:slPropertyMenuItems>
        <%
sortAscVisible="true"
%>

        <rui:slMenuItem id="sortAsc" label="Sort Asc" action="${['sortAscAction']}" visible="${sortAscVisible}">

        </rui:slMenuItem>
        <%
sortDescVisible="true"
%>

        <rui:slMenuItem id="sortDesc" label="Sort Desc" action="${['sortDescAction']}" visible="${sortDescVisible}">

        </rui:slMenuItem>
        <%
greaterThanVisible="YAHOO.lang.isNumber(parseInt(params.value))"
%>

        <rui:slMenuItem id="greaterThan" label="Greater Than" action="${['greaterThanAction']}" visible="${greaterThanVisible}">

        </rui:slMenuItem>
        <%
lessThanVisible="YAHOO.lang.isNumber(parseInt(params.value))"
%>

        <rui:slMenuItem id="lessThan" label="Less Than" action="${['lessThanAction']}" visible="${lessThanVisible}">

        </rui:slMenuItem>
        <%
greaterThanOrEqualToVisible="YAHOO.lang.isNumber(parseInt(params.value))"
%>

        <rui:slMenuItem id="greaterThanOrEqualTo" label="Greater than or equal to" action="${['greaterThanOrEqualToAction']}" visible="${greaterThanOrEqualToVisible}">

        </rui:slMenuItem>
        <%
lessThanOrEqualToVisible="YAHOO.lang.isNumber(parseInt(params.value))"
%>

        <rui:slMenuItem id="lessThanOrEqualTo" label="Less than or equal to" action="${['lessThanOrEqualToAction']}" visible="${lessThanOrEqualToVisible}">

        </rui:slMenuItem>
        <%
exceptVisible="true"
%>

        <rui:slMenuItem id="except" label="Except" action="${['exceptAction']}" visible="${exceptVisible}">

        </rui:slMenuItem>

    </rui:slPropertyMenuItems>
     <rui:slFields>

    </rui:slFields>
    <rui:slImages>

    </rui:slImages>
</rui:searchList>

<rui:treeGrid id="classTree" url="../rsBrowser/classes?format=xml" rootTag="Classes" pollingInterval="0" timeout="30"
        keyAttribute="name" contentPath="Class" title="Classes" expanded="false"



>
    <rui:tgColumns>

        <rui:tgColumn type="text" attributeName="name" colLabel="Class Name" width="300" sortBy="true" sortOrder="asc" sortType="string">

        </rui:tgColumn>

    </rui:tgColumns>
    <rui:tgMenuItems>
        <%
propsAndOperationsVisible="params.data.name != 'System' && params.data.name != 'Application'"
%>

        <rui:tgMenuItem id="propsAndOperations" label="Get Properties and Operations" visible="${propsAndOperationsVisible}" action="${['propsAndOperationsAction']}">

        </rui:tgMenuItem>

    </rui:tgMenuItems>
    <rui:tgRootImages>
        <%
rootImage2738Visible="params.data.name != 'System' && params.data.name != 'Application'"
%>

        <rui:tgRootImage visible="${rootImage2738Visible}" expanded="../images/rapidjs/component/tools/class.png" collapsed="../images/rapidjs/component/tools/class.png"></rui:tgRootImage>
        <%
rootImage2740Visible="params.data.name == 'Application'"
%>

        <rui:tgRootImage visible="${rootImage2740Visible}" expanded="../images/rapidjs/component/tools/application.png" collapsed="../images/rapidjs/component/tools/application.png"></rui:tgRootImage>
        <%
rootImage2742Visible="params.data.name == 'System'"
%>

        <rui:tgRootImage visible="${rootImage2742Visible}" expanded="../images/rapidjs/component/tools/configure.png" collapsed="../images/rapidjs/component/tools/configure.png"></rui:tgRootImage>

    </rui:tgRootImages>
</rui:treeGrid>

<rui:html id="objectDetails" iframe="false"  timeout="30"></rui:html>

<rui:html id="propsAndOperations" iframe="false"  timeout="30"></rui:html>

<%
functionActionCondition2750Condition=""
%>

<rui:action id="browseAction" type="function" function="show" componentId='objectDetails'

>

    <rui:functionArg><![CDATA[createURL('browser/browserObjectDetails.gsp', {id:params.data.id, domain:params.data.rsAlias})]]></rui:functionArg>

    <rui:functionArg><![CDATA['Details of ' + params.data.rsAlias + ' ' + params.data.id]]></rui:functionArg>

</rui:action>

<%
functionActionCondition2764Condition=""
%>

<rui:action id="propsAndOperationsAction" type="function" function="show" componentId='propsAndOperations'

>

    <rui:functionArg><![CDATA[createURL('browser/propsAndOperations.gsp', {className:params.data.name})]]></rui:functionArg>

    <rui:functionArg><![CDATA['Properties and Operations of ' + params.data.name]]></rui:functionArg>

</rui:action>

<%
functionActionCondition2775Condition=""
%>

<rui:action id="greaterThanAction" type="function" function="appendToQuery" componentId='objectList'

>

    <rui:functionArg><![CDATA[params.key + ':{' + params.value.toQuery() + ' TO *}']]></rui:functionArg>

</rui:action>

<%
functionActionCondition2784Condition=""
%>

<rui:action id="lessThanAction" type="function" function="appendToQuery" componentId='objectList'

>

    <rui:functionArg><![CDATA[params.key + ':{* TO ' + params.value.toQuery() + '}']]></rui:functionArg>

</rui:action>

<%
functionActionCondition2793Condition=""
%>

<rui:action id="lessThanOrEqualToAction" type="function" function="appendToQuery" componentId='objectList'

>

    <rui:functionArg><![CDATA[params.key + ':[* TO ' + params.value.toQuery() + ']']]></rui:functionArg>

</rui:action>

<%
functionActionCondition2802Condition=""
%>

<rui:action id="greaterThanOrEqualToAction" type="function" function="appendToQuery" componentId='objectList'

>

    <rui:functionArg><![CDATA[params.key + ':[' + params.value.toQuery() + ' TO *]']]></rui:functionArg>

</rui:action>

<%
functionActionCondition2811Condition=""
%>

<rui:action id="exceptAction" type="function" function="appendExceptQuery" componentId='objectList'

>

    <rui:functionArg><![CDATA[params.key]]></rui:functionArg>

    <rui:functionArg><![CDATA[params.value]]></rui:functionArg>

</rui:action>

<%
functionActionCondition2822Condition=""
%>

<rui:action id="sortAscAction" type="function" function="sort" componentId='objectList'

>

    <rui:functionArg><![CDATA[params.key]]></rui:functionArg>

    <rui:functionArg><![CDATA['asc']]></rui:functionArg>

</rui:action>

<%
functionActionCondition2833Condition=""
%>

<rui:action id="sortDescAction" type="function" function="sort" componentId='objectList'

>

    <rui:functionArg><![CDATA[params.key]]></rui:functionArg>

    <rui:functionArg><![CDATA['desc']]></rui:functionArg>

</rui:action>


<rui:popupWindow componentId="objectDetails" width="850" height="500" resizable="true"



></rui:popupWindow>

<rui:popupWindow componentId="propsAndOperations" width="550" height="600" resizable="true"



></rui:popupWindow>





       <rui:include template="pageContents/_browser.gsp" model="${binding.variables}"></rui:include>


<rui:layout id="layout">
    <rui:layoutUnit position="top" body="top" resize="false" height="45"></rui:layoutUnit>
    <rui:layoutUnit position="center" gutter="1px">



    <rui:innerLayout id="2705">

            <rui:layoutUnit position='left' gutter='0 5 0 0' id='2869' isActive='true' resize='true' scroll='false' useShim='false' width='345' component='classTree'>

            </rui:layoutUnit>

            <rui:layoutUnit position='center' gutter='0px' id='2866' isActive='true' scroll='false' useShim='false' component='objectList'>

            </rui:layoutUnit>

        </rui:innerLayout>



    </rui:layoutUnit>
</rui:layout>
</body>
</html>