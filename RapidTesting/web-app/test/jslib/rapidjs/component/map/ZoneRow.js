YAHOO.rapidjs.component.map.ZoneRow = function(xmlData, container, parentZone, config,index){
	YAHOO.rapidjs.component.map.ZoneRow.superclass.constructor.call(this, xmlData);
	this.container = container;
	this.config = config;
	this.parentZone = parentZone;
	this.index = index;
	this.render();
};

YAHOO.extendX(YAHOO.rapidjs.component.map.ZoneRow, YAHOO.rapidjs.component.RapidElement, {
	render: function(){
		var dh = YAHOO.ext.DomHelper;
		this.wrapper = dh.append(this.container, {tag:'div', cls:'r-map-zone-row', 
			html:'<span class="r-map-zone-rowpos"><span class="r-map-zone-rowlbl"></span></span>'});
		YAHOO.util.Dom.addClass(this.wrapper,'r-map-zone-row-unselected');
		this.wrapper.index = this.index;
		this.lblEl = this.wrapper.firstChild.firstChild;
		this.lblEl.innerHTML = this.xmlData.getAttribute(this.config.displayAttributeName);
		this.setImage();
	}, 
	dataDestroyed: function(){
		this.destroy();	
	},
	destroy : function(){
		delete this.parentZone.rows[this.index];
		this.parentZone = null;
		this.wrapper.index = null;
		this.container.removeChild(this.wrapper);
		this.container = null;
	}, 
	
	dataChanged: function(attributeName, attributeValue){
		if(attributeName == this.config.displayAttributeName){
			this.lblEl.innerHTML = attributeValue;
		}
		this.setImage();
	}, 
	
	setImage: function(){
		var data = this.xmlData.getAttributes();
		var expressionsArray = this.config.images;
		for(var i = 0 ; i < expressionsArray.length ; i++)
		{
			var currentExpressionStr = expressionsArray[i]['visible'];
			var evaluationResult = eval(currentExpressionStr);
			if(evaluationResult == true)
			{
				var imageSrc = expressionsArray[i]['src'];
				this.lblEl.style.backgroundImage = 'url("' + imageSrc + '")';
			}
		}
	}
});