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

