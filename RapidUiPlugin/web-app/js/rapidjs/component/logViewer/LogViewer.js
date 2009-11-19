/*
* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be
* noted in a separate copyright notice. All rights reserved.
* This file is part of RapidCMDB.
*
* RapidCMDB is free software; you can redistribute it and/or modify
* it under the terms version 2 of the GNU General Public License as
* published by the Free Software Foundation. This program is distributed
* in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
* even the implied warranty of MERCHANTABILITY or FITNESS FOR A
* PARTICULAR PURPOSE. See the GNU General Public License for more
* details.
*
* You should have received a copy of the GNU General Public License
* along with this program; if not, write to the Free Software
* Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
* USA.
*/
YAHOO.namespace('rapidjs', 'rapidjs.component');
YAHOO.rapidjs.component.LogViewer = function(container, config)
{
    YAHOO.rapidjs.component.LogViewer.superclass.constructor.call(this, container, config);
    this.render();
    this.logManagers = {};
    this.width = 0;
    this.height = 0;
    this.activeLogManager = null;
};

YAHOO.lang.extend(YAHOO.rapidjs.component.LogViewer, YAHOO.rapidjs.component.PollingComponentContainer, {
    render: function()
    {
        this.header = YAHOO.ext.DomHelper.append(this.container, {tag:'div'});
        this.body = YAHOO.ext.DomHelper.append(this.container, {tag:'div', cls:'logViewerBody'});
        this.toolbar = new YAHOO.rapidjs.component.tool.ButtonToolBar(this.header, {title:this.title});
        this.toolbar.addTool(new YAHOO.rapidjs.component.tool.LoadingTool(document.body, this));
        this.toolbar.addTool(new YAHOO.rapidjs.component.tool.SettingsTool(document.body, this));
        this.toolbar.addTool(new YAHOO.rapidjs.component.tool.ErrorTool(document.body, this));
    },
    viewFile: function(fileName)
    {
        var logManager = this.logManagers[fileName];
        if (logManager == null)
        {
            logManager = new YAHOO.rapidjs.component.LogManager(this.body, {url:this.url, logFile:fileName}, this);
            this.logManagers[fileName] = logManager;
        }
        if (this.activeLogManager != null)
        {
            this.activeLogManager.hide();
        }
        logManager.show();
        this.activeLogManager = logManager;
        this.resize(this.width, this.height);
    },
    poll: function() {
        if(this.activeLogManager != null)
        {
            this.activeLogManager.requestLogLines();
        }
    },

    resize: function(width, height) {
        this.width = width;
        this.height = height;
        if(this.activeLogManager != null)
        {
            this.activeLogManager.resize(width-10, height-this.header.offsetHeight);
        }
    }
})

YAHOO.rapidjs.component.LogManager = function(container, config, viewer)
{
    this.PAUSED = 0;
    this.STARTED = 1;
    this.NOT_STARTED = 2;
    this.container = container;
    this.url = config.url;
    this.offset = 0;
    this.viewer = viewer;
    this.logFile = config.logFile;
    this.pollInterval = config.pollInterval != null?config.pollInterval:1000;
    this.logFile = config.logFile;
    this.maxLineCount = config.maxLineCount == null?10:config.maxLineCount;
    this.wasLast = true;
    this.render();
    this.isDestroyed = false;
    this.processState = this.NOT_STARTED;
    this.isVisible = false;
    this.lastNLines = null;
}

