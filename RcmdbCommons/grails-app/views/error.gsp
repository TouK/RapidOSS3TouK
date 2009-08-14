<%@ page import="groovy.xml.MarkupBuilder; com.ifountain.rcmdb.mobile.MobileUtils" %>
<g:if test="${params.format == 'xml'}">
    <% response.contentType = "text/xml" %>
    <%
        StringWriter sw = new StringWriter();
        MarkupBuilder builder = new MarkupBuilder(sw)
        builder.Errors(){
            builder.Error(error:exception.message)
        }
        println sw.toString()
    %>
</g:if>
<g:elseif test="${MobileUtils.isMobile(request)}">
    <% response.contentType = "text/xml" %>
    <div>
        <strong>Message:</strong> ${exception.message} <br/>
        <strong>Caused by:</strong> ${exception.cause.message} <br/>
        <strong>Class:</strong> ${exception.className} <br/>
        <strong>At Line:</strong> [${exception.lineNumber}] <br/>
        <strong>Code Snippet:</strong><br/>
        <div class="snippet">
            <g:each var="cs" in="${exception.codeSnippet}">
                ${cs?.encodeAsHTML()}<br/>
            </g:each>
        </div>
    </div>
</g:elseif>
<g:else>
    <% response.contentType = "text/html" %>
    <html>
    <head>
        <title>Grails Runtime Exception</title>
        <style type="text/css">
        .message {
        border: 1px solid black;
        padding: 5px;
        background-color:#E9E9E9;
        }
        .stack {
        border: 1px solid black;
        padding: 5px;
        overflow:auto;
        height: 300px;
        }
        .snippet {
        padding: 5px;
        background-color:white;
        border:1px solid black;
        margin:3px;
        font-family:courier;
        }
        </style>
    </head>

    <body>
    <h1>Grails Runtime Exception</h1>
    <h2>Error Details</h2>
    <div class="message">
        <strong>Message:</strong> ${exception.message?.encodeAsHTML()} <br/>
        <strong>Caused by:</strong> ${exception.cause?.message?.encodeAsHTML()} <br/>
        <strong>Class:</strong> ${exception.className} <br/>
        <strong>At Line:</strong> [${exception.lineNumber}] <br/>
        <strong>Code Snippet:</strong><br/>
        <div class="snippet">
            <g:each var="cs" in="${exception.codeSnippet}">
                ${cs?.encodeAsHTML()}<br/>
            </g:each>
        </div>
    </div>
    <h2>Stack Trace</h2>
    <div class="stack">
        <pre><g:each in="${exception.stackTraceLines}">${it.encodeAsHTML()}<br/></g:each></pre>
    </div>
    </body>
    </html>
</g:else>

