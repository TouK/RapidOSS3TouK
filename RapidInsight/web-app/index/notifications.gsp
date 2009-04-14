<html>
<head>
    <meta name="layout" content="indexLayout" />
</head>
<body>
<rui:treeGrid id="ruleTree" url="../rsMessageRule/list" rootTag="Rules" pollingInterval="0" timeout="30"
        keyAttribute="id" contentPath="Rule" title="Your Notification List" expanded="true"

        onNodeClicked="${['ruleUpdateAction']}"
    
>
    <rui:tgColumns>

        <rui:tgColumn type="text" attributeName="name" colLabel="Search Query" width="250"   sortType="string">
            
        </rui:tgColumn>

        <rui:tgColumn type="text" attributeName="enabled" colLabel="Enabled" width="65"   sortType="string">
            
        </rui:tgColumn>

        <rui:tgColumn type="text" attributeName="delay" colLabel="Delay" width="50"   sortType="int">
            
        </rui:tgColumn>

        <rui:tgColumn type="text" attributeName="clearAction" colLabel="Send Clear Events" width="120"   sortType="string">
            
        </rui:tgColumn>

    </rui:tgColumns>    
    <rui:tgMenuItems>
        <%
disableVisible="params.data.enabled=='true' && params.data.nodeType =='rule'"
%>

        <rui:tgMenuItem id="disable" label="Disable" visible="${disableVisible}" action="${['ruleDisableAction']}">
               
        </rui:tgMenuItem>
        <%
enableVisible="params.data.enabled=='false' && params.data.nodeType =='rule'"
%>

        <rui:tgMenuItem id="enable" label="Enable" visible="${enableVisible}" action="${['ruleEnableAction']}">
               
        </rui:tgMenuItem>
        <%
deleteVisible="params.data.nodeType =='rule'"
%>

        <rui:tgMenuItem id="delete" label="Delete" visible="${deleteVisible}" action="${['ruleDeleteAction']}">
               
        </rui:tgMenuItem>
        
    </rui:tgMenuItems>
    <rui:tgRootImages>
        <%
rootImage4589Visible="params.data.nodeType == 'group'"
%>

        <rui:tgRootImage visible="${rootImage4589Visible}" expanded="../images/rapidjs/component/tools/folder_open.gif" collapsed="../images/rapidjs/component/tools/folder.gif"></rui:tgRootImage>
        
    </rui:tgRootImages>
</rui:treeGrid>

<rui:html id="ruleContent" iframe="false"  timeout="30"></rui:html>

<%
functionActionCondition4599Condition="params.data.nodeType=='rule'"
%>

<rui:action id="ruleUpdateAction" type="function" function="show" componentId='ruleContent' condition="$functionActionCondition4599Condition"

>
    
    <rui:functionArg><![CDATA[createURL('rsMessageRuleForm.gsp',{mode:'edit',ruleId:params.data.id})]]></rui:functionArg>
    
    <rui:functionArg><![CDATA['Rule Details For Search Query: ' + params.data.name]]></rui:functionArg>
    
</rui:action>

<%
requestActionCondition4609Condition=""
%>

<rui:action id="ruleDisableAction" type="request" url="../rsMessageRule/disableRule" components="${['ruleTree']}" 

        onSuccess="${['ruleTreePoll']}"
    
>
    <%
parameter4612Visible="params.data.id"
%>

    <rui:requestParam key="id" value="${parameter4612Visible}"></rui:requestParam>
    
</rui:action>

<%
requestActionCondition4618Condition=""
%>

<rui:action id="ruleEnableAction" type="request" url="../rsMessageRule/enableRule" components="${['ruleContent']}" 

        onSuccess="${['ruleTreePoll']}"
    
>
    <%
parameter4621Visible="params.data.id"
%>

    <rui:requestParam key="id" value="${parameter4621Visible}"></rui:requestParam>
    
</rui:action>

<%
requestActionCondition4627Condition="confirm('Are you sure to delete the Rule with Search Query: '+params.data.name)"
%>

<rui:action id="ruleDeleteAction" type="request" url="../rsMessageRule/delete" components="${['ruleTree']}" condition="$requestActionCondition4627Condition"

        onSuccess="${['ruleContentPoll','ruleTreePoll']}"
    
>
    <%
parameter4630Visible="params.data.id"
%>

    <rui:requestParam key="id" value="${parameter4630Visible}"></rui:requestParam>
    
</rui:action>

<%
functionActionCondition4636Condition=""
%>

<rui:action id="ruleContentPoll" type="function" function="show" componentId='ruleContent' 

>
    
    <rui:functionArg><![CDATA[YAHOO.rapidjs.Components['ruleContent'].url]]></rui:functionArg>
    
    <rui:functionArg><![CDATA[YAHOO.rapidjs.Components['ruleContent'].title]]></rui:functionArg>
    
</rui:action>

<%
functionActionCondition4646Condition=""
%>

<rui:action id="ruleTreePoll" type="function" function="poll" componentId='ruleTree' 

>
    
</rui:action>





       <rui:include template="pageContents/_notifications.gsp" model="${binding.variables}"></rui:include>


<rui:layout id="layout">
    <rui:layoutUnit position="top" body="top" resize="false" height="45"></rui:layoutUnit>
    <rui:layoutUnit position="center" gutter="1px">
        


    <rui:innerLayout id="4575">
        
            <rui:layoutUnit position='center' gutter='0px' id='4658' isActive='true' scroll='false' useShim='false' component='ruleContent'>
        
            </rui:layoutUnit>
        
            <rui:layoutUnit position='left' gutter='0 5 0 0' id='4661' isActive='true' resize='false' scroll='false' useShim='false' width='500' component='ruleTree'>
        
            </rui:layoutUnit>
        
        </rui:innerLayout>
        


    </rui:layoutUnit>
</rui:layout>
</body>
</html>