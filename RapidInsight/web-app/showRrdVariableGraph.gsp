<%@ page import="java.sql.Timestamp; java.text.SimpleDateFormat"%>
<%--
  Created by Crimson Editor.
  User: ender
  Date: Jul 1, 2009
  Time: 4:13:32 PM
--%>
<%
	def sourceName = params.name;
    def zoomLevel = params["zoomLevel"] ? params["zoomLevel"] : "24";
    def end = params["end"] ? params["end"] : "${System.currentTimeMillis()}";
    //def end = params["end"] ? params["end"] : "${DbUtils.getLastArchiveUpdate(dbName)}";
    def zoomRange = Long.parseLong(zoomLevel) * 60 * 60 * 1000
    def nextEnd = Long.parseLong(end) + zoomRange
    def previousEnd = Long.parseLong(end) - zoomRange

    def zoomLevels=[]
    zoomLevels.add([value:"1",label:"1h"]);
    zoomLevels.add([value:"6",label:"6h"]);
    zoomLevels.add([value:"24",label:"1d"]);
    zoomLevels.add([value:"120",label:"5d"]);
    zoomLevels.add([value:"720",label:"1m"]);
    zoomLevels.add([value:"2160",label:"3m"]);
    zoomLevels.add([value:"8760",label:"1y"]);


%>
<div>
    <script type="text/javascript">
        var graphHandler = function(componentId){
            this.componentId = componentId
            this.hideMaskTask = new YAHOO.ext.util.DelayedTask(this.hideGraphMask, this)
            YAHOO.rapidjs.Components[this.componentId].events['bodyCleared'].subscribe(this.destroy, this, true);
        }
        graphHandler.prototype = {
            showGraphMask: function(){
               YAHOO.rapidjs.Components[this.componentId].showMask();
               this.hideMaskTask.delay(120000);
            },
            hideGraphMask: function(){
               this.hideMaskTask.cancel();
               YAHOO.rapidjs.Components[this.componentId].hideMask();
            },
            destroy: function(){
                YAHOO.rapidjs.Components[this.componentId].events['bodyCleared'].unsubscribe(this.destroy, this);
                this.hideMaskTask.cancel();
            },
            redrawObjectGraph: function(name, zoomLevel, end){
                var params = {name:name, zoomLevel:zoomLevel, end:end};
                var url = createURL('showRrdVariableGraph.gsp', params);
                YAHOO.rapidjs.Components[this.componentId].show(url);
            }
        }
        window.graphHandler = new graphHandler('${params.componentId}');
        window.graphHandler.showGraphMask();
    </script>
    <div style="height:30px">
                <table>
                    <tbody>
                        <tr>
                            <td>
                                <div>
                                    <span style="font-weight:bold">Zoom:</span>
                                    <g:each var="zoomLevelItr" in="${zoomLevels}">
                                        <g:if test="${zoomLevelItr.value==zoomLevel}">
                                            <b>${zoomLevelItr.label}</b>
                                        </g:if>
                                        <g:else>
                                            <a class="ri-report-zoom-link" onclick="javascript:window.graphHandler.redrawObjectGraph('${params.name}', '${zoomLevelItr.value}', '${System.currentTimeMillis()}', '${params.componentId}')">${zoomLevelItr.label}</a>
                                        </g:else>
                                    </g:each>
                                </div>
                            </td>
                            <td>
                                <div style="padding-left:40px;">
                                    <a class="ri-report-previous-range" onclick="javascript:window.graphHandler.redrawObjectGraph('${params.name}',  '${zoomLevel}', '${previousEnd}', '${params.componentId}')">&#160;&#160;&#160;</a>
                                    <%
                                       SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
                                       def endTime = Long.parseLong(end);
                                       def startTime = endTime - zoomRange;
                                       def endString = format.format(new Timestamp(endTime))
                                       def startString = format.format(new Timestamp(startTime))
                                    %>
                                    <span style="font-weight:bold">${startString}</span><span style="padding-left:5px;padding-right:5px">TO</span><span  style="font-weight:bold">${endString}</span>
                                    <a class="ri-report-next-range" onclick="javascript:window.graphHandler.redrawObjectGraph('${params.name}',  '${zoomLevel}', '${nextEnd}', '${params.componentId}')">&#160;&#160;&#160;</a>
                                </div>
                            </td>
                            <td>
                                <div style="padding-left:20px;"><a class="graph-redraw-link" onclick="javascript:window.graphHandler.redrawObjectGraph('${params.name}',  '${zoomLevel}', '${end}', '${params.componentId}')">Redraw</a></div>
                            </td>
                        </tr>
                    </tbody>
                </table>

    </div>


    <div>
        <table align="center"><tr><td>
            <img src="${createLink(controller:'rrdVariable', action:'graph', params:[ startTime:previousEnd, name:params.name, endTime:end, rnd:System.currentTimeMillis()])}"  onload="window.graphHandler.hideGraphMask()">
        </td></tr></table>
    </div>

</div>

