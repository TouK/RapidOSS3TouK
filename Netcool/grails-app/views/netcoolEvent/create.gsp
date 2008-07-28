

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Create NetcoolEvent</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLinkTo(dir:'')}">Home</a></span>
    <span class="menuButton"><g:link class="list" action="list">NetcoolEvent List</g:link></span>
</div>
<div class="body">
    <h1>Create NetcoolEvent</h1>
    <g:if test="${flash.message}">
        <div class="message">${flash.message}</div>
    </g:if>
    <g:hasErrors bean="${netcoolEvent}">
        <div class="errors">
            <g:renderErrors bean="${netcoolEvent}" as="list"/>
        </div>
    </g:hasErrors>
    <g:form action="save" method="post" >
        <div class="dialog">
            <table>
                <tbody>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="servername">servername:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:netcoolEvent,field:'servername','errors')}">
                            <input type="text" id="servername" name="servername" value="${fieldValue(bean:netcoolEvent,field:'servername')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="serverserial">serverserial:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:netcoolEvent,field:'serverserial','errors')}">
                            <input type="text" id="serverserial" name="serverserial" value="${fieldValue(bean:netcoolEvent,field:'serverserial')}" />
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="acknowledged">acknowledged:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:netcoolEvent,field:'acknowledged','errors')}">
                            <input type="text" id="acknowledged" name="acknowledged" value="${fieldValue(bean:netcoolEvent,field:'acknowledged')}" />
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="agent">agent:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:netcoolEvent,field:'agent','errors')}">
                            <input type="text" id="agent" name="agent" value="${fieldValue(bean:netcoolEvent,field:'agent')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="alertgroup">alertgroup:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:netcoolEvent,field:'alertgroup','errors')}">
                            <input type="text" id="alertgroup" name="alertgroup" value="${fieldValue(bean:netcoolEvent,field:'alertgroup')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="alertkey">alertkey:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:netcoolEvent,field:'alertkey','errors')}">
                            <input type="text" id="alertkey" name="alertkey" value="${fieldValue(bean:netcoolEvent,field:'alertkey')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="connectorname">connectorname:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:netcoolEvent,field:'connectorname','errors')}">
                            <input type="text" id="connectorname" name="connectorname" value="${fieldValue(bean:netcoolEvent,field:'connectorname')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="customer">customer:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:netcoolEvent,field:'customer','errors')}">
                            <input type="text" id="customer" name="customer" value="${fieldValue(bean:netcoolEvent,field:'customer')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="eventid">eventid:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:netcoolEvent,field:'eventid','errors')}">
                            <input type="text" id="eventid" name="eventid" value="${fieldValue(bean:netcoolEvent,field:'eventid')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="expiretime">expiretime:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:netcoolEvent,field:'expiretime','errors')}">
                            <input type="text" id="expiretime" name="expiretime" value="${fieldValue(bean:netcoolEvent,field:'expiretime')}" />
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="firstoccurrence">firstoccurrence:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:netcoolEvent,field:'firstoccurrence','errors')}">
                            <input type="text" id="firstoccurrence" name="firstoccurrence" value="${fieldValue(bean:netcoolEvent,field:'firstoccurrence')}" />
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="flash">flash:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:netcoolEvent,field:'flash','errors')}">
                            <input type="text" id="flash" name="flash" value="${fieldValue(bean:netcoolEvent,field:'flash')}" />
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="grade">grade:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:netcoolEvent,field:'grade','errors')}">
                            <input type="text" id="grade" name="grade" value="${fieldValue(bean:netcoolEvent,field:'grade')}" />
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="identifier">identifier:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:netcoolEvent,field:'identifier','errors')}">
                            <input type="text" id="identifier" name="identifier" value="${fieldValue(bean:netcoolEvent,field:'identifier')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="internallast">internallast:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:netcoolEvent,field:'internallast','errors')}">
                            <input type="text" id="internallast" name="internallast" value="${fieldValue(bean:netcoolEvent,field:'internallast')}" />
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="lastoccurrence">lastoccurrence:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:netcoolEvent,field:'lastoccurrence','errors')}">
                            <input type="text" id="lastoccurrence" name="lastoccurrence" value="${fieldValue(bean:netcoolEvent,field:'lastoccurrence')}" />
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="localnodealias">localnodealias:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:netcoolEvent,field:'localnodealias','errors')}">
                            <input type="text" id="localnodealias" name="localnodealias" value="${fieldValue(bean:netcoolEvent,field:'localnodealias')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="localpriobj">localpriobj:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:netcoolEvent,field:'localpriobj','errors')}">
                            <input type="text" id="localpriobj" name="localpriobj" value="${fieldValue(bean:netcoolEvent,field:'localpriobj')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="localrootobj">localrootobj:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:netcoolEvent,field:'localrootobj','errors')}">
                            <input type="text" id="localrootobj" name="localrootobj" value="${fieldValue(bean:netcoolEvent,field:'localrootobj')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="localsecobj">localsecobj:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:netcoolEvent,field:'localsecobj','errors')}">
                            <input type="text" id="localsecobj" name="localsecobj" value="${fieldValue(bean:netcoolEvent,field:'localsecobj')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="location">location:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:netcoolEvent,field:'location','errors')}">
                            <input type="text" id="location" name="location" value="${fieldValue(bean:netcoolEvent,field:'location')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="manager">manager:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:netcoolEvent,field:'manager','errors')}">
                            <input type="text" id="manager" name="manager" value="${fieldValue(bean:netcoolEvent,field:'manager')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="ncclass">ncclass:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:netcoolEvent,field:'ncclass','errors')}">
                            <input type="text" id="ncclass" name="ncclass" value="${fieldValue(bean:netcoolEvent,field:'ncclass')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="nctype">nctype:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:netcoolEvent,field:'nctype','errors')}">
                            <input type="text" id="nctype" name="nctype" value="${fieldValue(bean:netcoolEvent,field:'nctype')}" />
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="nmoscausetype">nmoscausetype:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:netcoolEvent,field:'nmoscausetype','errors')}">
                            <input type="text" id="nmoscausetype" name="nmoscausetype" value="${fieldValue(bean:netcoolEvent,field:'nmoscausetype')}" />
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="nmosobjinst">nmosobjinst:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:netcoolEvent,field:'nmosobjinst','errors')}">
                            <input type="text" id="nmosobjinst" name="nmosobjinst" value="${fieldValue(bean:netcoolEvent,field:'nmosobjinst')}" />
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="nmosserial">nmosserial:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:netcoolEvent,field:'nmosserial','errors')}">
                            <input type="text" id="nmosserial" name="nmosserial" value="${fieldValue(bean:netcoolEvent,field:'nmosserial')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="node">node:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:netcoolEvent,field:'node','errors')}">
                            <input type="text" id="node" name="node" value="${fieldValue(bean:netcoolEvent,field:'node')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="nodealias">nodealias:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:netcoolEvent,field:'nodealias','errors')}">
                            <input type="text" id="nodealias" name="nodealias" value="${fieldValue(bean:netcoolEvent,field:'nodealias')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="ownergid">ownergid:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:netcoolEvent,field:'ownergid','errors')}">
                            <input type="text" id="ownergid" name="ownergid" value="${fieldValue(bean:netcoolEvent,field:'ownergid')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="owneruid">owneruid:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:netcoolEvent,field:'owneruid','errors')}">
                            <input type="text" id="owneruid" name="owneruid" value="${fieldValue(bean:netcoolEvent,field:'owneruid')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="physicalcard">physicalcard:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:netcoolEvent,field:'physicalcard','errors')}">
                            <input type="text" id="physicalcard" name="physicalcard" value="${fieldValue(bean:netcoolEvent,field:'physicalcard')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="physicalport">physicalport:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:netcoolEvent,field:'physicalport','errors')}">
                            <input type="text" id="physicalport" name="physicalport" value="${fieldValue(bean:netcoolEvent,field:'physicalport')}" />
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="physicalslot">physicalslot:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:netcoolEvent,field:'physicalslot','errors')}">
                            <input type="text" id="physicalslot" name="physicalslot" value="${fieldValue(bean:netcoolEvent,field:'physicalslot')}" />
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="poll">poll:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:netcoolEvent,field:'poll','errors')}">
                            <input type="text" id="poll" name="poll" value="${fieldValue(bean:netcoolEvent,field:'poll')}" />
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="processreq">processreq:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:netcoolEvent,field:'processreq','errors')}">
                            <input type="text" id="processreq" name="processreq" value="${fieldValue(bean:netcoolEvent,field:'processreq')}" />
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="remotenodealias">remotenodealias:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:netcoolEvent,field:'remotenodealias','errors')}">
                            <input type="text" id="remotenodealias" name="remotenodealias" value="${fieldValue(bean:netcoolEvent,field:'remotenodealias')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="remotepriobj">remotepriobj:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:netcoolEvent,field:'remotepriobj','errors')}">
                            <input type="text" id="remotepriobj" name="remotepriobj" value="${fieldValue(bean:netcoolEvent,field:'remotepriobj')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="remoterootobj">remoterootobj:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:netcoolEvent,field:'remoterootobj','errors')}">
                            <input type="text" id="remoterootobj" name="remoterootobj" value="${fieldValue(bean:netcoolEvent,field:'remoterootobj')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="remotesecobj">remotesecobj:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:netcoolEvent,field:'remotesecobj','errors')}">
                            <input type="text" id="remotesecobj" name="remotesecobj" value="${fieldValue(bean:netcoolEvent,field:'remotesecobj')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="serial">serial:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:netcoolEvent,field:'serial','errors')}">
                            <input type="text" id="serial" name="serial" value="${fieldValue(bean:netcoolEvent,field:'serial')}" />
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="service">service:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:netcoolEvent,field:'service','errors')}">
                            <input type="text" id="service" name="service" value="${fieldValue(bean:netcoolEvent,field:'service')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="severity">severity:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:netcoolEvent,field:'severity','errors')}">
                            <input type="text" id="severity" name="severity" value="${fieldValue(bean:netcoolEvent,field:'severity')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="statechange">statechange:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:netcoolEvent,field:'statechange','errors')}">
                            <input type="text" id="statechange" name="statechange" value="${fieldValue(bean:netcoolEvent,field:'statechange')}" />
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="summary">summary:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:netcoolEvent,field:'summary','errors')}">
                            <input type="text" id="summary" name="summary" value="${fieldValue(bean:netcoolEvent,field:'summary')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="suppressescl">suppressescl:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:netcoolEvent,field:'suppressescl','errors')}">
                            <input type="text" id="suppressescl" name="suppressescl" value="${fieldValue(bean:netcoolEvent,field:'suppressescl')}" />
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="tally">tally:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:netcoolEvent,field:'tally','errors')}">
                            <input type="text" id="tally" name="tally" value="${fieldValue(bean:netcoolEvent,field:'tally')}" />
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="tasklist">tasklist:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:netcoolEvent,field:'tasklist','errors')}">
                            <input type="text" id="tasklist" name="tasklist" value="${fieldValue(bean:netcoolEvent,field:'tasklist')}" />
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="url">url:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:netcoolEvent,field:'url','errors')}">
                            <input type="text" id="url" name="url" value="${fieldValue(bean:netcoolEvent,field:'url')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="x733corrnotif">x733corrnotif:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:netcoolEvent,field:'x733corrnotif','errors')}">
                            <input type="text" id="x733corrnotif" name="x733corrnotif" value="${fieldValue(bean:netcoolEvent,field:'x733corrnotif')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="x733eventtype">x733eventtype:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:netcoolEvent,field:'x733eventtype','errors')}">
                            <input type="text" id="x733eventtype" name="x733eventtype" value="${fieldValue(bean:netcoolEvent,field:'x733eventtype')}" />
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="x733probablecause">x733probablecause:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:netcoolEvent,field:'x733probablecause','errors')}">
                            <input type="text" id="x733probablecause" name="x733probablecause" value="${fieldValue(bean:netcoolEvent,field:'x733probablecause')}" />
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="x733specificprob">x733specificprob:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:netcoolEvent,field:'x733specificprob','errors')}">
                            <input type="text" id="x733specificprob" name="x733specificprob" value="${fieldValue(bean:netcoolEvent,field:'x733specificprob')}"/>
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
