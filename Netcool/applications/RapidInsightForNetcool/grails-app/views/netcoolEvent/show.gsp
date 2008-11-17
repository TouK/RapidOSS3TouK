

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title>Show NetcoolEvent</title>
</head>
<body>
<div class="nav">
    <span class="menuButton"><a class="home" href="${createLinkTo(dir:'')}">Home</a></span>
    <span class="menuButton"><g:link class="list" action="list">NetcoolEvent List</g:link></span>
    <span class="menuButton"><g:link class="create" action="create">New NetcoolEvent</g:link></span>
</div>
<div class="body">
    <h1>Show NetcoolEvent</h1>
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
                    
                    <td valign="top" class="value">${netcoolEvent.id}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">name:</td>
                    
                    <td valign="top" class="value">${netcoolEvent.name}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">acknowledged:</td>
                    
                    <td valign="top" class="value">${netcoolEvent.acknowledged}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">agent:</td>
                    
                    <td valign="top" class="value">${netcoolEvent.agent}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">alertgroup:</td>
                    
                    <td valign="top" class="value">${netcoolEvent.alertgroup}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">alertkey:</td>
                    
                    <td valign="top" class="value">${netcoolEvent.alertkey}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">changedAt:</td>
                    
                    <td valign="top" class="value">${netcoolEvent.changedAt}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">clearedAt:</td>
                    
                    <td valign="top" class="value">${netcoolEvent.clearedAt}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">count:</td>
                    
                    <td valign="top" class="value">${netcoolEvent.count}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">createdAt:</td>
                    
                    <td valign="top" class="value">${netcoolEvent.createdAt}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">customer:</td>
                    
                    <td valign="top" class="value">${netcoolEvent.customer}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">elementName:</td>
                    
                    <td valign="top" class="value">${netcoolEvent.elementName}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">eventid:</td>
                    
                    <td valign="top" class="value">${netcoolEvent.eventid}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">flash:</td>
                    
                    <td valign="top" class="value">${netcoolEvent.flash}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">grade:</td>
                    
                    <td valign="top" class="value">${netcoolEvent.grade}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">identifier:</td>
                    
                    <td valign="top" class="value">${netcoolEvent.identifier}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">internallast:</td>
                    
                    <td valign="top" class="value">${netcoolEvent.internallast}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">lastoccurrence:</td>
                    
                    <td valign="top" class="value">${netcoolEvent.lastoccurrence}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">localnodealias:</td>
                    
                    <td valign="top" class="value">${netcoolEvent.localnodealias}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">localpriobj:</td>
                    
                    <td valign="top" class="value">${netcoolEvent.localpriobj}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">localrootobj:</td>
                    
                    <td valign="top" class="value">${netcoolEvent.localrootobj}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">localsecobj:</td>
                    
                    <td valign="top" class="value">${netcoolEvent.localsecobj}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">location:</td>
                    
                    <td valign="top" class="value">${netcoolEvent.location}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">manager:</td>
                    
                    <td valign="top" class="value">${netcoolEvent.manager}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">ncclass:</td>
                    
                    <td valign="top" class="value">${netcoolEvent.ncclass}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">nctype:</td>
                    
                    <td valign="top" class="value">${netcoolEvent.nctype}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">ncx733corrnotif:</td>
                    
                    <td valign="top" class="value">${netcoolEvent.ncx733corrnotif}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">ncx733eventtype:</td>
                    
                    <td valign="top" class="value">${netcoolEvent.ncx733eventtype}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">ncx733probablecause:</td>
                    
                    <td valign="top" class="value">${netcoolEvent.ncx733probablecause}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">ncx733specificprob:</td>
                    
                    <td valign="top" class="value">${netcoolEvent.ncx733specificprob}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">nmoscausetype:</td>
                    
                    <td valign="top" class="value">${netcoolEvent.nmoscausetype}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">nmosobjinst:</td>
                    
                    <td valign="top" class="value">${netcoolEvent.nmosobjinst}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">nmosserial:</td>
                    
                    <td valign="top" class="value">${netcoolEvent.nmosserial}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">node:</td>
                    
                    <td valign="top" class="value">${netcoolEvent.node}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">nodealias:</td>
                    
                    <td valign="top" class="value">${netcoolEvent.nodealias}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">owner:</td>
                    
                    <td valign="top" class="value">${netcoolEvent.owner}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">ownergid:</td>
                    
                    <td valign="top" class="value">${netcoolEvent.ownergid}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">physicalcard:</td>
                    
                    <td valign="top" class="value">${netcoolEvent.physicalcard}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">physicalport:</td>
                    
                    <td valign="top" class="value">${netcoolEvent.physicalport}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">physicalslot:</td>
                    
                    <td valign="top" class="value">${netcoolEvent.physicalslot}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">poll:</td>
                    
                    <td valign="top" class="value">${netcoolEvent.poll}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">processreq:</td>
                    
                    <td valign="top" class="value">${netcoolEvent.processreq}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">remotenodealias:</td>
                    
                    <td valign="top" class="value">${netcoolEvent.remotenodealias}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">remotepriobj:</td>
                    
                    <td valign="top" class="value">${netcoolEvent.remotepriobj}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">remoterootobj:</td>
                    
                    <td valign="top" class="value">${netcoolEvent.remoterootobj}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">remotesecobj:</td>
                    
                    <td valign="top" class="value">${netcoolEvent.remotesecobj}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">rsDatasource:</td>
                    
                    <td valign="top" class="value">${netcoolEvent.rsDatasource}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">serial:</td>
                    
                    <td valign="top" class="value">${netcoolEvent.serial}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">servername:</td>
                    
                    <td valign="top" class="value">${netcoolEvent.servername}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">serverserial:</td>
                    
                    <td valign="top" class="value">${netcoolEvent.serverserial}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">service:</td>
                    
                    <td valign="top" class="value">${netcoolEvent.service}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">severity:</td>
                    
                    <td valign="top" class="value">${netcoolEvent.severity}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">source:</td>
                    
                    <td valign="top" class="value">${netcoolEvent.source}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">state:</td>
                    
                    <td valign="top" class="value">${netcoolEvent.state}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">summary:</td>
                    
                    <td valign="top" class="value">${netcoolEvent.summary}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">tasklist:</td>
                    
                    <td valign="top" class="value">${netcoolEvent.tasklist}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">url:</td>
                    
                    <td valign="top" class="value">${netcoolEvent.url}</td>
                    
                </tr>
                
                <tr class="prop">
                    <td valign="top" class="name">willExpireAt:</td>
                    
                    <td valign="top" class="value">${netcoolEvent.willExpireAt}</td>
                    
                </tr>
                
            </tbody>
        </table>
    </div>
    <div class="buttons">
        <g:form>
            <input type="hidden" name="id" value="${netcoolEvent?.id}"/>
            <span class="button"><g:actionSubmit class="edit" value="Edit"/></span>
            <span class="button"><g:actionSubmit class="delete" onclick="return confirm('Are you sure?');" value="Delete"/></span>
        </g:form>
    </div>
</div>
</body>
</html>
