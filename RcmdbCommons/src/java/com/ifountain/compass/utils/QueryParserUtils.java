package com.ifountain.compass.utils;

import org.compass.core.converter.basic.DateMathParser;

import java.util.Date;
import java.util.TimeZone;
import java.util.Locale;

import com.ifountain.compass.converter.CompassStringConverter;
import com.ifountain.compass.query.RangeQueryParameter;
import com.ifountain.compass.query.FieldQueryParameter;
import com.ifountain.compass.CompassConstants;

/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: Jan 6, 2009
 * Time: 5:57:06 PM
 * To change this template use File | Settings | File Templates.
 */
public class QueryParserUtils {
    public static final String EMPTY_STRING_FOR_RANGE_QUERY = "\"\"";
    public static final String EMPTY_STRING_FOR_FIELD_QUERY = "";
    public final static String CURRENT_TIME_PREFIX = "currenttime";
    public static String getCurrentTime(String queryString, Date now)
    {
        if(queryString.toLowerCase().startsWith(CURRENT_TIME_PREFIX))
        {
            DateMathParser parser = new DateMathParser(TimeZone.getDefault(), Locale.getDefault());
            parser.setNow(now);
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

    public static RangeQueryParameter createRangeQueryParameters(String field, String start, String end, boolean inclusive)
    {
        if(start.equals(QueryParserUtils.EMPTY_STRING_FOR_RANGE_QUERY) || end.equals(QueryParserUtils.EMPTY_STRING_FOR_RANGE_QUERY))
        {
            start = QueryParserUtils.replaceEmptyStringQuery(start, QueryParserUtils.EMPTY_STRING_FOR_RANGE_QUERY);
            end = QueryParserUtils.replaceEmptyStringQuery(end, QueryParserUtils.EMPTY_STRING_FOR_RANGE_QUERY);
            field = CompassConstants.UN_TOKENIZED_FIELD_PREFIX+field;
        }
        else
        {
            Date now = new Date();
            start = QueryParserUtils.getCurrentTime(start, now);
            end = QueryParserUtils.getCurrentTime(end, now);
        }
        RangeQueryParameter qparam = new RangeQueryParameter(field, start, end, inclusive);
        return qparam;
    }

    public static FieldQueryParameter createFieldQueryParameters(String field, String queryText)
    {
        if(QueryParserUtils.EMPTY_STRING_FOR_FIELD_QUERY.equals(queryText))
        {
            queryText = QueryParserUtils.replaceEmptyStringQuery(queryText, QueryParserUtils.EMPTY_STRING_FOR_FIELD_QUERY);
            field = CompassConstants.UN_TOKENIZED_FIELD_PREFIX+field;
        }
        FieldQueryParameter qparam = new FieldQueryParameter(field, queryText);
        return qparam;
    }
    public static String replaceEmptyStringQuery(String queryString, String emptyString)
    {
        if(emptyString.equals(queryString))
        {
            queryString = CompassStringConverter.EMPTY_VALUE;
        }
        return queryString;
    }
}
