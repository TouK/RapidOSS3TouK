<html>
<head>
    <meta name="layout" content="indexLayout" />
</head>
<body>

<rui:searchList id="objectList" url="../rsBrowser/searchWithQuery?format=xml" rootTag="Objects" contentPath="Object" keyAttribute="id"
    lineSize="3" title="Objects" queryParameter="query" totalCountAttribute="total" offsetAttribute="offset" sortOrderAttribute="sortOrder"
    pollingInterval="0" defaultFields='${["id", "name"]}' showMax='6'
    
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

<rui:treeGrid id="classTree" url="../rsBrowser/classes?format=xml" rootTag="Classes" pollingInterval="0"
        keyAttribute="name" contentPath="Class" title="Classes" expanded="false"

        onNodeClicked="${['setQueryAction']}"
    
>
    <rui:tgColumns>

        <rui:tgColumn type="text" attributeName="name" colLabel="Class Name" width="300" sortBy="true" sortOrder="asc">
            
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
rootImage4619Visible="params.data.name != 'System' && params.data.name != 'Application'"
%>

        <rui:tgRootImage visible="${rootImage4619Visible}" expanded="../images/rapidjs/component/tools/class.png" collapsed="../images/rapidjs/component/tools/class.png"></rui:tgRootImage>
        <%
rootImage4621Visible="params.data.name == 'Application'"
%>

        <rui:tgRootImage visible="${rootImage4621Visible}" expanded="../images/rapidjs/component/tools/application.png" collapsed="../images/rapidjs/component/tools/application.png"></rui:tgRootImage>
        <%
rootImage4623Visible="params.data.name == 'System'"
%>

        <rui:tgRootImage visible="${rootImage4623Visible}" expanded="../images/rapidjs/component/tools/configure.png" collapsed="../images/rapidjs/component/tools/configure.png"></rui:tgRootImage>
        
    </rui:tgRootImages>
</rui:treeGrid>

<rui:html id="objectDetails" iframe="false"></rui:html>

<rui:html id="propsAndOperations" iframe="false"></rui:html>

<%
functionActionCondition4631Condition=""
%>

<rui:action id="browseAction" type="function" function="show" componentId='objectDetails' 

>
    
    <rui:functionArg><![CDATA[createURL('browser/browserObjectDetails.gsp', {id:params.data.id, domain:params.data.rsAlias})]]></rui:functionArg>
    
    <rui:functionArg><![CDATA['Details of ' + params.data.rsAlias + ' ' + params.data.id]]></rui:functionArg>
    
</rui:action>

<%
functionActionCondition4645Condition=""
%>

<rui:action id="propsAndOperationsAction" type="function" function="show" componentId='propsAndOperations' 

>
    
    <rui:functionArg><![CDATA[createURL('browser/propsAndOperations.gsp', {className:params.data.name})]]></rui:functionArg>
    
    <rui:functionArg><![CDATA['Properties and Operations of ' + params.data.name]]></rui:functionArg>
    
</rui:action>

<%
functionActionCondition4656Condition=""
%>

<rui:action id="greaterThanAction" type="function" function="appendToQuery" componentId='objectList' 

>
    
    <rui:functionArg><![CDATA[params.key + ':{' + params.value + ' TO *}']]></rui:functionArg>
    
</rui:action>

<%
functionActionCondition4665Condition=""
%>

<rui:action id="lessThanAction" type="function" function="appendToQuery" componentId='objectList' 

>
    
    <rui:functionArg><![CDATA[params.key + ':{* TO ' + params.value + '}']]></rui:functionArg>
    
</rui:action>

<%
functionActionCondition4674Condition=""
%>

<rui:action id="lessThanOrEqualToAction" type="function" function="appendToQuery" componentId='objectList' 

>
    
    <rui:functionArg><![CDATA[params.key + ':[* TO ' + params.value + ']']]></rui:functionArg>
    
</rui:action>

<%
functionActionCondition4683Condition=""
%>

<rui:action id="greaterThanOrEqualToAction" type="function" function="appendToQuery" componentId='objectList' 

>
    
    <rui:functionArg><![CDATA[params.key + ':[' + params.value + ' TO *]']]></rui:functionArg>
    
</rui:action>

<%
functionActionCondition4692Condition=""
%>

<rui:action id="exceptAction" type="function" function="appendExceptQuery" componentId='objectList' 

>
    
    <rui:functionArg><![CDATA[params.key]]></rui:functionArg>
    
    <rui:functionArg><![CDATA[params.value]]></rui:functionArg>
    
</rui:action>

<%
functionActionCondition4703Condition=""
%>

<rui:action id="sortAscAction" type="function" function="sort" componentId='objectList' 

>
    
    <rui:functionArg><![CDATA[params.key]]></rui:functionArg>
    
    <rui:functionArg><![CDATA['asc']]></rui:functionArg>
    
</rui:action>

<%
functionActionCondition4714Condition=""
%>

<rui:action id="sortDescAction" type="function" function="sort" componentId='objectList' 

>
    
    <rui:functionArg><![CDATA[params.key]]></rui:functionArg>
    
    <rui:functionArg><![CDATA['desc']]></rui:functionArg>
    
</rui:action>

<%
functionActionCondition4725Condition="params.data.name != 'System' && params.data.name != 'Application'"
%>

<rui:action id="setQueryAction" type="function" function="setQuery" componentId='objectList' condition="$functionActionCondition4725Condition"

>
    
    <rui:functionArg><![CDATA['']]></rui:functionArg>
    
    <rui:functionArg><![CDATA['id']]></rui:functionArg>
    
    <rui:functionArg><![CDATA['asc']]></rui:functionArg>
    
    <rui:functionArg><![CDATA[{domain:params.data.logicalName}]]></rui:functionArg>
    
</rui:action>

<rui:popupWindow componentId="objectDetails" width="850" height="500" resizable="true"
 
 
 
></rui:popupWindow>

<rui:popupWindow componentId="propsAndOperations" width="550" height="600" resizable="true"
 
 
 
></rui:popupWindow>





       <rui:include template="pageContents/_browser.gsp"></rui:include>


<rui:layout id="layout">
    <rui:layoutUnit position="top" body="top" resize="false" height="45"></rui:layoutUnit>
    <rui:layoutUnit position="center" gutter="1px">
        


    <rui:innerLayout id="4586">
        
            <rui:layoutUnit position='center' gutter='0px' id='4745' isActive='true' scroll='false' useShim='false' component='objectList'>
        
            </rui:layoutUnit>
        
            <rui:layoutUnit position='left' gutter='0 5 0 0' id='4748' isActive='true' resize='true' scroll='false' useShim='false' width='345' component='classTree'>
        
            </rui:layoutUnit>
        
        </rui:innerLayout>
        


    </rui:layoutUnit>
</rui:layout>
</body>
</html>