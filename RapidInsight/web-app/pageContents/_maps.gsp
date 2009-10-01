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
        return decodeURLParamValue(strReturn);
    }

    function getURLParams(){
        var paramMap={};

        var strHref = window.location.search;

        if ( strHref.indexOf("?") > -1 ){
            var strQueryString = strHref.substr(strHref.indexOf("?")+1);
            var aQueryString = strQueryString.split("&");
            for ( var iParam = 0; iParam < aQueryString.length; iParam++ ){
                    var aParam = aQueryString[iParam].split("=");
                    paramMap[aParam[0]]=decodeURLParamValue(aParam[1]);
            }
        }
        return paramMap;
    }
    function decodeURLParamValue(value)
    {
    	return decodeURIComponent(value).replace(/\+/g,"%20");
    }
	function getMapSaveParams()
	{
		var topologyMap	=YAHOO.rapidjs.Components['topologyMap'];
		mapData=topologyMap.getMapData();
		var saveParams={mode:'create', nodes:mapData.nodes, nodePropertyList:mapData.nodePropertyList,mapPropertyList:mapData.mapPropertyList,mapProperties:mapData.mapProperties,layout:topologyMap.getLayout()};
		return saveParams;
	}
    function getMapUpdateParams(mapId)
	{
        var updateParams=getMapSaveParams();
        updateParams["id"]=mapId;
        updateParams["mode"]='edit';
        return updateParams;
    }


    var eventsGrid = YAHOO.rapidjs.Components['eventsGrid'];
    eventsGrid.renderCellFunction = function(key, value, data, el){
        if(key == "lastNotifiedAt" || key == "changedAt" || key == 'createdAt' || key == 'clearedAt' || key == 'willExpireAt'){
            if(value == "0" || value == "")
            {
                return "never"
            }
            else
            {
                try
                {
                    var d = new Date();
                    d.setTime(parseFloat(value))
                    return d.format("d M H:i:s");
                }
                catch(e)
                {}
            }
        }
        return value;
     }
    
    var tree = YAHOO.rapidjs.Components['mapTree'];
    var topologyMap = YAHOO.rapidjs.Components['topologyMap'];
    tree.addToolbarButton({
        className:'r-filterTree-groupAdd',
        scope:this,
        tooltip: 'Add group',
        click:function() {
            var queryGroupForm = YAHOO.rapidjs.Components['saveMapGroupForm'];
            queryGroupForm.popupWindow.show();
            queryGroupForm.show(createURL('mapGroupForm.gsp', {mode:'create'}));
        }
    });
    tree.addToolbarButton({
        className:'r-mapTree-mapAdd',
        scope:this,
        tooltip: 'Save Map',
        click:function() {
            var queryForm = YAHOO.rapidjs.Components['saveMapForm'];
            queryForm.popupWindow.show();
            queryForm.show(createURL('mapForm.gsp',getMapSaveParams()));
        }
    });
    tree.poll();
</script>