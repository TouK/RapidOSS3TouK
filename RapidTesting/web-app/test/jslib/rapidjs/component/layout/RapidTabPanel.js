YAHOO.rapidjs.component.layout.RapidTabPanel = function(container, config)
{
	YAHOO.rapidjs.component.layout.RapidTabPanel.superclass.constructor.call(this, container, config);
};

YAHOO.extendX(YAHOO.rapidjs.component.layout.RapidTabPanel, YAHOO.ext.TabPanel, {
	createStripList: function(strip){
	    // div wrapper for retard IE
	    strip.innerHTML = '<div class="ytab-strip-wrap"><table class="ytab-strip" cellspacing="0" cellpadding="0" border="0"><tbody><tr><td width="0%"></td><td width="100%"><table class="ytab-strip" cellspacing="0" cellpadding="0" border="0"><tbody><tr></tr></tbody></table></td><td width="0%"><table cellspacing="5" cellpadding="0" border="0"><tbody><tr></tr></tbody></table></td></tr></tbody></table></div>';
	    this.titleArea = strip.firstChild.firstChild.firstChild.firstChild.firstChild;
	    this.toolsArea = this.titleArea.nextSibling.nextSibling.firstChild.firstChild.firstChild;
	    return this.titleArea.nextSibling.firstChild.firstChild.firstChild;
	}
});