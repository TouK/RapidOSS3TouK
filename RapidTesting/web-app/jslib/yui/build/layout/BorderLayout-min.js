/*
 * yui-ext 0.33
 * Copyright(c) 2006, Jack Slocum.
 */


YAHOO.ext.BorderLayout=function(container,config){YAHOO.ext.BorderLayout.superclass.constructor.call(this,container);this.factory=config.factory||YAHOO.ext.BorderLayout.RegionFactory;this.hideOnLayout=config.hideOnLayout||false;for(var i=0,len=this.factory.validRegions.length;i<len;i++){var target=this.factory.validRegions[i];if(config[target]){this.addRegion(target,config[target]);}}};YAHOO.extendX(YAHOO.ext.BorderLayout,YAHOO.ext.LayoutManager,{addRegion:function(target,config){if(!this.regions[target]){var r=this.factory.create(target,this,config);this.regions[target]=r;r.on('visibilitychange',this.layout,this,true);r.on('paneladded',this.layout,this,true);r.on('panelremoved',this.layout,this,true);r.on('invalidated',this.layout,this,true);r.on('resized',this.onRegionResized,this,true);r.on('collapsed',this.onRegionCollapsed,this,true);r.on('expanded',this.onRegionExpanded,this,true);}
return this.regions[target];},layout:function(){if(this.updating)return;var size=this.getViewSize();var w=size.width,h=size.height;var centerW=w,centerH=h,centerY=0,centerX=0;var x=0,y=0;var rs=this.regions;var n=rs['north'],s=rs['south'],west=rs['west'],e=rs['east'],c=rs['center'];if(this.hideOnLayout){c.el.setStyle('display','none');}
if(n&&n.isVisible()){var b=n.getBox();var m=n.getMargins();b.width=w-(m.left+m.right);b.x=m.left;b.y=m.top;centerY=b.height+b.y+m.bottom;centerH-=centerY;n.updateBox(this.safeBox(b));}
if(s&&s.isVisible()){var b=s.getBox();var m=s.getMargins();b.width=w-(m.left+m.right);b.x=m.left;var totalHeight=(b.height+m.top+m.bottom);b.y=h-totalHeight+m.top;centerH-=totalHeight;s.updateBox(this.safeBox(b));}
if(west&&west.isVisible()){var b=west.getBox();var m=west.getMargins();b.height=centerH-(m.top+m.bottom);b.x=m.left;b.y=centerY+m.top;var totalWidth=(b.width+m.left+m.right);centerX+=totalWidth;centerW-=totalWidth;west.updateBox(this.safeBox(b));}
if(e&&e.isVisible()){var b=e.getBox();var m=e.getMargins();b.height=centerH-(m.top+m.bottom);var totalWidth=(b.width+m.left+m.right);b.x=w-totalWidth+m.left;b.y=centerY+m.top;centerW-=totalWidth;e.updateBox(this.safeBox(b));}
if(c){var m=c.getMargins();var centerBox={x:centerX+m.left,y:centerY+m.top,width:centerW-(m.left+m.right),height:centerH-(m.top+m.bottom)};if(this.hideOnLayout){c.el.setStyle('display','block');}
c.updateBox(this.safeBox(centerBox));}
this.el.repaint();this.fireEvent('layout',this);},safeBox:function(box){box.width=Math.max(0,box.width);box.height=Math.max(0,box.height);return box;},add:function(target,panel){target=target.toLowerCase();return this.regions[target].add(panel);},remove:function(target,panel){target=target.toLowerCase();return this.regions[target].remove(panel);},findPanel:function(panelId){var rs=this.regions;for(var target in rs){if(typeof rs[target]!='function'){var p=rs[target].getPanel(panelId);if(p){return p;}}}
return null;},showPanel:function(panelId){var rs=this.regions;for(var target in rs){var r=rs[target];if(typeof r!='function'){if(r.hasPanel(panelId)){return r.showPanel(panelId);}}}
return null;},restoreState:function(provider){if(!provider){provider=YAHOO.ext.state.Manager;}
var sm=new YAHOO.ext.LayoutStateManager();sm.init(this,provider);}});YAHOO.ext.BorderLayout.RegionFactory={};YAHOO.ext.BorderLayout.RegionFactory.validRegions=['north','south','east','west','center'];YAHOO.ext.BorderLayout.RegionFactory.create=function(target,mgr,config){if(config.lightweight){return new YAHOO.ext.LayoutRegionLite(mgr,config);}
target=target.toLowerCase();switch(target){case'north':return new YAHOO.ext.NorthLayoutRegion(mgr,config);case'south':return new YAHOO.ext.SouthLayoutRegion(mgr,config);case'east':return new YAHOO.ext.EastLayoutRegion(mgr,config);case'west':return new YAHOO.ext.WestLayoutRegion(mgr,config);case'center':return new YAHOO.ext.CenterLayoutRegion(mgr,config);}
throw'Layout region "'+target+'" not supported.';};