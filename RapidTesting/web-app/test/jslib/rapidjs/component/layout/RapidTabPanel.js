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
YAHOO.rapidjs.component.layout.RapidTabPanel = function(container, config)
{
	YAHOO.rapidjs.component.layout.RapidTabPanel.superclass.constructor.call(this, container, config);
};

YAHOO.extendX(YAHOO.rapidjs.component.layout.RapidTabPanel, YAHOO.ext.TabPanel, {
	createStripList: function(strip){
	    // div wrapper for retard IE
	    strip.innerHTML = '<div class="ytab-strip-wrap"><table class="ytab-strip" cellspacing="0" cellpadding="0" border="0"><tbody><tr><td width="0%"></td><td width="100%"><table class="ytab-strip" cellspacing="0" cellpadding="0" border="0"><tbody><tr></tr></tbody></table></td><td width="0%"><table cellspacing="5" cellpadding="0" border="0"><tbody><tr></tr></tbody></table></td></tr></tbody></table></div>';
	    this.titleArea = strip.firstChild.firstChild.firstChild.firstChild.firstChild;
	    this.toolsArea = this.titleArea.nextSibling.nextSibling.firstChild.firstChild.firstChild;
	    return this.titleArea.nextSibling.firstChild.firstChild.firstChild;
	}
});