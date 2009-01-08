<%--
  Created by IntelliJ IDEA.
  User: admin
  Date: Jan 7, 2009
  Time: 6:14:57 PM
  To change this template use File | Settings | File Templates.
--%>

<html>
<head>
    <meta name="layout" content="indexLayout"/>
</head>
<body>

<div id="ruleList">
       <div class="body">
            <h1>Your Notification List</h1>  <g:link class="create" action="create" controller="rsMessageRule">New Notification Rule</g:link>
            <div class="list">
                <table>
                    <thead>
                        <tr>
                            <td>Query</td>
                            <td>Delay</td>
                            <td>Clear Messages</td>
                            <td>Destination</td>
                        </tr>
                    </thead>
                    <tbody>
                      <%
                         def myRules= message.RsMessageRule.searchEvery("userId:4")
                      %>

                    <g:each in="${myRules}" status="i" var="rule">
                        <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                            <td>${search.SearchQuery.get(id:rule.searchQueryId)?.name?.encodeAsHTML()}</td>
                            <td>${rule.delay?.encodeAsHTML()}</td>
                            <td>${rule.clearAction?.encodeAsHTML()}</td>
                            <td>${rule.destinationType?.encodeAsHTML()}</td>
                        </tr>
                    </g:each>
                    </tbody>
                </table>
            </div>
        </div>
</div>

<script type="text/javascript">
var Dom = YAHOO.util.Dom, Event = YAHOO.util.Event;
    Event.onDOMReady(function() {
        var layout = new YAHOO.widget.Layout({
            units: [
                { position: 'top', body: 'top', resize: false, height:40},
                { position: 'center', body: "ruleList", resize: false, gutter: '1px' }

            ]
        });
        layout.on('render', function(){
        	var topUnit = layout.getUnitByPosition('top');
        	YAHOO.util.Dom.setStyle(topUnit.get('wrap'), 'background-color', '#BBD4F6')
            var header = topUnit.body;
            YAHOO.util.Dom.setStyle(header, 'border', 'none');
            var left = layout.getUnitByPosition('left').body;
            YAHOO.util.Dom.setStyle(left, 'top', '1px');
        });
        layout.render();
        var layoutLeft = layout.getUnitByPosition('left');
        layoutLeft.on('resize', function(){
            YAHOO.util.Dom.setStyle(layoutLeft.body, 'top', '1px');
        });

        window.layout = layout;

    })
</script>

</body>
</html>