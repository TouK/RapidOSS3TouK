YAHOO.rapidjs.component.windows.ConsoleWindow = function(container, config){
	YAHOO.rapidjs.component.windows.ConsoleWindow.superclass.constructor.call(this, container, config);
	this.textArea = YAHOO.ext.DomHelper.append(this.container, {tag:'textarea', cls:'rapid-textarea'});
	this.textArea.readOnly = true;
	this.textArea.rows = 10;
	this.panel = new YAHOO.rapidjs.component.layout.RapidPanel(this.container, {title:this.title});
};

YAHOO.extendX(YAHOO.rapidjs.component.windows.ConsoleWindow, YAHOO.rapidjs.component.ComponentContainer, {
	appendText : function(text){
		var value = this.textArea.value;
		if(value != ''){
			this.textArea.value = this.textArea.value + '\n' + text;
		}
		else{
			this.textArea.value = text;
		}
		this.textArea.scrollTop = this.textArea.scrollHeight - this.textArea.clientHeight;
	}
});