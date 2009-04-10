<%
    def mode = params.mode;
    def componentId = params.componentId;
    def refreshComponent=params.refreshComponent?params.refreshComponent:'';

    def objectName = params.name?params.name:'';

    def inMaintenanceObject=RsInMaintenance.get(objectName:objectName);
    def inMaintenance=inMaintenanceObject!=null;
%>
   <script type="text/javascript">
    window.refreshDataComponent = function(){

        <g:if test="${refreshComponent}">
        var dataComponent = YAHOO.rapidjs.Components['${refreshComponent}'];
        dataComponent.poll();
        </g:if>

    }
    </script>

<div class="yui-navset yui-navset-top" style="margin-top:5px">
    <ul class="yui-nav">
        <li class="selected">
            <a onclick="YAHOO.rapidjs.Components['${componentId}'].show(createURL('inMaintenanceForm.gsp', {name:'${objectName}'}));">
                <em>In Maintenance</em>
            </a>
        </li>
        <li>
            <a onclick="YAHOO.rapidjs.Components['${componentId}'].show(createURL('inMaintenanceScheduleForm.gsp', {name:'${objectName}'}));">
                <em>Schedule</em>
            </a>
        </li>
    </ul>
    <div style="display:block;margin-top:10px;">
        <rui:formRemote method="POST" action="script/run/putInMaintenance?format=xml" componentId="${params.componentId}" onSuccess="window.refreshDataComponent">
            <table>
                <tr><td width="50%"><label>Object Name:</label></td><td width="50%"><input type="textbox" name="objectName" style="width:100px" value="${objectName.encodeAsHTML()}"/></td></tr>
                <tr><td width="50%"><label>In Maintenance:</label></td><td width="50%"><input type="checkbox" name="inMaintenance" ${inMaintenance ? 'checked=true' : ''} /></td></tr>
                <tr><td width="50%"><label>For:</label></td><td width="50%"><input type="textbox" name="minutes" style="width:100px" value=""/> Minutes</td></tr>
                <tr><td width="50%"><label>Info:</label></td><td width="50%"><input type="textbox" name="info" style="width:100px" value="${inMaintenance?inMaintenanceObject.info.encodeAsHTML():''}"/></td></tr>
                <g:if test="${inMaintenance}">
                    <tr><td width="50%"><label>Source:</label></td><td width="50%">${inMaintenanceObject.source}</td></tr>
                    <g:if test="${inMaintenanceObject?.ending!=new Date(0) && inMaintenanceObject.ending!=null}">
                    <tr><td width="50%"><label>Currently Until:</label></td><td width="50%">${inMaintenanceObject.ending}</td></tr>
                    </g:if>
                    <g:else>
                    <tr><td width="50%"><label>Currently Until:</label></td><td width="50%">Manually canceled.</td></tr>    
                    </g:else>
                </g:if>
            </table>
            <input type="hidden" name="model" value="maintenance"/>
        </rui:formRemote>
    </div>
</div>
