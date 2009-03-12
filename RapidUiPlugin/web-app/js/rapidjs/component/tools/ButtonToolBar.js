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
YAHOO.namespace('rapidjs', 'rapidjs.component', 'rapidjs.component.tool');
YAHOO.rapidjs.component.tool.ButtonToolBar = function(container, config){
    var title = "";
    if(config.title){
        title = config.title;
    }
    this.el = YAHOO.ext.DomHelper.append(container, {tag:'div', cls:'r-buttontoolbar',
        html:'<span class="r-buttontoolbar-text">' + title+ '</span><div class="r-buttontoolbar-tools"></div>'});
    this.titleEl = this.el.childNodes[0]; 
    this.toolsEl = this.el.childNodes[1];
    this.tools = [];
};

YAHOO.rapidjs.component.tool.ButtonToolBar.prototype = {
    addButton : function(buttonConfig){
         var buttonContainer = YAHOO.ext.DomHelper.append(this.toolsEl, {tag:'div', cls:'r-buttontoolbar-button'});
         return new YAHOO.rapidjs.component.Button(buttonContainer, buttonConfig);
    },

    addTool : function(basicTool){
         var buttonContainer = YAHOO.ext.DomHelper.append(this.toolsEl, {tag:'div', cls:'r-buttontoolbar-button'});
        buttonContainer.appendChild(basicTool.button.el.dom);
        basicTool.containerChanged(buttonContainer);
        this.tools.push(basicTool);
    },

    setTitle: function(title){
        this.titleEl.innerHTML = title;
    }
}