YAHOO.namespace('rapidjs', 'rapidjs.component');
YAHOO.rapidjs.component.Form = function(container, config)
{
    YAHOO.rapidjs.component.Form.superclass.constructor.call(this, container, config);
    this.dialog = new YAHOO.widget.Dialog(container, { width : config.width,
        fixedcenter : true,
        visible : false,
        constraintoviewport : true,
        close:false,
        postmethod:'none',
        buttons : [ { text:"Save", handler:this.handleSubmit.createDelegate(this), isDefault:true, scope:this },
            { text:"Cancel", handler:this.handleCancel.createDelegate(this), scope:this } ]
    });


    this.successful = config.successfulyExecuted;
    this.EDIT_MODE = 0;
    this.CREATE_MODE = 1;
    this.dialog.render();
    this.dialog.form.action = this.formAction;
    this.mapping = config.mapping;
    this.editUrl = config.editUrl;
    this.createUrl = config.createUrl;
    this.saveUrl = config.saveUrl;
    this.updateUrl = config.updateUrl;
    this.mode = this.CREATE_MODE;
    this.isSubmitInProggress = false;
    YAHOO.util.Event.addListener(this.dialog.body, 'keypress', this.handleKeypress, this, true);
    this.render();

};

YAHOO.lang.extend(YAHOO.rapidjs.component.Form, YAHOO.rapidjs.component.PollingComponentContainer, {
    render: function()
    {
        var dh = YAHOO.ext.DomHelper;
        this.errors = dh.insertBefore(this.dialog.form.firstChild, {tag: 'div', cls:'rapid-errors'}, true);
        this.errors.setVisibilityMode(YAHOO.ext.Element.DISPLAY);
    },
    formAction: function()
    {

    },
    handleTimeout: function(response)
    {
        this.errors.dom.innerHTML = "";
        var dh = YAHOO.ext.DomHelper;
        this.isSubmitInProggress = false;

        var listItem = dh.append(this.errors.dom, {tag:"li"});
        listItem.appendChild(document.createTextNode("Request timeout"));
        this.errors.show();
    },
    handleSuccess: function(response, keepExisting, removeAttribute)
    {
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
        }
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
                var options = xmlNode.childNodes
                for (var i = 0; i < options.length; i++)
                {
                    var option = options[i];
                    if(option.nodeType == 1){
                        var optionValue = option.firstChild.nodeValue;
                        SelectUtils.addOption(formElement, optionValue, optionValue);
                        if(option.getAttribute('selected')){
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
        this.errors.show();
    },

    show: function(mode, params)
    {
        this.errors.hide();
        this.mode = mode;
        if (mode == this.EDIT_MODE && this.editUrl != null)
        {
            this.doRequest(this.editUrl, params);

        }
        else if (mode == this.CREATE_MODE && this.createUrl != null)
        {
            this.doRequest(this.createUrl, params);
        }

        this.dialog.show();
    },
    hide: function()
    {
        this.errors.dom.innerHTML = "";
        this.errors.hide();
        this.abort();
        this.isSubmitInProggress = false;
        this.clearAllFields();
        this.dialog.form.blur();
        this.dialog.hide();
    },

    clearAllFields: function()
    {
        var formElements = this.dialog.form.elements;
        for (var i = 0; i < formElements.length; i++)
        {
            var formElement = formElements[i];
            if(formElement.nodeName == 'SELECT'){
                SelectUtils.clear(formElement);
            }
            else{
                formElement.value = '';
            }
        }
    }
})

