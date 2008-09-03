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

	this.dialog.hideEvent.subscribe(function(){
			YAHOO.util.Dom.setStyle(this.dialog.form, 'overflow', 'hidden');
			YAHOO.util.Event.removeListener(this.dialog.form, 'keypress');
		}, this, true)
	this.dialog.beforeShowEvent.subscribe(function(){
			YAHOO.util.Dom.setStyle(this.dialog.form, 'overflow', 'auto');
			YAHOO.util.Event.addListener(this.dialog.form, 'keypress', this.handleKeypress, this, true);
		}, this, true)
    this.successful = config.successfulyExecuted;
    this.EDIT_MODE = 0;
    this.CREATE_MODE = 1;
    this.dialog.render();
    this.mapping = config.mapping;
    this.editUrl = config.editUrl;
    this.createUrl = config.createUrl;
    this.saveUrl = config.saveUrl;
    this.updateUrl = config.updateUrl;
    this.mode = this.CREATE_MODE;
    this.isSubmitInProggress = false;
    this.fieldParams = null;
    this.render();

};

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
    handleTimeout: function(response)
    {
        this.errors.dom.innerHTML = "";
        var dh = YAHOO.ext.DomHelper;
        this.isSubmitInProggress = false;

        var listItem = dh.append(this.errors.dom, {tag:"li"});
        listItem.appendChild(document.createTextNode("Request timeout"));
        this.hideMask();
        this.errors.show();
    },
    handleSuccess: function(response, keepExisting, removeAttribute)
    {
	    this.hideMask();
        if (this.isSubmitInProggress)
        {
            this.successful();
            this.hide();
            return;
        }
        var formElements = this.dialog.form.elements;
        for (var i = 0; i < formElements.length; i++)
        {
	        this.setFormElementValue(formElements[i], response.responseXML);

	        if(this.fieldParams != null && this.fieldParams[formElements[i].name] != null)
	        {
		        if(formElements[i].nodeName == 'SELECT')
		        	SelectUtils.selectTheValue(formElements[i],this.fieldParams[formElements[i].name],'Default');
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
                    if(option.nodeType == 1){
                        var optionValue = option.firstChild.nodeValue;
                        SelectUtils.addOption(formElement, optionValue, optionValue);
                        if(option.getAttribute('selected') == 'true'){
                            SelectUtils.selectTheValue(formElement, optionValue, 0);
                        }
                    }
                }
            }
            else{
                formElement.value = xmlNode.firstChild.nodeValue
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
    handleUnknownUrl: function(response)
    {
	  	this.hideMask();
	    YAHOO.rapidjs.component.Form.superclass.handleUnknownUrl.call(this);
    },

    handleSubmit: function()
    {
        this.errors.dom.innerHTML = "";
        this.errors.hide();
        this.isSubmitInProggress = true;
        if (this.mode == this.EDIT_MODE)
        {
            this.url = this.updateUrl;

        }
        else if (this.mode == this.CREATE_MODE)
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
        this.doRequest(this.url, params);
    },
    handleCancel: function() {
	    this.hide();
    },
    getFormElementValue : function(formElement){
         if (formElement.nodeName == 'SELECT') {
             return formElement.options[formElement.selectedIndex].value;
         }
         else{
             return formElement.value;
         }
    },

    handleErrors: function(response)
    {
	    var dh = YAHOO.ext.DomHelper;
        this.isSubmitInProggress = false;
        var errors = YAHOO.rapidjs.Connect.getErrorMessages(response.responseXML);
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
        if(fieldParams != null)
       		this.fieldParams = fieldParams;
        if (mode == this.EDIT_MODE && this.editUrl != null)
        {
	        this.showMask();
	        this.doRequest(this.editUrl, params);

        }
        else if (mode == this.CREATE_MODE && this.createUrl != null)
        {
	        this.showMask();
            this.doRequest(this.createUrl, params);
        }

        this.dialog.show();
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
            if(formElement.nodeName != 'SELECT'){
                formElement.value = '';
            }
        }
    },
    disableFormButtons: function()
    {
	    YAHOO.util.Dom.removeClass(this.dialog.buttonSpan.firstChild,'default');
	    for(var i = 0; i <this.dialog.getButtons().length ; i++)
	    {
		    this.dialog.getButtons()[i].set('disabled',true);
    	}

    },
    enableFormButtons: function()
    {
	    YAHOO.util.Dom.addClass(this.dialog.buttonSpan.firstChild,'default');
	    for(var i = 0; i <this.dialog.getButtons().length ; i++)
	    	this.dialog.getButtons()[i].set('disabled',false);
    },

    showMask: function() {
        this.mask.setTop(this.dialog.body.offsetTop);
        this.mask.setWidth(this.dialog.body.clientWidth);
        this.mask.setHeight(this.dialog.body.clientHeight);
        YAHOO.util.Dom.setStyle(this.mask.dom, 'display', '');
        YAHOO.util.Dom.setStyle(this.maskMessage.dom, 'display', '');
        this.maskMessage.center(this.mask.dom);
        this.disableFormButtons();

    },
    hideMask: function() {
	    this.enableFormButtons();
        YAHOO.util.Dom.setStyle(this.mask.dom, 'display', 'none');
        YAHOO.util.Dom.setStyle(this.maskMessage.dom, 'display', 'none');

    }
})