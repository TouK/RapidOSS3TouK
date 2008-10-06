YAHOO.namespace('rapidjs', 'rapidjs.component');
YAHOO.rapidjs.component.Autocomplete = function(container, config) {
    YAHOO.rapidjs.component.Autocomplete.superclass.constructor.call(this, container, config);
    this.rootTag = null;
    this.contentPath = null;
    this.fields = null;
    this.cacheSize = 0;
    this.animated = false;
    YAHOO.ext.util.Config.apply(this, config);
    var events = {
        'submit': new YAHOO.util.CustomEvent('submit')
    }
    YAHOO.ext.util.Config.apply(this.events, events);
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
            html:'<div class="r-autocomplete-bwrp">' +
                 '<form action="javascript:void(0)">' +
                 '<div>' +
                 '<input class="r-autocomplete-input" type="text"></input>' +
                 '<div class="r-autocomplete-swrp"></div>' +
                 '<div class="r-autocomplete-suggestion"></div>' +
                 '</div></form></div>'});
        this.searchInput = this.body.getElementsByTagName('input')[0];
        this.suggestion = YAHOO.util.Dom.getElementsByClassName('r-autocomplete-suggestion','div',  this.body)[0]
        var buttonWrp = YAHOO.util.Dom.getElementsByClassName('r-autocomplete-swrp','div',  this.body)[0]; 
        this.submitButton = new YAHOO.widget.Button(buttonWrp,{label:'Search', type:'submit'});

        this.datasource = new YAHOO.util.XHRDataSource(this.url);
        this.datasource.responseType = YAHOO.util.XHRDataSource.TYPE_XML;

        this.datasource.responseSchema = {
            metaNode: this.rootTag,
            resultNode: this.contentPath,
            fields: this.fields
        };
        this.datasource.maxCacheEntries = this.cacheSize;
        this.autoComp = new YAHOO.widget.AutoComplete(this.searchInput, this.suggestion, this.datasource);
        this.autoComp.useIFrame = true;
        this.autoComp.allowBrowserAutocomplete = false;
        this.autoComp.queryMatchCase = true;
        this.autoComp.useShadow = true;
        this.autoComp.animVert = this.animated;
        this.autoComp.forceSelection = true;
        this.autoComp.dataErrorEvent.subscribe(this.dataError, this, true);
        this.autoComp.doBeforeExpandContainer = function(oTextbox, oContainer, sQuery, aResults) {
	        var pos = YAHOO.util.Dom.getXY(oTextbox);
	        pos[1] += YAHOO.util.Dom.get(oTextbox).offsetHeight + 2;
	        YAHOO.util.Dom.setXY(oContainer,pos);
	        return true;
	    };
        YAHOO.util.Event.addListener(this.body.getElementsByTagName('form')[0], 'submit', this.handleSubmit, this, true);
    },
    dataError: function(autoComp, query){
    },

    handleSubmit: function(e){
        YAHOO.util.Event.preventDefault(e);
        var value = this.searchInput.value;
        if(value.trim() != ""){
            this.events['submit'].fireDirect(this.searchInput.value);
        }
    }
})