<html>
<head>
    <link rel="stylesheet" type="text/css" href="<g:createLinkTo dir="${pluginContextPath}/css" file="logmanager.css"/>"/>
    <link rel="stylesheet" type="text/css" href="<g:createLinkTo dir="${pluginContextPath}/css" file="logmanagermain.css"/>"/>
    <link rel="stylesheet" type="text/css" href="<g:createLinkTo dir="${pluginContextPath}/js/yui/treeview/assets/skins/sam" file="treeview.css"/>"/>
    <script type="text/javascript" src="<g:createLinkTo dir="${pluginContextPath}/js/yui/utilities" file="utilities.js"/>"></script>
    <script type="text/javascript" src="<g:createLinkTo dir="${pluginContextPath}/js/yui/treeview" file="treeview-min.js"/>"></script>
    <script type="text/javascript" src="<g:createLinkTo dir="${pluginContextPath}/js/logmanager" file="utils.js"/>"></script>
    <script type="text/javascript" src="<g:createLinkTo dir="${pluginContextPath}/js/logmanager" file="requester.js"/>"></script>
    <script type="text/javascript" src="<g:createLinkTo dir="${pluginContextPath}/js/logmanager" file="LogManager.js"/>"></script>
    <script type="text/javascript" src="<g:createLinkTo dir="${pluginContextPath}/js/logmanager" file="LogFileTree.js"/>"></script>
    <script type="text/javascript" src="<g:createLinkTo dir="${pluginContextPath}/js/logmanager" file="ErrorDialog.js"/>"></script>
    <script type="text/javascript">
        var STATUS_ERROR = 1;
        var STATUS_NORMAL = 0;
        var PAUSED = 0;
        var RESUMED = 1;
        var logFileRequesters = {};
        var currentlyViewingFile = null;
        function resize() {
            for (var i in logFileRequesters)
            {
                logFileRequesters[i].resize();
            }
        }
        function showLogRequestError(event, args)
        {
            var logFileName = args[0];
            var response = args[1].responseXML;
            var errors = response.getElementsByTagName("Error")
            var message = errors[0].getAttribute("message");
            message += "<a href='#' onclick='refreshFileTree();errorDialog.hide();destroyLogRequester(\""+logFileName+"\")'>Refresh Log Files</a>"
            showLogErrorDialog(event, logFileName, message);
        }
        function showLogError(event, args)
        {
            var logFileName = args[0];
            var message = args[1];
            message += "<a href='#' onclick='viewFile(\""+logFileName+"\");errorDialog.hide();'>Reload</a>"
            showLogErrorDialog(event, logFileName, message);
        }

        function showLogErrorDialog(event, logFileName, message)
        {
            errorDialog.setMessage(message)
            setStatus(STATUS_ERROR);
            document.getElementById("pauseMenuItem").style.display = "none";
            document.getElementById("resumeMenuItem").style.display = "none";
        }
        function setStatus(status)
        {
            var blueImg = document.getElementById("blueStatus")
            var redImg = document.getElementById("redStatus")
            if(status == STATUS_NORMAL)
            {
                errorDialog.hide();
                YAHOO.util.Dom.setStyle(blueImg, "display", "");
                YAHOO.util.Dom.setStyle(redImg, "display", "none");
            }
            else if(status == STATUS_ERROR)
            {
                YAHOO.util.Dom.setStyle(blueImg, "display", "none");
                YAHOO.util.Dom.setStyle(redImg, "display", "");
            }
            else
            {
                YAHOO.util.Dom.setStyle(blueImg, "display", "none");
                YAHOO.util.Dom.setStyle(redImg, "display", "none"); 
            }
        }
        function fileTreeError(err)
        {

        }

        function destroyLogRequester(fileName)
        {
            var logRequester = logFileRequesters[fileName];
            if(logRequester)
            {
                logRequester.destroy();
                logFileRequesters[fileName] = null;
                document.getElementById("clearMenuItem").style.display = "none";
                document.getElementById("pauseMenuItem").style.display = "none";
                document.getElementById("resumeMenuItem").style.display = "none";
                setStatus(-1);
            }
        }
        function clearLog()
        {
            var logRequester = getCurrentRequester();
            if(logRequester)
            {
                logRequester.clear();
            }
        }

        function setPauseButtonStates(state)
        {
            switch(state){
                case PAUSED:
                document.getElementById("pauseMenuItem").style.display = "none";
                document.getElementById("resumeMenuItem").style.display = "";
                break;
                case RESUMED:
                document.getElementById("pauseMenuItem").style.display = "";
                document.getElementById("resumeMenuItem").style.display = "none";
            }
        }
        function pauseLog()
        {
            var logRequester = getCurrentRequester();
            if(logRequester)
            {
                logRequester.pause();
                setPauseButtonStates(PAUSED);
            }
        }
        function resumeLog()
        {
            var logRequester = getCurrentRequester();
            if(logRequester)
            {
                logRequester.resume();
                setPauseButtonStates(RESUMED);
            }
        }
        function getCurrentRequester()
        {
            return logFileRequesters[currentlyViewingFile]
        }
        function viewFile(fileName)
        {
            var logRequester = logFileRequesters[fileName];
            if (logRequester == null)
            {
                logRequester = new LogManager(document.getElementById("viewArea"), {url:"${createLink(controller:"viewLog", action:"getLog")}", logFile:fileName});
                logRequester.events.logError.subscribe(showLogError)
                logRequester.events.requestFailed.subscribe(showLogRequestError)
                logFileRequesters[fileName] = logRequester;
            }
            var currentLogRequester = logFileRequesters[currentlyViewingFile]
            if (currentLogRequester != null)
            {
                currentLogRequester.hide();
            }
            logRequester.show();
            document.getElementById("clearMenuItem").style.display = "";
            setPauseButtonStates(logRequester.processState == logRequester.PAUSED?PAUSED:RESUMED);
            currentlyViewingFile = fileName;
            logRequester.resize();
            setStatus(STATUS_NORMAL)
        }
        function refreshFileTree()
        {
            fileTree.refresh();
        }
        function showHideError()
        {
            errorDialog.changeVisibility();
        }
        function initialize()
        {
            var errorStatusImg = document.getElementById("redStatus");
            errorStatusImg.onclick = showHideError;
            window.errorDialog = new ErrorDialog(document.body, errorStatusImg);
            window.fileTree = new LogFileTree(document.getElementById("fileTree"), {url:"${createLink(controller:"viewLog", action:"listFiles")}"});
            fileTree.events.requestFailed.subscribe(fileTreeError)
            fileTree.events.nodeClicked.subscribe(function(event, data) {
                viewFile(data[0].path);
            });
            fileTree.requestLogFiles();
            window.onresize = resize;
            resize();
        }
    </script>
