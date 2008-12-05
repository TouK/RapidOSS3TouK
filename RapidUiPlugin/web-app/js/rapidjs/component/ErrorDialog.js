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
YAHOO.rapidjs.component.ErrorDialog = function(config)
{
    YAHOO.rapidjs.component.ErrorDialog.superclass.constructor.call(this, document.body, config);

    this.config = config;
    this.render();

};

YAHOO.lang.extend(YAHOO.rapidjs.component.ErrorDialog, YAHOO.rapidjs.component.ComponentContainer, {

    render: function()
    {
        var dh = YAHOO.ext.DomHelper;
        this.errors = dh.append(this.container, {tag:"div", cls:"errorDialog"}, true)
        this.errorDialog = new YAHOO.widget.Dialog(this.errors.dom, { width : this.config.width,
                                      fixedcenter : true,
                                      visible : false,
                                      constraintoviewport : false,
                                      buttons : [ { text:"Close", handler:this.handleClose.createDelegate(this), isDefault:true, scope:this }]
                                    });
        this.errors.setVisibilityMode(YAHOO.ext.Element.DISPLAY);
        this.errorDialog.render();
    },
    handleClose: function(){
        this.hide();
    },


    show: function(response)
    {
        //this.clearAllFields();
        var dh = YAHOO.ext.DomHelper;
        var errors = YAHOO.rapidjs.Connect.getErrorMessages(response.responseXML);
        for(var i=0; i < errors.length; i++)
        {
            var listItem = dh.append(this.errors.dom, {tag:"li"});
            listItem.appendChild(document.createTextNode(errors[i].getAttribute("field") + " " + errors[i].getAttribute("error")));
        }
        this.errorDialog.show();
    },
    hide: function()
    {
        // this.clearAllFields();
        this.errorDialog.hide();
    },
    clearAllFields: function()
    {
        var inputs = this.errorDialog.elements;
        for(var i=0; i < inputs.length; i++)
        {
            inputs[i].value = null;
        }
    }
})

