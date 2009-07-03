package com.ifountain.compass.utils

import com.ifountain.rcmdb.test.util.RapidCmdbTestCase
import com.ifountain.compass.query.FieldQueryParameter
import com.ifountain.compass.converter.CompassStringConverter
import com.ifountain.compass.query.ExactQueryParseException

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Jul 3, 2009
* Time: 12:07:54 PM
* To change this template use File | Settings | File Templates.
*/
class QueryParserUtilsTest extends RapidCmdbTestCase{
    public void testCreateFieldQueryParametersWithEmptyExactQuery()
    {
        def field = "field1";
        def queryText = "${QueryParserUtils.EXACT_QUERY_START}${QueryParserUtils.EXACT_QUERY_END}".toString();
        def nonEscapedTerm = queryText;

        FieldQueryParameter p = QueryParserUtils.createFieldQueryParameters (field, queryText, [nonEscapedTerm]);
        assertEquals (QueryParserUtils.getUntokenizedFieldName(field), p.getField());
        assertEquals (CompassStringConverter.EMPTY_VALUE, p.getQueryText());
    }

    public void testCreateFieldQueryParametersExactQuery()
    {
        def field = "field1";
        def query = "qUeRy"
        def exactQueryText = "${QueryParserUtils.EXACT_QUERY_START}${query}${QueryParserUtils.EXACT_QUERY_END}".toString();
        def nonEscapedTerm = exactQueryText

        FieldQueryParameter p = QueryParserUtils.createFieldQueryParameters (field, exactQueryText, [nonEscapedTerm]);
        assertEquals (QueryParserUtils.getUntokenizedFieldName(field), p.getField());
        assertEquals (query.toLowerCase(), p.getQueryText());
    }

    public void testCreateFieldQueryParametersEmptyStringQuery()
    {
        def field = "field1";
        def queryText = ""
        def nonEscapedTerm = queryText

        FieldQueryParameter p = QueryParserUtils.createFieldQueryParameters (field, queryText, [nonEscapedTerm]);
        assertEquals (QueryParserUtils.getUntokenizedFieldName(field), p.getField());
        assertEquals (CompassStringConverter.EMPTY_VALUE, p.getQueryText());
    }

    public void testCreateFieldQueryParametersQillNotLowerCaseIfQueryIsNotExact()
    {
        def field = "field1";
        def queryText = "qUeRy"
        def nonEscapedTerm = queryText

        FieldQueryParameter p = QueryParserUtils.createFieldQueryParameters (field, queryText, [nonEscapedTerm]);
        assertEquals (field, p.getField());
        assertEquals (queryText, p.getQueryText());
    }

    public void testCreateFieldQueryParametersThrowsExceptionIfItDoesnotStartandEndExactQueryCharacters()
    {
        def field = "field1";
        def queryText = "${QueryParserUtils.EXACT_QUERY_START}query".toString();
        def nonEscapedTerm = queryText

        try{
            QueryParserUtils.createFieldQueryParameters (field, queryText, [nonEscapedTerm]);
            fail("Should throw exception");
        }catch(ExactQueryParseException e)
        {
            assertEquals (new ExactQueryParseException(field, nonEscapedTerm).getMessage(), e.getMessage())
        }

        queryText = "query${QueryParserUtils.EXACT_QUERY_END}".toString();
        nonEscapedTerm = queryText
        try{
            QueryParserUtils.createFieldQueryParameters (field, queryText, [nonEscapedTerm]);
            fail("Should throw exception");
        }catch(ExactQueryParseException e)
        {
            assertEquals (new ExactQueryParseException(field, nonEscapedTerm).getMessage(), e.getMessage())
        }
    }

    public void testCreateFieldQueryParametersEscapeCharactersInExactQuery()
    {
        def field = "field1";
        def queryText = "${QueryParserUtils.EXACT_QUERY_START}query".toString();
        def nonEscapedTerm = "\\"+queryText

        FieldQueryParameter p = QueryParserUtils.createFieldQueryParameters (field, queryText, [nonEscapedTerm]);
        assertEquals (field, p.getField());
        assertEquals (queryText, p.getQueryText());


        queryText = "query${QueryParserUtils.EXACT_QUERY_END}".toString();
        nonEscapedTerm = "query\\${QueryParserUtils.EXACT_QUERY_END}".toString();

        p = QueryParserUtils.createFieldQueryParameters (field, queryText, [nonEscapedTerm]);
        assertEquals (field, p.getField());
        assertEquals (queryText, p.getQueryText());

        queryText = "${QueryParserUtils.EXACT_QUERY_START}query${QueryParserUtils.EXACT_QUERY_END}".toString();
        nonEscapedTerm = "\\${QueryParserUtils.EXACT_QUERY_START}query\\${QueryParserUtils.EXACT_QUERY_END}".toString();

        p = QueryParserUtils.createFieldQueryParameters (field, queryText, [nonEscapedTerm]);
        assertEquals (field, p.getField());
        assertEquals (queryText, p.getQueryText());


        queryText = "${QueryParserUtils.EXACT_QUERY_START}query${QueryParserUtils.EXACT_QUERY_END}".toString();
        nonEscapedTerm = "${QueryParserUtils.EXACT_QUERY_START}query\\\\\\\\${QueryParserUtils.EXACT_QUERY_END}".toString();

        p = QueryParserUtils.createFieldQueryParameters (field, queryText, [nonEscapedTerm]);
        assertEquals (QueryParserUtils.getUntokenizedFieldName(field), p.getField());
        assertEquals ("query", p.getQueryText());
    }
}