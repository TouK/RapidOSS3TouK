package com.ifountain.compass.analyzer

import com.ifountain.rcmdb.test.util.RapidCmdbTestCase
import org.apache.lucene.analysis.Token

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Jul 24, 2008
 * Time: 11:52:51 AM
 * To change this template use File | Settings | File Templates.
 */
class WhiteSpaceLowerCaseTokenizerTest extends RapidCmdbTestCase{
    public void testTokenizer()
    {
        String input = "ThiS iS a query.";
        StringReader reader = new StringReader(input);
        WhiteSpaceLowerCaseTokenizer tokenizer = new WhiteSpaceLowerCaseTokenizer(reader);
        String[] parts = input.split(" ");
        for(int i=0; i < parts.length; i++)
        {
            Token token = tokenizer.next()
            assertEquals (parts[i].toLowerCase(), token.termText());
        }


        
    }
}