/*
 * yui-ext 0.33
 * Copyright(c) 2006, Jack Slocum.
 */

YAHOO.namespace('ext', 'ext.util', 'ext.grid');
YAHOO.ext.Strict = (document.compatMode == 'CSS1Compat');
YAHOO.ext.SSL_SECURE_URL = 'javascript:false';
// for old browsers
window.undefined = undefined;
/*
 * yui-ext 0.33
 * Copyright(c) 2006, Jack Slocum.
 */


/**
 * @class YAHOO.ext.EventManager
 * Registers event handlers that want to receive a normalized EventObject instead of the standard browser event and provides
 * several useful events directly.
 * See {@link YAHOO.ext.EventObject} for more details on normalized event objects.
 * @singleton
 */
YAHOO.ext.EventManager = new function(){
    var docReadyEvent;
    var docReadyProcId;
    var docReadyState = false;
    this.ieDeferSrc = false;
    var resizeEvent;
    var resizeTask;

    var fireDocReady = function(){
        if(!docReadyState){
            docReadyState = true;
            if(docReadyProcId){
                clearInterval(docReadyProcId);
            }
            if(docReadyEvent){
                docReadyEvent.fire();
            }
        }
    };

    var initDocReady = function(){
        docReadyEvent = new YAHOO.util.CustomEvent('documentready');
        if(document.addEventListener) {
            YAHOO.util.Event.on(document, "DOMContentLoaded", fireDocReady);
        }else if(YAHOO.ext.util.Browser.isIE){
            // inspired by  http://www.thefutureoftheweb.com/blog/2006/6/adddomloadevent
            document.write('<s'+'cript id="ie-deferred-loader" defer="defer" src="' +
                        (YAHOO.ext.EventManager.ieDeferSrc || YAHOO.ext.SSL_SECURE_URL) + '"></s'+'cript>');
            YAHOO.util.Event.on('ie-deferred-loader', 'readystatechange', function(){
                if(this.readyState == 'complete'){
                    fireDocReady();
                }
            });
        }else if(YAHOO.ext.util.Browser.isSafari){
            docReadyProcId = setInterval(function(){
                var rs = document.readyState;
                if(rs == 'loaded' || rs == 'complete') {
                    fireDocReady();
                 }
            }, 10);
        }
        // no matter what, make sure it fires on load
        YAHOO.util.Event.on(window, 'load', fireDocReady);
    };
    /**
     * Places a simple wrapper around an event handler to override the browser event
     * object with a YAHOO.ext.EventObject
     * @param {Function} fn        The method the event invokes
     * @param {Object}   scope    An object that becomes the scope of the handler
     * @param {boolean}  override If true, the obj passed in becomes
     *                             the execution scope of the listener
     * @return {Function} The wrapped function
     */
    this.wrap = function(fn, scope, override){
        var wrappedFn = function(e){
            YAHOO.ext.EventObject.setEvent(e);
            fn.call(override ? scope || window : window, YAHOO.ext.EventObject, scope);
        };
        return wrappedFn;
    };

    /**
     * Appends an event handler
     *
     * @param {Object}   element        The html element to assign the
     *                             event to
     * @param {String}   eventName     The type of event to append
     * @param {Function} fn        The method the event invokes
     * @param {Object}   scope    An object that becomes the scope of the handler
     * @param {boolean}  override If true, the obj passed in becomes
     *                             the execution scope of the listener
     * @return {Function} The wrapper function created (to be used to remove the listener if necessary)
     */
    this.addListener = function(element, eventName, fn, scope, override){
        var wrappedFn = this.wrap(fn, scope, override);
        YAHOO.util.Event.addListener(element, eventName, wrappedFn);
        return wrappedFn;
    };

    /**
     * Removes an event handler
     *
     * @param {Object}   element        The html element to remove the
     *                             event from
     * @param {String}   eventName     The type of event to append
     * @param {Function} wrappedFn        The wrapper method returned when adding the listener
     * @return {Boolean} True if a listener was actually removed
     */
    this.removeListener = function(element, eventName, wrappedFn){
        return YAHOO.util.Event.removeListener(element, eventName, wrappedFn);
    };

    /**
     * Appends an event handler (shorthand for addListener)
     *
     * @param {Object}   element        The html element to assign the
     *                             event to
     * @param {String}   eventName     The type of event to append
     * @param {Function} fn        The method the event invokes
     * @param {Object}   scope    An arbitrary object that will be
     *                             passed as a parameter to the handler
     * @param {boolean}  override If true, the obj passed in becomes
     *                             the execution scope of the listener
     * @return {Function} The wrapper function created (to be used to remove the listener if necessary)
     * @method
     */
    this.on = this.addListener;

    /**
     * Fires when the document is ready (before onload and before images are loaded)
     * @param {Function} fn        The method the event invokes
     * @param {Object}   scope    An  object that becomes the scope of the handler
     * @param {boolean}  override If true, the obj passed in becomes
     *                             the execution scope of the listener
     */
    this.onDocumentReady = function(fn, scope, override){
        if(docReadyState){ // if it already fired
            fn.call(override? scope || window : window, scope);
            return;
        }
        if(!docReadyEvent){
            initDocReady();
        }
        docReadyEvent.subscribe(fn, scope, override);
    }

    /**
     * Fires when the window is resized and provides resize event buffering (50 milliseconds), passes new viewport width and height to handlers.
     * @param {Function} fn        The method the event invokes
     * @param {Object}   scope    An object that becomes the scope of the handler
     * @param {boolean}  override If true, the obj passed in becomes
     *                             the execution scope of the listener
     */
    this.onWindowResize = function(fn, scope, override){
        if(!resizeEvent){
            resizeEvent = new YAHOO.util.CustomEvent('windowresize');
            resizeTask = new YAHOO.ext.util.DelayedTask(function(){
                resizeEvent.fireDirect(YAHOO.util.Dom.getViewportWidth(), YAHOO.util.Dom.getViewportHeight());
            });
            YAHOO.util.Event.on(window, 'resize', function(){
                resizeTask.delay(50);
            });
        }
        resizeEvent.subscribe(fn, scope, override);
    },

    /**
     * Removes the passed window resize listener.
     * @param {Function} fn        The method the event invokes
     * @param {Object}   scope    The scope of handler
     */
    this.removeResizeListener = function(fn, scope){
        if(resizeEvent){
            resizeEvent.unsubscribe(fn, scope);
        }
    }
};

/**
 * @class YAHOO.ext.EventObject
 * EventObject exposes the Yahoo! UI Event functionality directly on the object
 * passed to your event handler. It exists mostly for convenience. It also fixes the annoying null checks automatically to cleanup your code
 * (All the YAHOO.util.Event methods throw javascript errors if the passed event is null).
 * To get an EventObject instead of the standard browser event,
 * your must register your listener thru the {@link YAHOO.ext.EventManager} or directly on an Element
 * with {@link YAHOO.ext.Element#addManagedListener} or the shorthanded equivalent {@link YAHOO.ext.Element#mon}.<br>
 * Example:
 * <pre><code>
 fu<>nction handleClick(e){ // e is not a standard event object, it is a YAHOO.ext.EventObject
    e.preventDefault();
    var target = e.getTarget();
    ...
 }
 var myDiv = getEl('myDiv');
 myDiv.mon('click', handleClick);
 //or
 YAHOO.ext.EventManager.on('myDiv', 'click', handleClick);
 YAHOO.ext.EventManager.addListener('myDiv', 'click', handleClick);
 </code></pre>
 * @singleton
 */
YAHOO.ext.EventObject = new function(){
    /** The normal browser event */
    this.browserEvent = null;
    /** The button pressed in a mouse event */
    this.button = -1;
    /** True if the shift key was down during the event */
    this.shiftKey = false;
    /** True if the control key was down during the event */
    this.ctrlKey = false;
    /** True if the alt key was down during the event */
    this.altKey = false;

    /** Key constant @type Number */
    this.BACKSPACE = 8;
    /** Key constant @type Number */
    this.TAB = 9;
    /** Key constant @type Number */
    this.RETURN = 13;
    /** Key constant @type Number */
    this.ESC = 27;
    /** Key constant @type Number */
    this.SPACE = 32;
    /** Key constant @type Number */
    this.PAGEUP = 33;
    /** Key constant @type Number */
    this.PAGEDOWN = 34;
    /** Key constant @type Number */
    this.END = 35;
    /** Key constant @type Number */
    this.HOME = 36;
    /** Key constant @type Number */
    this.LEFT = 37;
    /** Key constant @type Number */
    this.UP = 38;
    /** Key constant @type Number */
    this.RIGHT = 39;
    /** Key constant @type Number */
    this.DOWN = 40;
    /** Key constant @type Number */
    this.DELETE = 46;
    /** Key constant @type Number */
    this.F5 = 116;

       /** @private */
    this.setEvent = function(e){
        if(e == this){ // already wrapped
            return this;
        }
        this.browserEvent = e;
        if(e){
            this.button = e.button;
            this.shiftKey = e.shiftKey;
            this.ctrlKey = e.ctrlKey;
            this.altKey = e.altKey;
        }else{
            this.button = -1;
            this.shiftKey = false;
            this.ctrlKey = false;
            this.altKey = false;
        }
        return this;
    };

    /**
     * Stop the event. Calls YAHOO.util.Event.stopEvent() if the event is not null.
     */
    this.stopEvent = function(){
        if(this.browserEvent){
            YAHOO.util.Event.stopEvent(this.browserEvent);
        }
    };

    /**
     * Prevents the browsers default handling of the event. Calls YAHOO.util.Event.preventDefault() if the event is not null.
     */
    this.preventDefault = function(){
        if(this.browserEvent){
            YAHOO.util.Event.preventDefault(this.browserEvent);
        }
    };

    /** @private */
    this.isNavKeyPress = function(){
        return (this.browserEvent.keyCode && this.browserEvent.keyCode >= 33 && this.browserEvent.keyCode <= 40);
    };

    /**
     * Cancels bubbling of the event. Calls YAHOO.util.Event.stopPropagation() if the event is not null.
     */
    this.stopPropagation = function(){
        if(this.browserEvent){
            YAHOO.util.Event.stopPropagation(this.browserEvent);
        }
    };

    /**
     * Gets the key code for the event. Returns value from YAHOO.util.Event.getCharCode() if the event is not null.
     * @return {Number}
     */
    this.getCharCode = function(){
        if(this.browserEvent){
            return YAHOO.util.Event.getCharCode(this.browserEvent);
        }
        return null;
    };

    /**
     * Returns a browsers key for a keydown event
     * @return {Number} The key code
     */
    this.getKey = function(){
        if(this.browserEvent){
            return this.browserEvent.keyCode || this.browserEvent.charCode;
        }
        return null;
    };

    /**
     * Gets the x coordinate of the event. Returns value from YAHOO.util.Event.getPageX() if the event is not null.
     * @return {Number}
     */
    this.getPageX = function(){
        if(this.browserEvent){
            return YAHOO.util.Event.getPageX(this.browserEvent);
        }
        return null;
    };

    /**
     * Gets the y coordinate of the event. Returns value from YAHOO.util.Event.getPageY() if the event is not null.
     * @return {Number}
     */
    this.getPageY = function(){
        if(this.browserEvent){
            return YAHOO.util.Event.getPageY(this.browserEvent);
        }
        return null;
    };

    /**
     * Gets the time of the event. Returns value from YAHOO.util.Event.getTime() if the event is not null.
     * @return {Number}
     */
    this.getTime = function(){
        if(this.browserEvent){
            return YAHOO.util.Event.getTime(this.browserEvent);
        }
        return null;
    };

    /**
     * Gets the page coordinates of the event. Returns value from YAHOO.util.Event.getXY() if the event is not null.
     * @return {Array} The xy values like [x, y]
     */
    this.getXY = function(){
        if(this.browserEvent){
            return YAHOO.util.Event.getXY(this.browserEvent);
        }
        return [];
    };

    /**
     * Gets the target for the event. Returns value from YAHOO.util.Event.getTarget() if the event is not null.
     * @return {HTMLelement}
     */
    this.getTarget = function(){
        if(this.browserEvent){
            return YAHOO.util.Event.getTarget(this.browserEvent);
        }
        return null;
    };

    /**
     * Walk up the DOM looking for a particular target - if the default target matches, it is returned.
     * @param {String} className The class name to look for or null
     * @param {String} tagName (optional) The tag name to look for
     * @return {HTMLelement}
     */
    this.findTarget = function(className, tagName){
        if(tagName) tagName = tagName.toLowerCase();
        if(this.browserEvent){
            function isMatch(el){
               if(!el){
                   return false;
               }
               if(className && !YAHOO.util.Dom.hasClass(el, className)){
                   return false;
               }
               if(tagName && el.tagName.toLowerCase() != tagName){
                   return false;
               }
               return true;
            };

            var t = this.getTarget();
            if(!t || isMatch(t)){
    		    return t;
    	    }
    	    var p = t.parentNode;
    	    var b = document.body;
    	    while(p && p != b){
                if(isMatch(p)){
                	return p;
                }
                p = p.parentNode;
            }
    	}
        return null;
    };
    /**
     * Gets the related target. Returns value from YAHOO.util.Event.getRelatedTarget() if the event is not null.
     * @return {HTMLElement}
     */
    this.getRelatedTarget = function(){
        if(this.browserEvent){
            return YAHOO.util.Event.getRelatedTarget(this.browserEvent);
        }
        return null;
    };

    /**
     * Normalizes mouse wheel delta across browsers
     * @return {Number} The delta
     */
    this.getWheelDelta = function(){
        var e = this.browserEvent;
        var delta = 0;
        if(e.wheelDelta){ /* IE/Opera. */
            delta = e.wheelDelta/120;
            /* In Opera 9, delta differs in sign as compared to IE. */
            if(window.opera) delta = -delta;
        }else if(e.detail){ /* Mozilla case. */
            delta = -e.detail/3;
        }
        return delta;
    };

    /**
     * Returns true if the control, shift or alt key was pressed during this event.
     * @return {Boolean}
     */
    this.hasModifier = function(){
        return this.ctrlKey || this.altKey || this.shiftKey;
    };
}();



/**
 * @class Function
 */
 //
 /**
 * Creates a callback that passes arguments[0], arguments[1], arguments[2], ...
 * Call directly on any function. Example: <code>myFunction.createCallback(myarg, myarg2)</code>
 * Will create a function that is bound to those 2 args.
 * @return {Function} The new function
*/
Function.prototype.createCallback = function(/*args...*/){
    // make args available, in function below
    var args = arguments;
    var method = this;
    return function() {
        return method.apply(window, args);
    };
};

/**
 * Creates a delegate (callback) that sets the scope to obj.
 * Call directly on any function. Example: <code>this.myFunction.createDelegate(this)</code>
 * Will create a function that is automatically scoped to this.
 * @param {Object} obj (optional) The object for which the scope is set
 * @param {<i>Array</i>} args (optional) Overrides arguments for the call. (Defaults to the arguments passed by the caller)
 * @param {<i>Boolean/Number</i>} appendArgs (optional) if True args are appended to call args instead of overriding,
 *                                             if a number the args are inserted at the specified position
 * @return {Function} The new function
 */
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

/**
 * Calls this function after the number of millseconds specified.
 * @param {Number} millis The number of milliseconds for the setTimeout call
 * @param {Object} obj (optional) The object for which the scope is set
 * @param {<i>Array</i>} args (optional) Overrides arguments for the call. (Defaults to the arguments passed by the caller)
 * @param {<i>Boolean/Number</i>} appendArgs (optional) if True args are appended to call args instead of overriding,
 *                                             if a number the args are inserted at the specified position
 * @return {Number} The timeout id that can be used with clearTimeout
 */
Function.prototype.defer = function(millis, obj, args, appendArgs){
    return setTimeout(this.createDelegate(obj, args, appendArgs), millis);
};
/**
 * Create a combined function call sequence of the original function + the passed function.
 * The resulting function returns the results of the original function.
 * The passed fcn is called with the parameters of the original function
 * @param {Function} fcn The function to sequence
 * @param {<i>Object</i>} scope (optional) The scope of the passed fcn (Defaults to scope of original function or window)
 * @return {Function} The new function
 */
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

/**
 * IE will leak if this isn't here
 */
YAHOO.util.Event.on(window, 'unload', function(){
    delete Function.prototype.createSequence;
    delete Function.prototype.defer;
    delete Function.prototype.createDelegate;
    delete Function.prototype.createCallback;
    delete Function.prototype.createInterceptor;
});

/**
 * Creates an interceptor function. The passed fcn is called before the original one. If it returns false, the original one is not called.
 * The resulting function returns the results of the original function.
 * The passed fcn is called with the parameters of the original function.
 * @addon
 * @param {Function} fcn The function to call before the original
 * @param {<i>Object</i>} scope (optional) The scope of the passed fcn (Defaults to scope of original function or window)
 * @return {Function} The new function
 */
Function.prototype.createInterceptor = function(fcn, scope){
    if(typeof fcn != 'function'){
        return this;
    }
    var method = this;
    return function() {
        fcn.target = this;
        fcn.method = method;
        if(fcn.apply(scope || this || window, arguments) === false){
            return;
        }
        return method.apply(this || window, arguments);;
    };
};

/**
 * @class YAHOO.ext.util.Browser
 * @singleton
 */
YAHOO.ext.util.Browser = new function(){
	var ua = navigator.userAgent.toLowerCase();
	/** @type Boolean */
	this.isOpera = (ua.indexOf('opera') > -1);
   	/** @type Boolean */
	this.isSafari = (ua.indexOf('webkit') > -1);
   	/** @type Boolean */
	this.isIE = (window.ActiveXObject);
   	/** @type Boolean */
	this.isIE7 = (ua.indexOf('msie 7') > -1);
   	/** @type Boolean */
	this.isGecko = !this.isSafari && (ua.indexOf('gecko') > -1);

	if(ua.indexOf("windows") != -1 || ua.indexOf("win32") != -1){
	    /** @type Boolean */
	    this.isWindows = true;
	}else if(ua.indexOf("macintosh") != -1){
		/** @type Boolean */
	    this.isMac = true;
	}
	if(this.isIE && !this.isIE7){
        try{
            document.execCommand("BackgroundImageCache", false, true);
        }catch(e){}
    }
}();

YAHOO.print = function(arg1, arg2, etc){
    if(!YAHOO.ext._console){
        var cs = YAHOO.ext.DomHelper.insertBefore(document.body.firstChild,
        {tag: 'div',style:'width:250px;height:350px;overflow:auto;border:3px solid #c3daf9;' +
                'background:white;position:absolute;right:5px;top:5px;' +
                'font:normal 8pt arial,verdana,helvetica;z-index:50000;padding:5px;'}, true);
        new YAHOO.ext.Resizable(cs, {
            transparent:true,
            handles: 'all',
            pinned:true,
            adjustments: [0,0],
            wrap:true,
            draggable:(YAHOO.util.DD ? true : false)
        });
        cs.on('dblclick', cs.hide);
        YAHOO.ext._console = cs;
    }
    var msg = '';
    for(var i = 0, len = arguments.length; i < len; i++) {
    	msg += arguments[i] + '<hr noshade style="color:#eeeeee;" size="1">';
    }
    YAHOO.ext._console.dom.innerHTML = msg + YAHOO.ext._console.dom.innerHTML;
    YAHOO.ext._console.dom.scrollTop = 0;
    YAHOO.ext._console.show();
};

YAHOO.printf = function(format, arg1, arg2, etc){
    var args = Array.prototype.slice.call(arguments, 1);
    YAHOO.print(format.replace(
      /\{\{[^{}]*\}\}|\{(\d+)(,\s*([\w.]+))?\}/g,
      function(m, a1, a2, a3) {
        if (m.chatAt == '{') {
          return m.slice(1, -1);
        }
        var rpl = args[a1];
        if (a3) {
          var f = eval(a3);
          rpl = f(rpl);
        }
        return rpl ? rpl : '';
      }));
}

 /**
 * Enable custom handler signature and event cancelling. Using fireDirect() instead of fire() calls the subscribed event handlers
 * with the exact parameters passed to fireDirect, instead of the usual (eventType, args[], obj). IMO this is more intuitive
 * and promotes cleaner code. Also, if an event handler returns false, it is returned by fireDirect and no other handlers will be called.<br>
 * Example:<br><br><pre><code>
 * if(beforeUpdateEvent.fireDirect(myArg, myArg2) !== false){
 *     // do update
 * }</code></pre>
 */
YAHOO.util.CustomEvent.prototype.fireDirect = function(){
    var len=this.subscribers.length;
    for (var i=0; i<len; ++i) {
        var s = this.subscribers[i];
        if(s){
            var scope = (s.overrideContext) ? s.obj : this.scope;
            if(s.fn.apply(scope, arguments) === false){
                return false;
            }
        }
    }
    return true;
};

YAHOO.lang.extend = function(subclass, superclass, overrides){
    YAHOO.extend(subclass, superclass);
    subclass.override = function(o){
        YAHOO.override(subclass, o);
    };
    if(!subclass.prototype.override){
        subclass.prototype.override = function(o){
            for(var method in o){
                this[method] = o[method];
            }
        };
    }
    if(overrides){
        subclass.override(overrides);
    }
};

YAHOO.override = function(origclass, overrides){
    if(overrides){
        var p = origclass.prototype;
        for(var method in overrides){
            p[method] = overrides[method];
        }
    }
};

/**
 * @class YAHOO.ext.util.DelayedTask
 * Provides a convenient method of performing setTimeout where a new
 * timeout cancels the old timeout. An example would be performing validation on a keypress.
 * You can use this class to buffer
 * the keypress events for a certain number of milliseconds, and perform only if they stop
 * for that amount of time.
 * @constructor The parameters to this constructor serve as defaults and are not required.
 * @param {<i>Function</i>} fn (optional) The default function to timeout
 * @param {<i>Object</i>} scope (optional) The default scope of that timeout
 * @param {<i>Array</i>} args (optional) The default Array of arguments
 */
