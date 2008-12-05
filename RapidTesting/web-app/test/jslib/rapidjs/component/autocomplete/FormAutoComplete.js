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
YAHOO.rapidjs.component.FormAutoComplete = function(inputEl,config){
	YAHOO.ext.util.Config.apply(this, config);
	this.inputEl = inputEl;
	this.render();
	this.datasource = new YAHOO.widget.DS_XHR(this.url, [this.contentPath]);
    this.datasource.responseType = YAHOO.widget.DS_XHR.TYPE_XML;
    this.datasource.scriptQueryParam = this.queryParam; 
    this.datasource.maxCacheEntries = 0;
    this.oAutoComp = new YAHOO.widget.AutoComplete(inputEl, this.suggestion.dom, this.datasource);
    this.oAutoComp.allowBrowserAutocomplete = false;
    this.oAutoComp.queryDelay = 0;	//default value is 0.2 seconds.
    if(this.delimChar){
    	this.oAutoComp.delimChar = this.delimChar; 
    } 
    this.oAutoComp.dataRequestEvent.subscribe(this.positionSuggestion, this, true);
	
};

YAHOO.rapidjs.component.FormAutoComplete.prototype = {
	render: function(){
		this.suggestion = YAHOO.ext.DomHelper.append(document.body, {tag:'div', cls:this.suggestCls}, true);
	},
	positionSuggestion: function(){
		var x = YAHOO.util.Dom.getX(this.inputEl);
		var y = YAHOO.util.Dom.getY(this.inputEl);
		this.suggestion.setX(x);
		this.suggestion.setY(y + this.inputEl.offsetHeight);
	}
};