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
YAHOO.namespace('rapidjs', 'rapidjs.component');
YAHOO.rapidjs.component.PopupWindow = function(component, config) {
    this.component = component;
    this.component.popupWindow = this;
    this.resizable = true;
    this.title = '&#160;'
    YAHOO.ext.util.Config.apply(this, config);
    this.dialog = new YAHOO.rapidjs.component.Dialog({
        width:this.width,
        height: this.height,
        minHeight:this.minHeight,
        minWidth: this.minWidth,
        maxWidth: this.maxWidth,
        maxHeight: this.maxHeight,
        resizable: this.resizable,
        x: this.x,
        y: this.y,
        title: this.title,
        close: true
    });
    this.setTitle(this.title);
    this.dialog.events['resize'].subscribe(this.handleWindowResize, this, true);
    this.dialog.body.appendChild(this.component.container);
    this.component.inPopupWindow();
    this.windowResized();

};

YAHOO.rapidjs.component.PopupWindow.prototype = {
   handleWindowResize: function(width, height){
      this.component.resize(width, height);
   },
   windowResized: function(){
       var panelBodyEl = getEl(this.dialog.panel.body);
       var height = panelBodyEl.getHeight() - panelBodyEl.getPadding('tb');
       this.handleWindowResize(this.dialog.bodyEl.getWidth(true), height);
   },
   show: function(){
       this.dialog.show();
       this.windowResized();
   },
   hide: function(){
       this.dialog.hide();
   },

   setTitle: function(title){
      this.title = title;
      this.dialog.setTitle(title);
      if(this.component.toolbar){
          this.component.toolbar.setTitle("");
      }
   },

   isVisible: function(){
      return this.dialog.isVisible(); 
   }
}