package com.ifountain.compass.analyzer

import com.ifountain.rcmdb.test.util.RapidCmdbTestCase
import org.apache.lucene.analysis.Tokenizer

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Jul 24, 2008
 * Time: 12:15:12 PM
 * To change this template use File | Settings | File Templates.
 */
class WhiteSpaceLowerCaseAnalyzerTest extends RapidCmdbTestCase{
    public void testTokenStream()
    {
        String input = "ThiS iS a query.";
        StringReader reader = new StringReader(input);
        WhiteSpaceLowerCaseAnalyzer analyzer = new WhiteSpaceLowerCaseAnalyzer();
        Tokenizer tokenizer = analyzer.tokenStream("field", reader);
        assertTrue(tokenizer instanceof WhiteSpaceLowerCaseTokenizer);
        assertEquals ("this", tokenizer.next().termText());
    }

    public void testReusableTokenStream()
    {
        String input = "ThiS iS a query.";
        StringReader reader = new StringReader(input);
        WhiteSpaceLowerCaseAnalyzer analyzer = new WhiteSpaceLowerCaseAnalyzer();
        Tokenizer tokenizer = analyzer.reusableTokenStream("field", reader);
        assertTrue(tokenizer instanceof WhiteSpaceLowerCaseTokenizer);
        assertEquals ("this", tokenizer.next().termText());
        Tokenizer tokenizerAfter = analyzer.reusableTokenStream("field", reader);
        assertSame (tokenizer, tokenizerAfter);
    }
}