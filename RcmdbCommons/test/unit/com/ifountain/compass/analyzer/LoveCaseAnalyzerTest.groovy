package com.ifountain.compass.analyzer

import com.ifountain.rcmdb.test.util.RapidCmdbTestCase
import org.apache.lucene.analysis.Tokenizer

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Nov 5, 2009
* Time: 11:01:07 AM
* To change this template use File | Settings | File Templates.
*/
class LoveCaseAnalyzerTest extends RapidCmdbTestCase{
    public void testTokenStream()
    {
        String input = "ThiS iS a query.";
        StringReader reader = new StringReader(input);
        LowerCaseAnalyzer analyzer = new LowerCaseAnalyzer();
        Tokenizer tokenizer = analyzer.tokenStream("field", reader);
        assertTrue(tokenizer instanceof LowerCaseTokenizer);
        assertEquals (input.toLowerCase(), tokenizer.next().termText());
    }

    public void testReusableTokenStream()
    {
        String input = "ThiS iS a query.";
        StringReader reader = new StringReader(input);
        LowerCaseAnalyzer analyzer = new LowerCaseAnalyzer();
        Tokenizer tokenizer = analyzer.reusableTokenStream("field", reader);
        assertTrue(tokenizer instanceof LowerCaseTokenizer);
        assertEquals (input.toLowerCase(), tokenizer.next().termText());
        Tokenizer tokenizerAfter = analyzer.reusableTokenStream("field", reader);
        assertSame (tokenizer, tokenizerAfter);
    }
}