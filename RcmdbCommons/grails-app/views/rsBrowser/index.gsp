<%--
  Created by IntelliJ IDEA.
  User: Sezgin Kucukkaraaslan
  Date: Jan 5, 2009
  Time: 10:40:41 AM
--%>


<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <meta name="layout" content="main" />
        <title>Rapid Browser</title>
    </head>
    <body>
        <div class="nav">
        </div>
        <div class="body">
            <h1>LdapConnection List</h1>
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <g:hasErrors bean="${flash.errors}">
                <div class="errors">
                    <g:renderErrors bean="${flash.errors}"/>
                </div>
            </g:hasErrors>
            <div class="list">
                <table>
                    <thead>
                        <tr>
                   	        <g:sortableColumn property="className" title="Class Name" />
                        </tr>
                    </thead>
                    <tbody>
                    <g:each in="${domainClassList}" status="i" var="domainClass">
                        <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                            <td><g:link action="${domainClass.logicalPropertyName}">${domainClass.fullName.encodeAsHTML()}</g:link></td>
                        </tr>
                    </g:each>
                    </tbody>
                </table>
            </div>
        </div>
    </body>
</html>
