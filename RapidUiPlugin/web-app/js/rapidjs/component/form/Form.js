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

YAHOO.rapidjs.component.Form = function(container, config)
{
    YAHOO.rapidjs.component.Form.superclass.constructor.call(this, container, config);
    this.dialog = new YAHOO.widget.Dialog(container, { width : config.width,
        fixedcenter : true,
        visible : false,
        constraintoviewport : true,
        hideaftersubmit:false,
        close:false,
        postmethod:'none',
        buttons : [ { text:"Save", handler:this.handleSubmit.createDelegate(this), isDefault:true, scope:this },
            { text:"Cancel", handler:this.handleCancel.createDelegate(this), scope:this } ]
    });

    this.dialog.hideEvent.subscribe(function() {
        YAHOO.util.Dom.setStyle(this.dialog.form, 'overflow', 'hidden');
        YAHOO.util.Event.removeListener(this.dialog.form, 'keypress');
    }, this, true)
    this.dialog.beforeShowEvent.subscribe(function() {
        YAHOO.util.Dom.setStyle(this.dialog.form, 'overflow', 'auto');
        YAHOO.util.Event.addListener(this.dialog.form, 'keypress', this.handleKeypress, this, true);
    }, this, true)
    this.dialog.render();
    YAHOO.rapidjs.component.OVERLAY_MANAGER.register(this.dialog);
    this.mapping = config.mapping;
    this.editUrl = config.editUrl;
    this.createUrl = config.createUrl;
    this.saveUrl = config.saveUrl;
    this.submitAction = config.submitAction || 'GET'
    this.updateUrl = config.updateUrl;
    this.mode = YAHOO.rapidjs.component.Form.CREATE_MODE;
    this.isSubmitInProggress = false;
    this.fieldParams = null;
    var events = {
        'submitSuccessful': new YAHOO.util.CustomEvent('submitSuccessful'),
        'submit': new YAHOO.util.CustomEvent('submit')
    }
    YAHOO.ext.util.Config.apply(this.events, events);
    this.render();

};

YAHOO.rapidjs.component.Form.EDIT_MODE = "edit";
YAHOO.rapidjs.component.Form.CREATE_MODE = "create";

