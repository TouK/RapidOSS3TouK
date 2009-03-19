package com.ifountain.compass.query;

import org.apache.lucene.queryParser.ParseException;

/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: Mar 19, 2009
 * Time: 11:34:06 AM
 * To change this template use File | Settings | File Templates.
 */
public class ExactQueryParseException extends ParseException{
    public ExactQueryParseException(String fieldName, String fieldValue) {
        super("Invalid exact match query "+fieldValue+" for field "+ fieldName);
    }

}
