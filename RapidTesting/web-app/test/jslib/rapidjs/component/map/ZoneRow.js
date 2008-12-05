/* 
* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be
* noted in a separate copyright notice. All rights reserved.
* This file is part of RapidCMDB.
*
* RapidCMDB is free software; you can redistribute it and/or modify
* it under the terms version 2 of the GNU General Public License as
* published by the Free Software Foundation. This program is distributed
* in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
* even the implied warranty of MERCHANTABILITY or FITNESS FOR A
* PARTICULAR PURPOSE. See the GNU General Public License for more
* details.
*
* You should have received a copy of the GNU General Public License
* along with this program; if not, write to the Free Software
* Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
* USA.
*/
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