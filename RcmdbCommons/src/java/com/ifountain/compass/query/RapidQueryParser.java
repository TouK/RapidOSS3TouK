package com.ifountain.compass.query;

import org.apache.lucene.queryParser.*;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.search.Query;
import org.apache.lucene.index.Term;
import org.compass.core.converter.basic.DateMathParser;
import org.compass.core.mapping.CompassMapping;
import org.compass.core.engine.SearchEngineFactory;

import java.util.TimeZone;
import java.util.Locale;
import java.util.Date;

import com.ifountain.compass.utils.QueryParserUtils;
import com.ifountain.compass.CompassConstants;
import com.ifountain.rcmdb.domain.converter.CompassStringConverter;

/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: Jan 6, 2009
 * Time: 2:38:25 PM
 * To change this template use File | Settings | File Templates.
 */
public class RapidQueryParser extends CompassQueryParser{
    public RapidQueryParser(String f, Analyzer a, CompassMapping mapping, SearchEngineFactory searchEngineFactory, boolean forceAnalyzer) {
        super(f, a, mapping, searchEngineFactory, forceAnalyzer);
    }

    protected Query getFieldQuery(String field, String queryText) throws ParseException {
        FieldQueryParameter param = QueryParserUtils.createFieldQueryParameters(field, queryText);
        return super.getFieldQuery(param.getField(), param.getQueryText());
    }

    protected Query getRangeQuery(String field, String start, String end, boolean inclusive) throws ParseException {
        RangeQueryParameter param = QueryParserUtils.createRangeQueryParameters(field, start, end, inclusive);
        return super.getRangeQuery(param.getField(), param.getStart(), param.getEnd(), param.isInclusive());
    }

}
