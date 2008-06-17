/*
 * yui-ext 0.33
 * Copyright(c) 2006, Jack Slocum.
 */


YAHOO.ext.LayoutRegion=function(mgr,config,pos){this.mgr=mgr;this.position=pos;var dh=YAHOO.ext.DomHelper;this.el=dh.append(mgr.el.dom,{tag:'div',cls:'ylayout-panel ylayout-panel-'+this.position},true);this.titleEl=dh.append(this.el.dom,{tag:'div',unselectable:'on',cls:'yunselectable ylayout-panel-hd ylayout-title-'+this.position,children:[{tag:'span',cls:'yunselectable ylayout-panel-hd-text',unselectable:'on',html:'&#160;'},{tag:'div',cls:'yunselectable ylayout-panel-hd-tools',unselectable:'on'}]},true);this.titleEl.enableDisplayMode();this.titleTextEl=this.titleEl.dom.firstChild;this.tools=getEl(this.titleEl.dom.childNodes[1],true);this.closeBtn=this.createTool(this.tools.dom,'ylayout-close');this.closeBtn.enableDisplayMode();this.closeBtn.on('click',this.closeClicked,this,true);this.closeBtn.hide();this.bodyEl=dh.append(this.el.dom,{tag:'div',cls:'ylayout-panel-body'},true);this.events={'beforeremove':new YAHOO.util.CustomEvent('beforeremove'),'invalidated':new YAHOO.util.CustomEvent('invalidated'),'visibilitychange':new YAHOO.util.CustomEvent('visibilitychange'),'paneladded':new YAHOO.util.CustomEvent('paneladded'),'panelremoved':new YAHOO.util.CustomEvent('panelremoved'),'collapsed':new YAHOO.util.CustomEvent('collapsed'),'expanded':new YAHOO.util.CustomEvent('expanded'),'panelactivated':new YAHOO.util.CustomEvent('panelactivated'),'resized':new YAHOO.util.CustomEvent('resized')};this.panels=new YAHOO.ext.util.MixedCollection();this.panels.getKey=this.getPanelId.createDelegate(this);this.box=null;this.visible=false;this.collapsed=false;this.hide();this.on('paneladded',this.validateVisibility,this,true);this.on('panelremoved',this.validateVisibility,this,true);this.activePanel=null;this.applyConfig(config);};YAHOO.extendX(YAHOO.ext.LayoutRegion,YAHOO.ext.util.Observable,{getPanelId:function(p){return p.getId();},applyConfig:function(config){if(config.collapsible&&this.position!='center'&&!this.collapsedEl){var dh=YAHOO.ext.DomHelper;this.collapseBtn=this.createTool(this.tools.dom,'ylayout-collapse-'+this.position);this.collapseBtn.mon('click',this.collapse,this,true);this.collapsedEl=dh.append(this.mgr.el.dom,{tag:'div',cls:'ylayout-collapsed ylayout-collapsed-'+this.position,children:[{tag:'div',cls:'ylayout-collapsed-tools'}]},true);if(config.floatable!==false){this.collapsedEl.addClassOnOver('ylayout-collapsed-over');this.collapsedEl.mon('click',this.collapseClick,this,true);}
this.expandBtn=this.createTool(this.collapsedEl.dom.firstChild,'ylayout-expand-'+this.position);this.expandBtn.mon('click',this.expand,this,true);}
if(this.collapseBtn){this.collapseBtn.setVisible(config.collapsible==true);}
this.cmargins=config.cmargins||this.cmargins||(this.position=='west'||this.position=='east'?{top:0,left:2,right:2,bottom:0}:{top:2,left:0,right:0,bottom:2});this.margins=config.margins||this.margins||{top:0,left:0,right:0,bottom:0};this.bottomTabs=config.tabPosition!='top';this.autoScroll=config.autoScroll||false;if(this.autoScroll){this.bodyEl.setStyle('overflow','auto');}else{this.bodyEl.setStyle('overflow','hidden');}
if((!config.titlebar&&!config.title)||config.titlebar===false){this.titleEl.hide();}else{this.titleEl.show();if(config.title){this.titleTextEl.innerHTML=config.title;}}
this.duration=config.duration||.30;this.slideDuration=config.slideDuration||.45;this.config=config;if(config.collapsed){this.collapse(true);}},resizeTo:function(newSize){switch(this.position){case'east':case'west':this.el.setWidth(newSize);this.fireEvent('resized',this,newSize);break;case'north':case'south':this.el.setHeight(newSize);this.fireEvent('resized',this,newSize);break;}},getBox:function(){var b;if(!this.collapsed){b=this.el.getBox(false,true);}else{b=this.collapsedEl.getBox(false,true);}
return b;},getMargins:function(){return this.collapsed?this.cmargins:this.margins;},highlight:function(){this.el.addClass('ylayout-panel-dragover');},unhighlight:function(){this.el.removeClass('ylayout-panel-dragover');},updateBox:function(box){this.box=box;if(!this.collapsed){this.el.dom.style.left=box.x+'px';this.el.dom.style.top=box.y+'px';this.el.setSize(box.width,box.height);var bodyHeight=this.titleEl.isVisible()?box.height-(this.titleEl.getHeight()||0):box.height;bodyHeight-=this.el.getBorderWidth('tb');bodyWidth=box.width-this.el.getBorderWidth('rl');this.bodyEl.setHeight(bodyHeight);this.bodyEl.setWidth(bodyWidth);var tabHeight=bodyHeight;if(this.tabs){tabHeight=this.tabs.syncHeight(bodyHeight);if(YAHOO.ext.util.Browser.isIE)this.tabs.el.repaint();}
this.panelSize={width:bodyWidth,height:tabHeight};if(this.activePanel){this.activePanel.setSize(bodyWidth,tabHeight);}}else{this.collapsedEl.dom.style.left=box.x+'px';this.collapsedEl.dom.style.top=box.y+'px';this.collapsedEl.setSize(box.width,box.height);}
if(this.tabs){this.tabs.autoSizeTabs();}},getEl:function(){return this.el;},hide:function(){if(!this.collapsed){this.el.dom.style.left='-2000px';this.el.hide();}else{this.collapsedEl.dom.style.left='-2000px';this.collapsedEl.hide();}
this.visible=false;this.fireEvent('visibilitychange',this,false);},show:function(){if(!this.collapsed){this.el.show();}else{this.collapsedEl.show();}
this.visible=true;this.fireEvent('visibilitychange',this,true);},isVisible:function(){return this.visible;},closeClicked:function(){if(this.activePanel){this.remove(this.activePanel);}},collapseClick:function(e){if(this.isSlid){e.stopPropagation();this.slideIn();}else{e.stopPropagation();this.slideOut();}},collapse:function(skipAnim){if(this.collapsed)return;this.collapsed=true;if(this.split){this.split.el.hide();}
if(this.config.animate&&skipAnim!==true){this.fireEvent('invalidated',this);this.animateCollapse();}else{this.el.setLocation(-20000,-20000);this.el.hide();this.collapsedEl.show();this.fireEvent('collapsed',this);this.fireEvent('invalidated',this);}},animateCollapse:function(){},expand:function(e,skipAnim){if(e)e.stopPropagation();if(!this.collapsed)return;if(this.isSlid){this.slideIn(this.expand.createDelegate(this));return;}
this.collapsed=false;this.el.show();if(this.config.animate&&skipAnim!==true){this.animateExpand();}else{if(this.split){this.split.el.show();}
this.collapsedEl.setLocation(-2000,-2000);this.collapsedEl.hide();this.fireEvent('invalidated',this);this.fireEvent('expanded',this);}},animateExpand:function(){},initTabs:function(){this.bodyEl.setStyle('overflow','hidden');var ts=new YAHOO.ext.TabPanel(this.bodyEl.dom,this.bottomTabs);this.tabs=ts;ts.resizeTabs=this.config.resizeTabs===true;ts.minTabWidth=this.config.minTabWidth||40;ts.maxTabWidth=this.config.maxTabWidth||250;ts.preferredTabWidth=this.config.preferredTabWidth||150;ts.monitorResize=false;ts.bodyEl.setStyle('overflow',this.config.autoScroll?'auto':'hidden');this.panels.each(this.initPanelAsTab,this);},initPanelAsTab:function(panel){var ti=this.tabs.addTab(panel.getEl().id,panel.getTitle(),null,this.config.closeOnTab&&panel.isClosable());ti.on('activate',function(){this.setActivePanel(panel);},this,true);if(this.config.closeOnTab){ti.on('beforeclose',function(t,e){e.cancel=true;this.remove(panel);},this,true);}
return ti;},updatePanelTitle:function(panel,title){if(this.activePanel==panel){this.updateTitle(title);}
if(this.tabs){this.tabs.getTab(panel.getEl().id).setText(title);}},updateTitle:function(title){if(this.titleTextEl&&!this.config.title){this.titleTextEl.innerHTML=(typeof title!='undefined'&&title.length>0?title:"&#160;");}},setActivePanel:function(panel){panel=this.getPanel(panel);if(this.activePanel&&this.activePanel!=panel){this.activePanel.setActiveState(false);}
this.activePanel=panel;panel.setActiveState(true);if(this.panelSize){panel.setSize(this.panelSize.width,this.panelSize.height);}
this.closeBtn.setVisible(!this.config.closeOnTab&&!this.isSlid&&panel.isClosable());this.updateTitle(panel.getTitle());this.fireEvent('panelactivated',this,panel);},showPanel:function(panel){if(panel=this.getPanel(panel)){if(this.tabs){this.tabs.activate(panel.getEl().id);}else{this.setActivePanel(panel);}}
return panel;},getActivePanel:function(){return this.activePanel;},validateVisibility:function(){if(this.panels.getCount()<1){this.updateTitle('&#160;');this.closeBtn.hide();this.hide();}else{if(!this.isVisible()){this.show();}}},add:function(panel){if(arguments.length>1){for(var i=0,len=arguments.length;i<len;i++){this.add(arguments[i]);}
return null;}
if(this.hasPanel(panel)){this.showPanel(panel);return panel;}
panel.setRegion(this);this.panels.add(panel);if(this.panels.getCount()==1&&!this.config.alwaysShowTabs){this.bodyEl.dom.appendChild(panel.getEl().dom);this.setActivePanel(panel);this.fireEvent('paneladded',this,panel);return panel;}
if(!this.tabs){this.initTabs();}else{this.initPanelAsTab(panel);}
this.tabs.activate(panel.getEl().id);this.fireEvent('paneladded',this,panel);return panel;},hasPanel:function(panel){if(typeof panel=='object'){panel=panel.getId();}
return this.getPanel(panel)?true:false;},hidePanel:function(panel){if(this.tabs&&(panel=this.getPanel(panel))){this.tabs.hideTab(panel.getEl().id);}},unhidePanel:function(panel){if(this.tabs&&(panel=this.getPanel(panel))){this.tabs.unhideTab(panel.getEl().id);}},clearPanels:function(){while(this.panels.getCount()>0){this.remove(this.panels.first());}},remove:function(panel,preservePanel){panel=this.getPanel(panel);if(!panel){return null;}
var e={};this.fireEvent('beforeremove',this,panel,e);if(e.cancel===true){return null;}
preservePanel=(typeof preservePanel!='undefined'?preservePanel:(this.config.preservePanels===true||panel.preserve===true));var panelId=panel.getId();this.panels.removeKey(panelId);if(preservePanel){document.body.appendChild(panel.getEl().dom);}
if(this.tabs){this.tabs.removeTab(panel.getEl().id);}else if(!preservePanel){this.bodyEl.dom.removeChild(panel.getEl().dom);}
if(this.panels.getCount()==1&&this.tabs&&!this.config.alwaysShowTabs){var p=this.panels.first();var tempEl=document.createElement('span');tempEl.appendChild(p.getEl().dom);this.bodyEl.update('');this.bodyEl.dom.appendChild(p.getEl().dom);tempEl=null;this.updateTitle(p.getTitle());this.tabs=null;this.bodyEl.setStyle('overflow',this.config.autoScroll?'auto':'hidden');this.setActivePanel(p);}
panel.setRegion(null);if(this.activePanel==panel){this.activePanel=null;}
if(this.config.autoDestroy!==false&&preservePanel!==true){try{panel.destroy();}catch(e){}}
this.fireEvent('panelremoved',this,panel);return panel;},getTabs:function(){return this.tabs;},getPanel:function(id){if(typeof id=='object'){return id;}
return this.panels.get(id);},getPosition:function(){return this.position;},createTool:function(parentEl,className){var btn=YAHOO.ext.DomHelper.append(parentEl,{tag:'div',cls:'ylayout-tools-button',children:[{tag:'div',cls:'ylayout-tools-button-inner '+className,html:'&#160;'}]},true);btn.addClassOnOver('ylayout-tools-button-over');return btn;}});