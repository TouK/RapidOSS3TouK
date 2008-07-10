
YAHOO.rapidjs.Links = [];
YAHOO.rapidjs.Actions = {};
YAHOO.rapidjs.Components = {};


Function.prototype.createCallback = function(/*args...*/){
    // make args available, in function below
    var args = arguments;
    var method = this;
    return function() {
        return method.apply(window, args);
    };
};
Function.prototype.createDelegate = function(obj, args, appendArgs){
    var method = this;
    return function() {
        var callArgs = args || arguments;
        if(appendArgs === true){
            callArgs = Array.prototype.slice.call(arguments, 0);
            callArgs = callArgs.concat(args);
        }else if(typeof appendArgs == 'number'){
            callArgs = Array.prototype.slice.call(arguments, 0); // copy arguments first
            var applyArgs = [appendArgs, 0].concat(args); // create method call params
            Array.prototype.splice.apply(callArgs, applyArgs); // splice them in
        }
        return method.apply(obj || window, callArgs);
    };
};
Function.prototype.defer = function(millis, obj, args, appendArgs){
    return setTimeout(this.createDelegate(obj, args, appendArgs), millis);
};
Function.prototype.createSequence = function(fcn, scope){
    if(typeof fcn != 'function'){
        return this;
    }
    var method = this;
    return function() {
        var retval = method.apply(this || window, arguments);
        fcn.apply(scope || this || window, arguments);
        return retval;
    };
};

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


YAHOO.rapidjs.DelayedTask = function(fn, scope, args){
    var timeoutId = null;

    /**
     * Cancels any pending timeout and queues a new one
     * @param {Number} delay The milliseconds to delay
     * @param {Function} newFn (optional) Overrides function passed to constructor
     * @param {Object} newScope (optional) Overrides scope passed to constructor
     * @param {Array} newArgs (optional) Overrides args passed to constructor
     */
    this.delay = function(delay, newFn, newScope, newArgs){
        if(timeoutId){
            clearTimeout(timeoutId);
        }
        fn = newFn || fn;
        scope = newScope || scope;
        args = newArgs || args;
        timeoutId = setTimeout(fn.createDelegate(scope, args), delay);
    };

    /**
     * Cancel the last queued timeout
     */
    this.cancel = function(){
        if(timeoutId){
            clearTimeout(timeoutId);
            timeoutId = null;
        }
    };
};
