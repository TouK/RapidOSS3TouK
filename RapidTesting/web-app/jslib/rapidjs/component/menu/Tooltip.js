YAHOO.rapidjs.component.Tooltip = new function()
{
	this.init = function(){
		var dh = YAHOO.ext.DomHelper;
		this.tooltip = dh.append(document.body, {tag:'div', cls:'r-tooltip'}, true);
		this.tipBody = dh.append(this.tooltip.dom, {tag:'div', cls:'r-tooltip-body'});
		this.hide();
		this.showTask = new YAHOO.ext.util.DelayedTask(this.show, this);
		this.hideTask = new YAHOO.ext.util.DelayedTask(this.hide, this);
	};
	
	this.add = function(element, text)
	{
		YAHOO.util.Event.addListener(element, 'mouseover', function(event){
			if(element.tooltipText || element.tooltipText != ''){
				this.tipBody.innerHTML = element.tooltipText;
				var pageX = YAHOO.util.Event.getPageX(event);
				var pageY = YAHOO.util.Event.getPageY(event);
				this.showTask.delay(150, this.show, this, [pageX,pageY]);
				this.hideTask.delay(2000);
			}
		}, this, true);
		YAHOO.util.Event.addListener(element, 'mouseout', this.handleMouseOut, this, true);
		element.tooltipText = text;
	};
	this.remove = function(element)
	{
		YAHOO.util.Event.removeListener(element, 'mouseover');
		YAHOO.util.Event.removeListener(element, 'mouseout', this.handleMouseOut);
		element.tooltipText = null;
	};
	this.update = function(element, text)
	{
		element.tooltipText = text;
	};
	this.handleMouseOut = function(event)
	{
		this.showTask.cancel();
		this.hide();
	};
	
	this.show = function(pageX,pageY){
		
		var offsetWidth = this.tooltip.dom.offsetWidth;
		var offsetHeight = this.tooltip.dom.offsetHeight;
		var viewPortWidth = YAHOO.util.Dom.getViewportWidth();
		var viewPortHeight = YAHOO.util.Dom.getViewportHeight();
		var scrollX = document.documentElement.scrollLeft || document.body.scrollLeft;
		var scrollY = document.documentElement.scrollTop || document.body.scrollTop;
		var leftConstraint = scrollX + 10;
		var rightConstraint = scrollX + viewPortWidth - offsetWidth - 10;
		if (pageX < leftConstraint) {
			pageX = leftConstraint;
		} else if (pageX > rightConstraint) {
			pageX= rightConstraint;
		}
		var topConstraint = scrollY + 10;
		var bottomConstraint = scrollY + viewPortHeight - offsetHeight - 10;
		if (pageY < topConstraint) {
			pageY = topConstraint;
		} else if (pageY > bottomConstraint) {
			pageY = bottomConstraint;
		}
		this.tooltip.setX(pageX);
		this.tooltip.setY(pageY);
		YAHOO.util.Dom.setStyle(this.tooltip.dom, 'visibility', 'visible');
	};
	
	this.hide = function(){
		YAHOO.util.Dom.setStyle(this.tooltip.dom, 'visibility', 'hidden');
	};
	
}();

var Tooltip = YAHOO.rapidjs.component.Tooltip;
YAHOO.ext.EventManager.onDocumentReady(Tooltip.init, Tooltip, true);