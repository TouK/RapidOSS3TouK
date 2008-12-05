/* 
* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be
* noted in a separate copyright notice. All rights reserved.
* This file is part of RapidCMDB.
*
* RapidCMDB is free software; you can redistribute it and/or modify
* it under the terms version 2 of the GNU General Public License as
* published by the Free Software Foundation. This program is distributed
* in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
* even the implied warranty of MERCHANTABILITY or FITNESS FOR A
* PARTICULAR PURPOSE. See the GNU General Public License for more
* details.
*
* You should have received a copy of the GNU General Public License
* along with this program; if not, write to the Free Software
* Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
* USA.
*/
package com.ifountain.compass

import com.ifountain.rcmdb.test.util.RapidCmdbTestCase
import org.apache.lucene.queryParser.QueryParser
import com.ifountain.compass.analyzer.WhiteSpaceLowerCaseAnalyzer
import org.apache.lucene.search.Query
import org.apache.lucene.search.BooleanQuery
import org.apache.lucene.search.BooleanClause
import org.apache.lucene.search.TermQuery
import org.apache.lucene.index.Term
import org.apache.lucene.search.WildcardQuery
import org.apache.lucene.search.PrefixQuery
import org.apache.lucene.search.RangeQuery
import org.apache.lucene.search.ConstantScoreRangeQuery
import org.apache.lucene.search.FuzzyQuery
import org.apache.lucene.search.PhraseQuery

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Aug 5, 2008
 * Time: 2:19:46 PM
 * To change this template use File | Settings | File Templates.
 */
class QueryConverterTest extends RapidCmdbTestCase{

//    * MultiTermQuery
//    * PhraseQuery
//    * MultiPhraseQuery
//    * RangeQuery
//    * SpanQuery
    public void testConvertQueryWithBooleanQuery()
    {
        String queryString = "this term1:term1value term2:term2value a query";
        QueryParser parser = new QueryParser("field", new WhiteSpaceLowerCaseAnalyzer());
        BooleanQuery q = parser.parse(queryString);
        def originalClauses = q.getClauses();

        BooleanQuery queryToBeConverted = parser.parse(queryString);

        QueryConverterStrategyMockImpl impl = new QueryConverterStrategyMockImpl();
        impl.termClosure = {Term term->
            return new Term(term.field()+"added", term.text() + "added");
        }
        BooleanQuery convertedQuery = QueryConverter.convertQuery(queryToBeConverted, impl);
        def convertedClauses = convertedQuery.getClauses();
        assertEquals (originalClauses.size(), convertedClauses.size())
        for(int i=0; i < originalClauses.length; i++)
        {
            TermQuery origTermQuery = originalClauses[i].getQuery()
            TermQuery convertedTermQuery = convertedClauses[i].getQuery()
            assertEquals (origTermQuery.getTerm().text()+"added", convertedTermQuery.getTerm().text());
            assertEquals (origTermQuery.getTerm().field()+"added", convertedTermQuery.getTerm().field());
        }
    }
    public void testConvertQueryWithWildcardQuery()
    {
        String queryString = "term1:p*a* a query";
        QueryParser parser = new QueryParser("field", new WhiteSpaceLowerCaseAnalyzer());
        BooleanQuery q = parser.parse(queryString);
        def originalClauses = q.getClauses();

        BooleanQuery queryToBeConverted = parser.parse(queryString);

        QueryConverterStrategyMockImpl impl = new QueryConverterStrategyMockImpl();
        impl.termClosure = {Term term->
            return new Term(term.field()+"added", term.text() + "added");
        }
        BooleanQuery convertedQuery = QueryConverter.convertQuery(queryToBeConverted, impl);
        def convertedClauses = convertedQuery.getClauses();
        assertEquals (originalClauses.size(), convertedClauses.size())
        for(int i=0; i < originalClauses.length; i++)
        {
            def origTermQuery = originalClauses[i].getQuery()
            def convertedTermQuery = convertedClauses[i].getQuery()
            assertEquals (origTermQuery.getTerm().text()+"added", convertedTermQuery.getTerm().text());
            assertEquals (origTermQuery.getTerm().field()+"added", convertedTermQuery.getTerm().field());
        }
    }

    public void testConvertQueryWithPrefixQuery()
    {
        String queryString = "term1:p* is ";
        QueryParser parser = new QueryParser("field", new WhiteSpaceLowerCaseAnalyzer());
        BooleanQuery q = parser.parse(queryString);
        def originalClauses = q.getClauses();

        BooleanQuery queryToBeConverted = parser.parse(queryString);

        QueryConverterStrategyMockImpl impl = new QueryConverterStrategyMockImpl();
        impl.termClosure = {Term term->
            return new Term(term.field()+"added", term.text() + "added");
        }
        BooleanQuery convertedQuery = QueryConverter.convertQuery(queryToBeConverted, impl);
        def convertedClauses = convertedQuery.getClauses();
        assertEquals (originalClauses.size(), convertedClauses.size())
        def originalPrefixTerm = originalClauses[0].getQuery().getPrefix();
        def convertedPrefixTerm = convertedClauses[0].getQuery().getPrefix();
        assertEquals (originalPrefixTerm.text()+"added", convertedPrefixTerm.text());
        assertEquals (originalPrefixTerm.field()+"added", convertedPrefixTerm.field());
    }

