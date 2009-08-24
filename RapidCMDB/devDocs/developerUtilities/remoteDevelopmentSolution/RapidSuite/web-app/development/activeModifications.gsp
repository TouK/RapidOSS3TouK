<html>
<head>
    <meta name="layout" content="developmentLayout" />
</head>
<body>
<rui:searchGrid id="modificationList" url="../script/run/getActiveModifications" queryParameter="query" rootTag="Modifications" contentPath="Modification"
        keyAttribute="id" totalCountAttribute="total" offsetAttribute="offset" sortOrderAttribute="sortOrder" title="Modifications"
        pollingInterval="10" fieldsUrl="../script/run/getModificationFieldList?format=xml" queryEnabled="false" defaultQuery="" timeout="30"
        defaultSearchClass="RemoteApplicationModification" 
    
>
    <rui:sgMenuItems>
    <%
commitVisible="true"
%>

        <rui:sgMenuItem id="commit" label="Commit" visible="${commitVisible}" action="${['commitAction']}">
            
        </rui:sgMenuItem>
    <%
ignoreVisible="true"
%>

        <rui:sgMenuItem id="ignore" label="Ignore Changes" visible="${ignoreVisible}" action="${['ignoreAction']}">
            
        </rui:sgMenuItem>
    <%
ignoreAllVisible="true"
%>

        <rui:sgMenuItem id="ignoreAll" label="Ignore All" visible="${ignoreAllVisible}" action="${['ignoreAllAction']}">
            
        </rui:sgMenuItem>
    
    </rui:sgMenuItems>
    <rui:sgImages>
    
    </rui:sgImages>
    <rui:sgColumns>
    
        <rui:sgColumn attributeName="operation" colLabel="Opr" width="30"   type="image">
            
            <rui:sgColumnImages>
                <%
image235Visible="params.data.operation == \"delete\""
%>

                <rui:sgColumnImage src="../images/remoteApplicationDevelopment/delete.png" visible="${image235Visible}" align="left"></rui:sgColumnImage>
                <%
image237Visible="params.data.operation == \"copy\""
%>

                <rui:sgColumnImage src="../images/remoteApplicationDevelopment/copy.png" visible="${image237Visible}" align="left"></rui:sgColumnImage>
                
            </rui:sgColumnImages>
            

        </rui:sgColumn>
    
        <rui:sgColumn attributeName="relativeFilePath" colLabel="File" width="600"   type="text">
            

        </rui:sgColumn>
    
    </rui:sgColumns>
    <rui:sgRowColors>
    
    </rui:sgRowColors>
</rui:searchGrid>

<%
mergeActionCondition247Condition=""
%>

<rui:action id="commitAction" type="merge" url="../script/run/modificationOperation" components="${['modificationList']}" submitType="GET" removeAttribute='willBeRemoved' 

>
    <%
parameter250Visible="params.data.id"
%>

    <rui:requestParam key="modificationId" value="${parameter250Visible}"></rui:requestParam>
    <%
parameter252Visible="'commit'"
%>

    <rui:requestParam key="operation" value="${parameter252Visible}"></rui:requestParam>
    
</rui:action>

<%
mergeActionCondition258Condition=""
%>

<rui:action id="ignoreAction" type="merge" url="../script/run/modificationOperation" components="${['modificationList']}" submitType="GET" removeAttribute='willBeRemoved' 

>
    <%
parameter261Visible="params.data.id"
%>

    <rui:requestParam key="modificationId" value="${parameter261Visible}"></rui:requestParam>
    <%
parameter263Visible="'ignore'"
%>

    <rui:requestParam key="operation" value="${parameter263Visible}"></rui:requestParam>
    
</rui:action>

<%
mergeActionCondition269Condition=""
%>

<rui:action id="ignoreAllAction" type="merge" url="../script/run/ignoreAllChanges" components="${['modificationList']}" submitType="GET"  

>
    
</rui:action>






<rui:layout id="layout">
    <rui:layoutUnit position="top" body="top" resize="false" height="45"></rui:layoutUnit>
    <rui:layoutUnit position="center" gutter="1px">
        


    <rui:innerLayout id="227">
        
            <rui:layoutUnit position='center' gutter='0px' id='276' isActive='true' scroll='false' useShim='false' component='modificationList'>
        
            </rui:layoutUnit>
        
        </rui:innerLayout>
        


    </rui:layoutUnit>
</rui:layout>
</body>
</html>