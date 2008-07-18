YAHOO.namespace('rapidjs');
YAHOO.rapidjs.Components = {};
String.prototype.trim = function() {
	a = this.replace(/^\s+/, '');
	return a.replace(/\s+$/, '');
};
YAHOO.rapidjs.ArrayUtils = new function()
{
	this.remove = function(arrayP, index)
	{
		if(index >= arrayP.length || index < 0) return;
		var lastIndex = arrayP.length - 1;
		arrayP[index] = arrayP[lastIndex];
		arrayP.splice(lastIndex, 1);
	};
}();

YAHOO.rapidjs.Connect = new function()
{
	this.containsError = function(response)
	{
		try
		{
			if(response.responseXML)
			{
				var errors = response.responseXML.getElementsByTagName("Errors");
				if(!errors)
				{
					return false;
				}
				if(errors.length != null)
				{
					return errors.length > 0;
				}
				else
				{
					return errors != null;
				}
			}
			else
			{
				//if response.responseXML is undefined, than we have no errors since RI sends errors in XML.
				return false;
			}
		}
		catch(e)
		{
			return response.responseText.indexOf("Errors") >= 0
		}

	};

	this.getErrorMessages = function(xmlDoc){
		return xmlDoc.getElementsByTagName('Error');
	};
	this.getSuccessMessage = function(xmlDoc){
		var success = xmlDoc.getElementsByTagName('Successful');
		if(success && success.length > 0 && success[0].firstChild)
		{
			return success[0].firstChild.nodeValue;
		}
		else
		{
			return "";
		}
	};

    this.checkAuthentication = function(xmlDoc){
		var authenticate = xmlDoc.responseXML.getElementsByTagName('Authenticate');
		if(authenticate && authenticate.length > 0)
		{
            window.location = "/auth/login?targetUri="+window.location;
            return false;
        }
		else
		{
			return true;
		}
	};
}();

YAHOO.rapidjs.DomUtils = new function()
{
	this.findParent = function(element, className){
		element = element.parentNode;
		while(element)
		{
			if(YAHOO.util.Dom.hasClass(element, className))
			{
				return element;
			}
			element = element.parentNode;
		}
		return null;
	};
	this.findChild = function(element, className){
		var childNodes = element.childNodes;
		for(var index=0; index<childNodes.length; index++) {
			if(YAHOO.util.Dom.hasClass(childNodes[index], className))
			{
				return childNodes[index];
			}
		}
		return null;
	};
	this.getElementFromChild = function(childEl, parentClass){
        if(!childEl || (YAHOO.util.Dom.hasClass(childEl, parentClass))){
		    return childEl;
	    }
	    var p = childEl.parentNode;
	    var b = document.body;
	    while(p && p != b){
            if(YAHOO.util.Dom.hasClass(p, parentClass)){
            	return p;
            }
            p = p.parentNode;
        }
	    return null;
    };
}();

YAHOO.rapidjs.CursorManager = new function(){
	this.processes = new Array();
	this.idle = function(){
		this.processes.pop();
		if(this.processes.length == 0){
			document.body.style.cursor = '';
		}
	};
	this.busy = function(){
		this.processes.push('');
		document.body.style.cursor = 'wait';
	};
}();

