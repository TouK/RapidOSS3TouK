YAHOO.namespace('rapidjs', 'rapidjs.component');
YAHOO.rapidjs.component.Autocomplete = function(container, config) {
    YAHOO.rapidjs.component.search.Autocomplete.superclass.constructor.call(this, container, config);
    this.rootTag = null;
    this.contentPath = null;
    this.fields = null;
    YAHOO.ext.util.Config.apply(this, config);
    this.render();

}
YAHOO.lang.extend(YAHOO.rapidjs.component.Autocomplete, YAHOO.rapidjs.component.ComponentContainer, {
    render:function() {
        var dh = YAHOO.ext.DomHelper;
        this.wrapper = dh.append(this.container, {tag: 'div', cls:'r-autocomplete'});
        this.header = dh.append(this.wrapper, {tag:'div'}, true);
        this.toolbar = new YAHOO.rapidjs.component.tool.ButtonToolBar(this.header.dom, {title:this.title});
        this.toolbar.addTool(new YAHOO.rapidjs.component.tool.ErrorTool(document.body, this));
        this.body = dh.append(this.wrapper, {tag: 'div', cls:'r-autocomplete-body',
            html:'<form action="javascript:void(0)"><div>' +
                 '<input type="text"><input type="submit" value="Search">' +
                 '<div></div>' +
                 '</div></form>'});
        this.searchInput = this.body.getElementsByTagName('input')[0];
        this.suggesstion = this.body.getElementsByTagName('div')[0];

        this.datasource = new YAHOO.util.XHRDataSource(this.url);
        this.datasource.responseType = YAHOO.util.XHRDataSource.TYPE_XML;

        this.datasource.responseSchema = {
            metaNode: this.rootTag,
            resultNode: this.contentPath,
            fields: this.fields
        };
        this.datasource.maxCacheEntries = 0;
        this.autoComp = new YAHOO.widget.AutoComplete(this.searchInput, this.suggesstion, this.datasource);
        this.autoComp.useIFrame = true;
        this.autoComp.allowBrowserAutocomplete = false;
        this.autoComp.queryMatchCase = true;
        this.autoComp.useShadow = true;
        this.autoComp.forceSelection = true;
        this.autoComp.dataErrorEvent.subscribe(this.dataError, this, true);
        this.autoComp.itemSelectEvent.subscribe(this.itemSelected, this, true);
        this.autoComp.unmatchedItemSelectEvent.subscribe(this.unmatchedItemSelected, this, true);
        this.autoComp.doBeforeExpandContainer = function(oTextbox, oContainer, sQuery, aResults) {
	        var pos = YAHOO.util.Dom.getXY(oTextbox);
	        pos[1] += YAHOO.util.Dom.get(oTextbox).offsetHeight + 2;
	        YAHOO.util.Dom.setXY(oContainer,pos);
	        return true;
	    }; 
    },
    dataError: function(autoComp, query){
       alert("data error: " + query);
    },
    itemSelected: function(autoComp , elItem , oData){
         alert("item seleceted : " + oData)
    },
    unmatchedItemSelected: function(autoComp , sSelection ){
       alert("unmatched: " +sSelection)
    }
})