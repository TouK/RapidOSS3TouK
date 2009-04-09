<script type="text/javascript">
    function getURLParam(strParamName){
        var strReturn = "";
        var strHref = window.location.search;
        if ( strHref.indexOf("?") > -1 ){
            var strQueryString = strHref.substr(strHref.indexOf("?")+1);
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
        return decodeURIComponent(strReturn);
    }

    function getURLParams(){
        var paramMap={};

        var strHref = window.location.search;

        if ( strHref.indexOf("?") > -1 ){
            var strQueryString = strHref.substr(strHref.indexOf("?")+1);
            var aQueryString = strQueryString.split("&");
            for ( var iParam = 0; iParam < aQueryString.length; iParam++ ){
                    var aParam = aQueryString[iParam].split("=");
                    paramMap[aParam[0]]=decodeURIComponent(aParam[1]);
            }
        }
        return paramMap;
    }
	function getMapSaveParams()
	{
		var topologyMap	=YAHOO.rapidjs.Components['topologyMap'];
		mapData=topologyMap.getMapData();
		var saveParams={mode:'create', nodes:mapData.nodes, nodePropertyList:mapData.nodePropertyList,layout:topologyMap.getLayout(),mapType:topologyMap.getMapType()};
		return saveParams;
	}
    function getMapUpdateParams(mapId)
	{
        var updateParams=getMapSaveParams();
        updateParams["mapId"]=mapId;
        updateParams["mode"]='edit';
        return updateParams;
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
        className:'r-mapTree-mapAdd',
        scope:this,
        tooltip: 'Save Map',
        click:function() {
            var queryForm = YAHOO.rapidjs.Components['saveMapForm'];
        	queryForm.show(createURL('mapForm.gsp',getMapSaveParams()));
        	queryForm.popupWindow.show();
        }
    });
    tree.poll();
</script>