</head>
<body onload="initialize()" style="overflow:hidden" class="yui-skin-sam logManagerMain">
<table width="100%" height="100%" cellpadding="0" cellspacing="0">
    <tr>
        <td width="0%" style="padding:1px;margin:0px;">
            <div id="treeOperations" class="nav">
                <span class="menuButton"><a class="refresh" href="#" onclick="refreshFileTree()">Refresh</a></span>
            </div>
            <div id="fileTree" style="width:200px;height:100%;"></div></td>
        <td width="100%" style="padding:1px;margin:0px;">

            <table cellpadding="0" cellspacing="0" style="border-width:0px;" width="100%" height="100%">
                <tr height="0%">
                    <td class="" width="100%" style="padding:1px;margin:0px;">
                        <div id="viewOperations" class="nav">
                            <span id="clearMenuItem" class="menuButton"  style="display:none"><a class="clear" href="#" onclick="clearLog()">Clear</a></span>
                            <span id="pauseMenuItem" class="menuButton"  style="display:none"><a class="pause" href="#"  onclick="pauseLog()">Pause</a></span>
                            <span id="resumeMenuItem" class="menuButton"  style="display:none"><a class="resume" href="#" onclick="resumeLog()">Resume</a></span>
                        </div>
                    </td>
                    <td class="" width="0%" style="padding:1px;margin:0px;">
                        <img id="redStatus" src="../images/red_anime.gif" alt="" style="display:none;cursor:pointer;"/>
                        <img id="blueStatus" src="../images/blue_anime.gif" alt="" style="display:none;"/>
                    </td>
                </tr>
                <tr height="100%">
                    <td colspan="2" style="padding:0px;margin:0px;">
                        <div id="viewArea" style="width:100%;height:100%;"></div>
                    </td>
                </tr>
            </table>
        </td>
    </tr>
</table>
</body>
</html>