package com.ifountain.compass;

import org.apache.lucene.queryParser.*;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.search.Query;
import org.compass.core.converter.basic.DateMathParser;
import org.compass.core.mapping.CompassMapping;
import org.compass.core.engine.SearchEngineFactory;

import java.util.TimeZone;
import java.util.Locale;
import java.util.Date;

import com.ifountain.compass.utils.QueryParserUtils;

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

    protected Query getRangeQuery(String field, String start, String end, boolean inclusive) throws ParseException {
        Date now = new Date();
        start = QueryParserUtils.getCurrentTime(start, now);
        end = QueryParserUtils.getCurrentTime(end, now);
        return super.getRangeQuery(field, start, end, inclusive);
    }

}
