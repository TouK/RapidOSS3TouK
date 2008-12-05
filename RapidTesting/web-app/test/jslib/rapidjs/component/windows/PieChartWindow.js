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
YAHOO.rapidjs.component.windows.PieChartWindow = function(container, config){
	YAHOO.rapidjs.component.windows.PieChartWindow.superclass.constructor.call(this,container, config);
	YAHOO.ext.util.Config.apply(this, config);
	this.chart = new FusionCharts(this.chartSWF, this.chartId, this.width, this.height, "0", "1"); 
	this.panel = new YAHOO.rapidjs.component.layout.RapidPanel(this.container, {title:this.title, fitToFrame:true});
	this.subscribeToPanel();
	this.render();
};

YAHOO.extendX(YAHOO.rapidjs.component.windows.PieChartWindow, YAHOO.rapidjs.component.PollingComponentContainer, {
	
	processData: function(response){
		var dataString = response.responseText;
		var startIndex = dataString.indexOf("<" + this.rootTag);
		var endIndex = dataString.indexOf('</' + this.rootTag + '>') + this.rootTag.length + 3;
		if(startIndex > -1 && endIndex > this.rootTag.length + 1){
			dataString = dataString.substring(startIndex, endIndex);
			this.events["erroroccurred"].fireDirect(this, false);
			
		}
		else{
			this.events["erroroccurred"].fireDirect(this, true, response.responseXML);
			dataString = '<' + this.rootTag + '/>';
		}
		this.chart.setDataXML(dataString);
		this.events["loadstatechanged"].fireDirect(this, false);
	}, 
	
	render : function(){
		this.chart.setDataXML('<' + this.rootTag + '/>');
		this.chart.render(this.container);
	}, 
	
	clearData:function(){
//		this.chart.setDataXML('<' + this.rootTag + '/>');
	}
	
});