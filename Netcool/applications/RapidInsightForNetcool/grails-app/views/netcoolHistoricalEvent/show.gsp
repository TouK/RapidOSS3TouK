

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Show NetcoolHistoricalEvent</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLinkTo(dir:'')}">Home</a></span>
    <span class="menuButton"><g:link class="list" action="list">NetcoolHistoricalEvent List</g:link></span>
    <span class="menuButton"><g:link class="create" action="create">New NetcoolHistoricalEvent</g:link></span>
</div>
<div class="body">
    <h1>Show NetcoolHistoricalEvent</h1>
    <g:if test="${flash.message}">
        <div class="message">${flash.message}</div>
    </g:if>
    <g:hasErrors bean="${flash.errors}">
       <div class="errors">
            <g:renderErrors bean="${flash.errors}"/>
        </div>
    </g:hasErrors>
    <div class="dialog">
        <table>
            <tbody>

                
                <tr class="prop">
                    <td valign="top" class="name">id:</td>
                    
                    <td valign="top" class="value">${netcoolHistoricalEvent.id}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">name:</td>
                    
                    <td valign="top" class="value">${netcoolHistoricalEvent.name}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">acknowledged:</td>
                    
                    <td valign="top" class="value">${netcoolHistoricalEvent.acknowledged}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">agent:</td>
                    
                    <td valign="top" class="value">${netcoolHistoricalEvent.agent}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">alertgroup:</td>
                    
                    <td valign="top" class="value">${netcoolHistoricalEvent.alertgroup}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">alertkey:</td>
                    
                    <td valign="top" class="value">${netcoolHistoricalEvent.alertkey}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">changedAt:</td>
                    
                    <td valign="top" class="value">${netcoolHistoricalEvent.changedAt}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">clearedAt:</td>
                    
                    <td valign="top" class="value">${netcoolHistoricalEvent.clearedAt}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">count:</td>
                    
                    <td valign="top" class="value">${netcoolHistoricalEvent.count}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">createdAt:</td>
                    
                    <td valign="top" class="value">${netcoolHistoricalEvent.createdAt}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">customer:</td>
                    
                    <td valign="top" class="value">${netcoolHistoricalEvent.customer}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">elementName:</td>
                    
                    <td valign="top" class="value">${netcoolHistoricalEvent.elementName}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">eventid:</td>
                    
                    <td valign="top" class="value">${netcoolHistoricalEvent.eventid}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">flash:</td>
                    
                    <td valign="top" class="value">${netcoolHistoricalEvent.flash}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">grade:</td>
                    
                    <td valign="top" class="value">${netcoolHistoricalEvent.grade}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">identifier:</td>
                    
                    <td valign="top" class="value">${netcoolHistoricalEvent.identifier}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">internallast:</td>
                    
                    <td valign="top" class="value">${netcoolHistoricalEvent.internallast}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">lastoccurrence:</td>
                    
                    <td valign="top" class="value">${netcoolHistoricalEvent.lastoccurrence}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">localnodealias:</td>
                    
                    <td valign="top" class="value">${netcoolHistoricalEvent.localnodealias}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">localpriobj:</td>
                    
                    <td valign="top" class="value">${netcoolHistoricalEvent.localpriobj}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">localrootobj:</td>
                    
                    <td valign="top" class="value">${netcoolHistoricalEvent.localrootobj}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">localsecobj:</td>
                    
                    <td valign="top" class="value">${netcoolHistoricalEvent.localsecobj}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">location:</td>
                    
                    <td valign="top" class="value">${netcoolHistoricalEvent.location}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">manager:</td>
                    
                    <td valign="top" class="value">${netcoolHistoricalEvent.manager}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">ncclass:</td>
                    
                    <td valign="top" class="value">${netcoolHistoricalEvent.ncclass}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">nctype:</td>
                    
                    <td valign="top" class="value">${netcoolHistoricalEvent.nctype}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">ncx733corrnotif:</td>
                    
                    <td valign="top" class="value">${netcoolHistoricalEvent.ncx733corrnotif}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">ncx733eventtype:</td>
                    
                    <td valign="top" class="value">${netcoolHistoricalEvent.ncx733eventtype}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">ncx733probablecause:</td>
                    
                    <td valign="top" class="value">${netcoolHistoricalEvent.ncx733probablecause}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">ncx733specificprob:</td>
                    
                    <td valign="top" class="value">${netcoolHistoricalEvent.ncx733specificprob}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">nmoscausetype:</td>
                    
                    <td valign="top" class="value">${netcoolHistoricalEvent.nmoscausetype}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">nmosobjinst:</td>
                    
                    <td valign="top" class="value">${netcoolHistoricalEvent.nmosobjinst}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">nmosserial:</td>
                    
                    <td valign="top" class="value">${netcoolHistoricalEvent.nmosserial}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">node:</td>
                    
                    <td valign="top" class="value">${netcoolHistoricalEvent.node}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">nodealias:</td>
                    
                    <td valign="top" class="value">${netcoolHistoricalEvent.nodealias}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">owner:</td>
                    
                    <td valign="top" class="value">${netcoolHistoricalEvent.owner}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">ownergid:</td>
                    
                    <td valign="top" class="value">${netcoolHistoricalEvent.ownergid}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">physicalcard:</td>
                    
                    <td valign="top" class="value">${netcoolHistoricalEvent.physicalcard}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">physicalport:</td>
                    
                    <td valign="top" class="value">${netcoolHistoricalEvent.physicalport}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">physicalslot:</td>
                    
                    <td valign="top" class="value">${netcoolHistoricalEvent.physicalslot}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">poll:</td>
                    
                    <td valign="top" class="value">${netcoolHistoricalEvent.poll}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">processreq:</td>
                    
                    <td valign="top" class="value">${netcoolHistoricalEvent.processreq}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">remotenodealias:</td>
                    
                    <td valign="top" class="value">${netcoolHistoricalEvent.remotenodealias}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">remotepriobj:</td>
                    
                    <td valign="top" class="value">${netcoolHistoricalEvent.remotepriobj}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">remoterootobj:</td>
                    
                    <td valign="top" class="value">${netcoolHistoricalEvent.remoterootobj}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">remotesecobj:</td>
                    
                    <td valign="top" class="value">${netcoolHistoricalEvent.remotesecobj}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">rsDatasource:</td>
                    
                    <td valign="top" class="value">${netcoolHistoricalEvent.rsDatasource}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">serial:</td>
                    
                    <td valign="top" class="value">${netcoolHistoricalEvent.serial}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">servername:</td>
                    
                    <td valign="top" class="value">${netcoolHistoricalEvent.servername}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">serverserial:</td>
                    
                    <td valign="top" class="value">${netcoolHistoricalEvent.serverserial}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">service:</td>
                    
                    <td valign="top" class="value">${netcoolHistoricalEvent.service}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">severity:</td>
                    
                    <td valign="top" class="value">${netcoolHistoricalEvent.severity}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">source:</td>
                    
                    <td valign="top" class="value">${netcoolHistoricalEvent.source}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">state:</td>
                    
                    <td valign="top" class="value">${netcoolHistoricalEvent.state}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">summary:</td>
                    
                    <td valign="top" class="value">${netcoolHistoricalEvent.summary}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">tasklist:</td>
                    
                    <td valign="top" class="value">${netcoolHistoricalEvent.tasklist}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">url:</td>
                    
                    <td valign="top" class="value">${netcoolHistoricalEvent.url}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">willExpireAt:</td>
                    
                    <td valign="top" class="value">${netcoolHistoricalEvent.willExpireAt}</td>
                    
                </tr>
                
            </tbody>
        </table>
    </div>
    <div class="buttons">
        <g:form>
            <input type="hidden" name="id" value="${netcoolHistoricalEvent?.id}"/>
            <span class="button"><g:actionSubmit class="edit" value="Edit"/></span>
            <span class="button"><g:actionSubmit class="delete" onclick="return confirm('Are you sure?');" value="Delete"/></span>
        </g:form>
    </div>
</div>
</body>
</html>
