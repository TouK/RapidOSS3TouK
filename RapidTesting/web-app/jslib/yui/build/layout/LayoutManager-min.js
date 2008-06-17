/*
 * yui-ext 0.33
 * Copyright(c) 2006, Jack Slocum.
 */


YAHOO.ext.LayoutManager=function(container){YAHOO.ext.LayoutManager.superclass.constructor.call(this);this.el=getEl(container,true);this.id=this.el.id;this.el.addClass('ylayout-container');this.monitorWindowResize=true;this.regions={};this.events={'layout':new YAHOO.util.CustomEvent(),'regionresized':new YAHOO.util.CustomEvent(),'regioncollapsed':new YAHOO.util.CustomEvent(),'regionexpanded':new YAHOO.util.CustomEvent()};this.updating=false;YAHOO.ext.EventManager.onWindowResize(this.onWindowResize,this,true);};YAHOO.extendX(YAHOO.ext.LayoutManager,YAHOO.ext.util.Observable,{isUpdating:function(){return this.updating;},beginUpdate:function(){this.updating=true;},endUpdate:function(noLayout){this.updating=false;if(!noLayout){this.layout();}},layout:function(){},onRegionResized:function(region,newSize){this.fireEvent('regionresized',region,newSize);this.layout();},onRegionCollapsed:function(region){this.fireEvent('regioncollapsed',region);},onRegionExpanded:function(region){this.fireEvent('regionexpanded',region);},getViewSize:function(){var size;if(this.el.dom!=document.body){this.el.beginMeasure();size=this.el.getSize();this.el.endMeasure();}else{size={width:YAHOO.util.Dom.getViewportWidth(),height:YAHOO.util.Dom.getViewportHeight()};}
size.width-=this.el.getBorderWidth('lr')-this.el.getPadding('lr');size.height-=this.el.getBorderWidth('tb')-this.el.getPadding('tb');return size;},getEl:function(){return this.el;},getRegion:function(target){return this.regions[target.toLowerCase()];},onWindowResize:function(){if(this.monitorWindowResize){this.layout();}}});