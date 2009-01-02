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
YAHOO.rapidjs.component.FlexPieChart = function(container, config) {
    YAHOO.rapidjs.component.FlexPieChart.superclass.constructor.call(this, container, config);
    this.swfURL = null;
    YAHOO.ext.util.Config.apply(this, config);
    this.gradients = {};
    var events = {
        'itemClicked': new YAHOO.util.CustomEvent('itemClicked')
    };
    YAHOO.ext.util.Config.apply(this.events, events);
    this.header = YAHOO.ext.DomHelper.append(this.container, {tag:'div'});
    this.toolbar = new YAHOO.rapidjs.component.tool.ButtonToolBar(this.header, {title:this.title});
    this.toolbar.addTool(new YAHOO.rapidjs.component.tool.LoadingTool(document.body, this));
    this.toolbar.addTool(new YAHOO.rapidjs.component.tool.SettingsTool(document.body, this));
    this.toolbar.addTool(new YAHOO.rapidjs.component.tool.ErrorTool(document.body, this));
    this.body = YAHOO.ext.DomHelper.append(this.container, {tag:'div'}, true);
    this.renderTask = new YAHOO.ext.util.DelayedTask(this.render, this);
    this.initializeTask = new YAHOO.ext.util.DelayedTask(this._handleSuccess, this);
    var oSelf = this;
    YAHOO.util.Event.onDOMReady(function() {
        oSelf.renderTask.delay(500);
    });
}
YAHOO.lang.extend(YAHOO.rapidjs.component.FlexPieChart, YAHOO.rapidjs.component.PollingComponentContainer, {
    render: function() {
        var requiredMajorVersion = 9;
        var requiredMinorVersion = 0;
        var requiredRevision = 0;

        var hasProductInstall = YAHOO.rapidjs.FlashUtils.DetectFlashVer(6, 0, 65);
        var hasRequestedVersion = YAHOO.rapidjs.FlashUtils.DetectFlashVer(requiredMajorVersion, requiredMinorVersion, requiredRevision);
        if (!hasProductInstall || !hasRequestedVersion)
        {
            this.body.dom.innerHTML = "This application requires Flash player version " + requiredMajorVersion + ". Click <a href='http://www.adobe.com/shockwave/download/download.cgi?P1_Prod_Version=ShockwaveFlash'>here</a> to download";
        }
        else
        {
            var sb = new Array();
            sb[sb.length] = "<object id='flexApp_" + this.id + "' classid='clsid:D27CDB6E-AE6D-11cf-96B8-444553540000' codebase='http://download.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=8,5,0,0' height='100%' width='100%'>";
            sb[sb.length] = "<param name='flashvars' value='bridgeName=" + this.id + "'/>";
            sb[sb.length] = "<param name='wmode' value='transparent'/>";
            sb[sb.length] = "<param name='src' value='" + this.swfURL + "'/>";
            sb[sb.length] = "<embed name='flexApp' wmode='transparent' pluginspage='http://www.macromedia.com/go/getflashplayer' src='" + this.swfURL + "' height='100%' width='100%' flashvars='bridgeName=" + this.id + "'/>";
            sb[sb.length] = "</object>";

            var chartHtml = sb.join('');
            this.body.dom.innerHTML = chartHtml;
        }
    },

    handleSuccess: function (response)
    {
        this.lastResponse = response;
        this._handleSuccess();
    },
    _handleSuccess: function() {
        if (FABridge[this.id]) {
            if (this.eventListenerAdded == false)
            {
                try
                {
                    var scope = this;
                    if (this.handlerFunc)
                        FABridge[this.id].root().getMyChart().removeEventListener("itemClick", this.handlerFunc);
                    this.handlerFunc = function(e) {
                        scope.handleItemClick(e);
                    };
                    FABridge[this.id].root().getMyChart().addEventListener("itemClick", this.handlerFunc);
                    this.eventListenerAdded = true;
                }
                catch(e)
                {
                    this.initializeTask.delay(10);
                }
            }
            try
            {
                this.processData(this.lastResponse);
            }
            catch(e)
            {
            }
        }
        else {
            this.initializeTask.delay(50);
        }
    },
    handleItemClick: function(e)
    {
        var index = e.getHitData().getChartItem().getIndex();
        if (index < 0)
        {
            return;
        }
        var mySeries = e.bridge.root().getMySeries();
        var oldRadiuses = mySeries.getPerWedgeExplodeRadius();
        var newRadiuses = new Array();
        for (var i = 0; i < oldRadiuses.length; i++)
        {
            newRadiuses[i] = oldRadiuses[i];
        }
        var value = oldRadiuses[index];
        if (!value || value == 0)
        {
            value = 0.1;
        }
        else
        {
            value = 0.0;
        }
        newRadiuses[index] = value;
        mySeries.setPerWedgeExplodeRadius(newRadiuses);

        var dataProvider = e.bridge.root().getMyChart().getDataProvider();
        var selectedData = dataProvider.getItemAt(index);
        var dataToSend = new YAHOO.rapidjs.data.RapidXmlNode(null, null, 1, null);
        dataToSend.attributes["chartId"] = this.id;
        var tmpColData = this.columnData[selectedData["Label"]];
        for (var propName in tmpColData)
        {
            var propValue = tmpColData[propName];
            dataToSend.attributes[propName] = propValue;
        }
        this.events["itemClicked"].fireDirect(dataToSend);
    },
    processData: function(response) {
        var dataString = response.responseText;
        var dataTag = response.responseXML.getElementsByTagName(this.rootTag)[0];
        var slices = dataTag.getElementsByTagName("set");
        var arrayOfSlices = new Array();
        var arrayOfGradients = new Array();
        var bridgeObj = FABridge[this.id];
        this.columnData = {};
        for (var i = 0; i < slices.length; i++)
        {
            var allAttrs = slices[i].attributes;
            var colLabel = slices[i].getAttribute("label");
            var closureForSlice = {Label:colLabel, Data:slices[i].getAttribute("value")};
            this.columnData[colLabel] = {};
            for (var index = 0; index < allAttrs.length; index++)
            {
                var attNode = allAttrs.item(index);
                this.columnData[colLabel][attNode.nodeName] = attNode.nodeValue;
            }
            arrayOfSlices[i] = closureForSlice;
            arrayOfGradients[i] = this.getGradient(bridgeObj, slices[i].getAttribute('color'));
            if (arrayOfGradients[i] == null)
            {
                return;
            }
        }
        bridgeObj.root().getMySeries().setStyle("fills", arrayOfGradients);
        bridgeObj.root().getMyChart().setDataProvider(arrayOfSlices);
        var title = dataTag.getAttribute("Title");
        if (title)
        {
            bridgeObj.root().getMyPanel().setTitle(title);
        }
    },

    getGradient: function(bridgeObj, colorStr)
    {
        if (this.gradients[colorStr]) {
            return this.gradients[colorStr];
        }
        else {
            try
            {
                var radialGradient = bridgeObj.create("mx.graphics.RadialGradient");
                var ge1 = bridgeObj.create("mx.graphics.GradientEntry");
                ge1.setColor("0xFFFFFF");
                ge1.setRatio(0.0);
                ge1.setAlpha(1.0);
                var ge2 = bridgeObj.create("mx.graphics.GradientEntry");
                ge2.setColor(colorStr);
                ge2.setRatio(1.0);
                ge2.setAlpha(0.5);
                var entries = new Array();
                entries[0] = ge1;
                entries[1] = ge2;
                radialGradient.setEntries(entries);
                this.gradients[colorStr] = radialGradient;
                return radialGradient;
            }
            catch(e)
            {
                this.eventListenerAdded = false;
                this.initializeTask.delay(10);
                return null;
            }
        }
    },
    resize : function(width, height) {
        this.body.setHeight(height - this.header.offsetHeight);
//        if( !this.isRendered )
//        	this.render();
//        this.poll();
    }
})