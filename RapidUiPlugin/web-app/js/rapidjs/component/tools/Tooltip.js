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
YAHOO.rapidjs.component.Tooltip = function() {
};
YAHOO.rapidjs.component.Tooltip.prototype = {
    init: function() {
        this.render();
        this.hide();
        this.showTask = new YAHOO.ext.util.DelayedTask(this.show, this);
        this.hideTask = new YAHOO.ext.util.DelayedTask(this.hide, this);
    },
    render : function() {
        var dh = YAHOO.ext.DomHelper;
        this.tooltip = dh.append(document.body, {tag:'div', cls:'r-tooltip'}, true);
        this.tipBody = dh.append(this.tooltip.dom, {tag:'div', cls:'r-tooltip-body'});
    },
    add : function(element, text)
    {
        YAHOO.util.Event.addListener(element, 'mouseover', function(event) {
            if (element.tooltipText || element.tooltipText != '') {
                this.tipBody.innerHTML = element.tooltipText;
                var pageX = YAHOO.util.Event.getPageX(event);
                var pageY = YAHOO.util.Event.getPageY(event);
                this.showTask.delay(150, this.show, this, [pageX,pageY]);
                this.hideTask.delay(10000);
            }
        }, this, true);
        YAHOO.util.Event.addListener(element, 'mouseout', this.handleMouseOut, this, true);
        element.tooltipText = text;
    },
    remove : function(element)
    {
        YAHOO.util.Event.removeListener(element, 'mouseover');
        YAHOO.util.Event.removeListener(element, 'mouseout', this.handleMouseOut);
        element.tooltipText = null;
    },
    update : function(element, text)
    {
        element.tooltipText = text;
    },
    show : function(pageX, pageY) {

        var offsetWidth = this.tooltip.dom.offsetWidth;
        var offsetHeight = this.tooltip.dom.offsetHeight;
        var viewPortWidth = YAHOO.util.Dom.getViewportWidth();
        var viewPortHeight = YAHOO.util.Dom.getViewportHeight();
        var scrollX = document.documentElement.scrollLeft || document.body.scrollLeft;
        var scrollY = document.documentElement.scrollTop || document.body.scrollTop;
        var leftConstraint = scrollX + 10;
        var rightConstraint = scrollX + viewPortWidth - offsetWidth - 10;
        if (pageX < leftConstraint) {
            pageX = leftConstraint;
        } else if (pageX > rightConstraint) {
            pageX = rightConstraint;
        }
        var topConstraint = scrollY + 10;
        var bottomConstraint = scrollY + viewPortHeight - offsetHeight - 10;
        if (pageY < topConstraint) {
            pageY = topConstraint;
        } else if (pageY > bottomConstraint) {
            pageY = bottomConstraint;
        }
        this.tooltip.setX(pageX);
        this.tooltip.setY(pageY);
        YAHOO.util.Dom.setStyle(this.tooltip.dom, 'visibility', 'visible');
    },

    hide : function() {
        YAHOO.util.Dom.setStyle(this.tooltip.dom, 'visibility', 'hidden');
    },
    handleMouseOut : function(event)
    {
        this.showTask.cancel();
        this.hide();
    }
}
var Tooltip = new YAHOO.rapidjs.component.Tooltip();
YAHOO.ext.EventManager.onDocumentReady(Tooltip.init, Tooltip, true);

YAHOO.rapidjs.component.ErrorTooltip = function(){
    YAHOO.rapidjs.component.ErrorTooltip.superclass.constructor.call(this);
}
YAHOO.lang.extend(YAHOO.rapidjs.component.ErrorTooltip, YAHOO.rapidjs.component.Tooltip, {
    render : function() {
        var dh = YAHOO.ext.DomHelper;
        this.tooltip = dh.append(document.body, {tag:'div', cls:'r-tooltip-error'}, true);
        this.tipBody = dh.append(this.tooltip.dom, {tag:'div', cls:'r-tooltip-body'});
    }
});
var ErrorTooltip = new YAHOO.rapidjs.component.ErrorTooltip();
YAHOO.ext.EventManager.onDocumentReady(ErrorTooltip.init, ErrorTooltip, true);