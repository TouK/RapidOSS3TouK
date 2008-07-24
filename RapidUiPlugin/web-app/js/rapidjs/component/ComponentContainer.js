YAHOO.namespace('rapidjs', 'rapidjs.component');
YAHOO.rapidjs.component.ComponentContainer = function(container, config){
	this.id = config.id;
	this.container = container;
	this.config = config;
	this.url = config.url;
	this.title = config.title;
    this.toolbar = null;
    this.events = {
		'contextmenuclicked': new YAHOO.util.CustomEvent('contextmenuclicked'),
		'loadstatechanged' :new YAHOO.util.CustomEvent('loadstatechanged'),
	    'erroroccurred' : new YAHOO.util.CustomEvent('erroroccurred')
	};
	YAHOO.rapidjs.Components[this.id] = this;
};

YAHOO.rapidjs.component.ComponentContainer.prototype = 
{

	
	clearData: function(){
	}, 

	handleVisible : function(){
	}, 
	
	handleUnvisible: function(){
	},

    addToolbarButton: function(buttonConfig){
         if(this.toolbar){
             return this.toolbar.addButton(buttonConfig)
         }
    }

};



