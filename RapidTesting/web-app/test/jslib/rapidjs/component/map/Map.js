YAHOO.rapidjs.component.map.Map = function(container, config){
	this.container = container;
	this.selectedEl = null;
	this.events = {
		'selectionchanged' : new YAHOO.util.CustomEvent('selectionchanged'), 
		'contextmenuclicked' : new YAHOO.util.CustomEvent('contextmenuclicked')
	};
	this.config = config;
};

YAHOO.rapidjs.component.map.Map.prototype = {
	render : function(){
		var dh = YAHOO.ext.DomHelper;
		this.wrapper = dh.append(this.container, {tag: 'div', cls:'r-map'});
		YAHOO.util.Event.addListener(this.wrapper, 'contextmenu', this.stopContextMenu, this, true);
		this.imageWrpEl = dh.append(this.wrapper, {tag:'div', cls:'r-map-imgwrp'}, true);
		this.imageEl = dh.append(this.imageWrpEl.dom, {tag:'img', cls:'r-map-image', src:this.config.backgroundImage}, true);
		
	}, 
	
	resize: function(width, height)
	{
		var vH = this.config.imageHeight;
		var vW = this.config.imageWidth;
		var heightRatio = vH/height;
		var widthRatio = vW/width;
		if(heightRatio > widthRatio){
			this.imageWrpEl.setHeight(height);
			var imageWidth = parseInt(height*vW/vH);
			this.imageWrpEl.setWidth(imageWidth);
		}
		else if(heightRatio < widthRatio){
			this.imageWrpEl.setWidth(width);
			var imageHeight = parseInt(width*vH/vW);
			this.imageWrpEl.setHeight(imageHeight);
		}
		else{
			this.imageWrpEl.setHeight(height);
			this.imageWrpEl.setWidth(width);
		}
	}, 
	stopContextMenu: function(event){
		var target = YAHOO.util.Event.getTarget(event);
		var dh = YAHOO.util.Dom;
		if(dh.hasClass(target,'r-map') == true || dh.hasClass(target,'r-map-imgwrp') == true || dh.hasClass(target,'r-map-image') == true){
			YAHOO.util.Event.stopEvent(event);
		}
	},
	
	handleData : function(data){
		this.rootZone = new YAHOO.rapidjs.component.map.RootZone(data, this.imageWrpEl.dom, this.config);
		this.rootZone.events['zoneclicked'].subscribe(this.zoneClicked, this, true);
		this.rootZone.events['contextmenu'].subscribe(this.fireContextMenu, this, true);
	}, 
	fireContextMenu: function(event, zone){
		this.zoneClicked(event, zone);
		var target = YAHOO.util.Event.getTarget(event);
		var row = YAHOO.rapidjs.DomUtils.getElementFromChild(target, 'r-map-zone-row');
		var xmlData = null;
		if(row){
			xmlData = zone.rows[row.index].xmlData;
		}
		else{
			xmlData = zone.xmlData;
		}
		this.events['contextmenuclicked'].fireDirect(event, xmlData);
	}, 
	
	zoneClicked : function(event, zone){
		var target = YAHOO.util.Event.getTarget(event);
		var row = YAHOO.rapidjs.DomUtils.getElementFromChild(target, 'r-map-zone-row');
		if(this.selectedEl){
			if(row){
				if(this.selectedEl != row){
					if(YAHOO.util.Dom.hasClass(this.selectedEl, 'r-map-zone-body') == true){
						YAHOO.util.Dom.replaceClass(this.selectedEl, 'r-map-zone-body-selected', 'r-map-zone-body-unselected');
					}
					else{
						YAHOO.util.Dom.replaceClass(this.selectedEl, 'r-map-zone-row-selected', 'r-map-zone-row-unselected');
					}
					this.selectedEl = row;
					YAHOO.util.Dom.replaceClass(this.selectedEl, 'r-map-zone-row-unselected', 'r-map-zone-row-selected');
					this.events['selectionchanged'].fireDirect(zone.rows[row.index].xmlData);
				}
			}
			else{
				if(this.selectedEl != zone.body.dom){
					if(YAHOO.util.Dom.hasClass(this.selectedEl, 'r-map-zone-body') == true){
						YAHOO.util.Dom.replaceClass(this.selectedEl, 'r-map-zone-body-selected', 'r-map-zone-body-unselected');
					}
					else{
						YAHOO.util.Dom.replaceClass(this.selectedEl, 'r-map-zone-row-selected', 'r-map-zone-row-unselected');
					}
					this.selectedEl = zone.body.dom;
					YAHOO.util.Dom.replaceClass(this.selectedEl, 'r-map-zone-body-unselected', 'r-map-zone-body-selected');
					this.events['selectionchanged'].fireDirect(zone.xmlData);
				}
			}
		}
		else{
			if(row){
				this.selectedEl = row;
				YAHOO.util.Dom.replaceClass(this.selectedEl, 'r-map-zone-row-unselected', 'r-map-zone-row-selected');
				this.events['selectionchanged'].fireDirect(zone.rows[row.index].xmlData);
			}
			else{
				this.selectedEl = zone.body.dom;
				YAHOO.util.Dom.replaceClass(this.selectedEl, 'r-map-zone-body-unselected', 'r-map-zone-body-selected');
				this.events['selectionchanged'].fireDirect(zone.xmlData);
			}
			
				
		}
	}, 
	
	refreshZoneSizes: function(){
		if(this.rootZone){
			this.rootZone.refreshZoneSizes();
		}
	}
};