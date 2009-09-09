/*
   Copyright (c) 2007-9, iUI Project Members
   See LICENSE.txt for licensing terms
 */


(function() {

    var slideSpeed = 20;
    var slideInterval = 0;

    var currentPage = null;
    var currentDialog = null;
    var currentWidth = 0;
    var currentHash = location.hash;
    var hashPrefix = "#_";
    var pageHistory = [];
    var newPageCount = 0;
    var checkTimer;
    var hasOrientationEvent = false;
    var portraitVal = "portrait";
    var landscapeVal = "landscape";

// *************************************************************************************************

    window.iui =
    {
        subscribers: {
            pageShown:[],
            pageContentReceived:[]
        },
        lastRequest: null,

        defaultFailureFunction : function(req, httpStatus) {
        },
        showPage: function(page, backwards, href, args)
        {
            if (page)
            {
                if (currentDialog)
                {
                    currentDialog.removeAttribute("selected");
                    currentDialog = null;
                }

                if (hasClass(page, "dialog"))
                    showDialog(page);
                else
                {
                    var fromPage = currentPage;
                    currentPage = page;

                    if (fromPage)
                        setTimeout(slidePages, 0, fromPage, page, backwards, href, args);
                    else
                        updatePage(page, fromPage, href, args);
                }
                iui.pageShown(page, href, args);
            }
        },

        pageShown: function(page, href, args){
            var functions = iui.subscribers['pageShown'];
            for(var index=0; index <functions.length; index ++){
                var func = functions[index];
                func(page, href, args)
            }
        },

        addSubscriber: function(event, func){
            if(iui.subscribers[event]){
                iui.subscribers[event].push(func);
            }
            else{
               throw new Error('No event is defined with name ' + event)
            }
        },

        /*
       * returns the last page visited. if remove is true, its removed from the list
       */
        getLastPage: function(remove)
        {
            var page = pageHistory[pageHistory.length - 1];
            if (remove)
            {
                pageHistory.pop();
            }
            return page;
        },


        showPageById: function(pageId)
        {
            var page = $(pageId);
            if (page)
            {

                var index = 0;
                var backwards = false;
                while(index < pageHistory.length){
                    if(pageHistory[index].pageId == pageId){
                        backwards = true;
                        break;
                    }
                    index++;
                }
                if (backwards)
                    pageHistory.splice(index, pageHistory.length);

                iui.showPage(page, backwards);
            }
        },


        /*
       * this function now has an ability of opening page with sliding backwards or forwards. Previous version was a default of forwards.
       */
        showPageByHref: function(href, args, method, replace, cb, backwards, failureFunc)
        {
            /*
            * each time an url is opened, it is added to url list
            */
            if (iui.lastRequest) {
                iui.abortRequest(iui.lastRequest);
            }
            var req = new XMLHttpRequest();
            iui.lastRequest = req;
            req.onerror = function()
            {
                if (cb)
                    cb(false);
            };

            req.onreadystatechange = function()
            {
                var httpStatus;
                if (req.readyState == 4)
                {
                    try
                    {
                        if (req.status !== undefined && req.status !== 0) {
                            httpStatus = req.status;
                        }
                        else {
                            httpStatus = 13030;
                        }
                    }
                    catch(e) {
                        httpStatus = 13030;
                    }
                    if (httpStatus >= 200 && httpStatus < 300 || httpStatus === 1223) {
                        if (replace)
                            replaceElementWithSource(replace, req.responseText);
                        else
                        {
                            var frag = document.createElement("div");
                            frag.innerHTML = req.responseText;
                            iui.insertPages(frag.childNodes, backwards, href, args);
                        }
                        iui.pageContentReceived(href, args, req.responseText);
                        if (cb)
                            setTimeout(cb, 1000, true);
                    }
                    else {
                        if (req.isAbort !== true) {
                            if (failureFunc) {
                                failureFunc(req, httpStatus)
                            }
                            else {
                                iui.defaultFailureFunction(req, httpStatus);
                            }
                        }

                    }
                }
            };

            if (args)
            {
                req.open(method || "POST", href, true);
                req.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
                req.setRequestHeader("Content-Length", args.length);
                req.send(args.join("&"));
            }
            else
            {
                req.open(method || "GET", href, true);
                req.send(null);
            }
        },

        pageContentReceived: function(href, args, responseText){
            var functions = iui.subscribers['pageContentReceived'];
            for(var index=0; index <functions.length; index ++){
                var func = functions[index];
                func(href, args, responseText)
            }
        },
        

        isRequestInProgress:function(request)
        {
            if (request) {
                return request.readyState !== 4 && request.readyState !== 0;
            }
            return false;
        },

        abortRequest: function(request) {
            if (iui.isRequestInProgress(request)) {
                request.isAbort = true;
                request.abort();
            }
        },

        /*
       * this function now has an ability of opening page with sliding backwards or forwards. Previous version was a default of forwards.
       */
        insertPages: function(nodes, backwards, href, args)
        {
            var targetPage;
            for (var i = 0; i < nodes.length; ++i)
            {
                var child = nodes[i];
                if (child.nodeType == 1)
                {
                    if (!child.id)
                        child.id = "__" + (++newPageCount) + "__";

                    var clone = $(child.id);
                    if (clone)
                        clone.parentNode.replaceChild(child, clone);
                    else
                        document.body.appendChild(child);

                    if (child.getAttribute("selected") == "true" || !targetPage)
                        targetPage = child;

                    --i;

                }
            }

            if (targetPage)
                iui.showPage(targetPage, backwards, href, args);
        },

        getSelectedPage: function()
        {
            for (var child = document.body.firstChild; child; child = child.nextSibling)
            {
                if (child.nodeType == 1 && child.getAttribute("selected") == "true")
                    return child;
            }
        },
        isNativeUrl: function(href)
        {
            for (var i = 0; i < iui.nativeUrlPatterns.length; i++)
            {
                if (href.match(iui.nativeUrlPatterns[i])) return true;
            }
            return false;
        },
        nativeUrlPatterns: [
            new RegExp("^http:\/\/maps.google.com\/maps\?"),
            new RegExp("^mailto:"),
            new RegExp("^tel:"),
            new RegExp("^http:\/\/www.youtube.com\/watch\\?v="),
            new RegExp("^http:\/\/www.youtube.com\/v\/")
        ]
    };

// *************************************************************************************************

    addEventListener("load", function(event)
    {
        var page = iui.getSelectedPage();
        if (page)
            iui.showPage(page);

        setTimeout(preloadImages, 0);
        setTimeout(checkOrientAndLocation, 0);
        checkTimer = setInterval(checkOrientAndLocation, 300);
    }, false);

    addEventListener("unload", function(event)
    {
        return;
    }, false);

    addEventListener("click", function(event)
    {
        var link = findParent(event.target, "a");
        if (link)
        {
            function unselect() {
                link.removeAttribute("selected");
            }

            function failure(req, httpStatus) {
                unselect();
                iui.defaultFailureFunction(req, httpStatus);
            }

            if (link.href && link.hash && link.hash != "#")
            {
                link.setAttribute("selected", "true");
                iui.showPage($(link.hash.substr(1)));
                setTimeout(unselect, 500);
            }
            else if (link == $("backButton"))
            {
                /* function of back button is now different from previous version, which just call history.back()
                 * backButton removes last element from url and page history lists, and opens new last element into the current page.
                 * in this way apge refreshing is done
                 */
                pageHistory.pop();
                var pageObj = pageHistory.pop();
                if(pageObj){
                     if(pageObj.href){
                        var page = document.getElementById(pageObj.pageId);
                        page.setAttribute("selected", "progress");
                        var selectedPage = currentPage;
                        iui.showPageByHref(pageObj.href, pageObj.args, null, null, null, true, failure);
                     }
                     else{
                         iui.showPageById(pageObj.pageId);
                     }
                }
            }
            else if (link.getAttribute("type") == "submit")
                submitForm(findParent(link, "form"));
            else if (link.getAttribute("type") == "cancel")
                cancelDialog(findParent(link, "form"));
            else if (link.target == "_replace")
            {
                link.setAttribute("selected", "progress");
                iui.showPageByHref(link.href, null, null, link, unselect, false, failure);
            }
            else if (link.target == "_open")
            {
                link.setAttribute("selected", "progress");
                iui.showPageByHref(link.href, null, null, null, unselect, false, failure);
            }
            else if (iui.isNativeUrl(link.href))
            {
                return;
            }
            else if (!link.target)
            {
                link.setAttribute("selected", "progress");
                iui.showPageByHref(link.href, null, null, null, unselect, false, failure);
            }
            else
                return;

            event.preventDefault();
        }
    }, true);

    addEventListener("click", function(event)
    {
        var div = findParent(event.target, "div");
        if (div && hasClass(div, "toggle"))
        {
            div.setAttribute("toggled", div.getAttribute("toggled") != "true");
            event.preventDefault();
        }
    }, true);

    function orientChangeHandler()
    {
        var orientation = window.orientation;
        switch (orientation)
                {
            case 0:
                setOrientation(portraitVal);
                break;

            case 90:
            case -90:
                setOrientation(landscapeVal);
                break;
        }
    }

    if (typeof window.onorientationchange == "object")
    {
        window.onorientationchange = orientChangeHandler;
        hasOrientationEvent = true;
        setTimeout(orientChangeHandler, 0);
    }

    function checkOrientAndLocation()
    {
        if (!hasOrientationEvent)
        {
            if (window.innerWidth != currentWidth)
            {
                currentWidth = window.innerWidth;
                var orient = currentWidth == 320 ? portraitVal : landscapeVal;
                setOrientation(orient);
            }
        }

        if (location.hash != currentHash)
        {
            var pageId = location.hash.substr(hashPrefix.length);
            iui.showPageById(pageId);
        }
    }

    function setOrientation(orient)
    {
        document.body.setAttribute("orient", orient);
        setTimeout(scrollTo, 100, 0, 1);
    }

    function showDialog(page)
    {
        currentDialog = page;
        page.setAttribute("selected", "true");

        if (hasClass(page, "dialog") && !page.target)
            showForm(page);
    }

    function showForm(form)
    {
        form.onsubmit = function(event)
        {
            event.preventDefault();
            submitForm(form);
        };

        form.onclick = function(event)
        {
            if (event.target == form && hasClass(form, "dialog"))
                cancelDialog(form);
        };
    }

    function cancelDialog(form)
    {
        form.removeAttribute("selected");
    }

    function updatePage(page, fromPage, href, args)
    {
        if (!page.id)
            page.id = "__" + (++newPageCount) + "__";

        location.href = currentHash = hashPrefix + page.id;
        pageHistory.push({pageId:page.id, href:href, args:args});

        var pageTitle = $("pageTitle");


        /* title attribute has two element deliminated by ':'
       * first one is used in the toolbar heading,
       * second one is used in back button
       * It is designed because sometimes normal title does not fit in back button size.
       * in this way, you can specify what to be in toolbar title and back button for that page seperately.
       */
        if (page.title)
        {
            //gets the first token in title
            var longTitle = page.title.split(":");
            pageTitle.innerHTML = longTitle[0];
        }
        if (page.localName.toLowerCase() == "form" && !page.target)
            showForm(page);

        var backButton = $("backButton");
        if (backButton)
        {
            var prevPage;
            var prevPageObj = pageHistory[pageHistory.length - 2];
            if(prevPageObj){
                prevPage = $(prevPageObj.pageId)
            }
            if (prevPage && !page.getAttribute("hideBackButton"))
            {
                backButton.style.display = "inline";
                var currentpage = $(pageHistory[pageHistory.length - 1].pageId);
                if (prevPage.title != currentPage.title)
                {
                    if (prevPage.title)
                    {
                        //gets the second element in title
                        var short = prevPage.title.split(":");
                        backButton.innerHTML = short[1] ? short[1] : short[0];
                    }
                    else
                        backButton.innerHTML = "Back";
                }
                else{
                    backButton.innerHTML = "Back"; 
                }
            }
            else
                backButton.style.display = "none";
        }
    }

    function slidePages(fromPage, toPage, backwards, href, args)
    {
        var axis = (backwards ? fromPage : toPage).getAttribute("axis");
        if (axis == "y")
            (backwards ? fromPage : toPage).style.top = "100%";
        else
            toPage.style.left = "100%";

        toPage.setAttribute("selected", "true");
        scrollTo(0, 1);
        clearInterval(checkTimer);

        var percent = 100;
        slide();
        var timer = setInterval(slide, slideInterval);

        function slide()
        {
            percent -= slideSpeed;
            if (percent <= 0)
            {
                percent = 0;
                if (!hasClass(toPage, "dialog"))
                    fromPage.removeAttribute("selected");
                clearInterval(timer);
                checkTimer = setInterval(checkOrientAndLocation, 300);
                setTimeout(updatePage, 0, toPage, fromPage, href, args);
            }

            if (axis == "y")
            {
                backwards
                        ? fromPage.style.top = (100 - percent) + "%"
                        : toPage.style.top = percent + "%";
            }
            else
            {
                fromPage.style.left = (backwards ? (100 - percent) : (percent - 100)) + "%";
                toPage.style.left = (backwards ? -percent : percent) + "%";
            }
        }
    }

    function preloadImages()
    {
        var preloader = document.createElement("div");
        preloader.id = "preloader";
        document.body.appendChild(preloader);
    }

    function submitForm(form)
    {
        iui.showPageByHref(form.action || "POST", encodeForm(form), form.method);
    }

    function encodeForm(form)
    {
        function encode(inputs)
        {
            for (var i = 0; i < inputs.length; ++i)
            {
                if (inputs[i].name)
                    args.push(inputs[i].name + "=" + escape(inputs[i].value));
            }
        }

        var args = [];
        encode(form.getElementsByTagName("input"));
        encode(form.getElementsByTagName("textarea"));
        encode(form.getElementsByTagName("select"));
        return args;
    }

    function findParent(node, localName)
    {
        while (node && (node.nodeType != 1 || node.localName.toLowerCase() != localName))
            node = node.parentNode;
        return node;
    }

    function hasClass(self, name)
    {
        var re = new RegExp("(^|\\s)" + name + "($|\\s)");
        return re.exec(self.getAttribute("class")) != null;
    }

    function replaceElementWithSource(replace, source)
    {
        var page = replace.parentNode;
        var parent = replace;
        while (page.parentNode != document.body)
        {
            page = page.parentNode;
            parent = parent.parentNode;
        }

        var frag = document.createElement(parent.localName);
        frag.innerHTML = source;

        page.removeChild(parent);

        while (frag.firstChild)
            page.appendChild(frag.firstChild);
    }

    function $(id) {
        return document.getElementById(id);
    }
    function ddd() {
        console.log.apply(console, arguments);
    }

})();
