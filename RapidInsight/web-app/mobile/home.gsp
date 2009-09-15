<html>
<head>
<title>RIMobile</title>
<meta name="viewport" content="width=device-width; initial-scale=1.0; maximum-scale=1.0; user-scalable=0;"/>
<link rel="apple-touch-icon" href="../images/mobile/iui-logo-touch-icon.png" />
<meta name="apple-touch-fullscreen" content="YES" />
<style type="text/css" media="screen">@import "../css/mobile/iui.css";</style>
<script type="application/x-javascript" src="../js/mobile/iui.js"></script>
<style type="text/css">
    .ri-objectdetails-expand{
        border: 1px solid #CFB39B;
        color: #666666;
        font-size: 10px;
        margin-top: 5px;
        overflow: hidden;
        padding: 0;
        background: transparent url( /RapidSuite/js/yui/assets/skins/sam/sprite.png ) repeat-x scroll 0 -1300px;
        border-color: #BFDAFF;
        cursor: pointer;
        width: 80px;
    }

</style>
</head>

<body>
	<%-------------------------------------------------------------------------------
										<Toolbar>
	 -------------------------------------------------------------------------------%>
    <div class="toolbar" id ="toolbar" name="toolbar">
        <ul class="leftButtons">
        	<li><a id="backButton" class="button" href="#">Back</a></li>
            <li><span class="button" style="display:none"><a id="refreshButton" href="#" target="_refresh">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</a></span></li>
        </ul>
        <h1 id="pageTitle"></h1>
        <ul class="rightButtons">
        	<a id="homeButton" class="button" href="#home" style="left:40%;text-align:center;display:none;width:60px;">RI Mobile</a>
            <a id="queriesButton" class="button" href="#" style="display:none;left:40%;text-align:center;width:60px;">Queries</a>
            <a id="logoutButton" class="button" href="../auth/logout" target="_self">Logout</a>
            <a id="searchButton" class="button" href="#searchForm" style="display: none;" title="Search">Search</a>
        </ul>
    </div>

    <form id="searchForm" name="searchForm" class="dialog" method="post" action="event.gsp">
        <fieldset>
            <h1>Event Search</h1>
            <a class="button leftButton" type="cancel">Cancel</a>
            <a class="button blueButton" type="submit">Search</a>
            <input id="search" type="text" name="query"/>
            <%
                def domainClasses = [];
                def domainClass = grailsApplication.getDomainClass("RsTopologyObject");
                domainClasses.add(domainClass);
                domainClasses.addAll(domainClass.getSubClasses());
                domainClasses = domainClasses.sort {it.fullName}
            %>
            <div id="searchInSelect" style="display:none"><label style="color:white">In:</label><g:select name="searchIn" from="${domainClasses.fullName}"></g:select></div>
        </fieldset>
    </form>
    <%-------------------------------------------------------------------------------
										</Toolbar>
	 -------------------------------------------------------------------------------%>

	<%-------------------------------------------------------------------------------
										<Home Page>
	 -------------------------------------------------------------------------------%>
    <ul id="home" title="RI Mobile" selected="true">
        <rui:include template="mobile/pages.gsp" model="${binding.variables}"></rui:include>
	</ul>
	<%-------------------------------------------------------------------------------
										</Home Page>
	 -------------------------------------------------------------------------------%>

    <rui:include template="mobile/errors.gsp" model="${binding.variables}"></rui:include>
    <rui:include template="mobile/commonScripts.gsp"></rui:include>
    <script type="text/javascript">
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
            if(page.id == 'searchForm'){
                return;
            }
             if(page.id == 'eventList' || page.id == 'historicalEventList' || page.id == 'inventoryList'){
                 document.getElementById('refreshButton').parentNode.style.display = '';
             }
             else{
                 document.getElementById('refreshButton').parentNode.style.display = 'none';
             }
            if(page.id == 'inventoryList' || page.id == 'objectDetails' || (page.id == 'query' && href && href.indexOf('topology') > -1)){
                document.getElementById('searchInSelect').style.display = '';
            }
            else{
                document.getElementById('searchInSelect').style.display = 'none';
            }

            if(page.id == 'eventList' || page.id == 'historicalEventList' || page.id == 'inventoryList' || page.id == 'query'){
                document.getElementById('pageTitle').style.display = 'none'
                var queriesButton = document.getElementById('queriesButton')
                var homeButton = document.getElementById('homeButton')
                if(page.id == 'query'){
                   homeButton.style.display = '';
                   queriesButton.style.display = 'none';
                }
                else{
                    homeButton.style.display = 'none';
                    queriesButton.style.display = '';
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
            }
            else{
                document.getElementById('pageTitle').style.display = ''
                document.getElementById('queriesButton').style.display = 'none';
                document.getElementById('homeButton').style.display = 'none';
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