    public void testConvertQueryWithRangeQuery()
    {
        String queryString = "term1:[1 TO *] is";
        QueryParser parser = new QueryParser("field", new WhiteSpaceLowerCaseAnalyzer());
        BooleanQuery q = parser.parse(queryString);
        def originalClauses = q.getClauses();

        BooleanQuery queryToBeConverted = parser.parse(queryString);

        QueryConverterStrategyMockImpl impl = new QueryConverterStrategyMockImpl();
        impl.termClosure = {Term term->
            return new Term(term.field()+"added", term.text() + "added");
        }
        BooleanQuery convertedQuery = QueryConverter.convertQuery(queryToBeConverted, impl);
        def convertedClauses = convertedQuery.getClauses();
        assertEquals (originalClauses.size(), convertedClauses.size())
        ConstantScoreRangeQuery originalRangeQuery = originalClauses[0].getQuery();
        ConstantScoreRangeQuery convertedRangeQuery = convertedClauses[0].getQuery();
        assertEquals (originalRangeQuery.getUpperVal()+"added", convertedRangeQuery.getUpperVal());
        assertEquals (originalRangeQuery.getField()+"added", convertedRangeQuery.getField());
        assertEquals (originalRangeQuery.getLowerVal()+"added", convertedRangeQuery.getLowerVal());
    }

    public void testConvertQueryWithPhraseQuery()
    {
        String queryString = """"new york" is""";

        QueryParser parser = new QueryParser("field", new WhiteSpaceLowerCaseAnalyzer());
        BooleanQuery q = parser.parse(queryString);
        def originalClauses = q.getClauses();

        BooleanQuery queryToBeConverted = parser.parse(queryString);

        QueryConverterStrategyMockImpl impl = new QueryConverterStrategyMockImpl();
        impl.termClosure = {Term term->
            return new Term(term.field()+"added", term.text() + "added");
        }
        BooleanQuery convertedQuery = QueryConverter.convertQuery(queryToBeConverted, impl);
        def convertedClauses = convertedQuery.getClauses();
        assertEquals (originalClauses.size(), convertedClauses.size())
        assertEquals(originalClauses[0].getQuery(), convertedClauses[0].getQuery());
    }

    public void testConvertQueryWithFuzzyQuery()
    {
        String queryString = "severity:xyz~ is";

        QueryParser parser = new QueryParser("field", new WhiteSpaceLowerCaseAnalyzer());
        BooleanQuery q = parser.parse(queryString);
        def originalClauses = q.getClauses();

        BooleanQuery queryToBeConverted = parser.parse(queryString);

        QueryConverterStrategyMockImpl impl = new QueryConverterStrategyMockImpl();
        impl.termClosure = {Term term->
            return new Term(term.field()+"added", term.text() + "added");
        }
        BooleanQuery convertedQuery = QueryConverter.convertQuery(queryToBeConverted, impl);
        def convertedClauses = convertedQuery.getClauses();
        assertEquals (originalClauses.size(), convertedClauses.size())
        FuzzyQuery originalFuzzy = originalClauses[0].getQuery()
        FuzzyQuery convertedFuzzy = convertedClauses[0].getQuery()
        assertEquals (originalFuzzy.getMinSimilarity(), convertedFuzzy.getMinSimilarity());
        assertEquals (originalFuzzy.getPrefixLength(), convertedFuzzy.getPrefixLength());
        assertEquals (originalFuzzy.getTerm().text()+"added", convertedFuzzy.getTerm().text());
        assertEquals (originalFuzzy.getTerm().field()+"added", convertedFuzzy.getTerm().field());
    }

    public void testConvertQueryWithProximityQuery()
    {
        String queryString = """"new york"~ is""";

        QueryParser parser = new QueryParser("field", new WhiteSpaceLowerCaseAnalyzer());
        BooleanQuery q = parser.parse(queryString);
        def originalClauses = q.getClauses();

        BooleanQuery queryToBeConverted = parser.parse(queryString);

        QueryConverterStrategyMockImpl impl = new QueryConverterStrategyMockImpl();
        impl.termClosure = {Term term->
            println term.text()
            return new Term(term.field()+"added", term.text() + "added");
        }
        BooleanQuery convertedQuery = QueryConverter.convertQuery(queryToBeConverted, impl);
        def convertedClauses = convertedQuery.getClauses();
        assertEquals (originalClauses.size(), convertedClauses.size())
        assertEquals(originalClauses[0].getQuery(), convertedClauses[0].getQuery());
    }
}

class QueryConverterStrategyMockImpl implements QueryConverterStrategy
{
    def termClosure;
    public Term getConvertedTerm(Term term) {
        return termClosure(term);  //To change body of implemented methods use File | Settings | File Templates.
    }

}