YAHOO.rapidjs.component.LogManager.prototype =
{
    hide: function ()
    {
        if(this.isVisible == true){
            this.isVisible = false;
            this.pause();
            this.logsElement.style.display = "none";
        }
    },
    show: function ()
    {
        if(this.isVisible == false)
        {
            this.isVisible = true;
            this.logsElement.style.display = "";
            this.processState = this.NOT_STARTED;
            YAHOO.util.Dom.setStyle(this.spinner, "display", "");
            this.requestLogLines();
        }
    },
    render: function ()
    {
        this.logsElement = document.createElement("div");
        this.logsElement.id = this.logFile+'logs';
        this.logsElement.style.display = "none";
        YAHOO.util.Dom.addClass(this.logsElement, "logs");
        var html = '<div id="'+this.logFile+'header" class="logViewerHeader">'
            +'    <table cellpadding="0" cellspacing="0" style="border-width:0px;">'
            +'        <tbody>'
            +'            <tr>'
            +'                <td width="100%" height="0%"><div id="'+this.logFile+'filename" class="logViewerFileName"></div></td>'
            +'                <td width="0%" height="0%"><input id="'+this.logFile+'filenameLastN" class="logViewerLastNLines"></input></td>'
            +'                <td width="0%" height="0%"><button id="'+this.logFile+'filenameLastNBtn" class="button logViewerLastNLinesBtn">LastN</button></td>'
            +'            </tr>'
            +'        </tbody>'
            +'    </table>'
            +'</div>'
            +'<div class="logContainer" id="'+this.logFile+'logContainer">'
            +'    <pre id="'+this.logFile+'out"></pre>'
            +'    <div class="logViewerSpinnerWrapper"><div id="'+this.logFile+'spinner" class="logViewerSpinner"></div></div>';

        +'</div>';
        this.logsElement.innerHTML = html;
        this.container.appendChild(this.logsElement);
        this.logsElement = document.getElementById(this.logFile+'logs');
        this.header = document.getElementById(this.logFile+'header');
        this.logOutputElement = document.getElementById(this.logFile+'out');
        this.filenameElement = document.getElementById(this.logFile+'filename');
        this.infoMessageElement = document.getElementById(this.logFile+'information');
        this.logContainer = document.getElementById(this.logFile+'logContainer');
        this.spinner = document.getElementById(this.logFile+'spinner');
        this.filenameElement.innerHTML = this.logFile;
        YAHOO.util.Event.addListener(document.getElementById(this.logFile+'filenameLastNBtn'), "click", this.lastNButtonClicked, this, true)
    },
    lastNButtonClicked: function(){
        var inputElement = document.getElementById(this.logFile+'filenameLastN');
        this.lastNLines = inputElement.value;
        this.viewer.abort();
        this.offset = 0;
        this.clear();
        this.requestLogLines();
    },
    isPaused: function(){
        return this.processState == this.PAUSED;
    },
    requestLogLines: function ()
    {
        if(this.isDestroyed || this.isPaused()) return;
        this.processState = this.STARTED;
        if(this.logContainer)
        {
            this.wasLast = (this.logContainer.scrollTop+this.logContainer.offsetHeight) >= this.logContainer.scrollHeight-10;
        }
        var logFileName = this.logFile;
        var owner = this;
        var params = {logFile:this.logFile, offset:this.offset, max:this.maxLineCount}
        if(this.lastNLines)
        {
            params["lastN"] = this.lastNLines;
        }
        this.viewer.doRequest(this.url, params, function(res){owner.processLogs(res)})
    },
    processLogs: function(res)
    {
        this.lastNLines = null;
        if(this.isDestroyed) return;
        {
            var newOffset = res.responseXML.firstChild.getAttribute("offset")*1;
            var lines = res.responseXML.getElementsByTagName("Line");
            if(this.offset > newOffset)
            {
                this.events.logError.fire(this.logFile, "File content is deleted or modified. You should reload log file.");
                YAHOO.util.Dom.setStyle(this.spinner, "display", "none");
            }
            else
            {
                var str = "";
                for(var i=0; i < lines.length; i++)
                {

                    str = str+lines[i].getAttribute("content");
                }
                var tmpDiv = document.createElement("div");
                tmpDiv.innerHTML = str;
                var childNodes = tmpDiv.childNodes;
                while(childNodes.length > 0)
                {
                    this.logOutputElement.appendChild(childNodes[0])
                }
                this.offset = newOffset;
                if(this.wasLast)
                {
                    this.logContainer.scrollTop = this.logContainer.scrollHeight-this.logContainer.offsetHeight+30;
                }
            }
        }
    },
    pause: function ()
    {
        if(this.processState != this.PAUSED)
        {
            this.viewer.abort();
            this.processState = this.PAUSED;
            YAHOO.util.Dom.setStyle(this.spinner, "display", "none");
        }
    },
    resume: function ()
    {
        if(this.processState == this.PAUSED)
        {
            YAHOO.util.Dom.setStyle(this.spinner, "display", "");
            this.processState = this.NOT_STARTED;
            this.requestLogLines();
        }
    },
    clear: function ()
    {
        this.logOutputElement.innerHTML = "";
    },
    resize: function (width, height)
    {
        var headerHeight = this.header.offsetHeight;
        var logContainerHeight = height - headerHeight;
        YAHOO.util.Dom.setStyle(this.logContainer, "height", ''+logContainerHeight+"px");
        YAHOO.util.Dom.setStyle(this.logContainer, "width", ''+width+"px");
    }
}


