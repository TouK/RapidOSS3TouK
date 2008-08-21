/*timeline.js*/
Timeline._Band.prototype.openBubbleForPoint=function(pageX,pageY,width,height,node){
	this.closeBubble();
	this._bubble=Timeline.Graphics.createBubbleForPoint(
		this._timeline.getDocument(),pageX,pageY,width,height,node);
	return this._bubble.content;
};

/*painters.js*/
Timeline.DurationEventPainter.prototype.initialize=function(band,timeline){
	this._band=band;
	this._timeline=timeline;
	this._layout.initialize(band,timeline);

	this._eventLayer=null;
	this._highlightLayer=null;
	this.bubbleClickedEvent = new YAHOO.util.CustomEvent('bubbleclicked');
};
Timeline.DurationEventPainter.prototype._showBubble=function(x,y,evt){
	var div=this._band.openBubbleForPoint(
		x,y,
		this._theme.event.bubble.width,
		this._theme.event.bubble.height,
		evt._node
	);
	evt.fillInfoBubble(div,this._theme,this._band.getLabeller());
	YAHOO.util.Event.addListener(div, 'click', this.fireBubbleClicked, this, true);
};
Timeline.DurationEventPainter.prototype.fireBubbleClicked=function(){
	this.bubbleClickedEvent.fireDirect(this._band._bubble);
};

/*graphics.js*/

