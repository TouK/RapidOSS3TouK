<%@ page import="auth.Role" %>
<jsec:lacksRole name="${Role.ADMINISTRATOR}">
     <%
         response.sendRedirect("/RapidSuite/auth/unauthorized");
     %>
</jsec:lacksRole>
<script>
    var classTree = YAHOO.rapidjs.Components["classTree"];
    classTree.poll();
    var objectList = YAHOO.rapidjs.Components["objectList"];
    var toolbarEl = objectList.toolbar.el;
    var tds = toolbarEl.nextSibling.getElementsByTagName('td');
    YAHOO.util.Dom.setStyle(tds[3], 'display', 'none')
    YAHOO.util.Dom.setStyle(tds[4], 'display', 'none')
    objectList.renderCellFunction = function(key, value, data, el){
        if(key == "id"){
           YAHOO.util.Dom.setStyle(el, 'color', 'blue');
        }
        return value;
    }

    classTree.events['nodeClicked'].subscribe(function(xmlData){
    	var className = xmlData.getAttribute('name');
    	if(className != 'System' && className != 'Application'){
    		var domain = xmlData.getAttribute('logicalName');
    		objectList._setQuery('', 'id', 'asc', className, {domain:domain});
    	}

    }, this, true)
</script>