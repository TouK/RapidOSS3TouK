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