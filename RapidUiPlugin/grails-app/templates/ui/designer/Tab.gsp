<html>
<head>
    <meta name="layout" content="${tab.webPage.name}Layout" />
</head>
<body>
<%
    print tabContent;
    def layoutUnitsHavingContentFile = ui.designer.UiLayoutUnit.getLayoutUnitHavingContentFile(tab.layout);
%>

<%
    layoutUnitsHavingContentFile.each{layoutUnit->
%>
    <div id="${layoutUnit.getContentFileDivId()}">
        <rui:include template="${layoutUnit.contentFile}"></rui:include>
    </div>
<%
    }
%>
<%
    if(tab.contentFile != null && tab.contentFile != "")
    {
%>
       <rui:include template="${tab.contentFile}"></rui:include>
<%
    }
%>
<rui:layout id="layout">
    <rui:layoutUnit position="top" body="top" resize="false" height="45"></rui:layoutUnit>
    <rui:layoutUnit position="center" gutter="1px">
        ${layoutContent}
    </rui:layoutUnit>
</rui:layout>
</body>
</html>