<html>
    <head>
        <title>Yui Charts API plugin</title>
		<meta name="layout" content="main" />

        <rui:javascript dir="yui/yahoo-dom-event" file="yahoo-dom-event.js"/>
        <rui:javascript dir="yui/element" file="element-beta-min.js"/>
        <rui:javascript dir="yui/datasource" file="datasource-min.js"/>
        <rui:javascript dir="yui/json" file="json-min.js"/>
        <rui:javascript dir="yui/connection" file="connection-min.js"/>
        <rui:javascript dir="yui/charts" file="charts-experimental-min.js"/>
    </head>
    <body>
   <%
    	def fields = ["Name","Value"]
    	def fields3 = ["Name","Value", "Value1"]

    	def DataURLs = ["generatedata.txt","generatedata2.txt","generatedata3.txt","generatedata4.txt"]

    	def fields2 = ["name","id"]
    	def DataURLs2 = ["a2.xml"]
    %>
    <table border = "1">
    <tr>
	<td><g:yuiBarChart width = "400" barWidth= "40" id="jsonChart" fieldCount= "3"  dataUrl='${DataURLs}' colorIndex = "3"  dataSize = "4" dataURLIndex ='0' fields='${fields3}' polling="3000" dataType = "JSON" node = "Results" textAreaId = "text1"/></td>
	<td><g:yuiBarChart width = "300" id="xmlChart" fieldCount= "2" dataUrl='${DataURLs2}' colorIndex = "3" Colors = '${Colors}' dataSize = "1" dataURLIndex ='0' fields='${fields2}' polling="3000" dataType = "XML" node = "Filter" textAreaId = "text1"/></td>
	<td>                                            
	<g:insertTextArea textAreaId = "text1"/>
	</td>
	</tr>
	<tr>
	<td><g:yuiPieChart width = "400" id="jsonChart2" dataUrl='${DataURLs}' fieldCount= "2" colorIndex = "1"  dataSize = "4" dataURLIndex ='0' fields='${fields}'  dataType = "JSON" node = "Results" textAreaId = "text2"/></td>
	<td><g:yuiPieChart id="xmlChart2" dataUrl='${DataURLs2}' colorIndex = "1"  fieldCount= "2"  dataSize = "1" dataURLIndex ='0' fields='${fields2}'  dataType = "XML" node = "Filter" textAreaId = "text2"/></td>
	<td>
	<g:insertTextArea textAreaId = "text2"/>
	</td>
	</tr>
	<tr>
	<td><g:yuiLineChart width = "400" id="jsonChart3" dataUrl='${DataURLs}' fieldCount= "3" colorIndex = "2"  dataSize = "4" dataURLIndex ='0' fields='${fields3}'  polling="3000" dataType = "JSON" node = "Results" textAreaId = "text3"/></td>
	<td><g:yuiLineChart id="xmlChart3" dataUrl='${DataURLs2}' colorIndex = "1" fieldCount= "2"  dataSize = "1" dataURLIndex ='0' fields='${fields2}'  polling="3000" dataType = "XML" node = "Filter" textAreaId = "text3"/></td>
	<td>
	<g:insertTextArea textAreaId = "text3"/>
	</td>
	</tr>
	</table>
     </body>
</html>
