

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Create NetcoolHistoricalEvent</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLinkTo(dir:'')}">Home</a></span>
    <span class="menuButton"><g:link class="list" action="list">NetcoolHistoricalEvent List</g:link></span>
</div>
<div class="body">
    <h1>Create NetcoolHistoricalEvent</h1>
    <g:if test="${flash.message}">
        <div class="message">${flash.message}</div>
    </g:if>
    <g:hasErrors bean="${netcoolHistoricalEvent}">
        <div class="errors">
            <g:renderErrors bean="${netcoolHistoricalEvent}" as="list"/>
        </div>
    </g:hasErrors>
    <g:hasErrors bean="${flash.errors}">
       <div class="errors">
            <g:renderErrors bean="${flash.errors}"/>
        </div>
    </g:hasErrors>
    <g:form action="save" method="post" >
        <div class="dialog">
            <table>
                <tbody>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="acknowledged">acknowledged:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:netcoolHistoricalEvent,field:'acknowledged','errors')}">
                            <input type="text" id="acknowledged" name="acknowledged" value="${fieldValue(bean:netcoolHistoricalEvent,field:'acknowledged')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="agent">agent:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:netcoolHistoricalEvent,field:'agent','errors')}">
                            <input type="text" id="agent" name="agent" value="${fieldValue(bean:netcoolHistoricalEvent,field:'agent')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="alertgroup">alertgroup:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:netcoolHistoricalEvent,field:'alertgroup','errors')}">
                            <input type="text" id="alertgroup" name="alertgroup" value="${fieldValue(bean:netcoolHistoricalEvent,field:'alertgroup')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="alertkey">alertkey:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:netcoolHistoricalEvent,field:'alertkey','errors')}">
                            <input type="text" id="alertkey" name="alertkey" value="${fieldValue(bean:netcoolHistoricalEvent,field:'alertkey')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="connectorname">connectorname:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:netcoolHistoricalEvent,field:'connectorname','errors')}">
                            <input type="text" id="connectorname" name="connectorname" value="${fieldValue(bean:netcoolHistoricalEvent,field:'connectorname')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="customer">customer:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:netcoolHistoricalEvent,field:'customer','errors')}">
                            <input type="text" id="customer" name="customer" value="${fieldValue(bean:netcoolHistoricalEvent,field:'customer')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="eventid">eventid:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:netcoolHistoricalEvent,field:'eventid','errors')}">
                            <input type="text" id="eventid" name="eventid" value="${fieldValue(bean:netcoolHistoricalEvent,field:'eventid')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="expiretime">expiretime:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:netcoolHistoricalEvent,field:'expiretime','errors')}">
                            <input type="text" id="expiretime" name="expiretime" value="${fieldValue(bean:netcoolHistoricalEvent,field:'expiretime')}" />
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="firstoccurrence">firstoccurrence:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:netcoolHistoricalEvent,field:'firstoccurrence','errors')}">
                            <input type="text" id="firstoccurrence" name="firstoccurrence" value="${fieldValue(bean:netcoolHistoricalEvent,field:'firstoccurrence')}" />
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="flash">flash:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:netcoolHistoricalEvent,field:'flash','errors')}">
                            <input type="text" id="flash" name="flash" value="${fieldValue(bean:netcoolHistoricalEvent,field:'flash')}" />
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="grade">grade:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:netcoolHistoricalEvent,field:'grade','errors')}">
                            <input type="text" id="grade" name="grade" value="${fieldValue(bean:netcoolHistoricalEvent,field:'grade')}" />
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="identifier">identifier:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:netcoolHistoricalEvent,field:'identifier','errors')}">
                            <input type="text" id="identifier" name="identifier" value="${fieldValue(bean:netcoolHistoricalEvent,field:'identifier')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="internallast">internallast:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:netcoolHistoricalEvent,field:'internallast','errors')}">
                            <input type="text" id="internallast" name="internallast" value="${fieldValue(bean:netcoolHistoricalEvent,field:'internallast')}" />
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="lastoccurrence">lastoccurrence:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:netcoolHistoricalEvent,field:'lastoccurrence','errors')}">
                            <input type="text" id="lastoccurrence" name="lastoccurrence" value="${fieldValue(bean:netcoolHistoricalEvent,field:'lastoccurrence')}" />
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="localnodealias">localnodealias:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:netcoolHistoricalEvent,field:'localnodealias','errors')}">
                            <input type="text" id="localnodealias" name="localnodealias" value="${fieldValue(bean:netcoolHistoricalEvent,field:'localnodealias')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="localpriobj">localpriobj:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:netcoolHistoricalEvent,field:'localpriobj','errors')}">
                            <input type="text" id="localpriobj" name="localpriobj" value="${fieldValue(bean:netcoolHistoricalEvent,field:'localpriobj')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="localrootobj">localrootobj:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:netcoolHistoricalEvent,field:'localrootobj','errors')}">
                            <input type="text" id="localrootobj" name="localrootobj" value="${fieldValue(bean:netcoolHistoricalEvent,field:'localrootobj')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="localsecobj">localsecobj:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:netcoolHistoricalEvent,field:'localsecobj','errors')}">
                            <input type="text" id="localsecobj" name="localsecobj" value="${fieldValue(bean:netcoolHistoricalEvent,field:'localsecobj')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="location">location:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:netcoolHistoricalEvent,field:'location','errors')}">
                            <input type="text" id="location" name="location" value="${fieldValue(bean:netcoolHistoricalEvent,field:'location')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="manager">manager:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:netcoolHistoricalEvent,field:'manager','errors')}">
                            <input type="text" id="manager" name="manager" value="${fieldValue(bean:netcoolHistoricalEvent,field:'manager')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="ncclass">ncclass:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:netcoolHistoricalEvent,field:'ncclass','errors')}">
                            <input type="text" id="ncclass" name="ncclass" value="${fieldValue(bean:netcoolHistoricalEvent,field:'ncclass')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="nctype">nctype:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:netcoolHistoricalEvent,field:'nctype','errors')}">
                            <input type="text" id="nctype" name="nctype" value="${fieldValue(bean:netcoolHistoricalEvent,field:'nctype')}" />
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="ncx733corrnotif">ncx733corrnotif:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:netcoolHistoricalEvent,field:'ncx733corrnotif','errors')}">
                            <input type="text" id="ncx733corrnotif" name="ncx733corrnotif" value="${fieldValue(bean:netcoolHistoricalEvent,field:'ncx733corrnotif')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="ncx733eventtype">ncx733eventtype:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:netcoolHistoricalEvent,field:'ncx733eventtype','errors')}">
                            <input type="text" id="ncx733eventtype" name="ncx733eventtype" value="${fieldValue(bean:netcoolHistoricalEvent,field:'ncx733eventtype')}" />
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="ncx733probablecause">ncx733probablecause:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:netcoolHistoricalEvent,field:'ncx733probablecause','errors')}">
                            <input type="text" id="ncx733probablecause" name="ncx733probablecause" value="${fieldValue(bean:netcoolHistoricalEvent,field:'ncx733probablecause')}" />
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="ncx733specificprob">ncx733specificprob:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:netcoolHistoricalEvent,field:'ncx733specificprob','errors')}">
                            <input type="text" id="ncx733specificprob" name="ncx733specificprob" value="${fieldValue(bean:netcoolHistoricalEvent,field:'ncx733specificprob')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="nmoscausetype">nmoscausetype:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:netcoolHistoricalEvent,field:'nmoscausetype','errors')}">
                            <input type="text" id="nmoscausetype" name="nmoscausetype" value="${fieldValue(bean:netcoolHistoricalEvent,field:'nmoscausetype')}" />
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="nmosobjinst">nmosobjinst:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:netcoolHistoricalEvent,field:'nmosobjinst','errors')}">
                            <input type="text" id="nmosobjinst" name="nmosobjinst" value="${fieldValue(bean:netcoolHistoricalEvent,field:'nmosobjinst')}" />
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="nmosserial">nmosserial:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:netcoolHistoricalEvent,field:'nmosserial','errors')}">
                            <input type="text" id="nmosserial" name="nmosserial" value="${fieldValue(bean:netcoolHistoricalEvent,field:'nmosserial')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="node">node:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:netcoolHistoricalEvent,field:'node','errors')}">
                            <input type="text" id="node" name="node" value="${fieldValue(bean:netcoolHistoricalEvent,field:'node')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="nodealias">nodealias:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:netcoolHistoricalEvent,field:'nodealias','errors')}">
                            <input type="text" id="nodealias" name="nodealias" value="${fieldValue(bean:netcoolHistoricalEvent,field:'nodealias')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="ownergid">ownergid:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:netcoolHistoricalEvent,field:'ownergid','errors')}">
                            <input type="text" id="ownergid" name="ownergid" value="${fieldValue(bean:netcoolHistoricalEvent,field:'ownergid')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="owneruid">owneruid:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:netcoolHistoricalEvent,field:'owneruid','errors')}">
                            <input type="text" id="owneruid" name="owneruid" value="${fieldValue(bean:netcoolHistoricalEvent,field:'owneruid')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="physicalcard">physicalcard:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:netcoolHistoricalEvent,field:'physicalcard','errors')}">
                            <input type="text" id="physicalcard" name="physicalcard" value="${fieldValue(bean:netcoolHistoricalEvent,field:'physicalcard')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="physicalport">physicalport:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:netcoolHistoricalEvent,field:'physicalport','errors')}">
                            <input type="text" id="physicalport" name="physicalport" value="${fieldValue(bean:netcoolHistoricalEvent,field:'physicalport')}" />
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="physicalslot">physicalslot:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:netcoolHistoricalEvent,field:'physicalslot','errors')}">
                            <input type="text" id="physicalslot" name="physicalslot" value="${fieldValue(bean:netcoolHistoricalEvent,field:'physicalslot')}" />
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="poll">poll:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:netcoolHistoricalEvent,field:'poll','errors')}">
                            <input type="text" id="poll" name="poll" value="${fieldValue(bean:netcoolHistoricalEvent,field:'poll')}" />
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="processreq">processreq:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:netcoolHistoricalEvent,field:'processreq','errors')}">
                            <input type="text" id="processreq" name="processreq" value="${fieldValue(bean:netcoolHistoricalEvent,field:'processreq')}" />
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="remotenodealias">remotenodealias:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:netcoolHistoricalEvent,field:'remotenodealias','errors')}">
                            <input type="text" id="remotenodealias" name="remotenodealias" value="${fieldValue(bean:netcoolHistoricalEvent,field:'remotenodealias')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="remotepriobj">remotepriobj:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:netcoolHistoricalEvent,field:'remotepriobj','errors')}">
                            <input type="text" id="remotepriobj" name="remotepriobj" value="${fieldValue(bean:netcoolHistoricalEvent,field:'remotepriobj')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="remoterootobj">remoterootobj:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:netcoolHistoricalEvent,field:'remoterootobj','errors')}">
                            <input type="text" id="remoterootobj" name="remoterootobj" value="${fieldValue(bean:netcoolHistoricalEvent,field:'remoterootobj')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="remotesecobj">remotesecobj:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:netcoolHistoricalEvent,field:'remotesecobj','errors')}">
                            <input type="text" id="remotesecobj" name="remotesecobj" value="${fieldValue(bean:netcoolHistoricalEvent,field:'remotesecobj')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="serial">serial:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:netcoolHistoricalEvent,field:'serial','errors')}">
                            <input type="text" id="serial" name="serial" value="${fieldValue(bean:netcoolHistoricalEvent,field:'serial')}" />
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="servername">servername:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:netcoolHistoricalEvent,field:'servername','errors')}">
                            <input type="text" id="servername" name="servername" value="${fieldValue(bean:netcoolHistoricalEvent,field:'servername')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="serverserial">serverserial:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:netcoolHistoricalEvent,field:'serverserial','errors')}">
                            <input type="text" id="serverserial" name="serverserial" value="${fieldValue(bean:netcoolHistoricalEvent,field:'serverserial')}" />
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="service">service:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:netcoolHistoricalEvent,field:'service','errors')}">
                            <input type="text" id="service" name="service" value="${fieldValue(bean:netcoolHistoricalEvent,field:'service')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="severity">severity:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:netcoolHistoricalEvent,field:'severity','errors')}">
                            <input type="text" id="severity" name="severity" value="${fieldValue(bean:netcoolHistoricalEvent,field:'severity')}" />
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="statechange">statechange:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:netcoolHistoricalEvent,field:'statechange','errors')}">
                            <input type="text" id="statechange" name="statechange" value="${fieldValue(bean:netcoolHistoricalEvent,field:'statechange')}" />
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="summary">summary:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:netcoolHistoricalEvent,field:'summary','errors')}">
                            <input type="text" id="summary" name="summary" value="${fieldValue(bean:netcoolHistoricalEvent,field:'summary')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="suppressescl">suppressescl:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:netcoolHistoricalEvent,field:'suppressescl','errors')}">
                            <input type="text" id="suppressescl" name="suppressescl" value="${fieldValue(bean:netcoolHistoricalEvent,field:'suppressescl')}" />
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="tally">tally:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:netcoolHistoricalEvent,field:'tally','errors')}">
                            <input type="text" id="tally" name="tally" value="${fieldValue(bean:netcoolHistoricalEvent,field:'tally')}" />
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="tasklist">tasklist:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:netcoolHistoricalEvent,field:'tasklist','errors')}">
                            <input type="text" id="tasklist" name="tasklist" value="${fieldValue(bean:netcoolHistoricalEvent,field:'tasklist')}" />
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="url">url:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:netcoolHistoricalEvent,field:'url','errors')}">
                            <input type="text" id="url" name="url" value="${fieldValue(bean:netcoolHistoricalEvent,field:'url')}"/>
                        </td>
                    </tr>
                    
                </tbody>
            </table>
        </div>
        <div class="buttons">
            <span class="button"><input class="save" type="submit" value="Create"/></span>
        </div>
    </g:form>
</div>
</body>
</html>