YAHOO.lang.extend(YAHOO.rapidjs.component.Form, YAHOO.rapidjs.component.PollingComponentContainer, {
    render: function()
    {
        var dh = YAHOO.ext.DomHelper;
        this.errors = dh.insertBefore(this.dialog.form.firstChild, {tag: 'ul', cls:'rapid-errors'}, true);
        this.mask = dh.append(this.dialog.form, {tag:'div', cls:'rcmdb-form-mask'}, true);
        this.maskMessage = dh.append(this.dialog.form, {tag:'div', cls:'rcmdb-form-mask-loadingwrp', html:'<div class="rcmdb-form-mask-loading">Loading...</div>'}, true)
        this.hideMask();
        this.errors.setVisibilityMode(YAHOO.ext.Element.DISPLAY);
        YAHOO.util.Dom.setStyle(this.container.parentNode, "top", "-15000px");

    },
    handleSuccess: function(response, keepExisting, removeAttribute)
    {
        this.hideMask();
        if (this.isSubmitInProggress)
        {
            this.events['submitSuccessful'].fireDirect(response);
            this.hide();
            return;
        }
        var formElements = this.dialog.form.elements;
        for (var i = 0; i < formElements.length; i++)
        {
            this.setFormElementValue(formElements[i], response.responseXML);

            if (this.fieldParams != null && this.fieldParams[formElements[i].name] != null)
            {
                if (formElements[i].nodeName == 'SELECT')
                    SelectUtils.selectTheValue(formElements[i], this.fieldParams[formElements[i].name], 'Default');
                else
                    formElements[i].value = this.fieldParams[formElements[i].name];
            }
        }
        this.fieldParams = null;
        this.isSubmitInProggress = false;
    },

    setFormElementValue: function(formElement, rootNode) {
        var dataTagName = formElement.name;
        if (this.mapping != null && this.mapping[dataTagName] != null)
        {
            dataTagName = this.mapping[formElement.name];
        }
        var xmlNode = null;
        var nodes = rootNode.getElementsByTagName(dataTagName);
        if (nodes.length > 0) {
            xmlNode = nodes[0];
            if (formElement.nodeName == 'SELECT') {
                SelectUtils.clear(formElement);
                var options = xmlNode.childNodes
                for (var i = 0; i < options.length; i++)
                {
                    var option = options[i];
                    if (option.nodeType == 1) {
                        var optionValue = option.firstChild.nodeValue;
                        SelectUtils.addOption(formElement, optionValue, optionValue);
                        if (option.getAttribute('selected') == 'true') {
                            SelectUtils.selectTheValue(formElement, optionValue, 0);
                        }
                    }
                }
            }
            else {
                formElement.value = xmlNode.firstChild ? xmlNode.firstChild.nodeValue : "";
            }
        }
    },
    handleKeypress: function(e)
    {
        if ((e.type == "keypress" && e.keyCode == 13))
        {
            this.handleSubmit();
        }
    },

    handleSubmit: function()
    {
        this.errors.dom.innerHTML = "";
        this.errors.hide();
        this.isSubmitInProggress = true;
        if (this.mode == YAHOO.rapidjs.component.Form.EDIT_MODE)
        {
            this.url = this.updateUrl;

        }
        else if (this.mode == YAHOO.rapidjs.component.Form.CREATE_MODE)
        {
            this.url = this.saveUrl;
        }
        var formElements = this.dialog.form.elements;
        var params = {};
        for (var i = 0; i < formElements.length; i++)
        {
            var formElement = formElements[i];
            params[formElement.name] = this.getFormElementValue(formElement);
        }
        this.showMask();
        if (this.submitAction == "GET")
        {
            this.doGetRequest(this.url, params);
        }
        else
        {
            this.doPostRequest(this.url, params);
        }
        this.events['submit'].fireDirect();
    },
    handleCancel: function() {
        this.hide();
    },
    getFormElementValue : function(formElement) {
        if (formElement.nodeName == 'SELECT') {
            return formElement.options[formElement.selectedIndex].value;
        }
        else {
            return formElement.value;
        }
    },

    handleErrors: function(errors)
    {
        var dh = YAHOO.ext.DomHelper;
        this.isSubmitInProggress = false;
        for (var i = 0; i < errors.length; i++)
        {
            var listItem = dh.append(this.errors.dom, {tag:"li"});
            listItem.appendChild(document.createTextNode(errors[i]));
        }
        this.hideMask();
        this.errors.show();

    },

    show: function(mode, params, fieldParams)
    {
        this.errors.hide();
        this.hideMask();
        this.clearAllFields();
        this.mode = mode;
        var willShowMask = false;
        if (fieldParams != null)
            this.fieldParams = fieldParams;
        if (mode == YAHOO.rapidjs.component.Form.EDIT_MODE && this.editUrl != null)
        {
            willShowMask = true;
            this.doRequest(this.editUrl, params);

        }
        else if (mode == YAHOO.rapidjs.component.Form.CREATE_MODE && this.createUrl != null)
        {
            willShowMask = true;
            this.doRequest(this.createUrl, params);
        }
        else if (this.fieldParams != null) {
            var formElements = this.dialog.form.elements;
            for (var i = 0; i < formElements.length; i++)
            {
                if (this.fieldParams != null && this.fieldParams[formElements[i].name] != null)
                {
                    if (formElements[i].nodeName == 'SELECT')
                        SelectUtils.selectTheValue(formElements[i], this.fieldParams[formElements[i].name], 'Default');
                    else
                        formElements[i].value = this.fieldParams[formElements[i].name];
                }
            }
            this.fieldParams = null;
        }

        this.dialog.show();
        YAHOO.rapidjs.component.OVERLAY_MANAGER.bringToTop(this.dialog);
        if (willShowMask) {
            this.showMask();
        }
    },
    hide: function()
    {
        this.abort();
        this.isSubmitInProggress = false;
        this.clearAllFields();
        this.dialog.hide();
        this.errors.dom.innerHTML = "";
        this.errors.hide();
        YAHOO.util.Dom.setStyle(this.container.parentNode, "top", "-15000px");
    },

    clearAllFields: function()
    {
        var formElements = this.dialog.form.elements;
        for (var i = 0; i < formElements.length; i++)
        {
            var formElement = formElements[i];
            if (formElement.nodeName != 'SELECT') {
                formElement.value = '';
            }
        }
    },
    disableFormButtons: function()
    {
        YAHOO.util.Dom.removeClass(this.dialog.buttonSpan.firstChild, 'default');
        for (var i = 0; i < this.dialog.getButtons().length; i++)
        {
            this.dialog.getButtons()[i].set('disabled', true);
        }

    },
    enableFormButtons: function()
    {
        YAHOO.util.Dom.addClass(this.dialog.buttonSpan.firstChild, 'default');
        for (var i = 0; i < this.dialog.getButtons().length; i++)
            this.dialog.getButtons()[i].set('disabled', false);
    },

    showMask: function() {
        this.mask.show();
        this.maskMessage.show();
        this.mask.setRegion(getEl(this.dialog.body).getRegion());
        this.maskMessage.center(this.mask.dom);
        this.disableFormButtons();

    },
    hideMask: function() {
        this.enableFormButtons();
        this.mask.hide();
        this.maskMessage.hide();
    }
})

