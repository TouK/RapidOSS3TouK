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
    
    </rui:sgMenuItems>
    <rui:sgImages>
    
    </rui:sgImages>
    <rui:sgColumns>
    
        <rui:sgColumn attributeName="operation" colLabel="Opr" width="30"   type="image">
            
            <rui:sgColumnImages>
                <%
image150Visible="params.data.operation == \"delete\""
%>

                <rui:sgColumnImage src="../images/remoteApplicationDevelopment/delete.png" visible="${image150Visible}" align="left"></rui:sgColumnImage>
                <%
image152Visible="params.data.operation == \"copy\""
%>

                <rui:sgColumnImage src="../images/remoteApplicationDevelopment/copy.png" visible="${image152Visible}" align="left"></rui:sgColumnImage>
                
            </rui:sgColumnImages>
            

        </rui:sgColumn>
    
        <rui:sgColumn attributeName="filePath" colLabel="File" width="600"   type="text">
            

        </rui:sgColumn>
    
    </rui:sgColumns>
    <rui:sgRowColors>
    
    </rui:sgRowColors>
</rui:searchGrid>

<%
mergeActionCondition160Condition=""
%>

<rui:action id="commitAction" type="merge" url="../script/run/modificationOperation" components="${['modificationList']}" submitType="GET" removeAttribute='willBeRemoved' 

>
    <%
parameter163Visible="params.data.id"
%>

    <rui:requestParam key="modificationId" value="${parameter163Visible}"></rui:requestParam>
    <%
parameter165Visible="'commit'"
%>

    <rui:requestParam key="operation" value="${parameter165Visible}"></rui:requestParam>
    
</rui:action>

<%
mergeActionCondition171Condition=""
%>

<rui:action id="ignoreAction" type="merge" url="../script/run/modificationOperation" components="${['modificationList']}" submitType="GET" removeAttribute='willBeRemoved' 

>
    <%
parameter174Visible="params.data.id"
%>

    <rui:requestParam key="modificationId" value="${parameter174Visible}"></rui:requestParam>
    <%
parameter176Visible="'ignore'"
%>

    <rui:requestParam key="operation" value="${parameter176Visible}"></rui:requestParam>
    
</rui:action>






<rui:layout id="layout">
    <rui:layoutUnit position="top" body="top" resize="false" height="45"></rui:layoutUnit>
    <rui:layoutUnit position="center" gutter="1px">
        


    <rui:innerLayout id="142">
        
            <rui:layoutUnit position='center' gutter='0px' id='182' isActive='true' scroll='false' useShim='false' component='modificationList'>
        
            </rui:layoutUnit>
        
        </rui:innerLayout>
        


    </rui:layoutUnit>
</rui:layout>
</body>
</html>