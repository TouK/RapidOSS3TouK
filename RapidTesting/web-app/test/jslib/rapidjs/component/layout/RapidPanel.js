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
YAHOO.rapidjs.component.layout.RapidPanel = function(el, config, content){
	YAHOO.rapidjs.component.layout.RapidPanel.superclass.constructor.call(this, el, config, content);
	this.events['visible'] = new YAHOO.util.CustomEvent('visible');
	this.events['unvisible'] = new YAHOO.util.CustomEvent('unvisible');
	this.isVisible = false;
	this.isLayoutUpdating = false;
};

YAHOO.extendX(YAHOO.rapidjs.component.layout.RapidPanel, YAHOO.ext.ContentPanel, {
	setVisibleState: function(isVisible){
		this.isVisible = isVisible;
		if(this.isLayoutUpdating == false){
			
			if(this.isVisible == true){
				this.fireEvent('visible', this);
			}
			else{
				this.fireEvent('unvisible', this);
			}
		}

	}, 
	 setActiveState : function(active){
        this.active = active;
        if(!this.active){
            this.fireEvent('deactivate', this);
            this.setVisibleState(false);
        }else{
            this.fireEvent('activate', this);
            this.setVisibleState(true);
        }
    }, 
    
    setUpdatingState : function(isLayoutUpdating){
    	this.isLayoutUpdating = isLayoutUpdating;
    }
});

YAHOO.rapidjs.component.layout.GridPanel = function(grid, config){
    this.wrapper = YAHOO.ext.DomHelper.append(document.body, // wrapper for IE7 strict & safari scroll issue
        {tag: 'div', cls: 'ylayout-grid-wrapper ylayout-inactive-content'}, true);
    this.wrapper.dom.appendChild(grid.container.dom);
    YAHOO.rapidjs.component.layout.GridPanel.superclass.constructor.call(this, this.wrapper, config);
    if(this.toolbar){
        this.toolbar.el.insertBefore(this.wrapper.dom.firstChild);
    }
    grid.monitorWindowResize = false; // turn off autosizing
    grid.autoHeight = false;
    grid.autoWidth = false;
    this.grid = grid;
    this.grid.container.replaceClass('ylayout-inactive-content', 'ylayout-component-panel');
};

YAHOO.extendX(YAHOO.rapidjs.component.layout.GridPanel, YAHOO.rapidjs.component.layout.RapidPanel, {
    getId : function(){
        return this.grid.id;
    },

    getGrid : function(){
        return this.grid;    
    },
    
    setSize : function(width, height){
        var grid = this.grid;
        var size = this.adjustForComponents(width, height);
        grid.container.setSize(size.width, size.height);
        grid.autoSize();
    },
    
    beforeSlide : function(){
        this.grid.getView().wrapEl.clip();
    },
    
    afterSlide : function(){
        this.grid.getView().wrapEl.unclip();
    },
    
    destroy : function(){
        this.grid.getView().unplugDataModel(this.grid.getDataModel());
        this.grid.container.removeAllListeners();
        YAHOO.rapidjs.component.layout.GridPanel.superclass.destroy.call(this);
    }
});

YAHOO.rapidjs.component.layout.MultiGridPanel = function(grids, config){
	this.wrapper = YAHOO.ext.DomHelper.append(document.body, // wrapper for IE7 strict & safari scroll issue
        {tag: 'div', cls: 'ylayout-grid-wrapper ylayout-inactive-content'}, true);
    for(var gridIndex in grids) {
    	var grid = grids[gridIndex];
    	if(!this.grid){
    		this.grid = grid;
    	}
    	this.addGrid(grid);
    	this.grid.container.setStyle('display', '');
    }
    YAHOO.rapidjs.component.layout.GridPanel.superclass.constructor.call(this, this.wrapper, config);
    if(this.toolbar){
        this.toolbar.el.insertBefore(this.wrapper.dom.firstChild);
    }
};
YAHOO.extendX(YAHOO.rapidjs.component.layout.MultiGridPanel, YAHOO.rapidjs.component.layout.GridPanel,{
	addGrid : function(grid){
		this.wrapper.dom.appendChild(grid.container.dom);
    	grid.monitorWindowResize = false; // turn off autosizing
	    grid.autoHeight = false;
	    grid.autoWidth = false;
	    grid.container.replaceClass('ylayout-inactive-content', 'ylayout-component-panel');
	    grid.container.setStyle('display', 'none');
	}, 
	
	activateGrid : function(grid){
		if(grid != this.grid){
			var size = this.grid.container.getSize();
			this.grid.container.setStyle('display', 'none');
			grid.container.setStyle('display', '');
			grid.container.setSize(size.width, size.height);
	        grid.autoSize();
	        this.grid = grid;	
		}
		
	}
});


YAHOO.rapidjs.component.layout.NestedLayoutPanel = function(layout, config){
    YAHOO.rapidjs.component.layout.NestedLayoutPanel.superclass.constructor.call(this, layout.getEl(), config);
    layout.monitorWindowResize = false; // turn off autosizing
    this.layout = layout;
    this.layout.getEl().addClass('ylayout-nested-layout');
};

YAHOO.extendX(YAHOO.rapidjs.component.layout.NestedLayoutPanel, YAHOO.rapidjs.component.layout.RapidPanel, {
    setSize : function(width, height){
        var size = this.adjustForComponents(width, height);
        this.layout.getEl().setSize(size.width, size.height);
        this.layout.layout();
    },
    
    getLayout : function(){
        return this.layout;
    }, 
    
    setVisibleState: function(isVisible){
		this.isVisible = isVisible;
		this.layout.setVisibleState(isVisible);
		if(this.isLayoutUpdating == false){
			if(this.isVisible == true){
				this.fireEvent('visible', this);
			}
			else{
				this.fireEvent('unvisible', this);
			}
		}

	}, 
    
    setUpdatingState : function(isLayoutUpdating){
    	this.isLayoutUpdating = isLayoutUpdating;
    	this.layout.setUpdatingState(this.isLayoutUpdating);
    }
});