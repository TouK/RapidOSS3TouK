var EditArea_actiontabs = {

    init: function() {
        editArea.load_css(this.baseURL + "../../../../js/edit_area/plugins/actiontabs/css/actiontabs.css");
        editArea.load_css(this.baseURL + "../../../../js/yui/assets/skins/sam/skin.css");
        editArea.load_css(this.baseURL + "../../../../js/yui/container/assets/skins/sam/container.css");
        editArea.load_script(this.baseURL + "../../../../js/yui/yahoo-dom-event/yahoo-dom-event.js");
        editArea.load_script(this.baseURL + "../../../../js/yui/utilities/utilities.jss");
        editArea.load_script(this.baseURL + "../../../../js/yui/container/container-min.js");
        editArea.load_script(this.baseURL + "../../../../js/yui/dragdrop/dragdrop-min.js");
        editArea.load_script(this.baseURL + "../../../../js/yui/element/element-beta-min.js");
        editArea.load_script(this.baseURL + "../../../../js/yui/button/button-min.js");
        editArea.load_script(this.baseURL + "../../../../js/yui/resize/resize-min.js");
    }

    ,onload: function() {
        this.actionTabsContainer = document.createElement('div');
        this.actionTabsContainer.id = 'actionTabsContainer'
        this.actionTabContainer = document.createElement('div');
        this.actionTabContainer.id = 'actionTabContainer'
        this.actionToolbar = document.createElement('div');
        this.actionToolbar.id = "actionTabsToolbar";
        document.getElementById("customBottomArea").appendChild(this.actionTabsContainer);
        this.actionTabsContainer.appendChild(this.actionTabContainer)
        this.actionTabsContainer.appendChild(this.actionToolbar);
        this.buttonGroup = new EditArea_ActionTab_ButtonGroup();
        this.createMessagesContainer();
        this.messageTab.show();
        editArea.update_size();
    },
    createMessagesContainer: function(parentContainer){
        this.messageTab = new EditArea_MessagesActionTab(editArea, this.buttonGroup);
    },

    abortAction: function(message, messageDiv)
    {
        this.messageTab.abortAction(message, messageDiv);
    },
    startAction: function(message, cancelFunc)
    {
        return this.messageTab.startAction(message, cancelFunc);
    },
    appendMessage: function(message, type)
    {
        this.messageTab.appendMessage(message, type);
    }
    ,
    appendMessage: function(message, type, messageEl)
    {
        this.messageTab.appendMessage(message, type, messageEl);
    }
    ,get_control_html: function(ctrl_name) {

        return false;
    }

    /**
     * Is called each time the user touch a keyboard key.
     *
     * @param (event) e: the keydown event
     * @return true - pass to next handler in chain, false - stop chain execution
     * @type boolean
     */
    ,onkeydown: function(e) {
        return true;
    }
    ,execCommand: function(cmd, param) {
        return true;
    }
};

function EditArea_ActionTab_ButtonGroup()
{
    this.buttons = {};
}

function EditArea_ActionTab(config, editArea, buttonGroup)
{
    this.buttonGroup = buttonGroup;
    this.editArea = editArea;
    this.init(config);
}

function EditArea_MessagesActionTab(editArea, buttonGroup)
{
    this.config = {
        buttonId:'actionsTabMessagesButton',
        buttonText:'Messages',
        title:"Messages"
    }
    this.actionTab = new EditArea_ActionTab(this.config, editArea, buttonGroup);
    this.render();
};

EditArea_MessagesActionTab.prototype.show= function()
{
    this.actionTab.show();
};
EditArea_MessagesActionTab.prototype.clearMessages= function()
{
    this.messagesContainer.innerHTML = "";
};
EditArea_MessagesActionTab.prototype.abortAction= function(message, messageDiv)
{
    messageDiv.innerHTML = message;
    YAHOO.util.Dom.removeClass(messageDiv, 'messagesActionTabLineOnGoingActionLine');
    YAHOO.util.Dom.addClass(messageDiv, 'messagesActionTabErrorLine');
    messageDiv.cancelFunction = null;
};
EditArea_MessagesActionTab.prototype.startAction= function(message, cancelFunction)
{
    var lineDiv = document.createElement("div")
    lineDiv.cancelFunction = cancelFunction;
    YAHOO.util.Dom.addClass(lineDiv, 'messagesActionTabLine');
    YAHOO.util.Dom.addClass(lineDiv, 'messagesActionTabLineOnGoingActionLine');
    lineDiv.innerHTML = message;
    this.messagesContainer.appendChild(lineDiv);
    this.actionTab.checkScrollToBottom();
    return lineDiv;
};
EditArea_MessagesActionTab.prototype.appendMessage= function(message, type)
{
    this.appendMessage(message, type, null)
};

EditArea_MessagesActionTab.prototype.appendMessage= function(message, type, lineDivElement)
{
    var lineDiv = lineDivElement;
    if(lineDiv == null)
    {
        lineDiv = document.createElement("div")
    }
    else
    {
        YAHOO.util.Dom.removeClass(lineDiv, 'messagesActionTabLineOnGoingActionLine');
        lineDiv.cancelFunction = null;
    }
    YAHOO.util.Dom.addClass(lineDiv, 'messagesActionTabLine');
    if(type == ERROR_MESSAGE_TYPE)
    {
        YAHOO.util.Dom.addClass(lineDiv, 'messagesActionTabErrorLine');
    }
    lineDiv.innerHTML = message;
    this.messagesContainer.appendChild(lineDiv);
    this.actionTab.checkScrollToBottom();
    if(message == "")
    {
        lineDiv.style.height="18px";
    }
};

