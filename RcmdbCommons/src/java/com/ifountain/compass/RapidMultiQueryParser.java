package com.ifountain.compass;

import org.apache.lucene.queryParser.CompassMultiFieldQueryParser;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.search.Query;
import org.compass.core.mapping.CompassMapping;
import org.compass.core.engine.SearchEngineFactory;
import com.ifountain.compass.utils.QueryParserUtils;

import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: Jan 6, 2009
 * Time: 5:51:52 PM
 * To change this template use File | Settings | File Templates.
 */
public class RapidMultiQueryParser extends CompassMultiFieldQueryParser
{
    public RapidMultiQueryParser(String[] fields, Analyzer analyzer, CompassMapping mapping, SearchEngineFactory searchEngineFactory, boolean forceAnalyzer) {
        super(fields, analyzer, mapping, searchEngineFactory, forceAnalyzer);
    }

    protected Query getRangeQuery(String field, String start, String end, boolean inclusive) throws ParseException {
        Date now = new Date();
        start = QueryParserUtils.getCurrentTime(start, now);
        end = QueryParserUtils.getCurrentTime(end, now);
        return super.getRangeQuery(field, start, end, inclusive);
    }
    
}
