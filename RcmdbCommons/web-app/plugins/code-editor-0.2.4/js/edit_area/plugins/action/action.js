var EditArea_action= {

	init: function(){
		//	alert("test init: "+ this._someInternalFunction(2, 3));
        editArea.load_css(this.baseURL+"css/action.css");
        editArea.load_css(this.baseURL+"../../../../js/yui/button/assets/skins/sam/button.css");
        editArea.load_css(this.baseURL+"../../../../js/yui/menu/assets/skins/sam/menu.css");
        editArea.load_css(this.baseURL+"../../../../js/yui/button/assets/skins/sam/button.css");
        editArea.load_css(this.baseURL+"../../../../js/yui/container/assets/skins/sam/container.css");
        editArea.load_css(this.baseURL+"../../../../js/yui/datatable/assets/skins/sam/datatable.css");
        editArea.load_script(this.baseURL+"../../../../js/yui/yahoo-dom-event/yahoo-dom-event.js");
        editArea.load_script(this.baseURL+"../../../../js/yui/utilities/utilities.js");
        editArea.load_script(this.baseURL+"../../../../js/yui/datasource/datasource-min.js");
        editArea.load_script(this.baseURL+"../../../../js/yui/datatable/datatable-min.js");
        editArea.load_script(this.baseURL+"../../../../js/yui/container/container-min.js");
        editArea.load_script(this.baseURL+"../../../../js/yui/menu/menu-min.js");
        editArea.load_script(this.baseURL+"../../../../js/yui/element/element-beta-min.js");
        editArea.load_script(this.baseURL+"../../../../js/yui/button/button-min.js");
        editArea.load_script(this.baseURL+"../../../../js/yui/connection/connection-min.js");
        editArea.load_script(this.baseURL+"../../../../js/codeeditor/requester.js");
        editArea.load_script(this.baseURL+"../../../../js/codeeditor/codeeditorutils.js");
        this.GET_ACTION_LIST = 0;
        this.EXECUTE_ACTION = 1;
        this.messageDiv = null;
        this.lastSelectedItem = null;

	},
    get_control_html: function(ctrl_name){
		switch(ctrl_name){
			case "executeAction":
				return '<span id="actionButtonSpan" class="actionButtonSpan"><input type="submit" id="executeActionButton" name="executeActionButton" value="Exec"></span>';

		}
		return false;
	},
    onload: function(){
        this.createMenu();
        this.execButton = new YAHOO.widget.Button("executeActionButton", {
                                        type: "menu",
                                        menu: "executeActionSelect" });
        this.requester = new Requester();
        this.requester.init(this, this.processSuccess, this.processFailure, this.processAbort);
        this.requester.doRequest("codeEditorAction/getActionConfiguration", {}, {action:this.GET_ACTION_LIST})
        this.execButton.getMenu().subscribe("show", this.changeActionVisibility, this, true);
	},

    changeActionVisibility: function()
    {
        var curFile = this.getCurrentFile();
        var as = this.executeActionDiv.childNodes;
        for( var i=0; i<as.length; i++ )
        {
            as[i].style.display = "none";
        }
        if(curFile)
        {
            var filePath = curFile.id
            for( var i=0; i<as.length; i++ )
            {
                var expressions = as[i].filterExpressions;
                for(var j=0; j < expressions.length; j++)
                {
                    try
                    {
                        if(filePath.match(expressions[j]))
                        {
                            as[i].style.display = "";
                        }
                    }catch(e)
                    {
                        var errorStr = "";
                        for(var k in e)
                        {
                            errorStr = errorStr + e[k] + " ";                             
                        }
                        this.setStatusMessage("Exception occurred while processing action filters. "+errorStr, ERROR_MESSAGE_TYPE);
                    }
                }
            }

        }
    },
    processSuccess: function(res)
    {
        var arguments = res.argument;
        if(arguments.action == this.GET_ACTION_LIST)
        {
            var actions = res.responseXML.getElementsByTagName("Action");
            for(var i=0; i<actions.length; i++)
            {
                var action = actions[i];
                var actionDiv = document.createElement("div");
                YAHOO.util.Dom.addClass(actionDiv, "actionMenuItem")
                actionDiv.id = action.getAttribute("name");
                this.executeActionDiv.appendChild(actionDiv);
                actionDiv.innerHTML = action.getAttribute("displayName");
                var filterNodes = null;
                var possibleFiltersNode = action.childNodes;
                for(var j=0; j < possibleFiltersNode.length; j++)
                {
                    if(possibleFiltersNode[j].nodeName == "Filters")
                    {
                        filterNodes = possibleFiltersNode[j].childNodes;
                        break;
                    }
                }
                var filterExpressions = [];
                if(filterNodes)
                {
                    for(var j=0; j < filterNodes.length; j++)
                    {
                        var filterNode = filterNodes[j];
                        if(filterNode.nodeName == "Filter")
                        {

                            filterExpressions[filterExpressions.length] = filterNode.getAttribute("expression");
                        }
                    }
                }
                actionDiv.filterExpressions = filterExpressions;
                actionDiv.actionName = action.getAttribute("name");
                actionDiv.onmouseover = this.menuItemChanged.createDelegate(this, [actionDiv]);
                actionDiv.onclick = this.menuItemClicked.createDelegate(this, [actionDiv]);
            }
        }else if(arguments.action == this.EXECUTE_ACTION)
        {
            var errors = res.responseXML.getElementsByTagName("Error");
            if(errors.length == 0)
            {
                var message = res.responseXML.getElementsByTagName("successful")[0].getAttribute("message");
                this.setStatusMessage(message, NORMAL_MESSAGE_TYPE);
            }
            else
            {
               this.setStatusMessage(errors[0].getAttribute("error"), ERROR_MESSAGE_TYPE);
            }
        }
    },

    setStatusMessage: function(message, type)
    {
        editArea.plugins["actiontabs"].appendMessage(message, type, this.messageDiv);
    },
    processFailure: function(res)
    {

    },
    processAbort: function(lastConnection)
    {
        editArea.plugins["actiontabs"].abortAction("Action "+this.lastExecutedAction + " is aborted", this.messageDiv);
    },
    createMenu: function()
    {
        this.executeActionDiv = document.createElement("div");
        this.executeActionDiv.id="executeActionSelect"
        this.executeActionDiv.name="executeActionSelect"
        document.body.appendChild(this.executeActionDiv);
        YAHOO.util.Dom.addClass(this.executeActionDiv, "yui-overlay");
        YAHOO.util.Dom.addClass(this.executeActionDiv, "actionMenu");
        YAHOO.util.Dom.setStyle(this.executeActionDiv, "z-index", "1000000");
    },

    getCurrentFile: function()
    {
        var file = editArea.get_file(editArea.curr_file);
        if(file.id != "")
        {
            return file;
        }
        return null;
    },

    menuItemClicked: function(args)
    {
        var target = args;
        if(target != null)
        {
            var actionName = target.actionName;
            this.lastExecutedAction = actionName;
            this.execButton.getMenu().hide();
            this.requester.doRequest("codeEditorAction", {actionToBeExecuted:actionName, fileName:this.getCurrentFile().id}, {action:this.EXECUTE_ACTION})
            this.messageDiv = editArea.plugins["actiontabs"].startAction("Excuting "+actionName, this.requester.abort.createDelegate(this.requester));
        }
        return false;
    },
    menuItemChanged: function(args)
    {
        var target = args;
        if(this.lastSelectedItem == null || this.lastSelectedItem.id != target.id)
        {
            var as = this.executeActionDiv.childNodes;
            for( var i=0; i<as.length; i++ )
		    {
                YAHOO.util.Dom.removeClass(as[ i ], "overedMenuItem")
            }
            this.lastSelectedItem = target;
            YAHOO.util.Dom.addClass(this.lastSelectedItem, "overedMenuItem")
        }
    },
    createActionMenuItems: function()
    {

    },
    onkeydown: function(e){

        return true;
	},
    execCommand: function(cmd, param){
        var chainEvent=true;
        switch(cmd){
                case "executeAction":
                     chainEvent = false;
                     break;
        }
		return chainEvent;
	}
};
editArea.add_plugin('action', EditArea_action);
