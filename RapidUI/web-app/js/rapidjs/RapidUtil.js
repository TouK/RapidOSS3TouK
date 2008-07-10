
YAHOO.rapidjs.Links = [];
YAHOO.rapidjs.Actions = {};
YAHOO.rapidjs.Components = {};


String.prototype.trim = function() {
	a = this.replace(/^\s+/, '');
	return a.replace(/\s+$/, '');
};
YAHOO.rapidjs.ArrayUtils = new function()
{
	this.remove = function(arrayP, index)
	{
		if(index >= arrayP.length || index < 0) return;
		var lastIndex = arrayP.length - 1;
		arrayP[index] = arrayP[lastIndex];
		arrayP.splice(lastIndex, 1);
	};
}();

YAHOO.rapidjs.DomUtils = new function()
{
	this.findParent = function(element, className){
		element = element.parentNode;
		while(element)
		{
			if(YAHOO.util.Dom.hasClass(element, className))
			{
				return element;
			}
			element = element.parentNode;
		}
		return null;
	};
	this.findChild = function(element, className){
		var childNodes = element.childNodes;
		for(var index=0; index<childNodes.length; index++) {
			if(YAHOO.util.Dom.hasClass(childNodes[index], className))
			{
				return childNodes[index];
			}
		}
		return null;
	};
	this.getElementFromChild = function(childEl, parentClass){
        if(!childEl || (YAHOO.util.Dom.hasClass(childEl, parentClass))){
		    return childEl;
	    }
	    var p = childEl.parentNode;
	    var b = document.body;
	    while(p && p != b){
            if(YAHOO.util.Dom.hasClass(p, parentClass)){
            	return p;
            }
            p = p.parentNode;
        }
	    return null;
    };
}();

YAHOO.rapidjs.CursorManager = new function(){
	this.processes = new Array();
	this.idle = function(){
		this.processes.pop();
		if(this.processes.length == 0){
			document.body.style.cursor = '';
		}
	};
	this.busy = function(){
		this.processes.push('');
		document.body.style.cursor = 'wait';
	};
}();

