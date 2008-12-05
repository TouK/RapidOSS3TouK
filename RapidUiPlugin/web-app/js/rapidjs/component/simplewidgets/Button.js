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
YAHOO.rapidjs.component.Button = function(container, config){
    YAHOO.ext.util.Config.apply(this, config);
    this.init(container);
};

YAHOO.rapidjs.component.Button.prototype = {
    init : function(appendTo){
        var element = document.createElement('span');
        element.className = 'r-button';
        if(this.id){
            element.id = this.id;
        }
        this.setDisabled(this.disabled === true);
        var inner = document.createElement('span');
        inner.className = 'r-button-inner ' + this.className;
        inner.unselectable = 'on';
        if(this.tooltip){
            element.setAttribute('title', this.tooltip);
        }
        if(this.style){
           YAHOO.ext.DomHelper.applyStyles(inner, this.style);
        }
        element.appendChild(inner);
        appendTo.appendChild(element);
        this.el = getEl(element, true);
        this.el.unselectable();
        inner.innerHTML = (this.text ? this.text : '&#160;');
        this.inner = inner;
        this.el.mon('click', this.onClick, this, true);
        this.el.mon('mouseover', this.onMouseOver, this, true);
        this.el.mon('mouseout', this.onMouseOut, this, true);
        this.el.mon('mousedown', this.onMouseDown, this, true);
        this.el.mon('mouseup', this.onMouseUp, this, true);
    },

    setHandler : function(click, scope){
        this.click = click;
        this.scope = scope;
    },

    setText : function(text){
        this.inner.innerHTML = text;
    },

    setTooltip : function(text){
        this.el.dom.title = text;
    },

    show: function(){
        this.el.dom.parentNode.style.display = '';
    },

    hide: function(){
        this.el.dom.parentNode.style.display = 'none';
    },

    disable : function(){
        this.disabled = true;
        if(this.el){
            this.el.addClass('r-button-disabled');
        }
    },
    enable : function(){
        this.disabled = false;
        if(this.el){
            this.el.removeClass('r-button-disabled');
        }
    },

    isDisabled : function(){
        return this.disabled === true;
    },

    setDisabled : function(disabled){
        if(disabled){
            this.disable();
        }else{
            this.enable();
        }
    },

    onClick : function(){
        if(!this.disabled && this.click){
            this.click.call(this.scope || window, this);
        }
    },

    onMouseOver : function(){
        if(!this.disabled){
            this.el.addClass('r-button-over');
            if(this.mouseover){
                this.mouseover.call(this.scope || window, this);
            }
        }
    },

    onMouseOut : function(){
        this.el.removeClass('r-button-over');
        if(!this.disabled){
            if(this.mouseout){
                this.mouseout.call(this.scope || window, this);
            }
        }
    },

    onMouseDown : function(){
        if(!this.disabled){
        	this.el.removeClass('r-button-over');
            this.el.addClass('r-button-down');
        }
    },

    onMouseUp : function(){
        if(!this.disabled){
            this.el.removeClass('r-button-down');
            this.el.addClass('r-button-over');
        }
    }
};