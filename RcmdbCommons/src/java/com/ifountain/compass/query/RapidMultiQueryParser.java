package com.ifountain.compass.query;

import org.apache.lucene.queryParser.CompassMultiFieldQueryParser;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.index.Term;
import org.compass.core.mapping.CompassMapping;
import org.compass.core.engine.SearchEngineFactory;
import com.ifountain.compass.utils.QueryParserUtils;
import com.ifountain.compass.CompassConstants;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: Jan 6, 2009
 * Time: 5:51:52 PM
 * To change this template use File | Settings | File Templates.
 */
public class RapidMultiQueryParser extends CompassMultiFieldQueryParser
{
    List nonEscapedTerms = new ArrayList();
    String untokenizedAliasFieldName;
    public RapidMultiQueryParser(String[] fields, Analyzer analyzer, CompassMapping mapping, SearchEngineFactory searchEngineFactory, boolean forceAnalyzer) {
        super(fields, analyzer, mapping, searchEngineFactory, forceAnalyzer);
        untokenizedAliasFieldName = CompassConstants.UN_TOKENIZED_FIELD_PREFIX+searchEngineFactory.getAliasProperty();
    }

    protected String discardEscapeChar(String s) throws ParseException {
        nonEscapedTerms.add(s);
        return super.discardEscapeChar(s);    //To change body of overridden methods use File | Settings | File Templates.
    }

    protected Query getFieldQuery(String field, String queryText, int slop) throws ParseException {
        FieldQueryParameter param = QueryParserUtils.createFieldQueryParameters(field, queryText, nonEscapedTerms);
        if(untokenizedAliasFieldName.equals(param.getField()))
        {
            return QueryParserUtils.getAliasQuery(searchEngineFactory.getAliasProperty(), QueryParserUtils.trimExactQuerySymbols(queryText));
        }
        return super.getFieldQuery(param.getField(), param.getQueryText(), slop);    //To change body of overridden methods use File | Settings | File Templates.
    }

    protected Query getFieldQuery(String field, String queryText) throws ParseException {
        FieldQueryParameter param = QueryParserUtils.createFieldQueryParameters(field, queryText, nonEscapedTerms);
        if(untokenizedAliasFieldName.equals(param.getField()))
        {
            return QueryParserUtils.getAliasQuery(searchEngineFactory.getAliasProperty(), QueryParserUtils.trimExactQuerySymbols(queryText));
        }
        return super.getFieldQuery(param.getField(), param.getQueryText());    //To change body of overridden methods use File | Settings | File Templates.
    }

    protected Query getRangeQuery(String field, String start, String end, boolean inclusive) throws ParseException {
        field = CompassConstants.UN_TOKENIZED_FIELD_PREFIX+field;
        RangeQueryParameter param = QueryParserUtils.createRangeQueryParameters(field, start, end, inclusive);
        return super.getRangeQuery(param.getField(), param.getStart(), param.getEnd(), param.isInclusive());
    }
    
}
