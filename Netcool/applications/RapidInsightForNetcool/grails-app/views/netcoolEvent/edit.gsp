

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Edit NetcoolEvent</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLinkTo(dir:'')}">Home</a></span>
    <span class="menuButton"><g:link class="list" action="list">NetcoolEvent List</g:link></span>
    <span class="menuButton"><g:link class="create" action="create">New NetcoolEvent</g:link></span>
</div>
<div class="body">
    <h1>Edit NetcoolEvent</h1>
    <g:if test="${flash.message}">
        <div class="message">${flash.message}</div>
    </g:if>
    <g:hasErrors bean="${netcoolEvent}">
        <div class="errors">
            <g:renderErrors bean="${netcoolEvent}" as="list"/>
        </div>
    </g:hasErrors>
    <g:hasErrors bean="${flash.errors}">
       <div class="errors">
            <g:renderErrors bean="${flash.errors}"/>
        </div>
    </g:hasErrors>
    <g:form method="post" >
        <input type="hidden" name="id" value="${netcoolEvent?.id}"/>
        <div class="dialog">
            <table>
                <tbody>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="name">name:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:netcoolEvent,field:'name','errors')}">
                            <input type="text" id="name" name="name" value="${fieldValue(bean:netcoolEvent,field:'name')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="acknowledged">acknowledged:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:netcoolEvent,field:'acknowledged','errors')}">
                            <g:checkBox name="acknowledged" value="${netcoolEvent?.acknowledged}" ></g:checkBox>
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
                            <label for="changedAt">changedAt:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:netcoolEvent,field:'changedAt','errors')}">
                            <input type="text" id="changedAt" name="changedAt" value="${fieldValue(bean:netcoolEvent,field:'changedAt')}" />
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="clearedAt">clearedAt:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:netcoolEvent,field:'clearedAt','errors')}">
                            <input type="text" id="clearedAt" name="clearedAt" value="${fieldValue(bean:netcoolEvent,field:'clearedAt')}" />
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="count">count:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:netcoolEvent,field:'count','errors')}">
                            <input type="text" id="count" name="count" value="${fieldValue(bean:netcoolEvent,field:'count')}" />
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="createdAt">createdAt:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:netcoolEvent,field:'createdAt','errors')}">
                            <input type="text" id="createdAt" name="createdAt" value="${fieldValue(bean:netcoolEvent,field:'createdAt')}" />
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
                            <label for="elementName">elementName:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:netcoolEvent,field:'elementName','errors')}">
                            <input type="text" id="elementName" name="elementName" value="${fieldValue(bean:netcoolEvent,field:'elementName')}"/>
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
                            <label for="ncx733corrnotif">ncx733corrnotif:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:netcoolEvent,field:'ncx733corrnotif','errors')}">
                            <input type="text" id="ncx733corrnotif" name="ncx733corrnotif" value="${fieldValue(bean:netcoolEvent,field:'ncx733corrnotif')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="ncx733eventtype">ncx733eventtype:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:netcoolEvent,field:'ncx733eventtype','errors')}">
                            <input type="text" id="ncx733eventtype" name="ncx733eventtype" value="${fieldValue(bean:netcoolEvent,field:'ncx733eventtype')}" />
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="ncx733probablecause">ncx733probablecause:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:netcoolEvent,field:'ncx733probablecause','errors')}">
                            <input type="text" id="ncx733probablecause" name="ncx733probablecause" value="${fieldValue(bean:netcoolEvent,field:'ncx733probablecause')}" />
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="ncx733specificprob">ncx733specificprob:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:netcoolEvent,field:'ncx733specificprob','errors')}">
                            <input type="text" id="ncx733specificprob" name="ncx733specificprob" value="${fieldValue(bean:netcoolEvent,field:'ncx733specificprob')}"/>
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
                            <label for="owner">owner:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:netcoolEvent,field:'owner','errors')}">
                            <input type="text" id="owner" name="owner" value="${fieldValue(bean:netcoolEvent,field:'owner')}"/>
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
                            <label for="rsDatasource">rsDatasource:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:netcoolEvent,field:'rsDatasource','errors')}">
                            <input type="text" id="rsDatasource" name="rsDatasource" value="${fieldValue(bean:netcoolEvent,field:'rsDatasource')}"/>
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
                            <input type="text" id="severity" name="severity" value="${fieldValue(bean:netcoolEvent,field:'severity')}" />
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="source">source:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:netcoolEvent,field:'source','errors')}">
                            <input type="text" id="source" name="source" value="${fieldValue(bean:netcoolEvent,field:'source')}"/>
                        </td>
                    </tr>
                    
                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="state">state:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:netcoolEvent,field:'state','errors')}">
                            <input type="text" id="state" name="state" value="${fieldValue(bean:netcoolEvent,field:'state')}" />
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
                            <label for="willExpireAt">willExpireAt:</label>
                        </td>
                        <td valign="top" class="value ${hasErrors(bean:netcoolEvent,field:'willExpireAt','errors')}">
                            <input type="text" id="willExpireAt" name="willExpireAt" value="${fieldValue(bean:netcoolEvent,field:'willExpireAt')}" />
                        </td>
                    </tr>
                    
                </tbody>
            </table>
        </div>
        <div class="buttons">
            <span class="button"><g:actionSubmit class="save" value="Update"/></span>
            <span class="button"><g:actionSubmit class="delete" onclick="return confirm('Are you sure?');" value="Delete"/></span>
        </div>
    </g:form>
</div>
</body>
</html>
