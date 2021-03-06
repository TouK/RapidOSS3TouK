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
YAHOO.namespace('rapidjs', 'rapidjs.component', 'rapidjs.component.treegrid');
YAHOO.rapidjs.component.treegrid.TreeHeaderCell = function(treeGridView, container, headerLabel, width, colIndex, isSortable)
{
    this.headerLabel = headerLabel;
    this.treeGridView = treeGridView;
    this.htmlElement = null;
    this.container = container;
    this.colIndex = colIndex;
    this.isSortable = isSortable;
    this.events =
    {
        'resize' : new YAHOO.util.CustomEvent('resize'),
        'click' : new YAHOO.util.CustomEvent('click')
    };
    this.render(this.container);
    this.setWidth(width);
    this.sortDirection = null;
};

YAHOO.rapidjs.component.treegrid.TreeHeaderCell.prototype =
{
    setWidth:function(width)
    {
        var splitterDifference = this.splitElement.offsetWidth + 2;
        this.width = width * 1;
        this.htmlElement.setWidth(this.width - splitterDifference);
        YAHOO.util.Dom.setStyle(this.wrapperElement, 'width', this.width + 'px');
    },
    getWidth:function()
    {
        return this.htmlElement.getWidth();
    },
    render:    function(parentHtmlElement)
    {
        if (this.htmlElement == null)
        {
            this.wrapperElement = document.createElement("td");
            parentHtmlElement.appendChild(this.wrapperElement);
            YAHOO.util.Dom.addClass(this.wrapperElement, 'r-tree-headercell');
            if (this.isSortable !== false) {
                getEl(this.wrapperElement).addClassOnOver('r-tree-headercell-on');
                YAHOO.util.Event.addListener(this.wrapperElement, 'click', this.headerClick, this, true);
            }
            this.htmlElement = YAHOO.ext.DomHelper.append(this.wrapperElement, {tag:'div', cls:'r-tree-hdcellpos',
                html:'<table border="0" cellpadding="0" cellspacing="0">' +
                     '<tbody><tr><td><span>' + this.headerLabel + '</span></td>' +
                     '<td><span class="sort-desc"></span><span class="sort-asc"></span></td>' +
                     '</tr></tbody></table>'}, true);
            var spans = this.htmlElement.dom.getElementsByTagName('span');
            this.sortDesc = spans[1];
            this.sortAsc = spans[2];
            this.splitElement = YAHOO.ext.DomHelper.append(this.wrapperElement, {tag:'div', cls:'r-tree-headersplit'});
            this.splitBar = new YAHOO.rapidjs.component.Split(this.splitElement, this.htmlElement.dom, null, YAHOO.rapidjs.component.Split.LEFT);
            YAHOO.util.Dom.addClass(this.splitBar.proxy, 'r-tree-column-sizer');
            YAHOO.util.Dom.setStyle(this.splitBar.proxy, 'background-color', '');

            var bodyHeight = parentHtmlElement.parentNode.parentNode.parentNode.parentNode.parentNode.offsetHeight - parentHtmlElement.parentNode.parentNode.parentNode.parentNode.offsetHeight - 130;
            this.splitBar.dd._resizeProxy = function() {
                var el = this.getDragEl();
                YAHOO.util.Dom.setStyle(el, 'height', bodyHeight + 'px');
            };

            this.splitBar.onMoved.subscribe(this.onColumnSplitterMoved, this, true);
        }
    },
    onColumnSplitterMoved: function(splitter, width)
    {
        var splitterDifference = this.splitElement.offsetWidth + 2;
        if (width <= splitterDifference)
        {
            width = splitterDifference + 1;
        }
        this.setWidth(width);
        this.events["resize"].fireDirect(this.colIndex, width);
    },

    headerClick: function() {
        if (this.treeGridView.isSortingDisabled == false) {
            var direction = null;
            if (this.sortDirection == 'asc') {
                direction = 'desc';
            }
            else if (this.sortDirection == 'desc') {
                direction = 'asc';
            }
            else {
                direction = 'asc';
            }
            this.events['click'].fireDirect(this, direction);
        }
    },

    updateSortState: function(direction) {
        this.sortDirection = direction;
        if (direction == 'asc') {
            YAHOO.util.Dom.setStyle(this.sortAsc, 'display', 'block');
            YAHOO.util.Dom.setStyle(this.sortDesc, 'display', 'none');
        } else if (direction == 'desc') {
            YAHOO.util.Dom.setStyle(this.sortAsc, 'display', 'none');
            YAHOO.util.Dom.setStyle(this.sortDesc, 'display', 'block');
        }
        else {
            YAHOO.util.Dom.setStyle(this.sortAsc, 'display', 'none');
            YAHOO.util.Dom.setStyle(this.sortDesc, 'display', 'none');
        }

    }
};