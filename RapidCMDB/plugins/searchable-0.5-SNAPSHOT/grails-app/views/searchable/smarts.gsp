<%@ page import="org.springframework.util.ClassUtils" %>
<%@ page import="org.codehaus.groovy.grails.plugins.searchable.SearchableUtils" %>
<%@ page import="org.codehaus.groovy.grails.plugins.searchable.lucene.LuceneUtils" %>
<html>
  <head>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8">
    <title><g:if test="${params.q && params.q?.trim() != ''}">${params.q} - </g:if>Grails Searchable Plugin</title>
    <link rel="stylesheet" href="plugins/searchable-0.5-SNAPSHOT/css/searchable.css">
    <script type="text/javascript">
        var focusQueryInput = function() {
            document.getElementById("q").focus();
        }

        var appendQuery = function(query) {
            document.getElementById("searchableForm").q.value = query;
        }
    </script>
  </head>
  <body onload="focusQueryInput();">
  <div id="header">
    <h1><a href="http://grails.org/Searchable+Plugin" target="_blank">Grails <span>Searchable</span> Plugin</a></h1>
    <g:form url='[controller: "searchable", action: "index"]' id="searchableForm" name="searchableForm" method="get">
        <g:textField name="q" value="${params.q}" size="50"/> <input type="submit" value="Search" />
    </g:form>
    <div style="clear: both; display: none;" class="hint">See <a href="http://lucene.apache.org/java/docs/queryparsersyntax.html">Lucene query syntax</a> for advanced queries</div>
  </div>
  <div id="main">
    <g:set var="haveQuery" value="${params.q?.trim()}" />
    <g:set var="haveResults" value="${searchResult?.results}" />
    <div class="title">
      <span>
        <g:if test="${haveQuery && haveResults}">
          Showing <strong>${searchResult.offset + 1}</strong> - <strong>${searchResult.results.size() + searchResult.offset}</strong> of <strong>${searchResult.total}</strong>
          results for <strong>${params.q}</strong>
        </g:if>
        <g:else>
        &nbsp;
        </g:else>
      </span>
    </div>

    <g:if test="${parseException}">
      <p>Your query - <strong>${params.q}</strong> - is not valid.</p>
      <p>Suggestions:</p>
      <ul>
        <li>Fix the query: see <a href="http://lucene.apache.org/java/docs/queryparsersyntax.html">Lucene query syntax</a> for examples</li>
        <g:if test="${LuceneUtils.queryHasSpecialCharacters(params.q)}">
          <li>Remove special characters like <strong>" - [ ]</strong>, before searching, eg, <em><strong>${LuceneUtils.cleanQuery(params.q)}</strong></em><br />
              <em>Use the Searchable Plugin's <strong>LuceneUtils#cleanQuery</strong> helper method for this: <g:link controller="searchable" action="index" params="[q: LuceneUtils.cleanQuery(params.q)]">Search again with special characters removed</g:link></em>
          </li>
          <li>Escape special characters like <strong>" - [ ]</strong> with <strong>\</strong>, eg, <em><strong>${LuceneUtils.escapeQuery(params.q)}</strong></em><br />
              <em>Use the Searchable Plugin's <strong>LuceneUtils#escapeQuery</strong> helper method for this: <g:link controller="searchable" action="index" params="[q: LuceneUtils.escapeQuery(params.q)]">Search again with special characters escaped</g:link></em><br />
              <em>Or use the Searchable Plugin's <strong>escape</strong> option: <em><g:link controller="searchable" action="index" params="[q: params.q, escape: true]">Search again with the <strong>escape</strong> option enabled</g:link></em>
          </li>
        </g:if>
      </ul>
    </g:if>
    <g:elseif test="${haveQuery && !haveResults}">
      <p>Nothing matched your query - <strong>${params.q}</strong></p>
    </g:elseif>
    <g:elseif test="${haveResults}">
      <div class="results">
        <g:each var="result" in="${searchResult.results}" status="index">
          <div class="result">
            <g:set var="className" value="${ClassUtils.getShortName(result.getClass())}" />
            <g:set var="link" value="${createLink(controller: className[0].toLowerCase() + className[1..-1], action: 'show', id: result.id)}" />
            <div class="name"><a href="${link}">${className} #${result.id}</a></div>
            <g:set var="desc" value="${result.toString()}" />
            <g:if test="${desc.size() > 120}"><g:set var="desc" value="${desc[0..120] + '...'}" /></g:if>
            <div class="desc">${desc.encodeAsHTML()}</div>
            <div class="resultProperties">
                <g:each var="resultProperty" in="${result.metaClass.getProperties()}" status="propIndex">
                    <%
                        def linkAddress = "${params.q} ${resultProperty.name.encodeAsHTML()}:${String.valueOf(result[resultProperty.name]).encodeAsHTML()}".toString();
                        def completeLink = createLink(controller: "searchable", action: 'index', params:[q:linkAddress]);
                    %>
                    <div class="resultProperty">
                        <span class="desc"><a href="${completeLink}" onclick='appendQuery( "${linkAddress}")'>${resultProperty.name}=</a></span>
                        <label>${result[resultProperty.name]}</label>
                        |
                    </div>
                </g:each>
            </div>
            <div class="displayLink">${link}</div>
          </div>
        </g:each>
      </div>

      <div>
        <div class="paging">
          <g:if test="${haveResults}">
              Page:
              <g:set var="totalPages" value="${Math.ceil(searchResult.total / searchResult.max)}" />
              <g:if test="${totalPages == 1}"><span class="currentStep">1</span></g:if>
              <g:else><g:paginate controller="searchable" action="index" params="[q: params.q]" total="${searchResult.total}" prev="&lt; previous" next="next &gt;"/></g:else>
          </g:if>
        </div>
      </div>
    </g:elseif>
  </div>
  </body>
</html>