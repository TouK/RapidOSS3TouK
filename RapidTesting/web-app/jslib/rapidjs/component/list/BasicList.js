YAHOO.rapidjs.component.list.BasicList = function(container, config){
	this.container = container;
	this.config = config;
	this.events = {
		'selectionchanged' : new YAHOO.util.CustomEvent('selectionchanged'), 
		'contextmenu' : new YAHOO.util.CustomEvent('contextmenu')
	};
	this.render();
};

YAHOO.rapidjs.component.list.BasicList.prototype = {
	render: function(){
		
	},
	
	loadData : function(data){
		if(!this.rootListItem){
			this.rootListItem = this.constructRootListItem(data);
			this.rootListItem.events['listitemclicked'].subscribe(this.listItemClicked, this, true);
			this.rootListItem.events['contextmenu'].subscribe(this.contextMenuClicked, this, true);	
		}
	}, 
	
	constructRootListItem : function(data){
		
	}, 
	
	listItemClicked : function(event, listItem){
		
	}, 
	
	contextMenuClicked: function(event, listItem){
		
	}
};