<style>
	.warnLogLevel{
		color:red;
        font-weight:bold;
    }
	.fatalLogLevel{
		color:red;
        font-weight:bold;
    }
	.debugLogLevel{
		color:yellow;
        font-weight:bold;
    }
	.infoLogLevel{
		color:green;
        font-weight:bold;
    }
	.errorLogLevel{
		color:red;
        font-weight:bold;
    }
    .exceptionLogLine{
        color:red;
        font-weight:bold;
    }
</style>
<div id="logContent">

</div>
<script>
    var config = {
        id:"logViewer",
        url:getUrlPrefix()+"viewLog/getLog",
        pollingInterval:1
    }
    YAHOO.util.Event.onDOMReady(function(){
        var logViewer = new YAHOO.rapidjs.component.LogViewer(document.getElementById("logContent"), config);
        YAHOO.rapidjs.Components["fileListTree"].events.nodeClicked.subscribe(function(data){
            logViewer.viewFile(data.getAttribute("name"));
        }, this, true)
        var resizeEventSubscriberTask = new YAHOO.ext.util.DelayedTask(function(){
            if(window.yuiLayout)
            {
                var centerUnit = window.yuiLayout.getUnitByPosition("center")
                var innerCenter = centerUnit.childLayout.getUnitByPosition("center")

                centerUnit.childLayout.on('resize', function() {
                    logViewer.resize(innerCenter.getSizes().body.w, innerCenter.getSizes().body.h);
                });
                logViewer.resize(innerCenter.getSizes().body.w, innerCenter.getSizes().body.h);
            }
            else
            {
                resizeEventSubscriberTask.delay(100);
            }
        }, this);
        resizeEventSubscriberTask.delay(100);
    })


</script>
