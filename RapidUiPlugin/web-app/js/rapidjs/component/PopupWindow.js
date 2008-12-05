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
    this.title = '&#160;'
    YAHOO.ext.util.Config.apply(this, config);
    this.dialog = new YAHOO.rapidjs.component.Dialog({
        width:this.width,
        height: this.height,
        minHeight:this.minHeight,
        minWidth: this.minWidth,
        maxWidth: this.maxWidth,
        maxHeight: this.maxHeight,
        x: this.x,
        y: this.y,
        title: this.title,
        close: true
    });
    this.setTitle(this.title);
    this.dialog.events['resize'].subscribe(this.windowResized, this, true);
    this.dialog.body.appendChild(this.component.container);
    this.windowResized(this.dialog.bodyEl.getWidth(true), this.dialog.bodyEl.getHeight(true));

};

YAHOO.rapidjs.component.PopupWindow.prototype = {
   windowResized: function(width, height){
       this.component.resize(width, height);
   },
   show: function(){
       this.dialog.show();
       this.windowResized(this.dialog.bodyEl.getWidth(), this.dialog.bodyEl.getHeight());
       this.component.handleVisible();
   },
   hide: function(){
       this.dialog.hide();
       this.component.handleUnvisible();
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