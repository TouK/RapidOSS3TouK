
var EditArea_autocomplete= {

	init: function(){
		//	alert("test init: "+ this._someInternalFunction(2, 3));
        this.shown = false;
        this.waiting = false;
        this.selectIndex = -1;
        this.autoCompleteItems = [];
        this.typedTextAfterAutoComplete = "";
        this.lastConnection = null;
        editArea.load_css(this.baseURL+"../../../../js/edit_area/plugins/autocomplete/css/autocomplete.css");
        editArea.load_script(this.baseURL+"../../../../js/codeeditor/requester.js");
        editArea.load_script(this.baseURL+"../../../../js/codeeditor/codeeditorutils.js");
    }

	,onload: function(){
        this.container	= document.createElement('div');
        this.container.className="autocomplete_suggestion"
        editArea.container.parentNode.insertBefore( this.container, editArea.container.parentNode.firstChild );

		// add event detection for hiding suggestion box
		parent.editAreaLoader.add_event( document, "click", function(){ editArea.plugins['autocomplete']._hide();} );
		parent.editAreaLoader.add_event( editArea.textarea, "blur", function(){ editArea.plugins['autocomplete']._hide();} );
        this._hide();
        this.requester = new Requester();
        this.requester.init(this, this.processSuccess, this.processFailure);
    }
    ,get_control_html: function(ctrl_name){
		return false;
	}

    /**
	 * Is called each time the user touch a keyboard key.
	 *
	 * @param (event) e: the keydown event
	 * @return true - pass to next handler in chain, false - stop chain execution
	 * @type boolean
	 */
	,onkeydown: function(e){
        var willContinueToTrigger = true;
        var letter;
        if (EA_keys[e.keyCode])
			letter=EA_keys[e.keyCode];
		else
			letter=String.fromCharCode(e.keyCode);
        if(this._isShown())
        {
            if(letter=="Esc")
			{
				this._hide();
				willContinueToTrigger = false;
			}
			else if( letter=="Entrer")
			{
                willContinueToTrigger = false;
                var selected	= this.getSelected();
				if( selected != null )
				{
					this._select(selected.start, selected.end, selected.code);
				}
				else
				{
					this._hide();
				}
            }
            else if( letter=="Tab" || letter=="Down")
			{
				this._selectNext();
				return false;
			}
            else if( letter=="Up")
			{
				this._selectPrev();
				return false;
			}
            else if( letter=="Page down")
            {
                this._selectNext();
                return false;
            }
            else if( letter=="Page up")
            {
                this._selectPrev();
                return false;
            }
            else if(!EA_keys[e.keyCode]){
                this.typedTextAfterAutoComplete =this.typedTextAfterAutoComplete+letter;
                this.changeItemVisibility(this.typedTextAfterAutoComplete);
            }
            else if(letter == "Retour arriere")
            {
                if(this.typedTextAfterAutoComplete == "")
                {
                    this._hide();    
                }
                else
                {
                    this.typedTextAfterAutoComplete = this.typedTextAfterAutoComplete.substring(0, this.typedTextAfterAutoComplete.length-1);
                    this.changeItemVisibility(this.typedTextAfterAutoComplete);
                }
            }



        }
        if( letter=="Space" && CtrlPressed(e) )
		{
            this.waiting = true;
            this._show(true);
            var currentFile = editArea.get_file(editArea.curr_file);
            var language = currentFile.syntax;
            this.requester.doRequest("completion", {language:language, text:currentFile.text, offset:currentFile.selection_start, filePath:currentFile.id});
            willContinueToTrigger = false;
        }
        if(this._isShown())
        {
            var cursor	= _$("cursor_pos");
            var top =  cursor.cursor_top + editArea.lineHeight +editArea.container.offsetTop;
            var left =  cursor.cursor_left + 8;
            if(top+this.container.offsetHeight - editArea.container.parentNode.scrollTop > editArea.container.parentNode.offsetHeight)
            {
                top = top - this.container.offsetHeight - editArea.lineHeight -editArea.container.offsetTop;
            }
            if(left+this.container.offsetWidth - editArea.container.parentNode.scrollLeft> editArea.container.parentNode.offsetWidth)
            {
                left = left - this.container.offsetWidth;
            }
            this.container.style.top		= ""+top +"px";
			this.container.style.left		= ""+left +"px";
        }
        return willContinueToTrigger;
    },

    getSelected: function()
    {
        var as	= this.container.getElementsByTagName('A');
        if( this.selectIndex >= 0 && this.selectIndex < as.length )
        {
            return as[ this.selectIndex ];
        }
        return null;
    },
    _selectNext: function(){

        var as	= this.container.getElementsByTagName('TR');
        if(as.length <= 0) return;
		for( var i=0; i<as.length; i++ )
		{
            YAHOO.util.Dom.removeClass(as[ i ], "focus")
        }
        this.selectIndex++;
        for(var i=this.selectIndex;i<as.length; i++)
        {
            if(as[ this.selectIndex].style.display == "none")
            {
		        this.selectIndex++;
            }
            else
            {
                break;
            }
        }
		this.selectIndex	= ( this.selectIndex >= as.length || this.selectIndex < 0 ) ? 0 : this.selectIndex;
		YAHOO.util.Dom.addClass(as[ this.selectIndex ], "focus")
        this.container.scrollTop = as[ this.selectIndex ].offsetTop;
    }
    ,_selectPrev: function(){

        var as	= this.container.getElementsByTagName('TR');
        if(as.length <= 0) return;
		// clean existing elements
		for( var i=0; i<as.length; i++ )
		{
                YAHOO.util.Dom.removeClass(as[ i ], "focus")
		}

		this.selectIndex--;
        for(var i=this.selectIndex;i >= 0; i--)
        {
            if(as[ this.selectIndex].style.display == "none")
            {
		        this.selectIndex--;
            }
            else
            {
                break;
            }
        }
		this.selectIndex	= ( this.selectIndex >= as.length || this.selectIndex < 0 ) ? as.length-1 : this.selectIndex;
        YAHOO.util.Dom.addClass(as[ this.selectIndex ], "focus")
        this.container.scrollTop = as[ this.selectIndex ].offsetTop;
    },
    _select: function( start, end, text )
	{
        var finalSelectionIndex = start + text.length;
		parent.editAreaLoader.setSelectionRange(editArea.id, start , end+this.typedTextAfterAutoComplete.length);
        parent.editAreaLoader.setSelectedText(editArea.id, text );
        parent.editAreaLoader.setSelectionRange(editArea.id, finalSelectionIndex , finalSelectionIndex);
		this._hide();
	}
    ,execCommand: function(cmd, param){
        return true;
	}
    // hide the suggested box
	,_hide: function(){
		this.container.style.display="none";
		this.shown	= false;
	}
	// display the suggested box
    ,_showWaiting: function(){

    }
    ,_hideWaiting: function(){

    }
    ,_show: function(isWaiting){
        if( !this._isShown() )
		{
            this.container.style.width = "200px";
            if(isWaiting)
            {
                this._showWaiting();   
            }
            this.container.scrollTop = 0;
            this.container.innerHTML="";
            this.container.style.display="";
			this.selectIndex	= -1;
			this.shown	= true;
		}
	}
	// is the suggested box displayed?
	,_isShown: function(){
		return this.shown;
	},

    abort: function()
    {
        if(this.lastConnection){
            var callStatus = YAHOO.util.Connect.isCallInProgress(this.lastConnection);
            if(callStatus == true){
                YAHOO.util.Connect.abort(this.lastConnection);
                this.lastConnection = null;
            }
        }
    },
    processSuccess: function (response)
    {
        this._hideWaiting();
        this.renderSuggestions(response.responseXML);
    },
    createAutocompleteItem: function(list, suggestion)
    {
        var trItem = document.createElement("tr")
        var tdGroupItem = document.createElement("td")
        var tdItemDisplay = document.createElement("td")
        var tdItemType = document.createElement("td")
        trItem.appendChild(tdGroupItem);
        trItem.appendChild(tdItemDisplay);
        trItem.appendChild(tdItemType);
        tdGroupItem.setAttribute("width", "0%");
        tdItemType.setAttribute("width", "0%");
        tdItemDisplay.setAttribute("width", "100%");

        var aItem = document.createElement("a")
        aItem.href = "#"

//        aItem.class = "entry"
        YAHOO.util.Dom.addClass(aItem, "entry")
        tdItemDisplay.appendChild(aItem);
        list.appendChild(trItem);
        var code = suggestion.getAttribute("code");
        var type = suggestion.getAttribute("type");
        var group = suggestion.getAttribute("group");
        type= type==null?"":type;
        var displayName= suggestion.getAttribute("displayName");
        var start= suggestion.getAttribute("start")*1;
        var end= suggestion.getAttribute("end")*1;
        aItem.code = code;
        aItem.start = start;
        aItem.end = end;
        
        tdGroupItem.innerHTML = "<img src='"+this.baseURL+"../../../../js/edit_area/images/codecomplete/"+group+".png' width='16' height='16'></img>"
        aItem.innerHTML  = "<span style='white-space:nowrap'>"+displayName+"</span>";
        tdItemType.innerHTML = "<span style='white-space:nowrap'>"+type+"</span>";
        this.autoCompleteItems[code.toLowerCase()] = [trItem, aItem];
    },

    changeItemVisibility: function(typedText)
    {
        var ltypedText = typedText.toLowerCase();
        for(var c in this.autoCompleteItems)
        {
            var item = this.autoCompleteItems[c];
            var tr = item[0];
            var al = item[1];
            var pos = al.end - al.start;

            if(c.substring(pos, pos+ltypedText.length) == ltypedText)
            {
                YAHOO.util.Dom.setStyle(tr, "display", "");
            }
            else
            {
                YAHOO.util.Dom.setStyle(tr, "display", "none");
            }
        }
        var selected = this.getSelected();
        if(selected != null)
        {
            var selectedItem = this.autoCompleteItems[selected.code];
            if(selectedItem == null || selectedItem[0].style.display == "none")
            {
                this.selectIndex = -1;
            }
        }

    },
    renderSuggestions: function(responseXML)
    {
        this.typedTextAfterAutoComplete = "";
        this.autoCompleteItems = [];
        this.container.innerHTML		= '<table width="100%" colspacing="0" cellspacing="0">' +
                                          '<tbody></tbody>' +
                                          '</table>';
        var suggestions = responseXML.getElementsByTagName("Suggestion");
        var lines=[];
        for(var i=0; i<suggestions.length; i++)
        {
            this.createAutocompleteItem(this.container.firstChild.tBodies[0], suggestions[i]);
        }

        //if scroll bar is already has a size which is nearly same as container width we will not resize
        if(this.container.offsetWidth < (this.container.scrollWidth+2))
        {
            this.container.style.width = ""+(this.container.scrollWidth+20)+"px"
        }
        this.changeItemVisibility(this.typedTextAfterAutoComplete);
        this.container.scrollTop = 0;
        this.selectIndex = -1;
        this._selectNext();
    },

    processFailure: function (response)
    {
        this._hideWaiting();
    }
};

// Load as a plugin
//editArea.add_plugin('autocomplete', EditArea_autocomplete);
