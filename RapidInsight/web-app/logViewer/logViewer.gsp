<html>
<head>
    <meta name="layout" content="logViewerLayout" />
</head>
<body>
<rui:treeGrid id="fileListTree" url="../viewLog/listFiles" rootTag="Files" pollingInterval="30" timeout="30"
        keyAttribute="name" expandAttribute="" contentPath="File" title="File List" expanded="false"

>
    <rui:tgColumns>

        <rui:tgColumn type="text" attributeName="name" colLabel="Path" width="200"   sortType="string">
            
        </rui:tgColumn>

    </rui:tgColumns>    
    <rui:tgMenuItems>
        
    </rui:tgMenuItems>
    <rui:tgRootImages>
        
    </rui:tgRootImages>
</rui:treeGrid>




    <div id="loyutUnit1web-app.logContent.gsp">
        <rui:include template="logContent.gsp" model="${binding.variables}"></rui:include>
    </div>


       <rui:include template="pageContents/_logViewer.gsp" model="${binding.variables}"></rui:include>


<rui:layout id="layout">
    <rui:layoutUnit position="top" body="top" resize="false" height="45"></rui:layoutUnit>
    <rui:layoutUnit position="center" gutter="1px">
        


<rui:innerLayout id="0">
    
    <rui:layoutUnit position='center' useShim='false' gutter='0px' contentFile='logContent.gsp' scroll='false' body='loyutUnit1web-app.logContent.gsp'>
        
    </rui:layoutUnit>
    
    <rui:layoutUnit position='left' width='200' gutter='0px' resize='true' useShim='false' scroll='false' component='fileListTree'>
        
    </rui:layoutUnit>
    
</rui:innerLayout>



    </rui:layoutUnit>
</rui:layout>
</body>
</html>