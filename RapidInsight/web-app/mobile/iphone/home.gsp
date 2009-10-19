<html>
<head>
<title>ROSSMobile</title>
<meta name="viewport" content="width=device-width; initial-scale=1.0"/>
<link rel="apple-touch-icon" href="${createLinkTo(dir:'images/mobile', file:'iui-logo-touch-icon.png')}"/>
<meta name="apple-touch-fullscreen" content="YES" />
<style type="text/css" media="screen">@import"${createLinkTo(dir:'css/mobile', file:'iui.css')}";</style>
<script type="application/x-javascript" src="${createLinkTo(dir:'js/mobile', file:'iui.js')}"></script>
</head>
<style>
    .ri-objectdetails-expand{
        border: 1px solid #CFB39B;
        color: #666666;
        font-size: 10px;
        margin-top: 5px;
        overflow: hidden;
        padding: 0;
        background: transparent url( ${createLinkTo(dir:'js/yui/assets/skins/sam', file:'sprite.png')} ) repeat-x scroll 0 -1300px;
        border-color: #BFDAFF;
        cursor: pointer;
        width: 80px;
    }
</style>
<body>
	<%-------------------------------------------------------------------------------
										<Toolbar>
	 -------------------------------------------------------------------------------%>
    <div class="toolbar" id ="toolbar" name="toolbar">
        <ul class="leftButtons">
        	<li><a id="backButton" class="button" href="#">Back</a></li>
            <li><span class="button" style="display:none"><a id="refreshButton" href="#" target="_refresh">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</a></span></li>
        </ul>
        <h1 id="pageTitle" style="display:none"></h1>
        <ul class="rightButtons">
        	<a id="homeButton" class="button" href="#home" style="display:none">Home</a>
            <a id="queriesButton" class="button" href="#" style="display:none">Queries</a>
            <rui:link id="eventsButton" class="button" url="mobile/iphone/event.gsp" style="display:none">Events</rui:link>
            <rui:link id="hEventsButton" class="button" url="mobile/iphone/historicalEvent.gsp" style="display:none">H. Events</rui:link>
            <rui:link id="inventoryButton" class="button" url="mobile/iphone/inventory.gsp" style="display:none">Inventory</rui:link>
            <rui:link id="objectReportsButton" class="button" url="mobile/iphone/objectReports.gsp" style="display:none">Reports</rui:link>
            <a id="logoutButton" class="button" href="${createLink(controller:'auth', action:'logout')}" target="_self">Logout</a>
            <a id="searchButton" class="button" href="#searchForm" style="display: none;" title="Search">Search</a>
        </ul>
    </div>

    <form id="searchForm" name="searchForm" class="dialog" method="post" action="${createLinkTo(dir:'mobile/iphone', file:'event.gsp')}">
        <fieldset>
            <h1>Event Search</h1>
            <input id="search" type="text" name="query"/>
            <%
                def domainClasses = [];
                def domainClass = grailsApplication.getDomainClass("RsTopologyObject");
                domainClasses.add(domainClass);
                domainClasses.addAll(domainClass.getSubClasses());
                domainClasses = domainClasses.sort {it.fullName}
            %>
            <div id="searchInSelect" style="display:none"><label style="color:white">In:</label><g:select name="searchIn" from="${domainClasses.fullName}"></g:select></div>
            <a class="button" type="cancel">Cancel</a>
            <a class="button blueButton" type="submit">Search</a>
        </fieldset>
    </form>
    <%-------------------------------------------------------------------------------
										</Toolbar>
	 -------------------------------------------------------------------------------%>

	<%-------------------------------------------------------------------------------
										<Home Page>
	 -------------------------------------------------------------------------------%>
    <ul id="home" selected="true" class="list">
        <rui:include template="mobile/contents/pages.gsp" model="${binding.variables}"></rui:include>
	</ul>
	<%-------------------------------------------------------------------------------
										</Home Page>
	 -------------------------------------------------------------------------------%>

    <div class="error" id="mobileErrors"></div>
    <rui:include template="mobile/iphone/commonScripts.gsp"></rui:include>
    <script type="text/javascript">

        iui.defaultFailureFunction = function(req, httpStatus){
            var errorEl = document.getElementById('mobileErrors');
            var innerHTML;
            if(httpStatus == -1){
                innerHTML = 'Request received timeout.'
            }
            else if(httpStatus == 13030){
                innerHTML = 'Server is not reachable.'
            }
            else if(httpStatus == 404){
                 innerHTML = 'Specified url cannot be found.'
            }
            else if (httpStatus == 500){
                innerHTML = 'Internal server error.'
            }
            errorEl.innerHTML = innerHTML
            iui.showPage(errorEl);
        }
        //Expand-Collapse javascript function for object details
        window.expandRelations = function(propertyName, relCount){
            var divEl = document.getElementById(propertyName + '_hiddenObjects');
            var buttonEl = document.getElementById(propertyName + '_expandButton');
            if(divEl.style.display == 'none'){
                divEl.style.display = '';
                buttonEl.innerHTML = 'Collapse'
            }
            else{
                divEl.style.display = 'none';
                buttonEl.innerHTML = 'Expand (' + (relCount - 10) + ')';
            }
         }
        iui.addSubscriber('pageShown', function(page, href, args){
            var buttonConfig = {
                homeButton: [],
                logoutButton: ['home'],
                searchButton: ['eventList', 'historicalEventList', 'inventoryList', 'query', 'eventDetails', 'historicalEventDetails', 'objectDetails', 'journals'],
                refreshButton: ['eventList', 'historicalEventList', 'inventoryList'],
                inventoryButton: ['objectDetails', 'objectReports'],
                objectReportsButton : ['objectReport'],
                eventsButton: ['eventDetails'],
                hEventsButton: ['historicalEventDetails'],
                queriesButton : ['eventList', 'historicalEventList', 'inventoryList'],
                searchInSelect: ['inventoryList', 'objectDetails']
            }
            if(page.id == 'searchForm'){
                return;
            }
            for(var button in buttonConfig){
                 var buttonEl = document.getElementById(button);
                 if(button == 'refreshButton'){
                    buttonEl = buttonEl.parentNode;
                 }
                 var isDisplayed = false;
                 var pages = buttonConfig[button];
                 for(var index = 0; index < pages.length; index ++){
                    var pageName = pages[index];
                    if(page.id == pageName){
                        isDisplayed = true;
                        break;
                    }
                 }
                 if(button == 'searchInSelect' && (page.id == 'query' && href && href.indexOf('topology') > -1)){
                    isDisplayed = true;
                 }
                 if(button == 'eventsButton' && (page.id == 'journals' && href && href.indexOf('isHistorical=true') < 0)){
                    isDisplayed = true;
                 }
                 if(button == 'hEventsButton' && (page.id == 'journals' && href && href.indexOf('isHistorical=true') > -1)){
                    isDisplayed = true;
                 }
                 if(button == 'homeButton' && page.id != 'home'){
                    isDisplayed = true;
                 }
                 if(isDisplayed){
                    buttonEl.style.display = '';
                 }
                 else{
                    buttonEl.style.display = 'none';
                 }

            }
             if(page.id == 'inventoryList'){
                 window.inventoryListHref = href;
             }
             if(page.id == 'objectDetails' || page.id == 'objectReports'){
                 var inventoryBtn = document.getElementById('inventoryButton');
                 inventoryBtn.setAttribute('href', window.inventoryListHref)
             }
             if(page.id == 'objectReports'){
                 window.lastReportsHref = href;
             }
             if(page.id == 'objectReport'){
                 var oReportsBtn = document.getElementById('objectReportsButton');
                 oReportsBtn.setAttribute('href', window.lastReportsHref)
             }
            if(page.id == 'eventList' || page.id == 'historicalEventList' || page.id == 'inventoryList'){
                var queriesButton = document.getElementById('queriesButton')
                var queriesLinks = document.getElementById('home').getElementsByTagName('a');
                if(page.id == 'eventList'){
                    queriesButton.setAttribute('href', queriesLinks[0].href)
                }
                else if(page.id == 'historicalEventList'){
                    queriesButton.setAttribute('href', queriesLinks[1].href)
                }
                else{
                    queriesButton.setAttribute('href', queriesLinks[2].href)
                }
            }
            if(page.id == 'query'){
                var sForm = document.getElementById('searchForm');
                if(href && href.indexOf('historicalEvent') > -1){
                     sForm.getElementsByTagName('h1')[0].innerHTML = 'Historical Event Search'
                     sForm.setAttribute('action', 'historicalEvent.gsp')
                }
                else if(href && href.indexOf('topology') > -1){
                     sForm.getElementsByTagName('h1')[0].innerHTML = 'Inventory Search'
                     sForm.setAttribute('action', 'inventory.gsp')
                }
                else{
                     sForm.getElementsByTagName('h1')[0].innerHTML = 'Event Search'
                     sForm.setAttribute('action', 'event.gsp')
                }
            }

        })

    </script>

</body>
</html>
