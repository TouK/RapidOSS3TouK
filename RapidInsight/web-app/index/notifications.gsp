<html>
<head>
    <meta name="layout" content="indexLayout" />
</head>
<body>
<rui:treeGrid id="ruleTree" url="../rsMessageRule/list" rootTag="Rules" pollingInterval="0" timeout="30"
        keyAttribute="id" expandAttribute="" contentPath="Rule" title="Your Notification List" expanded="true"

>
    <rui:tgColumns>

        <rui:tgColumn type="text" attributeName="name" colLabel="Search Query" width="250"   sortType="string">
            
        </rui:tgColumn>

        <rui:tgColumn type="text" attributeName="destinationType" colLabel="Destination Type" width="120"   sortType="string">
            
        </rui:tgColumn>

        <rui:tgColumn type="text" attributeName="enabled" colLabel="Enabled" width="65"   sortType="string">
            
        </rui:tgColumn>

        <rui:tgColumn type="text" attributeName="delay" colLabel="Delay" width="50"   sortType="int">
            
        </rui:tgColumn>

        <rui:tgColumn type="text" attributeName="sendClearEventType" colLabel="Send Clear Events" width="120"   sortType="string">
            
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
        <%
updateVisible="true"
%>

        <rui:tgMenuItem id="update" label="Update" visible="${updateVisible}" action="${['ruleUpdateAction','ruleUpdateAction2']}">
               
        </rui:tgMenuItem>
        
    </rui:tgMenuItems>
     <rui:tgMultiSelectionMenuItems>
    
    </rui:tgMultiSelectionMenuItems>
    <rui:tgRootImages>
        <%
rootImage320Visible="params.data.nodeType == 'group'"
%>

        <rui:tgRootImage visible="${rootImage320Visible}" expanded="../images/rapidjs/component/tools/folder_open.gif" collapsed="../images/rapidjs/component/tools/folder.gif"></rui:tgRootImage>
        
    </rui:tgRootImages>
</rui:treeGrid>


<rui:html id="addRuleForm" iframe="false"  timeout="30"  pollingInterval="0" title="Rule Details"></rui:html>

<rui:searchGrid id="calendars" url="../search" queryParameter="query" rootTag="Objects" contentPath="Object" bringAllProperties="true" 
        keyAttribute="id"  title="Calendars" pollingInterval="0" fieldsUrl="../script/run/getViewFields?format=xml&rootClass=message.RsMessageRuleCalendar"
        queryEnabled="false" searchInEnabled="false" defaultQuery="" timeout="30" multipleFieldSorting="true"
        defaultSearchClass="message.RsMessageRuleCalendar" defaultView="default"  extraPropertiesToRequest=""
    
>
    
    <rui:sgMenuItems>
    <%
updateVisible="params.data.isPublic != 'true' || window.currentUserHasRole('Administrator')"
%>

        <rui:sgMenuItem id="update" label="Update" visible="${updateVisible}" action="${['calendarUpdateAction']}">
            
        </rui:sgMenuItem>
    <%
deleteVisible="params.data.isPublic != 'true' || window.currentUserHasRole('Administrator')"
%>

        <rui:sgMenuItem id="delete" label="Delete" visible="${deleteVisible}" action="${['calendarDeleteAction']}">
            
        </rui:sgMenuItem>
    
    </rui:sgMenuItems>
     <rui:sgMultiSelectionMenuItems>
    
    </rui:sgMultiSelectionMenuItems>
    <rui:sgImages>
    
    </rui:sgImages>
    <rui:sgColumns>
    
        <rui:sgColumn attributeName="name" colLabel="Name" width="100"   type="text">
            

        </rui:sgColumn>
    
        <rui:sgColumn attributeName="daysString" colLabel="On" width="150"   type="text">
            

        </rui:sgColumn>
    
        <rui:sgColumn attributeName="starting" colLabel="Start time" width="100"   type="text">
            

        </rui:sgColumn>
    
        <rui:sgColumn attributeName="ending" colLabel="End time" width="100"   type="text">
            

        </rui:sgColumn>
    
    </rui:sgColumns>
    <rui:sgRowColors>
    
    </rui:sgRowColors>
</rui:searchGrid>


<rui:html id="addCalendarForm" iframe="false"  timeout="30"  pollingInterval="0" title=""></rui:html>


<rui:html id="addRuleForPeopleForm" iframe="false"  timeout="30"  pollingInterval="0" title="Add Rule For User And Groups"></rui:html>

<%
requestActionConditionindex_notifications_ruleEnableActionCondition=""
%>

<rui:action id="ruleEnableAction" type="request" url="../rsMessageRule/enableRule" components="${['ruleTree']}" submitType="GET" 

        onSuccess="${['ruleTreePoll']}"
    
>
    <%
parameterindex_notifications_ruleEnableAction_idVisible="params.data.id"
%>

    <rui:requestParam key="id" value="${parameterindex_notifications_ruleEnableAction_idVisible}"></rui:requestParam>
    
</rui:action>

