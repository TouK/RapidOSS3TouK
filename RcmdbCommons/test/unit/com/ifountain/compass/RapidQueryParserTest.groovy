package com.ifountain.compass

import com.ifountain.rcmdb.test.util.RCMDBTestCase
import org.apache.lucene.queryParser.QueryParser
import org.apache.lucene.analysis.WhitespaceAnalyzer
import org.apache.lucene.search.Query
import org.apache.lucene.analysis.SimpleAnalyzer
import org.apache.commons.lang.StringUtils

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Jan 6, 2009
* Time: 2:51:43 PM
* To change this template use File | Settings | File Templates.
*/
class RapidQueryParserTest extends RCMDBTestCase
{

    public void testIntegerRangeWithCurrentTime() throws Exception {
        String start = RapidQueryParser.CURRENT_TIME_PREFIX+"-1day"
        String end = RapidQueryParser.CURRENT_TIME_PREFIX+"+1day"
        String field = "time"
        QueryParser qp = new RapidQueryParser("field", new SimpleAnalyzer());
        Query q = qp.parse ("${field}:[${start} TO ${end}]");
        String fieldQuery = q.toString(field);
        String fromTime = StringUtils.substringBetween(fieldQuery, "[", "TO").trim();
        String toTime = StringUtils.substringBetween(fieldQuery, "TO", "]").trim();
        def interval = toTime.toLong()- fromTime.toLong();
        def oneDayInMSecs = 1l*24*3600*1000;
        def expectedInterval = 2*oneDayInMSecs;
        
        assertTrue ("${interval} should be greater than or equal ${expectedInterval}", interval >= expectedInterval);

        //Test if range query checks whether it started with currentTime
        String aStringWithSameLengthWithCurrentTime = RapidQueryParser.CURRENT_TIME_PREFIX.replaceAll(".", "c");
        start = aStringWithSameLengthWithCurrentTime+"-1day"
        end = aStringWithSameLengthWithCurrentTime+"+1day"
        q = qp.parse ("${field}:[${start} TO ${end}]");

        fieldQuery = q.toString(field);
        String from = StringUtils.substringBetween(fieldQuery, "[", "TO").trim();
        String to = StringUtils.substringBetween(fieldQuery, "TO", "]").trim();
        assertEquals (start, from)
        assertEquals (end, to)

        //could not be parsed with DateMathParser it returns entered query
        start = "invalidStart"
        end = "invalidEnd"
        q = qp.parse ("${field}:[${start} TO ${end}]");

        fieldQuery = q.toString(field);
        from = StringUtils.substringBetween(fieldQuery, "[", "TO").trim();
        to = StringUtils.substringBetween(fieldQuery, "TO", "]").trim();
        assertEquals (start.toLowerCase(), from)
        assertEquals (end.toLowerCase(), to)
        
    }

     public void testIntegerRangeWithCurrentTimeIsCaseInsensitive() throws Exception {
        def currentTimeString = RapidQueryParser.CURRENT_TIME_PREFIX.toUpperCase();
        currentTimeString = currentTimeString.substring(0,1).toLowerCase()+currentTimeString.substring(1);
        String start = currentTimeString+"-1dAy"
        String end = currentTimeString+"+1dAy"
        String field = "time"
        QueryParser qp = new RapidQueryParser("field", new SimpleAnalyzer());
        Query q = qp.parse ("${field}:[${start} TO ${end}]");
        String fieldQuery = q.toString(field);
        String fromTime = StringUtils.substringBetween(fieldQuery, "[", "TO").trim();
        String toTime = StringUtils.substringBetween(fieldQuery, "TO", "]").trim();
        def interval = toTime.toLong()- fromTime.toLong();
        def oneDayInMSecs = 1l*24*3600*1000;
        def expectedInterval = 2*oneDayInMSecs;

        assertTrue ("${interval} should be greater than or equal ${expectedInterval}", interval >= expectedInterval);
     }

}