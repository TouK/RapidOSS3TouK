/*
 * yui-ext 0.33
 * Copyright(c) 2006, Jack Slocum.
 */

YAHOO.namespace('ext', 'ext.util', 'ext.grid');
YAHOO.ext.Strict = (document.compatMode == 'CSS1Compat');
YAHOO.ext.SSL_SECURE_URL = 'javascript:false';

window.undefined = undefined;

 
 
Function.prototype.createCallback = function(){
    
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
            callArgs = Array.prototype.slice.call(arguments, 0); 
            var applyArgs = [appendArgs, 0].concat(args); 
            Array.prototype.splice.apply(callArgs, applyArgs); 
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


YAHOO.util.Event.on(window, 'unload', function(){
    delete Function.prototype.createSequence;
    delete Function.prototype.defer;
    delete Function.prototype.createDelegate;
    delete Function.prototype.createCallback;
    delete Function.prototype.createInterceptor;
});


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


YAHOO.ext.util.Browser = new function(){
	var ua = navigator.userAgent.toLowerCase();
	
	this.isOpera = (ua.indexOf('opera') > -1);
   	
	this.isSafari = (ua.indexOf('webkit') > -1);
   	
	this.isIE = (window.ActiveXObject);
   	
	this.isIE7 = (ua.indexOf('msie 7') > -1);
   	
	this.isGecko = !this.isSafari && (ua.indexOf('gecko') > -1);
	
	if(ua.indexOf("windows") != -1 || ua.indexOf("win32") != -1){
	    
	    this.isWindows = true;
	}else if(ua.indexOf("macintosh") != -1){
		
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

 
YAHOO.util.CustomEvent.prototype.fireDirect = function(){
    var len=this.subscribers.length;
    for (var i=0; i<len; ++i) {
        var s = this.subscribers[i];
        if(s){
            var scope = (s.override) ? s.obj : this.scope;
            if(s.fn.apply(scope, arguments) === false){
                return false;
            }
        }
    }
    return true;
};

YAHOO.extendX = function(subclass, superclass, overrides){
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


YAHOO.ext.util.DelayedTask = function(fn, scope, args){
    var timeoutId = null;
    
    
    this.delay = function(delay, newFn, newScope, newArgs){
        if(timeoutId){
            clearTimeout(timeoutId);
        }
        fn = newFn || fn;
        scope = newScope || scope;
        args = newArgs || args;
        timeoutId = setTimeout(fn.createDelegate(scope, args), delay);
    };
    
    
    this.cancel = function(){
        if(timeoutId){
            clearTimeout(timeoutId);
            timeoutId = null;
        }
    };
};


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
	    if(this.enabled){ 
    	    var b = this.bindings;
    	    for(var i = 0, len = b.length; i < len; i++){
    	        b[i](e);
    	    }
	    }
	},
	
	
	isEnabled : function(){
	    return this.enabled;  
	},
	
	
	enable: function(){
		if (!this.enabled){
	        this.el.on(this.eventName, this.keyDownDelegate);
		    this.enabled = true;
		}
	},

	
	disable: function(){
		if (this.enabled){
			this.el.removeListener(this.eventName, this.keyDownDelegate);
		    this.enabled = false;
		}
	}
};


YAHOO.ext.util.Observable = function(){};
YAHOO.ext.util.Observable.prototype = {
    
    fireEvent : function(){
        var ce = this.events[arguments[0].toLowerCase()];
        if(typeof ce == 'object'){
            return ce.fireDirect.apply(ce, Array.prototype.slice.call(arguments, 1));
        }else{
            return true;
        }
    },
    
    addListener : function(eventName, fn, scope, override){
        eventName = eventName.toLowerCase();
        var ce = this.events[eventName];
        if(!ce){
            
            throw 'You are trying to listen for an event that does not exist: "' + eventName + '".';
        }
        if(typeof ce == 'boolean'){
            ce = new YAHOO.util.CustomEvent(eventName);
            this.events[eventName] = ce;
        }
        ce.subscribe(fn, scope, override);
    },
    
    
    delayedListener : function(eventName, fn, scope, delay){
        var newFn = function(){
            setTimeout(fn.createDelegate(scope, arguments), delay || 1);
        }
        this.addListener(eventName, newFn);
        return newFn;
    },
    
    
    bufferedListener : function(eventName, fn, scope, millis){
        var task = new YAHOO.ext.util.DelayedTask();
        var newFn = function(){
            task.delay(millis || 250, fn, scope, Array.prototype.slice.call(arguments, 0));
        }
        this.addListener(eventName, newFn);
        return newFn;
    },
    
    
    removeListener : function(eventName, fn, scope){
        var ce = this.events[eventName.toLowerCase()];
        if(typeof ce == 'object'){
            ce.unsubscribe(fn, scope);
        }
    },
    
    
    purgeListeners : function(){
        for(var evt in this.events){
            if(typeof this.events[evt] == 'object'){
                 this.events[evt].unsubscribeAll();
            }
        }
    }
};

YAHOO.ext.util.Observable.prototype.on = YAHOO.ext.util.Observable.prototype.addListener;


YAHOO.ext.util.Config = {
    
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


if(YAHOO.util.Connect){
    YAHOO.util.Connect.setHeader = function(o){
		for(var prop in this._http_header){
		    
			if(typeof this._http_header[prop] != 'function'){
				o.conn.setRequestHeader(prop, this._http_header[prop]);
			}
		}
		delete this._http_header;
		this._http_header = {};
		this._has_http_headers = false;
	};   
}

if(YAHOO.util.DragDrop){
    
    YAHOO.util.DragDrop.prototype.defaultPadding = {left:0, right:0, top:0, bottom:0};
    
    
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
        this.setXConstraint(leftSpace - (pad.left||0), 
                c.width - leftSpace - b.width - (pad.right||0) 
        );
        this.setYConstraint(topSpace - (pad.top||0), 
                c.height - topSpace - b.height - (pad.bottom||0) 
        );
    } 
}

YAHOO.ext.DomHelper = new function(){
    
    var d = document;
    var tempTableEl = null;
    
    this.useDom = false;
    var emptyTags = /^(?:base|basefont|br|frame|hr|img|input|isindex|link|meta|nextid|range|spacer|wbr|audioscope|area|param|keygen|col|limittext|spot|tab|over|right|left|choose|atop|of)$/i;
    
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
    
    
    
    var createDom = function(o, parentNode){
        var el = d.createElement(o.tag);
        var useSet = el.setAttribute ? true : false; 
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
    
    
    this.insertHtml = function(where, el, html){
        where = where.toLowerCase();
        if(el.insertAdjacentHTML){
            var tag = el.tagName.toLowerCase();
            if(tag == 'table' || tag == 'tbody' || tag == 'tr'){
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
                if(el.firstChild){ 
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
                    range.setStartAfter(el.lastChild); 
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
    
    
    this.overwrite = function(el, o, returnElement){
        el = YAHOO.util.Dom.get(el);
        el.innerHTML = createHtml(o);
        return returnElement ? YAHOO.ext.Element.get(el.firstChild, true) : el.firstChild;
    };
    
    
    this.createTemplate = function(o){
        var html = createHtml(o);
        return new YAHOO.ext.DomHelper.Template(html);
    };
}();


YAHOO.ext.DomHelper.Template = function(html){
    
    this.html = html;
};
YAHOO.ext.DomHelper.Template.prototype = {
    
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
    
    
    re : /\{(\w+)\}/g,
    
    
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
   
    
    insertBefore: function(el, values, returnElement){
        el = YAHOO.util.Dom.get(el);
        var newNode = YAHOO.ext.DomHelper.insertHtml('beforeBegin', el, this.applyTemplate(values));
        return returnElement ? YAHOO.ext.Element.get(newNode, true) : newNode;
    },
    
    
    insertAfter : function(el, values, returnElement){
        el = YAHOO.util.Dom.get(el);
        var newNode = YAHOO.ext.DomHelper.insertHtml('afterEnd', el, this.applyTemplate(values));
        return returnElement ? YAHOO.ext.Element.get(newNode, true) : newNode;
    },
    
    
    append : function(el, values, returnElement){
        el = YAHOO.util.Dom.get(el);
        var newNode = YAHOO.ext.DomHelper.insertHtml('beforeEnd', el, this.applyTemplate(values));
        return returnElement ? YAHOO.ext.Element.get(newNode, true) : newNode;
    },
    
    
    overwrite : function(el, values, returnElement){
        el = YAHOO.util.Dom.get(el);
        el.innerHTML = '';
        var newNode = YAHOO.ext.DomHelper.insertHtml('beforeEnd', el, this.applyTemplate(values));
        return returnElement ? YAHOO.ext.Element.get(newNode, true) : newNode;
    }
};

YAHOO.ext.Template = YAHOO.ext.DomHelper.Template;

YAHOO.ext.Element = function(element, forceNew){
    var dom = YAHOO.util.Dom.get(element);
    if(!dom){ 
        return null;
    }
    if(!forceNew && YAHOO.ext.Element.cache[dom.id]){ 
        return YAHOO.ext.Element.cache[dom.id];
    }
    
    this.dom = dom;
    
    
    this.id = this.dom.id;
    
    this.visibilityMode = YAHOO.ext.Element.VISIBILITY;
    
    
    
    this.originalDisplay = YAHOO.util.Dom.getStyle(this.dom, 'display') || '';
    if(this.autoDisplayMode){
        if(this.originalDisplay == 'none'){
            this.setVisibilityMode(YAHOO.ext.Element.DISPLAY);
        }
    }
    if(this.originalDisplay == 'none'){
        this.originalDisplay = '';
    }
    
    
    this.defaultUnit = 'px';
}

YAHOO.ext.Element.prototype = {    
    
    setVisibilityMode : function(visMode){
        this.visibilityMode = visMode;
        return this;
    },
    
    
    enableDisplayMode : function(display){
        this.setVisibilityMode(YAHOO.ext.Element.DISPLAY);
        if(typeof display != 'undefined') this.originalDisplay = display;
        return this;
    },
    
    
    animate : function(args, duration, onComplete, easing, animType){
        this.anim(args, duration, onComplete, easing, animType);
        return this;
    },
    
    
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
        var containerTop = parseInt(c.scrollTop, 10); 
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
        
    
    autoHeight : function(animate, duration, onComplete, easing){
        var oldHeight = this.getHeight();
        this.clip();
        this.setHeight(1); 
        setTimeout(function(){
            var height = parseInt(this.dom.scrollHeight, 10); 
            if(!animate){
                this.setHeight(height);
                this.unclip();
                if(typeof onComplete == 'function'){
                    onComplete();
                }
            }else{
                this.setHeight(oldHeight); 
                this.setHeight(height, animate, duration, function(){
                    this.unclip();
                    if(typeof onComplete == 'function') onComplete();
                }.createDelegate(this), easing);
            }
        }.createDelegate(this), 0);
        return this;
    },
    
    
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
    
    
    select : function(selector, unique){
        return YAHOO.ext.Element.select('#' + this.dom.id + ' ' + selector, unique);  
    },
    
    
    initDD : function(group, config, overrides){
        var dd = new YAHOO.util.DD(YAHOO.util.Dom.generateId(this.dom), group, config);
        return YAHOO.ext.util.Config.apply(dd, overrides);
    },
   
    
    initDDProxy : function(group, config, overrides){
        var dd = new YAHOO.util.DDProxy(YAHOO.util.Dom.generateId(this.dom), group, config);
        return YAHOO.ext.util.Config.apply(dd, overrides);
    },
   
    
    initDDTarget : function(group, config, overrides){
        var dd = new YAHOO.util.DDTarget(YAHOO.util.Dom.generateId(this.dom), group, config);
        return YAHOO.ext.util.Config.apply(dd, overrides);
    },
   
    
     setVisible : function(visible, animate, duration, onComplete, easing){
        
        if(!animate || !YAHOO.util.Anim){
            if(this.visibilityMode == YAHOO.ext.Element.DISPLAY){
                this.setDisplayed(visible);
            }else{
                YAHOO.util.Dom.setStyle(this.dom, 'visibility', visible ? 'visible' : 'hidden');
            }
        }else{
            
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
    
    
    isDisplayed : function() {
        return YAHOO.util.Dom.getStyle(this.dom, 'display') != 'none';
    },
    
    
    toggle : function(animate, duration, onComplete, easing){
        this.setVisible(!this.isVisible(), animate, duration, onComplete, easing);
        return this;
    },
    
    
    setDisplayed : function(value) {
        if(typeof value == 'boolean'){
           value = value ? this.originalDisplay : 'none';
        }
        YAHOO.util.Dom.setStyle(this.dom, 'display', value);
        return this;
    },
    
    
    focus : function() {
        try{
            this.dom.focus();
        }catch(e){}
        return this;
    },
    
    
    blur : function() {
        try{
            this.dom.blur();
        }catch(e){}
        return this;
    },
    
    
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
    
    
    toggleClass : function(className){
        if(this.hasClass(className)){
            this.removeClass(className);
        }else{
            this.addClass(className);
        }
        return this;
    },
    
    
    hasClass : function(className){
        var re = new RegExp('(?:^|\\s+)' + className + '(?:\\s+|$)');
        return re.test(this.dom.className);
    },
    
    
    replaceClass : function(oldClassName, newClassName){
        this.removeClass(oldClassName);
        this.addClass(newClassName);
        return this;
    },
    
    
    getStyle : function(name){
        return YAHOO.util.Dom.getStyle(this.dom, name);
    },
    
    
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
    
    
    applyStyles : function(style){
       YAHOO.ext.DomHelper.applyStyles(this.dom, style);
    },
    
    
    getX : function(){
        return YAHOO.util.Dom.getX(this.dom);
    },
    
    
    getY : function(){
        return YAHOO.util.Dom.getY(this.dom);
    },
    
    
    getXY : function(){
        return YAHOO.util.Dom.getXY(this.dom);
    },
    
    
    setX : function(x, animate, duration, onComplete, easing){
        if(!animate || !YAHOO.util.Anim){
            YAHOO.util.Dom.setX(this.dom, x);
        }else{
            this.setXY([x, this.getY()], animate, duration, onComplete, easing);
        }
        return this;
    },
    
    
    setY : function(y, animate, duration, onComplete, easing){
        if(!animate || !YAHOO.util.Anim){
            YAHOO.util.Dom.setY(this.dom, y);
        }else{
            this.setXY([this.getX(), y], animate, duration, onComplete, easing);
        }
        return this;
    },
    
    
    setLeft : function(left){
        YAHOO.util.Dom.setStyle(this.dom, 'left', this.addUnits(left));
        return this;
    },
    
    
    setTop : function(top){
        YAHOO.util.Dom.setStyle(this.dom, 'top', this.addUnits(top));
        return this;
    },
    
    
    setRight : function(right){
        YAHOO.util.Dom.setStyle(this.dom, 'right', this.addUnits(right));
        return this;
    },
    
    
    setBottom : function(bottom){
        YAHOO.util.Dom.setStyle(this.dom, 'bottom', this.addUnits(bottom));
        return this;
    },
    
    
    setXY : function(pos, animate, duration, onComplete, easing){
        if(!animate || !YAHOO.util.Anim){
            YAHOO.util.Dom.setXY(this.dom, pos);
        }else{
            this.anim({points: {to: pos}}, duration, onComplete, easing, YAHOO.util.Motion);
        }
        return this;
    },
    
    
    setLocation : function(x, y, animate, duration, onComplete, easing){
        this.setXY([x, y], animate, duration, onComplete, easing);
        return this;
    },
    
    
    moveTo : function(x, y, animate, duration, onComplete, easing){
        
        
        this.setXY([x, y], animate, duration, onComplete, easing);
        return this;
    },
    
    
    getRegion : function(){
        return YAHOO.util.Dom.getRegion(this.dom);
    },
    
    
    getHeight : function(contentHeight){
        var h = this.dom.offsetHeight;
        return contentHeight !== true ? h : h-this.getBorderWidth('tb')-this.getPadding('tb');
    },
    
    
    getWidth : function(contentWidth){
        var w = this.dom.offsetWidth;
        return contentWidth !== true ? w : w-this.getBorderWidth('lr')-this.getPadding('lr');
    },
    
    
    getSize : function(contentSize){
        return {width: this.getWidth(contentSize), height: this.getHeight(contentSize)};
    },
    
    
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
    
    
    setRegion : function(region, animate, duration, onComplete, easing){
        this.setBounds(region.left, region.top, region.right-region.left, region.bottom-region.top, animate, duration, onComplete, easing);
        return this;
    },
    
    
    addListener : function(eventName, handler, scope, override){
        YAHOO.util.Event.addListener(this.dom, eventName, handler, scope || this, true);
        return this;
    },
    
    bufferedListener : function(eventName, fn, scope, millis){
        var task = new YAHOO.ext.util.DelayedTask();
        scope = scope || this;
        var newFn = function(){
            task.delay(millis || 250, fn, scope, Array.prototype.slice.call(arguments, 0));
        }
        this.addListener(eventName, newFn);
        return newFn;
    },
    
    
    
    addHandler : function(eventName, stopPropagation, handler, scope, override){
        var fn = YAHOO.ext.Element.createStopHandler(stopPropagation, handler, scope || this, true);
        YAHOO.util.Event.addListener(this.dom, eventName, fn);
        return this;
    },
    
    
    on : function(eventName, handler, scope, override){
        YAHOO.util.Event.addListener(this.dom, eventName, handler, scope || this, true);
        return this;
    },
    
    
    addManagedListener : function(eventName, fn, scope, override){
        return YAHOO.ext.EventManager.on(this.dom, eventName, fn, scope || this, true);
    },
    
    
    mon : function(eventName, fn, scope, override){
        return YAHOO.ext.EventManager.on(this.dom, eventName, fn, scope || this, true);
    },
    
    removeListener : function(eventName, handler, scope){
        YAHOO.util.Event.removeListener(this.dom, eventName, handler);
        return this;
    },
    
    
    removeAllListeners : function(){
        YAHOO.util.Event.purgeElement(this.dom);
        return this;
    },
    
    
    
     setOpacity : function(opacity, animate, duration, onComplete, easing){
        if(!animate || !YAHOO.util.Anim){
            YAHOO.util.Dom.setStyle(this.dom, 'opacity', opacity);
        }else{
            this.anim({opacity: {to: opacity}}, duration, onComplete, easing);
        }
        return this;
    },
    
    
    getLeft : function(local){
        if(!local){
            return this.getX();
        }else{
            return parseInt(this.getStyle('left'), 10) || 0;
        }
    },
    
    
    getRight : function(local){
        if(!local){
            return this.getX() + this.getWidth();
        }else{
            return (this.getLeft(true) + this.getWidth()) || 0;
        }
    },
    
    
    getTop : function(local) {
        if(!local){
            return this.getY();
        }else{
            return parseInt(this.getStyle('top'), 10) || 0;
        }
    },
    
    
    getBottom : function(local){
        if(!local){
            return this.getY() + this.getHeight();
        }else{
            return (this.getTop(true) + this.getHeight()) || 0;
        }
    },
    
    
    setAbsolutePositioned : function(zIndex){
        this.setStyle('position', 'absolute');
        if(zIndex){
            this.setStyle('z-index', zIndex);
        }
        return this;
    },
    
    
    setRelativePositioned : function(zIndex){
        this.setStyle('position', 'relative');
        if(zIndex){
            this.setStyle('z-index', zIndex);
        }
        return this;
    },
    
    
    clearPositioning : function(){
        this.setStyle('position', '');
        this.setStyle('left', '');
        this.setStyle('right', '');
        this.setStyle('top', '');
        this.setStyle('bottom', '');
        return this;
    },
    
    
    getPositioning : function(){
        return {
            'position' : this.getStyle('position'),
            'left' : this.getStyle('left'),
            'right' : this.getStyle('right'),
            'top' : this.getStyle('top'),
            'bottom' : this.getStyle('bottom')
        };
    },
    
    
    getBorderWidth : function(side){
        return this.addStyles(side, YAHOO.ext.Element.borders);
    },
    
    
    getPadding : function(side){
        return this.addStyles(side, YAHOO.ext.Element.paddings);
    },
    
    
    setPositioning : function(positionCfg){
        if(positionCfg.position)this.setStyle('position', positionCfg.position);
        if(positionCfg.left)this.setLeft(positionCfg.left);
        if(positionCfg.right)this.setRight(positionCfg.right);
        if(positionCfg.top)this.setTop(positionCfg.top);
        if(positionCfg.bottom)this.setBottom(positionCfg.bottom);
        return this;
    },
    
    
    
     setLeftTop : function(left, top){
        this.dom.style.left = this.addUnits(left);
        this.dom.style.top = this.addUnits(top);
        return this;
    },
    
    
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
    
    
     alignTo : function(element, position, offsets, animate, duration, onComplete, easing){
        var otherEl = getEl(element);
        if(!otherEl){
            return this; 
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
    
    
    hide : function(animate, duration, onComplete, easing){
        this.setVisible(false, animate, duration, onComplete, easing);
        return this;
    },
    
    
    show : function(animate, duration, onComplete, easing){
        this.setVisible(true, animate, duration, onComplete, easing);
        return this;
    },
    
    
    addUnits : function(size){
        if(size === '' || size == 'auto' || typeof size == 'undefined'){
            return size;
        }
        if(typeof size == 'number' || !YAHOO.ext.Element.unitPattern.test(size)){
            return size + this.defaultUnit;
        }
        return size;
    },
    
    
    beginMeasure : function(){
        var el = this.dom;
        if(el.offsetWidth || el.offsetHeight){
            return this; 
        }
        var changed = [];
        var p = this.dom; 
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
    
    
    load : function(){
        var um = this.getUpdateManager();
        um.update.apply(um, arguments);
        return this;
    },
    
    
    getUpdateManager : function(){
        if(!this.updateManager){
            this.updateManager = new YAHOO.ext.UpdateManager(this);
        }
        return this.updateManager;
    },
    
    
    unselectable : function(){
        this.dom.unselectable = 'on';
        this.swallowEvent('selectstart', true);
        this.applyStyles('-moz-user-select:none;-khtml-user-select:none;');
        return this;
    },
    
    
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

    
    getChildrenByTagName : function(tagName){
        var children = this.dom.getElementsByTagName(tagName);
        var len = children.length;
        var ce = new Array(len);
        for(var i = 0; i < len; ++i){
            ce[i] = YAHOO.ext.Element.get(children[i], true);
        }
        return ce;
    },
    
    
    getChildrenByClassName : function(className, tagName){
        var children = YAHOO.util.Dom.getElementsByClassName(className, tagName, this.dom);
        var len = children.length;
        var ce = new Array(len);
        for(var i = 0; i < len; ++i){
            ce[i] = YAHOO.ext.Element.get(children[i], true);
        }
        return ce;
    },
    
    
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
    
    
    setBox : function(box, adjust, animate, duration, onComplete, easing){
        var w = box.width, h = box.height;
        if((adjust && !this.autoBoxAdjust) && !this.isBorderBox()){
           w -= (this.getBorderWidth('lr') + this.getPadding('lr'));
           h -= (this.getBorderWidth('tb') + this.getPadding('tb'));
        }
        this.setBounds(box.x, box.y, w, h, animate, duration, onComplete, easing);
        return this;
    },
    
    
     repaint : function(){
        var dom = this.dom;
        YAHOO.util.Dom.addClass(dom, 'yui-ext-repaint');
        setTimeout(function(){
            YAHOO.util.Dom.removeClass(dom, 'yui-ext-repaint');
        }, 1);
        return this;
    },
    
    
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
    
    
    remove : function(){
        this.dom.parentNode.removeChild(this.dom);
        delete YAHOO.ext.Element.cache[this.dom.id];
    },
    
    
    addClassOnOver : function(className){
        this.on('mouseover', function(){
            this.addClass(className);
        }, this, true);
        this.on('mouseout', function(){
            this.removeClass(className);
        }, this, true);
        return this;
    },
    
    
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
    
    
    fitToParent : function(monitorResize){
        var p = getEl(this.dom.parentNode, true);
        p.beginMeasure(); 
        var box = p.getBox(true, true);
        p.endMeasure();
        this.setSize(box.width, box.height);
        if(monitorResize === true){
            YAHOO.ext.EventManager.onWindowResize(this.fitToParent, this, true);
        }
        return this;
    },
    
    
    getNextSibling : function(){
        var n = this.dom.nextSibling;
        while(n && n.nodeType != 1){
            n = n.nextSibling;
        }
        return n;
    },
    
    
    getPrevSibling : function(){
        var n = this.dom.previousSibling;
        while(n && n.nodeType != 1){
            n = n.previousSibling;
        }
        return n;
    },
    
    
    
    appendChild: function(el){
        el = getEl(el);
        el.appendTo(this);
        return this;
    },
    
    
    createChild: function(config, insertBefore){
        var c;
        if(insertBefore){
            c = YAHOO.ext.DomHelper.insertBefore(insertBefore, config, true);
        }else{
            c = YAHOO.ext.DomHelper.append(this.dom, config, true);
        }
        return c;
    },
    
    
    appendTo: function(el){
        var node = getEl(el).dom;
        node.appendChild(this.dom);
        return this;
    },
    
    
    insertBefore: function(el){
        var node = getEl(el).dom;
        node.parentNode.insertBefore(this.dom, node);
        return this;
    },
    
    
    insertAfter: function(el){
        var node = getEl(el).dom;
        node.parentNode.insertBefore(this.dom, node.nextSibling);
        return this;
    },
    
    
    wrap: function(config){
        if(!config){
            config = {tag: 'div'};
        }
        var newEl = YAHOO.ext.DomHelper.insertBefore(this.dom, config, true);
        newEl.dom.appendChild(this.dom);
        return newEl;
    },
    
    
    replace: function(el){
        el = getEl(el);
        this.insertBefore(el);
        el.remove();
        return this;
    },
    
    
    insertHtml : function(where, html){
        YAHOO.ext.DomHelper.insertHtml(where, this.dom, html);
        return this;
    },
    
    
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
    
    
    addKeyMap : function(config){
        return new YAHOO.ext.KeyMap(this, config);
    }
};


YAHOO.ext.Element.prototype.autoBoxAdjust = true;

YAHOO.ext.Element.prototype.autoDisplayMode = true;

YAHOO.ext.Element.unitPattern = /\d+(px|em|%|en|ex|pt|in|cm|mm|pc)$/i;

YAHOO.ext.Element.VISIBILITY = 1;

YAHOO.ext.Element.DISPLAY = 2;

YAHOO.ext.Element.blockElements = /^(?:address|blockquote|center|dir|div|dl|fieldset|form|h\d|hr|isindex|menu|ol|ul|p|pre|table|dd|dt|li|tbody|tr|td|thead|tfoot|iframe)$/i;
YAHOO.ext.Element.borders = {l: 'border-left-width', r: 'border-right-width', t: 'border-top-width', b: 'border-bottom-width'};
YAHOO.ext.Element.paddings = {l: 'padding-left', r: 'padding-right', t: 'padding-top', b: 'padding-bottom'};
YAHOO.ext.Element.margins = {l: 'margin-left', r: 'margin-right', t: 'margin-top', b: 'margin-bottom'};
        

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


YAHOO.ext.Element.cache = {};


YAHOO.ext.Element.get = function(el, autoGenerateId){
    if(!el){ return null; }
    autoGenerateId = true; 
    if(el instanceof YAHOO.ext.Element){
        el.dom = YAHOO.util.Dom.get(el.id); 
        YAHOO.ext.Element.cache[el.id] = el; 
        return el;
    }else if(el.isComposite){
        return el;
    }else if(el instanceof Array){
        return YAHOO.ext.Element.select(el);
    }else if(el === document){
        
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
    if(typeof el != 'string'){ 
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


var getEl = YAHOO.ext.Element.get;


YAHOO.util.Event.addListener(window, 'unload', function(){ 
    YAHOO.ext.Element.cache = null;
});



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
    
    each : function(fn, scope){
        var els = this.elements;
        for(var i = 0, len = els.length; i < len; i++){
            fn.call(scope || els[i], els[i], this, i);
        }
        return this;
    }
};

YAHOO.ext.CompositeElementLite = function(els){
    YAHOO.ext.CompositeElementLite.superclass.constructor.call(this, els);
    this.el = YAHOO.ext.Element.get(this.elements[0], true);
};
YAHOO.extendX(YAHOO.ext.CompositeElementLite, YAHOO.ext.CompositeElement, {
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
if(typeof cssQuery == 'function'){
    YAHOO.ext.Element.selectorFunction = cssQuery;
}else if(typeof document.getElementsBySelector == 'function'){ 
    YAHOO.ext.Element.selectorFunction = document.getElementsBySelector.createDelegate(document);
}

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
        
        YAHOO.util.Event.on(window, 'load', fireDocReady);
    };
    
    this.wrap = function(fn, scope, override){
        var wrappedFn = function(e){
            YAHOO.ext.EventObject.setEvent(e);
            fn.call(override ? scope || window : window, YAHOO.ext.EventObject, scope);
        };
        return wrappedFn;
    };
    
    
    this.addListener = function(element, eventName, fn, scope, override){
        var wrappedFn = this.wrap(fn, scope, override);
        YAHOO.util.Event.addListener(element, eventName, wrappedFn);
        return wrappedFn;
    };
    
    
    this.removeListener = function(element, eventName, wrappedFn){
        return YAHOO.util.Event.removeListener(element, eventName, wrappedFn);
    };
    
    
    this.on = this.addListener;
    
    
    this.onDocumentReady = function(fn, scope, override){
        if(docReadyState){ 
            fn.call(override? scope || window : window, scope);
            return;
        }
        if(!docReadyEvent){
            initDocReady();
        }
        docReadyEvent.subscribe(fn, scope, override);
    }
    
    
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
    
    
    this.removeResizeListener = function(fn, scope){
        if(resizeEvent){
            resizeEvent.unsubscribe(fn, scope);
        }
    }
};


YAHOO.ext.EventObject = new function(){
     
    this.browserEvent = null;
     
    this.button = -1;
     
    this.shiftKey = false;
     
    this.ctrlKey = false;
     
    this.altKey = false;
    
    
    this.BACKSPACE = 8;
    
    this.TAB = 9;
    
    this.RETURN = 13;
    
    this.ESC = 27;
    
    this.SPACE = 32;
    
    this.PAGEUP = 33;
    
    this.PAGEDOWN = 34;
    
    this.END = 35;
    
    this.HOME = 36;
    
    this.LEFT = 37;
    
    this.UP = 38;
    
    this.RIGHT = 39;
    
    this.DOWN = 40;
    
    this.DELETE = 46;
    
    this.F5 = 116;

        
    this.setEvent = function(e){
        if(e == this){ 
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
    
     
    this.stopEvent = function(){
        if(this.browserEvent){
            YAHOO.util.Event.stopEvent(this.browserEvent);
        }
    };
    
     
    this.preventDefault = function(){
        if(this.browserEvent){
            YAHOO.util.Event.preventDefault(this.browserEvent);
        }
    };
    
    
    this.isNavKeyPress = function(){
        return (this.browserEvent.keyCode && this.browserEvent.keyCode >= 33 && this.browserEvent.keyCode <= 40);
    };
    
     
    this.stopPropagation = function(){
        if(this.browserEvent){
            YAHOO.util.Event.stopPropagation(this.browserEvent);
        }
    };
    
     
    this.getCharCode = function(){
        if(this.browserEvent){
            return YAHOO.util.Event.getCharCode(this.browserEvent);
        }
        return null;
    };
    
    
    this.getKey = function(){
        if(this.browserEvent){
            return this.browserEvent.keyCode || this.browserEvent.charCode;
        }
        return null;
    };
    
     
    this.getPageX = function(){
        if(this.browserEvent){
            return YAHOO.util.Event.getPageX(this.browserEvent);
        }
        return null;
    };
    
     
    this.getPageY = function(){
        if(this.browserEvent){
            return YAHOO.util.Event.getPageY(this.browserEvent);
        }
        return null;
    };
    
     
    this.getTime = function(){
        if(this.browserEvent){
            return YAHOO.util.Event.getTime(this.browserEvent);
        }
        return null;
    };
    
     
    this.getXY = function(){
        if(this.browserEvent){
            return YAHOO.util.Event.getXY(this.browserEvent);
        }
        return [];
    };
    
     
    this.getTarget = function(){
        if(this.browserEvent){
            return YAHOO.util.Event.getTarget(this.browserEvent);
        }
        return null;
    };
    
     
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
     
    this.getRelatedTarget = function(){
        if(this.browserEvent){
            return YAHOO.util.Event.getRelatedTarget(this.browserEvent);
        }
        return null;
    };
    
    
    this.getWheelDelta = function(){
        var e = this.browserEvent;
        var delta = 0;
        if(e.wheelDelta){ 
            delta = e.wheelDelta/120;
            
            if(window.opera) delta = -delta;
        }else if(e.detail){ 
            delta = -e.detail/3;
        }
        return delta;
    };
    
     
    this.hasModifier = function(){
        return this.ctrlKey || this.altKey || this.shiftKey;
    };
}();
            
    

YAHOO.ext.UpdateManager = function(el, forceNew){
    el = YAHOO.ext.Element.get(el);
    if(!forceNew && el.updateManager){
        return el.updateManager;
    }
    
    this.el = el;
    
    this.defaultUrl = null;
    this.beforeUpdate = new YAHOO.util.CustomEvent('UpdateManager.beforeUpdate');
    this.onUpdate = new YAHOO.util.CustomEvent('UpdateManager.onUpdate');
    this.onFailure = new YAHOO.util.CustomEvent('UpdateManager.onFailure');
    
    this.events = {
        
        'beforeupdate': this.beforeUpdate,
        
        'update': this.onUpdate,
        
        'failure': this.onFailure 
    };
    
    
    this.sslBlankUrl = YAHOO.ext.UpdateManager.defaults.sslBlankUrl;
    
    this.disableCaching = YAHOO.ext.UpdateManager.defaults.disableCaching;
    
    this.indicatorText = YAHOO.ext.UpdateManager.defaults.indicatorText;
    
    this.showLoadIndicator = YAHOO.ext.UpdateManager.defaults.showLoadIndicator;
    
    this.timeout = YAHOO.ext.UpdateManager.defaults.timeout;
    
    
    this.loadScripts = YAHOO.ext.UpdateManager.defaults.loadScripts;
    
    
    this.transaction = null;
    
    
    this.autoRefreshProcId = null;
    
    this.refreshDelegate = this.refresh.createDelegate(this);
    
    this.updateDelegate = this.update.createDelegate(this);
    
    this.formUpdateDelegate = this.formUpdate.createDelegate(this);
    
    this.successDelegate = this.processSuccess.createDelegate(this);
    
     this.failureDelegate = this.processFailure.createDelegate(this);
     
     
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
    
    getEl : function(){
        return this.el;
    },
    
    
    update : function(url, params, callback, discardUrl){
        if(this.beforeUpdate.fireDirect(this.el, url, params) !== false){
            if(typeof url == 'object'){ 
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
            if(params && typeof params != 'string'){ 
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
    
    
    refresh : function(callback){
        if(this.defaultUrl == null){
            return;
        }
        this.update(this.defaultUrl, null, callback, true);
    },
    
    
    startAutoRefresh : function(interval, url, params, callback, refreshNow){
        if(refreshNow){
            this.update(url || this.defaultUrl, params, callback, true);
        }
        if(this.autoRefreshProcId){
            clearInterval(this.autoRefreshProcId);
        }
        this.autoRefreshProcId = setInterval(this.update.createDelegate(this, [url || this.defaultUrl, params, callback, true]), interval*1000);
    },
    
    
     stopAutoRefresh : function(){
        if(this.autoRefreshProcId){
            clearInterval(this.autoRefreshProcId);
        }
    },
    
    
    showLoading : function(){
        if(this.showLoadIndicator){
            this.el.update(this.indicatorText);
        }
    },
    
    
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
    
    
    processSuccess : function(response){
        this.transaction = null;
        if(response.argument.form && response.argument.reset){
            try{ 
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
    
    
    processFailure : function(response){
        this.transaction = null;
        this.onFailure.fireDirect(this.el, response);
        if(typeof response.argument.callback == 'function'){
            response.argument.callback(this.el, false);
        }
    },
    
    
    setRenderer : function(renderer){
        this.renderer = renderer;
    },
    
    getRenderer : function(){
       return this.renderer;  
    },
    
    
    setDefaultUrl : function(defaultUrl){
        this.defaultUrl = defaultUrl;
    },
    
    
    abort : function(){
        if(this.transaction){
            YAHOO.util.Connect.abort(this.transaction);
        }
    },
    
    
    isUpdating : function(){
        if(this.transaction){
            return YAHOO.util.Connect.isCallInProgress(this.transaction);
        }
        return false;
    }
};


   YAHOO.ext.UpdateManager.defaults = {
       
         timeout : 30,
         
         
        loadScripts : false,
         
        
        sslBlankUrl : (YAHOO.ext.SSL_SECURE_URL || 'javascript:false'),
        
        disableCaching : false,
        
        showLoadIndicator : true,
        
        indicatorText : '<div class="loading-indicator">Loading...</div>'
   };


YAHOO.ext.UpdateManager.updateElement = function(el, url, params, options){
    var um = getEl(el, true).getUpdateManager();
    YAHOO.ext.util.Config.apply(um, options);
    um.update(url, params, options.callback);
}

YAHOO.ext.UpdateManager.update = YAHOO.ext.UpdateManager.updateElement;
 
YAHOO.ext.UpdateManager.BasicRenderer = function(){};

YAHOO.ext.UpdateManager.BasicRenderer.prototype = {
    
     render : function(el, response, updateManager, callback){
        el.update(response.responseText, updateManager.loadScripts, callback);
    }
};

