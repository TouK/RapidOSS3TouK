var EditArea_fileoperations= {

	init: function(){
		//	alert("test init: "+ this._someInternalFunction(2, 3));
        editArea.load_css(this.baseURL+"css/fileoperations.css");
        editArea.load_css(this.baseURL+"../../../../js/yui/button/assets/skins/sam/button.css");
        editArea.load_css(this.baseURL+"../../../../js/yui/button/assets/skins/sam/button.css");
        editArea.load_css(this.baseURL+"../../../../js/yui/container/assets/skins/sam/container.css");
        editArea.load_css(this.baseURL+"../../../../js/yui/datatable/assets/skins/sam/datatable.css");
        editArea.load_script(this.baseURL+"../../../../js/yui/yahoo-dom-event/yahoo-dom-event.js");
        editArea.load_script(this.baseURL+"../../../../js/yui/utilities/utilities.js");
        editArea.load_script(this.baseURL+"../../../../js/yui/element/element-beta-min.js");
        editArea.load_script(this.baseURL+"../../../../js/yui/button/button-min.js");
        editArea.load_script(this.baseURL+"../../../../js/yui/datasource/datasource-min.js");
        editArea.load_script(this.baseURL+"../../../../js/yui/datatable/datatable-min.js");
        editArea.load_script(this.baseURL+"../../../../js/yui/container/container-min.js");
        editArea.load_script(this.baseURL+"../../../../js/yui/connection/connection-min.js");
        editArea.load_script(this.baseURL+"../../../../js/codeeditor/requester.js");
        editArea.load_script(this.baseURL+"../../../../js/codeeditor/codeeditorutils.js");
        this.FILELIST_SAVE_MODE = 0;
        this.FILELIST_OPEN_MODE = 1;
        this.ACTION_SAVE = 0;
        this.ACTION_OPEN = 1;
        this.ACTION_FILE_LIST_FOR_OPEN = 2;
        this.ACTION_FILE_LIST_FOR_SAVE = 3;
        this.ACTION_DELETE = 4;
        this.selectedFileAttributes = {};
        this.isCommandContinueToExecute = false;
        this.newFileId = 1;
        this.buttonStates = {};
        this.openedFiles = {};
	},
    get_control_html: function(ctrl_name){
		switch(ctrl_name){
			case "openfile":
				return parent.editAreaLoader.get_button_html('openfile', 'load.gif', 'openfile', false, this.baseURL);
			case "deletefile":
				return parent.editAreaLoader.get_button_html('deletefile', 'delete.png', 'deletefile', false, this.baseURL);
            case "savefile":
				return parent.editAreaLoader.get_button_html('savefile', 'save.gif', 'savefile', false, this.baseURL);
            case "newfile":
				return parent.editAreaLoader.get_button_html('newfile', 'newdocument.gif', 'newfile', false, this.baseURL);
		}
		return false;
	},
    onload: function(){
        parent.confirmCloseFile = this.confirmCloseFile.createDelegate(this);
        editArea.settings["EA_file_close_callback"] = "confirmCloseFile";
        this.requester = new Requester();
        this.requester.init(this, this.processSuccess, this.processFailure)
        this.initializeFileListDialog();
        this.initializeLoadingDialog();
        YAHOO.util.Dom.addClass(document.body, "yui-skin-sam");
        this.setToolbarButtonState("savefile", false);
        this.setToolbarButtonState("deletefile", false);
        editArea.open_file({id:"temp"});
        editArea.close_file("temp");
	},
    onkeydown: function(e){
        var letter;
        if (EA_keys[e.keyCode])
			letter=EA_keys[e.keyCode];
		else
			letter=String.fromCharCode(e.keyCode);
        if( (letter=="S" || letter=="s") && CtrlPressed(e) )
		{
            this.execCommand("savefile", null);
            return false;
        }
        else if( letter=="F4" && CtrlPressed(e) && ShiftPressed(e) || letter=="Q" && CtrlPressed(e))
		{
            this.closeCurrentFile();
            return false;
        }
        return true;
	},
    execCommand: function(cmd, param){
		// Handle commands
        if(!this.isToolbarButtonEnabled(cmd)){
            return true;
        }
        if(cmd == "file_open")
        {
            this.openedFiles[param.id] = param.id;
            this.setToolbarButtonState("savefile", true);
            this.setToolbarButtonState("deletefile", true);
            return true;
        }
        else if(cmd == "file_close")
        {
            this.setToolbarButtonState("savefile", false);
            this.setToolbarButtonState("deletefile", false);
            return true;
        }
        else if(cmd == "file_close")
        {
            this.setToolbarButtonState("savefile", false);
            this.setToolbarButtonState("deletefile", false);
            return true;
        }
        else if(cmd == "file_switch_off")
        {
            this.setToolbarButtonState("savefile", false);
            this.setToolbarButtonState("deletefile", false);
            return true;
        }
        else if(cmd == "file_switch_on")
        {
            if(editArea.curr_file != "")
            {
                this.setToolbarButtonState("savefile", true);
                this.setToolbarButtonState("deletefile", true);
            }
            return true;
        }
        var chainEvent = true;
        if(!this.isCommandContinueToExecute)
        {
            this.isCommandContinueToExecute = true;
            switch(cmd){
                case "openfile":
                     this.showLoading();
                     this.requester.doRequest("code/listFiles", {file:this.lastProcessedDir}, {action:this.ACTION_FILE_LIST_FOR_OPEN});
                     chainEvent = false;
                     break;
                case "deletefile":
                     var currentFile = editArea.get_file(editArea.curr_file);
                     var res = confirm("Are you sure to delete file "+ currentFile.id + "?");
                     if(res)
                     {
                        this.showLoading();
                        this.requester.doRequest("code/delete", {file:currentFile.id}, {action:this.ACTION_DELETE, file:currentFile.id});
                     }
                     chainEvent = false;
                     break;
                case "savefile":
                      this.showLoading();
                      var currentFile = editArea.get_file(editArea.curr_file);
                      if(this.isLoadedFromServerSide(currentFile.id))
                      {
                        this.requester.doRequest("code/save", {file:currentFile.id, fileContent:currentFile.text}, {action:this.ACTION_SAVE}) ;
                      }
                      else
                      {
                        this.requester.doRequest("code/listFiles", {file:this.lastProcessedDir}, {action:this.ACTION_FILE_LIST_FOR_SAVE});
                      }
                      chainEvent = false;
                      break;
                case "newfile":
                    this.createNewFile();
                    chainEvent = false;
                    break;
            }
            this.isCommandContinueToExecute = false;
            // Pass to next handler in chain
        }
		return chainEvent;
	},

    setToolbarButtonState: function(buttonName, isEnabled)
    {
        if(isEnabled)
        {
            editArea.switchClassSticky(_$(buttonName), "editAreaButtonNormal", false);
        }
        else
        {
            editArea.switchClassSticky(_$(buttonName), "editAreaButtonDisabled", true);
        }
        this.buttonStates[buttonName] = isEnabled;
    },

    isToolbarButtonEnabled: function(buttonName)
    {
        return this.buttonStates[buttonName] == true || this.buttonStates[buttonName] == null;
    },

    isLoadedFromServerSide: function(fileName)
    {
        var file = editArea.get_file(fileName);
        return file.isLoadedFromServer == "true";
    },

    getTextContent: function(el)
    {
        // Use textContent if supported
        if (typeof el.textConent == 'string') return el.textContent;

        // Otherwise, recurse down the childNodes & siblings
        var cNode, cNodes = el.childNodes;
        var txt = '';
        for (var i=0, len=cNodes.length; i<len; ++i){
            cNode = cNodes[i];
            if (1 == cNode.nodeType) {
                txt += getTextContent(cNode);
            }
            if (3 == cNode.nodeType){
                txt += cNode.data;
            }
        }
        return txt;
    },

    setLoadedFromServerSide: function(fileName)
    {
        var file = editArea.get_file(fileName);
        file.isLoadedFromServer = "true";
    },

    confirmCloseFile: function(e)
    {
        var res = true;
        if(e.edited)
        {
            res = confirm("The content of file "+ e.id + " is changed. Would you like to close without saving?");
        }
        if(res)
        {
            this.openedFiles[e.id] = null;
        }
        return res;
    },

    openFile: function(filePath, fileContent, fileType, title)
    {
        var newFile = {id:filePath, text:fileContent, syntax:fileType, title:title};
        this.openFileWithSettings(newFile);
    },

    closeCurrentFile: function()
    {
        if(editArea.curr_file != null)
        {
            var currentFile = editArea.get_file(editArea.curr_file);
            if(currentFile != null)
            {
                this.closeFile(currentFile.id);
            }
        }

    },
    closeFile: function(fileId)
    {
        if(fileId != "")
        {
            editArea.close_file(fileId);
        }
    },

    openFileWithSettings: function(settings)
    {
        editArea.open_file(settings);
        YAHOO.util.Dom.addClass(document.body, "yui-skin-sam");
    },
    getFileCount: function()
    {
        var count = 0;
        for(var i in editArea.get_all_files())
        {
            if(this.openedFiles[i] != null)
            count++;
        }
        return count;
    },

    processSuccess: function(res)
    {
        var arguments = res.argument;
        var responseXml = res.responseXML;
        var errors = res.responseXML.getElementsByTagName("Error");
        if(errors.length != 0)
        {
            var message = errors[0].getAttribute("error");
            editArea.plugins["actiontabs"].appendMessage(message, ERROR_MESSAGE_TYPE);
        }
        else
        {
            if(arguments.action == this.ACTION_OPEN)
            {
                var filePath = res.responseXML.firstChild.getAttribute("file");
                var fileContent = "";
                if(res.responseXML.firstChild.firstChild != null)
                {
                    fileContent = this.getTextContent(res.responseXML.firstChild);
                }
                var fileType = res.responseXML.firstChild.getAttribute("type");
                this.openFile(filePath, fileContent, fileType, filePath);
                this.setLoadedFromServerSide(editArea.curr_file);
                this.hideFileList();
            }
            else if(arguments.action == this.ACTION_SAVE)
            {
                editArea.set_file_edited_mode(editArea.curr_file, false);
                this.hideFileList();
                if(res.argument.newFileName != null)
                {
                    var file = res.argument.file;
                    editArea.close_file(file.id);
                    file.id = res.argument.newFileName;
                    file.title = res.argument.newFileName;
                    this.openFileWithSettings(file);
                    this.setLoadedFromServerSide(file.id);
                }
            }
            else if(arguments.action == this.ACTION_DELETE)
            {
                var file = res.argument.file;
                editArea.close_file(file);
            }
            else if(arguments.action == this.ACTION_FILE_LIST_FOR_OPEN)
            {
                this.loadFilesToList(res);
                this.showFileList(this.FILELIST_OPEN_MODE);
            }
            else if(arguments.action == this.ACTION_FILE_LIST_FOR_SAVE)
            {
                this.loadFilesToList(res);
                this.showFileList(this.FILELIST_SAVE_MODE);
            }
        }
        this.hideLoading();
    },

    processFailure: function(res)
    {
        this.hideLoading();
    },
    createNewFile: function()
    {
        var filePath = "untitled"+(this.newFileId++)+".groovy";
        var fileContent = "";
        var fileType = "groovy";
        this.openFile(filePath, fileContent, fileType, filePath);
    },
    selectFile: function (e)
    {
        var target = YAHOO.util.Event.getTarget(e)
        if(YAHOO.util.Dom.hasClass(target, "fileDetails") || YAHOO.util.Dom.hasClass(target, "fileDetailsDir") )
        {
            this.selectedFileAttributes.file = target.id;
            this.selectedFileAttributes.isDir = target.isDir;
            this.selectedFileAttributes.type = target.type;
            var prevSelEl = YAHOO.util.Dom.getElementsByClassName("fileDetailsSelected", "div", this.loadFileList);
            if(prevSelEl.length > 0)
            {
                YAHOO.util.Dom.removeClass(prevSelEl[0], "fileDetailsSelected")
            }
            YAHOO.util.Dom.addClass(target, "fileDetailsSelected")
            document.getElementById("selectedFileInput").value = target.id;
            this.saveButton.setAttributes({"disabled":false});
            this.openButton.setAttributes({"disabled":false});
        }
    },
    loadFilesToList: function(response)
    {
        this.loadFileList.innerHTML = "";
        this.rootDir = response.responseXML.firstChild.getAttribute("rootDir");
        var files = response.responseXML.getElementsByTagName("File");

        var fileConfig = [{file:this.rootDir, type:"", isDir:true, displayName:"."},{file:this.rootDir+"/..", type:"", isDir:true, displayName:".."}];
        for(var i=0; i < files.length; i++)
        {
            var id = files[i].getAttribute("file");
            var fileType = files[i].getAttribute("type");
            var isDir = files[i].getAttribute("isDir") == "true";
            fileConfig[fileConfig.length] = {file:id, type:fileType, isDir:isDir, displayName:files[i].getAttribute("displayName")};
        }
        for(var i=0; i < fileConfig.length; i++)
        {
            var fdiv = document.createElement("div");

            fdiv.id = fileConfig[i].file;
            fdiv.fileType = fileConfig[i].type;
            fdiv.isDir = fileConfig[i].isDir;
            if(fdiv.isDir)
            {
                fdiv.className = "fileDetailsDir";
            }
            else
            {
                fdiv.className = "fileDetails";
            }
            fdiv.innerHTML = fileConfig[i].displayName;
            this.loadFileList.appendChild(fdiv);
        }
    },
    initializeFileListDialog: function () {
            this.lastProcessedDir = ".";
            this.fileListMode = this.FILELIST_OPEN_MODE;
            var dialogDiv = document.createElement("div")
            dialogDiv.id = "fileListDialog"
            document.body.appendChild(dialogDiv);
            this.fileListDialog = new YAHOO.widget.SimpleDialog("fileListDialog",
                         { fixedcenter: true,
                           visible: false,
                           draggable: true,
                           close: true,
                           modal:true,
                           zindex:10000,
                           constraintoviewport: true,
                           buttons: [ { text:"Open", isDefault:true },
                                      { text:"Save", isDefault:true },
                                      { text:"Cancel", scope:this } ]
                         } );
            this.fileListDialog.render(document.body);
            this.fileListDialog.hide();
            this.loadFileList = document.createElement("div");
            YAHOO.util.Event.addListener(this.loadFileList, "click", this.fileListClick, this, true)
            YAHOO.util.Event.addListener(this.loadFileList, "dblclick", this.fileListDoubleClick, this, true)
            this.loadFileList.className="fileList"
            this.selectedFileDiv = document.createElement("div");
            this.selectedFileDiv.innerHTML="<table><tr><td width='0%'><span style='white-space:nowrap'>File Name:</span></td><td width='100%'><input id='selectedFileInput' style='width:100%'></input></td></tr></table>"
            this.fileListDialog.appendToBody(this.loadFileList);
            this.fileListDialog.appendToBody(this.selectedFileDiv);
            this.selectedFileDiv.style.width = "400px"
            this.openButton = this.fileListDialog.getButtons()[0];
            this.saveButton = this.fileListDialog.getButtons()[1];
            this.cancelButton = this.fileListDialog.getButtons()[2];
            this.openButton.addListener("click", this.fileListOpen, this, true)
            this.saveButton.addListener("click", this.fileListSave, this, true)
            this.cancelButton.addListener("click", this.fileListCancel, this, true)
            this.fileNameInput = document.getElementById("selectedFileInput");

            YAHOO.util.Event.addListener(this.fileNameInput, "keyup", function(){
                if(this.fileNameInput.value != "")
                {
                    this.saveButton.setAttributes({"disabled":false});
                    this.openButton.setAttributes({"disabled":false});
                }else
                {
                    this.saveButton.setAttributes({"disabled":true});
                    this.openButton.setAttributes({"disabled":true});
                }
            }, this, true)
     },

    initializeLoadingDialog: function()
    {
// Initialize the temporary Panel to display while waiting for external content to load
        this.loadingDialog =
                new YAHOO.widget.Panel("wait",
                    { width:"240px",
                      fixedcenter:true,
                      close:false,
                      draggable:false,
                      zindex:10001,
                      modal:true,
                      visible:false
                    }
                );

        this.loadingDialog.setHeader("Loading, please wait...");
        this.loadingDialog.setBody('<img src="http://us.i1.yimg.com/us.yimg.com/i/us/per/gr/gp/rel_interstitial_loading.gif" />');
        this.loadingDialog.render(document.body);

    },

     showFileList: function(mode)
     {
            this.fileListDialog.setHeader(mode==this.FILELIST_OPEN_MODE?"Open File":"Save File");
            this.loadFileList.style.display = ""
            this.selectedFileDiv.style.display = ""
            this.openButton.setAttributes({"disabled":true});
            this.saveButton.setAttributes({"disabled":true});
            this.openButton.setStyle("display",mode==this.FILELIST_OPEN_MODE?"":"none");
            this.saveButton.setStyle("display",mode==this.FILELIST_SAVE_MODE?"":"none");
            this.fileNameInput.value = this.rootDir?this.rootDir+"/":"";
            this.fileListDialog.show();
            this.fileListMode = mode;
     },

    showLoading: function()
    {
        this.loadingDialog.show();
    },
    hideLoading: function()
    {
        this.loadingDialog.hide();
    },

     hideFileList: function()
     {
        this.fileListDialog.hide();      
     },
     fileListDoubleClick: function(e)
     {
        this.selectFile(e);
        if(this.fileListMode == this.FILELIST_OPEN_MODE)
        {
            this.fileListOpen();
        }
        else
        {
            this.fileListSave();    
        }
     },
     fileListClick: function(e)
     {
        this.selectFile(e); 
     },
     fileListOpen: function()
     {
        if(this.selectedFileAttributes.isDir)
        {
            this.lastProcessedDir = this.selectedFileAttributes.file;
            this.showLoading();
            this.requester.doRequest("code/listFiles", {file:this.lastProcessedDir}, {action:this.ACTION_FILE_LIST_FOR_OPEN});
        }
        else
        {
            this.requester.doRequest("code/view", {file:document.getElementById("selectedFileInput").value}, {action:this.ACTION_OPEN}) ;
        }
     },
     fileListSave: function()
     {
         if(this.selectedFileAttributes.file != this.fileNameInput.value)
         {
            this.showLoading();
            var file = editArea.get_file(editArea.curr_file);
            var fileName = this.fileNameInput.value;
            this.requester.doRequest("code/save", {file:fileName, fileContent:file.text}, {action:this.ACTION_SAVE, newFileName:fileName, file:file}) ;
         }
        else if(this.selectedFileAttributes.isDir)
        {
            this.lastProcessedDir = this.selectedFileAttributes.file;
            this.showLoading();
            this.requester.doRequest("code/listFiles", {file:this.lastProcessedDir}, {action:this.ACTION_FILE_LIST_FOR_SAVE});
        }
        else
        {
            this.showLoading();
            var file = editArea.get_file(editArea.curr_file);
            var fileName = this.fileNameInput.value;
            this.requester.doRequest("code/save", {file:fileName, fileContent:file.text}, {action:this.ACTION_SAVE, newFileName:fileName, file:file}) ;
        }
     },
     fileListCancel: function()
     {
        this.hideFileList();
     }

};
editArea.add_plugin('fileoperations', EditArea_fileoperations);
