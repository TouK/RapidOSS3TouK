<%@ page import="auth.Group; auth.SegmentFilter" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="adminLayout"/>
    <title>Create SegmentFilter</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLinkTo(dir: '/')}"></a></span>
</div>
<div class="body">
    <g:form method="post" controller="uploader" action="upload"
      enctype="multipart/form-data">
        <input type="file" name="file"/>
        <div class="buttons">
            <span class="button"><g:actionSubmit class="save" value="Upload"/></span>
        </div>
    </g:form>
</div>
</body>
</html>
