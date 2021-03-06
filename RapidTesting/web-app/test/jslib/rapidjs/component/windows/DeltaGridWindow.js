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
YAHOO.rapidjs.component.windows.DeltaGridWindow = function(container, config){
	YAHOO.rapidjs.component.windows.DeltaGridWindow.superclass.constructor.call(this,container, config);
	this.lastUpdate = 0;
};

YAHOO.extendX(YAHOO.rapidjs.component.windows.DeltaGridWindow, YAHOO.rapidjs.component.windows.GridWindow, {
	mergeData: function(oldData, newData, keyAttribute, keepExisting)
	{
		this._setAttributes(newData, oldData);
		var oldChildren = oldData.childNodes();
		var newChildren = newData.childNodes();
		for(var index=0; index<newChildren.length; index++) {
			var newChild = newChildren[index];
			var action = newChild.getAttribute('_Action');
			var oldChild = oldData.findChildNode(keyAttribute, newChild.getAttribute(keyAttribute), newChild.nodeName);
			if(oldChild && oldChild.length > 0)
			{
				
				if(action == 'Remove'){
					oldData.removeChild(oldChild[0]);
				}
				else{
					this._setAttributes(newChild, oldChild[0]);
				}
			}
			else
			{
				if(action != 'Remove'){
					oldData.appendChild(newChild, true);
				}
			}
		}
	}, 
	
	_setAttributes: function(from, to){
		var newAttributes = from.getAttributes();
		for(var attribute in newAttributes) 
		{
			to.setAttribute(attribute, newAttributes[attribute]);
		}
	}, 
	
	poll : function(){
		if(this.isLinked == true){
			if(this.params && this.panel.isVisible == true){
				this.params['_LastUpdate'] = this.lastUpdate;
				this.doRequest(this.url, this.params);
			}
		}
		else{
			if(this.panel.isVisible == true){
				if(!this.params){
					this.params = {};
				}
				this.params['_LastUpdate'] = this.lastUpdate;
				this.doRequest(this.url, this.params);
			}
		}
	},
	processData : function(response, keepExisting, removeAttribute){
		var data = new YAHOO.rapidjs.data.RapidXmlDocument(response,[this.keyAttribute]);
		var node = this.getRootNode(data, response.responseText);
		if(node){
			if(!this.rootNode || this.lastUpdate == 0){
				this.dm.isSortingDisabled = true;
				this.rootNode = node;
				this.dm.setRootNode(this.rootNode);
				this.dm.loadData(data);
				this.dm.isSortingDisabled = false;
			}
			else
			{
				this.dm.isSortingDisabled = true;
				this.mergeData(this.rootNode, node, this.keyAttribute, keepExisting, removeAttribute);
				this.dm.isSortingDisabled = false;
				this.dm.purgeRemovedData();
			}
			this.grid.view.updateBodyHeight();
			this.grid.view.adjustForScroll(true);
			this.dm.applySort();
		}
		
		if(this.rootNode){
			this.lastUpdate = parseInt(this.rootNode.getAttribute('_LastUpdate'),10);
		}
	}, 
	processSuccess : function(response, keepExisting){
		YAHOO.rapidjs.component.windows.DeltaGridWindow.superclass.processSuccess.call(this,response, keepExisting);
		if(this.pollInterval <= 0){
			this.lastUpdate = 0;
		}
	}, 
	clearData: function(){
		YAHOO.rapidjs.component.windows.DeltaGridWindow.superclass.clearData.call(this);
		this.lastUpdate = 0;
	}
});