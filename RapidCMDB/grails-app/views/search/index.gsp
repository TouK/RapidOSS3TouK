<%@ page import="org.springframework.util.ClassUtils" %>
<%@ page import="org.codehaus.groovy.grails.plugins.searchable.SearchableUtils" %>
<%@ page import="org.codehaus.groovy.grails.plugins.searchable.lucene.LuceneUtils" %>
<html>
  <head>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8">
    <title><g:if test="${params.q && params.q?.trim() != ''}">${params.q} - </g:if>RapidBrowser</title>
    <link rel="stylesheet" href="/RapidCMDB/css/search/smarts.css">
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
    <table border="0" cellpadding="0" cellspacing="5" width="100%">
        <tr>
            <td style="white-space:nowrap">
                <h1>Rapid <span>Browser</span></h1>
            </td>
            <td width="100%">
                <g:form url='[controller: "search", action: "index"]' id="searchableForm" name="searchableForm" method="get" style="width:100%">
                    <table width="100%">
                        <tbody>
                            <tr>
                                <td width="100%">
                                    <g:textField name="q" value="${params.q}" size="50" style="width:100%"/>
                                </td>
                                <td>
                                    <input type="submit" name="submitBtn" value="Search" />
                                </td>
                                <td>
                                    <input type="submit" name="submitBtn" value="Save" />
                                </td>
                            </tr>
                        </tbody>
                    </table>
                </g:form>
            </td>
        </tr>
        <tr>
            <td></td>
            <td>
                <g:form url='[controller: "search", action: "index"]' id="deleteQueryForm" name="deleteQueryForm" method="get">
                    <g:select name="queryId" onchange="this.form.submit();" value="${params.queryId}" from="${savedQueries}" optionValue="query" optionKey="id" noSelection="['':' - Select Query - ']" style="width:300px;"/>
                    <input type="submit"  name="submitBtn" value="Delete Saved Query" />
                </g:form>
            </td>
        </tr>
        <tr>
            <td></td>
            <td>
                <div style="clear: both; display: none;" class="hint">See <a href="http://lucene.apache.org/java/docs/queryparsersyntax.html">Lucene query syntax</a> for advanced queries</div>
            </td>
        </tr>
    </table>
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
              <em>Use the Searchable Plugin's <strong>LuceneUtils#cleanQuery</strong> helper method for this: <g:link controller="search" action="index" params="[q: LuceneUtils.cleanQuery(params.q)]">Search again with special characters removed</g:link></em>
          </li>
          <li>Escape special characters like <strong>" - [ ]</strong> with <strong>\</strong>, eg, <em><strong>${LuceneUtils.escapeQuery(params.q)}</strong></em><br />
              <em>Use the Searchable Plugin's <strong>LuceneUtils#escapeQuery</strong> helper method for this: <g:link controller="search" action="index" params="[q: LuceneUtils.escapeQuery(params.q)]">Search again with special characters escaped</g:link></em><br />
              <em>Or use the Searchable Plugin's <strong>escape</strong> option: <em><g:link controller="search" action="index" params="[q: params.q, escape: true]">Search again with the <strong>escape</strong> option enabled</g:link></em>
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
            <div class="name"><a href="${link}">${search.SearchController.getLinkProperty(result).encodeAsHTML()}</a></div>
            <div class="resultProperties">
                <g:each var="resultProperty" in="${search.SearchController.getPropertyConfiguration(result.class.name)}" status="propIndex">
                    <%
                        def linkAddress = "${params.q} ${resultProperty.encodeAsHTML()}:${"\""+String.valueOf(result[resultProperty]).encodeAsHTML()+"\""}".toString();
                        def completeLink = createLink(controller: "search", action: 'index', params:[q:linkAddress]);
                    %>
                    <div class="resultProperty">
                        <span class="desc">${resultProperty}=</span>
                        <label><a href="${completeLink}" onclick='appendQuery( "${linkAddress}")'>${result[resultProperty]}</a></label>
                        |
                    </div>
                </g:each>
            </div>
          </div>
        </g:each>
      </div>

      <div>
        <div class="paging">
          <g:if test="${haveResults}">
              Page:
              <g:set var="totalPages" value="${Math.ceil(searchResult.total / searchResult.max)}" />
              <g:if test="${totalPages == 1}"><span class="currentStep">1</span></g:if>
              <g:else><g:paginate controller="search" action="index" params="[q: params.q]" total="${searchResult.total}" prev="&lt; previous" next="next &gt;"/></g:else>
          </g:if>
        </div>
      </div>
    </g:elseif>
  </div>
  </body>
</html>