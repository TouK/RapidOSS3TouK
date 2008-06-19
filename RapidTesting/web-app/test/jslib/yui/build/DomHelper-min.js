/*
 * yui-ext 0.33
 * Copyright(c) 2006, Jack Slocum.
 */


YAHOO.ext.DomHelper=new function(){var d=document;var tempTableEl=null;this.useDom=false;var emptyTags=/^(?:base|basefont|br|frame|hr|img|input|isindex|link|meta|nextid|range|spacer|wbr|audioscope|area|param|keygen|col|limittext|spot|tab|over|right|left|choose|atop|of)$/i;this.applyStyles=function(el,styles){if(styles){var D=YAHOO.util.Dom;if(typeof styles=="string"){var re=/\s?([a-z\-]*)\:([^;]*);?/gi;var matches;while((matches=re.exec(styles))!=null){D.setStyle(el,matches[1],matches[2]);}}else if(typeof styles=="object"){for(var style in styles){D.setStyle(el,style,styles[style]);}}else if(typeof styles=="function"){YAHOO.ext.DomHelper.applyStyles(el,styles.call());}}};var createHtml=function(o){var b='';b+='<'+o.tag;for(var attr in o){if(attr=='tag'||attr=='children'||attr=='html'||typeof o[attr]=='function')continue;if(attr=='style'){var s=o['style'];if(typeof s=='function'){s=s.call();}
if(typeof s=='string'){b+=' style="'+s+'"';}else if(typeof s=='object'){b+=' style="';for(var key in s){if(typeof s[key]!='function'){b+=key+':'+s[key]+';';}}
b+='"';}}else{if(attr=='cls'){b+=' class="'+o['cls']+'"';}else if(attr=='htmlFor'){b+=' for="'+o['htmlFor']+'"';}else{b+=' '+attr+'="'+o[attr]+'"';}}}
if(emptyTags.test(o.tag)){b+=' />';}else{b+='>';if(o.children){for(var i=0,len=o.children.length;i<len;i++){b+=createHtml(o.children[i],b);}}
if(o.html){b+=o.html;}
b+='</'+o.tag+'>';}
return b;}
var createDom=function(o,parentNode){var el=d.createElement(o.tag);var useSet=el.setAttribute?true:false;for(var attr in o){if(attr=='tag'||attr=='children'||attr=='html'||attr=='style'||typeof o[attr]=='function')continue;if(attr=='cls'){el.className=o['cls'];}else{if(useSet)el.setAttribute(attr,o[attr]);else el[attr]=o[attr];}}
YAHOO.ext.DomHelper.applyStyles(el,o.style);if(o.children){for(var i=0,len=o.children.length;i<len;i++){createDom(o.children[i],el);}}
if(o.html){el.innerHTML=o.html;}
if(parentNode){parentNode.appendChild(el);}
return el;};var insertIntoTable=function(tag,where,el,html){if(!tempTableEl){tempTableEl=document.createElement('div');}
var node;if(tag=='table'||tag=='tbody'){tempTableEl.innerHTML='<table><tbody>'+html+'</tbody></table>';node=tempTableEl.firstChild.firstChild.firstChild;}else{tempTableEl.innerHTML='<table><tbody><tr>'+html+'</tr></tbody></table>';node=tempTableEl.firstChild.firstChild.firstChild.firstChild;}
if(where=='beforebegin'){el.parentNode.insertBefore(node,el);return node;}else if(where=='afterbegin'){el.insertBefore(node,el.firstChild);return node;}else if(where=='beforeend'){el.appendChild(node);return node;}else if(where=='afterend'){el.parentNode.insertBefore(node,el.nextSibling);return node;}}
this.insertHtml=function(where,el,html){where=where.toLowerCase();if(el.insertAdjacentHTML){var tag=el.tagName.toLowerCase();if(tag=='table'||tag=='tbody'||tag=='tr'){return insertIntoTable(tag,where,el,html);}
switch(where){case'beforebegin':el.insertAdjacentHTML(where,html);return el.previousSibling;case'afterbegin':el.insertAdjacentHTML(where,html);return el.firstChild;case'beforeend':el.insertAdjacentHTML(where,html);return el.lastChild;case'afterend':el.insertAdjacentHTML(where,html);return el.nextSibling;}
throw'Illegal insertion point -> "'+where+'"';}
var range=el.ownerDocument.createRange();var frag;switch(where){case'beforebegin':range.setStartBefore(el);frag=range.createContextualFragment(html);el.parentNode.insertBefore(frag,el);return el.previousSibling;case'afterbegin':if(el.firstChild){range.setStartBefore(el.firstChild);}else{range.selectNodeContents(el);range.collapse(true);}
frag=range.createContextualFragment(html);el.insertBefore(frag,el.firstChild);return el.firstChild;case'beforeend':if(el.lastChild){range.setStartAfter(el.lastChild);}else{range.selectNodeContents(el);range.collapse(false);}
frag=range.createContextualFragment(html);el.appendChild(frag);return el.lastChild;case'afterend':range.setStartAfter(el);frag=range.createContextualFragment(html);el.parentNode.insertBefore(frag,el.nextSibling);return el.nextSibling;}
throw'Illegal insertion point -> "'+where+'"';};this.insertBefore=function(el,o,returnElement){el=YAHOO.util.Dom.get(el);var newNode;if(this.useDom){newNode=createDom(o,null);el.parentNode.insertBefore(newNode,el);}else{var html=createHtml(o);newNode=this.insertHtml('beforeBegin',el,html);}
return returnElement?YAHOO.ext.Element.get(newNode,true):newNode;};this.insertAfter=function(el,o,returnElement){el=YAHOO.util.Dom.get(el);var newNode;if(this.useDom){newNode=createDom(o,null);el.parentNode.insertBefore(newNode,el.nextSibling);}else{var html=createHtml(o);newNode=this.insertHtml('afterEnd',el,html);}
return returnElement?YAHOO.ext.Element.get(newNode,true):newNode;};this.append=function(el,o,returnElement){el=YAHOO.util.Dom.get(el);var newNode;if(this.useDom){newNode=createDom(o,null);el.appendChild(newNode);}else{var html=createHtml(o);newNode=this.insertHtml('beforeEnd',el,html);}
return returnElement?YAHOO.ext.Element.get(newNode,true):newNode;};this.overwrite=function(el,o,returnElement){el=YAHOO.util.Dom.get(el);el.innerHTML=createHtml(o);return returnElement?YAHOO.ext.Element.get(el.firstChild,true):el.firstChild;};this.createTemplate=function(o){var html=createHtml(o);return new YAHOO.ext.DomHelper.Template(html);};}();YAHOO.ext.DomHelper.Template=function(html){this.html=html;};YAHOO.ext.DomHelper.Template.prototype={applyTemplate:function(values){if(this.compiled){return this.compiled(values);}
var empty='';var fn=function(match,index){if(typeof values[index]!='undefined'){return values[index];}else{return empty;}}
return this.html.replace(this.re,fn);},re:/\{(\w+)\}/g,compile : function(){
        var body = ["this.compiled = function(values){ return ['"];
        body.push(this.html.replace(this.re, "', values['$1'], '"));
        body.push("'].join('');};");
        eval(body.join(''));
        return this;
    },insertBefore:function(el,values,returnElement){el=YAHOO.util.Dom.get(el);var newNode=YAHOO.ext.DomHelper.insertHtml('beforeBegin',el,this.applyTemplate(values));return returnElement?YAHOO.ext.Element.get(newNode,true):newNode;},insertAfter:function(el,values,returnElement){el=YAHOO.util.Dom.get(el);var newNode=YAHOO.ext.DomHelper.insertHtml('afterEnd',el,this.applyTemplate(values));return returnElement?YAHOO.ext.Element.get(newNode,true):newNode;},append:function(el,values,returnElement){el=YAHOO.util.Dom.get(el);var newNode=YAHOO.ext.DomHelper.insertHtml('beforeEnd',el,this.applyTemplate(values));return returnElement?YAHOO.ext.Element.get(newNode,true):newNode;},overwrite:function(el,values,returnElement){el=YAHOO.util.Dom.get(el);el.innerHTML='';var newNode=YAHOO.ext.DomHelper.insertHtml('beforeEnd',el,this.applyTemplate(values));return returnElement?YAHOO.ext.Element.get(newNode,true):newNode;}};YAHOO.ext.Template=YAHOO.ext.DomHelper.Template;