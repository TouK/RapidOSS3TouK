package com.ifountain.compass

import com.ifountain.rcmdb.test.util.RapidCmdbTestCase

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Jan 6, 2009
* Time: 6:05:15 PM
* To change this template use File | Settings | File Templates.
*/
class RapidLuceneQueryParserTest extends RapidCmdbTestCase
{
    public void testLuceneQueryParser()
    {
        RapidLuceneQueryParser parser = new RapidLuceneQueryParser();
        assertTrue(parser.createQueryParser(null, null, false) instanceof RapidQueryParser);
        assertTrue(parser.createMultiQueryParser(null, null, false) instanceof RapidMultiQueryParser);
    }
}