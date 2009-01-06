package com.ifountain.compass;

import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.queryParser.CharStream;
import org.apache.lucene.queryParser.QueryParserTokenManager;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.RangeQuery;
import org.compass.core.converter.basic.DateMathParser;

import java.util.TimeZone;
import java.util.Locale;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: Jan 6, 2009
 * Time: 2:38:25 PM
 * To change this template use File | Settings | File Templates.
 */
public class RapidQueryParser extends QueryParser{
    public final static String CURRENT_TIME_PREFIX = "currenttime";
    private final static DateMathParser parser = new DateMathParser(TimeZone.getDefault(), Locale.getDefault());
    public RapidQueryParser(String s, Analyzer analyzer) {
        super(s, analyzer);
    }

    public RapidQueryParser(CharStream charStream) {
        super(charStream);
    }

    public RapidQueryParser(QueryParserTokenManager queryParserTokenManager) {
        super(queryParserTokenManager);
    }

    protected Query getRangeQuery(String field, String start, String end, boolean inclusive) throws ParseException {
        start = getCurrentTime(start);
        end = getCurrentTime(end);
        return super.getRangeQuery(field, start, end, inclusive);
    }

    private String getCurrentTime(String queryString)
    {
        if(queryString.toLowerCase().startsWith(CURRENT_TIME_PREFIX))
        {
            try
            {
                Date startDate = parser.parseMath(queryString.substring(CURRENT_TIME_PREFIX.length()));
                return ""+startDate.getTime();
            }catch(java.text.ParseException e)
            {
            }
        }
        return queryString;
    }
}
