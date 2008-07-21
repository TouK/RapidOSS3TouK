YAHOO.namespace('rapidjs', 'rapidjs.component');
YAHOO.rapidjs.component.Form = function(container, config)
{
    YAHOO.rapidjs.component.Form.superclass.constructor.call(this, container, config);
    this.dialog = new YAHOO.widget.Dialog(container, { width : config.width,
                                      fixedcenter : true,
                                      visible : false,
                                      constraintoviewport : true,
                                      buttons : [ { text:"Save", handler:this.handleSubmit.createDelegate(this), isDefault:true, scope:this },
                                              { text:"Cancel", handler:this.handleCancel.createDelegate(this), scope:this } ]
                                    });

    YAHOO.util.Event.addListener(container, 'keypress', this.handleKeypress, this, true);

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
    handleSuccess: function(response)
    {
        if(this.isSubmitInProggress)
        {
            this.successful();
            this.hide();
            return;
        }
        var data = new YAHOO.rapidjs.data.RapidXmlDocument(response,[this.nodeId]);
		var node = this.getRootNode(data, response.responseText);
        var inputs = this.dialog.form.elements;
        for(var i=0; i < inputs.length; i++)
        {
            var input = inputs[i];

            var dataColumnName = input.name;
            if(this.mapping != null && this.mapping[dataColumnName] != null)
            {
                dataColumnName = this.mapping[input.name];;
            }
            input.value = node.getAttribute(dataColumnName);
        }
        this.isSubmitInProggress = false;
    },
    handleKeypress: function(e)
    {
        if( (e.type == "keypress" && e.keyCode == 13) )
        {
            this.handleSubmit();
        }
    },

    handleSubmit: function()
    {
        this.errors.dom.innerHTML = "";
        this.errors.hide() ;
        this.isSubmitInProggress = true;
        if(this.mode == this.EDIT_MODE)
        {
            this.url = this.updateUrl;

        }
        else if(this.mode  == this.CREATE_MODE)
        {
            this.url = this.saveUrl;
        }
        var inputs = this.dialog.form.elements;
        var params = {};
        for(var i=0; i < inputs.length; i++)
        {
            var input = inputs[i];
            params[input.name] = input.value;
        }
        this.doRequest(this.url, params);
    },
    handleCancel: function(){
        this.hide();
    },


    handleFailure: function(response)
    {
        var dh = YAHOO.ext.DomHelper;
        this.isSubmitInProggress = false;
        var errors = YAHOO.rapidjs.Connect.getErrorMessages(response.responseXML);
        for(var i=0; i < errors.length; i++)
        {
            var listItem = dh.append(this.errors.dom, {tag:"li"});
           listItem.appendChild(document.createTextNode(errors[i].getAttribute("error")));
        }
        this.errors.show();
    },

    show: function(mode)
    {
        this.errors.hide() ;
        this.mode = mode;
        if(mode == this.EDIT_MODE && this.editUrl != null)
        {
            this.doRequest(this.editUrl);

        }
        else if(mode  == this.CREATE_MODE && this.createUrl != null)
        {
            this.doRequest(this.createUrl);
        }

        this.dialog.show();
    },
    hide: function()
    {
        this.errors.dom.innerHTML = "";
        this.errors.hide() ;
        this.abort();
        this.isSubmitInProggress = false;
        this.clearAllFields();
        this.dialog.hide();
    },

    clearAllFields: function()
    {
        var inputs = this.dialog.form.elements;
        for(var i=0; i < inputs.length; i++)
        {
            inputs[i].value = null;   
        }
    }
})

