YAHOO.namespace('rapidjs', 'rapidjs.component');
YAHOO.rapidjs.component.PieChart = function(container, config){
    YAHOO.rapidjs.component.PieChart.superclass.constructor.call(this,container, config);
    YAHOO.ext.util.Config.apply(this, config);

    this.dataUrl = config.url;
    this.dataType = config.dataType;
    this.swfURL = config.swfURL;
    this.width = config.width || 500;
    
    this.chart = null;
    YAHOO.widget.Chart.SWFURL = this.swfURL;


    //this.initializeTask = new YAHOO.ext.util.DelayedTask(this.handleChart, this);
    
    this.dataSource = new YAHOO.util.DataSource(this.dataUrl);
	this.dataSource.responseType = YAHOO.util.DataSource[this.dataType];
   	this.dataSource.responseSchema =
    {
        resultNode: "Item",
        fields: [
	        { key: "name" },
	        { key: "count" }
	    ]
    };

    this.header = YAHOO.ext.DomHelper.append(this.container, {tag:'div'});
    this.toolbar = new YAHOO.rapidjs.component.tool.ButtonToolBar(this.header, {title:this.title});
    this.toolbar.addTool(new YAHOO.rapidjs.component.tool.LoadingTool(document.body, this));
    this.toolbar.addTool(new YAHOO.rapidjs.component.tool.SettingsTool(document.body, this));
    this.toolbar.addTool(new YAHOO.rapidjs.component.tool.ErrorTool(document.body, this));
    this.body = YAHOO.ext.DomHelper.append(this.container, {tag:'div'}, true);

    this.chart=null;
    this.isRendered = false;
    this.layoutCreated=false;   //true when external layoutcreated is called 
    this.chartCreated=false;   //true when chart content ready event occured
    this.chartLoaded=false;    //false before every poll , true after chart is loaded
    this.render();

    /*
    this.pr = YAHOO.ext.DomHelper.append(this.body.dom, {tag:'p',html:'ali ata bak'}, true);

    */
    
    
    /*
    this.chart.subscribe("beforeDataFieldChange",function(e){
        //alert("data changed in chart");
    });

    this.chart.subscribe("beforeCategoryFieldChange",function(e){
        //alert("cat changed in chart");        
    });

    this.chart.subscribe("contentReady",function(){
        alert("content ready in chart");
        createLayout();
    });
    
    this.chart.subscribe("itemClickEvent",function(e){
        alert("item clicked in chart");
    });
    */
    
    //alert(domindex);
    //alert(el.attributes[2]);
    //var output=""
    //for(c in this.chart)
        //output+="---->"+c+":"+this.chart[c]+"\n"
    //var el=document.getElementById(this.chart._id);
    //alert(el.attributes[2]);

    /*
    var output=""
    for(c in el)
        output+="---->"+c+":"+el[c]+"\n"
    alert(output);
    */


}


YAHOO.extend(YAHOO.rapidjs.component.PieChart, YAHOO.rapidjs.component.PollingComponentContainer, {
    poll:function()
    {
      this.chartLoaded=false;
      YAHOO.rapidjs.component.PieChart.superclass.poll.call(this);      
    },
    render:function()
    {
        this.chart=new YAHOO.widget.PieChart( this.body.dom, null,
        {
            dataField: "count",
            categoryField: "name",
            style:
            {
                padding: 10,
                legend:
                {
                    display: "right",
                    padding: 10,
                    spacing: 5,
                    font:
                    {
                        family: "Arial",
                        size: 13
                    }
                }
            }
            ,wmode: "Transparent"
            //only needed for flash player express install
            //expressInstall: "assets/expressinstall.swf"
        });


        this.chart.subscribe("contentReady",function(){
            //alert("chart content ready");
            //alert("chart state is"+this.chartCreated);
            this.chartCreated=true;
        },this,true);

        this.isRendered = true;
    },
    handleSuccess: function (response)
	{
        //alert("handle success");
        this.response = response;
        //this.handleChart();
		var respond =  this.dataSource.parseXMLData(null,response.responseXML);
        this.chart._loadDataHandler("", respond, false);
	},
    handleChart : function()
    {
        if ( !this.chartCreated  )
        {
            alert("gonna wait");
            this.initializeTask.delay(100);
        }
        else
        {


             if(!this.chartLoaded)
             {
                alert("refresh chart");
                var respond =  this.dataSource.parseXMLData(null,this.response.responseXML);
                // alert("refresh chart 2");
                this.chart._loadDataHandler("", respond, false);
                // alert("refresh chart 3");
                this.chartLoaded=true;
                // alert("refresh chart 4");
                this.initializeTask.delay(100);
                // alert("refresh chart 5");
             }
             /*
             else if (!this.layoutCreated )
             {
                 //createLayout();
                 alert("creating layout");
                 var layout = new YAHOO.widget.Layout({
                    units: [
                            { position: 'center',body:this.container.id ,resize:true }
                    ]
                 });
                 layout.render();

                 this.layoutCreated=true;
                 
             }
             */


        }
       
    },                 
    resize: function(width, height){
        //alert("bein resized");
        this.body.setHeight( height - this.header.offsetHeight );
        this.body.setWidth( width);
     
    }
}
);
