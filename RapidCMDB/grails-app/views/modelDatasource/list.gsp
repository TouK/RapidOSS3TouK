

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <meta name="layout" content="main" />
        <title>ModelDatasource List</title>
    </head>
    <body>
        <div class="nav">
            <span class="menuButton"><a class="home" href="${createLinkTo(dir:'')}">Home</a></span>
            <span class="menuButton"><g:link class="create" action="create">New ModelDatasource</g:link></span>
        </div>
        <div class="body">
            <h1>ModelDatasource List</h1>
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <div class="list">
                <table>
                    <thead>
                        <tr>
                        
                   	        <g:sortableColumn property="id" title="Id" />
                        
                   	        <th>Datasource</th>
                   	    
                   	        <g:sortableColumn property="master" title="Master" />
                        
                   	        <th>Model</th>
                   	    
                        </tr>
                    </thead>
                    <tbody>
                    <g:each in="${modelDatasourceList}" status="i" var="modelDatasource">
                        <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                        
                            <td><g:link action="show" id="${modelDatasource.id}">${modelDatasource.id?.encodeAsHTML()}</g:link></td>
                        
                            <td>${modelDatasource.datasource?.encodeAsHTML()}</td>
                        
                            <td>${modelDatasource.master?.encodeAsHTML()}</td>
                        
                            <td>${modelDatasource.model?.encodeAsHTML()}</td>
                        
                        </tr>
                    </g:each>
                    </tbody>
                </table>
            </div>
            <div class="paginateButtons">
                <g:paginate total="${ModelDatasource.count()}" />
            </div>
        </div>
    </body>
</html>
