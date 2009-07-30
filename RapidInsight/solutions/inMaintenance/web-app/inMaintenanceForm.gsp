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
                <tr><td width="20%"><label>Object Name:</label></td><td width="80%"><input type="textbox" name="objectName" style="width:100px" value="${objectName.encodeAsHTML()}"/></td></tr>
                <tr><td width="20%"><label>In Maintenance:</label></td><td width="80%"><input type="checkbox" name="inMaintenance" ${inMaintenance ? 'checked=true' : ''} /></td></tr>
                <tr><td width="20%"><label>For:</label></td><td width="80%"><input type="textbox" name="minutes" style="width:100px" value=""/> Minutes</td></tr>
                <tr><td width="20%" valign="top"><label>Info:</label></td><td width="80%"><textarea rows="2" cols="60" name="info">${inMaintenance?inMaintenanceObject.info.encodeAsHTML():''}</textarea></td></tr>
                <g:if test="${inMaintenance}">
                    <tr><td width="20%"><label>Source:</label></td><td width="80%">${inMaintenanceObject.source}</td></tr>
                    <tr><td width="20%"><label>Started:</label></td><td width="80%">${inMaintenanceObject.starting}</td></tr>

                    <g:if test="${inMaintenanceObject?.ending!=new Date(0) && inMaintenanceObject.ending!=null}">
                        <tr><td width="20%"><label>Ending:</label></td><td width="80%">${inMaintenanceObject.ending}</td></tr>
                    </g:if>
                    <g:else>
                        <tr><td width="20%"><label>Ending:</label></td><td width="80%">Until manually canceled.</td></tr>
                    </g:else>
                </g:if>
            </table>
            <input type="hidden" name="model" value="maintenance"/>
        </rui:formRemote>
    </div>
</div>
