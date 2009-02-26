<script type="text/javascript">
    function getURLParam(strParamName){
        var strReturn = "";
        var strHref = window.location.href;
        if ( strHref.indexOf("?") > -1 ){
            var strQueryString = strHref.substr(strHref.indexOf("?"));
            var aQueryString = strQueryString.split("&");
            for ( var iParam = 0; iParam < aQueryString.length; iParam++ ){
                if (
                    aQueryString[iParam].toLowerCase().indexOf(strParamName.toLowerCase() + "=") > -1 ){
                    var aParam = aQueryString[iParam].split("=");
                    strReturn = aParam[1];
                    break;
                }
            }
        }
        return unescape(strReturn);
    }
    var tree = YAHOO.rapidjs.Components['mapTree'];             
    var topologyMap = YAHOO.rapidjs.Components['topologyMap'];             
    tree.addToolbarButton({
        className:'r-filterTree-groupAdd',
        scope:this,
        tooltip: 'Add group',
        click:function() {
            var queryGroupForm = YAHOO.rapidjs.Components['saveMapGroupForm'];
        	queryGroupForm.show(createURL('mapGroupForm.gsp', {mode:'create'}));
        	queryGroupForm.popupWindow.show();
        }
    });
    tree.addToolbarButton({
        className:'r-filterTree-queryAdd',
        scope:this,
        tooltip: 'Save Map',
        click:function() {
            var queryForm = YAHOO.rapidjs.Components['saveMapForm'];
        	queryForm.show(createURL('mapForm.gsp', {mode:'create', nodes:topologyMap.getNodesString(), layout:topologyMap.getLayout()}));
        	queryForm.popupWindow.show();
        }
    });
    tree.poll();
</script>