Timeline.Graphics.createBubbleForPoint=function(doc,pageX,pageY,contentWidth,contentHeight,node){
	function getWindowDims(){
		if(typeof window.innerWidth=='number'){
			return{w:window.innerWidth,h:window.innerHeight};
		}else if(document.documentElement&&document.documentElement.clientWidth){
			return{
				w:document.documentElement.clientWidth,
				h:document.documentElement.clientHeight
			};
		}else if(document.body&&document.body.clientWidth){
			return{
				w:document.body.clientWidth,
				h:document.body.clientHeight
			};
		}
	}

	var bubble={
		_closed:false,
		_doc:doc,
		node:node,
		close:function(){
			if(!this._closed){
				YAHOO.util.Event.purgeElement(this.content, false);
				this._doc.body.removeChild(this._div);
				this._doc=null;
				this._div=null;
				this._content=null;
				this._closed=true;
				this.node = null;
			}
		}
	};

	var dims=getWindowDims();
	var docWidth=dims.w;
	var docHeight=dims.h;

	var margins=Timeline.Graphics._bubbleMargins;
	contentWidth=parseInt(contentWidth,10);
	contentHeight=parseInt(contentHeight,10);
	var bubbleWidth=margins.left+contentWidth+margins.right;
	var bubbleHeight=margins.top+contentHeight+margins.bottom;

	var pngIsTranslucent=Timeline.Graphics.pngIsTranslucent;
	var urlPrefix=Timeline.urlPrefix;

	var setImg=function(elmt,url,width,height){
		elmt.style.position="absolute";
		elmt.style.width=width+"px";
		elmt.style.height=height+"px";
		if(pngIsTranslucent){
			elmt.style.background="url("+url+")";
		}else{
			elmt.style.filter="progid:DXImageTransform.Microsoft.AlphaImageLoader(src='"+url+"', sizingMethod='crop')";
		}
	}
	var div=doc.createElement("div");
	div.style.width=bubbleWidth+"px";
	div.style.height=bubbleHeight+"px";
	div.style.position="absolute";
	div.style.zIndex=20000;
	bubble._div=div;

	var divInner=doc.createElement("div");
	divInner.style.width="100%";
	divInner.style.height="100%";
	divInner.style.position="relative";
	div.appendChild(divInner);

	var createImg=function(url,left,top,width,height){
		var divImg=doc.createElement("div");
		divImg.style.left=left+"px";
		divImg.style.top=top+"px";
		setImg(divImg,url,width,height);
		divInner.appendChild(divImg);
	}

	createImg(urlPrefix+"RapidSuite/images/rapidjs/component/timeline/bubble-top-left.png",0,0,margins.left,margins.top);
	createImg(urlPrefix+"RapidSuite/images/rapidjs/component/timeline/bubble-top.png",margins.left,0,contentWidth,margins.top);
	createImg(urlPrefix+"RapidSuite/images/rapidjs/component/timeline/bubble-top-right.png",margins.left+contentWidth,0,margins.right,margins.top);

	createImg(urlPrefix+"RapidSuite/images/rapidjs/component/timeline/bubble-left.png",0,margins.top,margins.left,contentHeight);
	createImg(urlPrefix+"RapidSuite/images/rapidjs/component/timeline/bubble-right.png",margins.left+contentWidth,margins.top,margins.right,contentHeight);

	createImg(urlPrefix+"RapidSuite/images/rapidjs/component/timeline/bubble-bottom-left.png",0,margins.top+contentHeight,margins.left,margins.bottom);
	createImg(urlPrefix+"RapidSuite/images/rapidjs/component/timeline/bubble-bottom.png",margins.left,margins.top+contentHeight,contentWidth,margins.bottom);
	createImg(urlPrefix+"RapidSuite/images/rapidjs/component/timeline/bubble-bottom-right.png",margins.left+contentWidth,margins.top+contentHeight,margins.right,margins.bottom);

	var divClose=doc.createElement("div");
	divClose.style.left=(bubbleWidth-margins.right+Timeline.Graphics._bubblePadding-16-2)+"px";
	divClose.style.top=(margins.top-Timeline.Graphics._bubblePadding+1)+"px";
	divClose.style.cursor="pointer";
	setImg(divClose,urlPrefix+"RapidSuite/images/rapidjs/component/timeline/close-button.png",16,16);
	Timeline.DOM.registerEventWithObject(divClose,"click",bubble,bubble.close);
	divInner.appendChild(divClose);

	var divContent=doc.createElement("div");
	divContent.style.position="absolute";
	divContent.style.left=margins.left+"px";
	divContent.style.top=margins.top+"px";
	divContent.style.width=contentWidth+"px";
	divContent.style.height=contentHeight+"px";
	divContent.style.overflow="auto";
	divContent.style.background="white";
	divContent.style.cursor="pointer";
	divInner.appendChild(divContent);
	bubble.content=divContent;

	(function(){
		if(pageX-Timeline.Graphics._halfArrowWidth-Timeline.Graphics._bubblePadding>0&&
		pageX+Timeline.Graphics._halfArrowWidth+Timeline.Graphics._bubblePadding<docWidth){

			var left=pageX-Math.round(contentWidth/2)-margins.left;
			left=pageX<(docWidth/2)?
			Math.max(left,-(margins.left-Timeline.Graphics._bubblePadding)):
			Math.min(left,docWidth+(margins.right-Timeline.Graphics._bubblePadding)-bubbleWidth);

			if(pageY-Timeline.Graphics._bubblePointOffset-bubbleHeight>0){
				var divImg=doc.createElement("div");

				divImg.style.left=(pageX-Timeline.Graphics._halfArrowWidth-left)+"px";
				divImg.style.top=(margins.top+contentHeight)+"px";
				setImg(divImg,urlPrefix+"RapidSuite/images/rapidjs/component/timeline/bubble-bottom-arrow.png",37,margins.bottom);
				divInner.appendChild(divImg);

				div.style.left=left+"px";
				div.style.top=(pageY-Timeline.Graphics._bubblePointOffset-bubbleHeight+
				Timeline.Graphics._arrowOffsets.bottom)+"px";

				return;
			}else if(pageY+Timeline.Graphics._bubblePointOffset+bubbleHeight<docHeight){
				var divImg=doc.createElement("div");

				divImg.style.left=(pageX-Timeline.Graphics._halfArrowWidth-left)+"px";
				divImg.style.top="0px";
				setImg(divImg,urlPrefix+"RapidSuite/images/rapidjs/component/timeline/bubble-top-arrow.png",37,margins.top);
				divInner.appendChild(divImg);

				div.style.left=left+"px";
				div.style.top=(pageY+Timeline.Graphics._bubblePointOffset-
				Timeline.Graphics._arrowOffsets.top)+"px";

				return;
			}
		}

		var top=pageY-Math.round(contentHeight/2)-margins.top;
		top=pageY<(docHeight/2)?
		Math.max(top,-(margins.top-Timeline.Graphics._bubblePadding)):
		Math.min(top,docHeight+(margins.bottom-Timeline.Graphics._bubblePadding)-bubbleHeight);

		if(pageX-Timeline.Graphics._bubblePointOffset-bubbleWidth>0){
			var divImg=doc.createElement("div");

			divImg.style.left=(margins.left+contentWidth)+"px";
			divImg.style.top=(pageY-Timeline.Graphics._halfArrowWidth-top)+"px";
			setImg(divImg,urlPrefix+"RapidSuite/images/rapidjs/component/timeline/bubble-right-arrow.png",margins.right,37);
			divInner.appendChild(divImg);

			div.style.left=(pageX-Timeline.Graphics._bubblePointOffset-bubbleWidth+
			Timeline.Graphics._arrowOffsets.right)+"px";
			div.style.top=top+"px";
		}else{
			var divImg=doc.createElement("div");

			divImg.style.left="0px";
			divImg.style.top=(pageY-Timeline.Graphics._halfArrowWidth-top)+"px";
			setImg(divImg,urlPrefix+"RapidSuite/images/rapidjs/component/timeline/bubble-left-arrow.png",margins.left,37);
			divInner.appendChild(divImg);

			div.style.left=(pageX+Timeline.Graphics._bubblePointOffset-
			Timeline.Graphics._arrowOffsets.left)+"px";
			div.style.top=top+"px";
		}
	})();

	doc.body.appendChild(div);

	return bubble;
};