<%
requestActionConditionindex_notifications_ruleDeleteActionCondition="confirm('Are you sure to delete the Rule with Search Query: '+params.data.name)"
%>

<rui:action id="ruleDeleteAction" type="request" url="../rsMessageRule/delete" components="${['ruleTree']}" submitType="GET" condition="$requestActionConditionindex_notifications_ruleDeleteActionCondition"

        onSuccess="${['ruleTreePoll']}"
    
>
    <%
parameterindex_notifications_ruleDeleteAction_idVisible="params.data.id"
%>

    <rui:requestParam key="id" value="${parameterindex_notifications_ruleDeleteAction_idVisible}"></rui:requestParam>
    
</rui:action>

<%
requestActionConditionindex_notifications_ruleDisableActionCondition=""
%>

<rui:action id="ruleDisableAction" type="request" url="../rsMessageRule/disableRule" components="${['ruleTree']}" submitType="GET" 

        onSuccess="${['ruleTreePoll']}"
    
>
    <%
parameterindex_notifications_ruleDisableAction_idVisible="params.data.id"
%>

    <rui:requestParam key="id" value="${parameterindex_notifications_ruleDisableAction_idVisible}"></rui:requestParam>
    
</rui:action>

<%
functionActionConditionindex_notifications_ruleUpdateActionCondition="params.data.ruleType != 'public'"
%>

<rui:action id="ruleUpdateAction" type="function" function="show" componentId="addRuleForm" condition="$functionActionConditionindex_notifications_ruleUpdateActionCondition"

>
    
    <rui:functionArg><![CDATA[createURL('rsMessageRuleForm.gsp', {mode:'edit', ruleId:params.data.id})]]></rui:functionArg>
    
    <rui:functionArg><![CDATA['Update Rule']]></rui:functionArg>
    
</rui:action>

<%
functionActionConditionindex_notifications_ruleUpdateAction2Condition="params.data.ruleType == 'public'"
%>

<rui:action id="ruleUpdateAction2" type="function" function="show" componentId="addRuleForPeopleForm" condition="$functionActionConditionindex_notifications_ruleUpdateAction2Condition"

>
    
    <rui:functionArg><![CDATA[createURL('rsMessageRuleForm.gsp', {mode:'edit', ruleId:params.data.id})]]></rui:functionArg>
    
    <rui:functionArg><![CDATA['Update Rule']]></rui:functionArg>
    
</rui:action>

<%
requestActionConditionindex_notifications_calendarDeleteActionCondition="confirm('Are you sure?')"
%>

<rui:action id="calendarDeleteAction" type="request" url="../rsMessageRuleCalendar/delete" components="${['calendars']}" submitType="POST" condition="$requestActionConditionindex_notifications_calendarDeleteActionCondition"

        onSuccess="${['calendarsRefreshAction']}"
    
>
    <%
parameterindex_notifications_calendarDeleteAction_idVisible="params.data.id"
%>

    <rui:requestParam key="id" value="${parameterindex_notifications_calendarDeleteAction_idVisible}"></rui:requestParam>
    
</rui:action>

<%
functionActionConditionindex_notifications_calendarsRefreshActionCondition=""
%>

<rui:action id="calendarsRefreshAction" type="function" function="poll" componentId="calendars" 

>
    
</rui:action>

<%
functionActionConditionindex_notifications_calendarUpdateActionCondition=""
%>

<rui:action id="calendarUpdateAction" type="function" function="show" componentId="addCalendarForm" 

>
    
    <rui:functionArg><![CDATA[createURL('rsMessageRuleCalendarForm.gsp', {mode:'edit', calendarId: params.data.id})]]></rui:functionArg>
    
    <rui:functionArg><![CDATA['Update Calendar']]></rui:functionArg>
    
</rui:action>

<%
functionActionConditionindex_notifications_ruleTreePollCondition=""
%>

<rui:action id="ruleTreePoll" type="function" function="poll" componentId="ruleTree" 

>
    
</rui:action>

<rui:popupWindow componentId="addRuleForm" width="380" height="250" resizable="false"
 
 
  
></rui:popupWindow>

<rui:popupWindow componentId="addCalendarForm" width="440" height="300" resizable="false"
 
 
  
></rui:popupWindow>

<rui:popupWindow componentId="addRuleForPeopleForm" width="560" height="520" resizable="false"
 
 
  
></rui:popupWindow>





       <rui:include template="pageContents/_notifications.gsp" model="${binding.variables}"></rui:include>


<rui:layout id="layout">
    <rui:layoutUnit position="top" body="top" resize="false" height="45"></rui:layoutUnit>
    <rui:layoutUnit position="center" gutter="1px">
        


<rui:innerLayout id="338">
    
    <rui:layoutUnit position='center' gutter='0px' useShim='false' scroll='false' component='ruleTree'>
        
    </rui:layoutUnit>
    
    <rui:layoutUnit position='bottom' gutter='5 0 0 0' resize='true' height='300' useShim='false' scroll='false' component='calendars'>
        
    </rui:layoutUnit>
    
</rui:innerLayout>



    </rui:layoutUnit>
</rui:layout>
</body>
</html>