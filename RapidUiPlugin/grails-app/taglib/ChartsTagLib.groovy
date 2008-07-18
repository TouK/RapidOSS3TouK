class ChartsTagLib {
	def Colors = ["0x0000ff", "0x00ff00", "0xff0000", "0xff00ff"];

	def hello = { attrs, body ->
		out << "<h1>Hello World"
	}

	def includeYUI = { attrs, head ->

		out << """
            



			"""
	}
	def insertTextArea = { attrs, body ->

		out << """
			<textarea id="${attrs.textAreaId}" rows="2" cols="40">
			</textarea>
			<script type="text/javascript">
			document.getElementById("${attrs.textAreaId}").value = "";

			</script>


		"""
	}

	def test = { attrs, body ->

		out << """
					${attrs.dataUrl[Integer.parseInt(attrs.dataURLIndex)]}

		"""
	}

	def style( container, width, height ){
		def styleString = """
			<style type="text/css">
				#${container}
				{
					width: ${width}px;
					height: ${height}px;
				}
			</style>
		"""

		return styleString;
	}

	def yAxis( id, min, max ) {
		def axisString = """
			var ${id}yAxis = new YAHOO.widget.NumericAxis();
			${id}yAxis.minimum = ${min};
			${id}yAxis.maximum = ${max};
		"""

		return axisString;
	}

	def dataSource( name, url, index, fields, fieldCount, type, node ) {

		def dataString = """
			var ${name} = new YAHOO.util.DataSource( "${url[index]}?" );
			//use POST so that IE doesn't cache the data
			${name}.connMethodPost = true;
			${name}.responseType = YAHOO.util.DataSource.TYPE_${type};
			${name}.responseSchema =
			{
					resultsList: "${node}",
					resultNode: "${node}",
					"""

					dataString +="fields:[";
					for( def i = 0; i < fieldCount; i++)
					{
						dataString +="\"${fields[i]}\""
						if(i != fieldCount - 1)
							dataString += ",";
					}
					dataString += "]";



				dataString += """
			};
		"""
		return dataString;
	}



	def baseColumnChart( name, fields, seriesDef,polling){
		def chartString = """
			var ${name} = new YAHOO.widget.ColumnChart( "${name}", null,
			{
				xField: "${fields[0]}",
				yField: "${fields[1]}",
				series: ${seriesDef},
				yAxis: ${name}yAxis,
				polling: ${polling},
				//only needed for flash player express install
				expressInstall: "assets/expressinstall.swf"
			});
		"""
		return chartString;
	}

	def basePieChart( name, fields, polling){
		def chartString = """
			var ${name} = new YAHOO.widget.PieChart( "${name}", null,
			{
				categoryField: "${fields[0]}",
				dataField: "${fields[1]}",
				polling: ${polling},
				//only needed for flash player express install
				expressInstall: "assets/expressinstall.swf"
			});
		"""
		return chartString;
	}

	def baseLineChart( name, fields, seriesDef, polling){
		def chartString = """
			var ${name} = new YAHOO.widget.LineChart( "${name}", null,
			{
				xField: "${fields[0]}",
				series: ${seriesDef},
				yAxis: ${name}yAxis,
				polling: ${polling},
				//only needed for flash player express install
				expressInstall: "assets/expressinstall.swf",
				dataTipFunction: YAHOO.example.getDataTipText
			});
		"""
		return chartString;
	}

	def check (attrs){

		if( !attrs.id )
		{
			throwTagError("Tag [javascript] is missing required attribute [id]")
			return;
		}

		if( !attrs.dataUrl )
		{
			throwTagError("Tag [javascript] is missing required attribute [dataUrl]")
			return;
		}

		if( !attrs.fields )
		{
			throwTagError("Tag [javascript] is missing required attribute [fields]")
			return;
		}
		if( !attrs.fieldCount)
		{
			throwTagError("Tag [javascript] is missing required attribute [fieldsCount]")
			return;
		}

		if( attrs.dataType != "XML" && attrs.dataType != "JSON"   )
		{
			throwTagError("Tag [dataType] has invalid value")
			return;
		}


	}

	def yuiBarChart = { attrs, body ->

		check(attrs);

		// default values
		def width = 500
		def height = 350
		def min = 0
		def max = 100
		def polling = 0


		if( attrs.width )
			width = Integer.parseInt(attrs.width)

		if( attrs.heigth )
			heigth = attrs.heigth

		if( attrs.min )
			min = attrs.min

		if( attrs.max )
			max = attrs.max

		if( attrs.polling )
			polling = attrs.polling

		def dataIndex = attrs.dataURLIndex;


		out << style( attrs.id, width, height )
		out << """
		<div id="${attrs.id}">Unable to load Flash content. The YUI Charts Control requires Flash Player 9.0.45 or higher. You can download the latest version of Flash Player from the <a href="http://www.adobe.com/go/getflashplayer">Adobe Flash Player Download Center</a>.</p></div>
		<script type="text/javascript">
		YAHOO.widget.Chart.SWFURL = "js/yui/charts/assets/charts.swf";

		"""
		out << series(attrs);
		out << yAxis( attrs.id, min, max );
		out << baseColumnChart( attrs.id, attrs.fields, "seriesDef${attrs.id}", attrs.polling);

		out << barClickedHandler( attrs.textAreaId, attrs.id, attrs.fields )
		out << multipleSources( attrs);

		out << "</script>"
		def size = Integer.parseInt(attrs.dataSize);
		for( def i = 0; i < size; i++ )
		{
			out << """<button onclick="javascript:setDatasource${attrs.id}${i}();">Datasoure ${i}</button>"""
		}
	}

	def barClickedHandler ( container, name, fields ) {
		def handlerString = """
			function fnCallback(e) { document.getElementById("${container}").value = "In ${name} clicked on " + e.item.${fields[0]} + " " + e.item.${fields[1]};}
	    	${name}.subscribe("itemClickEvent", fnCallback);
	    """

	    return handlerString;

	}

	def multipleSources( attrs ){

		def multDataString = "";
		def fieldCount = 2;
		if(attrs.fieldCount)
			fieldCount = Integer.parseInt(attrs.fieldCount);
		def size = Integer.parseInt(attrs.dataSize);

		for( def i = 0; i < size; i++ )
		{
			multDataString += dataSource( attrs.id + "Data" + i , attrs.dataUrl, i, attrs.fields ,fieldCount, attrs.dataType, attrs.node )
			multDataString += """
				function setDatasource${attrs.id}${i}()
				{
					document.getElementById("${attrs.textAreaId}").value = "";
			"""
					for( int k = 0; k < size; k++ ){
						multDataString += """
							if( ${i} != ${k} )
							{
								${attrs.id}Data${k}.clearInterval(${attrs.id}._pollingID);
							}
						"""
					}
					multDataString += """
						${attrs.id}.set("dataSource", ${attrs.id}Data${i});
				}

				"""
		}
		return multDataString;

	}


	def yuiPieChart = { attrs, body ->

		check(attrs);

		// default values
		def width = 500
		def height = 350
		def min = 0
		def max = 100
		def polling = 0


		if( attrs.width )
			width = attrs.width

		if( attrs.heigth )
			heigth = attrs.heigth

		if( attrs.min )
			min = attrs.min

		if( attrs.max )
			max = attrs.max

		if( attrs.polling )
			polling = attrs.polling


		def dataIndex = attrs.dataURLIndex;


		out << style( attrs.id, width, height )
		out << """
		<div id="${attrs.id}">Unable to load Flash content. The YUI Charts Control requires Flash Player 9.0.45 or higher. You can download the latest version of Flash Player from the <a href="http://www.adobe.com/go/getflashplayer">Adobe Flash Player Download Center</a>.</p></div>
		<script type="text/javascript">
		YAHOO.widget.Chart.SWFURL = "js/yui/charts/assets/charts.swf";

		"""

		out << basePieChart( attrs.id, attrs.fields, attrs.polling);
		out << barClickedHandler( attrs.textAreaId, attrs.id, attrs.fields )
		out << multipleSources( attrs);
		out << "</script>"
		def size = Integer.parseInt(attrs.dataSize);
		for( def i = 0; i < size; i++ )
		{
			out << """<button onclick="javascript:setDatasource${attrs.id}${i}();">Datasoure ${i}</button>"""
		}
	}

	def series ( attrs){

		def clorIndex = 0;

		def seriesCount;
		def barWidth = 20;
		def serieColors = Colors;
		if (attrs.barWidth)
			barWidth = Integer.parseInt(attrs.barWidth);

		if (attrs.colorIndex)
			clorIndex = Integer.parseInt(attrs.colorIndex);

		def clor = Colors[clorIndex];

		if(attrs.fieldCount)
			seriesCount = Integer.parseInt(attrs.fieldCount) - 1;




		def seriesString = """
			var seriesDef${attrs.id} =
			[
				"""
				for(int a = 0; a < seriesCount; a++)
				{
					seriesString += """
					{
						yField: "${attrs.fields[a+1]}" ,
						displayName: "${attrs.fields[a+1]}",
						style:
						{
							"""
							if(seriesCount >= 2)
								seriesString += "color: ${Colors[a]},"
						    else
						    	seriesString += "color: ${clor},"
						    seriesString += """
							size: ${barWidth}
						}
					}
					"""
					if( a + 1 != seriesCount)
						seriesString += ","
				}
				seriesString += """

			];
		"""
		return seriesString;
	}


	def yuiLineChart = { attrs, body ->

		check(attrs);

		// default values
		def width = 500
		def height = 350
		def min = 0
		def max = 100
		def polling = 0

		if( attrs.width )
			width = attrs.width

		if( attrs.heigth )
			heigth = attrs.heigth

		if( attrs.min )
			min = attrs.min

		if( attrs.max )
			max = attrs.max

		if( attrs.polling )
			polling = attrs.polling


		def dataIndex = attrs.dataURLIndex;


		out << style( attrs.id, width, height )
		out << """
		<div id="${attrs.id}">Unable to load Flash content. The YUI Charts Control requires Flash Player 9.0.45 or higher. You can download the latest version of Flash Player from the <a href="http://www.adobe.com/go/getflashplayer">Adobe Flash Player Download Center</a>.</p></div>
		<script type="text/javascript">
		YAHOO.widget.Chart.SWFURL = "js/yui/charts/assets/charts.swf";


		YAHOO.example.getDataTipText = function( item, index, series )
		{
			var toolTipText = series.displayName + " for " + item.Name;
			toolTipText += "\\n" + item[series.yField];
			return toolTipText;
		}

		"""

		out << series(attrs);
		out << yAxis( attrs.id, min, max );
		out << baseLineChart( attrs.id, attrs.fields, "seriesDef${attrs.id}", attrs.polling);

		out << barClickedHandler( attrs.textAreaId, attrs.id, attrs.fields )
		out << multipleSources( attrs);

		out << "</script>"
		def size = Integer.parseInt(attrs.dataSize);
		for( def i = 0; i < size; i++ )
		{
			out << """<button onclick="javascript:setDatasource${attrs.id}${i}();">Datasoure ${i}</button>"""
		}
	}


}