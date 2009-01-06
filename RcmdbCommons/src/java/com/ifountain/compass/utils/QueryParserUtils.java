package com.ifountain.compass.utils;

import org.compass.core.converter.basic.DateMathParser;

import java.util.Date;
import java.util.TimeZone;
import java.util.Locale;

/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: Jan 6, 2009
 * Time: 5:57:06 PM
 * To change this template use File | Settings | File Templates.
 */
public class QueryParserUtils {
    private final static DateMathParser parser = new DateMathParser(TimeZone.getDefault(), Locale.getDefault());
    public final static String CURRENT_TIME_PREFIX = "currenttime";
    public static String getCurrentTime(String queryString)
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
