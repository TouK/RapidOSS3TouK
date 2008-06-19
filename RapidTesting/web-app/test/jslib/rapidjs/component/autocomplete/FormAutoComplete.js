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