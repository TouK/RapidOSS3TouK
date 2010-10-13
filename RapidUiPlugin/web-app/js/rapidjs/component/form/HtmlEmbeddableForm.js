YAHOO.namespace('rapidjs', 'rapidjs.component');

YAHOO.rapidjs.component.HtmlEmbeddableForm = function(container, config, htmlComponent) {
    var configClone=YAHOO.rapidjs.ObjectUtils.clone(config,true);
    var id=YAHOO.util.Dom.generateId(null, 'r-htmlembeddable-form')
    if(configClone["id"])
    {
        id=configClone["id"];
    }
    this.useDefaultButtons=true;
    this.submitConfirmation = configClone.submitConfirmation;
    this.submitButtonText = configClone.submitButtonText || "Save";
    if(configClone["useDefaultButtons"]!=null)
    {
       if(configClone["useDefaultButtons"]==false)
       {
          this.useDefaultButtons=false; 
       }
    }
    YAHOO.rapidjs.component.HtmlEmbeddableForm.superclass.constructor.call(this, container,
    {id:id,subscribeToHistoryChange:false,timeout:htmlComponent.timeout})
    this.htmlComp = htmlComponent;
    htmlComponent.events['bodyCleared'].subscribe(this.destroy, this, true);
    this.events['loadstatechanged'].subscribe(this.hideMask, this, true);
    var events = {
        'submitSuccessful': new YAHOO.util.CustomEvent('submitSuccessful'),
        'submit': new YAHOO.util.CustomEvent('submit')
    }
    YAHOO.ext.util.Config.apply(this.events, events);
    this.render();
};

YAHOO.lang.extend(YAHOO.rapidjs.component.HtmlEmbeddableForm, YAHOO.rapidjs.component.PollingComponentContainer, {
    render:function() {
        this.registerForm();
        this.renderButtons();
        if (this.htmlComp.popupWindow) {
            var dialog = this.htmlComp.popupWindow.dialog;
            var panel = dialog.panel;
            dialog.adjustSize(panel.cfg.getProperty('width'), panel.cfg.getProperty('height'));
        }
    },

    renderButtons: function() {
        if(this.useDefaultButtons)
        {
            var dh = YAHOO.ext.DomHelper;
            var oSpan = dh.append(document.body, {tag:'span', cls:'button-group'});
            this.saveButton = new YAHOO.widget.Button({label:this.submitButtonText, onclick:{fn:this.submit, obj:this, scope:this}})
            this.saveButton.appendTo(oSpan);
            this.saveButton.addClass('default');
            this.cancelButton = new YAHOO.widget.Button({label:'Cancel', onclick:{fn:this.cancel, obj:this, scope:this}})
            this.cancelButton.appendTo(oSpan);
            if (this.htmlComp.popupWindow) {
                this.htmlComp.popupWindow.dialog.panel.setFooter(oSpan);
                var buttons = this.htmlComp.popupWindow.dialog.getButtons();
                buttons[buttons.length] = this.saveButton;
                buttons[buttons.length] = this.cancelButton;
            }
            else {
                var footer = dh.append(this.container, {tag:'div'});
                footer.appendChild(oSpan);
            }
        }
    },

    handleSuccess: function(response)
    {
        this.events['submitSuccessful'].fireDirect(response);
        this.htmlComp.fireSuccessEvent();
        if(this.useDefaultButtons)
        {
            if (this.htmlComp.popupWindow) {
                this.htmlComp.popupWindow.hide();
            }
        }
    },
    handleErrors: function(errors)
    {
        this.htmlComp.handleErrors(errors);
    },

    destroy: function() {
        delete YAHOO.rapidjs.Components[this.id];
        YAHOO.util.Event.purgeElement(this.form);
        this.htmlComp.events['bodyCleared'].unsubscribe(this.destroy, this);
        for (var event in this.events) {
            var cEvent = this.events[event];
            cEvent.unsubscribeAll();
        }
        if(this.useDefaultButtons)
        {
            this.saveButton.destroy();
            this.cancelButton.destroy();
        }
        if (this.htmlComp.popupWindow) {
            this.htmlComp.popupWindow.dialog._buttons = [];
        }
    },

    cancel: function() {
        if (this.htmlComp.popupWindow) {
            this.htmlComp.popupWindow.hide();
        }
    },
    hideMask:function() {
        this.htmlComp.hideMask();
        if(this.useDefaultButtons)
        {
            this.saveButton.set('disabled', false)
            this.cancelButton.set('disabled', false)
        }
    },
    showMask: function() {
        this.htmlComp.showMask();
        if(this.useDefaultButtons)
        {
            this.saveButton.set('disabled', true)
            this.cancelButton.set('disabled', true)
        }
    },
    _getFormAttributes : function(oForm) {
        var attrs = {
            method : null,
            action : null
        };

        if (oForm) {
            if (oForm.getAttributeNode) {
                var action = oForm.getAttributeNode("action");
                var method = oForm.getAttributeNode("method");

                if (action) {
                    attrs.action = action.value;
                }

                if (method) {
                    attrs.method = method.value;
                }

            } else {
                attrs.action = oForm.getAttribute("action");
                attrs.method = oForm.getAttribute("method");
            }
        }

        attrs.method = (YAHOO.lang.isString(attrs.method) ? attrs.method : "POST").toUpperCase();
        attrs.action = YAHOO.lang.isString(attrs.action) ? getUrlPrefix() + attrs.action : "";

        return attrs;
    },
    registerForm: function() {

        var form = this.container.getElementsByTagName("form")[0];

        if (this.form) {
            if (this.form == form && YAHOO.util.Dom.isAncestor(this.container, this.form)) {
                return;
            } else {
                YAHOO.util.Event.purgeElement(this.form);
                this.form = null;
            }
        }

        if (!form) {
            form = document.createElement("form");
            form.name = "frm_" + this.id;
            this.container.appendChild(form);
        }

        if (form) {
            this.form = form;
            YAHOO.util.Event.on(form, "submit", this._submitHandler, this, true);
        }
    },
    _submitHandler : function(e) {
        YAHOO.util.Event.stopEvent(e);
        this.submit();
        this.form.blur();
    },

    submit: function() {
        if(!this.submitConfirmation || eval('(' + this.submitConfirmation + ')')){
            var oForm = this.form,
                bUseFileUpload = false,
                bUseSecureFileUpload = false,
                aElements,
                nElements,
                i,
                formAttrs;
        var callback = {
            success: this.requester.processSuccess,
            failure: this.requester.processFailure,
            timeout: this.requester.timeout,
            scope: this.requester,
            cache:false,
            argument : []
        };
        aElements = oForm.elements;
        nElements = aElements.length;
        if (nElements > 0) {
            i = nElements - 1;
            do {
                if (aElements[i].type == "file") {
                    bUseFileUpload = true;
                    callback.upload = this.requester.handleUpload;
                    callback.abort = this.requester.handleAbort;
                    break;
                }
            }
            while (i--);
        }
        if (bUseFileUpload && YAHOO.env.ua.ie && this.isSecure) {
            bUseSecureFileUpload = true;
        }

        var formAttrs = this._getFormAttributes(oForm);
        this.showMask();
        YAHOO.util.Connect.setForm(oForm, bUseFileUpload, bUseSecureFileUpload);
        YAHOO.util.Connect.asyncRequest(formAttrs.method, formAttrs.action, callback);    
        }

    },
    isSecure: function () {
        if (window.location.href.toLowerCase().indexOf("https") === 0) {
            return true;
        } else {
            return false;
        }
    }()

})
