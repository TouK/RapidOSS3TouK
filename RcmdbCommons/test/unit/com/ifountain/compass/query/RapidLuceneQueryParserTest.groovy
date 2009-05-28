package com.ifountain.compass.query

import com.ifountain.compass.query.RapidLuceneQueryParser
import com.ifountain.compass.query.RapidMultiQueryParser
import com.ifountain.compass.query.RapidQueryParser
import com.ifountain.rcmdb.test.util.RapidCmdbTestCase
import com.ifountain.rcmdb.test.util.compass.TestCompassFactory
import org.compass.core.spi.InternalCompass


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
        InternalCompass compass = TestCompassFactory.getCompass ([],[],false)
        try{
            RapidLuceneQueryParser parser = new RapidLuceneQueryParser();
            parser.setSearchEngineFactory (compass.getSearchEngineFactory());
            assertTrue(parser.createQueryParser(null, null, false) instanceof RapidQueryParser);
            assertTrue(parser.createMultiQueryParser(null, null, false) instanceof RapidMultiQueryParser);
        }finally{
            compass.close();
        }
    }
}