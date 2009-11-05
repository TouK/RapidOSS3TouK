package com.ifountain.compass.utils;

import org.compass.core.converter.basic.DateMathParser;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.index.Term;

import java.util.Date;
import java.util.TimeZone;
import java.util.Locale;
import java.util.List;

import com.ifountain.compass.converter.CompassStringConverter;
import com.ifountain.compass.query.RangeQueryParameter;
import com.ifountain.compass.query.FieldQueryParameter;
import com.ifountain.compass.query.ExactQueryParseException;
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
    public static final String EXACT_QUERY_START = "(";
    public static final String QUOTE = "\"";
    public static final String EXACT_QUERY_END = ")";
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

    public static String toExactQuery(String query)
    {
        return   QUOTE+EXACT_QUERY_START+escapeQuery(query)+EXACT_QUERY_END+QUOTE;
    }

    public static String escapeQuery(String s)
    {
        return org.apache.lucene.queryParser.QueryParser.escape(s);
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

    private static boolean endsWithExactQuery(String nonEscapedQueryTerm, String escapedQueryTerm)
    {
        if(!nonEscapedQueryTerm.endsWith(EXACT_QUERY_END))
        {
            return false;
        }
        else
        {
            int numberOfEscapeChars = 0;
            for(int i=nonEscapedQueryTerm.length()-2; i>=0; i--)
            {
                if(nonEscapedQueryTerm.charAt(i) == '\\')
                {
                    numberOfEscapeChars++;       
                }
                else
                {
                    break;
                }
            }
            return numberOfEscapeChars%2==0;
        }
    }

    public static String trimExactQuerySymbols(String queryText)
    {
        return queryText.substring(1, queryText.length()-1);        
    }

    public static String getUntokenizedFieldName(String field)
    {
        return CompassConstants.UN_TOKENIZED_FIELD_PREFIX+field;
    }

    public static Query getAliasQuery(String alias, String queryText)
    {
        BooleanQuery query = new BooleanQuery();
        query.add(new TermQuery(new Term(alias, queryText)), BooleanClause.Occur.SHOULD);
        return query;
    }

    public static FieldQueryParameter createFieldQueryParameters(String field, String queryText, List nonEscapedTerms) throws ParseException {
        if(field != null)
        {
            String lastNonEscapedTerm = (String)nonEscapedTerms.get(nonEscapedTerms.size()-1);
            if(lastNonEscapedTerm.startsWith(EXACT_QUERY_START) && endsWithExactQuery(lastNonEscapedTerm, queryText))
            {
                queryText = trimExactQuerySymbols(queryText);
                field = getUntokenizedFieldName(field);
                if(EMPTY_STRING_FOR_FIELD_QUERY.equals(queryText))
                {
                    queryText = QueryParserUtils.replaceEmptyStringQuery(queryText, QueryParserUtils.EMPTY_STRING_FOR_FIELD_QUERY);
                }
            }
            else if(lastNonEscapedTerm.startsWith(EXACT_QUERY_START) || endsWithExactQuery(lastNonEscapedTerm, queryText))
            {
                throw new ExactQueryParseException(field, lastNonEscapedTerm);
            }
            else if(QueryParserUtils.EMPTY_STRING_FOR_FIELD_QUERY.equals(queryText))
            {
                queryText = QueryParserUtils.replaceEmptyStringQuery(queryText, QueryParserUtils.EMPTY_STRING_FOR_FIELD_QUERY);
                field = getUntokenizedFieldName(field);
            }
        }
        return new FieldQueryParameter(field, queryText);
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
