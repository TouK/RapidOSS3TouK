<html>
<script type="text/javascript">
    function callFunction(functionName, params)
    {
        return window.parent[functionName](params)
    }
    function callFlashFunction(functionName, params)
    {
        return document.getElementById("mapDivflashObject")[functionName].apply(document.getElementById("mapDivflashObject"), params);
    }
</script>
<body style="margin:0px;overflow:hidden;">
    <%
    	System.out.println(request.getHeader("user-agent"));
        if(request.getHeader("user-agent").indexOf("MSIE") < 0)
        {
    %>
    <embed height="100%" width="100%" wmode="Transparent" allowscriptaccess="always" quality="high" bgcolor="#eeeeee" name="mapDiv" id="mapDivflashObject" style="" src="TopologyMapping.swf?configFunction=${request.getParameter("configFunction")}" type="application/x-shockwave-flash"/>
    <%
        }else{
    %>
    <OBJECT id="mapDivflashObject" height="100%" width="100%" classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000">
		<PARAM value="TopologyMapping.swf?configFunction=${request.getParameter("configFunction")}" name="movie" />
		<PARAM value="#eeeeee" name="bgcolor" />
		<PARAM value="high" name="quality" />
		<PARAM value="always" name="allowScriptAccess" />
		<PARAM value="Transparent" name="wmode" />
	</OBJECT>
    <%
        }
    %>
</body>
</html>