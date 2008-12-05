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
YAHOO.rapidjs.component.windows.ConsoleWindow = function(container, config){
	YAHOO.rapidjs.component.windows.ConsoleWindow.superclass.constructor.call(this, container, config);
	this.textArea = YAHOO.ext.DomHelper.append(this.container, {tag:'textarea', cls:'rapid-textarea'});
	this.textArea.readOnly = true;
	this.textArea.rows = 10;
	this.panel = new YAHOO.rapidjs.component.layout.RapidPanel(this.container, {title:this.title});
};

YAHOO.extendX(YAHOO.rapidjs.component.windows.ConsoleWindow, YAHOO.rapidjs.component.ComponentContainer, {
	appendText : function(text){
		var value = this.textArea.value;
		if(value != ''){
			this.textArea.value = this.textArea.value + '\n' + text;
		}
		else{
			this.textArea.value = text;
		}
		this.textArea.scrollTop = this.textArea.scrollHeight - this.textArea.clientHeight;
	}
});