EditArea_MessagesActionTab.prototype.render= function()
{
    this.messagesContainer = document.createElement("div");
    YAHOO.util.Dom.addClass(this.messagesContainer, 'messagesActionTab');
    this.actionTab.getRenderArea().appendChild(this.messagesContainer);
    YAHOO.util.Event.addListener(this.messagesContainer, "dblclick", this.messageContainerDblClicked, this, true)
};
EditArea_MessagesActionTab.prototype.messageContainerDblClicked= function(ev)
{
    var clickedLine = YAHOO.util.Event.getTarget(ev, true)
    var cancelFunction = clickedLine.cancelFunction;
    if(cancelFunction != null)
    {
        cancelFunction();
    }
};
EditArea_ActionTab_ButtonGroup.prototype.buttonStateChanged= function(ev, button)
{
    if(ev.newValue == true)
    {
        for(var k in this.buttons)
        {
            var tmpButton = this.buttons[k];
            if(button != tmpButton)
            {
                tmpButton.set("checked", false);
            }
        }
    }
};
EditArea_ActionTab_ButtonGroup.prototype.addButton= function(button)
{
    this.buttons[button.get("id")] = button;
    button.addListener("checkedChange", this.buttonStateChanged, button, this);
};

EditArea_ActionTab.prototype.show= function()
{
    this.actionTabButton.set("checked", true);
};

EditArea_ActionTab.prototype.hide= function()
{
    this.actionTabButton.set("checked", false);
};
EditArea_ActionTab.prototype.startResize= function(ev)
{
    this.resize.set("maxHeight", document.getElementById("editor").offsetHeight-100);
};
EditArea_ActionTab.prototype.endResize= function(ev)
{
    this.containerDiv.style.width = "100%";
};

EditArea_ActionTab.prototype.resized= function(ev)
{
    var newHeight = ev.height;
    var extraSpace = this.actionTabViewContainer.offsetHeight-this.actionTabViewContainer.clientHeight+3;
    var messageAreaHeight = newHeight-this.headerSize-extraSpace;
    if(messageAreaHeight <= extraSpace){
        messageAreaHeight = 0;
    }
    this.actionTabViewContainer.style.height = ""+messageAreaHeight+"px";
    this.containerDiv.style.width = "100%";
    this.editArea.update_size();
};

EditArea_ActionTab.prototype.buttonStateChanged= function(ev, args)
{
    var isChecked = this.actionTabButton.get("checked");
    if(isChecked == true)
    {
        this.containerDiv.style.display = ''
    }
    else
    {
        this.containerDiv.style.display = 'none'
    }
    this.editArea.update_size();
};

EditArea_ActionTab.prototype.getRenderArea = function()
{
    return this.actionTabViewContainer;
};
EditArea_ActionTab.prototype.checkScrollToBottom = function()
{
    if(this.actionTabViewContainer.scrollTop+this.actionTabViewContainer.offsetHeight+10  >= this.actionTabViewContainer.scrollHeight)
    {
        this.actionTabViewContainer.scrollTop = this.actionTabViewContainer.scrollHeight;
    }
};
EditArea_ActionTab.prototype.init = function(config)
{
    this.containerDiv = document.createElement('div');
    this.containerDiv.id = config.buttonId+"TabMain";
    this.headerDiv = document.createElement('div');
    this.headerDiv.innerHTML = config.title;
    YAHOO.util.Dom.addClass(this.headerDiv, 'actionTabHeader');
    this.actionTabViewContainer = document.createElement('div');
    this.actionTabViewContainer.id = config.buttonId+"TabViewContainer"
    YAHOO.util.Dom.addClass(this.actionTabViewContainer, 'actionTabViewContainer');
    YAHOO.util.Dom.addClass(this.containerDiv, 'actionTabMain');
    this.containerDiv.appendChild(this.headerDiv)
    this.containerDiv.appendChild(this.actionTabViewContainer)
    this.actionTabButton = new YAHOO.widget.Button({
        id: config.buttonId,
        type: "checkbox",
        height:"18px",
        label: "&nbsp;"+config.buttonText,
        container: "actionTabsToolbar"
    });
    this.buttonGroup.addButton(this.actionTabButton);
    this.actionTabButton.addListener("checkedChange", this.buttonStateChanged, this, this);
    this.hide();
    document.getElementById("actionTabContainer").appendChild(this.containerDiv)
    this.headerSize = this.headerDiv.offsetHeight;
    this.resize = new YAHOO.util.Resize(this.containerDiv.id, {handles: ['t'],minWidth: 0,minHeight: 5 });
    this.resize.addListener('resize', this.resized, this, this);
    this.resize.addListener('endResize', this.endResize, this, this);
    this.resize.addListener('startResize', this.startResize, this, this);
    this.resized({height:150});

};
ERROR_MESSAGE_TYPE=0;
NORMAL_MESSAGE_TYPE=1;
// Load as a plugin
editArea.add_plugin('actiontabs', EditArea_actiontabs);