YAHOO.ext.util.DelayedTask = function(fn, scope, args){
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

/**
 * @class YAHOO.ext.KeyMap
 * Handles mapping keys to actions for an element. One key map can be used for multiple actions.
 * A KeyMap can also handle a string representation of keys.<br />
 * Usage:
 <pre><code>
 // map one key by key code
 var map = new YAHOO.ext.KeyMap('my-element', {
     key: 13,
     fn: myHandler,
     scope: myObject
 });

 // map multiple keys to one action by string
 var map = new YAHOO.ext.KeyMap('my-element', {
     key: "a\r\n\t",
     fn: myHandler,
     scope: myObject
 });

 // map multiple keys to multiple actions by strings and array of codes
 var map = new YAHOO.ext.KeyMap('my-element', [
    {
        key: [10,13],
        fn: function(){ alert('Return was pressed'); }
    }, {
        key: "abc",
        fn: function(){ alert('a, b or c was pressed'); }
    }, {
        key: "\t",
        ctrl:true,
        shift:true,
        fn: function(){ alert('Control + shift + tab was pressed.'); }
    }
]);
 </code></pre>
* <b>Note: A KepMap starts enabled</b>
* @constructor
* @param {String/HTMLElement/YAHOO.ext.Element} el The element to bind to
* @param {Object} config The config
* @param {String} eventName (optional) The event to bind to (Defaults to "keydown").
 */
YAHOO.ext.KeyMap = function(el, config, eventName){
    this.el  = getEl(el);
    this.eventName = eventName || 'keydown';
    this.bindings = [];
    if(config instanceof Array){
	    for(var i = 0, len = config.length; i < len; i++){
	        this.addBinding(config[i]);
	    }
    }else{
        this.addBinding(config);
    }
    this.keyDownDelegate = YAHOO.ext.EventManager.wrap(this.handleKeyDown, this, true);
    this.enable();
}

YAHOO.ext.KeyMap.prototype = {
    /**
     * Add a new binding to this KeyMap
     * @param {Object} config A single KeyMap config
     */
	addBinding : function(config){
        var keyCode = config.key,
            shift = config.shift,
            ctrl = config.ctrl,
            alt = config.alt,
            fn = config.fn,
            scope = config.scope;
        if(typeof keyCode == 'string'){
            var ks = [];
            var keyString = keyCode.toUpperCase();
            for(var j = 0, len = keyString.length; j < len; j++){
                ks.push(keyString.charCodeAt(j));
            }
            keyCode = ks;
        }
        var keyArray = keyCode instanceof Array;
        var handler = function(e){
            if((!shift || e.shiftKey) && (!ctrl || e.ctrlKey) &&  (!alt || e.altKey)){
                var k = e.getKey();
                if(keyArray){
                    for(var i = 0, len = keyCode.length; i < len; i++){
                        if(keyCode[i] == k){
                          fn.call(scope || window, k, e);
                          return;
                        }
                    }
                }else{
                    if(k == keyCode){
                        fn.call(scope || window, k, e);
                    }
                }
            }
        };
        this.bindings.push(handler);
	},

	handleKeyDown : function(e){
	    if(this.enabled){ //just in case
    	    var b = this.bindings;
    	    for(var i = 0, len = b.length; i < len; i++){
    	        b[i](e);
    	    }
	    }
	},

	/**
	 * Returns true if this KepMap is enabled
	 * @return {Boolean}
	 */
	isEnabled : function(){
	    return this.enabled;
	},

	/**
	 * Enable this KeyMap
	 */
	enable: function(){
		if (!this.enabled){
	        this.el.on(this.eventName, this.keyDownDelegate);
		    this.enabled = true;
		}
	},

	/**
	 * Disable this KeyMap
	 */
	disable: function(){
		if (this.enabled){
			this.el.removeListener(this.eventName, this.keyDownDelegate);
		    this.enabled = false;
		}
	}
};

/**
 * @class YAHOO.ext.util.Observable
 * Abstract base class that provides a common interface for publishing events. Subclasses are expected to
 * to have a property "events" with all the events defined.<br>
 * For example:
 * <pre><code>
 var Employee = function(name){
    this.name = name;
    this.events = {
        'fired' : new YAHOO.util.CustomEvent('fired'),
        'quit' : true // lazy initialize the CustomEvent
    }
 }
 YAHOO.extend(Employee, YAHOO.ext.util.Observable);
</code></pre>
 */
YAHOO.ext.util.Observable = function(){};
YAHOO.ext.util.Observable.prototype = {
    /**
     * Fires the specified event with the passed parameters (minus the event name).
     * @param {String} eventName
     * @param {Object...} args Variable number of parameters are passed to handlers
     * @return {Boolean} returns false if any of the handlers return false otherwise it returns true
     */
    fireEvent : function(){
        var ce = this.events[arguments[0].toLowerCase()];
        if(typeof ce == 'object'){
            return ce.fireDirect.apply(ce, Array.prototype.slice.call(arguments, 1));
        }else{
            return true;
        }
    },
    /**
     * Appends an event handler to this component
     * @param {String}   eventName     The type of event to listen for
     * @param {Function} handler        The method the event invokes
     * @param {<i>Object</i>}   scope  (optional) The scope (this object) for the handler
     * @param {<i>boolean</i>}  override (optional) If true, scope becomes the scope
     */
    addListener : function(eventName, fn, scope, override){
        eventName = eventName.toLowerCase();
        var ce = this.events[eventName];
        if(!ce){
            // added for a better message when subscribing to wrong event
            throw 'You are trying to listen for an event that does not exist: "' + eventName + '".';
        }
        if(typeof ce == 'boolean'){
            ce = new YAHOO.util.CustomEvent(eventName);
            this.events[eventName] = ce;
        }
        ce.subscribe(fn, scope, override);
    },

    /**
     * Appends an event handler to this component that is delayed the specified number of milliseconds. This
     * is useful for events that modify the DOM and need to wait for the browser to catch up.
     * @param {String}   eventName     The type of event to listen for
     * @param {Function} handler        The method the event invokes
     * @param {<i>Object</i>}   scope  (optional) The scope (this object) for the handler
     * @param {<i>Number</i>}  delay (optional) The number of milliseconds to delay (defaults to 1 millisecond)
     * @return {Function} The wrapped function that was created (can be used to remove the listener)
     */
    delayedListener : function(eventName, fn, scope, delay){
        var newFn = function(){
            setTimeout(fn.createDelegate(scope, arguments), delay || 1);
        }
        this.addListener(eventName, newFn);
        return newFn;
    },

    /**
     * Appends an event handler to this component that is buffered. If the event is triggered more than once
     * in the specified time-frame, only the last one actually fires.
     * @param {String}   eventName     The type of event to listen for
     * @param {Function} handler        The method the event invokes
     * @param {<i>Object</i>}   scope  (optional) The scope (this object) for the handler
     * @param {<i>Number</i>}  millis (optional) The number of milliseconds to buffer (defaults to 250)
     * @return {Function} The wrapped function that was created (can be used to remove the listener)
     */
    bufferedListener : function(eventName, fn, scope, millis){
        var task = new YAHOO.ext.util.DelayedTask();
        var newFn = function(){
            task.delay(millis || 250, fn, scope, Array.prototype.slice.call(arguments, 0));
        }
        this.addListener(eventName, newFn);
        return newFn;
    },

    /**
     * Removes a listener
     * @param {String}   eventName     The type of event to listen for
     * @param {Function} handler        The handler to remove
     * @param {<i>Object</i>}   scope  (optional) The scope (this object) for the handler
     */
    removeListener : function(eventName, fn, scope){
        var ce = this.events[eventName.toLowerCase()];
        if(typeof ce == 'object'){
            ce.unsubscribe(fn, scope);
        }
    },

    /**
     * Removes all listeners for this object
     */
    purgeListeners : function(){
        for(var evt in this.events){
            if(typeof this.events[evt] == 'object'){
                 this.events[evt].unsubscribeAll();
            }
        }
    }
};
/**
 * Appends an event handler to this element (shorthand for addListener)
 * @param {String}   eventName     The type of event to listen for
 * @param {Function} handler        The method the event invokes
 * @param {<i>Object</i>}   scope  (optional) The scope (this object) for the handler
 * @param {<i>boolean</i>}  override (optional) If true, scope becomes the scope
 * @method
 */
YAHOO.ext.util.Observable.prototype.on = YAHOO.ext.util.Observable.prototype.addListener;

/**
 * @class YAHOO.ext.util.Config
 * Class with one useful method
 * @singleton
 */
YAHOO.ext.util.Config = {
    /**
     * Copies all the properties of config to obj.
     * @param {Object} obj The receiver of the properties
     * @param {Object} config The source of the properties
     * @param {Object} defaults A different object that will also be applied for default values
     * @return {Object} returns obj
     */
    apply : function(obj, config, defaults){
        if(defaults){
            this.apply(obj, defaults);
        }
        if(config){
            for(var prop in config){
                obj[prop] = config[prop];
            }
        }
        return obj;
    }
};

if(!String.escape){
    String.escape = function(string) {
        return string.replace(/('|\\)/g, "\\$1");
    };
};

String.leftPad = function (val, size, ch) {
    var result = new String(val);
    if (ch == null) {
        ch = " ";
    }
    while (result.length < size) {
        result = ch + result;
    }
    return result;
};

/**
 * A simple enhancement to drag drop that allows you to constrain the movement of the
 * DD or DDProxy object to a particular element.<br /><br />
 *
 * Usage:
 <pre><code>
 var dd = new YAHOO.util.DDProxy("dragDiv1", "proxytest",
                { dragElId: "existingProxyDiv" });
 dd.startDrag = function(){
     this.constrainTo('parent-id');
 };
 </code></pre>
 * Or you can initalize it using the {@link YAHOO.ext.Element} object:
 <pre><code>
 getEl('dragDiv1').initDDProxy('proxytest', {dragElId: "existingProxyDiv"}, {
     startDrag : function(){
         this.constrainTo('parent-id');
     }
 });
 </code></pre>
 */
if(YAHOO.util.DragDrop){
    /**
     * Provides default constraint padding to "constrainTo" elements (defaults to {left: 0, right:0, top:0, bottom:0}).
     * @type Object
     */
    YAHOO.util.DragDrop.prototype.defaultPadding = {left:0, right:0, top:0, bottom:0};

    /**
     * Initializes the drag drop object's constraints to restrict movement to a certain element.
     * @param {String/HTMLElement/Element} constrainTo The element to constrain to.
     * @param {Object/Number} pad (optional) Pad provides a way to specify "padding" of the constraints,
     * and can be either a number for symmetrical padding (4 would be equal to {left:4, right:4, top:4, bottom:4}) or
     * an object containing the sides to pad. For example: {right:10, bottom:10}
     * @param {Boolean} inContent (optional) Constrain the draggable in the content box of the element (inside padding and borders)
     */
    YAHOO.util.DragDrop.prototype.constrainTo = function(constrainTo, pad, inContent){
        if(typeof pad == 'number'){
            pad = {left: pad, right:pad, top:pad, bottom:pad};
        }
        pad = pad || this.defaultPadding;
        var b = getEl(this.getEl()).getBox();
        var ce = getEl(constrainTo);
        var c = ce.dom == document.body ? { x: 0, y: 0,
                width: YAHOO.util.Dom.getViewportWidth(),
                height: YAHOO.util.Dom.getViewportHeight()} : ce.getBox(inContent || false);
        var topSpace = b.y - c.y;
        var leftSpace = b.x - c.x;

        this.resetConstraints();
        this.setXConstraint(leftSpace - (pad.left||0), // left
                c.width - leftSpace - b.width - (pad.right||0) //right
        );
        this.setYConstraint(topSpace - (pad.top||0), //top
                c.height - topSpace - b.height - (pad.bottom||0) //bottom
        );
    }
}

/**
 * @class YAHOO.ext.UpdateManager
 * @extends YAHOO.ext.util.Observable
 * Provides AJAX-style update for Element object using Yahoo
 * UI library YAHOO.util.Connect functionality.<br><br>
 * Usage:<br>
 * <pre><code>
 * // Get it from a YAHOO.ext.Element object
 * var el = getEl('foo');
 * var mgr = el.getUpdateManager();
 * mgr.update('http://myserver.com/index.php', 'param1=1&amp;param2=2');
 * ...
 * mgr.formUpdate('myFormId', 'http://myserver.com/index.php');
 * <br>
 * // or directly (returns the same UpdateManager instance)
 * var mgr = new YAHOO.ext.UpdateManager('myElementId');
 * mgr.startAutoRefresh(60, 'http://myserver.com/index.php');
 * mgr.on('update', myFcnNeedsToKnow);
 * <br>
 * </code></pre>
 * @requires YAHOO.ext.Element
 * @requires YAHOO.util.Dom
 * @requires YAHOO.util.Event
 * @requires YAHOO.util.CustomEvent
 * @requires YAHOO.util.Connect
 * @constructor
 * Create new UpdateManager directly.
 * @param {String/HTMLElement/YAHOO.ext.Element} el The element to update
 * @param {<i>Boolean</i>} forceNew (optional) By default the constructor checks to see if the passed element already has an UpdateManager and if it does it returns the same instance. This will skip that check (useful for extending this class).
 */
YAHOO.ext.UpdateManager = function(el, forceNew){
    el = YAHOO.ext.Element.get(el);
    if(!forceNew && el.updateManager){
        return el.updateManager;
    }
    /**
     * The Element object
     * @type YAHOO.ext.Element
     */
    this.el = el;
    /**
     * Cached url to use for refreshes. Overwritten every time update() is called unless 'discardUrl' param is set to true.
     * @type String
     */
    this.defaultUrl = null;
    this.beforeUpdate = new YAHOO.util.CustomEvent('UpdateManager.beforeUpdate');
    this.onUpdate = new YAHOO.util.CustomEvent('UpdateManager.onUpdate');
    this.onFailure = new YAHOO.util.CustomEvent('UpdateManager.onFailure');

    this.events = {
        /**
         * @event beforeupdate
         * Fired before an update is made, return false from your handler and the update is cancelled.
         * @param {YAHOO.ext.Element} el
         * @param {String/Object/Function} url
         * @param {String/Object} params
         */
        'beforeupdate': this.beforeUpdate,
        /**
         * @event update
         * Fired after successful update is made.
         * @param {YAHOO.ext.Element} el
         * @param {Object} oResponseObject The YAHOO.util.Connect response Object
         */
        'update': this.onUpdate,
        /**
         * @event failure
         * Fired on update failure. Uses fireDirect with signature: (oElement, oResponseObject)
         * @param {YAHOO.ext.Element} el
         * @param {Object} oResponseObject The YAHOO.util.Connect response Object
         */
        'failure': this.onFailure
    };

    /**
     * Blank page URL to use with SSL file uploads (Defaults to YAHOO.ext.UpdateManager.defaults.sslBlankUrl or 'about:blank').
     * @type String
     */
    this.sslBlankUrl = YAHOO.ext.UpdateManager.defaults.sslBlankUrl;
    /**
     * Whether to append unique parameter on get request to disable caching (Defaults to YAHOO.ext.UpdateManager.defaults.disableCaching or false).
     * @type Boolean
     */
    this.disableCaching = YAHOO.ext.UpdateManager.defaults.disableCaching;
    /**
     * Text for loading indicator (Defaults to YAHOO.ext.UpdateManager.defaults.indicatorText or '&lt;div class="loading-indicator"&gt;Loading...&lt;/div&gt;').
     * @type String
     */
    this.indicatorText = YAHOO.ext.UpdateManager.defaults.indicatorText;
    /**
     * Whether to show indicatorText when loading (Defaults to YAHOO.ext.UpdateManager.defaults.showLoadIndicator or true).
     * @type String
     */
    this.showLoadIndicator = YAHOO.ext.UpdateManager.defaults.showLoadIndicator;
    /**
     * Timeout for requests or form posts in seconds (Defaults to YAHOO.ext.UpdateManager.defaults.timeout or 30 seconds).
     * @type Number
     */
    this.timeout = YAHOO.ext.UpdateManager.defaults.timeout;

    /**
     * True to process scripts in the output (Defaults to YAHOO.ext.UpdateManager.defaults.loadScripts (false)).
     * @type Boolean
     */
    this.loadScripts = YAHOO.ext.UpdateManager.defaults.loadScripts;

    /**
     * YAHOO.util.Connect transaction object of current executing transaction
     */
    this.transaction = null;

    /**
     * @private
     */
    this.autoRefreshProcId = null;
    /**
     * Delegate for refresh() prebound to 'this', use myUpdater.refreshDelegate.createCallback(arg1, arg2) to bind arguments
     * @type Function
     */
    this.refreshDelegate = this.refresh.createDelegate(this);
    /**
     * Delegate for update() prebound to 'this', use myUpdater.updateDelegate.createCallback(arg1, arg2) to bind arguments
     * @type Function
     */
    this.updateDelegate = this.update.createDelegate(this);
    /**
     * Delegate for formUpdate() prebound to 'this', use myUpdater.formUpdateDelegate.createCallback(arg1, arg2) to bind arguments
     * @type Function
     */
    this.formUpdateDelegate = this.formUpdate.createDelegate(this);
    /**
     * @private
     */
    this.successDelegate = this.processSuccess.createDelegate(this);
    /**
     * @private
     */
     this.failureDelegate = this.processFailure.createDelegate(this);

     /**
      * The renderer for this UpdateManager. Defaults to {@link YAHOO.ext.UpdateManager.BasicRenderer}.
      */
      this.renderer = new YAHOO.ext.UpdateManager.BasicRenderer();
};

YAHOO.ext.UpdateManager.prototype = {
    fireEvent : YAHOO.ext.util.Observable.prototype.fireEvent,
    on : YAHOO.ext.util.Observable.prototype.on,
    addListener : YAHOO.ext.util.Observable.prototype.addListener,
    delayedListener : YAHOO.ext.util.Observable.prototype.delayedListener,
    removeListener : YAHOO.ext.util.Observable.prototype.removeListener,
    purgeListeners : YAHOO.ext.util.Observable.prototype.purgeListeners,
    bufferedListener : YAHOO.ext.util.Observable.prototype.bufferedListener,
    /**
     * Get the Element this UpdateManager is bound to
     * @return {YAHOO.ext.Element} The element
     */
    getEl : function(){
        return this.el;
    },

    /**
     * Performs an async request, updating this element with the response. If params are specified it uses POST, otherwise it uses GET.
     * @param {Object/String/Function} url The url for this request or a function to call to get the url or a config object containing any of the following options:
<pre><code>
um.update({<br/>
    url: 'your-url.php',<br/>
    params: {param1: 'foo', param2: 'bar'}, // or a URL encoded string<br/>
    callback: yourFunction,<br/>
    scope: yourObject, //(optional scope)  <br/>
    discardUrl: false, <br/>
    nocache: false,<br/>
    text: 'Loading...',<br/>
    timeout: 30,<br/>
    scripts: false<br/>
});
</code></pre>
     * The only required property is url. The optional properties nocache, text and scripts
     * are shorthand for disableCaching, indicatorText and loadScripts and are used to set their associated property on this UpdateManager instance.
     * @param {<i>String/Object</i>} params (optional) The parameters to pass as either a url encoded string "param1=1&amp;param2=2" or an object {param1: 1, param2: 2}
     * @param {<i>Function</i>} callback (optional) Callback when transaction is complete - called with signature (oElement, bSuccess)
     * @param {<i>Boolean</i>} discardUrl (optional) By default when you execute an update the defaultUrl is changed to the last used url. If true, it will not store the url.
     */
    update : function(url, params, callback, discardUrl){
        if(this.beforeUpdate.fireDirect(this.el, url, params) !== false){
            if(typeof url == 'object'){ // must be config object
                var cfg = url;
                url = cfg.url;
                params = params || cfg.params;
                callback = callback || cfg.callback;
                discardUrl = discardUrl || cfg.discardUrl;
                if(callback && cfg.scope){
                    callback = callback.createDelegate(cfg.scope);
                }
                if(typeof cfg.nocache != 'undefined'){this.disableCaching = cfg.nocache};
                if(typeof cfg.text != 'undefined'){this.indicatorText = '<div class="loading-indicator">'+cfg.text+'</div>'};
                if(typeof cfg.scripts != 'undefined'){this.loadScripts = cfg.scripts};
                if(typeof cfg.timeout != 'undefined'){this.timeout = cfg.timeout};
            }
            this.showLoading();
            if(!discardUrl){
                this.defaultUrl = url;
            }
            if(typeof url == 'function'){
                url = url();
            }
            if(typeof params == 'function'){
                params = params();
            }
            if(params && typeof params != 'string'){ // must be object
                var buf = [];
                for(var key in params){
                    if(typeof params[key] != 'function'){
                        buf.push(encodeURIComponent(key), '=', encodeURIComponent(params[key]), '&');
                    }
                }
                delete buf[buf.length-1];
                params = buf.join('');
            }
            var callback = {
                success: this.successDelegate,
                failure: this.failureDelegate,
                timeout: (this.timeout*1000),
                argument: {'url': url, 'form': null, 'callback': callback, 'params': params}
            };
            var method = params ? 'POST' : 'GET';
            if(method == 'GET'){
                url = this.prepareUrl(url);
            }
            this.transaction = YAHOO.util.Connect.asyncRequest(method, url, callback, params);
        }
    },

    /**
     * Performs an async form post, updating this element with the response. If the form has the attribute enctype="multipart/form-data", it assumes it's a file upload.
     * Uses this.sslBlankUrl for SSL file uploads to prevent IE security warning. See YUI docs for more info.
     * @param {String/HTMLElement} form The form Id or form element
     * @param {<i>String</i>} url (optional) The url to pass the form to. If omitted the action attribute on the form will be used.
     * @param {<i>Boolean</i>} reset (optional) Whether to try to reset the form after the update
     * @param {<i>Function</i>} callback (optional) Callback when transaction is complete - called with signature (oElement, bSuccess)
     */
    formUpdate : function(form, url, reset, callback){
        if(this.beforeUpdate.fireDirect(this.el, form, url) !== false){
            this.showLoading();
            formEl = YAHOO.util.Dom.get(form);
            if(typeof url == 'function'){
                url = url();
            }
            if(typeof params == 'function'){
                params = params();
            }
            url = url || formEl.action;
            var callback = {
                success: this.successDelegate,
                failure: this.failureDelegate,
                timeout: (this.timeout*1000),
                argument: {'url': url, 'form': form, 'callback': callback, 'reset': reset}
            };
            var isUpload = false;
            var enctype = formEl.getAttribute('enctype');
            if(enctype && enctype.toLowerCase() == 'multipart/form-data'){
                isUpload = true;
            }
            YAHOO.util.Connect.setForm(form, isUpload, this.sslBlankUrl);
            this.transaction = YAHOO.util.Connect.asyncRequest('POST', url, callback);
        }
    },

    /**
     * Refresh the element with the last used url or defaultUrl. If there is no url, it returns immediately
     * @param {Function} callback (optional) Callback when transaction is complete - called with signature (oElement, bSuccess)
     */
    refresh : function(callback){
        if(this.defaultUrl == null){
            return;
        }
        this.update(this.defaultUrl, null, callback, true);
    },

    /**
     * Set this element to auto refresh.
     * @param {Number} interval How often to update (in seconds).
     * @param {<i>String/Function</i>} url (optional) The url for this request or a function to call to get the url (Defaults to the last used url)
     * @param {<i>String/Object</i>} params (optional) The parameters to pass as either a url encoded string "&param1=1&param2=2" or as an object {param1: 1, param2: 2}
     * @param {<i>Function</i>} callback (optional) Callback when transaction is complete - called with signature (oElement, bSuccess)
     * @param {<i>Boolean</i>} refreshNow (optional) Whether to execute the refresh now, or wait the interval
     */
    startAutoRefresh : function(interval, url, params, callback, refreshNow){
        if(refreshNow){
            this.update(url || this.defaultUrl, params, callback, true);
        }
        if(this.autoRefreshProcId){
            clearInterval(this.autoRefreshProcId);
        }
        this.autoRefreshProcId = setInterval(this.update.createDelegate(this, [url || this.defaultUrl, params, callback, true]), interval*1000);
    },

    /**
     * Stop auto refresh on this element.
     */
     stopAutoRefresh : function(){
        if(this.autoRefreshProcId){
            clearInterval(this.autoRefreshProcId);
        }
    },

    /**
     * Called to update the element to "Loading" state. Override to perform custom action.
     */
    showLoading : function(){
        if(this.showLoadIndicator){
            this.el.update(this.indicatorText);
        }
    },

    /**
     * Adds unique parameter to query string if disableCaching = true
     * @private
     */
    prepareUrl : function(url){
        if(this.disableCaching){
            var append = '_dc=' + (new Date().getTime());
            if(url.indexOf('?') !== -1){
                url += '&' + append;
            }else{
                url += '?' + append;
            }
        }
        return url;
    },

    /**
     * @private
     */
    processSuccess : function(response){
        this.transaction = null;
        if(response.argument.form && response.argument.reset){
            try{ // put in try/catch since some older FF releases had problems with this
                response.argument.form.reset();
            }catch(e){}
        }
        if(this.loadScripts){
            this.renderer.render(this.el, response, this,
                this.updateComplete.createDelegate(this, [response]));
        }else{
            this.renderer.render(this.el, response, this);
            this.updateComplete(response);
        }
    },

    updateComplete : function(response){
        this.fireEvent('update', this.el, response);
        if(typeof response.argument.callback == 'function'){
            response.argument.callback(this.el, true);
        }
    },

    /**
     * @private
     */
    processFailure : function(response){
        this.transaction = null;
        this.onFailure.fireDirect(this.el, response);
        if(typeof response.argument.callback == 'function'){
            response.argument.callback(this.el, false);
        }
    },

    /**
     * Set the content renderer for this UpdateManager. See {@link YAHOO.ext.UpdateManager.BasicRenderer#render} for more details.
     * @param {Object} renderer The object implementing the render() method
     */
    setRenderer : function(renderer){
        this.renderer = renderer;
    },

    getRenderer : function(){
       return this.renderer;
    },

    /**
     * Set the defaultUrl used for updates
     * @param {String/Function} defaultUrl The url or a function to call to get the url
     */
    setDefaultUrl : function(defaultUrl){
        this.defaultUrl = defaultUrl;
    },

    /**
     * Aborts the executing transaction
     */
    abort : function(){
        if(this.transaction){
            YAHOO.util.Connect.abort(this.transaction);
        }
    },

    /**
     * Returns true if an update is in progress
     * @return {Boolean}
     */
    isUpdating : function(){
        if(this.transaction){
            return YAHOO.util.Connect.isCallInProgress(this.transaction);
        }
        return false;
    }
};

/**
 * The defaults collection enables customizing the default properties of UpdateManager
 */
   YAHOO.ext.UpdateManager.defaults = {
       /**
         * Timeout for requests or form posts in seconds (Defaults 30 seconds).
         * @type Number
         */
         timeout : 30,

         /**
         * True to process scripts by default (Defaults to false).
         * @type Number
         */
        loadScripts : false,

        /**
        * Blank page URL to use with SSL file uploads (Defaults to 'about:blank').
        * @type String
        */
        sslBlankUrl : (YAHOO.ext.SSL_SECURE_URL || 'javascript:false'),
        /**
         * Whether to append unique parameter on get request to disable caching (Defaults to false).
         * @type Boolean
         */
        disableCaching : false,
        /**
         * Whether to show indicatorText when loading (Defaults to true).
         * @type String
         */
        showLoadIndicator : true,
        /**
         * Text for loading indicator (Defaults to '&lt;div class="loading-indicator"&gt;Loading...&lt;/div&gt;').
         * @type String
         */
        indicatorText : '<div class="loading-indicator">Loading...</div>'
   };

/**
 * Static convenience method, Usage:
 * <pre><code>YAHOO.ext.UpdateManager.update('my-div', 'stuff.php');</code></pre>
 * @param {String/HTMLElement/YAHOO.ext.Element} el The element to update
 * @param {String} url The url
 * @param {<i>String/Object</i>} params (optional) Url encoded param string or an object of name/value pairs
 * @param {<i>Object</i>} options (optional) A config object with any of the UpdateManager properties you want to set - for example: {disableCaching:true, indicatorText: 'Loading data...'}
 * @static
 */
YAHOO.ext.UpdateManager.updateElement = function(el, url, params, options){
    var um = getEl(el, true).getUpdateManager();
    YAHOO.ext.util.Config.apply(um, options);
    um.update(url, params, options.callback);
}
// alias for backwards compat
YAHOO.ext.UpdateManager.update = YAHOO.ext.UpdateManager.updateElement;
/**
 * @class YAHOO.ext.UpdateManager.BasicRenderer
 * Default Content renderer. Updates the elements innerHTML with the responseText.
 */
YAHOO.ext.UpdateManager.BasicRenderer = function(){};

YAHOO.ext.UpdateManager.BasicRenderer.prototype = {
    /**
     * This is called when the transaction is completed and it's time to update the element - The BasicRenderer
     * updates the elements innerHTML with the responseText - To perform a custom render (i.e. XML or JSON processing),
     * create an object with a "render(el, response)" method and pass it to setRenderer on the UpdateManager.
     * @param {YAHOO.ext.Element} el The element being rendered
     * @param {Object} response The YUI Connect response object
     * @param {UpdateManager} updateManager The calling update manager
     * @param {Function} callback A callback that will need to be called if loadScripts is true on the UpdateManager
     */
     render : function(el, response, updateManager, callback){
        el.update(response.responseText, updateManager.loadScripts, callback);
    }
};
/*
 * yui-ext 0.33
 * Copyright(c) 2006, Jack Slocum.
 */

/**
 * @class YAHOO.ext.Element
 * Wraps around a DOM element and provides convenient access to Yahoo
 * UI library functionality (and more).<br><br>
 * Usage:<br>
 * <pre><code>
 * var el = YAHOO.ext.Element.get('myElementId');
 * // or the shorter
 * var el = getEl('myElementId');
 * </code></pre>
 * Using YAHOO.ext.Element.get() instead of calling the constructor directly ensures you get the same object
 * each call instead of constructing a new one.<br><br>
 * For working with collections of Elements, see <a href="YAHOO.ext.CompositeElement.html">YAHOO.ext.CompositeElement</a>
 * @requires YAHOO.util.Dom
 * @requires YAHOO.util.Event
 * @requires YAHOO.util.CustomEvent
 * @requires YAHOO.util.Anim (optional) to support animation
 * @requires YAHOO.util.Motion (optional) to support animation
 * @requires YAHOO.util.Easing (optional) to support animation
 * @constructor Create a new Element directly.
 * @param {String/HTMLElement} element
 * @param {<i>Boolean</i>} forceNew (optional) By default the constructor checks to see if there is already an instance of this element in the cache and if there is it returns the same instance. This will skip that check (useful for extending this class).
 */
YAHOO.ext.Element = function(element, forceNew){
    var dom = YAHOO.util.Dom.get(element);
    if(!dom){ // invalid id/element
        return null;
    }
    if(!forceNew && YAHOO.ext.Element.cache[dom.id]){ // element object already exists
        return YAHOO.ext.Element.cache[dom.id];
    }
    /**
     * The DOM element
     * @type HTMLElement
     */
    this.dom = dom;

    /**
     * The DOM element ID
     * @type String
     */
    this.id = this.dom.id;
    /**
     * @private the current visibility mode
     */
    this.visibilityMode = YAHOO.ext.Element.VISIBILITY;


    /**
     * The element's default display mode @type String
     */
    this.originalDisplay = YAHOO.util.Dom.getStyle(this.dom, 'display') || '';
    if(this.autoDisplayMode){
        if(this.originalDisplay == 'none'){
            this.setVisibilityMode(YAHOO.ext.Element.DISPLAY);
        }
    }
    if(this.originalDisplay == 'none'){
        this.originalDisplay = '';
    }

    /**
     * The default unit to append to CSS values where a unit isn't provided (Defaults to px).
     * @type String
     */
    this.defaultUnit = 'px';
}

YAHOO.ext.Element.prototype = {
    /**
     * Sets the elements visibility mode. When setVisible() is called it
     * will use this to determine whether to set the visibility or the display property.
     * @param visMode Element.VISIBILITY or Element.DISPLAY
     * @return {YAHOO.ext.Element} this
     */
    setVisibilityMode : function(visMode){
        this.visibilityMode = visMode;
        return this;
    },

    /**
     * Convenience method for setVisibilityMode(Element.DISPLAY)
     * @param {String} display (optional) What to set display to when visible
     * @return {YAHOO.ext.Element} this
     */
    enableDisplayMode : function(display){
        this.setVisibilityMode(YAHOO.ext.Element.DISPLAY);
        if(typeof display != 'undefined') this.originalDisplay = display;
        return this;
    },

    /**
     * Perform Yahoo UI animation on this element.
     * @param {Object} args The YUI animation control args
     * @param {<i>Float</i>} duration (optional) How long the animation lasts. (Defaults to .35 seconds)
     * @param {<i>Function</i>} onComplete (optional) Function to call when animation completes.
     * @param {<i>Function</i>} easing (optional) YAHOO.util.Easing method to use. (Defaults to YAHOO.util.Easing.easeBoth)
     * @param {<i>Function</i>} animType (optional) YAHOO.util.Anim subclass to use. For example: YAHOO.util.Motion
     * @return {YAHOO.ext.Element} this
     */
    animate : function(args, duration, onComplete, easing, animType){
        this.anim(args, duration, onComplete, easing, animType);
        return this;
    },

    /**
     * @private Internal animation call
     */
    anim : function(args, duration, onComplete, easing, animType){
        animType = animType || YAHOO.util.Anim;
        var anim = new animType(this.dom, args, duration || .35,
                easing || YAHOO.util.Easing.easeBoth);
        if(onComplete){
            if(!(onComplete instanceof Array)){
                anim.onComplete.subscribe(onComplete, this, true);
            }else{
                for(var i = 0; i < onComplete.length; i++){
                    var fn = onComplete[i];
                    if(fn) anim.onComplete.subscribe(fn, this, true);
                }
            }
        }
        anim.animate();
    },

    /**
     * Scrolls this element into view within the passed container.
     * @param {<i>String/HTMLElement/Element</i>} container (optional) The container element to scroll (defaults to document.body)
     * @return {YAHOO.ext.Element} this
     */
    scrollIntoView : function(container){
        var c = getEl(container || document.body, true);
        var cp = c.getStyle('position');
        var restorePos = false;
        if(cp != 'relative' && cp != 'absolute'){
            c.setStyle('position', 'relative');
            restorePos = true;
        }
        var el = this.dom;
        var childTop = parseInt(el.offsetTop, 10);
        var childBottom = childTop + el.offsetHeight;
        var containerTop = parseInt(c.scrollTop, 10); // parseInt for safari bug
        var containerBottom = containerTop + c.clientHeight;
        if(childTop < containerTop){
        	c.scrollTop = childTop;
        }else if(childBottom > containerBottom){
            c.scrollTop = childBottom-c.clientHeight;
        }
        if(restorePos){
            c.setStyle('position', cp);
        }
        return this;
    },

    /**
     * Measures the elements content height and updates height to match. Note, this function uses setTimeout and
     * the new height may not be available immediately.
     * @param {<i>Boolean</i>} animate (optional) Animate the transition (Default is false)
     * @param {<i>Float</i>} duration (optional) Length of the animation. (Defaults to .35 seconds)
     * @param {<i>Function</i>} onComplete (optional) Function to call when animation completes.
     * @param {<i>Function</i>} easing (optional) YAHOO.util.Easing method to use. (Defaults to YAHOO.util.Easing.easeOut for hiding or YAHOO.util.Easing.easeIn for showing)
     * @return {YAHOO.ext.Element} this
     */
    autoHeight : function(animate, duration, onComplete, easing){
        var oldHeight = this.getHeight();
        this.clip();
        this.setHeight(1); // force clipping
        setTimeout(function(){
            var height = parseInt(this.dom.scrollHeight, 10); // parseInt for Safari
            if(!animate){
                this.setHeight(height);
                this.unclip();
                if(typeof onComplete == 'function'){
                    onComplete();
                }
            }else{
                this.setHeight(oldHeight); // restore original height
                this.setHeight(height, animate, duration, function(){
                    this.unclip();
                    if(typeof onComplete == 'function') onComplete();
                }.createDelegate(this), easing);
            }
        }.createDelegate(this), 0);
        return this;
    },

    /**
     * Checks whether the element is currently visible using both visibility and display properties.
     * @param {<i>Boolean</i>} deep True to walk the dom and see if parent elements are hidden.
     * @return {Boolean} true if the element is currently visible
     */
    isVisible : function(deep) {
        var vis = YAHOO.util.Dom.getStyle(this.dom, 'visibility') != 'hidden'
               && YAHOO.util.Dom.getStyle(this.dom, 'display') != 'none';
        if(!deep || !vis){
            return vis;
        }
        var p = this.dom.parentNode;
        while(p && p.tagName.toLowerCase() != 'body'){
            if(YAHOO.util.Dom.getStyle(p, 'visibility') == 'hidden' || YAHOO.util.Dom.getStyle(p, 'display') == 'none'){
                return false;
            }
            p = p.parentNode;
        }
        return true;
    },

    /**
     * Selects child nodes based on the passed CSS selector (the selector should not contain an id)
     * @param {String} selector The CSS selector
     * @param {Boolean} unique true to create a unique YAHOO.ext.Element for each child (defaults to a shared flyweight object)
     * @return {CompositeElement/CompositeElementLite} The composite element
     */
    select : function(selector, unique){
        return YAHOO.ext.Element.select('#' + this.dom.id + ' ' + selector, unique);
    },

    /**
     * Initializes a YAHOO.util.DD object for this element.
     * @param {String} group The group the DD object is member of
     * @param {Object} config The DD config object
     * @param {Object} overrides An object containing methods to override/implement on the DD object
     * @return {YAHOO.util.DD} The DD object
     */
    initDD : function(group, config, overrides){
        var dd = new YAHOO.util.DD(YAHOO.util.Dom.generateId(this.dom), group, config);
        return YAHOO.ext.util.Config.apply(dd, overrides);
    },

    /**
     * Initializes a YAHOO.util.DDProxy object for this element.
     * @param {String} group The group the DDProxy object is member of
     * @param {Object} config The DDProxy config object
     * @param {Object} overrides An object containing methods to override/implement on the DDProxy object
     * @return {YAHOO.util.DDProxy} The DDProxy object
     */
    initDDProxy : function(group, config, overrides){
        var dd = new YAHOO.util.DDProxy(YAHOO.util.Dom.generateId(this.dom), group, config);
        return YAHOO.ext.util.Config.apply(dd, overrides);
    },

    /**
     * Initializes a YAHOO.util.DDTarget object for this element.
     * @param {String} group The group the DDTarget object is member of
     * @param {Object} config The DDTarget config object
     * @param {Object} overrides An object containing methods to override/implement on the DDTarget object
     * @return {YAHOO.util.DDTarget} The DDTarget object
     */
    initDDTarget : function(group, config, overrides){
        var dd = new YAHOO.util.DDTarget(YAHOO.util.Dom.generateId(this.dom), group, config);
        return YAHOO.ext.util.Config.apply(dd, overrides);
    },

    /**
     * Sets the visibility of the element (see details). If the visibilityMode is set to Element.DISPLAY, it will use
     * the display property to hide the element, otherwise it uses visibility. The default is to hide and show using the visibility property.
     * @param {Boolean} visible Whether the element is visible
     * @param {<i>Boolean</i>} animate (optional) Fade the element in or out (Default is false)
     * @param {<i>Float</i>} duration (optional) How long the fade effect lasts. (Defaults to .35 seconds)
     * @param {<i>Function</i>} onComplete (optional) Function to call when animation completes.
     * @param {<i>Function</i>} easing (optional) YAHOO.util.Easing method to use. (Defaults to YAHOO.util.Easing.easeOut for hiding or YAHOO.util.Easing.easeIn for showing)
     * @return {YAHOO.ext.Element} this
     */
     setVisible : function(visible, animate, duration, onComplete, easing){
        //if(this.isVisible() == visible) return; // nothing to do
        if(!animate || !YAHOO.util.Anim){
            if(this.visibilityMode == YAHOO.ext.Element.DISPLAY){
                this.setDisplayed(visible);
            }else{
                YAHOO.util.Dom.setStyle(this.dom, 'visibility', visible ? 'visible' : 'hidden');
            }
        }else{
            // make sure they can see the transition
            this.setOpacity(visible?0:1);
            YAHOO.util.Dom.setStyle(this.dom, 'visibility', 'visible');
            if(this.visibilityMode == YAHOO.ext.Element.DISPLAY){
                this.setDisplayed(true);
            }
            var args = {opacity: { from: (visible?0:1), to: (visible?1:0) }};
            var anim = new YAHOO.util.Anim(this.dom, args, duration || .35,
                easing || (visible ? YAHOO.util.Easing.easeIn : YAHOO.util.Easing.easeOut));
            anim.onComplete.subscribe((function(){
                if(this.visibilityMode == YAHOO.ext.Element.DISPLAY){
                    this.setDisplayed(visible);
                }else{
                    YAHOO.util.Dom.setStyle(this.dom, 'visibility', visible ? 'visible' : 'hidden');
                }
            }).createDelegate(this));
            if(onComplete){
                anim.onComplete.subscribe(onComplete);
            }
            anim.animate();
        }
        return this;
    },

    /**
     * Returns true if display is not "none"
     * @return {Boolean}
     */
    isDisplayed : function() {
        return YAHOO.util.Dom.getStyle(this.dom, 'display') != 'none';
    },

    /**
     * Toggles the elements visibility or display, depending on visibility mode.
     * @param {<i>Boolean</i>} animate (optional) Fade the element in or out (Default is false)
     * @param {<i>float</i>} duration (optional) How long the fade effect lasts. (Defaults to .35 seconds)
     * @param {<i>Function</i>} onComplete (optional) Function to call when animation completes.
     * @param {<i>Function</i>} easing (optional) YAHOO.util.Easing method to use. (Defaults to YAHOO.util.Easing.easeOut for hiding or YAHOO.util.Easing.easeIn for showing)
     * @return {YAHOO.ext.Element} this
     */
    toggle : function(animate, duration, onComplete, easing){
        this.setVisible(!this.isVisible(), animate, duration, onComplete, easing);
        return this;
    },

    /**
     * Sets the css display. Uses originalDisplay if value is a boolean true.
     * @param {Boolean} value Boolean to display the element using it's default display or a string to set the display directly
     * @return {YAHOO.ext.Element} this
     */
    setDisplayed : function(value) {
        if(typeof value == 'boolean'){
           value = value ? this.originalDisplay : 'none';
        }
        YAHOO.util.Dom.setStyle(this.dom, 'display', value);
        return this;
    },

    /**
     * Tries to focus the element. Any exceptions are caught.
     * @return {YAHOO.ext.Element} this
     */
    focus : function() {
        try{
            this.dom.focus();
        }catch(e){}
        return this;
    },

    /**
     * Tries to blur the element. Any exceptions are caught.
     * @return {YAHOO.ext.Element} this
     */
    blur : function() {
        try{
            this.dom.blur();
        }catch(e){}
        return this;
    },

    /**
     * Add a CSS class to the element.
     * @param {String/Array} className The CSS class to add or an array of classes
     * @return {YAHOO.ext.Element} this
     */
    addClass : function(className){
        if(className instanceof Array){
            for(var i = 0, len = className.length; i < len; i++) {
            	this.addClass(className[i]);
            }
        }else{
            if(!this.hasClass(className)){
                this.dom.className = this.dom.className + ' ' + className;
            }
        }
        return this;
    },

    /**
     * Adds the passed className to this element and removes the class from all siblings
     * @param {String} className The className to add
     * @return {YAHOO.ext.Element} this
     */
    radioClass : function(className){
        var siblings = this.dom.parentNode.childNodes;
        for(var i = 0; i < siblings.length; i++) {
        	var s = siblings[i];
        	if(s.nodeType == 1){
        	    YAHOO.util.Dom.removeClass(s, className);
        	}
        }
        this.addClass(className);
        return this;
    },
    /**
     * Removes a CSS class from the element.
     * @param {String/Array} className The CSS class to remove or an array of classes
     * @return {YAHOO.ext.Element} this
     */
    removeClass : function(className){
        if(className instanceof Array){
            for(var i = 0, len = className.length; i < len; i++) {
            	this.removeClass(className[i]);
            }
        }else{
            var re = new RegExp('(?:^|\\s+)' + className + '(?:\\s+|$)', 'g');
            var c = this.dom.className;
            if(re.test(c)){
                this.dom.className = c.replace(re, ' ');
            }
        }
        return this;
    },

    /**
     * Toggles (adds or removes) the passed class.
     * @param {String} className
     * @return {YAHOO.ext.Element} this
     */
    toggleClass : function(className){
        if(this.hasClass(className)){
            this.removeClass(className);
        }else{
            this.addClass(className);
        }
        return this;
    },

    /**
     * Checks if a CSS class is in use by the element.
     * @param {String} className The CSS class to check
     * @return {Boolean} true or false
     */
    hasClass : function(className){
        var re = new RegExp('(?:^|\\s+)' + className + '(?:\\s+|$)');
        return re.test(this.dom.className);
    },

    /**
     * Replaces a CSS class on the element with another.
     * @param {String} oldClassName The CSS class to replace
     * @param {String} newClassName The replacement CSS class
     * @return {YAHOO.ext.Element} this
     */
    replaceClass : function(oldClassName, newClassName){
        this.removeClass(oldClassName);
        this.addClass(newClassName);
        return this;
    },

    /**
       * Normalizes currentStyle and ComputedStyle.
       * @param {String} property The style property whose value is returned.
       * @return {String} The current value of the style property for this element.
       */
    getStyle : function(name){
        return YAHOO.util.Dom.getStyle(this.dom, name);
    },

    /**
       * Wrapper for setting style properties, also takes single object parameter of multiple styles
       * @param {String/Object} property The style property to be set or an object of multiple styles.
       * @param {String} val (optional) The value to apply to the given property or null if an object was passed.
       * @return {YAHOO.ext.Element} this
     */
    setStyle : function(name, value){
        if(typeof name == 'string'){
            YAHOO.util.Dom.setStyle(this.dom, name, value);
        }else{
            var D = YAHOO.util.Dom;
            for(var style in name){
                if(typeof name[style] != 'function'){
                   D.setStyle(this.dom, style, name[style]);
                }
            }
        }
        return this;
    },

    /**
     * More flexible version of {@link #setStyle} for setting style properties.
     * @param {String/Object/Function} styles A style specification string eg "width:100px", or object in the form {width:"100px"}, or
     * a function which returns such a specification.
     * @return {YAHOO.ext.Element} this
     */
    applyStyles : function(style){
       YAHOO.ext.DomHelper.applyStyles(this.dom, style);
    },

    /**
       * Gets the current X position of the element based on page coordinates.  Element must be part of the DOM tree to have page coordinates (display:none or elements not appended return false).
       @ return {Number} The X position of the element
       */
    getX : function(){
        return YAHOO.util.Dom.getX(this.dom);
    },

    /**
       * Gets the current Y position of the element based on page coordinates.  Element must be part of the DOM tree to have page coordinates (display:none or elements not appended return false).
       @ return {Number} The Y position of the element
       */
    getY : function(){
        return YAHOO.util.Dom.getY(this.dom);
    },

    /**
       * Gets the current position of the element based on page coordinates.  Element must be part of the DOM tree to have page coordinates (display:none or elements not appended return false).
       @ return {Array} The XY position of the element
       */
    getXY : function(){
        return YAHOO.util.Dom.getXY(this.dom);
    },

    /**
       * Sets the X position of the element based on page coordinates.  Element must be part of the DOM tree to have page coordinates (display:none or elements not appended return false).
       @param {Number} The X position of the element
      * @param {<i>Boolean</i>} animate (optional) Animate the transition (Default is false)
     * @param {<i>float</i>} duration (optional) How long the animation lasts. (Defaults to .35 seconds)
     * @param {<i>Function</i>} onComplete (optional) Function to call when animation completes.
     * @param {<i>Function</i>} easing (optional) YAHOO.util.Easing method to use. (Defaults to YAHOO.util.Easing.easeBoth)
     * @return {YAHOO.ext.Element} this
     */
    setX : function(x, animate, duration, onComplete, easing){
        if(!animate || !YAHOO.util.Anim){
            YAHOO.util.Dom.setX(this.dom, x);
        }else{
            this.setXY([x, this.getY()], animate, duration, onComplete, easing);
        }
        return this;
    },

    /**
       * Sets the Y position of the element based on page coordinates.  Element must be part of the DOM tree to have page coordinates (display:none or elements not appended return false).
       @param {Number} The Y position of the element
      * @param {<i>Boolean</i>} animate (optional) Animate the transition (Default is false)
     * @param {<i>float</i>} duration (optional) How long the animation lasts. (Defaults to .35 seconds)
     * @param {<i>Function</i>} onComplete (optional) Function to call when animation completes.
     * @param {<i>Function</i>} easing (optional) YAHOO.util.Easing method to use. (Defaults to YAHOO.util.Easing.easeBoth)
     * @return {YAHOO.ext.Element} this
      */
    setY : function(y, animate, duration, onComplete, easing){
        if(!animate || !YAHOO.util.Anim){
            YAHOO.util.Dom.setY(this.dom, y);
        }else{
            this.setXY([this.getX(), y], animate, duration, onComplete, easing);
        }
        return this;
    },

    /**
     * Set the element's left position directly using CSS style (instead of setX())
     * @param {String} left The left CSS property value
     * @return {YAHOO.ext.Element} this
     */
    setLeft : function(left){
        YAHOO.util.Dom.setStyle(this.dom, 'left', this.addUnits(left));
        return this;
    },

    /**
     * Set the element's top position directly using CSS style (instead of setY())
     * @param {String} top The top CSS property value
     * @return {YAHOO.ext.Element} this
     */
    setTop : function(top){
        YAHOO.util.Dom.setStyle(this.dom, 'top', this.addUnits(top));
        return this;
    },

    /**
     * Set the element's css right style
     * @param {String} right The right CSS property value
     * @return {YAHOO.ext.Element} this
     */
    setRight : function(right){
        YAHOO.util.Dom.setStyle(this.dom, 'right', this.addUnits(right));
        return this;
    },

    /**
     * Set the element's css bottom style
     * @param {String} bottom The bottom CSS property value
     * @return {YAHOO.ext.Element} this
     */
    setBottom : function(bottom){
        YAHOO.util.Dom.setStyle(this.dom, 'bottom', this.addUnits(bottom));
        return this;
    },

    /**
     * Set the position of the element in page coordinates, regardless of how the element is positioned.
     * The element must be part of the DOM tree to have page coordinates (display:none or elements not appended return false).
     * @param {Array} pos Contains X & Y [x, y] values for new position (coordinates are page-based)
     * @param {<i>Boolean</i>} animate (optional) Animate the transition (Default is false)
     * @param {<i>float</i>} duration (optional) How long the animation lasts. (Defaults to .35 seconds)
     * @param {<i>Function</i>} onComplete (optional) Function to call when animation completes.
     * @param {<i>Function</i>} easing (optional) YAHOO.util.Easing method to use. (Defaults to YAHOO.util.Easing.easeBoth)
      * @return {YAHOO.ext.Element} this
       */
    setXY : function(pos, animate, duration, onComplete, easing){
        if(!animate || !YAHOO.util.Anim){
            YAHOO.util.Dom.setXY(this.dom, pos);
        }else{
            this.anim({points: {to: pos}}, duration, onComplete, easing, YAHOO.util.Motion);
        }
        return this;
    },

    /**
     * Set the position of the element in page coordinates, regardless of how the element is positioned.
     * The element must be part of the DOM tree to have page coordinates (display:none or elements not appended return false).
     * @param {Number} x X value for new position (coordinates are page-based)
     * @param {Number} y Y value for new position (coordinates are page-based)
     * @param {<i>Boolean</i>} animate (optional) Animate the transition (Default is false)
     * @param {<i>float</i>} duration (optional) How long the animation lasts. (Defaults to .35 seconds)
     * @param {<i>Function</i>} onComplete (optional) Function to call when animation completes.
     * @param {<i>Function</i>} easing (optional) YAHOO.util.Easing method to use. (Defaults to YAHOO.util.Easing.easeBoth)
     * @return {YAHOO.ext.Element} this
     */
    setLocation : function(x, y, animate, duration, onComplete, easing){
        this.setXY([x, y], animate, duration, onComplete, easing);
        return this;
    },

    /**
     * Set the position of the element in page coordinates, regardless of how the element is positioned.
     * The element must be part of the DOM tree to have page coordinates (display:none or elements not appended return false).
     * @param {Number} x X value for new position (coordinates are page-based)
     * @param {Number} y Y value for new position (coordinates are page-based)
     * @param {<i>Boolean</i>} animate (optional) Animate the transition (Default is false)
     * @param {<i>float</i>} duration (optional) How long the animation lasts. (Defaults to .35 seconds)
     * @param {<i>Function</i>} onComplete (optional) Function to call when animation completes.
     * @param {<i>Function</i>} easing (optional) YAHOO.util.Easing method to use. (Defaults to YAHOO.util.Easing.easeBoth)
     * @return {YAHOO.ext.Element} this
     */
    moveTo : function(x, y, animate, duration, onComplete, easing){
        //YAHOO.util.Dom.setStyle(this.dom, 'left', this.addUnits(x));
        //YAHOO.util.Dom.setStyle(this.dom, 'top', this.addUnits(y));
        this.setXY([x, y], animate, duration, onComplete, easing);
        return this;
    },

    /**
       * Returns the region of the given element.
       * The element must be part of the DOM tree to have a region (display:none or elements not appended return false).
       * @return {Region} A YAHOO.util.Region containing "top, left, bottom, right" member data.
       */
    getRegion : function(){
        return YAHOO.util.Dom.getRegion(this.dom);
    },

    /**
     * Returns the offset height of the element
     * @param {Boolean} contentHeight (optional) true to get the height minus borders and padding
     * @return {Number} The element's height
     */
    getHeight : function(contentHeight){
        var h = this.dom.offsetHeight;
        return contentHeight !== true ? h : h-this.getBorderWidth('tb')-this.getPadding('tb');
    },

    /**
     * Returns the offset width of the element
     * @param {Boolean} contentWidth (optional) true to get the width minus borders and padding
     * @return {Number} The element's width
     */
    getWidth : function(contentWidth){
        var w = this.dom.offsetWidth;
        return contentWidth !== true ? w : w-this.getBorderWidth('lr')-this.getPadding('lr');
    },

    /**
     * Returns the size of the element
     * @param {Boolean} contentSize (optional) true to get the width/size minus borders and padding
     * @return {Object} An object containing the element's size {width: (element width), height: (element height)}
     */
    getSize : function(contentSize){
        return {width: this.getWidth(contentSize), height: this.getHeight(contentSize)};
    },

    /** @private */
    adjustWidth : function(width){
        if(typeof width == 'number'){
            if(this.autoBoxAdjust && !this.isBorderBox()){
               width -= (this.getBorderWidth('lr') + this.getPadding('lr'));
            }
            if(width < 0){
                width = 0;
            }
        }
        return width;
    },

    /** @private */
    adjustHeight : function(height){
        if(typeof height == 'number'){
           if(this.autoBoxAdjust && !this.isBorderBox()){
               height -= (this.getBorderWidth('tb') + this.getPadding('tb'));
           }
           if(height < 0){
               height = 0;
           }
        }
        return height;
    },

    /**
     * Set the width of the element
     * @param {Number} width The new width
     * @param {<i>Boolean</i>} animate (optional) Animate the transition (Default is false)
     * @param {<i>float</i>} duration (optional) How long the animation lasts. (Defaults to .35 seconds)
     * @param {<i>Function</i>} onComplete (optional) Function to call when animation completes.
     * @param {<i>Function</i>} easing (optional) YAHOO.util.Easing method to use. (Defaults to YAHOO.util.Easing.easeOut if width is larger or YAHOO.util.Easing.easeIn if it is smaller)
     * @return {YAHOO.ext.Element} this
     */
    setWidth : function(width, animate, duration, onComplete, easing){
        width = this.adjustWidth(width);
        if(!animate || !YAHOO.util.Anim){
            YAHOO.util.Dom.setStyle(this.dom, 'width', this.addUnits(width));
        }else{
            this.anim({width: {to: width}}, duration, onComplete,
                easing || (width > this.getWidth() ? YAHOO.util.Easing.easeOut : YAHOO.util.Easing.easeIn));
        }
        return this;
    },

    /**
     * Set the height of the element
     * @param {Number} height The new height
     * @param {<i>Boolean</i>} animate (optional) Animate the transition (Default is false)
     * @param {<i>float</i>} duration (optional) How long the animation lasts. (Defaults to .35 seconds)
     * @param {<i>Function</i>} onComplete (optional) Function to call when animation completes.
     * @param {<i>Function</i>} easing (optional) YAHOO.util.Easing method to use. (Defaults to YAHOO.util.Easing.easeOut if height is larger or YAHOO.util.Easing.easeIn if it is smaller)
     * @return {YAHOO.ext.Element} this
     */
     setHeight : function(height, animate, duration, onComplete, easing){
        height = this.adjustHeight(height);
        if(!animate || !YAHOO.util.Anim){
            YAHOO.util.Dom.setStyle(this.dom, 'height', this.addUnits(height));
        }else{
            this.anim({height: {to: height}}, duration, onComplete,
                   easing || (height > this.getHeight() ? YAHOO.util.Easing.easeOut : YAHOO.util.Easing.easeIn));
        }
        return this;
    },

    /**
     * Set the size of the element. If animation is true, both width an height will be animated concurrently.
     * @param {Number} width The new width
     * @param {Number} height The new height
     * @param {<i>Boolean</i>} animate (optional) Animate the transition (Default is false)
     * @param {<i>float</i>} duration (optional) How long the animation lasts. (Defaults to .35 seconds)
     * @param {<i>Function</i>} onComplete (optional) Function to call when animation completes.
     * @param {<i>Function</i>} easing (optional) YAHOO.util.Easing method to use. (Defaults to YAHOO.util.Easing.easeBoth)
     * @return {YAHOO.ext.Element} this
     */
     setSize : function(width, height, animate, duration, onComplete, easing){
        if(!animate || !YAHOO.util.Anim){
            this.setWidth(width);
            this.setHeight(height);
        }else{
            width = this.adjustWidth(width); height = this.adjustHeight(height);
            this.anim({width: {to: width}, height: {to: height}}, duration, onComplete, easing);
        }
        return this;
    },

    /**
     * Sets the element's position and size in one shot. If animation is true then width, height, x and y will be animated concurrently.
     * @param {Number} x X value for new position (coordinates are page-based)
     * @param {Number} y Y value for new position (coordinates are page-based)
     * @param {Number} width The new width
     * @param {Number} height The new height
     * @param {<i>Boolean</i>} animate (optional) Animate the transition (Default is false)
     * @param {<i>float</i>} duration (optional) How long the animation lasts. (Defaults to .35 seconds)
     * @param {<i>Function</i>} onComplete (optional) Function to call when animation completes.
     * @param {<i>Function</i>} easing (optional) YAHOO.util.Easing method to use. (Defaults to YAHOO.util.Easing.easeBoth)
     * @return {YAHOO.ext.Element} this
     */
    setBounds : function(x, y, width, height, animate, duration, onComplete, easing){
        if(!animate || !YAHOO.util.Anim){
            this.setWidth(width);
            this.setHeight(height);
            this.setLocation(x, y);
        }else{
            width = this.adjustWidth(width); height = this.adjustHeight(height);
            this.anim({points: {to: [x, y]}, width: {to: width}, height: {to: height}}, duration, onComplete, easing, YAHOO.util.Motion);
        }
        return this;
    },

    /**
     * Sets the element's position and size the the specified region. If animation is true then width, height, x and y will be animated concurrently.
     * @param {YAHOO.util.Region} region The region to fill
     * @param {<i>Boolean</i>} animate (optional) Animate the transition (Default is false)
     * @param {<i>float</i>} duration (optional) How long the animation lasts. (Defaults to .35 seconds)
     * @param {<i>Function</i>} onComplete (optional) Function to call when animation completes.
     * @param {<i>Function</i>} easing (optional) YAHOO.util.Easing method to use. (Defaults to YAHOO.util.Easing.easeBoth)
     * @return {YAHOO.ext.Element} this
     */
    setRegion : function(region, animate, duration, onComplete, easing){
        this.setBounds(region.left, region.top, region.right-region.left, region.bottom-region.top, animate, duration, onComplete, easing);
        return this;
    },

    /**
     * Appends an event handler to this element
     * @param {String}   eventName     The type of event to listen for
     * @param {Function} handler        The method the event invokes
     * @param {<i>Object</i>}   scope  (optional)  An arbitrary object that will be
     *                             passed as a parameter to the handler
     * @param {<i>boolean</i>}  override (optional) If true, the obj passed in becomes
     *                             the execution scope of the listener
     * @return {YAHOO.ext.Element} this
     */
    addListener : function(eventName, handler, scope, override){
        YAHOO.util.Event.addListener(this.dom, eventName, handler, scope || this, true);
        return this;
    },
    /**
     * Appends an event handler to this element that is buffered. If the event is triggered more than once
     * in the specified time-frame, only the last one actually fires.
     * @param {String}   eventName     The type of event to listen for
     * @param {Function} handler        The method the event invokes
     * @param {<i>Object</i>}   scope  (optional) The scope (this object) for the handler
     * @param {<i>Number</i>}  millis (optional) The number of milliseconds to buffer (defaults to 250)
     * @return {Function} The wrapped function that was created (can be used to remove the listener)
     */
    bufferedListener : function(eventName, fn, scope, millis){
        var task = new YAHOO.ext.util.DelayedTask();
        scope = scope || this;
        var newFn = function(){
            task.delay(millis || 250, fn, scope, Array.prototype.slice.call(arguments, 0));
        }
        this.addListener(eventName, newFn);
        return newFn;
    },


    /**
     * Appends an event handler to this element. The difference between this function and addListener is this
     * function prevents the default action, and if set stops propagation (bubbling) as well
     * @param {String}   eventName     The type of event to listen for
     * @param {Boolean}   stopPropagation     Whether to also stopPropagation (bubbling)
     * @param {Function} handler        The method the event invokes
     * @param {<i>Object</i>}   scope  (optional)  An arbitrary object that will be
     *                             passed as a parameter to the handler
     * @param {<i>boolean</i>}  override (optional) If true, the obj passed in becomes
     *                             the execution scope of the listener
     * @return {YAHOO.ext.Element} this
     */
    addHandler : function(eventName, stopPropagation, handler, scope, override){
        var fn = YAHOO.ext.Element.createStopHandler(stopPropagation, handler, scope || this, true);
        YAHOO.util.Event.addListener(this.dom, eventName, fn);
        return this;
    },

    /**
     * Appends an event handler to this element (Same as addListener)
     * @param {String}   eventName     The type of event to listen for
     * @param {Function} handler        The method the event invokes
     * @param {<i>Object</i>}   scope (optional)   An arbitrary object that will be
     *                             passed as a parameter to the handler
     * @param {<i>boolean</i>}  override (optional) If true, the obj passed in becomes
     *                             the execution scope of the listener
     * @return {YAHOO.ext.Element} this
     */
    on : function(eventName, handler, scope, override){
        YAHOO.util.Event.addListener(this.dom, eventName, handler, scope || this, true);
        return this;
    },

    /**
     * Append a managed listener - See {@link YAHOO.ext.EventObject} for more details. Use mon() for a shorter version.
     * @param {String}   eventName     The type of event to listen for
     * @param {Function} fn        The method the event invokes
     * @param {<i>Object</i>}   scope  (optional)  An arbitrary object that will be
     *                             passed as a parameter to the handler
     * @param {<i>boolean</i>}  override (optional) If true, the obj passed in becomes
     *                             the execution scope of the listener
     * @return {Function} The EventManager wrapped function that can be used to remove the listener
     */
    addManagedListener : function(eventName, fn, scope, override){
        return YAHOO.ext.EventManager.on(this.dom, eventName, fn, scope || this, true);
    },

    /**
     * Append a managed listener (shorthanded for {@link #addManagedListener})
     * @param {String}   eventName     The type of event to listen for
     * @param {Function} fn        The method the event invokes
     * @param {<i>Object</i>}   scope  (optional)  An arbitrary object that will be
     *                             passed as a parameter to the handler
     * @param {<i>boolean</i>}  override (optional) If true, the obj passed in becomes
     *                             the execution scope of the listener
     * @return {Function} The EventManager wrapped function that can be used to remove the listener
     */
    mon : function(eventName, fn, scope, override){
        return YAHOO.ext.EventManager.on(this.dom, eventName, fn, scope || this, true);
    },
    /**
     * Removes an event handler from this element
     * @param {String} sType the type of event to remove
     * @param {Function} fn the method the event invokes
     * @param {Object} scope
     * @return {YAHOO.ext.Element} this
     */
    removeListener : function(eventName, handler, scope){
        YAHOO.util.Event.removeListener(this.dom, eventName, handler);
        return this;
    },

    /**
     * Removes all previous added listeners from this element
     * @return {YAHOO.ext.Element} this
     */
    removeAllListeners : function(){
        YAHOO.util.Event.purgeElement(this.dom);
        return this;
    },


    /**
     * Set the opacity of the element
     * @param {Float} opacity The new opacity. 0 = transparent, .5 = 50% visibile, 1 = fully visible, etc
     * @param {<i>Boolean</i>} animate (optional) Animate (fade) the transition (Default is false)
     * @param {<i>Float</i>} duration (optional) How long the animation lasts. (Defaults to .35 seconds)
     * @param {<i>Function</i>} onComplete (optional) Function to call when animation completes.
     * @param {<i>Function</i>} easing (optional) YAHOO.util.Easing method to use. (Defaults to YAHOO.util.Easing.easeOut if height is larger or YAHOO.util.Easing.easeIn if it is smaller)
     * @return {YAHOO.ext.Element} this
     */
     setOpacity : function(opacity, animate, duration, onComplete, easing){
        if(!animate || !YAHOO.util.Anim){
            YAHOO.util.Dom.setStyle(this.dom, 'opacity', opacity);
        }else{
            this.anim({opacity: {to: opacity}}, duration, onComplete, easing);
        }
        return this;
    },

    /**
     * Gets the left X coordinate
     * @param {Boolean} local True to get the local css position instead of page coordinate
     * @return {Number}
     */
    getLeft : function(local){
        if(!local){
            return this.getX();
        }else{
            return parseInt(this.getStyle('left'), 10) || 0;
        }
    },

    /**
     * Gets the right X coordinate of the element (element X position + element width)
     * @param {Boolean} local True to get the local css position instead of page coordinate
     * @return {Number}
     */
    getRight : function(local){
        if(!local){
            return this.getX() + this.getWidth();
        }else{
            return (this.getLeft(true) + this.getWidth()) || 0;
        }
    },

    /**
     * Gets the top Y coordinate
     * @param {Boolean} local True to get the local css position instead of page coordinate
     * @return {Number}
     */
    getTop : function(local) {
        if(!local){
            return this.getY();
        }else{
            return parseInt(this.getStyle('top'), 10) || 0;
        }
    },

    /**
     * Gets the bottom Y coordinate of the element (element Y position + element height)
     * @param {Boolean} local True to get the local css position instead of page coordinate
     * @return {Number}
     */
    getBottom : function(local){
        if(!local){
            return this.getY() + this.getHeight();
        }else{
            return (this.getTop(true) + this.getHeight()) || 0;
        }
    },

    /**
    * Set the element as absolute positioned with the specified z-index
    * @param {<i>Number</i>} zIndex (optional)
    * @return {YAHOO.ext.Element} this
     */
    setAbsolutePositioned : function(zIndex){
        this.setStyle('position', 'absolute');
        if(zIndex){
            this.setStyle('z-index', zIndex);
        }
        return this;
    },

    /**
    * Set the element as relative positioned with the specified z-index
    * @param {<i>Number</i>} zIndex (optional)
    * @return {YAHOO.ext.Element} this
     */
    setRelativePositioned : function(zIndex){
        this.setStyle('position', 'relative');
        if(zIndex){
            this.setStyle('z-index', zIndex);
        }
        return this;
    },

    /**
    * Clear positioning back to the default when the document was loaded
    * @return {YAHOO.ext.Element} this
     */
    clearPositioning : function(){
        this.setStyle('position', '');
        this.setStyle('left', '');
        this.setStyle('right', '');
        this.setStyle('top', '');
        this.setStyle('bottom', '');
        return this;
    },

    /**
    * Gets an object with all CSS positioning properties. Useful along with setPostioning to get
    * snapshot before performing an update and then restoring the element.
    * @return {Object}
    */
    getPositioning : function(){
        return {
            'position' : this.getStyle('position'),
            'left' : this.getStyle('left'),
            'right' : this.getStyle('right'),
            'top' : this.getStyle('top'),
            'bottom' : this.getStyle('bottom')
        };
    },

    /**
     * Gets the width of the border(s) for the specified side(s)
     * @param {String} side Can be t, l, r, b or any combination of those to add multiple values. For example,
     * passing lr would get the border (l)eft width + the border (r)ight width.
     * @return {Number} The width of the sides passed added together
     */
    getBorderWidth : function(side){
        return this.addStyles(side, YAHOO.ext.Element.borders);
    },

    /**
     * Gets the width of the padding(s) for the specified side(s)
     * @param {String} side Can be t, l, r, b or any combination of those to add multiple values. For example,
     * passing lr would get the padding (l)eft + the padding (r)ight.
     * @return {Number} The padding of the sides passed added together
     */
    getPadding : function(side){
        return this.addStyles(side, YAHOO.ext.Element.paddings);
    },

    /**
    * Set positioning with an object returned by getPositioning().
    * @param {Object} posCfg
    * @return {YAHOO.ext.Element} this
     */
    setPositioning : function(positionCfg){
        if(positionCfg.position)this.setStyle('position', positionCfg.position);
        if(positionCfg.left)this.setLeft(positionCfg.left);
        if(positionCfg.right)this.setRight(positionCfg.right);
        if(positionCfg.top)this.setTop(positionCfg.top);
        if(positionCfg.bottom)this.setBottom(positionCfg.bottom);
        return this;
    },


    /**
     * Quick set left and top adding default units
     * @return {YAHOO.ext.Element} this
     */
     setLeftTop : function(left, top){
        this.dom.style.left = this.addUnits(left);
        this.dom.style.top = this.addUnits(top);
        return this;
    },

    /**
     * Move this element relative to it's current position.
     * @param {String} direction Possible values are: 'l','left' - 'r','right' - 't','top','up' - 'b','bottom','down'.
     * @param {Number} distance How far to move the element in pixels
     * @param {<i>Boolean</i>} animate (optional) Animate the movement (Default is false)
     * @param {<i>Float</i>} duration (optional) How long the animation lasts. (Defaults to .35 seconds)
     * @param {<i>Function</i>} onComplete (optional) Function to call when animation completes.
     * @param {<i>Function</i>} easing (optional) YAHOO.util.Easing method to use.
     * @return {YAHOO.ext.Element} this
     */
     move : function(direction, distance, animate, duration, onComplete, easing){
        var xy = this.getXY();
        direction = direction.toLowerCase();
        switch(direction){
            case 'l':
            case 'left':
                this.moveTo(xy[0]-distance, xy[1], animate, duration, onComplete, easing);
                break;
           case 'r':
           case 'right':
                this.moveTo(xy[0]+distance, xy[1], animate, duration, onComplete, easing);
                break;
           case 't':
           case 'top':
           case 'up':
                this.moveTo(xy[0], xy[1]-distance, animate, duration, onComplete, easing);
                break;
           case 'b':
           case 'bottom':
           case 'down':
                this.moveTo(xy[0], xy[1]+distance, animate, duration, onComplete, easing);
                break;
        }
        return this;
    },

    /**
     *  Store the current overflow setting and clip overflow on the element - use {@link #unclip} to remove
     * @return {YAHOO.ext.Element} this
     */
    clip : function(){
        if(!this.isClipped){
           this.isClipped = true;
           this.originalClip = {
               'o': this.getStyle('overflow'),
               'x': this.getStyle('overflow-x'),
               'y': this.getStyle('overflow-y')
           };
           this.setStyle('overflow', 'hidden');
           this.setStyle('overflow-x', 'hidden');
           this.setStyle('overflow-y', 'hidden');
        }
        return this;
    },

    /**
     *  Return clipping (overflow) to original clipping before clip() was called
     * @return {YAHOO.ext.Element} this
     */
    unclip : function(){
        if(this.isClipped){
            this.isClipped = false;
            var o = this.originalClip;
            if(o.o){this.setStyle('overflow', o.o);}
            if(o.x){this.setStyle('overflow-x', o.x);}
            if(o.y){this.setStyle('overflow-y', o.y);}
        }
        return this;
    },

    /**
     * Align this element with another element.
     * @param {String/HTMLElement/YAHOO.ext.Element} element The element to align to.
     * @param {String} position The position to align to. Possible values are 'tl' - top left, 'tr' - top right, 'bl' - bottom left, and 'br' - bottom right.
     * @param {<i>Array</i>} offsets (optional) Offset the positioning by [x, y]
     * @param {<i>Boolean</i>} animate (optional) Animate the movement (Default is false)
     * @param {<i>Float</i>} duration (optional) How long the animation lasts. (Defaults to .35 seconds)
     * @param {<i>Function</i>} onComplete (optional) Function to call when animation completes.
     * @param {<i>Function</i>} easing (optional) YAHOO.util.Easing method to use.
     * @return {YAHOO.ext.Element} this
     */
     alignTo : function(element, position, offsets, animate, duration, onComplete, easing){
        var otherEl = getEl(element);
        if(!otherEl){
            return this; // must not exist
        }
        offsets = offsets || [0, 0];
        var r = otherEl.getRegion();
        position = position.toLowerCase();
        switch(position){
           case 'bl':
                this.moveTo(r.left + offsets[0], r.bottom + offsets[1],
                            animate, duration, onComplete, easing);
                break;
           case 'br':
                this.moveTo(r.right + offsets[0], r.bottom + offsets[1],
                            animate, duration, onComplete, easing);
                break;
           case 'tl':
                this.moveTo(r.left + offsets[0], r.top + offsets[1],
                            animate, duration, onComplete, easing);
                break;
           case 'tr':
                this.moveTo(r.right + offsets[0], r.top + offsets[1],
                            animate, duration, onComplete, easing);
                break;
        }
        return this;
    },

    /**
    * Clears any opacity settings from this element. Required in some cases for IE.
    * @return {YAHOO.ext.Element} this
     */
    clearOpacity : function(){
        if (window.ActiveXObject) {
            this.dom.style.filter = '';
        } else {
            this.dom.style.opacity = '';
            this.dom.style['-moz-opacity'] = '';
            this.dom.style['-khtml-opacity'] = '';
        }
        return this;
    },

    /**
    * Hide this element - Uses display mode to determine whether to use "display" or "visibility". See {@link #setVisible}.
    * @param {<i>Boolean</i>} animate (optional) Animate (fade) the transition (Default is false)
     * @param {<i>Float</i>} duration (optional) How long the animation lasts. (Defaults to .35 seconds)
     * @param {<i>Function</i>} onComplete (optional) Function to call when animation completes.
     * @param {<i>Function</i>} easing (optional) YAHOO.util.Easing method to use. (Defaults to YAHOO.util.Easing.easeBoth)
    * @return {YAHOO.ext.Element} this
      */
    hide : function(animate, duration, onComplete, easing){
        this.setVisible(false, animate, duration, onComplete, easing);
        return this;
    },

    /**
    * Show this element - Uses display mode to determine whether to use "display" or "visibility". See {@link #setVisible}.
    * @param {<i>Boolean</i>} animate (optional) Animate (fade in) the transition (Default is false)
     * @param {<i>Float</i>} duration (optional) How long the animation lasts. (Defaults to .35 seconds)
     * @param {<i>Function</i>} onComplete (optional) Function to call when animation completes.
     * @param {<i>Function</i>} easing (optional) YAHOO.util.Easing method to use. (Defaults to YAHOO.util.Easing.easeBoth)
     * @return {YAHOO.ext.Element} this
     */
    show : function(animate, duration, onComplete, easing){
        this.setVisible(true, animate, duration, onComplete, easing);
        return this;
    },

    /**
     * @private Test if size has a unit, otherwise appends the default
     */
    addUnits : function(size){
        if(size === '' || size == 'auto' || typeof size == 'undefined'){
            return size;
        }
        if(typeof size == 'number' || !YAHOO.ext.Element.unitPattern.test(size)){
            return size + this.defaultUnit;
        }
        return size;
    },

    /**
     * Temporarily enables offsets (width,height,x,y) for an element with display:none, use endMeasure() when done.
     * @return {YAHOO.ext.Element} this
     */
    beginMeasure : function(){
        var el = this.dom;
        if(el.offsetWidth || el.offsetHeight){
            return this; // offsets work already
        }
        var changed = [];
        var p = this.dom; // start with this element
        while((!el.offsetWidth && !el.offsetHeight) && p && p.tagName && p.tagName.toLowerCase() != 'body'){
            if(YAHOO.util.Dom.getStyle(p, 'display') == 'none'){
                changed.push({el: p, visibility: YAHOO.util.Dom.getStyle(p, 'visibility')});
                p.style.visibility = 'hidden';
                p.style.display = 'block';
            }
            p = p.parentNode;
        }
        this._measureChanged = changed;
        return this;

    },

    /**
     * Restores displays to before beginMeasure was called
     * @return {YAHOO.ext.Element} this
     */
    endMeasure : function(){
        var changed = this._measureChanged;
        if(changed){
            for(var i = 0, len = changed.length; i < len; i++) {
            	var r = changed[i];
            	r.el.style.visibility = r.visibility;
                r.el.style.display = 'none';
            }
            this._measureChanged = null;
        }
        return this;
    },

    /**
    * Update the innerHTML of this element, optionally searching for and processing scripts
    * @param {String} html The new HTML
    * @param {<i>Boolean</i>} loadScripts (optional) true to look for and process scripts
    * @param {Function} callback For async script loading you can be noticed when the update completes
    * @return {YAHOO.ext.Element} this
     */
    update : function(html, loadScripts, callback){
        if(typeof html == 'undefined'){
            html = '';
        }
        if(loadScripts !== true){
            this.dom.innerHTML = html;
            if(typeof callback == 'function'){
                callback();
            }
            return this;
        }
        var id = YAHOO.util.Dom.generateId();
        var dom = this.dom;

        html += '<span id="' + id + '"></span>';

        YAHOO.util.Event.onAvailable(id, function(){
            var hd = document.getElementsByTagName("head")[0];
            var re = /(?:<script.*?>)((\n|\r|.)*?)(?:<\/script>)/img;
            var srcRe = /\ssrc=([\'\"])(.*?)\1/i;
            var match;
            while(match = re.exec(html)){
                var srcMatch = match[0].match(srcRe);
                if(srcMatch && srcMatch[2]){
                   var s = document.createElement("script");
                   s.src = srcMatch[2];
                   hd.appendChild(s);
                }else if(match[1] && match[1].length > 0){
                   eval(match[1]);
                }
            }
            var el = document.getElementById(id);
            if(el){el.parentNode.removeChild(el);}
            if(typeof callback == 'function'){
                callback();
            }
        });
        dom.innerHTML = html.replace(/(?:<script.*?>)((\n|\r|.)*?)(?:<\/script>)/img, '');
        return this;
    },

    /**
     * Direct access to the UpdateManager update() method (takes the same parameters).
     * @param {String/Function} url The url for this request or a function to call to get the url
     * @param {<i>String/Object</i>} params (optional) The parameters to pass as either a url encoded string "param1=1&amp;param2=2" or an object {param1: 1, param2: 2}
     * @param {<i>Function</i>} callback (optional) Callback when transaction is complete - called with signature (oElement, bSuccess)
     * @param {<i>Boolean</i>} discardUrl (optional) By default when you execute an update the defaultUrl is changed to the last used url. If true, it will not store the url.
     * @return {YAHOO.ext.Element} this
     */
    load : function(){
        var um = this.getUpdateManager();
        um.update.apply(um, arguments);
        return this;
    },

    /**
    * Gets this elements UpdateManager
    * @return {YAHOO.ext.UpdateManager} The UpdateManager
    */
    getUpdateManager : function(){
        if(!this.updateManager){
            this.updateManager = new YAHOO.ext.UpdateManager(this);
        }
        return this.updateManager;
    },

    /**
     * Disables text selection for this element (normalized across browsers)
     * @return {YAHOO.ext.Element} this
     */
    unselectable : function(){
        this.dom.unselectable = 'on';
        this.swallowEvent('selectstart', true);
        this.applyStyles('-moz-user-select:none;-khtml-user-select:none;');
        return this;
    },

    /**
    * Calculates the x, y to center this element on the screen
    * @param {Boolean} offsetScroll True to offset the documents current scroll position
    * @return {Array} The x, y values [x, y]
    */
    getCenterXY : function(offsetScroll){
        var centerX = Math.round((YAHOO.util.Dom.getViewportWidth()-this.getWidth())/2);
        var centerY = Math.round((YAHOO.util.Dom.getViewportHeight()-this.getHeight())/2);
        if(!offsetScroll){
            return [centerX, centerY];
        }else{
            var scrollX = document.documentElement.scrollLeft || document.body.scrollLeft || 0;
            var scrollY = document.documentElement.scrollTop || document.body.scrollTop || 0;
            return[centerX + scrollX, centerY + scrollY];
        }
    },

    /**
    * Centers the Element in either the viewport, or another Element.
    * @param {String/HTMLElement/YAHOO.ext.Element} centerIn (optional) The element in which to center the element.
    */
    center : function(centerIn) {
        if(!centerIn){
            this.setXY(this.getCenterXY(true));
        }else{
            var box = YAHOO.ext.Element.get(centerIn).getBox();
            this.setXY([box.x + (box.width / 2) - (this.getWidth() / 2),
                   box.y + (box.height / 2) - (this.getHeight() / 2)]);
        }
        return this;
    },

    /**
    * Gets an array of child YAHOO.ext.Element objects by tag name
    * @param {String} tagName
    * @return {Array} The children
    */
    getChildrenByTagName : function(tagName){
        var children = this.dom.getElementsByTagName(tagName);
        var len = children.length;
        var ce = new Array(len);
        for(var i = 0; i < len; ++i){
            ce[i] = YAHOO.ext.Element.get(children[i], true);
        }
        return ce;
    },

    /**
    * Gets an array of child YAHOO.ext.Element objects by class name and optional tagName
    * @param {String} className
    * @param {<i>String</i>} tagName (optional)
    * @return {Array} The children
    */
    getChildrenByClassName : function(className, tagName){
        var children = YAHOO.util.Dom.getElementsByClassName(className, tagName, this.dom);
        var len = children.length;
        var ce = new Array(len);
        for(var i = 0; i < len; ++i){
            ce[i] = YAHOO.ext.Element.get(children[i], true);
        }
        return ce;
    },

    /**
     * Tests various css rules/browsers to determine if this element uses a border box
     * @return {Boolean}
     */
    isBorderBox : function(){
        if(typeof this.bbox == 'undefined'){
            var el = this.dom;
            var b = YAHOO.ext.util.Browser;
            var strict = YAHOO.ext.Strict;
            this.bbox = ((b.isIE && !strict && el.style.boxSizing != 'content-box') ||
               (b.isGecko && YAHOO.util.Dom.getStyle(el, "-moz-box-sizing") == 'border-box') ||
               (!b.isSafari && YAHOO.util.Dom.getStyle(el, "box-sizing") == 'border-box'));
        }
        return this.bbox;
    },

    /**
     * Return a box {x, y, width, height} that can be used to set another elements
     * size/location to match this element.
     * @param {Boolean} contentBox (optional) If true a box for the content of the element is returned.
     * @param {Boolean} local (optional) If true the element's left and top are returned instead of page x/y.
     * @return {Object}
     */
    getBox : function(contentBox, local){
        var xy;
        if(!local){
            xy = this.getXY();
        }else{
            var left = parseInt(YAHOO.util.Dom.getStyle('left'), 10) || 0;
            var top = parseInt(YAHOO.util.Dom.getStyle('top'), 10) || 0;
            xy = [left, top];
        }
        var el = this.dom;
        var w = el.offsetWidth;
        var h = el.offsetHeight;
        if(!contentBox){
            return {x: xy[0], y: xy[1], width: w, height: h};
        }else{
            var l = this.getBorderWidth('l')+this.getPadding('l');
            var r = this.getBorderWidth('r')+this.getPadding('r');
            var t = this.getBorderWidth('t')+this.getPadding('t');
            var b = this.getBorderWidth('b')+this.getPadding('b');
            return {x: xy[0]+l, y: xy[1]+t, width: w-(l+r), height: h-(t+b)};
        }
    },

    /**
     * Sets the element's box. Use getBox() on another element to get a box obj. If animate is true then width, height, x and y will be animated concurrently.
     * @param {Object} box The box to fill {x, y, width, height}
     * @param {<i>Boolean</i>} adjust (optional) Whether to adjust for box-model issues automatically
     * @param {<i>Boolean</i>} animate (optional) Animate the transition (Default is false)
     * @param {<i>float</i>} duration (optional) How long the animation lasts. (Defaults to .35 seconds)
     * @param {<i>Function</i>} onComplete (optional) Function to call when animation completes.
     * @param {<i>Function</i>} easing (optional) YAHOO.util.Easing method to use. (Defaults to YAHOO.util.Easing.easeBoth)
     * @return {YAHOO.ext.Element} this
     */
    setBox : function(box, adjust, animate, duration, onComplete, easing){
        var w = box.width, h = box.height;
        if((adjust && !this.autoBoxAdjust) && !this.isBorderBox()){
           w -= (this.getBorderWidth('lr') + this.getPadding('lr'));
           h -= (this.getBorderWidth('tb') + this.getPadding('tb'));
        }
        this.setBounds(box.x, box.y, w, h, animate, duration, onComplete, easing);
        return this;
    },

    /**
     * Forces the browser to repaint this element
     * @return {YAHOO.ext.Element} this
     */
     repaint : function(){
        var dom = this.dom;
        YAHOO.util.Dom.addClass(dom, 'yui-ext-repaint');
        setTimeout(function(){
            YAHOO.util.Dom.removeClass(dom, 'yui-ext-repaint');
        }, 1);
        return this;
    },

    /**
     * Returns an object with properties top, left, right and bottom representing the margins of this element unless sides is passed,
     * then it returns the calculated width of the sides (see getPadding)
     * @param {String} sides (optional) Any combination of l, r, t, b to get the sum of those sides
     * @return {Object/Number}
     */
    getMargins : function(side){
        if(!side){
            return {
                top: parseInt(this.getStyle('margin-top'), 10) || 0,
                left: parseInt(this.getStyle('margin-left'), 10) || 0,
                bottom: parseInt(this.getStyle('margin-bottom'), 10) || 0,
                right: parseInt(this.getStyle('margin-right'), 10) || 0
            };
        }else{
            return this.addStyles(side, YAHOO.ext.Element.margins);
         }
    },

    addStyles : function(sides, styles){
        var val = 0;
        for(var i = 0, len = sides.length; i < len; i++){
             var w = parseInt(this.getStyle(styles[sides.charAt(i)]), 10);
             if(!isNaN(w)) val += w;
        }
        return val;
    },

    /**
     * Creates a proxy element of this element
     * @param {String/Object} config The class name of the proxy element or a DomHelper config object
     * @param {<i>String/HTMLElement</i>} renderTo (optional) The element or element id to render the proxy to (defaults to document.body)
     * @param {<i>Boolean</i>} matchBox (optional) True to align and size the proxy to this element now (defaults to false)
     * @return {YAHOO.ext.Element} The new proxy element
     */
    createProxy : function(config, renderTo, matchBox){
        if(renderTo){
            renderTo = YAHOO.util.Dom.get(renderTo);
        }else{
            renderTo = document.body;
        }
        config = typeof config == 'object' ?
            config : {tag : 'div', cls: config};
        var proxy = YAHOO.ext.DomHelper.append(renderTo, config, true);
        if(matchBox){
           proxy.setBox(this.getBox());
        }
        return proxy;
    },

    /**
     * Creates an iframe shim for this element to keep selects and other windowed objects from
     * showing through.
     * @return {YAHOO.ext.Element} The new shim element
     */
    createShim : function(){
        var config = {
            tag : 'iframe',
            frameBorder:'no',
            cls: 'yiframe-shim',
            style: 'position:absolute;visibility:hidden;left:0;top:0;overflow:hidden;',
            src: YAHOO.ext.SSL_SECURE_URL
        };
        var shim = YAHOO.ext.DomHelper.append(this.dom.parentNode, config, true);
        shim.setBox(this.getBox());
        return shim;
    },

    /**
     * Removes this element from the DOM and deletes it from the cache
     */
    remove : function(){
        this.dom.parentNode.removeChild(this.dom);
        delete YAHOO.ext.Element.cache[this.dom.id];
    },

    /**
     * Sets up event handlers to add and remove a css class when the mouse is over this element
     * @param {String} className
     * @return {YAHOO.ext.Element} this
     */
    addClassOnOver : function(className){
        this.on('mouseover', function(){
            this.addClass(className);
        }, this, true);
        this.on('mouseout', function(){
            this.removeClass(className);
        }, this, true);
        return this;
    },

    /**
     * Stops the specified event from bubbling and optionally prevent's the default action
     * @param {String} eventName
     * @param {Boolean} preventDefault (optional) true to prevent the default action too
     * @return {YAHOO.ext.Element} this
     */
    swallowEvent : function(eventName, preventDefault){
        var fn = function(e){
            e.stopPropagation();
            if(preventDefault){
                e.preventDefault();
            }
        };
        this.mon(eventName, fn);
        return this;
    },

    /**
     * Sizes this element to it's parent element's dimensions performing
     * neccessary box adjustments.
     * @param {Boolean} monitorResize (optional) If true maintains the fit when the browser window is resized.
     * @return {YAHOO.ext.Element} this
     */
    fitToParent : function(monitorResize){
        var p = getEl(this.dom.parentNode, true);
        p.beginMeasure(); // in case parent is display:none
        var box = p.getBox(true, true);
        p.endMeasure();
        this.setSize(box.width, box.height);
        if(monitorResize === true){
            YAHOO.ext.EventManager.onWindowResize(this.fitToParent, this, true);
        }
        return this;
    },

    /**
     * Gets the next sibling, skipping text nodes
     * @return {HTMLElement} The next sibling or null
	 */
    getNextSibling : function(){
        var n = this.dom.nextSibling;
        while(n && n.nodeType != 1){
            n = n.nextSibling;
        }
        return n;
    },

    /**
     * Gets the previous sibling, skipping text nodes
     * @return {HTMLElement} The previous sibling or null
	 */
    getPrevSibling : function(){
        var n = this.dom.previousSibling;
        while(n && n.nodeType != 1){
            n = n.previousSibling;
        }
        return n;
    },


    /**
     * Appends the passed element(s) to this element
     * @param {String/HTMLElement/Array/Element/CompositeElement} el
     * @return {YAHOO.ext.Element} this
     */
    appendChild: function(el){
        el = getEl(el);
        el.appendTo(this);
        return this;
    },

    /**
     * Creates the passed DomHelper config and appends it to this element or optionally inserts it before the passed child element.
     * @param {Object} config DomHelper element config object
     * @param {<i>HTMLElement</i>} insertBefore (optional) a child element of this element
     * @return {YAHOO.ext.Element} The new child element
     */
    createChild: function(config, insertBefore){
        var c;
        if(insertBefore){
            c = YAHOO.ext.DomHelper.insertBefore(insertBefore, config, true);
        }else{
            c = YAHOO.ext.DomHelper.append(this.dom, config, true);
        }
        return c;
    },

    /**
     * Appends this element to the passed element
     * @param {String/HTMLElement/Element} el The new parent element
     * @return {YAHOO.ext.Element} this
     */
    appendTo: function(el){
        var node = getEl(el).dom;
        node.appendChild(this.dom);
        return this;
    },

    /**
     * Inserts this element before the passed element in the DOM
     * @param {String/HTMLElement/Element} el The element to insert before
     * @return {YAHOO.ext.Element} this
     */
    insertBefore: function(el){
        var node = getEl(el).dom;
        node.parentNode.insertBefore(this.dom, node);
        return this;
    },

    /**
     * Inserts this element after the passed element in the DOM
     * @param {String/HTMLElement/Element} el The element to insert after
     * @return {YAHOO.ext.Element} this
     */
    insertAfter: function(el){
        var node = getEl(el).dom;
        node.parentNode.insertBefore(this.dom, node.nextSibling);
        return this;
    },

    /**
     * Creates and wraps this element with another element
     * @param {Object} config (optional) DomHelper element config object for the wrapper element or null for an empty div
     * @return {Element} The newly created wrapper element
     */
    wrap: function(config){
        if(!config){
            config = {tag: 'div'};
        }
        var newEl = YAHOO.ext.DomHelper.insertBefore(this.dom, config, true);
        newEl.dom.appendChild(this.dom);
        return newEl;
    },

    /**
     * Replaces the passed element with this element
     * @param {String/HTMLElement/Element} el The element to replace
     * @return {YAHOO.ext.Element} this
     */
    replace: function(el){
        el = getEl(el);
        this.insertBefore(el);
        el.remove();
        return this;
    },

    /**
     * Inserts an html fragment into this element
     * @param {String} where Where to insert the html in relation to the this element - beforeBegin, afterBegin, beforeEnd, afterEnd.
     * @param {String} html The HTML fragment
     * @return {YAHOO.ext.Element} this
     */
    insertHtml : function(where, html){
        YAHOO.ext.DomHelper.insertHtml(where, this.dom, html);
        return this;
    },

    /**
     * Sets the passed attributes as attributes of this element (a style attribute can be a string, object or function)
     * @param {Object} o The object with the attributes
     * @return {YAHOO.ext.Element} this
     */
    set : function(o){
        var el = this.dom;
        var useSet = el.setAttribute ? true : false;
        for(var attr in o){
            if(attr == 'style' || typeof o[attr] == 'function') continue;
            if(attr=='cls'){
                el.className = o['cls'];
            }else{
                if(useSet) el.setAttribute(attr, o[attr]);
                else el[attr] = o[attr];
            }
        }
        YAHOO.ext.DomHelper.applyStyles(el, o.style);
        return this;
    },

    /**
     * Convenience method for constructing a KeyMap
     * @param {Number/Array/Object/String} key Either a string with the keys to listen for, the numeric key code, array of key codes or an object with the following options:
     *                                  {key: (number or array), shift: (true/false), ctrl: (true/false), alt: (true/false)}
     * @param {Function} fn The function to call
     * @param {Object} scope (optional) The scope of the function
     * @return {YAHOO.ext.KeyMap} The KeyMap created
     */
    addKeyListener : function(key, fn, scope){
        var config;
        if(typeof key != 'object' || key instanceof Array){
            config = {
                key: key,
                fn: fn,
                scope: scope
            };
        }else{
            config = {
                key : key.key,
                shift : key.shift,
                ctrl : key.ctrl,
                alt : key.alt,
                fn: fn,
                scope: scope
            };
        }
        var map = new YAHOO.ext.KeyMap(this, config);
        return map;
    },

    /**
     * Creates a KeyMap for this element
     * @param {Object} config The KeyMap config. See {@link YAHOO.ext.KeyMap} for more details
     * @return {YAHOO.ext.KeyMap} The KeyMap created
     */
    addKeyMap : function(config){
        return new YAHOO.ext.KeyMap(this, config);
    }
};

/**
 * true to automatically adjust width and height settings for box-model issues (default to true)
 */
YAHOO.ext.Element.prototype.autoBoxAdjust = true;
/**
 * true to automatically detect display mode and use display instead of visibility with show()/hide() (defaults to false).
 * To enable this globally:<pre><code>YAHOO.ext.Element.prototype.autoDisplayMode = true;</code></pre>
 */
YAHOO.ext.Element.prototype.autoDisplayMode = true;

YAHOO.ext.Element.unitPattern = /\d+(px|em|%|en|ex|pt|in|cm|mm|pc)$/i;
/**
 * Visibility mode constant - Use visibility to hide element
 * @static
 * @type Number
 */
YAHOO.ext.Element.VISIBILITY = 1;
/**
 * Visibility mode constant - Use display to hide element
 * @static
 * @type Number
 */
YAHOO.ext.Element.DISPLAY = 2;

YAHOO.ext.Element.blockElements = /^(?:address|blockquote|center|dir|div|dl|fieldset|form|h\d|hr|isindex|menu|ol|ul|p|pre|table|dd|dt|li|tbody|tr|td|thead|tfoot|iframe)$/i;
YAHOO.ext.Element.borders = {l: 'border-left-width', r: 'border-right-width', t: 'border-top-width', b: 'border-bottom-width'};
YAHOO.ext.Element.paddings = {l: 'padding-left', r: 'padding-right', t: 'padding-top', b: 'padding-bottom'};
YAHOO.ext.Element.margins = {l: 'margin-left', r: 'margin-right', t: 'margin-top', b: 'margin-bottom'};

/**
 * @private Call out to here so we make minimal closure
 */
YAHOO.ext.Element.createStopHandler = function(stopPropagation, handler, scope, override){
    return function(e){
        if(e){
            if(stopPropagation){
                YAHOO.util.Event.stopEvent(e);
            }else {
                YAHOO.util.Event.preventDefault(e);
            }
        }
        handler.call(override && scope ? scope : window, e, scope);
    };
};

/**
 * @private
 */
YAHOO.ext.Element.cache = {};

/**
 * Static method to retreive Element objects. Uses simple caching to consistently return the same object.
 * Automatically fixes if an object was recreated with the same id via AJAX or DOM.
 * @param {String/HTMLElement/Element} el The id of the element or the element to wrap (must have an id). If you pass in an element, it is returned
 * @param {<i>Boolean</i>} autoGenerateId (optional) Set this flag to true if you are passing an element without an id (like document.body). It will auto generate an id if one isn't present.
 * @return {Element} The element object
 * @static
 */
YAHOO.ext.Element.get = function(el, autoGenerateId){
    if(!el){ return null; }
    autoGenerateId = true; // now generates id by default
    if(el instanceof YAHOO.ext.Element){
        el.dom = YAHOO.util.Dom.get(el.id); // refresh dom element in case no longer valid
        YAHOO.ext.Element.cache[el.id] = el; // in case it was created directly with Element(), let's cache it
        return el;
    }else if(el.isComposite){
        return el;
    }else if(el instanceof Array){
        return YAHOO.ext.Element.select(el);
    }else if(el === document){
        // create a bogus element object representing the document object
        if(!YAHOO.ext.Element.cache['__ydocument']){
            var docEl = function(){};
            docEl.prototype = YAHOO.ext.Element.prototype;
            var o = new docEl();
            o.dom = document;
            YAHOO.ext.Element.cache['__ydocument'] = o;
        }
        return YAHOO.ext.Element.cache['__ydocument'];
    }
    var key = el;
    if(typeof el != 'string'){ // must be an element
        if(!el.id && !autoGenerateId){ return null; }
        YAHOO.util.Dom.generateId(el, 'elgen-');
        key = el.id;
    }
    var element = YAHOO.ext.Element.cache[key];
    if(!element){
        element = new YAHOO.ext.Element(key);
        if(!element.dom) return null;
        YAHOO.ext.Element.cache[key] = element;
    }else{
        element.dom = YAHOO.util.Dom.get(key);
    }
    return element;
};

/**
 * Shorthand function for YAHOO.ext.Element.get()
 */
var getEl = YAHOO.ext.Element.get;

// clean up refs
YAHOO.util.Event.addListener(window, 'unload', function(){
    YAHOO.ext.Element.cache = null;
});

/**
 * @class YAHOO.ext.DomHelper
 * Utility class for working with DOM and/or Templates. It transparently supports using HTML fragments or DOM.
 * For more information see <a href="http://www.jackslocum.com/yui/2006/10/06/domhelper-create-elements-using-dom-html-fragments-or-templates/">this blog post with examples</a>.
 * @singleton
 */
YAHOO.ext.DomHelper = new function(){
    /**@private*/
    var d = document;
    var tempTableEl = null;
    /** True to force the use of DOM instead of html fragments @type Boolean */
    this.useDom = false;
    var emptyTags = /^(?:base|basefont|br|frame|hr|img|input|isindex|link|meta|nextid|range|spacer|wbr|audioscope|area|param|keygen|col|limittext|spot|tab|over|right|left|choose|atop|of)$/i;
    /**
     * Applies a style specification to an element
     * @param {String/HTMLElement} el The element to apply styles to
     * @param {String/Object/Function} styles A style specification string eg "width:100px", or object in the form {width:"100px"}, or
     * a function which returns such a specification.
     */
    this.applyStyles = function(el, styles){
        if(styles){
           var D = YAHOO.util.Dom;
           if (typeof styles == "string"){
               var re = /\s?([a-z\-]*)\:([^;]*);?/gi;
               var matches;
               while ((matches = re.exec(styles)) != null){
                   D.setStyle(el, matches[1], matches[2]);
               }
           }else if (typeof styles == "object"){
               for (var style in styles){
                  D.setStyle(el, style, styles[style]);
               }
           }else if (typeof styles == "function"){
                YAHOO.ext.DomHelper.applyStyles(el, styles.call());
           }
        }
    };

    // build as innerHTML where available
    /** @ignore */
    var createHtml = function(o){
        var b = '';
        b += '<' + o.tag;
        for(var attr in o){
            if(attr == 'tag' || attr == 'children' || attr == 'html' || typeof o[attr] == 'function') continue;
            if(attr == 'style'){
                var s = o['style'];
                if(typeof s == 'function'){
                    s = s.call();
                }
                if(typeof s == 'string'){
                    b += ' style="' + s + '"';
                }else if(typeof s == 'object'){
                    b += ' style="';
                    for(var key in s){
                        if(typeof s[key] != 'function'){
                            b += key + ':' + s[key] + ';';
                        }
                    }
                    b += '"';
                }
            }else{
                if(attr == 'cls'){
                    b += ' class="' + o['cls'] + '"';
                }else if(attr == 'htmlFor'){
                    b += ' for="' + o['htmlFor'] + '"';
                }else{
                    b += ' ' + attr + '="' + o[attr] + '"';
                }
            }
        }
        if(emptyTags.test(o.tag)){
            b += ' />';
        }else{
            b += '>';
            if(o.children){
                for(var i = 0, len = o.children.length; i < len; i++) {
                    b += createHtml(o.children[i], b);
                }
            }
            if(o.html){
                b += o.html;
            }
            b += '</' + o.tag + '>';
        }
        return b;
    }

    // build as dom
    /** @ignore */
    var createDom = function(o, parentNode){
        var el = d.createElement(o.tag);
        var useSet = el.setAttribute ? true : false; // In IE some elements don't have setAttribute
        for(var attr in o){
            if(attr == 'tag' || attr == 'children' || attr == 'html' || attr == 'style' || typeof o[attr] == 'function') continue;
            if(attr=='cls'){
                el.className = o['cls'];
            }else{
                if(useSet) el.setAttribute(attr, o[attr]);
                else el[attr] = o[attr];
            }
        }
        YAHOO.ext.DomHelper.applyStyles(el, o.style);
        if(o.children){
            for(var i = 0, len = o.children.length; i < len; i++) {
             	createDom(o.children[i], el);
            }
        }
        if(o.html){
            el.innerHTML = o.html;
        }
        if(parentNode){
           parentNode.appendChild(el);
        }
        return el;
    };

    /**
     * @ignore
     * Nasty code for IE's broken table implementation
     */
    var insertIntoTable = function(tag, where, el, html){
        if(!tempTableEl){
            tempTableEl = document.createElement('div');
        }
        var node;
        if(tag == 'table' || tag == 'tbody'){
           tempTableEl.innerHTML = '<table><tbody>'+html+'</tbody></table>';
           node = tempTableEl.firstChild.firstChild.firstChild;
        }else{
           tempTableEl.innerHTML = '<table><tbody><tr>'+html+'</tr></tbody></table>';
           node = tempTableEl.firstChild.firstChild.firstChild.firstChild;
        }
        if(where == 'beforebegin'){
            el.parentNode.insertBefore(node, el);
            return node;
        }else if(where == 'afterbegin'){
            el.insertBefore(node, el.firstChild);
            return node;
        }else if(where == 'beforeend'){
            el.appendChild(node);
            return node;
        }else if(where == 'afterend'){
            el.parentNode.insertBefore(node, el.nextSibling);
            return node;
        }
    }

    /**
     * Inserts an HTML fragment into the Dom
     * @param {String} where Where to insert the html in relation to el - beforeBegin, afterBegin, beforeEnd, afterEnd.
     * @param {HTMLElement} el The context element
     * @param {String} html The HTML fragmenet
     * @return {HTMLElement} The new node
     */
    this.insertHtml = function(where, el, html){
        where = where.toLowerCase();
        if(el.insertAdjacentHTML){
            var tag = el.tagName.toLowerCase();
            if(YAHOO.ext.util.Browser.isIE && (tag == 'table' || tag == 'tbody' || tag == 'tr')){
               return insertIntoTable(tag, where, el, html);
            }
            switch(where){
                case 'beforebegin':
                    el.insertAdjacentHTML(where, html);
                    return el.previousSibling;
                case 'afterbegin':
                    el.insertAdjacentHTML(where, html);
                    return el.firstChild;
                case 'beforeend':
                    el.insertAdjacentHTML(where, html);
                    return el.lastChild;
                case 'afterend':
                    el.insertAdjacentHTML(where, html);
                    return el.nextSibling;
            }
            throw 'Illegal insertion point -> "' + where + '"';
        }
        var range = el.ownerDocument.createRange();
        var frag;
        switch(where){
             case 'beforebegin':
                range.setStartBefore(el);
                frag = range.createContextualFragment(html);
                el.parentNode.insertBefore(frag, el);
                return el.previousSibling;
             case 'afterbegin':
                if(el.firstChild){ // faster
                    range.setStartBefore(el.firstChild);
                }else{
                    range.selectNodeContents(el);
                    range.collapse(true);
                }
                frag = range.createContextualFragment(html);
                el.insertBefore(frag, el.firstChild);
                return el.firstChild;
            case 'beforeend':
                if(el.lastChild){
                    range.setStartAfter(el.lastChild); // faster
                }else{
                    range.selectNodeContents(el);
                    range.collapse(false);
                }
                frag = range.createContextualFragment(html);
                el.appendChild(frag);
                return el.lastChild;
            case 'afterend':
                range.setStartAfter(el);
                frag = range.createContextualFragment(html);
                el.parentNode.insertBefore(frag, el.nextSibling);
                return el.nextSibling;
            }
            throw 'Illegal insertion point -> "' + where + '"';
    };

    /**
     * Creates new Dom element(s) and inserts them before el
     * @param {HTMLElement} el The context element
     * @param {Object} o The Dom object spec (and children)
     * @param {<i>Boolean</i>} returnElement (optional) true to return a YAHOO.ext.Element
     * @return {HTMLElement} The new node
     */
    this.insertBefore = function(el, o, returnElement){
        el = YAHOO.util.Dom.get(el);
        var newNode;
        if(this.useDom){
            newNode = createDom(o, null);
            el.parentNode.insertBefore(newNode, el);
        }else{
            var html = createHtml(o);
            newNode = this.insertHtml('beforeBegin', el, html);
        }
        return returnElement ? YAHOO.ext.Element.get(newNode, true) : newNode;
    };

    /**
     * Creates new Dom element(s) and inserts them after el
     * @param {HTMLElement} el The context element
     * @param {Object} o The Dom object spec (and children)
     * @param {<i>Boolean</i>} returnElement (optional) true to return a YAHOO.ext.Element
     * @return {HTMLElement} The new node
     */
    this.insertAfter = function(el, o, returnElement){
        el = YAHOO.util.Dom.get(el);
        var newNode;
        if(this.useDom){
            newNode = createDom(o, null);
            el.parentNode.insertBefore(newNode, el.nextSibling);
        }else{
            var html = createHtml(o);
            newNode = this.insertHtml('afterEnd', el, html);
        }
        return returnElement ? YAHOO.ext.Element.get(newNode, true) : newNode;
    };

    /**
     * Creates new Dom element(s) and appends them to el
     * @param {HTMLElement} el The context element
     * @param {Object} o The Dom object spec (and children)
     * @param {<i>Boolean</i>} returnElement (optional) true to return a YAHOO.ext.Element
     * @return {HTMLElement} The new node
     */
    this.append = function(el, o, returnElement){
        el = YAHOO.util.Dom.get(el);
        var newNode;
        if(this.useDom){
            newNode = createDom(o, null);
            el.appendChild(newNode);
        }else{
            var html = createHtml(o);
            newNode = this.insertHtml('beforeEnd', el, html);
        }
        return returnElement ? YAHOO.ext.Element.get(newNode, true) : newNode;
    };

    /**
     * Creates new Dom element(s) and overwrites the contents of el with them
     * @param {HTMLElement} el The context element
     * @param {Object} o The Dom object spec (and children)
     * @param {<i>Boolean</i>} returnElement (optional) true to return a YAHOO.ext.Element
     * @return {HTMLElement} The new node
     */
    this.overwrite = function(el, o, returnElement){
        el = YAHOO.util.Dom.get(el);
        el.innerHTML = createHtml(o);
        return returnElement ? YAHOO.ext.Element.get(el.firstChild, true) : el.firstChild;
    };

    /**
     * Creates a new YAHOO.ext.DomHelper.Template from the Dom object spec
     * @param {Object} o The Dom object spec (and children)
     * @return {YAHOO.ext.DomHelper.Template} The new template
     */
    this.createTemplate = function(o){
        var html = createHtml(o);
        return new YAHOO.ext.DomHelper.Template(html);
    };
}();

/**
* @class YAHOO.ext.DomHelper.Template
* Represents an HTML fragment template.
* For more information see <a href="http://www.jackslocum.com/yui/2006/10/06/domhelper-create-elements-using-dom-html-fragments-or-templates/">this blog post with examples</a>.
* <br>
* <b>This class is also available as YAHOO.ext.Template</b>.
* @constructor
* @param {String} html The HTML fragment
*/
YAHOO.ext.DomHelper.Template = function(html){
    /**@private*/
    this.html = html;
};
YAHOO.ext.DomHelper.Template.prototype = {
    /**
     * Returns an HTML fragment of this template with the specified values applied
     * @param {Object} values The template values. Can be an array if your params are numeric (i.e. {0}) or an object (i.e. {foo: 'bar'})
     * @return {String}
     */
    applyTemplate : function(values){
        if(this.compiled){
            return this.compiled(values);
        }
        var empty = '';
        var fn = function(match, index){
            if(typeof values[index] != 'undefined'){
                return values[index];
            }else{
                return empty;
            }
        }
        return this.html.replace(this.re, fn);
    },

    /**
    * The regular expression used to match template variables
    * @type RegExp
    * @property
    */
    re : /\{(\w+)\}/g,

    /**
     * Compiles the template into an internal function, eliminating the RegEx overhead
     */
    compile : function(){
        var html = this.html;
        var re = this.re;
        var body = [];
        body.push("this.compiled = function(values){ return [");
        var result;
        var lastMatchEnd = 0;
        while ((result = re.exec(html)) != null){
            body.push("'", html.substring(lastMatchEnd, result.index), "', ");
            body.push("values['", html.substring(result.index+1,re.lastIndex-1), "'], ");
            lastMatchEnd = re.lastIndex;
        }
        body.push("'", html.substr(lastMatchEnd), "'].join('');};");
        eval(body.join(''));
    },

    /**
     * Applies the supplied values to the template and inserts the new node(s) before el
     * @param {HTMLElement} el The context element
     * @param {Object} values The template values. Can be an array if your params are numeric (i.e. {0}) or an object (i.e. {foo: 'bar'})
     * @param {<i>Boolean</i>} returnElement (optional) true to return a YAHOO.ext.Element
     * @return {HTMLElement} The new node
     */
    insertBefore: function(el, values, returnElement){
        el = YAHOO.util.Dom.get(el);
        var newNode = YAHOO.ext.DomHelper.insertHtml('beforeBegin', el, this.applyTemplate(values));
        return returnElement ? YAHOO.ext.Element.get(newNode, true) : newNode;
    },

    /**
     * Applies the supplied values to the template and inserts the new node(s) after el
     * @param {HTMLElement} el The context element
     * @param {Object} values The template values. Can be an array if your params are numeric (i.e. {0}) or an object (i.e. {foo: 'bar'})
     * @param {<i>Boolean</i>} returnElement (optional) true to return a YAHOO.ext.Element
     * @return {HTMLElement} The new node
     */
    insertAfter : function(el, values, returnElement){
        el = YAHOO.util.Dom.get(el);
        var newNode = YAHOO.ext.DomHelper.insertHtml('afterEnd', el, this.applyTemplate(values));
        return returnElement ? YAHOO.ext.Element.get(newNode, true) : newNode;
    },

    /**
     * Applies the supplied values to the template and append the new node(s) to el
     * @param {HTMLElement} el The context element
     * @param {Object} values The template values. Can be an array if your params are numeric (i.e. {0}) or an object (i.e. {foo: 'bar'})
     * @param {<i>Boolean</i>} returnElement (optional) true to return a YAHOO.ext.Element
     * @return {HTMLElement} The new node
     */
    append : function(el, values, returnElement){
        el = YAHOO.util.Dom.get(el);
        var newNode = YAHOO.ext.DomHelper.insertHtml('beforeEnd', el, this.applyTemplate(values));
        return returnElement ? YAHOO.ext.Element.get(newNode, true) : newNode;
    },

    /**
     * Applies the supplied values to the template and overwrites the content of el with the new node(s)
     * @param {HTMLElement} el The context element
     * @param {Object} values The template values. Can be an array if your params are numeric (i.e. {0}) or an object (i.e. {foo: 'bar'})
     * @param {<i>Boolean</i>} returnElement (optional) true to return a YAHOO.ext.Element
     * @return {HTMLElement} The new node
     */
    overwrite : function(el, values, returnElement){
        el = YAHOO.util.Dom.get(el);
        el.innerHTML = '';
        var newNode = YAHOO.ext.DomHelper.insertHtml('beforeEnd', el, this.applyTemplate(values));
        return returnElement ? YAHOO.ext.Element.get(newNode, true) : newNode;
    }
};

YAHOO.ext.Template = YAHOO.ext.DomHelper.Template;

/*
 * yui-ext 0.33
 * Copyright(c) 2006, Jack Slocum.
 */
YAHOO.namespace('ext');
/**
 * @class YAHOO.ext.CompositeElement
 * Standard composite class. Creates a YAHOO.ext.Element for every element in the collection.
 * <br><br>
 * <b>NOTE: Although they are not listed, this class supports all of the set/update methods of YAHOO.ext.Element. All YAHOO.ext.Element
 * actions will be performed on all the elements in this collection.</b>
 * <br><br>
 * All methods return <i>this</i> and can be chained.
 <pre><code>
 var els = getEls('#some-el div.some-class');
 // or
 var els = YAHOO.ext.Element.select('#some-el div.some-class');
 els.setWidth(100); // all elements become 100 width
 els.hide(true); // all elements fade out and hide
 // or
 els.setWidth(100).hide(true);
 </code></pre>
 */
YAHOO.ext.CompositeElement = function(els){
    this.elements = [];
    this.addElements(els);
};
YAHOO.ext.CompositeElement.prototype = {
    isComposite: true,
    addElements : function(els){
        if(!els) return this;
        var yels = this.elements;
        var index = yels.length-1;
        for(var i = 0, len = els.length; i < len; i++) {
        	yels[++index] = getEl(els[i], true);
        }
        return this;
    },
    invoke : function(fn, args){
        var els = this.elements;
        for(var i = 0, len = els.length; i < len; i++) {
        	YAHOO.ext.Element.prototype[fn].apply(els[i], args);
        }
        return this;
    },
    /**
    * Adds elements to this composite.
    * @param {String/Array} els A string CSS selector, an array of elements or an element
    * @return {CompositeElement} this
    */
    add : function(els){
        if(typeof els == 'string'){
            this.addElements(YAHOO.ext.Element.selectorFunction(string));
        }else if(els instanceof Array){
            this.addElements(els);
        }else{
            this.addElements([els]);
        }
        return this;
    },
    /**
    * Calls the passed function passing (el, this, index) for each element in this composite.
    * @param {Function} fn The function to call
    * @param {Object} scope (optional) The <i>this</i> object (defaults to the element)
    * @return {CompositeElement} this
    */
    each : function(fn, scope){
        var els = this.elements;
        for(var i = 0, len = els.length; i < len; i++){
            fn.call(scope || els[i], els[i], this, i);
        }
        return this;
    }
};
/**
 * @class YAHOO.ext.CompositeElementLite
 * @extends YAHOO.ext.CompositeElement
 * Flyweight composite class. Reuses the same YAHOO.ext.Element for element operations.
 * <br><br>
 * <b>NOTE: Although they are not listed, this class supports all of the set/update methods of YAHOO.ext.Element. All YAHOO.ext.Element
 * actions will be performed on all the elements in this collection.</b>
 */
YAHOO.ext.CompositeElementLite = function(els){
    YAHOO.ext.CompositeElementLite.superclass.constructor.call(this, els);
    this.el = YAHOO.ext.Element.get(this.elements[0], true);
};
YAHOO.lang.extend(YAHOO.ext.CompositeElementLite, YAHOO.ext.CompositeElement, {
    addElements : function(els){
        if(els){
            this.elements = this.elements.concat(els);
        }
        return this;
    },
    invoke : function(fn, args){
        var els = this.elements;
        var el = this.el;
        for(var i = 0, len = els.length; i < len; i++) {
            el.dom = els[i];
        	YAHOO.ext.Element.prototype[fn].apply(el, args);
        }
        return this;
    }
});
YAHOO.ext.CompositeElement.createCall = function(proto, fnName){
    if(!proto[fnName]){
        proto[fnName] = function(){
            return this.invoke(fnName, arguments);
        };
    }
};
for(var fnName in YAHOO.ext.Element.prototype){
    if(typeof YAHOO.ext.Element.prototype[fnName] == 'function'){
        YAHOO.ext.CompositeElement.createCall(YAHOO.ext.CompositeElement.prototype, fnName);
    }
}
if(typeof cssQuery == 'function'){// Dean Edwards cssQuery
    YAHOO.ext.Element.selectorFunction = cssQuery;
}else if(typeof document.getElementsBySelector == 'function'){ // Simon Willison's getElementsBySelector
    YAHOO.ext.Element.selectorFunction = document.getElementsBySelector.createDelegate(document);
}
/**
 * @member YAHOO.ext.Element
* Selects elements based on the passed CSS selector to enable working on them as 1.
* @param {String/Array} selector The CSS selector or an array of elements
* @param {Boolean} unique (optional) true to create a unique YAHOO.ext.Element for each element (defaults to a shared flyweight object)
* @return {CompositeElementLite/CompositeElement}
* @method @static
*/
YAHOO.ext.Element.select = function(selector, unique){
    var els;
    if(typeof selector == 'string'){
        els = YAHOO.ext.Element.selectorFunction(selector);
    }else if(selector instanceof Array){
        els = selector;
    }else{
        throw 'Invalid selector';
    }
    if(unique === true){
        return new YAHOO.ext.CompositeElement(els);
    }else{
        return new YAHOO.ext.CompositeElementLite(els);
    }
};

var getEls = YAHOO.ext.Element.select;

/*
 * yui-ext 0.33
 * Copyright(c) 2006, Jack Slocum.
 */

/**
 * @class YAHOO.ext.util.CSS
 * Class for manipulating CSS Rules
 * @singleton
 */
YAHOO.ext.util.CSS = new function(){
	var rules = null;

   	var toCamel = function(property) {
      var convert = function(prop) {
         var test = /(-[a-z])/i.exec(prop);
         return prop.replace(RegExp.$1, RegExp.$1.substr(1).toUpperCase());
      };
      while(property.indexOf('-') > -1) {
         property = convert(property);
      }
      return property;
   };

   /**
    * Gets all css rules for the document
    * @param {Boolean} refreshCache true to refresh the internal cache
    * @return {Object} An object (hash) of rules indexed by selector
    */
   this.getRules = function(refreshCache){
   		if(rules == null || refreshCache){
   			rules = {};
   			var ds = document.styleSheets;
   			for(var i =0, len = ds.length; i < len; i++){
   			    try{
    		        var ss = ds[i];
    		        var ssRules = ss.cssRules || ss.rules;
    		        for(var j = ssRules.length-1; j >= 0; --j){
    		        	rules[ssRules[j].selectorText] = ssRules[j];
    		        }
   			    }catch(e){} // try catch for cross domain access issue
	        }
   		}
   		return rules;
   	};

   	/**
    * Gets an an individual CSS rule by selector(s)
    * @param {String/Array} selector The CSS selector or an array of selectors to try. The first selector that is found is returned.
    * @param {Boolean} refreshCache true to refresh the internal cache
    * @return {CSSRule} The CSS rule or null if one is not found
    */
   this.getRule = function(selector, refreshCache){
   		var rs = this.getRules(refreshCache);
   		if(!(selector instanceof Array)){
   		    return rs[selector];
   		}
   		for(var i = 0; i < selector.length; i++){
			if(rs[selector[i]]){
				return rs[selector[i]];
			}
		}
		return null;
   	};


   	/**
    * Updates a rule property
    * @param {String/Array} selector If it's an array it tries each selector until it finds one. Stops immediately once one is found.
    * @param {String} property The css property
    * @param {String} value The new value for the property
    * @return {Boolean} true if a rule was found and updated
    */
   this.updateRule = function(selector, property, value){
   		if(!(selector instanceof Array)){
   			var rule = this.getRule(selector);
   			if(rule){
   				rule.style[toCamel(property)] = value;
   				return true;
   			}
   		}else{
   			for(var i = 0; i < selector.length; i++){
   				if(this.updateRule(selector[i], property, value)){
   					return true;
   				}
   			}
   		}
   		return false;
   	};

   	/**
    * Applies a rule to an element without adding the class
    * @param {HTMLElement} el The element
    * @param {String/Array} selector If it's an array it tries each selector until it finds one. Stops immediately once one is found.
    * @return {Boolean} true if a rule was found and applied
    */
   this.apply = function(el, selector){
   		if(!(selector instanceof Array)){
   			var rule = this.getRule(selector);
   			if(rule){
   			    var s = rule.style;
   				for(var key in s){
   				    if(typeof s[key] != 'function'){
       					if(s[key] && String(s[key]).indexOf(':') < 0 && s[key] != 'false'){
       						try{el.style[key] = s[key];}catch(e){}
       					}
   				    }
   				}
   				return true;
   			}
   		}else{
   			for(var i = 0; i < selector.length; i++){
   				if(this.apply(el, selector[i])){
   					return true;
   				}
   			}
   		}
   		return false;
   	};

   	this.applyFirst = function(el, id, selector){
   		var selectors = [
   			'#' + id + ' ' + selector,
   			selector
   		];
   		return this.apply(el, selectors);
   	};

   	this.revert = function(el, selector){
   		if(!(selector instanceof Array)){
   			var rule = this.getRule(selector);
   			if(rule){
   				for(key in rule.style){
   					if(rule.style[key] && String(rule.style[key]).indexOf(':') < 0 && rule.style[key] != 'false'){
   						try{el.style[key] = '';}catch(e){}
   					}
   				}
   				return true;
   			}
   		}else{
   			for(var i = 0; i < selector.length; i++){
   				if(this.revert(el, selector[i])){
   					return true;
   				}
   			}
   		}
   		return false;
   	};

   	this.revertFirst = function(el, id, selector){
   		var selectors = [
   			'#' + id + ' ' + selector,
   			selector
   		];
   		return this.revert(el, selectors);
   	};
}();

YAHOO.namespace("ext.util")
/*
 * yui-ext 0.33
 * Copyright(c) 2006, Jack Slocum.
 */

YAHOO.ext.util.Bench = function(){
   this.timers = {};
   this.lastKey = null;
};
YAHOO.ext.util.Bench.prototype = {
   start : function(key){
       this.lastKey = key;
       this.timers[key] = {};
       this.timers[key].startTime = new Date().getTime();
   },

   stop : function(key){
       key = key || this.lastKey;
       this.timers[key].endTime = new Date().getTime();
   },

   getElapsed : function(key){
       key = key || this.lastKey;
       return this.timers[key].endTime - this.timers[key].startTime;
   },

   toString : function(html){
       var results = "";
       for(var key in this.timers){
           if(typeof this.timers[key] != 'function'){
               results += key + ":\t" + (this.getElapsed(key) / 1000) + " seconds\n";
           }
       }
       if(html){
           results = results.replace("\n", '<br>');
       }
       return results;
   },

   show : function(){
       alert(this.toString());
   }
};

/*
 * yui-ext 0.33
 * Copyright(c) 2006, Jack Slocum.
 */

/*
 * All the Date functions below are the excellent work of Baron Schwartz
 * They generate precompiled functions from date formats instead of parsing and processing
 * the format everytime you do something with a date.
 */
/** @ignore */
Date.parseFunctions = {count:0};
/** @ignore */
Date.parseRegexes = [];
/** @ignore */
Date.formatFunctions = {count:0};

/**
 * Formats a date given to the supplied format - the format syntax is the same as <a href="http://www.php.net/date">PHP's date() function</a>.
 */
Date.prototype.dateFormat = function(format) {
    if (Date.formatFunctions[format] == null) {
        Date.createNewFormat(format);
    }
    var func = Date.formatFunctions[format];
    return this[func]();
};

/**
 * Same as {@link #dateFormat}
 */
Date.prototype.format = Date.prototype.dateFormat;

/** @ignore */
Date.createNewFormat = function(format) {
    var funcName = "format" + Date.formatFunctions.count++;
    Date.formatFunctions[format] = funcName;
    var code = "Date.prototype." + funcName + " = function(){return ";
    var special = false;
    var ch = '';
    for (var i = 0; i < format.length; ++i) {
        ch = format.charAt(i);
        if (!special && ch == "\\") {
            special = true;
        }
        else if (special) {
            special = false;
            code += "'" + String.escape(ch) + "' + ";
        }
        else {
            code += Date.getFormatCode(ch);
        }
    }
    eval(code.substring(0, code.length - 3) + ";}");
};

/** @ignore */
Date.getFormatCode = function(character) {
    switch (character) {
    case "d":
        return "String.leftPad(this.getDate(), 2, '0') + ";
    case "D":
        return "Date.dayNames[this.getDay()].substring(0, 3) + ";
    case "j":
        return "this.getDate() + ";
    case "l":
        return "Date.dayNames[this.getDay()] + ";
    case "S":
        return "this.getSuffix() + ";
    case "w":
        return "this.getDay() + ";
    case "z":
        return "this.getDayOfYear() + ";
    case "W":
        return "this.getWeekOfYear() + ";
    case "F":
        return "Date.monthNames[this.getMonth()] + ";
    case "m":
        return "String.leftPad(this.getMonth() + 1, 2, '0') + ";
    case "M":
        return "Date.monthNames[this.getMonth()].substring(0, 3) + ";
    case "n":
        return "(this.getMonth() + 1) + ";
    case "t":
        return "this.getDaysInMonth() + ";
    case "L":
        return "(this.isLeapYear() ? 1 : 0) + ";
    case "Y":
        return "this.getFullYear() + ";
    case "y":
        return "('' + this.getFullYear()).substring(2, 4) + ";
    case "a":
        return "(this.getHours() < 12 ? 'am' : 'pm') + ";
    case "A":
        return "(this.getHours() < 12 ? 'AM' : 'PM') + ";
    case "g":
        return "((this.getHours() %12) ? this.getHours() % 12 : 12) + ";
    case "G":
        return "this.getHours() + ";
    case "h":
        return "String.leftPad((this.getHours() %12) ? this.getHours() % 12 : 12, 2, '0') + ";
    case "H":
        return "String.leftPad(this.getHours(), 2, '0') + ";
    case "i":
        return "String.leftPad(this.getMinutes(), 2, '0') + ";
    case "s":
        return "String.leftPad(this.getSeconds(), 2, '0') + ";
    case "O":
        return "this.getGMTOffset() + ";
    case "T":
        return "this.getTimezone() + ";
    case "Z":
        return "(this.getTimezoneOffset() * -60) + ";
    default:
        return "'" + String.escape(character) + "' + ";
    };
};

/**
 * Parses a date given the supplied format - the format syntax is the same as <a href="http://www.php.net/date">PHP's date() function</a>.
 */
Date.parseDate = function(input, format) {
    if (Date.parseFunctions[format] == null) {
        Date.createParser(format);
    }
    var func = Date.parseFunctions[format];
    return Date[func](input);
};

/** @ignore */
Date.createParser = function(format) {
    var funcName = "parse" + Date.parseFunctions.count++;
    var regexNum = Date.parseRegexes.length;
    var currentGroup = 1;
    Date.parseFunctions[format] = funcName;

    var code = "Date." + funcName + " = function(input){\n"
        + "var y = -1, m = -1, d = -1, h = -1, i = -1, s = -1;\n"
        + "var d = new Date();\n"
        + "y = d.getFullYear();\n"
        + "m = d.getMonth();\n"
        + "d = d.getDate();\n"
        + "var results = input.match(Date.parseRegexes[" + regexNum + "]);\n"
        + "if (results && results.length > 0) {"
    var regex = "";

    var special = false;
    var ch = '';
    for (var i = 0; i < format.length; ++i) {
        ch = format.charAt(i);
        if (!special && ch == "\\") {
            special = true;
        }
        else if (special) {
            special = false;
            regex += String.escape(ch);
        }
        else {
            obj = Date.formatCodeToRegex(ch, currentGroup);
            currentGroup += obj.g;
            regex += obj.s;
            if (obj.g && obj.c) {
                code += obj.c;
            }
        }
    }

    code += "if (y > 0 && m >= 0 && d > 0 && h >= 0 && i >= 0 && s >= 0)\n"
        + "{return new Date(y, m, d, h, i, s);}\n"
        + "else if (y > 0 && m >= 0 && d > 0 && h >= 0 && i >= 0)\n"
        + "{return new Date(y, m, d, h, i);}\n"
        + "else if (y > 0 && m >= 0 && d > 0 && h >= 0)\n"
        + "{return new Date(y, m, d, h);}\n"
        + "else if (y > 0 && m >= 0 && d > 0)\n"
        + "{return new Date(y, m, d);}\n"
        + "else if (y > 0 && m >= 0)\n"
        + "{return new Date(y, m);}\n"
        + "else if (y > 0)\n"
        + "{return new Date(y);}\n"
        + "}return null;}";

    Date.parseRegexes[regexNum] = new RegExp("^" + regex + "$");
    eval(code);
};

/** @ignore */
Date.formatCodeToRegex = function(character, currentGroup) {
    switch (character) {
    case "D":
        return {g:0,
        c:null,
        s:"(?:Sun|Mon|Tue|Wed|Thu|Fri|Sat)"};
    case "j":
    case "d":
        return {g:1,
            c:"d = parseInt(results[" + currentGroup + "], 10);\n",
            s:"(\\d{1,2})"};
    case "l":
        return {g:0,
            c:null,
            s:"(?:" + Date.dayNames.join("|") + ")"};
    case "S":
        return {g:0,
            c:null,
            s:"(?:st|nd|rd|th)"};
    case "w":
        return {g:0,
            c:null,
            s:"\\d"};
    case "z":
        return {g:0,
            c:null,
            s:"(?:\\d{1,3})"};
    case "W":
        return {g:0,
            c:null,
            s:"(?:\\d{2})"};
    case "F":
        return {g:1,
            c:"m = parseInt(Date.monthNumbers[results[" + currentGroup + "].substring(0, 3)], 10);\n",
            s:"(" + Date.monthNames.join("|") + ")"};
    case "M":
        return {g:1,
            c:"m = parseInt(Date.monthNumbers[results[" + currentGroup + "]], 10);\n",
            s:"(Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec)"};
    case "n":
    case "m":
        return {g:1,
            c:"m = parseInt(results[" + currentGroup + "], 10) - 1;\n",
            s:"(\\d{1,2})"};
    case "t":
        return {g:0,
            c:null,
            s:"\\d{1,2}"};
    case "L":
        return {g:0,
            c:null,
            s:"(?:1|0)"};
    case "Y":
        return {g:1,
            c:"y = parseInt(results[" + currentGroup + "], 10);\n",
            s:"(\\d{4})"};
    case "y":
        return {g:1,
            c:"var ty = parseInt(results[" + currentGroup + "], 10);\n"
                + "y = ty > Date.y2kYear ? 1900 + ty : 2000 + ty;\n",
            s:"(\\d{1,2})"};
    case "a":
        return {g:1,
            c:"if (results[" + currentGroup + "] == 'am') {\n"
                + "if (h == 12) { h = 0; }\n"
                + "} else { if (h < 12) { h += 12; }}",
            s:"(am|pm)"};
    case "A":
        return {g:1,
            c:"if (results[" + currentGroup + "] == 'AM') {\n"
                + "if (h == 12) { h = 0; }\n"
                + "} else { if (h < 12) { h += 12; }}",
            s:"(AM|PM)"};
    case "g":
    case "G":
    case "h":
    case "H":
        return {g:1,
            c:"h = parseInt(results[" + currentGroup + "], 10);\n",
            s:"(\\d{1,2})"};
    case "i":
        return {g:1,
            c:"i = parseInt(results[" + currentGroup + "], 10);\n",
            s:"(\\d{2})"};
    case "s":
        return {g:1,
            c:"s = parseInt(results[" + currentGroup + "], 10);\n",
            s:"(\\d{2})"};
    case "O":
        return {g:0,
            c:null,
            s:"[+-]\\d{4}"};
    case "T":
        return {g:0,
            c:null,
            s:"[A-Z]{3}"};
    case "Z":
        return {g:0,
            c:null,
            s:"[+-]\\d{1,5}"};
    default:
        return {g:0,
            c:null,
            s:String.escape(character)};
    }
};

Date.prototype.getTimezone = function() {
    return this.toString().replace(
        /^.*? ([A-Z]{3}) [0-9]{4}.*$/, "$1").replace(
        /^.*?\(([A-Z])[a-z]+ ([A-Z])[a-z]+ ([A-Z])[a-z]+\)$/, "$1$2$3");
};

Date.prototype.getGMTOffset = function() {
    return (this.getTimezoneOffset() > 0 ? "-" : "+")
        + String.leftPad(Math.floor(this.getTimezoneOffset() / 60), 2, "0")
        + String.leftPad(this.getTimezoneOffset() % 60, 2, "0");
};

Date.prototype.getDayOfYear = function() {
    var num = 0;
    Date.daysInMonth[1] = this.isLeapYear() ? 29 : 28;
    for (var i = 0; i < this.getMonth(); ++i) {
        num += Date.daysInMonth[i];
    }
    return num + this.getDate() - 1;
};

Date.prototype.getWeekOfYear = function() {
    // Skip to Thursday of this week
    var now = this.getDayOfYear() + (4 - this.getDay());
    // Find the first Thursday of the year
    var jan1 = new Date(this.getFullYear(), 0, 1);
    var then = (7 - jan1.getDay() + 4);
    return String.leftPad(((now - then) / 7) + 1, 2, "0");
};

Date.prototype.isLeapYear = function() {
    var year = this.getFullYear();
    return ((year & 3) == 0 && (year % 100 || (year % 400 == 0 && year)));
};

Date.prototype.getFirstDayOfMonth = function() {
    var day = (this.getDay() - (this.getDate() - 1)) % 7;
    return (day < 0) ? (day + 7) : day;
};

Date.prototype.getLastDayOfMonth = function() {
    var day = (this.getDay() + (Date.daysInMonth[this.getMonth()] - this.getDate())) % 7;
    return (day < 0) ? (day + 7) : day;
};

Date.prototype.getDaysInMonth = function() {
    Date.daysInMonth[1] = this.isLeapYear() ? 29 : 28;
    return Date.daysInMonth[this.getMonth()];
};

/** @ignore */
Date.prototype.getSuffix = function() {
    switch (this.getDate()) {
        case 1:
        case 21:
        case 31:
            return "st";
        case 2:
        case 22:
            return "nd";
        case 3:
        case 23:
            return "rd";
        default:
            return "th";
    }
};

/** @ignore */
Date.daysInMonth = [31,28,31,30,31,30,31,31,30,31,30,31];

/**
 * Override these values for international dates, for example...
 * Date.monthNames = ['JanInYourLang', 'FebInYourLang', ...];
 */
Date.monthNames =
   ["January",
    "February",
    "March",
    "April",
    "May",
    "June",
    "July",
    "August",
    "September",
    "October",
    "November",
    "December"];

/**
 * Override these values for international dates, for example...
 * Date.dayNames = ['SundayInYourLang', 'MondayInYourLang', ...];
 */
Date.dayNames =
   ["Sunday",
    "Monday",
    "Tuesday",
    "Wednesday",
    "Thursday",
    "Friday",
    "Saturday"];

/** @ignore */
Date.y2kYear = 50;

/** @ignore */
Date.monthNumbers = {
    Jan:0,
    Feb:1,
    Mar:2,
    Apr:3,
    May:4,
    Jun:5,
    Jul:6,
    Aug:7,
    Sep:8,
    Oct:9,
    Nov:10,
    Dec:11};



/*
 * yui-ext 0.33
 * Copyright(c) 2006, Jack Slocum.
 */

/**
 * @class YAHOO.ext.util.JSON
 * Modified version of Douglas Crockford's json.js that doesn't
 * mess with the Object prototype
 * http://www.json.org/js.html
 * @singleton
 */
YAHOO.ext.util.JSON = new function(){
    var useHasOwn = {}.hasOwnProperty ? true : false;

    // crashes Safari in some instances
    //var validRE = /^("(\\.|[^"\\\n\r])*?"|[,:{}\[\]0-9.\-+Eaeflnr-u \n\r\t])+?$/;

    var pad = function(n) {
        return n < 10 ? '0' + n : n;
    };

    var m = {
        '\b': '\\b',
        '\t': '\\t',
        '\n': '\\n',
        '\f': '\\f',
        '\r': '\\r',
        '"' : '\\"',
        '\\': '\\\\'
    };

    var encodeString = function(s){
        if (/["\\\x00-\x1f]/.test(s)) {
            return '"' + s.replace(/([\x00-\x1f\\"])/g, function(a, b) {
                var c = m[b];
                if(c){
                    return c;
                }
                c = b.charCodeAt();
                return '\\u00' +
                    Math.floor(c / 16).toString(16) +
                    (c % 16).toString(16);
            }) + '"';
        }
        return '"' + s + '"';
    };

    var encodeArray = function(o){
        var a = ['['], b, i, l = o.length, v;
            for (i = 0; i < l; i += 1) {
                v = o[i];
                switch (typeof v) {
                    case 'undefined':
                    case 'function':
                    case 'unknown':
                        break;
                    default:
                        if (b) {
                            a.push(',');
                        }
                        a.push(v === null ? "null" : YAHOO.ext.util.JSON.encode(v));
                        b = true;
                }
            }
            a.push(']');
            return a.join('');
    };

    var encodeDate = function(o){
        return '"' + o.getFullYear() + '-' +
                pad(o.getMonth() + 1) + '-' +
                pad(o.getDate()) + 'T' +
                pad(o.getHours()) + ':' +
                pad(o.getMinutes()) + ':' +
                pad(o.getSeconds()) + '"';
    };

    /**
     * Encodes an Object, Array or other value
     * @param {Mixed} o The variable to encode
     * @return {String} The JSON string
     */
    this.encode = function(o){
        if(typeof o == 'undefined' || o === null){
            return 'null';
        }else if(o instanceof Array){
            return encodeArray(o);
        }else if(o instanceof Date){
            return encodeDate(o);
        }else if(typeof o == 'string'){
            return encodeString(o);
        }else if(typeof o == 'number'){
            return isFinite(o) ? String(o) : "null";
        }else if(typeof o == 'boolean'){
            return String(o);
        }else {
            var a = ['{'], b, i, v;
            for (var i in o) {
                if(!useHasOwn || o.hasOwnProperty(i)) {
                    v = o[i];
                    switch (typeof v) {
                    case 'undefined':
                    case 'function':
                    case 'unknown':
                        break;
                    default:
                        if(b){
                            a.push(',');
                        }
                        a.push(this.encode(i), ':',
                                v === null ? "null" : this.encode(v));
                        b = true;
                    }
                }
            }
            a.push('}');
            return a.join('');
        }
    };

    /**
     * Decodes (parses) a JSON string to an object. If the JSON is invalid, this function throws a SyntaxError.
     * @param {String} json The JSON string
     * @return {Object} The resulting object
     */
    this.decode = function(json){
        // although crockford had a good idea, this line crashes safari in some instances
        //try{
            //if(validRE.test(json)) {
                return eval('(' + json + ')');
           // }
       // }catch(e){
       // }
       // throw new SyntaxError("parseJSON");
    };
}();

/**
 * @class YAHOO.ext.util.MixedCollection
 * A Collection class that maintains both numeric indexes and keys and exposes events.<br>
 * @constructor
 * @param {Boolean} allowFunctions True if the addAll function should add function references
 * to the collection.
 */
YAHOO.ext.util.MixedCollection = function(allowFunctions){
    this.items = [];
    this.keys = [];
    this.events = {
        /**
         * @event clear
         * Fires when the collection is cleared.
         */
        'clear' : new YAHOO.util.CustomEvent('clear'),
        /**
         * @event add
         * Fires when an item is added to the collection.
         * @param {Number} index The index at which the item was added.
         * @param {Object} o The item added.
         * @param {String} key The key associated with the added item.
         */
        'add' : new YAHOO.util.CustomEvent('add'),
        /**
         * @event replace
         * Fires when an item is replaced in the collection.
         * @param {String} key he key associated with the new added.
         * @param {Object} old The item being replaced.
         * @param {Object} new The new item.
         */
        'replace' : new YAHOO.util.CustomEvent('replace'),
        /**
         * @event remove
         * Fires when an item is removed from the collection.
         * @param {Object} o The item being removed.
         * @param {String} key (optional) The key associated with the removed item.
         */
        'remove' : new YAHOO.util.CustomEvent('remove')
    }
    this.allowFunctions = allowFunctions === true;
};

YAHOO.lang.extend(YAHOO.ext.util.MixedCollection, YAHOO.ext.util.Observable, {
    allowFunctions : false,

/**
 * Adds an item to the collection.
 * @param {String} key The key to associate with the item
 * @param {Object} o The item to add.
 * @return {Object} The item added.
 */
    add : function(key, o){
        if(arguments.length == 1){
            o = arguments[0];
            key = this.getKey(o);
        }
        this.items.push(o);
        if(typeof key != 'undefined' && key != null){
            this.items[key] = o;
            this.keys.push(key);
        }
        this.fireEvent('add', this.items.length-1, o, key);
        return o;
    },

/**
  * MixedCollection has a generic way to fetch keys if you implement getKey.
    <pre><code>
    // normal way
    var mc = new YAHOO.ext.util.MixedCollection();
    mc.add(someEl.dom.id, someEl);
    mc.add(otherEl.dom.id, otherEl);
    //and so on

    // using getKey
    var mc = new YAHOO.ext.util.MixedCollection();
    mc.getKey = function(el){
       return el.dom.id;
    }
    mc.add(someEl);
    mc.add(otherEl);
    // etc
    </code>
 * @param o {Object} The item for which to find the key.
 * @return {Object} The key for the passed item.
 */
    getKey : function(o){
         return null;
    },

/**
 * Replaces an item in the collection.
 * @param {String} key The key associated with the item to replace, or the item to replace.
 * @param o {Object} o (optional) If the first parameter passed was a key, the item to associate with that key.
 * @return {Object}  The new item.
 */
    replace : function(key, o){
        if(arguments.length == 1){
            o = arguments[0];
            key = this.getKey(o);
        }
        if(typeof this.items[key] == 'undefined'){
            return this.add(key, o);
        }
        var old = this.items[key];
        if(typeof key == 'number'){ // array index key
            this.items[key] = o;
        }else{
            var index = this.indexOfKey(key);
            this.items[index] = o;
            this.items[key] = o;
        }
        this.fireEvent('replace', key, old, o);
        return o;
    },

/**
 * Adds all elements of an Array or an Object to the collection.
 * @param {Object/Array} objs An Object containing properties which will be added to the collection, or
 * an Array of values, each of which are added to the collection.
 */
    addAll : function(objs){
        if(arguments.length > 1 || objs instanceof Array){
            var args = arguments.length > 1 ? arguments : objs;
            for(var i = 0, len = args.length; i < len; i++){
                this.add(args[i]);
            }
        }else{
            for(var key in objs){
                if(this.allowFunctions || typeof objs[key] != 'function'){
                    this.add(objs[key], key);
                }
            }
        }
    },

/**
 * Executes the specified function once for every item in the collection, passing each
 * item as the first and only parameter.
 * @param {Function} fn The function to execute for each item.
 * @param {Object} scope (optional) The scope in which to execute the function.
 */
    each : function(fn, scope){
        for(var i = 0, len = this.items.length; i < len; i++){
            fn.call(scope || window, this.items[i]);
        }
    },

/**
 * Executes the specified function once for every key in the collection, passing each
 * key, and its associated item as the first two parameters.
 * @param {Function} fn The function to execute for each item.
 * @param {Object} scope (optional) The scope in which to execute the function.
 */
    eachKey : function(fn, scope){
        for(var i = 0, len = this.keys.length; i < len; i++){
            fn.call(scope || window, this.keys[i], this.items[i]);
        }
    },

/**
 * Returns the first item in the collection which elicits a true return value from the
 * passed selection function.
 * @param {Function} fn The selection function to execute for each item.
 * @param {Object} scope (optional) The scope in which to execute the function.
 * @return {Object} The first item in the collection which returned true from the selection function.
 */
    find : function(fn, scope){
        for(var i = 0, len = this.items.length; i < len; i++){
            if(fn.call(scope || window, this.items[i])){
                return this.items[i];
            }
        }
        return null;
    },

/**
 * Inserts an item at the specified index in the collection.
 * @param {Number} index The index to insert the item at.
 * @param {String} key The key to associate with the new item, or the item itself.
 * @param {Object} o  (optional) If the second parameter was a key, the new item.
 * @return {Object} The item inserted.
 */
    insert : function(index, key, o){
        if(arguments.length == 2){
            o = arguments[1];
            key = this.getKey(o);
        }
        if(index >= this.items.length){
            return this.add(o, key);
        }
        this.items.splice(index, 0, o);
        if(typeof key != 'undefined' && key != null){
            this.items[key] = o;
            this.keys.splice(index, 0, key);
        }
        this.fireEvent('add', index, o, key);
        return o;
    },

/**
 * Removed an item from the collection.
 * @param {Object} o The item to remove.
 * @return {Object} The item removed.
 */
    remove : function(o){
        var index = this.indexOf(o);
        this.items.splice(index, 1);
        if(typeof this.keys[index] != 'undefined'){
            var key = this.keys[index];
            this.keys.splice(index, 1);
            delete this.items[key];
        }
        this.fireEvent('remove', o);
        return o;
    },

/**
 * Remove an item from a specified index in the collection.
 * @param {Number} index The index within the collection of the item to remove.
 */
    removeAt : function(index){
        this.items.splice(index, 1);
        var key = this.keys[index];
        if(typeof key != 'undefined'){
             this.keys.splice(index, 1);
             delete this.items[key];
        }
        this.fireEvent('remove', o, key);
    },

/**
 * Removed an item associated with the passed key fom the collection.
 * @param {String} key The key of the item to remove.
 */
    removeKey : function(key){
        var o = this.items[key];
        var index = this.indexOf(o);
        this.items.splice(index, 1);
        this.keys.splice(index, 1);
        delete this.items[key];
        this.fireEvent('remove', o, key);
    },

/**
 * Returns the number of items in the collection.
 * @return {Number} the number of items in the collection.
 */
    getCount : function(){
        return this.items.length;
    },

/**
 * Returns index within the collection of the passed Object.
 * @param {Object} o The item to find the index of.
 * @return {Number} index of the item.
 */
    indexOf : function(o){
        if(!this.items.indexOf){
            for(var i = 0, len = this.items.length; i < len; i++){
                if(this.items[i] == o) return i;
            }
            return -1;
        }else{
            return this.items.indexOf(o);
        }
    },

/**
 * Returns index within the collection of the passed key.
 * @param {String} key The key to find the index of.
 * @return {Number} index of the key.
 */
    indexOfKey : function(key){
        if(!this.keys.indexOf){
            for(var i = 0, len = this.keys.length; i < len; i++){
                if(this.keys[i] == key) return i;
            }
            return -1;
        }else{
            return this.keys.indexOf(key);
        }
    },

/**
 * Returns the item associated with the passed key.
 * @param {String/Number} key The key or index of the item.
 * @return {Object} The item associated with the passed key.
 */
    item : function(key){
        return this.items[key];
    },

/**
 * Returns true if the collection contains the passed Object as an item.
 * @param {Object} o  The Object to look for in the collection.
 * @return {Boolean} True if the collection contains the Object as an item.
 */
    contains : function(o){
        return this.indexOf(o) != -1;
    },

/**
 * Returns true if the collection contains the passed Object as a key.
 * @param {String} key The key to look for in the collection.
 * @return {Boolean} True if the collection contains the Object as a key.
 */
    containsKey : function(key){
        return typeof this.items[key] != 'undefined';
    },

/**
 * Removes all items from the collection.
 */
    clear : function(o){
        this.items = [];
        this.keys = [];
        this.fireEvent('clear');
    },

/**
 * Returns the first item in the collection.
 * @return {Object} the first item in the collection..
 */
    first : function(){
        return this.items[0];
    },

/**
 * Returns the last item in the collection.
 * @return {Object} the last item in the collection..
 */
    last : function(){
        return this.items[this.items.length];
    }
});
/**
 * Returns the item associated with the passed key or index.
 * @method
 * @param {String/Number} key The key or index of the item.
 * @return {Object} The item associated with the passed key.
 */
YAHOO.ext.util.MixedCollection.prototype.get = YAHOO.ext.util.MixedCollection.prototype.item;

